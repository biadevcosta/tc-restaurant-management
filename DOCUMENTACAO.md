# TC Restaurant Management — Documentação Técnica

## Sumário

1. [Arquitetura da Aplicação](#1-arquitetura-da-aplicação)
2. [Modelagem das Entidades e Relacionamentos](#2-modelagem-das-entidades-e-relacionamentos)
3. [Endpoints Disponíveis](#3-endpoints-disponíveis)
4. [Documentação Swagger](#4-documentação-swagger)
5. [Coleção Postman](#5-coleção-postman)
6. [Estrutura do Banco de Dados](#6-estrutura-do-banco-de-dados)
7. [Executando com Docker Compose](#7-executando-com-docker-compose)

---

## 1. Arquitetura da Aplicação

### Stack tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework principal | Spring Boot 4.0.5 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | MySQL 8 |
| Segurança | Spring Security + JWT (JJWT 0.12.6) |
| Documentação da API | SpringDoc OpenAPI 2.8.6 (Swagger UI) |
| Containerização | Docker + Docker Compose |
| Build | Maven |

### Estrutura de camadas

```
src/main/java/com/fiap/tc/restaurant/
│
├── config/             # Configurações globais (Security, Swagger)
├── controller/         # Controllers REST (endpoints)
│   └── v1/             # Controllers versionados
├── domain/             # Entidades JPA (BaseUser, Customer, RestaurantOwner, Address)
├── dto/
│   ├── request/        # Objetos de entrada (CreateCustomerRequest, LoginRequest, …)
│   └── response/       # Objetos de saída (UserResponse, LoginResponse, …)
├── enums/              # Enumerações (UserRole)
├── exception/          # Exceções customizadas e GlobalExceptionHandler
├── filter/             # Filtro JWT (JwtAuthFilter)
├── mapper/             # Mapeadores entidade ↔ DTO
├── repository/         # Interfaces Spring Data JPA
├── security/           # Entry point e access-denied handler customizados
└── service/            # Interfaces e implementações de negócio
    └── impl/
```

### Fluxo de autenticação (JWT)

```
Cliente
  │
  ├─► POST /api/v1/auth/login  ──► AuthService
  │                                    │
  │                              AuthenticationManager
  │                                    │
  │                           UserDetailsServiceImpl
  │                            (busca em Customer ou
  │                             RestaurantOwner)
  │                                    │
  │                               JwtService
  │                         (gera token HMAC-SHA,
  │                           expira em 24h)
  │
  │◄── { token, userId, userType }
  │
  │  (próximas requisições)
  │
  ├─► GET /api/v1/customers/1
  │   Authorization: Bearer <token>
  │              │
  │         JwtAuthFilter
  │    (valida token, seta SecurityContext)
  │              │
  │         Controller → Service → Repository
  │              │
  │◄── 200 OK + corpo JSON
```

### Fluxo de autorização (403 Forbidden)

Os endpoints de atualização (`PUT /{id}`, `PATCH /{id}/password`, `DELETE /{id}`) validam se o usuário autenticado é o mesmo que está sendo alterado. Caso contrário, a exceção `UnauthorizedOperationException` é lançada e retorna **403 Forbidden**.

---

## 2. Modelagem das Entidades e Relacionamentos

### Diagrama de entidades

> 📷 *[Inserir diagrama ER aqui]*

### BaseUser (entidade-pai abstrata)

Superclasse com herança do tipo **JOINED** — cada subclasse tem sua própria tabela ligada por chave estrangeira.

Implementa a interface `UserDetails` do Spring Security para integração nativa com o mecanismo de autenticação.

| Campo | Tipo | Restrições |
|---|---|---|
| `id` | `Long` | PK, auto-increment |
| `name` | `String` | NOT NULL |
| `email` | `String` | NOT NULL, UNIQUE |
| `login` | `String` | NOT NULL, UNIQUE |
| `password` | `String` | NOT NULL (BCrypt) |
| `role` | `UserRole` (enum) | NOT NULL |
| `lastModifiedAt` | `LocalDateTime` | Atualizado em create/update |
| `address` | `Address` | Embedded (ver abaixo) |

### Address (objeto de valor embutido)

Armazenado diretamente nas tabelas das subclasses via `@Embeddable`.

| Campo | Tipo |
|---|---|
| `street` | `String` |
| `number` | `String` |
| `city` | `String` |
| `state` | `String` |
| `zipCode` | `String` |

### Customer

Herda de `BaseUser`. Tabela própria: `customers`.

- `role` sempre = `ROLE_CUSTOMER`
- Chave estrangeira referenciando `base_users.id`

### RestaurantOwner

Herda de `BaseUser`. Tabela própria: `restaurant_owners`.

- `role` sempre = `ROLE_RESTAURANT_OWNER`
- Chave estrangeira referenciando `base_users.id`

### Enum UserRole

```
ROLE_CUSTOMER
ROLE_RESTAURANT_OWNER
```

### Relacionamentos

```
BaseUser (base_users)
    │
    ├── Customer (customers)          FK: customers.id → base_users.id
    └── RestaurantOwner               FK: restaurant_owners.id → base_users.id
          (restaurant_owners)
```

Não há relacionamentos entre `Customer` e `RestaurantOwner` — as entidades são independentes e compartilham apenas a estrutura herdada de `BaseUser`.

---

## 3. Endpoints Disponíveis

### Convenção de respostas de erro

Todos os erros seguem o formato **RFC 7807 Problem Detail**:

```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Mensagem descritiva do erro",
  "instance": "/api/v1/customers"
}
```

Para erros de validação, há um campo adicional:

```json
{
  "status": 400,
  "title": "Validation Error",
  "detail": "Campos inválidos",
  "instance": "/api/v1/customers",
  "errors": {
    "email": "deve ser um endereço de e-mail válido",
    "password": "tamanho mínimo é 8"
  }
}
```

---

### Auth

#### `POST /api/v1/auth/login`

Autentica um usuário (Customer ou RestaurantOwner) e retorna o JWT.

**Acesso:** público

**Request body:**
```json
{
  "login": "joaosilva",
  "password": "senha123"
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "userType": "RESTAURANT_OWNER"
}
```

**Erros:**

| Status | Situação |
|---|---|
| 400 | `login` ou `password` ausentes/vazios |
| 401 | Credenciais inválidas |

---

### Customers

Base: `/api/v1/customers`

#### `POST /api/v1/customers`

Cria um novo cliente.

**Acesso:** público

**Request body:**
```json
{
  "name": "Maria Souza",
  "email": "maria@email.com",
  "login": "mariasouza",
  "password": "senha123",
  "address": {
    "street": "Av. Paulista",
    "number": "1000",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01310-200"
  }
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "name": "Maria Souza",
  "email": "maria@email.com",
  "login": "mariasouza",
  "role": "ROLE_CUSTOMER",
  "lastModifiedAt": "2026-05-03T12:00:00",
  "address": {
    "street": "Av. Paulista",
    "number": "1000",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01310-200"
  }
}
```

**Erros:**

| Status | Situação |
|---|---|
| 400 | Campos obrigatórios ausentes, e-mail inválido ou senha muito curta (< 8 caracteres) |
| 409 | E-mail ou login já cadastrado |

---

#### `GET /api/v1/customers/{id}`

Busca um cliente pelo ID.

**Acesso:** protegido — `Authorization: Bearer <token>`

**Response 200 OK:** mesmo formato do UserResponse acima

**Erros:**

| Status | Situação |
|---|---|
| 401 | Token ausente ou inválido |
| 404 | Cliente não encontrado |

---

#### `GET /api/v1/customers?name={query}`

Busca clientes pelo nome (case-insensitive, busca parcial).

**Acesso:** protegido — `Authorization: Bearer <token>`

**Exemplo:** `GET /api/v1/customers?name=maria`

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "name": "Maria Souza",
    ...
  }
]
```

**Erros:**

| Status | Situação |
|---|---|
| 401 | Token ausente ou inválido |

---

#### `PUT /api/v1/customers/{id}`

Atualiza nome, e-mail e endereço de um cliente. Apenas o próprio cliente pode executar esta operação.

**Acesso:** protegido — `Authorization: Bearer <token>`

**Request body:**
```json
{
  "name": "Maria Souza Atualizada",
  "email": "maria.nova@email.com",
  "address": {
    "street": "Av. Brigadeiro Faria Lima",
    "number": "2000",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01310-400"
  }
}
```

**Response 200 OK:** UserResponse atualizado

**Erros:**

| Status | Situação |
|---|---|
| 400 | Campos obrigatórios ausentes ou e-mail inválido |
| 401 | Token ausente ou inválido |
| 403 | Tentativa de alterar dados de outro usuário |
| 404 | Cliente não encontrado |
| 409 | Novo e-mail já em uso |

---

#### `PATCH /api/v1/customers/{id}/password`

Altera a senha de um cliente. Apenas o próprio cliente pode executar esta operação.

**Acesso:** protegido — `Authorization: Bearer <token>`

**Request body:**
```json
{
  "currentPassword": "senha123",
  "newPassword": "novaSenha456"
}
```

**Response 200 OK** (sem corpo)

**Erros:**

| Status | Situação |
|---|---|
| 400 | Senha atual incorreta ou nova senha muito curta (< 8 caracteres) |
| 401 | Token ausente ou inválido |
| 403 | Tentativa de alterar senha de outro usuário |
| 404 | Cliente não encontrado |

---

#### `DELETE /api/v1/customers/{id}`

Remove um cliente.

**Acesso:** protegido — `Authorization: Bearer <token>`

**Response 204 No Content**

**Erros:**

| Status | Situação |
|---|---|
| 401 | Token ausente ou inválido |
| 404 | Cliente não encontrado |

---

### Restaurant Owners

Base: `/restaurant-owners`

Os endpoints espelham exatamente a estrutura do módulo de Customers. O corpo de criação usa `RestaurantOwnerRequest` (mesmos campos que `CreateCustomerRequest`).

#### Resumo dos endpoints

| Método | Path | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/restaurant-owners` | Público | Cria dono de restaurante |
| `GET` | `/restaurant-owners/{id}` | Protegido | Busca por ID |
| `GET` | `/restaurant-owners?name=` | Protegido | Busca por nome |
| `PUT` | `/restaurant-owners/{id}` | Protegido | Atualiza dados |
| `PATCH` | `/restaurant-owners/{id}/password` | Protegido | Altera senha |
| `DELETE` | `/restaurant-owners/{id}` | Protegido | Remove |

**Exemplo de criação:**
```json
{
  "name": "João Silva",
  "email": "joao@restaurante.com",
  "login": "joaosilva",
  "password": "senha123",
  "address": {
    "street": "Rua das Flores",
    "number": "123",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01310-100"
  }
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@restaurante.com",
  "login": "joaosilva",
  "role": "ROLE_RESTAURANT_OWNER",
  "lastModifiedAt": "2026-05-03T12:00:00",
  "address": {
    "street": "Rua das Flores",
    "number": "123",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01310-100"
  }
}
```

Os erros e regras de autorização são idênticos aos de Customers.

---

## 4. Documentação Swagger

### Acesso

Com a aplicação em execução, acesse:

```
http://localhost:8080/swagger-ui.html
```

A especificação OpenAPI em JSON está disponível em:

```
http://localhost:8080/v3/api-docs
```

Esses dois caminhos são públicos e não exigem token.

### Autenticação no Swagger UI

Para testar endpoints protegidos diretamente pelo Swagger:

1. Execute o endpoint `POST /api/v1/auth/login` e copie o valor do campo `token` da resposta.
2. Clique no botão **Authorize** (ícone de cadeado) no topo da página.
3. No campo **Value**, insira: `Bearer <token>` (inclua o prefixo `Bearer `).
4. Clique em **Authorize** e feche o modal.
5. Todos os endpoints protegidos passarão a enviar o cabeçalho automaticamente.

> 📷 *[Inserir print da tela inicial do Swagger UI]*

> 📷 *[Inserir print do modal de autenticação (Authorize)]*

> 📷 *[Inserir print de um endpoint expandido com exemplo de request/response]*

### Grupos de endpoints

O Swagger organiza os endpoints em três grupos (tags):

| Tag | Endpoints |
|---|---|
| **Auth** | `POST /api/v1/auth/login` |
| **Customers** | Todos os endpoints `/api/v1/customers/**` |
| **Restaurant Owners** | Todos os endpoints `/restaurant-owners/**` |

Endpoints protegidos exibem o ícone de cadeado indicando que exigem Bearer Token.

---

## 5. Coleção Postman

### Arquivos

| Arquivo | Descrição |
|---|---|
| `postman/TC Restaurant Management.postman_collection.json` | Collection com todos os cenários de teste |
| `postman/TC Restaurant Management.postman_environment.json` | Environment com variáveis `baseUrl` e `token` |

### Como importar

1. Abra o Postman.
2. Clique em **Import** (canto superior esquerdo).
3. Arraste os dois arquivos `.json` ou navegue até a pasta `postman/` e selecione ambos.
4. Selecione o environment **TC Restaurant - Local** no seletor de environments (canto superior direito).

> 📷 *[Inserir print da tela de importação do Postman]*

> 📷 *[Inserir print da collection importada com a estrutura de pastas]*

### Variáveis de ambiente

| Variável | Valor padrão | Descrição |
|---|---|---|
| `baseUrl` | `http://localhost:8080` | URL base da API |
| `token` | *(vazio)* | JWT preenchido automaticamente após o login |

### Captura automática do token

O request `Auth > Success > Login` possui um test script que, após um login bem-sucedido (HTTP 200), salva o token retornado automaticamente na variável de ambiente `token`:

```javascript
const json = pm.response.json();
if (pm.response.code === 200 && json.token) {
    pm.environment.set('token', json.token);
}
```

Após executar o Login, todos os requests protegidos passam a usar `Bearer {{token}}` no cabeçalho `Authorization` sem configuração manual.

> 📷 *[Inserir print do script de test no request de Login]*

### Estrutura da collection

```
TC Restaurant Management
│
├── Auth
│   ├── Success
│   │   └── Login                            ← salva token automaticamente
│   └── Errors
│       ├── [401] Invalid Credentials
│       ├── [400] Missing Fields
│       └── [401] No Token - Protected Route
│
├── Restaurant Owners
│   ├── Success
│   │   ├── Create Restaurant Owner
│   │   ├── Get Restaurant Owner by ID
│   │   ├── Search Restaurant Owner by Name
│   │   ├── Update Restaurant Owner
│   │   ├── Change Restaurant Owner Password
│   │   └── Delete Restaurant Owner
│   └── Errors
│       ├── [409] Duplicate Email
│       ├── [409] Duplicate Login
│       ├── [400] Missing Required Fields
│       ├── [400] Invalid Email Format
│       ├── [400] Password Too Short
│       ├── [400] Missing Address Fields
│       ├── [404] Get by Non-existent ID
│       ├── [404] Delete Non-existent ID
│       ├── [409] Update - Duplicate Email
│       ├── [404] Update Non-existent ID
│       ├── [400] Update - Missing Required Fields
│       ├── [403] Update - Another User's Data
│       ├── [403] Change Password - Another User's Password
│       ├── [400] Change Password - Wrong Current Password
│       ├── [400] Change Password - New Password Too Short
│       ├── [404] Change Password - Non-existent ID
│       ├── [401] Update - Without Token
│       ├── [401] Delete - Without Token
│       └── [401] Change Password - Without Token
│
└── Customers
    ├── Success
    │   ├── Create Customer
    │   ├── Get Customer by ID
    │   ├── Search Customer by Name
    │   ├── Update Customer
    │   ├── Change Customer Password
    │   └── Delete Customer
    └── Errors
        ├── [409] Duplicate Email
        ├── [409] Duplicate Login
        ├── [400] Missing Required Fields
        ├── [400] Invalid Email Format
        ├── [400] Password Too Short
        ├── [400] Missing Address Fields
        ├── [404] Get by Non-existent ID
        ├── [404] Delete Non-existent ID
        ├── [409] Update - Duplicate Email
        ├── [404] Update Non-existent ID
        ├── [400] Update - Missing Required Fields
        ├── [403] Update - Another User's Data
        ├── [403] Change Password - Another User's Password
        ├── [400] Change Password - Wrong Current Password
        ├── [400] Change Password - New Password Too Short
        ├── [404] Change Password - Non-existent ID
        ├── [401] Update - Without Token
        ├── [401] Delete - Without Token
        └── [401] Change Password - Without Token
```

> 📷 *[Inserir print de um request de sucesso com resposta 200/201]*

> 📷 *[Inserir print de um request de erro (ex: 403 ou 409)]*

---

## 6. Estrutura do Banco de Dados

O schema é gerado e mantido automaticamente pelo Hibernate (`ddl-auto: update`). As tabelas abaixo refletem a hierarquia de herança **JOINED** do JPA.

### Tabela `base_users`

Tabela-pai da hierarquia. Contém os campos comuns a todos os usuários.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id` | `BIGINT` | PK, AUTO_INCREMENT | Identificador único |
| `name` | `VARCHAR(255)` | NOT NULL | Nome completo |
| `email` | `VARCHAR(255)` | NOT NULL, UNIQUE | E-mail |
| `login` | `VARCHAR(255)` | NOT NULL, UNIQUE | Login de acesso |
| `password` | `VARCHAR(255)` | NOT NULL | Senha codificada em BCrypt |
| `role` | `VARCHAR(50)` | NOT NULL | `ROLE_CUSTOMER` ou `ROLE_RESTAURANT_OWNER` |
| `last_modified_at` | `DATETIME` | | Data/hora da última modificação |
| `street` | `VARCHAR(255)` | | Rua (address embutido) |
| `number` | `VARCHAR(50)` | | Número (address embutido) |
| `city` | `VARCHAR(255)` | | Cidade (address embutido) |
| `state` | `VARCHAR(50)` | | Estado (address embutido) |
| `zip_code` | `VARCHAR(20)` | | CEP (address embutido) |

### Tabela `customers`

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id` | `BIGINT` | PK, FK → `base_users.id` | Herda o ID da tabela-pai |

### Tabela `restaurant_owners`

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id` | `BIGINT` | PK, FK → `base_users.id` | Herda o ID da tabela-pai |

### Relacionamentos e diagrama

```
base_users (id PK)
    │
    ├──── customers (id PK, FK → base_users.id)
    │
    └──── restaurant_owners (id PK, FK → base_users.id)
```

> 📷 *[Inserir print do schema no MySQL Workbench ou similar]*

### Exemplo de registro

Ao criar um Customer, são inseridas **duas linhas**: uma em `base_users` com todos os dados e uma em `customers` com apenas o `id` referenciando `base_users`.

```sql
-- base_users
INSERT INTO base_users (id, name, email, login, password, role, last_modified_at, street, number, city, state, zip_code)
VALUES (1, 'Maria Souza', 'maria@email.com', 'mariasouza', '$2a$10$...', 'ROLE_CUSTOMER', NOW(), 'Av. Paulista', '1000', 'São Paulo', 'SP', '01310-200');

-- customers
INSERT INTO customers (id) VALUES (1);
```

---

## 7. Executando com Docker Compose

### Pré-requisitos

- Docker Desktop instalado e em execução
- Docker Compose v2+
- Maven 3.9+ (para gerar o `.jar`)

### Passo 1 — Gerar o JAR

```bash
./mvnw clean package -DskipTests
```

O arquivo `target/restaurant-0.0.1-SNAPSHOT.jar` será gerado.

### Passo 2 — Configurar as variáveis de ambiente

O `docker-compose.yml` precisa das seguintes variáveis. Crie um arquivo `.env` na raiz do projeto:

```env
# Banco de dados
DB_USER=root
DB_PASS=root

# JWT — chave Base64 com no mínimo 256 bits (32 caracteres)
JWT_SECRET=dGhpcy1pcy1hLXZlcnktc2VjcmV0LWtleS0xMjM0NTY=
```

> **Atenção:** nunca versione o arquivo `.env`. Adicione-o ao `.gitignore`.

### Passo 3 — Atualizar o docker-compose.yml

O arquivo atual precisa das variáveis de JWT e banco passadas corretamente para o container da API. Versão corrigida:

```yaml
version: "3.8"

services:

  db:
    image: mysql:8
    container_name: mysql_db
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASS}
      MYSQL_DATABASE: restaurant_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  api:
    build: .
    container_name: spring_api
    ports:
      - "8080:8080"
    depends_on:
      db:
        condition: service_healthy
    environment:
      DB_USER: ${DB_USER}
      DB_PASS: ${DB_PASS}
      JWT_SECRET: ${JWT_SECRET}

volumes:
  mysql_data:
```

> O `application.yml` já usa `jdbc:mysql://db:3306/restaurant_db` como URL, portanto o serviço do banco deve se chamar `db`.

### Passo 4 — Subir os containers

```bash
docker compose up --build
```

Para rodar em background:

```bash
docker compose up --build -d
```

### Passo 5 — Verificar os logs

```bash
docker compose logs -f api
```

A aplicação está pronta quando aparecer:

```
Started RestaurantApplication in X.XXX seconds
```

### Passo 6 — Testar

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"joaosilva","password":"senha123"}'
```

### Passo 7 — Encerrar

```bash
docker compose down
```

Para remover também os volumes (apaga os dados do banco):

```bash
docker compose down -v
```

### Resumo das portas

| Serviço | Porta |
|---|---|
| API Spring Boot | `8080` |
| MySQL | `3306` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |

### Variáveis de ambiente — referência completa

| Variável | Obrigatório | Descrição | Exemplo |
|---|---|---|---|
| `DB_USER` | Sim | Usuário do MySQL | `root` |
| `DB_PASS` | Sim | Senha do MySQL | `root` |
| `JWT_SECRET` | Sim | Chave HMAC-SHA em Base64 (mín. 256 bits) | `dGhpcy1pcy1hLXZ...` |

> Para gerar uma chave segura: `openssl rand -base64 32`

### Estrutura dos containers

```
┌─────────────────────────────────────────────┐
│               Docker Network                │
│                                             │
│  ┌──────────────┐      ┌─────────────────┐  │
│  │  spring_api  │─────►│   mysql_db      │  │
│  │  :8080       │      │   :3306         │  │
│  └──────────────┘      └─────────────────┘  │
│         ▲                      │            │
└─────────│──────────────────────│────────────┘
          │                      │
       Cliente                Volume
    (Postman/Browser)       mysql_data
```

> 📷 *[Inserir print do Docker Desktop com os containers em execução]*
