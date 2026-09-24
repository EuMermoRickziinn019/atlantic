# Atlantic API

Backend Java para gestão de pessoas, com modelos de empresas, produtos, usuários, perfis e permissões. O projeto está em desenvolvimento e utiliza Spring Boot, PostgreSQL e uma estrutura Maven com três módulos.

## Tecnologias

- Spring Boot **3.5.5** e Spring Web para a API REST.
- Spring Data JPA / Hibernate para persistência.
- PostgreSQL **16** no ambiente Docker.
- Spring Security e BCrypt para autenticação e controle de permissões.
- Flyway para migrações de banco.
- Springdoc OpenAPI **2.8.9**, Actuator e dependências JJWT **0.12.7**.
- JUnit 5 e Mockito para testes.

A presença das dependências JJWT não significa que a autenticação JWT esteja implementada: veja [Autenticação e permissões](#autenticação-e-permissões).

## Organização do projeto

| Módulo | Responsabilidade |
| --- | --- |
| `atlantic-web` | Inicialização do Spring Boot, controllers REST, segurança, mapeamento de DTOs, configurações e migrações SQL. |
| `atlantic-db` | Entidades, DTOs, interfaces de persistência, classes de regras de negócio e fachada administrativa. |
| `atlantic-comuns` | Módulo reservado para componentes compartilhados; atualmente possui apenas a configuração Maven. |

```text
atlantic/
├── pom.xml
├── atlantic-web/
│   └── src/
│       ├── main/java/com/atlantic/atlanticapi/
│       │   ├── AtlanticApiApplication.java
│       │   ├── config/
│       │   ├── controller/
│       │   └── core/mapper/
│       ├── main/resources/
│       │   ├── application.properties
│       │   └── db/migration/
│       └── test/java/
├── atlantic-db/
│   └── src/main/java/com/atlantic/
│       ├── ISBServices/
│       └── models/
├── atlantic-comuns/
├── docker-compose.yaml
└── api-tests.http
```

Nas operações de pessoas, o controller recebe os dados, utiliza o `PessoaMapper` para conversão e chama a `SessionFacadeADM`, que concentra o acesso às operações administrativas.

## Pré-requisitos

- Git.
- JDK: o POM raiz declara Java **21**, enquanto os módulos declaram `source/target` **23**. Use um JDK 23 para desenvolvimento e confira a configuração efetiva do Maven/IDE; essas declarações ainda precisam ser padronizadas.
- Maven instalado e disponível no terminal, ou o Maven integrado do IntelliJ IDEA.
- PostgreSQL disponível localmente, ou Docker com Docker Compose.

Os scripts `mvnw` e `mvnw.cmd` estão no repositório, mas a pasta `.mvn/wrapper` não está versionada. Por isso, os comandos abaixo utilizam `mvn`.

## Executar localmente

### 1. Clonar o repositório

```sh
git clone https://github.com/Richardmedeiros11/atlantic.git
cd atlantic
```

### 2. Iniciar o banco

```sh
docker compose up -d postgres
```

O Compose inicia somente o PostgreSQL, usando a imagem `postgres:16-alpine`, o banco `teste_tcc`, a porta `5432` e um volume persistente chamado `postgres_data`. As credenciais locais estão no arquivo [docker-compose.yaml](docker-compose.yaml).

Se já houver PostgreSQL na porta 5432, utilize essa instância ou ajuste o mapeamento de porta e a URL da aplicação.

### 3. Conferir a configuração

Arquivo principal: [application.properties](atlantic-web/src/main/resources/application.properties).

| Propriedade | Configuração atual |
| --- | --- |
| `spring.application.name` | `AtlanticAPI` |
| `server.port` | `9011` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/teste_tcc` |
| `spring.datasource.username` | `postgres` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `spring.jpa.show-sql` | `true` |
| `spring.flyway.enabled` | `true` |
| `spring.flyway.locations` | `classpath:db/migration` |

Para usar outro banco ou outras credenciais, sobrescreva a configuração por variáveis de ambiente. Exemplo no PowerShell, substituindo os valores de exemplo:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/teste_tcc"
$env:SPRING_DATASOURCE_USERNAME = "seu_usuario"
$env:SPRING_DATASOURCE_PASSWORD = "sua_senha"
$env:SERVER_PORT = "9011"
```

Essas variáveis configuram a aplicação; as credenciais do PostgreSQL precisam corresponder às da instância utilizada.

### 4. Compilar e iniciar

Na raiz do projeto:

```sh
mvn clean install -DskipTests
mvn -pl atlantic-web spring-boot:run
```

O primeiro comando instala os módulos no repositório Maven local, sem executar os testes. O segundo inicia a aplicação web. Também é possível importar o POM raiz no IntelliJ e executar a classe `AtlanticApiApplication`.

Endereço base após a inicialização: `http://localhost:9011`.

**Estado atual:** estes passos dependem de banco e ambiente Java/Maven configurados. Consulte as pendências de migração e autenticação abaixo se a inicialização ou as chamadas protegidas falharem.

## Endpoints implementados

Todas as rotas usam o endereço base acima.

| Método | Rota | Operação |
| --- | --- | --- |
| `POST` | `/api/v1/auth/login` | Validar e-mail e senha de um usuário. |
| `POST` | `/api/v1/pessoa/criarPessoa` | Criar pessoa. |
| `GET` | `/api/v1/pessoa/getTodasPessoas` | Listar pessoas. |
| `GET` | `/api/v1/pessoa/{id}` | Consultar pessoa pelo identificador. |
| `POST` | `/api/v1/pessoa/{id}` | Atualizar pessoa. |
| `DELETE` | `/api/v1/pessoa/{id}` | Excluir pessoa. |

A atualização utiliza **POST** no controller atual. Empresas e produtos possuem modelos e operações na camada de negócio, mas não têm controllers REST neste estado do projeto.

Exemplo de corpo JSON para login:

```json
{
  "email": "usuario@example.com",
  "senha": "sua_senha"
}
```

O usuário precisa existir no banco, com senha compatível com BCrypt. O login consulta usuários pelo e-mail; as propriedades `spring.security.user.*` não devem ser tratadas como um cadastro de usuário da aplicação.

Exemplo de corpo JSON para criação de pessoa:

```json
{
  "nome": "Maria Silva",
  "dataNascimento": "1990-05-12",
  "cpf": "12345678901",
  "cnpj": null,
  "email": "maria@example.com",
  "telefoneFixo": null,
  "telefoneModel": "11999998888",
  "inativo": false
}
```

O campo `telefoneModel` reproduz o nome presente no DTO atual. Há também requisições de exemplo em [api-tests.http](api-tests.http).

## Autenticação e permissões

A configuração usa sessões `STATELESS`, BCrypt e consulta de usuários no banco. As rotas `/api/v1/auth/**` são públicas; as demais exigem autenticação.

O login retorna informações do usuário e suas autorizações, mas **ainda não emite token nem estabelece uma sessão para as próximas chamadas**. Não há filtro JWT configurado, e HTTP Basic e formulário de login estão desativados. Assim, o fluxo de acesso autenticado às rotas protegidas ainda precisa ser concluído.

As regras de URL e as anotações `@PreAuthorize` são cumulativas:

| Operação de pessoa | Exigências atuais |
| --- | --- |
| Consultar / listar | Usuário autenticado e autoridade `VISUALIZAR`. |
| Criar | Autoridade `CRIAR`, também exigida no método. |
| Atualizar por POST | Autoridade `EDITAR` no método e `CRIAR` ou `ROLE_ADMIN` na regra de URL para POST. |
| Excluir | Papel `ADMIN` (`ROLE_ADMIN`) e autoridade `EXCLUIR`. |

A dependência Springdoc está incluída, mas as rotas da documentação também estão sujeitas à regra geral de autenticação.

## Banco de dados e migrações

Os scripts SQL ficam em [db/migration](atlantic-web/src/main/resources/db/migration).

Atualmente, somente `V1__create_teste.sql` utiliza o separador duplo `__` esperado para migrações versionadas. Os demais arquivos, como `V1_create_pessoa.sql` e `V2_create_usuario.sql`, usam um único sublinhado e precisam de revisão. Ao corrigir os nomes, atribua versões únicas e respeite a ordem de criação das tabelas e inserção dos dados.

O Hibernate também está configurado com `ddl-auto=update`. Isso não substitui a revisão das migrações nem garante o cadastro de usuários e permissões necessário ao login.

## Testes

Com o ambiente e o banco preparados:

```sh
mvn test
```

A suíte contém testes unitários de `PessoaController` e `PessoaMapper`, além de um teste de carregamento do contexto Spring que depende da configuração da aplicação e do banco.

Para selecionar apenas os dois testes unitários, a partir da raiz:

```sh
mvn -pl atlantic-web -am "-Dtest=PessoaControllerTest,PessoaMapperTest" -Dsurefire.failIfNoSpecifiedTests=false test
```

Os testes do controller chamam os métodos diretamente e não validam toda a cadeia HTTP de segurança.

## Pendências para evolução

- Padronizar a versão Java entre o POM raiz e os módulos.
- Completar a autenticação das requisições protegidas e alinhar as regras de atualização de pessoas.
- Revisar nomes, versões e ordem das migrações Flyway.
- Remover do login os logs de depuração que imprimem senha e hash.
- Externalizar credenciais antes de utilizar o projeto fora do ambiente local.
