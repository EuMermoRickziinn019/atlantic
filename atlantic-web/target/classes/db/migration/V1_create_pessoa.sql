create table Pessoa (
    id_pessoa INTEGER NOT NULL,
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    cnpj VARCHAR(14),
    email VARCHAR(50),
    telefone_fixo VARCHAR(10),
    telefone_movel VARCHAR(11),
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alteracao TIMESTAMP,
    inativo BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_pessoa PRIMARY KEY (id_pessoa)
);

CREATE SEQUENCE seq_pessoa
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;