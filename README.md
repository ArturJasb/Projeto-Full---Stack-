# 🍔 Comanda Digital — Backend API

Sistema de Pedidos para Dark Kitchen | UNASP SP | Prof. Thiago Silva | 2026/1

## Stack
- **Java 17** + **Spring Boot 3.2**
- **Spring Security** + **JWT** (jjwt 0.11.5)
- **Spring Data JPA** + **Hibernate** + **MySQL**
- **Flyway** para migrations versionadas
- **SpringDoc OpenAPI** (Swagger UI)
- **Maven**

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java (JDK) | 17 |
| Maven | 3.8+ |
| MySQL | 8.0+ |

---

## Como rodar

### 1. Banco de dados

```sql
-- Conecte no MySQL e execute:
CREATE DATABASE comanda_digital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> O Flyway cria todas as tabelas e insere os dados de seed automaticamente na primeira execução.

### 2. Configuração

Edite `src/main/resources/application.properties` se necessário:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/comanda_digital?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
spring.datasource.username=root
spring.datasource.password=root   # ← altere se sua senha for diferente
```

### 3. Build e execução

```bash
# Na raiz do projeto (onde está o pom.xml):
./mvnw spring-boot:run

# Ou com Maven instalado:
mvn spring-boot:run
```

O servidor sobe em **http://localhost:8080**

---

## Usuários seed (todos com senha `senha123`)

| Email | Perfil |
|---|---|
| admin@email.com | ADMIN |
| gerente@email.com | GERENTE |
| cozinheiro@email.com | COZINHEIRO |
| cliente@email.com | CLIENTE |

---

## Swagger UI

Acesse: **http://localhost:8080/swagger-ui.html**

Para autenticar no Swagger:
1. `POST /api/auth/login` com email + senha
2. Copie o `token` da resposta
3. Clique em **Authorize** (cadeado) e cole: `Bearer {token}`

---

## Endpoints principais

### Públicos (sem token)
```
POST   /api/auth/login          Login → retorna JWT
POST   /api/auth/register       Cadastro de cliente
GET    /api/cardapio             Lista pratos ativos
GET    /api/cardapio/{id}        Detalhe do prato
GET    /api/cardapio/categorias  Lista categorias
```

### Cliente (ROLE_CLIENTE)
```
POST   /api/pedidos              Criar pedido (checkout)
GET    /api/pedidos/meus         Histórico de pedidos
GET    /api/pedidos/{id}/status  Acompanhar status
```

### Admin/Gerente
```
# Cardápio
GET/POST/PUT/DELETE  /api/admin/categorias
GET/POST/PUT/DELETE  /api/admin/pratos
POST/GET             /api/admin/pratos/{id}/ficha    Ficha técnica
GET                  /api/admin/pratos/{id}/custo    Custo + food cost

# Pedidos
GET    /api/admin/pedidos                    Lista com filtros
PATCH  /api/admin/pedidos/{id}/status        Muda status
PATCH  /api/admin/pedidos/{id}/cancelar      Cancela com motivo

# Estoque
GET/POST/PUT/DELETE  /api/admin/ingredientes
GET    /api/admin/ingredientes/estoque/saldo    Saldo atual
GET    /api/admin/ingredientes/estoque/alertas  Abaixo do mínimo
POST   /api/admin/ingredientes/estoque/movimentacao  Saída manual

# Fornecedores
GET/POST/PUT/DELETE  /api/admin/fornecedores
GET/POST/PUT         /api/admin/fornecedores/{id}/produtos
GET                  /api/admin/fornecedores/cotacao/{ingredienteId}

# Compras
GET/POST/PUT         /api/admin/compras
PATCH  /api/admin/compras/{id}/status
POST   /api/admin/compras/{id}/receber    Recebe → entra no estoque

# Dashboard
GET    /api/admin/dashboard/resumo        KPIs do dia
GET    /api/admin/dashboard/top-pratos    Top 5 mais vendidos

# Usuários (ADMIN only)
GET/POST/PUT/DELETE  /api/admin/usuarios
```

---

## Regras de Negócio implementadas

| ID | Regra |
|---|---|
| RN01 | Prato só fica ATIVO se tiver ficha técnica com ≥ 1 ingrediente |
| RN02 | Food cost > 35% retorna aviso na resposta |
| RN03 | Estoque insuficiente bloqueia criação do pedido (422) |
| RN04 | Cancelamento após EM_PREPARO só por ADMIN/GERENTE |
| RN05 | Recebimento de compra atualiza custo unitário do ingrediente |
| RN06 | Soft delete: registros nunca são deletados fisicamente |
| RN07 | CNPJ validado pelo algoritmo da Receita Federal |
| RN08 | Fator de correção na ficha técnica ≥ 1.0 |
| RN09 | Cardápio público só exibe pratos ATIVOS |
| RN10 | Email único — retorna 409 se já existir |

---

## Fórmulas implementadas

```
custo_prato = SUM(quantidade × fator_correcao × custo_unitario) / rendimento
food_cost%  = (custo_prato / preco_venda) × 100

Classificação:
  ≤ 30%  → VERDE   ✅
  31-35% → AMARELO ⚠️
  > 35%  → VERMELHO 🔴
```

---

## Estrutura do projeto

```
src/main/java/com/unasp/comandadigital/
├── ComandaDigitalApplication.java
├── config/
│   ├── SecurityConfig.java       # Spring Security + JWT
│   ├── CorsConfig.java           # CORS para localhost:4200
│   ├── OpenApiConfig.java        # Swagger com auth
│   └── CnpjValidator.java        # Algoritmo RFB
├── controller/
│   ├── AuthController.java       # POST /api/auth/**
│   ├── CardapioController.java   # GET /api/cardapio (público)
│   ├── PedidoController.java     # /api/pedidos (CLIENTE)
│   └── admin/                   # /api/admin/** (ADMIN/GERENTE/COZ)
├── service/                     # Toda lógica de negócio
├── repository/                  # Interfaces JPA
├── entity/                      # @Entity + enums
├── dto/                         # Records de request/response
├── security/                    # JWT filter + UserDetails
└── exception/                   # @ControllerAdvice global
```

---

## Migrations Flyway

```
src/main/resources/db/migration/
├── V1__create_tables.sql   # DDL completo
└── V2__seed_data.sql       # Admin + 5 pratos com fichas técnicas
```

---

## Observações de segurança

- Senhas armazenadas com **BCrypt** (fator 10)
- JWT com expiração de **8 horas**
- Endpoints protegidos com `@PreAuthorize`
- CORS configurado para `localhost:4200`
- Nenhuma `@Entity` exposta diretamente (sempre DTOs)
- Tratamento global de erros sem stack trace para o cliente
