<div align="center">
  <h1>🏋️‍♂️ GymTracker KMP</h1>
  <p><b>Seu parceiro definitivo para treinos e dieta, construído com o poder do Kotlin Multiplatform.</b></p>
  
  [![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-7F52FF.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org)
  [![Compose Multiplatform](https://img.shields.io/badge/Compose-1.7.1-4285F4.svg?style=flat-square&logo=android)](https://www.jetbrains.com/lp/compose-multiplatform/)
  [![SQLDelight](https://img.shields.io/badge/SQLDelight-2.1.0-FF4081.svg?style=flat-square&logo=sqlite)](https://cashapp.github.io/sqldelight/)
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

* **🤖 Android APK (Debug):** [Baixar GymTracker.apk](androidApp/build/outputs/apk/debug/androidApp-debug.apk) *(Requer que o arquivo seja commitado no repositório)*

---

## 🏗️ Estrutura do Projeto

O projeto segue a arquitetura KMP padrão, separando claramente o código comum dos entrypoints de cada plataforma.

```text
GymTrackerKMP/
├── shared/                        # 🧠 KMP Core: 100% da Lógica de Negócios e UI (Compose)
│   └── src/
│       ├── commonMain/            # Código compartilhado entre todos os targets (UI, Repo, DB)
│       ├── androidMain/           # actual: AndroidSqliteDriver
│       ├── iosMain/               # actual: NativeSqliteDriver
│       ├── jvmMain/               # actual: JdbcSqliteDriver
│       └── wasmJsMain/            # actual: WebWorkerDriver
│
├── androidApp/                    # 📱 Entrypoint Android (MainActivity)
├── desktopApp/                    # 💻 Entrypoint Desktop (JVM + jpackage)
├── webApp/                        # 🌐 Entrypoint Web (WasmJs + webpack)
└── iosApp/                        # 🍎 Projeto Xcode (consome o shared framework)
```

---

## 🚀 Como Buildar e Executar

### Pré-requisitos
| Plataforma | Ferramentas Necessárias |
|---|---|
| **Android** | Android Studio Narwhal 2025.1+ / JDK 17 |
| **iOS** | macOS + Xcode 16+ + Plugin Kotlin Multiplatform |
| **Desktop** | JDK 17+ |
| **Web** | Node.js 18+ + Navegador com suporte a WasmGC (Chrome 119+, Firefox 120+, Safari 18.2+) |

### Comandos Principais

<details>
<summary><b>🤖 Android</b></summary>

```bash
# Compilar e gerar o APK de debug
./gradlew :androidApp:assembleDebug

# Instalar no dispositivo conectado
adb install androidApp/build/outputs/apk/debug/androidApp-debug.apk
```
*Ou simplesmente clique em "Run" no Android Studio.*
</details>

<details>
<summary><b>💻 Desktop (JVM)</b></summary>

```bash
# Executar a aplicação Desktop diretamente
./gradlew :desktopApp:run                       

# Empacotamento Nativo
./gradlew :desktopApp:createDistributable       # Gera executável stand-alone
./gradlew :desktopApp:packageDeb                # Linux (.deb)
./gradlew :desktopApp:packageMsi                # Windows (.msi)
./gradlew :desktopApp:packageDmg                # macOS (.dmg)
```
</details>

<details>
<summary><b>🌐 Web (Wasm)</b></summary>

```bash
# Servidor de desenvolvimento com Hot Reload
./gradlew :webApp:wasmJsBrowserDevelopmentRun   

# Build Otimizado de Produção (Saída em webApp/build/dist/)
./gradlew :webApp:wasmJsBrowserDistribution     
```
</details>

<details>
<summary><b>🍎 iOS</b></summary>

1. Execute a compilação do framework:
   ```bash
   ./gradlew :shared:assembleXCFramework
   ```
2. Abra o projeto no Xcode (`iosApp/iosApp.xcodeproj`).
3. Selecione seu Simulador ou Dispositivo e clique em **Run**.
</details>

---

## 🛠️ Stack Tecnológica

O GymTracker foi modernizado para extrair o máximo do ecossistema Kotlin:

| Camada | Tecnologia |
|---|---|
| **UI** | Compose Multiplatform 1.7.1 |
| **Linguagem** | Kotlin 2.1.20 |
| **Banco de Dados** | SQLDelight 2.1.0 (Bancos Nativos por Plataforma) |
| **ViewModel** | `androidx.lifecycle` 2.9.0 (Suporte KMP) |
| **Gestão de Estado**| StateFlow + `collectAsState()` |
| **Gráficos** | Koalaplot |
| **Assincronismo** | Kotlinx Coroutines 1.10.x |

---

## 🔄 Histórico de Migração (Android ➡️ KMP)

Este projeto evoluiu de um app exclusivamente Android para um projeto multiplataforma completo. Principais substituições:

- `Room + DAOs` ➡️ **SQLDelight (`.sq` files)**
- `LiveData` ➡️ **StateFlow**
- `RecyclerView + Adapters` ➡️ **LazyColumn**
- `ViewBinding + XML` ➡️ **@Composable Functions**
- `MPAndroidChart` ➡️ **Koalaplot**
- `Activity/Fragment` ➡️ **Screen @Composable (Navegação baseada em estado)**

---

## 📋 Roadmap e Próximos Passos

- [ ] Integrar projeto Xcode `iosApp` via framework KMP.
- [ ] Finalizar integração de gráficos de progresso com Koalaplot.
- [ ] Configurar persistência Web via OPFS (Origin Private File System).
- [ ] Configuração de pipelines de CI/CD (Fastlane / GitHub Actions).
- [ ] Implementar AdMob (Android) e SKAdNetwork (iOS) para monetização.

---
<p align="center">
  <i>Construído com ❤️ e Kotlin</i>
</p>
