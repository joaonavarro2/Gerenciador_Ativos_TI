ALTER TABLE pessoa
    ADD COLUMN usuario_id BIGINT UNIQUE;

ALTER TABLE pessoa
    ADD CONSTRAINT fk_pessoa_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(id);
