ALTER TABLE item
    ADD COLUMN instituicao VARCHAR(4);

ALTER TABLE outro_item
    ADD COLUMN patrimonio VARCHAR(50),
    ADD COLUMN instituicao VARCHAR(4);

ALTER TABLE item
    ADD CONSTRAINT ck_item_instituicao
        CHECK (instituicao IS NULL OR instituicao IN ('CAR', 'FLEM'));

ALTER TABLE outro_item
    ADD CONSTRAINT ck_outro_item_instituicao
        CHECK (instituicao IS NULL OR instituicao IN ('CAR', 'FLEM'));

CREATE TABLE historico_instituicao (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_entidade VARCHAR(20) NOT NULL CHECK (tipo_entidade IN ('ITEM', 'OUTRO_ITEM')),
    entidade_id BIGINT NOT NULL,
    instituicao_anterior VARCHAR(4) CHECK (
        instituicao_anterior IS NULL OR instituicao_anterior IN ('CAR', 'FLEM')
    ),
    instituicao_nova VARCHAR(4) NOT NULL CHECK (instituicao_nova IN ('CAR', 'FLEM')),
    alterado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT,
    usuario_nome VARCHAR(150)
);

CREATE INDEX ix_historico_instituicao_entidade
    ON historico_instituicao (tipo_entidade, entidade_id, alterado_em DESC);

CREATE FUNCTION impedir_alteracao_historico_instituicao()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE EXCEPTION 'O histórico de instituição é imutável.';
END;
$$;

CREATE TRIGGER trg_historico_instituicao_imutavel
    BEFORE UPDATE OR DELETE ON historico_instituicao
    FOR EACH ROW EXECUTE FUNCTION impedir_alteracao_historico_instituicao();

CREATE FUNCTION exigir_instituicao_em_novo_patrimonio()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.instituicao IS NULL THEN
        RAISE EXCEPTION 'A instituição é obrigatória para novos registros patrimoniais.';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_item_instituicao_obrigatoria
    BEFORE INSERT ON item
    FOR EACH ROW EXECUTE FUNCTION exigir_instituicao_em_novo_patrimonio();

CREATE TRIGGER trg_outro_item_instituicao_obrigatoria
    BEFORE INSERT ON outro_item
    FOR EACH ROW EXECUTE FUNCTION exigir_instituicao_em_novo_patrimonio();
