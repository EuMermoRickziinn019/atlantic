INSERT INTO permissao (id_permissao, nome) VALUES
       (1, 'CRIAR'),
       (2, 'EDITAR'),
       (3, 'EXCLUIR'),
       (4, 'VISUALIZAR');


INSERT INTO perfil (id_perfil, nome) VALUES
     (1, 'ROLE_ADMIN'),
     (2, 'ROLE_OPERADOR');

INSERT INTO perfil_permissao (perfil_id, permissao_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4);

INSERT INTO perfil_permissao (perfil_id, permissao_id) VALUES
   (2, 1), (2, 2), (2, 4);

INSERT INTO usuario (id_usuario, nome, email, senha, inativo) VALUES
    (1, 'Administrador do Sistema', 'admin@atlantic.com', '$2a$10$e8.I1N..9R9C6dO9ZqI8Ou.Xm1jU2X3P7Q8R9S0T1U2V3W4X5Y6Z7', false);

INSERT INTO usuario_perfil (usuario_id, perfil_id) VALUES
    (1, 1);