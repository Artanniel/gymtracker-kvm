# 🚀 Deploy no Railway - GymTracker

Guia passo a passo para fazer deploy do backend GymTracker no Railway.

---

## Pré-requisitos

- [ ] Conta no [Railway](https://railway.app)
- [ ] Railway CLI instalado (`npm install -g @railway/cli`)
- [ ] Conta no [Keycloak](https://www.keycloak.org/) (já existe no InvoiceBuilder)
- [ ] PostgreSQL acessível (já existe no InvoiceBuilder)

---

## Passo 1: Preparar o Backend

### 1.1 Verificar que o Dockerfile está correto

O Dockerfile multi-stage já está configurado em `backend/Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/quarkus-app ./quarkus-app
EXPOSE 8082
CMD ["java", "-jar", "quarkus-app/quarkus-run.jar"]
```

### 1.2 Verificar o docker-compose.yml

O `backend/docker-compose.yml` compartilha infraestrutura com InvoiceBuilder:

```yaml
services:
  gymtracker-backend:
    build: .
    ports:
      - "8082:8082"
    environment:
      QUARKUS_DATASOURCE_USERNAME: ${PGUSER}
      QUARKUS_DATASOURCE_PASSWORD: ${PGPASSWORD}
      QUARKUS_DATASOURCE_JDBC_URL: jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}?sslmode=disable
      QUARKUS_OIDC_AUTH_SERVER_URL: ${KC_HOSTNAME}/realms/gymtracker
      QUARKUS_OIDC_CLIENT_ID: gymtracker-backend
      QUARKUS_OIDC_CREDENTIALS_SECRET: ${KC_CLIENT_SECRET}
      QUARKUS_HTTP_PORT: 8082
    depends_on:
      - postgres
      - keycloak
```

---

## Passo 2: Configurar o Keycloak

### 2.1 Importar o Realm

O realm `gymtracker` já está exportado em `backend/keycloak/realm-export.json`.

**Se o Keycloak já está rodando (InvoiceBuilder):**

```bash
# Acessar o Keycloak Admin Console
# http://localhost:8081 (ou URL do Keycloak)

# Importar o realm:
# 1. Clicar no botão de domínio (topo esquerdo)
# 2. "Create Realm" → "Import"
# 3. Selecionar o arquivo: backend/keycloak/realm-export.json
# 4. Clicar em "Create"
```

**Se precisar criar manualmente:**

1. Criar realm `gymtracker`
2. Criar client `gymtracker-backend`:
   - Access Type: `bearer-only`
   - Valid Redirect URIs: `http://localhost:3000/*`
3. Criar client `gymtracker-frontend`:
   - Access Type: `public`
   - Valid Redirect URIs: `http://localhost:3000/*`
   - Web Origins: `http://localhost:3000`
4. Criar usuário `alice`:
   - Password: `alice`
   - Role: `user`

### 2.2 Obter o Client Secret

```bash
# No Keycloak Admin Console:
# 1. Clients → gymtracker-backend
# 2. Credentials tab
# 3. Copiar o "Secret"
```

---

## Passo 3: Configurar o Banco de Dados

### 3.1 Criar Schema `gymtracker`

O schema `gymtracker` já está definido em `backend/schema.sql`:

```sql
CREATE SCIF NOT EXISTS gymtracker;
```

**Executar manualmente:**

```bash
# Conectar ao PostgreSQL (mesmo do InvoiceBuilder)
psql -h localhost -p 5432 -U postgres -d invoicepro

# Criar schema
CREATE SCHEMA IF NOT EXISTS gymtracker;

# Conceder permissões (se necessário)
GRANT ALL ON SCHEMA gymtracker TO postgres;
```

### 3.2 Verificar Migrations

As migrations do Liquibase estão em `backend/src/main/resources/db/`:

- `001-create-workout-sessions.xml`
- `002-create-set-logs.xml`
- `003-create-workout-configs.xml`

Elas são executadas automaticamente na inicialização do backend.

---

## Passo 4: Deploy no Railway

### 4.1 Login no Railway

```bash
railway login
```

### 4.2 Criar o Projeto

```bash
# Criar novo projeto
railway init gymtracker-backend

# Ou adicionar ao projeto existente
railway link
```

### 4.3 Configurar as Variáveis de Ambiente

```bash
# Variáveis do PostgreSQL (usar as mesmas do InvoiceBuilder)
railway variables set PGHOST="roundhouse.proxy.rlwy.net"
railway variables set PGPORT="43420"
railway variables set PGUSER="postgres"
railway variables set PGPASSWORD="sua_senha"
railway variables set PGDATABASE="invoicepro"

# Variáveis do Keycloak
railway variables set KC_HOSTNAME="keyclock.railway.internal"
railway variables set KC_CLIENT_SECRET="seu_client_secret"

# Variáveis do Quarkus
railway variables set QUARKUS_PROFILE="prod"
railway variables set QUARKUS_DATASOURCE_USERNAME="${PGUSER}"
railway variables set QUARKUS_DATASOURCE_PASSWORD="${PGPASSWORD}"
railway variables set QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}?sslmode=disable"
railway variables set QUARKUS_OIDC_AUTH_SERVER_URL="${KC_HOSTNAME}/realms/gymtracker"
railway variables set QUARKUS_OIDC_CLIENT_ID="gymtracker-backend"
railway variables set QUARKUS_OIDC_CREDENTIALS_SECRET="${KC_CLIENT_SECRET}"
```

### 4.4 Fazer Deploy

```bash
# Deploy do backend
railway up --service gymtracker-backend

# Ou deploy automático (se GitHub conectado)
# O Railway fará deploy a cada push na branch main
```

### 4.5 Verificar o Deploy

```bash
# Ver logs
railway logs --service gymtracker-backend

# Verificar health check
curl https://gymtracker-backend.up.railway.app/api/sync/health
```

---

## Passo 5: Configurar o Frontend (KMP)

### 5.1 Atualizar a URL do Backend

Em `shared/src/commonMain/kotlin/com/gymtracker/data/sync/KtorApiClient.kt`:

```kotlin
// Desenvolvimento (localhost)
companion object {
    private const val BASE_URL = "http://localhost:8082"
}

// Produção (Railway) - usar variável de ambiente ou URL fixa
// private const val BASE_URL = "https://gymtracker-backend.up.railway.app"
```

### 5.2 Configurar CORS no Backend

Em `backend/src/main/resources/application.properties`:

```properties
# CORS (produção)
quarkus.http.cors=true
quarkus.http.cors.origins=*
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
quarkus.http.cors.headers=*
quarkus.http.cors.exposed-headers=*
quarkus.http.cors.allow-credentials=true
```

---

## Passo 6: Verificar o Deploy

### 6.1 Health Check

```bash
curl https://gymtracker-backend.up.railway.app/api/sync/health

# Resposta esperada:
{
  "status": "UP",
  "database": "UP",
  "keycloak": "UP"
}
```

### 6.2 Testar Autenticação

```bash
# Obter token JWT
TOKEN=$(curl -s -X POST "http://localhost:8081/realms/gymtracker/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=gymtracker-frontend" \
  -d "username=alice" \
  -d "password=alice" | jq -r '.access_token')

# Testar endpoint protegido
curl -H "Authorization: Bearer $TOKEN" \
  https://gymtracker-backend.up.railway.app/api/workout-sessions
```

### 6.3 Testar Sync

```bash
# Enviar dados de sync
curl -X POST https://gymtracker-backend.up.railway.app/api/sync \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sessions": [],
    "setLogs": [],
    "configs": []
  }'
```

---

## Troubleshooting

### Problema: Backend não conecta ao PostgreSQL

**Causa:** URL de conexão incorreta ou schema não existe.

**Solução:**
```bash
# Verificar se o schema existe
psql -h localhost -p 5432 -U postgres -d invoicepro -c "\dn"

# Criar schema se necessário
psql -h localhost -p 5432 -U postgres -d invoicepro -c "CREATE SCHEMA IF NOT EXISTS gymtracker;"
```

### Problema: Autenticação OIDC falha

**Causa:** Client secret incorreto ou realm não existe.

**Solução:**
```bash
# Verificar se o realm existe
curl http://localhost:8081/realms/gymtracker/.well-known/openid-configuration

# Verificar client secret no Keycloak Admin Console
```

### Problema: CORS bloqueado

**Causa:** Frontend não pode acessar backend.

**Solução:** Verificar configuração CORS em `application.properties`.

### Problema: Deploy falha no Railway

**Causa:** Variáveis de ambiente não configuradas.

**Solução:**
```bash
# Listar variáveis configuradas
railway variables

# Verificar logs do deploy
railway logs --service gymtracker-backend
```

---

## Variáveis de Ambiente Referência

| Variável | Descrição | Exemplo |
|---|---|---|
| `PGHOST` | Host do PostgreSQL | `roundhouse.proxy.rlwy.net` |
| `PGPORT` | Porta do PostgreSQL | `43420` |
| `PGUSER` | Usuário do PostgreSQL | `postgres` |
| `PGPASSWORD` | Senha do PostgreSQL | `sua_senha` |
| `PGDATABASE` | Nome do banco | `invoicepro` |
| `KC_HOSTNAME` | URL do Keycloak | `keyclock.railway.internal` |
| `KC_CLIENT_SECRET` | Client secret do backend | `abc123...` |
| `QUARKUS_PROFILE` | Perfil do Quarkus | `prod` |

---

## Deploy Completo

```
✅ Passo 1: Backend preparado (Dockerfile, docker-compose)
✅ Passo 2: Keycloak configurado (realm, clients, users)
✅ Passo 3: PostgreSQL configurado (schema gymtracker)
✅ Passo 4: Railway deploy (variáveis, deploy)
✅ Passo 5: Frontend configurado (URL do backend)
✅ Passo 6: Verificação (health check, auth, sync)
```

---

<p align="center">
  <i>Deploy concluído! 🎉</i>
</p>
