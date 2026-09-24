CREATE TABLE perfil (
    id_perfil BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE permissao (
   id_permissao BIGSERIAL PRIMARY KEY,
   nome VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE usuario (
     id_usuario BIGSERIAL PRIMARY KEY,
     nome VARCHAR(100) NOT NULL,
     email VARCHAR(100) NOT NULL UNIQUE,
     senha VARCHAR(255) NOT NULL,
     inativo BOOLEAN DEFAULT FALSE
);
CREATE TABLE usuario_perfil (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    perfil_id BIGINT NOT NULL REFERENCES perfil(id_perfil) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, perfil_id)
);
CREATE TABLE perfil_permissao (
      perfil_id BIGINT NOT NULL REFERENCES perfil(id_perfil) ON DELETE CASCADE,
      permissao_id BIGINT NOT NULL REFERENCES permissao(id_permissao) ON DELETE CASCADE,
      PRIMARY KEY (perfil_id, permissao_id)
);