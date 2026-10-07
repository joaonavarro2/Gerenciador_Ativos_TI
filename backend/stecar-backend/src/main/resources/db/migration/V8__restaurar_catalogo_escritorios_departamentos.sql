INSERT INTO departamento (nome, status)
SELECT catalogo.nome, 'ATIVO'
FROM (VALUES
    ('Assessoria da Diretoria'),
    ('Controle Interno'),
    ('Comitê Técnico Ambiental'),
    ('Diretoria Geral'),
    ('Assessoria de Comunicação'),
    ('Assessoria Jurídica'),
    ('Dep.Recursos Humanos'),
    ('Dep.Administrativo'),
    ('Dep.Convênios e Contratos'),
    ('Dep.Financeiro'),
    ('Dep.Licitações'),
    ('Dep.Capacitação'),
    ('Dep.Engenharia'),
    ('Coord.Água para Todos'),
    ('Coord.Pró-Semiárido'),
    ('Coord.Bahia Produtiva'),
    ('Coord.Projetos Especiais'),
    ('Coord.Articulação de Políticas')
) AS catalogo(nome)
WHERE NOT EXISTS (
    SELECT 1 FROM departamento existente WHERE existente.nome = catalogo.nome
);

INSERT INTO escritorio (nome, cidade, cep, endereco, status)
SELECT catalogo.nome, catalogo.nome, 'N/A', 'Endereço não informado', 'ATIVO'
FROM (VALUES
    ('Alagoinhas'),
    ('Amargosa'),
    ('Barreiras'),
    ('Bom Jesus da Lapa'),
    ('Caetité'),
    ('Salvador'),
    ('Cruz das Almas'),
    ('Eunápolis'),
    ('Ribeira do Pombal'),
    ('Feira de Santana'),
    ('Irecê'),
    ('Itaberaba'),
    ('Itabuna'),
    ('Itapetinga'),
    ('Jacobina'),
    ('Jequié'),
    ('Juazeiro'),
    ('Macaúbas'),
    ('Paulo Afonso'),
    ('Riachão do Jacuípe'),
    ('Santa Maria da Vitória'),
    ('Seabra'),
    ('Senhor do Bonfim'),
    ('Serrinha'),
    ('Teixeira de Freitas'),
    ('Valença'),
    ('Vitória da Conquista')
) AS catalogo(nome)
WHERE NOT EXISTS (
    SELECT 1 FROM escritorio existente WHERE existente.nome = catalogo.nome
);