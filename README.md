<div align="center">
  <h1>🏋️‍♂️ GymTracker KMP</h1>
  <p><b>Seu parceiro definitivo para treinos e dieta, construído com o poder do Kotlin Multiplatform.</b></p>
  
  [![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-7F52FF.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org)
  [![Compose Multiplatform](https://img.shields.io/badge/Compose-1.7.3-4285F4.svg?style=flat-square&logo=android)](https://www.jetbrains.com/lp/compose-multiplatform/)
  [![SQLDelight](https://img.shields.io/badge/SQLDelight-2.1.0-FF4081.svg?style=flat-square&logo=sqlite)](https://cashapp.github.io/sqldelight/)
  [![Quarkus](https://img.shields.io/badge/Quarkus-3.15.1-4695EB.svg?style=flat-square&logo=quarkus)](https://quarkus.io/)
  [![Railway](https://img.shields.io/badge/Railway-Deploy-1B1D26.svg?style=flat-square&logo=railway)](https://railway.app)
</div>

---

## 📱 Targets Suportados

O **GymTracker** é um aplicativo 100% nativo construído uma única vez e distribuído para múltiplas plataformas:

- 🤖 **Android**
- 🍎 **iOS** 
- 💻 **Desktop** (Windows, macOS, Linux)
- 🌐 **Web** (WasmGC)

---

## 📥 Download

Você pode baixar a versão mais recente do aplicativo diretamente através dos links abaixo:

* **🤖 Android APK (Debug):** [Baixar GymTracker-v1.0-debug.apk]([releases/GymTracker-v1.0-debug.apk](https://github.com/Artanniel/gymtracker-kvm/releases/download/v1.0.0-MVP/GymTracker-v1.0-debug.apk)) *(19MB)*

---

## 🏗️ Arquitetura Geral

```
┌─────────────────────────────────────────────────────────────────┐
│                        MOBILE / DESKTOP / WEB                   │
│                    (Kotlin Multiplatform + Compose)              │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────────┐  │
│  │  Local DB   │  │  Sync Queue  │  │  ConnectivityMonitor   │  │
│  │ (SQLDelight)│  │ (pending_sync│  │  (expect/actual)       │  │
│  └──────┬──────┘  └──────┬───────┘  └───────────┬────────────┘  │
│         │                │                      │               │
│         └────────────────┼──────────────────────┘               │
│                          │                                      │
│                 ┌────────▼────────┐                             │
│                 │  HybridRepo     │                             │
│                 │ (local-first)   │                             │
│                 └────────┬────────┘                             │
│                          │                                      │
│                 ┌────────▼────────┐                             │
│                 │  KtorApiClient  │──── HTTP ──────────┐        │
│                 └─────────────────┘                    │        │
└────────────────────────────────────────────────────────┼────────┘
                                                         │
                              ┌───────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────┐
│                     BACKEND (Quarkus 3.15.1)                    │
├─────────────────────────────────────────────────────────────────┤
│  ┌────────────────┐  ┌────────────────┐  ┌──────────────────┐   │
│  │  Controllers   │  │   Services     │  │   Liquibase      │   │
│  │  (REST API)    │  │  (Business)    │  │  (Migrations)    │   │
│  └───────┬────────┘  └───────┬────────┘  └──────────────────┘   │
│          │                   │                                  │
│  ┌───────▼───────────────────▼────────┐                         │
│  │       Hibernate ORM + Panache      │                         │
│  └────────────────┬───────────────────┘                         │
│                   │                                             │
└───────────────────┼─────────────────────────────────────────────┘
                    │
         ┌──────────▼──────────┐
         │     PostgreSQL 15   │◄──── Schema: gymtracker
         │  (compartilhado     │
         │   com InvoiceBuilder)│
         └─────────────────────┘
                    │
         ┌──────────▼──────────┐
         │   Keycloak 24.0.4   │◄──── Realm: gymtracker
         │  (OIDC + JWT Auth)  │
         └─────────────────────┘
```

### Estrutura do Projeto

```text
GymTrackerKMP/
├── shared/                        # 🧠 KMP Core: Lógica de Negócios e UI
│   └── src/
│       ├── commonMain/            # Código compartilhado (UI, Repos, DB, Sync)
│       ├── androidMain/           # actual: AndroidSqliteDriver, ConnectivityMonitor
│       ├── iosMain/               # actual: NativeSqliteDriver, ConnectivityMonitor
│       ├── jvmMain/               # actual: JdbcSqliteDriver, ConnectivityMonitor
│       └── wasmJsMain/            # actual: WebWorkerDriver, ConnectivityMonitor
│
├── backend/                       # ☕ Backend Quarkus (Java 21)
│   ├── src/main/java/
│   │   ├── controller/            # REST Controllers
│   │   ├── domain/                # JPA Entities
│   │   ├── service/               # Business Logic
│   │   └── dto/                   # Data Transfer Objects
│   ├── src/main/resources/
│   │   ├── application.properties # Config (dev/test/prod)
│   │   └── db/                    # Liquibase Migrations
│   ├── keycloak/                  # Keycloak Realm Config
│   ├── Dockerfile                 # Multi-stage build
│   └── docker-compose.yml         # Local development
│
├── androidApp/                    # 📱 Entrypoint Android
├── desktopApp/                    # 💻 Entrypoint Desktop
├── webApp/                        # 🌐 Entrypoint Web
└── iosApp/                        # 🍎 Projeto Xcode
```

---

## 🔄 Offline-First Sync

O GymTracker funciona **100% offline**. Todos os dados são salvos localmente primeiro, depois sincronizados com o backend quando disponível.

### Fluxo de Sincronização

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   App KMP    │────►│  Local DB    │────►│  Sync Queue  │
│  (Operação)  │     │  (SQLDelight)│     │ (pending_sync│
└──────────────┘     └──────────────┘     └──────┬───────┘
                                                  │
                                        ┌─────────▼─────────┐
                                        │ ConnectivityMonitor │
                                        │   (Online?)        │
                                        └─────────┬─────────┘
                                                  │ SIM
                                        ┌─────────▼─────────┐
                                        │   KtorApiClient    │
                                        │  (HTTP → Backend)  │
                                        └─────────┬─────────┘
                                                  │
                                        ┌─────────▼─────────┐
                                        │   Backend API      │
                                        │  (Quarkus + PG)    │
                                        └───────────────────┘
```

### Configurações de Sync

| Configuração | Valor |
|---|---|
| Retry automático | 3 tentativas |
| Resolução de conflitos | Remote wins (servidor tem prioridade) |
| Sync automático | Ao reconectar |
| UI | Badge no header + tela de status |
| Limpeza | Operações > 7 dias são removidas |

---

## 🛠️ Stack Tecnológica

### Frontend (KMP)

| Camada | Tecnologia |
|---|---|
| **UI** | Compose Multiplatform 1.7.3 |
| **Linguagem** | Kotlin 2.1.20 |
| **Banco de Dados** | SQLDelight 2.1.0 |
| **HTTP Client** | Ktor 3.1.1 (multiplatform) |
| **ViewModel** | `androidx.lifecycle` 2.9.0 |
| **Gestão de Estado** | StateFlow + `collectAsState()` |
| **Serialização** | Kotlinx Serialization 1.8.1 |
| **Gráficos** | Koalaplot |
| **Assincronismo** | Kotlinx Coroutines 1.10.x |

### Backend (Quarkus)

| Camada | Tecnologia |
|---|---|
| **Framework** | Quarkus 3.15.1 |
| **Linguagem** | Java 21 |
| **ORM** | Hibernate ORM + Panache |
| **Banco de Dados** | PostgreSQL 15 |
| **Migrations** | Liquibase |
| **Autenticação** | Keycloak (OIDC + JWT) |
| **Health Check** | SmallRye Health |
| **Métricas** | Micrometer + Prometheus |

### Infraestrutura

| Componente | Tecnologia |
|---|---|
| **Containerização** | Docker (multi-stage build) |
| **Orquestração** | Docker Compose |
| **Deploy** | Railway |
| **Auth Server** | Keycloak 24.0.4 |
| **Banco de Dados** | PostgreSQL 15 (compartilhado com InvoiceBuilder) |

---

## 🚀 Como Buildar e Executar

### Pré-requisitos

| Plataforma | Ferramentas Necessárias |
|---|---|
| **Frontend** | JDK 17+, Kotlin 2.1.20 |
| **Backend** | JDK 21+, Maven 3.9+, Docker |
| **Android** | Android Studio Narwhal 2025.1+ |
| **iOS** | macOS + Xcode 16+ |
| **Desktop** | JDK 17+ |
| **Web** | Node.js 18+ + Chrome 119+/Firefox 120+ |

### Comandos Principais

<details>
<summary><b>🖥️ Backend (Quarkus)</b></summary>

```bash
cd backend

# Modo desenvolvimento (com Docker para PG + Keycloak)
docker-compose up -d
mvn quarkus:dev

# Acessar:
# - API: http://localhost:8082
# - Health: http://localhost:8082/q/health
# - Swagger: http://localhost:8082/q/dev-ui
```
</details>

<details>
<summary><b>🤖 Android</b></summary>

```bash
# Compilar APK
./gradlew :androidApp:assembleDebug

# Instalar
adb install androidApp/build/outputs/apk/debug/androidApp-debug.apk
```
</details>

<details>
<summary><b>💻 Desktop (JVM)</b></summary>

```bash
./gradlew :desktopApp:run

# Empacotamento
./gradlew :desktopApp:createDistributable
./gradlew :desktopApp:packageDeb    # Linux
./gradlew :desktopApp:packageMsi    # Windows
./gradlew :desktopApp:packageDmg    # macOS
```
</details>

<details>
<summary><b>🌐 Web (Wasm)</b></summary>

```bash
# Dev server com Hot Reload
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Build de produção
./gradlew :webApp:wasmJsBrowserDistribution
```
</details>

<details>
<summary><b>🍎 iOS</b></summary>

```bash
./gradlew :shared:assembleXCFramework
# Abrir iosApp/iosApp.xcodeproj no Xcode
```
</details>

---

## 📋 API Endpoints

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/api/workout-sessions` | Listar sessões | JWT |
| `POST` | `/api/workout-sessions` | Criar sessão | JWT |
| `GET` | `/api/workout-sessions/{id}` | Buscar sessão | JWT |
| `PUT` | `/api/workout-sessions/{id}` | Atualizar sessão | JWT |
| `DELETE` | `/api/workout-sessions/{id}` | Remover sessão | JWT |
| `GET` | `/api/workout-sessions/{id}/sets` | Listar sets | JWT |
| `POST` | `/api/workout-sessions/{id}/sets` | Criar set | JWT |
| `POST` | `/api/sync` | Sync batch | JWT |
| `GET` | `/api/sync/pull` | Pull dados | JWT |
| `GET` | `/api/sync/health` | Health check | Não |

---

## 📋 Roadmap

### ✅ Concluído
- [x] KMP (Android, iOS, Desktop, Web)
- [x] SQLDelight (banco nativo por plataforma)
- [x] Splash Screen com animação
- [x] Onboarding (5 páginas + perfil)
- [x] Sistema de streaks e badges
- [x] Notificações in-app
- [x] Gerador de treinos com IA
- [x] Gráficos de progresso (Koalaplot)
- [x] Google Ads estratégicos
- [x] Backend Quarkus (REST API)
- [x] Keycloak (OIDC + JWT)
- [x] Liquibase (migrations)
- [x] Offline-first sync (Ktor + SyncManager)
- [x] ConnectivityMonitor (multiplatform)
- [x] SyncStatusScreen (UI de status)
- [x] Docker Compose (local dev)
- [x] Railway config

### 🔄 Em Progresso
- [ ] Deploy no Railway
- [ ] Integração completa KMP ↔ Backend

### 📌 Próximos
- [ ] CI/CD (GitHub Actions)
- [ ] Testes E2E
- [ ] Push notifications
- [ ] Monetização (AdMob + SKAdNetwork)

---

## 📚 Links Úteis

- [Documentação KMP](https://kotlinlang.org/docs/multiplatform.html)
- [Quarkus Guide](https://quarkus.io/guides/)
- [Keycloak Getting Started](https://www.keycloak.org/getting-started)
- [Railway Deploy](https://docs.railway.app/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Ktor Client](https://ktor.io/docs/client.html)

---

<p align="center">
  <i>Construído com ❤️ e Kotlin</i>
</p>
