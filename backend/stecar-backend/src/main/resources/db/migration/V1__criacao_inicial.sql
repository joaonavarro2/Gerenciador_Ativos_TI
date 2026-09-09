-- =========================================================
-- STECAR - CRIAÇÃO INICIAL DO BANCO DE DADOS
-- Migration: V1
-- =========================================================


-- =========================================================
-- 1. USUARIO
-- Usuários que administram/utilizam o sistema.
-- =========================================================

CREATE TABLE usuario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome_completo VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    funcao VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL
);


-- =========================================================
-- 2. ESCRITORIO
-- Locais físicos onde os itens podem estar armazenados.
-- =========================================================

CREATE TABLE escritorio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(150),
    gestor_id BIGINT,
    status VARCHAR(20) NOT NULL
);


-- =========================================================
-- 3. DEPARTAMENTO
-- Departamentos existentes dentro dos escritórios.
-- =========================================================

CREATE TABLE departamento (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL
);


-- =========================================================
-- 4. PESSOA
-- Funcionários que podem receber/possuir itens.
-- =========================================================

CREATE TABLE pessoa (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome_completo VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefone VARCHAR(20),
    cargo VARCHAR(100) NOT NULL,
    escritorio_id BIGINT NOT NULL,
    departamento_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_pessoa_escritorio
        FOREIGN KEY (escritorio_id)
        REFERENCES escritorio(id),

    CONSTRAINT fk_pessoa_departamento
        FOREIGN KEY (departamento_id)
        REFERENCES departamento(id)
);


-- =========================================================
-- 5. ITEM
-- Todos os bens/equipamentos administrados pelo STECAR.
-- OUTRO_ITEM foi incorporado nesta tabela.
-- =========================================================

CREATE TABLE item (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    patrimonio VARCHAR(50),
    nome VARCHAR(150) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    serial VARCHAR(150),
    fabricante VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    descricao TEXT,
    status VARCHAR(30) NOT NULL,
    data_aquisicao DATE NOT NULL,
    escritorio_id BIGINT NOT NULL,
    departamento_id BIGINT NOT NULL,
    pessoa_id BIGINT,

    CONSTRAINT fk_item_escritorio
        FOREIGN KEY (escritorio_id)
        REFERENCES escritorio(id),

    CONSTRAINT fk_item_departamento
        FOREIGN KEY (departamento_id)
        REFERENCES departamento(id),

    CONSTRAINT fk_item_pessoa
        FOREIGN KEY (pessoa_id)
        REFERENCES pessoa(id)
);


-- =========================================================
-- 6. MOVIMENTACAO
-- Registra as movimentações dos itens.
-- usuario_id identifica quem realizou a operação.
-- =========================================================

CREATE TABLE movimentacao (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    data TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL,

    departamento_origem_id BIGINT,
    departamento_destino_id BIGINT,

    escritorio_origem_id BIGINT,
    escritorio_destino_id BIGINT,

    pessoa_origem_id BIGINT,
    pessoa_destino_id BIGINT,

    item_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT fk_movimentacao_departamento_origem
        FOREIGN KEY (departamento_origem_id)
        REFERENCES departamento(id),

    CONSTRAINT fk_movimentacao_departamento_destino
        FOREIGN KEY (departamento_destino_id)
        REFERENCES departamento(id),

    CONSTRAINT fk_movimentacao_escritorio_origem
        FOREIGN KEY (escritorio_origem_id)
        REFERENCES escritorio(id),

    CONSTRAINT fk_movimentacao_escritorio_destino
        FOREIGN KEY (escritorio_destino_id)
        REFERENCES escritorio(id),

    CONSTRAINT fk_movimentacao_pessoa_origem
        FOREIGN KEY (pessoa_origem_id)
        REFERENCES pessoa(id),

    CONSTRAINT fk_movimentacao_pessoa_destino
        FOREIGN KEY (pessoa_destino_id)
        REFERENCES pessoa(id),

    CONSTRAINT fk_movimentacao_item
        FOREIGN KEY (item_id)
        REFERENCES item(id),

    CONSTRAINT fk_movimentacao_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id)
);


-- =========================================================
-- 7. CONSERTO
-- Histórico de manutenção/conserto dos itens.
-- =========================================================

CREATE TABLE conserto (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    descricao_problema TEXT NOT NULL,
    descricao_solucao TEXT,
    data_entrada TIMESTAMP NOT NULL,
    data_saida TIMESTAMP,
    status VARCHAR(30) NOT NULL,
    item_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT fk_conserto_item
        FOREIGN KEY (item_id)
        REFERENCES item(id),

    CONSTRAINT fk_conserto_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id)
);


-- =========================================================
-- 8. RELACIONAMENTO ESCRITORIO -> PESSOA
-- O gestor do escritório é uma Pessoa.
-- =========================================================

ALTER TABLE escritorio
ADD CONSTRAINT fk_escritorio_gestor
    FOREIGN KEY (gestor_id)
    REFERENCES pessoa(id);