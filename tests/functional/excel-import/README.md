# Testes funcionais da importação Excel

## Como executar

No PowerShell, na raiz do repositório:

```powershell
cd frontend/quasar-project
npm.cmd test
cd ../..
./run-java-integration-tests.ps1
./run-excel-import-tests.ps1
```

O primeiro comando executa a suíte Vitest do frontend. O runner Java usa exclusivamente `docker-compose.integration.yml`. O runner E2E cria um projeto Compose com nome aleatório, PostgreSQL dedicado em `tmpfs`, rede interna sem portas publicadas e sem volumes persistentes. Ao final, remove somente os containers e a rede daquele projeto, sem `down -v`.

## Cobertura adicionada

- Backend/PostgreSQL: 9 testes de integração para validação sem gravação, confirmação, persistência e consulta pela API; lote com linhas válidas e inválidas; lote de 250 registros; campos opcionais; categorias, status e datas; duplicidade/reimportação; outros itens; referências e campos obrigatórios inválidos.
- Frontend: 6 testes do diálogo de importação para leitura/validação, confirmação via API, extensão inválida, cabeçalhos ausentes, ausência de registros, planilha corrompida, carregamento, fechamento e erro da API.
- E2E Playwright: 2 cenários implementados. Um percorre login, seleção e confirmação de XLSX, consulta pela API, listagem e verificação SQL no PostgreSQL. O outro rejeita PDF, verifica a mensagem e comprova que não houve POST nem gravação.

## Execuções verificadas

- `npm.cmd test` em `frontend/quasar-project`: 24 testes aprovados em 6 arquivos (inclui os 6 testes do diálogo).
- `./run-java-integration-tests.ps1`: 57 testes aprovados, 0 falhas e 0 erros, no banco de integração descartável `stecar_integration`.
- `./run-excel-import-tests.ps1`: isolamento do stack passou, mas a execução E2E ficou bloqueada no build do runner. `npm ci` no container terminou com erro interno do npm (`Exit handler never called!`). A tentativa equivalente no host retornou `ECONNRESET` ao buscar `xtend` em `registry.npmjs.org`. Portanto os 2 cenários E2E estão escritos, mas ainda não foram executados com sucesso.

## Lacunas a cobrir

Não há, ainda, cenários automatizados separados para XLSX inválido/corrompido no navegador, arquivo vazio, falta/duplicação de cabeçalho, limite máximo de arquivo, 5 linhas inválidas, falha de conexão/banco ou interrupção da requisição. A cobertura atual registra mistura de registros válidos e inválidos, mas não define comportamento transacional além do observado na implementação. Números de série repetidos são permitidos pela regra atual; códigos e patrimônios duplicados são rejeitados. Falhas de infraestrutura para os testes E2E precisam ser resolvidas antes de declarar a validação E2E concluída.
