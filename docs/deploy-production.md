# Implantacao do STECAR em producao

Este procedimento prepara frontend, API e PostgreSQL em uma unica maquina Docker. Ele nao publica a API nem o PostgreSQL diretamente. O frontend fica em `127.0.0.1:8080` por padrao e deve ser atendido por um proxy HTTPS no host.

## Antes de iniciar

- Use uma maquina Linux suportada, Docker Engine e Docker Compose v2 atualizados.
- Configure um dominio e um proxy HTTPS (por exemplo, Nginx/Caddy gerenciado no host) com certificado valido; encaminhe o dominio para `http://127.0.0.1:8080` e preserve os cabecalhos `Host`, `X-Forwarded-For` e `X-Forwarded-Proto`.
- Libere externamente somente as portas 80/443 do proxy. A porta 8080, a API 8080 interna e a porta 5432 do PostgreSQL nao devem ser publicadas externamente.
- Prepare um disco persistente e monitorado para o volume `stecar-production-postgres-data` e backups externos ao servidor.
- Nao copie o `.env` de desenvolvimento, `.env.docker.test`, nem o volume do ambiente de testes para producao.

## Segredos

Crie os arquivos abaixo no servidor. Eles sao montados como Docker secrets locais e estao excluidos do Git em `deploy/secrets/`:

- `deploy/secrets/db_password`: senha exclusiva do PostgreSQL de producao.
- `deploy/secrets/jwt_secret`: chave aleatoria com pelo menos 64 bytes. Gere-a com `openssl rand -base64 64` e grave o valor sem a quebra de linha final.
- `deploy/secrets/admin_password`: senha temporaria forte para criar o primeiro Administrador Geral.

Restrinja a leitura dos arquivos ao usuario de deploy (`chmod 600 deploy/secrets/*`). Nao envie os valores para logs, tickets ou Git. O nome e email do Administrador Geral ficam no `.env.production`, que tambem deve ser protegido (`chmod 600 .env.production`). Copie `.env.production.example` para `.env.production`, defina a URL publica e os dados da conta inicial. Nunca use os valores de exemplo literalmente.

O Compose entrega `db_password`, JWT e senha inicial ao Spring via arquivos em `/run/secrets` usando o import de configuracao em arvore do Spring Boot. A imagem oficial do PostgreSQL recebe a senha usando `POSTGRES_PASSWORD_FILE`.

Na inicializacao de um volume novo, o script `deploy/postgres-init/10-restrict-app-role.sql` remove os privilegios de superusuario e de criacao de roles/bancos do usuario da aplicacao. Ele continua dono do banco para que o Flyway possa aplicar migrations. Scripts de `/docker-entrypoint-initdb.d` rodam somente quando o volume PostgreSQL e criado pela primeira vez; em um banco preexistente, verifique e ajuste os privilegios antes da publicacao, sem presumir que este script foi reaplicado.

## Construir e iniciar

Na raiz do repositorio, depois de provisionar os arquivos acima:

```sh
docker compose --env-file .env.production -f docker-compose.production.yml config --quiet
docker compose --env-file .env.production -f docker-compose.production.yml build
docker compose --env-file .env.production -f docker-compose.production.yml up -d
docker compose --env-file .env.production -f docker-compose.production.yml ps
docker compose --env-file .env.production -f docker-compose.production.yml logs --tail=200 backend
```

O Flyway executa as migrations pendentes no primeiro inicio; o Hibernate valida o esquema. O `AdminInitializer` cria a conta inicial somente se o email configurado ainda nao existir e a senha inicial estiver definida. Confirme o login por HTTPS e troque a senha temporaria. Depois que o administrador inicial estiver criado e o login validado, remova o secret `admin_password` da configuracao e do host; mantenha os secrets de banco e JWT protegidos e estaveis. A chave JWT deve ser mantida entre reinicios e atualizacoes, pois troca-la invalida os tokens existentes.

Em banco novo, as migrations incluem o catalogo inicial de escritorios e departamentos. Se for migrar um banco ja existente cuja migration de catalogo ja foi aplicada e os registros foram apagados, a migration nao sera repetida: cadastre os escritorios e departamentos pela interface, ou planeje uma carga inicial revisada antes de abrir o sistema aos usuarios.

## Backup e restauracao

O volume Docker nao substitui backup. Automatize backups criptografados fora do servidor e valide periodicamente a restauracao em uma instancia descartavel. Exemplo de backup logico manual:

```sh
docker compose --env-file .env.production -f docker-compose.production.yml exec -T database \
  pg_dump -U stecar_app -d stecar -Fc > stecar-$(date -u +%Y%m%dT%H%M%SZ).dump
```

Proteja o arquivo como dado confidencial. Antes de qualquer atualizacao, confirme que ha backup recente e que a restauracao foi testada. Nao use `docker compose down -v`: isso apaga o volume persistente do banco.

## Atualizacoes e operacao

- Use uma tag de imagem versionada, registre a versao implantada e mantenha o artefato anterior para rollback.
- Revise migrations antes da publicacao; elas rodam automaticamente no inicio do backend.
- Para atualizar: construa a versao, confirme backup, recrie os servicos e verifique healthchecks, login, cadastro e consulta. Nao use `latest` como identificador de release.
- Configure monitoramento de disco, memoria, disponibilidade HTTPS, expiracao/renovacao do certificado e sucesso dos backups.
- Configure SMTP para habilitar recuperacao de senha; sem SMTP, o recurso por email nao funcionara.
- Avalie atualizar as imagens base para digests verificados e executar scanner de vulnerabilidades no pipeline de release. As tags base atuais dos Dockerfiles nao sao fixadas por digest.

## Escopo e limites

Este Compose e uma base de producao para um unico host, com frontend atras de proxy TLS ja existente. Nao configura DNS, certificado, firewall, monitoramento, backup automatico, alta disponibilidade, armazenamento externo de anexos nem envio SMTP. Esses recursos dependem da infraestrutura escolhida e devem ser configurados antes de um servico critico ou publico.
