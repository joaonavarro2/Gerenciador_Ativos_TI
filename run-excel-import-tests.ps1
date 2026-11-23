$ErrorActionPreference = 'Stop'

$composeFile = 'docker-compose.excel-e2e.yml'
$projectName = 'stecar-excel-e2e-' + [guid]::NewGuid().ToString('N').Substring(0, 10)
$composeArgs = @('-p', $projectName, '-f', $composeFile)
$composeUpAttempted = $false
$testExitCode = 1
$cleanupExitCode = 0

function New-RandomHex([int]$byteCount) {
    $bytes = New-Object byte[] $byteCount
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
    return ([BitConverter]::ToString($bytes)).Replace('-', '').ToLowerInvariant()
}

$env:STECAR_EXCEL_E2E_PASSWORD = New-RandomHex 32
$env:STECAR_EXCEL_E2E_JWT_SECRET = New-RandomHex 64
$env:STECAR_EXCEL_E2E_ADMIN_EMAIL = 'admin-' + [guid]::NewGuid().ToString('N') + '@stecar.test'
$env:STECAR_EXCEL_E2E_ADMIN_PASSWORD = 'E2e-' + (New-RandomHex 18) + '!'

function Invoke-Compose([string[]]$CommandArgs) {
    $previousPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        $output = & docker compose @composeArgs @CommandArgs 2>&1
        $exitCode = $LASTEXITCODE
        $output | ForEach-Object { Write-Host $_ }
        return $exitCode
    }
    finally {
        $ErrorActionPreference = $previousPreference
    }
}

Push-Location $PSScriptRoot
try {
    $composeText = Get-Content $composeFile -Raw
    if ($composeText -match '(?m)^\s+ports:' -or
        $composeText -notmatch 'internal:\s*true' -or
        $composeText -notmatch '/var/lib/postgresql:rw,noexec,nosuid,size=512m') {
        throw 'E2E isolation preflight failed: expected internal network, no published ports, and PostgreSQL tmpfs.'
    }

    if ((Invoke-Compose @('config', '--quiet')) -ne 0) {
        throw 'E2E Compose configuration is invalid.'
    }
    $composeUpAttempted = $true
    if ((Invoke-Compose @('up', '--build', '-d', 'database', 'backend', 'frontend')) -ne 0) {
        throw 'Could not start the isolated E2E stack.'
    }

    $databaseId = (& docker compose @composeArgs ps -q database | Out-String).Trim()
    if ([string]::IsNullOrWhiteSpace($databaseId)) { throw 'Disposable E2E database was not created.' }
    $database = (docker inspect $databaseId | ConvertFrom-Json)[0]
    $envMap = @{}
    foreach ($entry in $database.Config.Env) {
        $parts = $entry -split '=', 2
        $envMap[$parts[0]] = if ($parts.Length -gt 1) { $parts[1] } else { '' }
    }
    $published = @($database.HostConfig.PortBindings.PSObject.Properties |
        Where-Object { $null -ne $_.Value -and @($_.Value | Where-Object { $_.HostPort }).Count -gt 0 })
    $nonTmpfsMounts = @($database.Mounts | Where-Object { $_.Type -ne 'tmpfs' })
    $tmpfs = $database.HostConfig.Tmpfs.PSObject.Properties | Where-Object { $_.Name -eq '/var/lib/postgresql' }
    $networkNames = @($database.NetworkSettings.Networks.PSObject.Properties | ForEach-Object { $_.Name })
    if ($envMap.POSTGRES_DB -ne 'stecar_excel_e2e' -or $envMap.POSTGRES_USER -ne 'stecar_excel_e2e' -or
        $published.Count -gt 0 -or $nonTmpfsMounts.Count -gt 0 -or $null -eq $tmpfs -or $networkNames.Count -ne 1) {
        throw 'Runtime isolation proof failed for E2E database.'
    }
    $network = (docker network inspect $networkNames[0] | ConvertFrom-Json)[0]
    if (-not $network.Internal) { throw 'E2E network is not internal.' }
    Write-Host 'Runtime isolation passed: unique Compose project, dedicated database, internal network, no published ports, tmpfs-only storage.'

    $testExitCode = Invoke-Compose @('run', '--build', '--rm', 'e2e')
}
finally {
    if ($composeUpAttempted) {
        $cleanupExitCode = Invoke-Compose @('down', '--remove-orphans')
    }
    Remove-Item Env:\STECAR_EXCEL_E2E_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:\STECAR_EXCEL_E2E_JWT_SECRET -ErrorAction SilentlyContinue
    Remove-Item Env:\STECAR_EXCEL_E2E_ADMIN_EMAIL -ErrorAction SilentlyContinue
    Remove-Item Env:\STECAR_EXCEL_E2E_ADMIN_PASSWORD -ErrorAction SilentlyContinue
    Pop-Location
}

if ($cleanupExitCode -ne 0) { throw 'Could not remove temporary E2E containers/network; inspect the unique E2E project.' }
if ($testExitCode -ne 0) { throw "Excel import E2E suite failed with exit code $testExitCode." }
Write-Host 'Excel import E2E tests passed; only the unique disposable project was removed.'
