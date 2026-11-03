ALTER TABLE conserto
ADD COLUMN tecnico VARCHAR(150);

UPDATE conserto c
SET tecnico = u.nome_completo
FROM usuario u
WHERE u.id = c.usuario_id AND c.tecnico IS NULL;

ALTER TABLE conserto
ALTER COLUMN tecnico SET NOT NULL;
