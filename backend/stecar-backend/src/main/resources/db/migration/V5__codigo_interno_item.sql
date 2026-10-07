ALTER TABLE item
ADD COLUMN codigo VARCHAR(50);

CREATE UNIQUE INDEX uk_item_codigo
ON item (codigo)
WHERE codigo IS NOT NULL;