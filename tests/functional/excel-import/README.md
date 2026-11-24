# Testes funcionais da importação Excel

## Como executar

No PowerShell, na raiz do repositório:

```powershell
cd frontend/quasar-project
npm.cmd test
npm.cmd run build
cd ../..
cd tests/functional/excel-import/e2e
npm.cmd ci --ignore-scripts --no-audit --no-fund
cd ../../../../
./run-java-integration-tests.ps1
./run-excel-import-tests.ps1
```

Os comandos instalam as dependências do runner Playwright no workspace local (ignoradas pelo Git) e executam as suítes. O runner Java usa exclusivamente `docker-compose.integration.yml`. O runner E2E cria um projeto Compose com nome aleatório, PostgreSQL dedicado em `tmpfs`, rede interna sem portas publicadas e sem volumes persistentes. Ao final, remove somente os containers e a rede daquele projeto, sem `down -v`.

## Cobertura adicionada

- Backend/PostgreSQL: 9 testes de integração para validação sem gravação, confirmação, persistência e consulta pela API; lote com linhas válidas e inválidas; lote de 250 registros; campos opcionais; categorias, status e datas; duplicidade/reimportação; outros itens; referências e campos obrigatórios inválidos.
- Frontend: 6 testes do diálogo de importação para leitura/validação, confirmação via API, extensão inválida, cabeçalhos ausentes, ausência de registros, planilha corrompida, carregamento, fechamento e erro da API.
- E2E Playwright: 6 cenarios aprovados. Importa XLSX pela interface e confirma API e banco; rejeita PDF e planilha corrompida; verifica acesso sem autenticacao; percorre as 11 rotas funcionais; e cadastra escritorio, departamento e pessoa, verificando os vinculos no PostgreSQL.

## Execuções verificadas

- `npm.cmd test` em `frontend/quasar-project`: 24 testes aprovados em 6 arquivos (inclui os 6 testes do diálogo).
- `./run-java-integration-tests.ps1`: 57 testes aprovados, 0 falhas e 0 erros, no banco de integração descartável `stecar_integration`.
- `./run-excel-import-tests.ps1`: 6 testes aprovados. O stack usou banco PostgreSQL em tmpfs, rede interna sem portas publicadas e foi removido ao final.
- `npm.cmd run build` em `frontend/quasar-project`: build de produção aprovado.
- `npm.cmd audit --json`: 5 vulnerabilidades moderadas, 0 altas e 0 críticas; os pacotes sinalizados estão na árvore de desenvolvimento/teste (`@vue/test-utils`, `editorconfig`, `glob`, `js-beautify`, `minimatch`). `npm.cmd audit --omit=dev --json` reportou 0 vulnerabilidades nas dependências de produção.
- `npm.cmd run lint:check`: terminou com exit code 0; ESLint passou. O comando Prettier configurado nesse script não usa `--check`, portanto esse trecho não comprova conformidade de formatação.

As execucoes aprovadas somam 87 testes automatizados (57 Java, 24 Vitest e 6 Playwright). O E2E relacional confirma a persistencia do escritorio, departamento e pessoa, inclusive o mesmo escritorio associado ao departamento e a pessoa.

## Lacunas a cobrir

Ainda faltam cenários específicos de importação para arquivo vazio, cabeçalhos ausentes/duplicados, limite máximo, cinco linhas inválidas, falha de conexão/banco e interrupção da requisição. A cobertura atual registra mistura de registros válidos e inválidos, mas não define comportamento transacional além do observado na implementação. Números de série repetidos são permitidos pela regra atual; códigos e patrimônios duplicados são rejeitados.

A cobertura geral do STECAR ainda precisa de E2E para operacoes de cadastro, edicao, exclusao, filtros, anexos e exportacoes nos demais modulos. No fluxo de departamentos foi corrigido o binding do escritorio para `escritorioId`; o E2E usa scroll do menu virtual do QSelect para localizar opcoes fora da area inicialmente renderizada. Os seis cenarios E2E passaram. O smoke de navegacao confirma rotas e respostas, mas nao substitui os fluxos UI restantes. Os cinco alertas do npm audit estao restritos as dependencias de desenvolvimento/teste.
