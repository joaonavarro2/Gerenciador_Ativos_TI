UPDATE item
SET codigo = 'BEM-' || LPAD(id::TEXT, 3, '0')
WHERE codigo IS NULL;