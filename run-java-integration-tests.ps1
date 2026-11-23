$ErrorActionPreference = 'Stop'

$projectName = 'stecar-integration'
$composeFile = 'docker-compose.integration.yml'
$composeArgs = @('-p', $projectName, '-f', $composeFile)
$previousPassword = $env:STECAR_IT_POSTGRES_PASSWORD
$composeUpAttempted = $false
$testExitCode = 1
$cleanupExitCode = 0

$randomBytes = New-Object byte[] 32
$random = [Security.Cryptography.RandomNumberGenerator]::Create()
try {
    $random.GetBytes($randomBytes)
}
finally {
    $random.Dispose()
}
$env:STECAR_IT_POSTGRES_PASSWORD = ([BitConverter]::ToString($randomBytes)).Replace('-', '').ToLowerInvariant()

function Invoke-ComposeCommand {
    param([string[]]$CommandArgs)

    $previousPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        $output = & docker compose @composeArgs @CommandArgs 2>&1
        $exitCode = $LASTEXITCODE
        $output |
            Where-Object {
                $_ -match '(?i)(Creating|Created|Starting|Started|Healthy|Stopping|Stopped|Removing|Removed|Built|Tests run:|BUILD SUCCESS|BUILD FAILURE|COMPILATION ERROR|Preflight|Runtime proof|failed|error|warning)'
            } |
            ForEach-Object { Write-Host $_ }
        return $exitCode
    }
    finally {
        $ErrorActionPreference = $previousPreference
    }
}

Push-Location $PSScriptRoot
try {
    $composeText = Get-Content $composeFile -Raw
    $testBasePath = 'backend/stecar-backend/src/test/java/br/com/stecar/stecar_backend/PostgresIntegrationTest.java'
    $testBase = Get-Content $testBasePath -Raw
    $testSources = Get-ChildItem 'backend/stecar-backend/src/test/java' -Filter '*.java' -Recurse |
        ForEach-Object { Get-Content $_.FullName -Raw }
    $allTestSource = $testSources -join "`n"

    $unsafeTestSource = @(
        $allTestSource -match 'localhost:5432|127\.0\.0\.1|host\.docker\.internal'
        [regex]::Matches($allTestSource, 'jdbc:postgresql://').Count -ne 1
        $testBase -notmatch 'jdbc:postgresql://" \+ TEST_DATABASE_HOST \+ ":" \+ TEST_DATABASE_PORT \+ "/" \+ TEST_DATABASE_NAME'
        $testBase -notmatch 'Path\.of\("/\.dockerenv"\)'
        $testBase -notmatch 'STECAR_INTEGRATION_RUNNER'
        $testBase -notmatch 'registry\.add\("spring\.datasource\.url",\s*\(\)\s*->\s*TEST_DATABASE_URL\)'
        $testBase -notmatch 'registry\.add\("spring\.flyway\.url",\s*\(\)\s*->\s*TEST_DATABASE_URL\)'
        $testBase -notmatch 'registry\.add\("spring\.flyway\.clean-disabled",\s*\(\)\s*->\s*true\)'
    ) -contains $true
    if ($unsafeTestSource) {
        throw 'Database isolation preflight failed: integration test guard or JDBC destination changed.'
    }
    $unsafeCompose = @(
        $composeText -notmatch 'STECAR_TEST_DB_HOST:\s*database'
        $composeText -notmatch 'STECAR_TEST_DB_PORT:\s*"5432"'
        $composeText -notmatch 'STECAR_TEST_DB_NAME:\s*stecar_integration'
        $composeText -notmatch 'internal:\s*true'
        $composeText -notmatch '/var/lib/postgresql:rw,noexec,nosuid,size=512m'
        $composeText -match '(?m)^\s+(ports|volumes):'
    ) -contains $true
    if ($unsafeCompose) {
        throw 'Database isolation preflight failed: Compose must use the internal disposable database without host ports or volumes.'
    }

    $composeExitCode = Invoke-ComposeCommand -CommandArgs @('config', '--quiet')
    if ($composeExitCode -ne 0) {
        throw 'Integration Compose configuration validation failed.'
    }

    $composeUpAttempted = $true
    $composeExitCode = Invoke-ComposeCommand -CommandArgs @('up', '-d', 'database')
    if ($composeExitCode -ne 0) {
        throw 'Could not start the disposable integration database.'
    }

    $databaseId = (docker compose @composeArgs ps -q database | Out-String).Trim()
    if ([string]::IsNullOrWhiteSpace($databaseId)) {
        throw 'The disposable integration database container was not created.'
    }

    $databaseHealthy = $false
    for ($attempt = 0; $attempt -lt 24; $attempt++) {
        $healthStatus = docker inspect --format '{{.State.Health.Status}}' $databaseId
        if ($healthStatus -eq 'healthy') {
            $databaseHealthy = $true
            break
        }
        Start-Sleep -Seconds 5
    }
    if (-not $databaseHealthy) {
        throw 'The disposable integration database did not become healthy.'
    }

    $database = (docker inspect $databaseId | ConvertFrom-Json)[0]
    $environment = @{}
    foreach ($entry in $database.Config.Env) {
        $parts = $entry -split '=', 2
        $environment[$parts[0]] = if ($parts.Length -gt 1) { $parts[1] } else { '' }
    }
    $publishedPorts = @(
        $database.HostConfig.PortBindings.PSObject.Properties |
            Where-Object { $null -ne $_.Value -and @($_.Value | Where-Object { $_.HostPort }).Count -gt 0 }
    )
    $persistentMounts = @($database.Mounts | Where-Object { $_.Type -ne 'tmpfs' })
    $networkNames = @($database.NetworkSettings.Networks.PSObject.Properties | ForEach-Object { $_.Name })
    $databaseTmpfs = $database.HostConfig.Tmpfs.PSObject.Properties |
        Where-Object { $_.Name -eq '/var/lib/postgresql' } |
        Select-Object -First 1
    $unsafeDatabaseRuntime = @(
        $environment.POSTGRES_DB -ne 'stecar_integration'
        $environment.POSTGRES_USER -ne 'stecar_it'
        $publishedPorts.Count -gt 0
        $persistentMounts.Count -gt 0
        $null -eq $databaseTmpfs
        $null -ne $databaseTmpfs -and $databaseTmpfs.Value -notmatch 'size=512m'
        $networkNames.Count -ne 1
    ) -contains $true
    if ($unsafeDatabaseRuntime) {
        throw 'Runtime isolation proof failed: database identity, ports, mounts, or network did not match the disposable configuration.'
    }
    $network = (docker network inspect $networkNames[0] | ConvertFrom-Json)[0]
    if (-not $network.Internal) {
        throw 'Runtime isolation proof failed: integration network is not internal.'
    }

    Write-Host 'Preflight passed: JDBC target is jdbc:postgresql://database:5432/stecar_integration.'
    Write-Host 'Tests require the Docker runner marker; Flyway and the datasource share this target.'
    Write-Host 'Runtime proof passed: internal network, no published database port, tmpfs-only database storage, and no manual test volume.'

    $testExitCode = Invoke-ComposeCommand -CommandArgs @('run', '--build', '--rm', 'integration-tests')
}
finally {
    if ($composeUpAttempted) {
        $cleanupExitCode = Invoke-ComposeCommand -CommandArgs @('down', '--remove-orphans')
    }

    if ($null -eq $previousPassword) {
        Remove-Item Env:\STECAR_IT_POSTGRES_PASSWORD -ErrorAction SilentlyContinue
    }
    else {
        $env:STECAR_IT_POSTGRES_PASSWORD = $previousPassword
    }

    Pop-Location
}

if ($cleanupExitCode -ne 0) {
    throw 'Integration test containers could not be removed; inspect the stecar-integration project.'
}
if ($testExitCode -ne 0) {
    throw "Java integration tests failed with exit code $testExitCode."
}

Write-Host 'Java integration tests passed; the disposable database container was removed without touching the manual test volume.'
