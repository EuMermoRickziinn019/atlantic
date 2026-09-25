# Atlantic API

Backend Java para gestão de pessoas, com modelos de empresas, produtos, usuários, perfis e permissões. O projeto está em desenvolvimento e utiliza Spring Boot, PostgreSQL e uma estrutura Maven com dois módulos.

## Tecnologias

- Spring Boot **3.5.5** e Spring Web para a API REST.
- Spring Data JPA / Hibernate para persistência.
- PostgreSQL **16** no ambiente Docker.
- Spring Security e BCrypt para autenticação e controle de permissões.
- Flyway para migrações de banco.
- Springdoc OpenAPI **2.8.9**, Actuator e dependências JJWT **0.12.7**.
- JUnit 5 e Mockito para testes.

A autenticação utiliza tokens JWT assinados com HS256. Veja [Autenticação e permissões](#autenticação-e-permissões).

## Organização do projeto

| Módulo | Responsabilidade |
| --- | --- |
| `atlantic-web` | Inicialização do Spring Boot, controllers REST, segurança, mapeamento de DTOs, configurações e migrações SQL. |
| `atlantic-db` | Entidades, DTOs, interfaces de persistência, classes de regras de negócio e fachada administrativa. |
| `libs/repository` | JARs e POMs do Submarino para conversão JSON e cliente HTTP, consumidos pelo módulo web. |

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
├── libs/repository/
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
| `spring.datasource.username` | Variável `DB_USERNAME` |
| `spring.datasource.password` | Variável `DB_PASSWORD` |
| `security.jwt.secret` | Variável obrigatória `JWT_SECRET` |
| `security.jwt.expiration-seconds` | `900` (15 minutos), sobrescrito por `JWT_EXPIRATION_SECONDS` |
| `security.jwt.issuer` | `AtlanticAPI`, sobrescrito por `JWT_ISSUER` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `spring.jpa.show-sql` | `true` |
| `spring.flyway.enabled` | `true` |
| `spring.flyway.locations` | `classpath:db/migration` |

Para usar outro banco ou outras credenciais, sobrescreva a configuração por variáveis de ambiente. Exemplo no PowerShell, substituindo os valores de exemplo:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/teste_tcc"
$env:DB_USERNAME = "seu_usuario"
$env:DB_PASSWORD = "sua_senha"
$env:SERVER_PORT = "9011"
```

Essas variáveis configuram a aplicação; as credenciais do PostgreSQL precisam corresponder às da instância utilizada.

Defina também `JWT_SECRET` com uma chave aleatória de pelo menos 32 bytes, codificada em Base64. Exemplo para gerar uma chave apenas na sessão PowerShell atual:

```powershell
$jwtKeyBytes = New-Object byte[] 32
$jwtRng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$jwtRng.GetBytes($jwtKeyBytes)
$jwtRng.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($jwtKeyBytes)
```

Execute o Maven nessa mesma sessão. No IntelliJ, configure `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET` nas variáveis de ambiente da configuração de execução. Não versione a chave. Em um ambiente persistente, mantenha a mesma chave em um gerenciador de segredos; gerar outra invalida os tokens anteriores. A aplicação recusa chave inválida/fraca e validade fora do intervalo de 1 a 86400 segundos.

### 4. Compilar e iniciar

Na raiz do projeto:

```sh
mvn clean install -DskipTests
mvn -pl atlantic-web spring-boot:run
```

O primeiro comando instala os módulos no repositório Maven local, sem executar os testes. O segundo inicia a aplicação web. Também é possível importar o POM raiz no IntelliJ e executar a classe `AtlanticApiApplication`.

Endereço base após a inicialização: `http://localhost:9011`.

**Estado atual:** estes passos dependem de banco e ambiente Java/Maven configurados. Confira as variáveis de ambiente e as pendências de migração abaixo se a inicialização falhar.

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

O usuário precisa existir e estar ativo no banco, com senha em BCrypt. O login consulta usuários pelo e-mail. As antigas propriedades `spring.security.user.*` foram removidas; `SC_USERNAME` e `SC_PASSWORD` não são utilizadas.

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

1. Envie e-mail e senha para `POST /api/v1/auth/login`.
2. Copie o campo `accessToken` da resposta.
3. Envie `Authorization: Bearer <accessToken>` em cada chamada protegida.

A resposta mantém `nome`, `email`, `autorizacoes` e `mensagem`, e acrescenta:

| Campo | Conteúdo |
| --- | --- |
| `accessToken` | JWT assinado com HS256. |
| `tokenType` | `Bearer`. |
| `expiresIn` | Validade em segundos; padrão 900. |
| `expiresAt` | Data/hora de expiração em UTC. |

Exemplo de chamada:

```http
GET /api/v1/pessoa/getTodasPessoas HTTP/1.1
Host: localhost:9011
Authorization: Bearer <accessToken>
```

Os tokens contêm identificação do usuário por e-mail, emissor, identificador único, emissão e expiração. Senhas e hashes não são incluídos. O servidor verifica assinatura, algoritmo, emissor e validade, e consulta o usuário no banco a cada chamada para aplicar desativação e alterações de permissão imediatamente.

A API não cria sessão HTTP e não aceita HTTP Basic. Apenas o POST de login é público; as demais rotas, incluindo a documentação Springdoc, exigem autenticação.

| Situação | Resposta |
| --- | --- |
| Login com campos inválidos | `400 Bad Request`. |
| Credenciais incorretas, usuário inexistente ou inativo | `401 Unauthorized`, com mensagem genérica. |
| Token ausente, inválido ou expirado | `401 Unauthorized`. |
| Usuário autenticado sem a permissão necessária | `403 Forbidden`. |

As permissões de pessoas são aplicadas nos métodos do controller:

| Operação | Exigência |
| --- | --- |
| Consultar / listar | `VISUALIZAR`. |
| Criar | `CRIAR`. |
| Atualizar por POST | `EDITAR`. |
| Excluir | Papel `ADMIN` (`ROLE_ADMIN`) e autoridade `EXCLUIR`. |

O token de acesso expira após 15 minutos por padrão. Ao expirar, faça login novamente: não há refresh token nem endpoint de logout/revogação individual. Para sair no cliente, descarte o token; uma cópia dele continua válida até expirar, salvo se o usuário for desativado/removido ou a chave for trocada. Uma troca de senha, isoladamente, não revoga tokens já emitidos.

Use HTTPS fora do ambiente local e trate o token como credencial. Os logs de senha e hash do antigo controller foram removidos.

## Banco de dados e migrações

Os scripts SQL ficam em [db/migration](atlantic-web/src/main/resources/db/migration).

Atualmente, somente `V1__create_teste.sql` utiliza o separador duplo `__` esperado para migrações versionadas. Os demais arquivos, como `V1_create_pessoa.sql` e `V2_create_usuario.sql`, usam um único sublinhado e precisam de revisão. Ao corrigir os nomes, atribua versões únicas e respeite a ordem de criação das tabelas e inserção dos dados.

O Hibernate também está configurado com `ddl-auto=update`. Isso não substitui a revisão das migrações nem garante o cadastro de usuários e permissões necessário ao login.

## Testes

Com o ambiente e o banco preparados:

```sh
mvn test
```

A suíte contém testes de `PessoaController`, `PessoaMapper`, assinatura/validação JWT e integração HTTP com a cadeia real do Spring Security. Os testes JWT usam usuários simulados e não dependem de PostgreSQL. O teste `AtlanticApiApplicationTests` carrega o contexto completo e depende do banco e das variáveis de ambiente.

Para executar os testes de pessoas e autenticação sem depender do banco, a partir da raiz:

```sh
mvn -pl atlantic-web -am "-Dtest=PessoaControllerTest,PessoaMapperTest,JwtServiceTest,JwtSecurityIntegrationTest" -Dsurefire.failIfNoSpecifiedTests=false test
```

`JwtSecurityIntegrationTest` valida login com BCrypt, acesso por Bearer, respostas 400/401/403, expiração, assinatura incorreta, alterações de permissões e desativação de usuários.

## Processos de Funcionamento da API
#### Ultilizaçao dos endpoints
Todo endpoint excluindo o ``POST {{baseUrl}}/api/v1/auth/login`` precisam de login, ou seja, antes de acessar qualquer endpoint,
o endpoint ``POST {{baseUrl}}/api/v1/auth/login`` deve ser acessado com um usuario valido e o tolken salvo na memoria.
Todos os demais endpoints precisam do tolken para liberar.
Toda implementaçao frontend da aplicaçao deve estabelecer um tempo de 15 minutos de espera para rodar o endpoint de login novamente,
visto que o tempo de validade do tolken e de 15 minutos.
