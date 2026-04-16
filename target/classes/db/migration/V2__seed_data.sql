-- ============================================================
-- V2__seed_data.sql
-- Comanda Digital - UNASP SP - Dados iniciais obrigatórios
-- Admin: admin@email.com / senha123  (BCrypt)
-- ============================================================

-- ----------------------------
-- Usuários (senha: senha123 → BCrypt)
-- ----------------------------
INSERT INTO usuario (nome, email, senha_hash, perfil, telefone, endereco, status) VALUES
('Administrador', 'admin@email.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'ADMIN', '(11) 99999-0001', 'Rua da Cozinha, 100 - SP', 'ATIVO'),
('Gerente Ops', 'gerente@email.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'GERENTE', '(11) 99999-0002', 'Rua da Gestão, 200 - SP', 'ATIVO'),
('Chef Carlos', 'cozinheiro@email.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'COZINHEIRO', '(11) 99999-0003', 'Av. dos Sabores, 300 - SP', 'ATIVO'),
('Cliente Teste', 'cliente@email.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'CLIENTE', '(11) 98888-0001', 'Rua dos Pedidos, 42, Apto 10 - SP', 'ATIVO');

-- ----------------------------
-- Categorias
-- ----------------------------
INSERT INTO categoria (nome, descricao, ordem, status) VALUES
('Hambúrgueres', 'Artesanais e smash', 1, 'ATIVO'),
('Açaí',         'Bowls e tigelas', 2, 'ATIVO'),
('Bebidas',      'Refrigerantes, sucos e água', 3, 'ATIVO'),
('Sobremesas',   'Doces e gelados', 4, 'ATIVO'),
('Porções',      'Aperitivos e petiscos', 5, 'ATIVO');

-- ----------------------------
-- Ingredientes
-- ----------------------------
INSERT INTO ingrediente (nome, sku, unidade_padrao, estoque_minimo, custo_unitario, status) VALUES
-- Proteínas
('Blend bovino 80/20', 'PROT-001', 'G',   500,  0.0450, 'ATIVO'),
('Frango grelhado',    'PROT-002', 'G',   400,  0.0280, 'ATIVO'),
-- Pães
('Pão brioche',        'PAO-001',  'UN',   20,  2.8000, 'ATIVO'),
('Pão australiano',    'PAO-002',  'UN',   15,  3.2000, 'ATIVO'),
-- Laticínios
('Queijo cheddar',     'LACT-001', 'G',   200,  0.0650, 'ATIVO'),
('Queijo prato',       'LACT-002', 'G',   150,  0.0420, 'ATIVO'),
-- Vegetais
('Alface americana',   'VEG-001',  'G',   100,  0.0120, 'ATIVO'),
('Tomate',             'VEG-002',  'G',   150,  0.0080, 'ATIVO'),
('Cebola roxa',        'VEG-003',  'G',   100,  0.0060, 'ATIVO'),
('Bacon',              'FRIO-001', 'G',   200,  0.0900, 'ATIVO'),
-- Molhos e condimentos
('Molho especial',     'MOL-001',  'ML',  200,  0.0320, 'ATIVO'),
('Mostarda dijon',     'MOL-002',  'ML',  100,  0.0250, 'ATIVO'),
('Maionese',           'MOL-003',  'ML',  300,  0.0180, 'ATIVO'),
-- Açaí
('Polpa de açaí',      'ACA-001',  'G',  1000,  0.0280, 'ATIVO'),
('Granola',            'ACA-002',  'G',   500,  0.0150, 'ATIVO'),
('Banana',             'ACA-003',  'UN',   30,  0.8000, 'ATIVO'),
('Leite condensado',   'ACA-004',  'ML',  500,  0.0200, 'ATIVO'),
-- Bebidas
('Refrigerante lata',  'BEB-001',  'UN',   48,  2.5000, 'ATIVO'),
('Suco concentrado',   'BEB-002',  'ML',  500,  0.0150, 'ATIVO'),
-- Batata
('Batata palito',      'BAT-001',  'G',   500,  0.0120, 'ATIVO'),
('Óleo para fritar',   'OLE-001',  'ML',  1000, 0.0050, 'ATIVO'),
('Sal',                'TMP-001',  'G',   500,  0.0010, 'ATIVO');

-- ----------------------------
-- Fornecedores
-- ----------------------------
INSERT INTO fornecedor (razao_social, cnpj, telefone, email, status) VALUES
('Frigorífico São Paulo LTDA',  '11.222.333/0001-81', '(11) 3333-1111', 'vendas@frigorificasp.com.br', 'ATIVO'),
('Distribuidora Verde Sabor',   '22.333.444/0001-89', '(11) 3333-2222', 'pedidos@verdesabor.com.br',   'ATIVO'),
('Açaí da Amazônia EIRELI',     '33.444.555/0001-06', '(11) 3333-3333', 'contato@acaidaamazonia.com',  'ATIVO'),
('Bebidas Brasil Distribuidora','44.555.666/0001-40', '(11) 3333-4444', 'vendas@bebidasbrasil.com',    'ATIVO');

-- ----------------------------
-- Pratos (5 obrigatórios)
-- ----------------------------
INSERT INTO prato (categoria_id, nome, descricao, foto_url, preco_venda, tempo_preparo_min, status) VALUES
(1, 'Hambúrguer Artesanal Classic',
    'Blend bovino 180g, queijo cheddar, alface, tomate, molho especial no pão brioche.',
    'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500',
    39.90, 15, 'ATIVO'),
(1, 'Smash Burguer Bacon',
    'Dois smash 90g, bacon crocante, queijo prato, cebola caramelizada e mostarda dijon.',
    'https://images.unsplash.com/photo-1565299507177-b0ac66763828?w=500',
    44.90, 12, 'ATIVO'),
(1, 'Chicken Crispy',
    'Frango grelhado, queijo prato, alface, tomate e maionese no pão australiano.',
    'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=500',
    36.90, 15, 'ATIVO'),
(2, 'Bowl de Açaí 500g',
    '500g de açaí cremoso com granola, banana e leite condensado.',
    'https://images.unsplash.com/photo-1590301157890-4810ed352733?w=500',
    28.90, 8, 'ATIVO'),
(5, 'Porção de Batata Frita',
    '300g de batata palito frita, temperada com sal e servida com maionese.',
    'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=500',
    22.90, 12, 'ATIVO');

-- ----------------------------
-- Fichas Técnicas
-- ----------------------------

-- Prato 1: Hambúrguer Artesanal Classic
INSERT INTO ficha_tecnica (prato_id, rendimento, modo_preparo) VALUES
(1, 1, '1. Tempere o blend com sal e pimenta. 2. Grelhe na chapa por 4 min cada lado. 3. Monte: pão tostado, molho, alface, tomate, hambúrguer, queijo derretido, pão.');

INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
(1, 1,  180, 'G',  1.00),  -- blend bovino
(1, 3,  1,   'UN', 1.00),  -- pão brioche
(1, 5,  40,  'G',  1.00),  -- queijo cheddar
(1, 7,  30,  'G',  1.40),  -- alface (FC: limpeza)
(1, 8,  50,  'G',  1.25),  -- tomate (FC: sementes)
(1, 11, 25,  'ML', 1.00);  -- molho especial

-- Prato 2: Smash Burguer Bacon
INSERT INTO ficha_tecnica (prato_id, rendimento, modo_preparo) VALUES
(2, 1, '1. Divida 180g de blend em 2 bolinhas. 2. Smash na chapa bem quente. 3. Frite o bacon. 4. Monte com queijo, cebola caramelizada e mostarda.');

INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
(2, 1,  180, 'G',  1.00),  -- blend bovino
(2, 3,  1,   'UN', 1.00),  -- pão brioche
(2, 6,  50,  'G',  1.00),  -- queijo prato
(2, 10, 40,  'G',  1.00),  -- bacon
(2, 9,  30,  'G',  1.15),  -- cebola roxa
(2, 12, 20,  'ML', 1.00);  -- mostarda dijon

-- Prato 3: Chicken Crispy
INSERT INTO ficha_tecnica (prato_id, rendimento, modo_preparo) VALUES
(3, 1, '1. Tempere e grelhe o frango. 2. Torre o pão australiano. 3. Monte com maionese, alface, tomate e o frango.');

INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
(3, 2,  150, 'G',  1.10),  -- frango (FC: osso/aparas)
(3, 4,  1,   'UN', 1.00),  -- pão australiano
(3, 6,  40,  'G',  1.00),  -- queijo prato
(3, 7,  30,  'G',  1.40),  -- alface
(3, 8,  50,  'G',  1.25),  -- tomate
(3, 13, 30,  'ML', 1.00);  -- maionese

-- Prato 4: Bowl de Açaí 500g
INSERT INTO ficha_tecnica (prato_id, rendimento, modo_preparo) VALUES
(4, 1, '1. Bata a polpa de açaí congelada. 2. Monte na tigela. 3. Adicione granola, banana fatiada e fio de leite condensado.');

INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
(4, 14, 500, 'G',  1.00),  -- polpa de açaí
(4, 15, 60,  'G',  1.00),  -- granola
(4, 16, 1,   'UN', 1.20),  -- banana (FC: casca)
(4, 17, 30,  'ML', 1.00);  -- leite condensado

-- Prato 5: Porção de Batata Frita
INSERT INTO ficha_tecnica (prato_id, rendimento, modo_preparo) VALUES
(5, 1, '1. Frite as batatas em óleo a 180°C por 8 min. 2. Escorra e tempere com sal. 3. Sirva com maionese à parte.');

INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
(5, 20, 350, 'G',  1.15),  -- batata palito (FC: aparas)
(5, 21, 200, 'ML', 1.00),  -- óleo
(5, 22, 3,   'G',  1.00),  -- sal
(5, 13, 30,  'ML', 1.00);  -- maionese

-- ----------------------------
-- Estoque inicial (entradas manuais)
-- ----------------------------
INSERT INTO estoque_movimentacao
    (ingrediente_id, tipo, quantidade, motivo, custo_unitario, usuario_id)
VALUES
-- Proteínas
(1,  'ENTRADA', 5000,  'AJUSTE', 0.0450, 1),
(2,  'ENTRADA', 3000,  'AJUSTE', 0.0280, 1),
-- Pães
(3,  'ENTRADA', 100,   'AJUSTE', 2.8000, 1),
(4,  'ENTRADA', 60,    'AJUSTE', 3.2000, 1),
-- Laticínios
(5,  'ENTRADA', 2000,  'AJUSTE', 0.0650, 1),
(6,  'ENTRADA', 1500,  'AJUSTE', 0.0420, 1),
-- Vegetais
(7,  'ENTRADA', 800,   'AJUSTE', 0.0120, 1),
(8,  'ENTRADA', 1200,  'AJUSTE', 0.0080, 1),
(9,  'ENTRADA', 600,   'AJUSTE', 0.0060, 1),
(10, 'ENTRADA', 1500,  'AJUSTE', 0.0900, 1),
-- Molhos
(11, 'ENTRADA', 1000,  'AJUSTE', 0.0320, 1),
(12, 'ENTRADA', 500,   'AJUSTE', 0.0250, 1),
(13, 'ENTRADA', 1500,  'AJUSTE', 0.0180, 1),
-- Açaí
(14, 'ENTRADA', 5000,  'AJUSTE', 0.0280, 1),
(15, 'ENTRADA', 2000,  'AJUSTE', 0.0150, 1),
(16, 'ENTRADA', 50,    'AJUSTE', 0.8000, 1),
(17, 'ENTRADA', 2000,  'AJUSTE', 0.0200, 1),
-- Bebidas
(18, 'ENTRADA', 144,   'AJUSTE', 2.5000, 1),
(19, 'ENTRADA', 3000,  'AJUSTE', 0.0150, 1),
-- Batata
(20, 'ENTRADA', 3000,  'AJUSTE', 0.0120, 1),
(21, 'ENTRADA', 5000,  'AJUSTE', 0.0050, 1),
(22, 'ENTRADA', 1000,  'AJUSTE', 0.0010, 1);

-- ----------------------------
-- Catálogo: vincula ingredientes aos fornecedores
-- ----------------------------
INSERT INTO fornecedor_produto (fornecedor_id, ingrediente_id, preco, unidade_venda) VALUES
-- Frigorífico
(1, 1,  0.0430, 'G'),
(1, 2,  0.0270, 'G'),
(1, 10, 0.0880, 'G'),
-- Distribuidora Verde Sabor
(2, 3,  2.7500, 'UN'),
(2, 4,  3.1500, 'UN'),
(2, 5,  0.0640, 'G'),
(2, 6,  0.0410, 'G'),
(2, 7,  0.0110, 'G'),
(2, 8,  0.0075, 'G'),
(2, 9,  0.0055, 'G'),
(2, 11, 0.0310, 'ML'),
(2, 12, 0.0240, 'ML'),
(2, 13, 0.0170, 'ML'),
(2, 20, 0.0115, 'G'),
(2, 21, 0.0048, 'ML'),
(2, 22, 0.0009, 'G'),
-- Açaí da Amazônia
(3, 14, 0.0270, 'G'),
(3, 15, 0.0140, 'G'),
(3, 16, 0.7500, 'UN'),
(3, 17, 0.0190, 'ML'),
-- Bebidas Brasil
(4, 18, 2.4500, 'UN'),
(4, 19, 0.0140, 'ML');
