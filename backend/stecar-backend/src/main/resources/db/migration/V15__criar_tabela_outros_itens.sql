CREATE TABLE outro_item (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    escritorio_id BIGINT NOT NULL,
    localizacao VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL DEFAULT 0 CHECK (quantidade >= 0),
    estoque_minimo INTEGER NOT NULL DEFAULT 0 CHECK (estoque_minimo >= 0),
    fornecedor VARCHAR(150),
    status VARCHAR(20) NOT NULL,
    CONSTRAINT uq_outro_item_codigo UNIQUE (codigo),
    CONSTRAINT fk_outro_item_escritorio FOREIGN KEY (escritorio_id) REFERENCES escritorio(id)
);
