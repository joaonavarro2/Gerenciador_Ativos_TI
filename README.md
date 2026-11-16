# Gerenciador de Ativos TI (STECAR)

Sistema de inventário e movimentação de bens de TI: bens, movimentações, consertos, pessoas, escritórios, departamentos e outros itens.

| Camada | Tecnologia |
| --- | --- |
| Backend | Java 25, Spring Boot 4, Spring Security (JWT HS512), JPA, Flyway, PostgreSQL |
| Frontend | Vue 3, Quasar 2, Vite, Axios, Pinia |
| Banco de dados | PostgreSQL instalado no Windows; sem containers |

## Pré-requisitos para desenvolvimento no Windows

- JDK 25 ou superior compatível com Java 25.
- Node.js 22.12+ em 22.x, 24.x ou 26+ e npm (Node 24 também é compatível).
- PostgreSQL instalado e ativo no Windows, porta `5432`.
- A base local existente `stecar`, schema `public`.
- VS Code com as extensões Extension Pack for Java e Vue/Quasar recomendadas pelo projeto.

O backend e o frontend são iniciados diretamente no Windows. O PostgreSQL oficial de desenvolvimento é `localhost:5432/stecar`, schema `public`; não crie nem use outra base para executar a aplicação.

## Configuração segura do `.env`

O arquivo `.env` na raiz é local e não deve ser compartilhado nem commitado. Se ainda não existir, crie-o no VS Code e configure `DB_HOST=localhost`, `DB_PORT=5432`, `DB_NAME=stecar`, `DB_USER=postgres` e a senha já válida em `DB_PASSWORD`. Defina também `STECAR_JWT_SECRET` com uma chave própria de pelo menos 64 caracteres. `STECAR_ADMIN_PASSWORD` só é usada se a inicialização precisar criar o administrador geral.

Não altere a senha do PostgreSQL nem use `POSTGRES_PASSWORD` no backend: a variável de conexão é `DB_PASSWORD`. Não cole os valores em comandos, relatórios ou conversas. Confirme que o arquivo é ignorado pelo Git:

```powershell
git check-ignore .env
```

## Iniciar o PostgreSQL

Confira o serviço local:

```powershell
Get-Service postgresql-x64-18
```

Se estiver parado, inicie-o em um PowerShell elevado:

```powershell
Start-Service postgresql-x64-18
```

Valide a porta:

```powershell
Test-NetConnection localhost -Port 5432
```

## Iniciar o backend pelo VS Code

Abra a raiz do repositório no VS Code (`File > Open Folder`). O perfil **STECAR Backend (Windows)** em Run and Debug usa `backend/stecar-backend` como diretório de trabalho, carrega o `.env` da raiz e ativa o perfil Spring `local`. O backend conecta a `localhost:5432/stecar` e escuta em `http://localhost:8081`.

Também é possível iniciar pelo PowerShell, na raiz do repositório:

```powershell
Set-Location backend\stecar-backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

Confirme que a porta do backend está ouvindo:

```powershell
Test-NetConnection localhost -Port 8081
```

## Iniciar o frontend pelo terminal

Em outro PowerShell, na raiz do repositório:

```powershell
Set-Location frontend\quasar-project
npm ci
npm run dev
```

A aplicação fica em `http://localhost:9000`. O proxy `/api` do Quasar encaminha as chamadas para o backend local em `http://localhost:8081`; não é necessária URL alternativa nem alteração do arquivo `.env`.

Verifique se o frontend responde:

```powershell
Invoke-WebRequest http://localhost:9000 -UseBasicParsing
```

## Perfis de acesso

Existem somente duas funções:

| Função | Permissões |
| --- | --- |
| `ADMINISTRADOR` | Operações operacionais e administrativas do sistema; pode consultar usuários e alterar a própria senha. Não pode criar, editar, excluir ou alterar funções de usuários. |
| `ADMINISTRADOR GERAL` | Todas as operações do administrador e, adicionalmente, gerenciamento de usuários e alteração de suas funções, respeitadas as proteções contra autoalteração e remoção do último administrador geral. |

Não há cadastro público nem perfil ativo `USUARIO`. O cadastro de usuários é feito por um administrador geral. Movimentações são registros históricos: sua exclusão retorna conflito e a correção deve ser registrada como uma nova movimentação.

As permissões são verificadas no backend. JWTs são assinados com HS512 e a chave configurada precisa ter pelo menos 64 caracteres.

## Testes do backend com PostgreSQL local

Os testes de integração usam exclusivamente a base local separada `stecar_test`, nunca a base oficial `stecar`. Na primeira execução, crie a base de teste, se ainda não existir, a partir da raiz do repositório:

```powershell
$psql = "$env:ProgramFiles\PostgreSQL\18\bin\psql.exe"
$line = Get-Content .env | Where-Object { $_ -match '^DB_PASSWORD=' } | Select-Object -First 1
if (-not $line) { throw 'DB_PASSWORD não encontrado no .env' }
$env:PGPASSWORD = $line.Substring(12)
$exists = & $psql -h localhost -p 5432 -U postgres -d postgres -At -c "SELECT 1 FROM pg_database WHERE datname = 'stecar_test'"
if (-not $exists) {
    & $psql -h localhost -p 5432 -U postgres -d postgres -c "CREATE DATABASE stecar_test OWNER postgres"
    if ($LASTEXITCODE -ne 0) { throw 'Não foi possível criar o banco isolado stecar_test' }
}
$env:STECAR_TEST_DB_USER = 'postgres'
$env:STECAR_TEST_DB_PASSWORD = $env:PGPASSWORD
Remove-Item Env:\PGPASSWORD
Set-Location backend\stecar-backend
.\mvnw.cmd verify
$testExitCode = $LASTEXITCODE
Remove-Item Env:\STECAR_TEST_DB_USER
Remove-Item Env:\STECAR_TEST_DB_PASSWORD
if ($testExitCode -ne 0) { throw 'A suíte backend falhou' }
```

O comando lê `DB_PASSWORD` apenas para autenticar no banco de teste; a URL JDBC dos testes é fixada em `localhost:5432/stecar_test`. Não altere nem apague a base `stecar` para preparar testes. No CI, os testes usam PostgreSQL nativo do runner.

Testes do frontend:

```powershell
Set-Location frontend\quasar-project
npm run lint:check
npm test
npm run build
```
