# Bibliotecas Submarino

O repositório Maven em `repository/` contém os JARs e POMs de
`com.submarino:submarino-comuns:1.0.0` e
`com.submarino:submarino-http-client:1.0.0`, além do POM pai.
Os artefatos são compilados com Java 23 e incluídos nas dependências de `atlantic-web`.

Imports: `com.submarino.json.JsonConvert`,
`com.submarino.json.impl.JsonConvertComuns`,
`com.submarino.httpClient.SubmarinoHttpClient` e
`com.submarino.httpClient.impl.SubmarinoHttpClientMethods`.

Para atualizar, execute `mvn clean package` no projeto Submarino, substitua os
JARs e POMs nas pastas de versão correspondentes e atualize os arquivos SHA-1.
Depois execute `mvn -U package` na raiz do Atlantic.

Também disponíveis: submarino-files e submarino-fiscal, versão 1.0.0.
Os quatro JARs são gerados na pasta target da raiz do Submarino.
