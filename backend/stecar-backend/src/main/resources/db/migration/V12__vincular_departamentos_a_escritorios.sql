ALTER TABLE departamento
    ADD COLUMN escritorio_id BIGINT;

ALTER TABLE departamento
    ADD CONSTRAINT fk_departamento_escritorio
    FOREIGN KEY (escritorio_id) REFERENCES escritorio(id);
