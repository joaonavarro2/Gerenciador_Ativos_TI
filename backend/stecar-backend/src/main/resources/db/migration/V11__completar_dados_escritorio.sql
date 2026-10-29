ALTER TABLE escritorio
    ADD COLUMN estado VARCHAR(2),
    ADD COLUMN gestor_nome VARCHAR(150);

UPDATE escritorio
SET gestor_nome = usuario.nome_completo
FROM usuario
WHERE escritorio.gestor_id = usuario.id
  AND escritorio.gestor_nome IS NULL;
