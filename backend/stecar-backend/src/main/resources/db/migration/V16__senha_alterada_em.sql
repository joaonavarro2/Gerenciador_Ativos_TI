-- Tokens emitidos antes da última troca de senha deixam de ser aceitos.
ALTER TABLE usuario ADD COLUMN senha_alterada_em TIMESTAMP WITH TIME ZONE;
