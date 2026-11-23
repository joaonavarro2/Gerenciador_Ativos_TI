# Ambiente Docker isolado para testes

O compose deste ambiente cria somente serviços de teste: PostgreSQL 18 em uma rede interna, backend Spring Boot e frontend Quasar servido por Nginx. Ele não conecta ao PostgreSQL instalado no Windows nem importa dados do ambiente de desenvolvimento.

## Pré-requisitos

- Docker Desktop instalado e iniciado no Windows, com suporte ao Docker Compose v2.
- Na primeira inicialização, acesso à internet para baixar imagens e dependências do Maven/npm.

Não é necessário instalar ou iniciar o PostgreSQL do Windows.

## Preparar variáveis locais

Na raiz do repositório, copie o exemplo para um arquivo ignorado pelo Git:

```powershell
if (-not (Test-Path .env.docker.test)) { Copy-Item .env.docker.test.example .env.docker.test }
code .env.docker.test
```

Defina `POSTGRES_TEST_PASSWORD` com uma senha exclusiva para esta base, `STECAR_TEST_JWT_SECRET` com um segredo aleatório de pelo menos 64 bytes e `STECAR_TEST_ADMIN_PASSWORD` com uma senha exclusiva para o administrador geral inicial do ambiente. O endereço e o nome desse administrador podem ser ajustados pelas variáveis de exemplo.

Depois que o banco for inicializado, mantenha `POSTGRES_TEST_PASSWORD` consistente com a senha do usuário PostgreSQL persistida no volume.

Esses valores são usados somente pelo compose de teste. Não copie credenciais do `.env` de desenvolvimento.

## Construir e iniciar

Execute na raiz do repositório:

```powershell
docker compose --env-file .env.docker.test -f docker-compose.test.yml up --build -d
```

O banco executa as migrations Flyway existentes em sua própria base `stecar_test`. Não são criadas nem alteradas migrations por este ambiente.

## Endereços e estado dos serviços

- Frontend: http://127.0.0.1:18080
- Backend: http://127.0.0.1:18081
- Saúde do backend: http://127.0.0.1:18081/actuator/health
- PostgreSQL: porta 5432 somente na rede interna do Docker; não há porta publicada no Windows.

O frontend encaminha `/api/` ao backend pela rede bridge privada definida pelo Compose. O backend conecta exclusivamente ao serviço `database`; a porta `5432` não é publicada no Windows. O banco usa o volume nomeado `stecar-test-postgres-data`, separado do PostgreSQL do Windows.

Confira a inicialização e os healthchecks:

```powershell
docker compose --env-file .env.docker.test -f docker-compose.test.yml ps
docker compose --env-file .env.docker.test -f docker-compose.test.yml logs --follow
```

Pressione `Ctrl+C` para sair da visualização de logs.

## Executar testes de frontend

Os testes de frontend podem ser executados no Windows:

```powershell
Set-Location frontend\quasar-project
npm ci
npm test
npm run build
```

## Executar testes Java de integração

**Não execute `mvn test` no Windows para a suíte que acessa PostgreSQL.** Os testes de integração só aceitam o host literal `database`, a porta `5432`, o banco `stecar_integration` e o usuário `stecar_it`; também exigem o marcador do runner e `/.dockerenv`. A ausência ou divergência de qualquer configuração interrompe os testes antes da conexão. A configuração de datasource e a do Flyway usam a mesma URL.

Execute o runner a partir da raiz do repositório:

```powershell
.\run-java-integration-tests.ps1
```

O script valida previamente a URL, o marcador Docker, a rede e a ausência de portas publicadas e volumes no Compose. Em seguida, cria uma senha temporária aleatória, constrói a imagem de teste e inicia somente o PostgreSQL descartável e o runner Java. O Compose de integração usa outra rede interna e não declara volumes nem portas. Ao final, o script executa `docker compose down --remove-orphans` sem `-v`, remove o container de banco descartável e restaura a variável de ambiente anterior.

O serviço `database` armazena os dados em `tmpfs` e não declara volumes ou portas. Assim, as migrations Flyway e os testes com operações de escrita ficam restritos a `stecar_integration` e os dados são descartados quando o script encerra o Compose. Nenhuma configuração JDBC de integração aponta para `localhost`, `host.docker.internal` ou para o banco manual do ambiente Docker; o volume persistente `stecar-test-postgres-data` não é montado nem removido.

O Dockerfile do backend mantém o estágio de produção inalterado; o estágio `integration-tests` executa `./mvnw -B test` dentro do container na rede Docker. A API manual continua disponível em `http://127.0.0.1:18081` e a aplicação web em `http://127.0.0.1:18080`.

O pacote `xlsx@0.20.3` é mantido localmente em `frontend/quasar-project/vendor/xlsx-0.20.3.tgz`, então a construção não baixa essa dependência do CDN. O `package-lock.json` registra o arquivo local e conserva o SHA-512 original; `npm ci` valida a integridade antes de instalar.

### Atualizar e verificar o artefato SheetJS

Baixe uma atualização apenas de uma origem oficial/verificada para um arquivo temporário. Para a versão atual:

```powershell
$download = Join-Path $env:TEMP 'xlsx-0.20.3.tgz'
curl.exe --fail --location --output $download 'https://cdn.sheetjs.com/xlsx-0.20.3/xlsx-0.20.3.tgz'
if ($LASTEXITCODE -ne 0) { throw 'Download SheetJS falhou.' }
$expected = 'sha512-oLDq3jw7AcLqKWH2AhCpVTZl8mf6X2YReP+Neh0SJUzV/BdZYjth94tG5toiMB1PPrYtxOCfaoUCkvtuH+3AJA=='
$hex = (Get-FileHash $download -Algorithm SHA512).Hash
$bytes = for ($i = 0; $i -lt $hex.Length; $i += 2) { [Convert]::ToByte($hex.Substring($i, 2), 16) }
$actual = 'sha512-' + [Convert]::ToBase64String([byte[]]$bytes)
if ($actual -cne $expected) { throw 'SHA-512 mismatch; do not use this artifact.' }
Copy-Item $download 'frontend\quasar-project\vendor\xlsx-0.20.3.tgz'
```

O valor esperado é o SHA-512 do lockfile atual e só autentica esta versão/arquivo. Para atualizar a versão, primeiro obtenha de uma fonte oficial uma referência de integridade independente; só então substitua o tarball e atualize a dependência e o lockfile, mantendo `version`, `resolved` local e `integrity` consistentes. Nunca aceite como referência o hash calculado do próprio arquivo baixado.

### CA de proxy HTTPS para outras dependências

Se uma dependência diferente do SheetJS falhar com `SELF_SIGNED_CERT_IN_CHAIN`, obtenha a CA raiz/intermediária **do administrador da rede** ou exporte a CA já confiada no Windows após confirmar com esse administrador que é a CA correta. Use PEM/Base-64 X.509, sem chave privada, salve fora do repositório e não a obtenha do próprio endpoint que falhou.

O Dockerfile aceita essa CA como secret temporário do BuildKit em `/run/secrets/corporate-ca.pem`; o certificado não é copiado para a imagem nem para uma camada. Com o caminho fornecido pela equipe de TI:

```powershell
$env:STECAR_DOCKER_CA_FILE = (Resolve-Path 'C:\caminho\seguro\proxy-ca.pem').Path
docker compose --env-file .env.docker.test -f docker-compose.test.yml build backend
docker build --secret "id=corporate_ca,src=$env:STECAR_DOCKER_CA_FILE" --tag stecar-test-frontend .\frontend\quasar-project
docker compose --env-file .env.docker.test -f docker-compose.test.yml up -d --no-build
Remove-Item Env:\STECAR_DOCKER_CA_FILE
```

Em uma rede sem interceptação TLS, use o comando normal de `up --build`; nenhum secret é necessário. O `.npmrc` global do Windows não é copiado para a imagem. Em particular, não configure `strict-ssl=false` nem `NODE_TLS_REJECT_UNAUTHORIZED=0`.

## Encerrar

Na raiz do repositório:

```powershell
docker compose --env-file .env.docker.test -f docker-compose.test.yml down
```

Esse comando para e remove os containers e a rede, mas preserva o volume persistente do PostgreSQL de teste. Não use `--volumes` se quiser conservar os dados entre inicializações. O comando de encerramento não afeta o PostgreSQL do Windows.
