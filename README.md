# Shortlink API

REST API para encurtamento de URLs, desenvolvida com Java e Spring Boot. O projeto permite criar links curtos, armazená-los no PostgreSQL e redirecionar usuários para as URLs originais.

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Web
- Bean Validation
- PostgreSQL
- Flyway
- Maven
- Docker e Docker Compose

## Funcionalidades

- Criação de links curtos.
- Geração aleatória de códigos de 7 caracteres.
- Persistência dos links no PostgreSQL.
- Validação das URLs recebidas.
- Redirecionamento HTTP 302.
- Resposta HTTP 404 para códigos inexistentes.
- Data de criação gerada pelo banco de dados.
- Migrações de banco de dados com Flyway.
- Execução da aplicação com Docker Compose.

## Como executar

### Pré-requisitos

- Docker e Docker Compose
- Git

### 1. Clone o repositório

```bash
git clone https://github.com/arlindofernandes/shortlink-api.git
cd shortlink-api
```

### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto, conforme a configuração do Docker Compose:

```dotenv
DB_URL=jdbc:postgresql://postgres:5432/shortlink
DB_USERNAME=shortlink
DB_PASSWORD=devpassword
```

Os valores acima são destinados ao ambiente local de desenvolvimento. Não utilize credenciais de desenvolvimento em produção e não versione o arquivo `.env`.

### 3. Inicie a aplicação

```bash
docker compose up -d --build
```

Consulte os logs:

```bash
docker compose logs -f app
```

Para verificar a saúde da aplicação:

```bash
curl -i http://localhost:8080/actuator/health
```

### 4. Execute os testes

Com Java 21 instalado:

```bash
./mvnw test
```

## Endpoints

### Criar um link curto

**Endpoint:** `POST /api/v1/short-links`

Requisição:

```json
{
  "originalUrl": "https://www.google.com"
}
```

Resposta esperada — `201 Created`:

```json
{
  "id": 1,
  "originalUrl": "https://www.google.com",
  "shortCode": "aWHPiM4",
  "shortUrl": "http://localhost:8080/aWHPiM4",
  "createdAt": "2026-10-26T19:20:00Z"
}
```

Os valores são ilustrativos; o identificador, o código e a data serão gerados durante a execução.

Exemplo com cURL:

```bash
curl -i -X POST http://localhost:8080/api/v1/short-links \
  -H "Content-Type: application/json" \
  -d '{"originalUrl":"https://www.google.com"}'
```

### Redirecionar para a URL original

**Endpoint:** `GET /{shortCode}`

Exemplo:

```bash
curl -i http://localhost:8080/aWHPiM4
```

Para um código existente, a API retorna `302 Found`, com o cabeçalho `Location` apontando para a URL original.

### Códigos de resposta

| Status | Descrição |
|---|---|
| `201 Created` | Link curto criado |
| `302 Found` | Redirecionamento para a URL original |
| `400 Bad Request` | Requisição inválida |
| `404 Not Found` | Código curto inexistente |

## Banco de dados

O PostgreSQL armazena os links na tabela `short_links`.

As migrações são gerenciadas pelo Flyway e ficam em:

`src/main/resources/db/migration/`

## Próximas melhorias

- Testes automatizados dos endpoints.
- Padronização das respostas de erro.
- Configuração da URL-base por ambiente.
- Documentação OpenAPI/Swagger.
- Métricas de utilização dos links.
- Estratégias adicionais para colisões de códigos curtos.

## Objetivo

Projeto de portfólio para praticar desenvolvimento backend com Java, Spring Boot, APIs REST, persistência relacional, validação, migrações de banco de dados e conteinerização.
