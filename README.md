# Shortlink API

REST API para encurtamento de URLs, desenvolvida com Java 21 e Spring Boot. Permite criar links curtos, armazená-los no PostgreSQL e redirecionar usuários para as URLs originais.

![CI](https://github.com/arlindofernandes/shortlink-api/actions/workflows/ci.yml/badge.svg)

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Web
- Bean Validation
- PostgreSQL 16
- Flyway
- Maven
- Docker e Docker Compose
- GitHub Actions
- GitHub Container Registry (GHCR)

## Funcionalidades

- Criação de links curtos.
- Geração aleatória de códigos alfanuméricos de 7 caracteres.
- Persistência dos links no PostgreSQL.
- Validação das URLs recebidas.
- Redirecionamento HTTP 302 para a URL original.
- Resposta HTTP 404 para códigos inexistentes.
- Data de criação gerada pelo banco de dados.
- Migrações do esquema com Flyway.
- Testes unitários e de integração.
- CI no GitHub Actions para executar os testes.
- Publicação automática da imagem Docker no GHCR após os testes passarem em um push para a branch `main`.

## Como executar localmente

### Pré-requisitos

- Git
- Docker Engine e Docker Compose
- Java 21, caso deseje executar os testes fora do Docker

### 1. Clone o repositório

```bash
git clone https://github.com/arlindofernandes/shortlink-api.git
cd shortlink-api
```

### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```dotenv
DB_URL=jdbc:postgresql://postgres:5432/shortlink
DB_USERNAME=shortlink
DB_PASSWORD=devpassword
```

Esses valores destinam-se somente ao desenvolvimento local. Não use essas credenciais em produção e não versione o arquivo `.env`.

### 3. Inicie a aplicação

```bash
docker compose up -d --build
```

Consulte o estado dos serviços:

```bash
docker compose ps
```

Consulte os logs da API:

```bash
docker compose logs -f app
```

Verifique a saúde da aplicação:

```bash
curl -i http://localhost:8080/actuator/health
```

Uma resposta saudável retorna HTTP `200` e status `UP`.

### 4. Execute os testes

Com Java 21 instalado e o PostgreSQL de teste configurado conforme os arquivos de teste:

```bash
./mvnw clean test
```

Os testes de integração usam o perfil `test` e a configuração em `src/test/resources/application-test.yaml`.

### 5. Encerre os serviços

```bash
docker compose down
```

Para remover também o volume de dados local do PostgreSQL, use `docker compose down -v`. **Isso apaga os dados persistidos nesse volume.**

## Endpoints

### Criar um link curto

**Endpoint:** `POST /api/v1/short-links`

Requisição:

```json
{
  "originalUrl": "https://www.google.com"
}
```

Resposta ilustrativa — `201 Created`:

```json
{
  "id": 1,
  "originalUrl": "https://www.google.com",
  "shortCode": "aWHPiM4",
  "shortUrl": "http://localhost:8080/aWHPiM4",
  "createdAt": "2026-10-26T19:20:00Z"
}
```

O identificador, o código curto e a data são valores ilustrativos; serão definidos durante a execução.

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

Para um código existente, a API retorna `302 Found` e o cabeçalho `Location` com a URL original.

### Códigos de resposta

| Status | Descrição |
|---|---|
| `201 Created` | Link curto criado |
| `302 Found` | Redirecionamento para a URL original |
| `400 Bad Request` | Requisição inválida |
| `404 Not Found` | Código curto inexistente |

## Banco de dados

O PostgreSQL armazena os links na tabela `short_links`. As migrações de esquema são gerenciadas pelo Flyway e ficam em:

`src/main/resources/db/migration/`

## CI e publicação da imagem Docker

O workflow em `.github/workflows/ci.yml` é acionado em pushes e pull requests direcionados à branch `main`.

- O job `test` executa os testes automatizados com Java 21 e um serviço PostgreSQL.
- O job `publish` depende do sucesso dos testes e publica a imagem no GitHub Container Registry (GHCR) somente em pushes para `main`.
- A imagem é publicada com as tags `latest` e o SHA do commit.

A publicação da imagem não configura, por si só, o deploy da aplicação em um servidor de produção.

## Próximas melhorias

- Configuração da URL-base por ambiente.
- Documentação OpenAPI/Swagger.
- Métricas de utilização dos links.
- Estratégias adicionais para colisões de códigos curtos.
- Implantação automatizada em um ambiente de hospedagem.

## Objetivo

Projeto de portfólio para praticar desenvolvimento backend com Java, Spring Boot, APIs REST, persistência relacional, validação, migrações de banco de dados, testes automatizados, Docker e integração contínua.
