ALTER TABLE pessoa
    ADD COLUMN matricula VARCHAR(30),
    ADD COLUMN cpf VARCHAR(14);

CREATE UNIQUE INDEX uq_pessoa_matricula
    ON pessoa (LOWER(matricula))
    WHERE matricula IS NOT NULL AND TRIM(matricula) <> '';

CREATE UNIQUE INDEX uq_pessoa_cpf
    ON pessoa (REGEXP_REPLACE(cpf, '[^0-9]', '', 'g'))
    WHERE cpf IS NOT NULL AND TRIM(cpf) <> '';
