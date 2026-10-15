CREATE TABLE token_recuperacao_senha (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(100) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    expira_em TIMESTAMP WITH TIME ZONE NOT NULL,
    usado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_token_recuperacao_usuario ON token_recuperacao_senha(usuario_id);