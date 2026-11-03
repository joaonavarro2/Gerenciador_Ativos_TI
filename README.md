# Gerenciador de Ativos TI (STECAR)

Sistema de inventário e movimentação de bens de TI: bens, movimentações, consertos, pessoas, escritórios, departamentos e outros itens.

| Camada | Tecnologia |
| --- | --- |
| Backend | Java 25, Spring Boot 4, Spring Security (JWT HS512), JPA, Flyway, PostgreSQL 16 |
| Frontend | Vue 3, Quasar 2, Vite, Axios, Pinia |
| Infra | Docker Compose (PostgreSQL, backend, nginx servindo o SPA e fazendo proxy de `/api`) |

## Subindo com Docker

```bash
cp .env.example .env   # preencha todos os valores
docker compose up -d --build
```

- Aplicação: http://localhost:8080 (o nginx serve o frontend e encaminha `/api` para o backend, que não é publicado diretamente)
- PostgreSQL: publicado apenas em `127.0.0.1:5432`

## Variáveis de ambiente

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `POSTGRES_DB`, `POSTGRES_USER` | não | Nome do banco e usuário (padrão `stecar` / `ativos_ti`). |
| `POSTGRES_PASSWORD` | **sim** | Senha do banco. Não há valor padrão. |
| `STECAR_JWT_SECRET` | **sim** | Chave HMAC do JWT, **mínimo de 64 caracteres** (HS512). A aplicação não sobe com chave menor. Gere com `openssl rand -base64 64 \| tr -d '\n'`. |
| `STECAR_JWT_EXPIRACAO_MINUTOS` | não | Validade do token (padrão 30). O frontend renova o token automaticamente nos 5 minutos finais. |
| `STECAR_ADMIN_EMAIL`, `STECAR_ADMIN_NOME` | não | Dados do administrador geral criado na primeira execução. |
| `STECAR_ADMIN_PASSWORD` | **sim** (Docker) | Senha inicial do administrador geral. Só é usada se o usuário ainda não existir. |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` | não | SMTP da recuperação de senha. Sem SMTP, o pedido é aceito mas nenhum e-mail é enviado (fica registrado no log). |
| `STECAR_FRONTEND_URL` | não | URL usada no link de redefinição de senha. |
| `SHOW_SQL` | não | `true` para logar o SQL do Hibernate (apenas desenvolvimento). |

## Perfis de acesso

| Função | Permissões |
| --- | --- |
| `USUARIO` | Somente leitura. É a função de quem se cadastra pela tela pública. |
| `ADMINISTRADOR` | Leitura e escrita (criar, editar, excluir) em todos os cadastros. |
| `ADMINISTRADOR GERAL` | Tudo do administrador, mais gestão de usuários (`/api/usuarios`) e exclusão de movimentações. |

As regras são aplicadas no backend (`SecurityConfig`). A função e o status são lidos do banco a cada requisição, então desativar um usuário ou mudar sua função tem efeito imediato.

Movimentações registradas são históricas: bem, tipo, data, origem e destino não podem ser alterados, apenas status e justificativa. Para corrigir, registre uma nova movimentação.

## Desenvolvimento local

Backend (precisa de um PostgreSQL acessível):

```bash
cd backend/stecar-backend
export DB_PASSWORD=... STECAR_JWT_SECRET=... STECAR_ADMIN_PASSWORD=...
./mvnw spring-boot:run
```

Frontend:

```bash
cd frontend/quasar-project
npm ci
npm run dev
```

## Testes

```bash
# Backend: usa o PostgreSQL de DB_HOST (padrão localhost) com senha DB_PASSWORD (padrão nos testes: postgres)
cd backend/stecar-backend && ./mvnw verify

# Frontend
cd frontend/quasar-project && npm run lint:check && npm run build
```

O CI (`.github/workflows/ci.yml`) roda os testes do backend contra um PostgreSQL 16, o lint e o build do frontend e o `npm audit`.
