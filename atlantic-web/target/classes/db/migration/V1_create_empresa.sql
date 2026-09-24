CREATE TABLE Empresa (
     id_empresa INTEGER,
     razao_social VARCHAR(150) NOT NULL,
     nome_fantasia VARCHAR(150),
     cnpj VARCHAR(14) NOT NULL UNIQUE,
     inscricao_estadual VARCHAR(20),
     inscricao_municipal VARCHAR(20),
     regime_tributario SMALLINT NOT NULL DEFAULT 1, -- 1: Simples Nacional, 2: Lucro Presumido, 3: Lucro Real
     cep VARCHAR(8) NOT NULL,
     logradouro VARCHAR(150) NOT NULL,
     numero VARCHAR(20) NOT NULL,
     complemento VARCHAR(100),
     bairro VARCHAR(100) NOT NULL,
     cidade VARCHAR(100) NOT NULL,
     uf VARCHAR(2) NOT NULL,
     codigo_ibge_cidade VARCHAR(7),
     telefone VARCHAR(15),
     email VARCHAR(100) NOT NULL,
     site VARCHAR(150),
     logo VARCHAR(255), -- Caminho/URL da logomarca
     ativo BOOLEAN NOT NULL DEFAULT TRUE,
     data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

     CONSTRAINT pk_empresa PRIMARY KEY (id_empresa)
);

CREATE SEQUENCE seq_empresa
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;