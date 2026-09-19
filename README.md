<div align="center">
  <h1>🏋️‍♂️ GymTracker KMP</h1>
  <p><b>Seu parceiro definitivo para treinos, dieta e gestão de alunos — em todas as plataformas.</b></p>

  [![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-7F52FF.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org)
  [![Compose Multiplatform](https://img.shields.io/badge/Compose-1.7.3-4285F4.svg?style=flat-square&logo=android)](https://www.jetbrains.com/lp/compose-multiplatform/)
  [![SQLDelight](https://img.shields.io/badge/SQLDelight-2.1.0-FF4081.svg?style=flat-square&logo=sqlite)](https://cashapp.github.io/sqldelight/)
  [![Ktor](https://img.shields.io/badge/Ktor-3.1.1-087CFA.svg?style=flat-square&logo=ktor)](https://ktor.io/)
  [![Quarkus](https://img.shields.io/badge/Quarkus-3.15.1-4695EB.svg?style=flat-square&logo=quarkus)](https://quarkus.io/)
  [![Railway](https://img.shields.io/badge/Railway-Deploy-1B1D26.svg?style=flat-square&logo=railway)](https://railway.app)
  [![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF.svg?style=flat-square&logo=github-actions)](.github/workflows)
</div>

---

## 📱 Targets Suportados

O **GymTracker** é um aplicativo **Kotlin Multiplatform** que roda nativamente em:

- 🤖 **Android**
- 🍎 **iOS**
- 💻 **Desktop** (Windows, macOS, Linux)
- 🌐 **Web** (WasmGC)

---

## 📥 Download

Baixe a versão mais recente diretamente:

| Versão | Plataforma | Link | Tamanho |
|---|---|---|---|
| **v1.1.0** | 🤖 Android APK | [Baixar GymTracker-v1.1.0.apk](https://github.com/Artanniel/gymtracker-kvm/releases/latest/download/GymTracker-v1.1.0.apk) | ~13 MB |

> 💡 A release mais recente sempre está disponível em [GitHub Releases](https://github.com/Artanniel/gymtracker-kvm/releases).

---

## ✨ Funcionalidades

- 🔐 **Autenticação JWT** — login/cadastro com token seguro e persistência de sessão
- 👥 **Gestão de Alunos** — cadastro, vinculação de treinos e feedback pós-treino
- 🎬 **Biblioteca de Vídeos** — vídeos por exercício, grupo muscular e categoria
- 💰 **Gestão Financeira** — planos de pagamento, faturas e controle de receitas
- 🔔 **Push Notifications** — lembretes de treino e hidratação
- 🔄 **Offline-First Sync** — funciona sem internet e sincroniza quando online
- 📊 **Progresso e Streaks** — acompanhamento de evolução e metas
- 🤖 **Treinos com IA** — geração inteligente de planos de treino

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
│                 │  (JWT Bearer)   │                    │        │
│                 └─────────────────┘                    │        │
└────────────────────────────────────────────────────────┼────────┘
                                                         │
                              ┌──────────────────────────┘
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
         │     PostgreSQL 15   │
         └─────────────────────┘
```

### Estrutura do Projeto

```text
GymTrackerKMP/
├── shared/                              # 🧠 KMP Core: Lógica + UI
│   └── src/
│       ├── commonMain/                  # Código compartilhado
│       ├── androidMain/                 # actual: Android driver, notifications
│       ├── iosMain/                     # actual: Native driver
│       ├── jvmMain/                     # actual: JDBC driver (desktop)
│       └── wasmJsMain/                  # actual: Web driver
│
├── backend/                             # ☕ Backend Quarkus (Java 21)
│   ├── src/main/java/
│   │   ├── controller/                  # REST Controllers
│   │   ├── domain/                      # JPA Entities
│   │   ├── service/                     # Business Logic
│   │   └── dto/                         # Data Transfer Objects
│   ├── src/main/resources/
│   │   ├── application.properties       # Configurações
│   │   └── db/                          # Liquibase Migrations
│   ├── Dockerfile                       # Multi-stage build
│   └── docker-compose.yml               # Local development
│
└── apps/student/                        # 📱 Entrypoints das plataformas
    ├── androidApp/                      # Android
    ├── desktopApp/                      # Desktop (JVM)
    └── webApp/                          # Web (WasmJS)
```

---

## 🔄 Offline-First Sync

O GymTracker funciona **100% offline**. Todos os dados são salvos localmente primeiro e sincronizados com o backend quando houver conexão.

### Fluxo de Sincronização

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   App KMP    │────►│  Local DB    │────►│  Sync Queue  │
│  (Operação)  │     │  (SQLDelight)│     │ (pending_sync)│
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
| Resolução de conflitos | Remote wins |
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
| **HTTP Client** | Ktor 3.1.1 |
| **ViewModel** | `androidx.lifecycle` 2.9.0 |
| **Gestão de Estado** | StateFlow + `collectAsState()` |
| **Serialização** | Kotlinx Serialization 1.8.1 |
| **Autenticação** | JWT Bearer Token |
| **Assincronismo** | Kotlinx Coroutines 1.10.x |

### Backend (Quarkus)

| Camada | Tecnologia |
|---|---|
| **Framework** | Quarkus 3.15.1 |
| **Linguagem** | Java 21 |
| **ORM** | Hibernate ORM + Panache |
| **Banco de Dados** | PostgreSQL 15 |
| **Migrations** | Liquibase |
| **Autenticação** | JWT (custom) |
| **Health Check** | SmallRye Health |

### Infraestrutura

| Componente | Tecnologia |
|---|---|
| **CI/CD** | GitHub Actions |
| **Containerização** | Docker |
| **Deploy** | Railway |
| **Banco de Dados** | PostgreSQL 15 |

---

## 🚀 Como Buildar e Executar

### Pré-requisitos

| Plataforma | Ferramentas Necessárias |
|---|---|
| **Frontend** | JDK 21, Kotlin 2.1.20 |
| **Backend** | JDK 21+, Maven 3.9+, Docker |
| **Android** | Android Studio Narwhal 2025.1+ |
| **iOS** | macOS + Xcode 16+ |
| **Desktop** | JDK 21+ |
| **Web** | Node.js 18+ + Chrome 119+ |

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
# APK Debug
./gradlew :apps:student:androidApp:assembleDebug

# APK Release
./gradlew :apps:student:androidApp:assembleRelease

# Instalar
adb install apps/student/androidApp/build/outputs/apk/debug/androidApp-debug.apk
```
</details>

<details>
<summary><b>💻 Desktop (JVM)</b></summary>

```bash
./gradlew :apps:student:desktopApp:run

# Empacotamento
./gradlew :apps:student:desktopApp:createDistributable
./gradlew :apps:student:desktopApp:packageDeb    # Linux
./gradlew :apps:student:desktopApp:packageMsi    # Windows
./gradlew :apps:student:desktopApp:packageDmg    # macOS
```
</details>

<details>
<summary><b>🌐 Web (Wasm)</b></summary>

```bash
# Dev server com Hot Reload
./gradlew :apps:student:webApp:wasmJsBrowserDevelopmentRun

# Build de produção
./gradlew :apps:student:webApp:wasmJsBrowserDistribution
```
</details>

---

## 📋 API Endpoints

### Autenticação

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Registrar novo usuário | Não |
| `POST` | `/api/auth/login` | Login e obter JWT | Não |
| `POST` | `/api/auth/logout` | Revogar sessão | JWT |
| `GET` | `/api/auth/me` | Dados do usuário logado | JWT |

### Treinos

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/api/workout-sessions` | Listar sessões | JWT |
| `POST` | `/api/workout-sessions` | Criar sessão | JWT |
| `GET` | `/api/workout-sessions/{id}` | Buscar sessão | JWT |
| `PUT` | `/api/workout-sessions/{id}` | Atualizar sessão | JWT |
| `DELETE` | `/api/workout-sessions/{id}` | Remover sessão | JWT |
| `GET` | `/api/workout-sessions/{id}/sets` | Listar sets | JWT |
| `POST` | `/api/workout-sessions/{id}/sets` | Criar set | JWT |

### Alunos

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/api/students` | Listar alunos | JWT |
| `POST` | `/api/students` | Criar aluno | JWT |
| `GET` | `/api/students/{id}` | Buscar aluno | JWT |
| `PUT` | `/api/students/{id}` | Atualizar aluno | JWT |
| `DELETE` | `/api/students/{id}` | Remover aluno | JWT |
| `POST` | `/api/students/{id}/workouts` | Vincular treino | JWT |
| `POST` | `/api/students/{id}/feedback` | Enviar feedback | JWT |

### Vídeos

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/api/videos` | Listar vídeos | JWT |
| `POST` | `/api/videos` | Criar vídeo | JWT |
| `GET` | `/api/videos/{id}` | Buscar vídeo | JWT |
| `DELETE` | `/api/videos/{id}` | Remover vídeo | JWT |

### Financeiro

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/api/finance/plans` | Listar planos | JWT |
| `POST` | `/api/finance/plans` | Criar plano | JWT |
| `GET` | `/api/finance/invoices` | Listar faturas | JWT |
| `POST` | `/api/finance/invoices` | Criar fatura | JWT |
| `PUT` | `/api/finance/invoices/{id}/pay` | Registrar pagamento | JWT |

### Sync

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/sync` | Sync batch | JWT |
| `GET` | `/api/sync/pull` | Pull dados | JWT |
| `GET` | `/api/sync/health` | Health check | Não |

### Push Notifications

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/push/register` | Registrar token push | JWT |

---

## 📋 Roadmap

### ✅ Concluído

- [x] KMP (Android, iOS, Desktop, Web)
- [x] SQLDelight com migrações
- [x] Splash Screen e Onboarding
- [x] Sistema de streaks e badges
- [x] Notificações in-app
- [x] Gerador de treinos com IA
- [x] Gráficos de progresso
- [x] Backend Quarkus (REST API)
- [x] Autenticação JWT (KMP ↔ Backend)
- [x] Offline-first sync
- [x] Gestão de alunos
- [x] Biblioteca de vídeos
- [x] Gestão financeira
- [x] Push notifications
- [x] CI/CD com GitHub Actions
- [x] Docker Compose local

### 🔄 Em Progresso

- [ ] Deploy no Railway
- [ ] Testes E2E

### 📌 Próximos

- [ ] Monetização (AdMob + SKAdNetwork)
- [ ] Relatórios avançados de financeiro
- [ ] Integração com wearables

---

## 📚 Links Úteis

- [Documentação KMP](https://kotlinlang.org/docs/multiplatform.html)
- [Quarkus Guide](https://quarkus.io/guides/)
- [Railway Deploy](https://docs.railway.app/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Ktor Client](https://ktor.io/docs/client.html)

---

<p align="center">
  <i>Construído com ❤️ e Kotlin</i>
</p>
