 /**
 * Author:  jeff_
 * Created: 2 de jul. de 2025
 */
CREATE TABLE tb_venda (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Gera UUID automático
    data_venda TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_total NUMERIC(10, 2) NOT NULL,
    forma_pagamento forma_pagamento_enum NOT NULL
);

CREATE TABLE item_pedido (
    venda_id UUID NOT NULL,
    produto_id UUID NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),

    CONSTRAINT fk_venda FOREIGN KEY (venda_id)
        REFERENCES tb_venda(uuid) ON DELETE CASCADE,

    CONSTRAINT fk_produto FOREIGN KEY (produto_id)
        REFERENCES tb_produto(uuid) ON DELETE CASCADE
);

CREATE TABLE produto (
    uuid UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(100),
    preco NUMERIC(10, 2) NOT NULL,
    estoque INT DEFAULT 0,
    codigo_barras VARCHAR(20) UNIQUE NOT NULL
);

# Comando para atribuir privilégios ao usuário
GRANT ALL PRIVILEGES ON TABLE TABELA_AQUI TO "Admin_AdA";
