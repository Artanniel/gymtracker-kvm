# GymTracker KMP

App de acompanhamento de treino e dieta em **Kotlin Multiplatform + Compose Multiplatform**.

Targets: Android · iOS · Desktop (Windows/macOS/Linux) · Web (Wasm)

---

## Estrutura do projeto

```
GymTrackerKMP/
├── shared/                        # Módulo KMP: 100% da lógica e UI (Compose)
│   └── src/
│       ├── commonMain/            # Código compartilhado por todos os targets
│       │   ├── kotlin/com/gymtracker/
│       │   │   ├── AppDependencies.kt
│       │   │   ├── data/{db, model, repository}
│       │   │   ├── ui/{home, workout, history, diet}
│       │   │   └── util/
│       │   └── sqldelight/        # Arquivos .sq (substituem Room)
│       ├── androidMain/           # actual: AndroidSqliteDriver
│       ├── iosMain/               # actual: NativeSqliteDriver
│       ├── jvmMain/               # actual: JdbcSqliteDriver
│       └── wasmJsMain/            # actual: WebWorkerDriver
│
├── androidApp/                    # Entrypoint Android (MainActivity)
├── desktopApp/                    # Entrypoint Desktop (JVM + jpackage)
├── webApp/                        # Entrypoint Web (WasmJs + webpack)
└── iosApp/                        # Projeto Xcode (consume shared framework)
```

---

## Requisitos

| Plataforma | Requisito |
|---|---|
| Android | Android Studio Narwhal 2025.1+ / JDK 17 |
| iOS | macOS + Xcode 16+ + Kotlin Multiplatform plugin |
| Desktop | JDK 17+ |
| Web | Node.js 18+ (webpack) + navegador com WasmGC (Chrome 119+, Firefox 120+, Safari 18.2+) |

---

## Como buildar

### Android
```bash
./gradlew :androidApp:assembleDebug
# ou pelo Android Studio: Run 'androidApp'
```

### Desktop (JVM)
```bash
./gradlew :desktopApp:run                       # Roda direto
./gradlew :desktopApp:createDistributable       # Gera distribuível nativo
./gradlew :desktopApp:packageDeb                # .deb (Linux)
./gradlew :desktopApp:packageMsi                # .msi (Windows)
./gradlew :desktopApp:packageDmg                # .dmg (macOS)
```

### Web
```bash
./gradlew :webApp:wasmJsBrowserDevelopmentRun   # Servidor dev com hot reload
./gradlew :webApp:wasmJsBrowserDistribution     # Build de produção → webApp/build/dist/
```

### iOS
1. Execute: `./gradlew :shared:assembleXCFramework`
2. Abra `iosApp/iosApp.xcodeproj` no Xcode
3. O framework KMP é linkado via direct integration (embedAndSignAppleFrameworkForXcode)
4. Run no simulador ou device

---

## Stack técnica

| Camada | Tecnologia |
|---|---|
| UI | Compose Multiplatform 1.7.x |
| Linguagem | Kotlin 2.1.20 |
| Banco de dados | SQLDelight 2.1.0 |
| ViewModel | androidx.lifecycle 2.9.0 (KMP) |
| Estado | StateFlow / collectAsState() |
| Gráficos | Koalaplot (substituiu MPAndroidChart) |
| Async | Kotlinx Coroutines 1.10.x |

---

## Migrações feitas

| Antes (Android-only) | Depois (KMP) |
|---|---|
| Room + DAOs | SQLDelight .sq files |
| LiveData | StateFlow |
| RecyclerView + Adapters | LazyColumn |
| ViewBinding + XML | @Composable functions |
| MPAndroidChart | Koalaplot |
| Activity/Fragment | Screen @Composable |

---

## Adicionando o alvo iOS (iosApp)

A pasta `iosApp/` deve conter um projeto Xcode criado pelo KMP wizard em https://kmp.jetbrains.com.

Configuração no Xcode (Build Phase "Run Script"):
```bash
cd "$SRCROOT/.."
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

---

## Notas sobre a Web (WasmJs)

- **Status**: Beta (Compose Multiplatform 1.7+)
- O banco usa `WebWorkerDriver` (SQLDelight 2.1.0 + sql.js via Web Worker)
- Persistência: mantida em memória por sessão (adicione OPFS ou localStorage para persistir entre sessões)
- Requer browsers com suporte a WasmGC: Chrome 119+, Firefox 120+, Safari 18.2+

---

## Próximos passos sugeridos

- [ ] Adicionar iosApp com projeto Xcode (via kmp.jetbrains.com wizard)
- [ ] Implementar gráficos de progresso com Koalaplot (substituir MPAndroidChart)
- [ ] Persistência Web com OPFS (Origin Private File System)
- [ ] Configurar Fastlane para CI/CD Android + iOS
- [ ] Adicionar AdMob (Android) / SKAdNetwork (iOS) para monetização
