CREATE TABLE estoque (
    id_estoque        INTEGER NOT NULL,
    id_produto        INTEGER NOT NULL,
    qtde_produto      DECIMAL(15, 2) NOT NULL DEFAULT 0,
    qtde_reserva      DECIMAL(15, 2) NOT NULL DEFAULT 0,
    qtde_min          DECIMAL(15, 2) NOT NULL DEFAULT 0,
    qtde_maximo       DECIMAL(15, 2) NOT NULL DEFAULT 0,
    data_criacao      TIMESTAMP NOT NULL,
    data_modificacao  TIMESTAMP,
    id_user_criacao   INTEGER NOT NULL,
    id_user_alteracao INTEGER,

    CONSTRAINT pk_id_estoque PRIMARY KEY (id_estoque),
    CONSTRAINT fk_estoque_produto FOREIGN KEY (id_produto) REFERENCES public.produto,
    CONSTRAINT fk_estoque_user_criacao FOREIGN KEY (id_user_criacao) REFERENCES public.usuario,
    CONSTRAINT fk_estoque_user_alteracao FOREIGN KEY (id_user_alteracao) REFERENCES public.usuario
);

CREATE SEQUENCE seq_estoque
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;