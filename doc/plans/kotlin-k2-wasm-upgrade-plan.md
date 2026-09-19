# Plano: Atualização do Kotlin para corrigir crash do compilador K2/Wasm

**Criado em**: 2026-09-19
**Motivação**: PRs #13 e #20 (Dependabot) falham no check `KMP Compile Check` com um Internal Compiler Error não relacionado ao conteúdo de nenhuma das duas.

---

## 1. Contexto — o que já foi investigado e confirmado

- **Erro**: `org.jetbrains.kotlin.util.FileAnalysisException: ... java.util.NoSuchElementException: Collection contains no element matching the predicate` durante `:shared:compileKotlinWasmJs`.
- **Não é causado pelo conteúdo das dependências bumpadas** — PR #13 (`coroutines 1.10.2→1.11.0`) e PR #20 (`androidxLifecycle 2.9.0→2.11.0`) não têm nada em comum além de ambas invalidarem o cache do Gradle (mudam hash de `libs.versions.toml`).
- **Reproduzido localmente 2x de forma determinística** para a PR #20, com cache do Gradle totalmente limpo e a mesma versão do Gradle usada na CI (9.3.0).
- **A localização do erro é enganosa**: ao editar `AppConfig.kt:138` (onde o erro apontava) o crash migrou para `WorkoutGenerator.kt:203`, um arquivo completamente diferente — prova de que não é um bug no código-fonte, é corrupção de estado interno do compilador K2 durante a análise FIR.
- **Aumentar o heap do daemon Kotlin não resolveu** (`kotlin.daemon.jvm.options`) — a estratégia de execução usada (`GradleCompilerRunnerWithWorkers`) nem consome essa configuração.
- **Categoria confirmada via YouTrack da JetBrains**: família de bugs `NoSuchElementException: Collection/Sequence contains no element matching the predicate` no compiler K2, com tickets abertos afetando especificamente o alvo Wasm combinado com `expect`/`actual` (usado extensivamente no módulo `shared` deste projeto — ver `KT-61573`, warnings de Beta em `TokenStore.kt`, `DatabaseDriverFactory.kt`, `ConnectivityMonitor.kt`, `NotificationScheduler.kt`).
- **PR #13 buildou com sucesso localmente** usando sua própria versão pinada do Gradle (9.7.1, diferente da 9.3.0 do `main`/PR #20) — indício de que versões mais novas de tooling já esquivam do bug.

**Conclusão prática**: é um bug de toolchain (Kotlin K2/Wasm), não do código do app. A correção esperada é atualizar o Kotlin (e o Compose Multiplatform, que anda em lockstep com ele desde o Kotlin 2.0).

---

## 2. Inventário de versões atuais

Arquivo: [`gradle/libs.versions.toml`](file:///home/artanniel/git/kotlin/gymtracker-kvm/gradle/libs.versions.toml)

| Componente | Versão atual |
|---|---|
| Kotlin | `2.1.20` |
| Compose Multiplatform | `1.7.3` |
| AGP (Android Gradle Plugin) | `8.7.3` |
| Gradle (wrapper) | `9.3.0` (main/PR #20) — PR #13 tem `9.7.1` |
| kotlinx-coroutines | `1.10.2` |
| kotlinx-serialization | `1.8.1` |
| kotlinx-datetime | `0.6.1` |
| SQLDelight | `2.1.0` |
| Ktor | `3.1.1` |

**Regra importante**: desde o Kotlin 2.0.0, o **Compose Compiler está mergeado no repositório do Kotlin** — a versão do plugin `org.jetbrains.kotlin.plugin.compose` é sempre igual à versão do Kotlin (`version.ref = "kotlin"` no toml). Ou seja, ao subir o Kotlin, o compiler do Compose sobe junto automaticamente; o que precisa de atenção separada é a versão da **biblioteca** Compose Multiplatform (`composeMultiplatform` no toml), que tem sua própria matriz de compatibilidade com o Kotlin.

Consultar a matriz oficial antes de fixar a versão-alvo: https://kotlinlang.org/docs/multiplatform/compose-compatibility-and-versioning.html

---

## 3. Recomendação de versão-alvo

Estado do ecossistema em 2026-09: Kotlin estável mais recente é a série 2.4.x (2.4.0 lançado em junho/2026), Compose Multiplatform em 1.11.0. Um salto de 2.1.20 direto para 2.4.x é 3 minor versions de distância — alto risco de trazer *outras* quebras (deprecations, mudanças de API) junto com a correção que buscamos.

**Abordagem recomendada — faseada, começando pelo menor risco:**

1. **Primeira tentativa**: Kotlin `2.2.20` (JetBrains recomenda essa versão especificamente para "plataformas com suporte em rápida evolução, como iOS e web" — Wasm se encaixa aqui) + a versão de Compose Multiplatform compatível equivalente (checar matriz).
2. **Se o bug persistir em 2.2.20**: subir para a última patch da série 2.3.x.
3. **Último recurso**: Kotlin 2.4.x (mais distante da versão atual, maior superfície de mudança pra validar).

Em cada etapa, parar assim que o build de `:shared:compileKotlinWasmJs` passar de forma limpa (ver seção de verificação).

> Antes de fixar o número exato, confirmar no [Gradle Plugin Portal](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.multiplatform) ou no [changelog do Kotlin](https://kotlinlang.org/docs/releases.html) qual é o patch mais recente de cada minor — o ecossistema evolui rápido e um número fixado hoje pode já ter um patch mais novo até a execução deste plano.

---

## 4. Passo a passo de execução

### 4.1. Preparação
- [x] Criar branch dedicada: `chore/upgrade-kotlin-2.2` a partir de `main`.
- [x] Checar changelog do Kotlin e Compose Multiplatform por *breaking changes* relevantes.

> **Nota**: ao iniciar a execução, o `main` já estava em Kotlin `2.4.20` (PRs de dependências do Dependabot foram mergeadas). O crash persistia porque `composeMultiplatform` ainda estava em `1.7.3`, incompatível com Kotlin 2.4.x.

### 4.2. Atualização de versões
- [x] Em [`gradle/libs.versions.toml`](file:///home/artanniel/git/kotlin/gymtracker-kvm/gradle/libs.versions.toml): atualizar `composeMultiplatform` de `1.7.3` para `1.11.0`.
- [x] Atualizar AGP de `8.7.3` para `8.9.0` para suportar `compileSdk = 36` exigido pelo `lifecycle-viewmodel-android` 2.11.0.
- [x] Atualizar `compileSdk`/`targetSdk` de 35 para 36 no `androidApp`.
- [x] Forçar `okhttp:4.12.0` para evitar `okhttp-android:5.5.0`, que exige compileSdk 37 (ainda não disponível).

### 4.3. Build local limpo (reproduzir o ambiente da CI)
```bash
./gradlew :shared:compileKotlinWasmJs --no-build-cache
```
- [x] Confirmação: buildou sem `Internal compiler error`.
- [x] Builds validados:
```bash
./gradlew :shared:compileKotlinJvm :shared:test          # ✅
./gradlew :apps:student:androidApp:assembleDebug         # ✅
./gradlew :shared:detekt                                 # ✅
./gradlew :apps:student:androidApp:assembleRelease       # ✅
```

### 4.4. Validar contra as PRs que expuseram o bug
- [x] Simulado localmente o bump de `androidxLifecycle` para `2.11.0` (conteúdo da PR #20) e executado `./gradlew :shared:compileKotlinWasmJs --no-build-cache`.
- [x] Resultado: build passou sem `Internal compiler error`.
- [ ] Reabrir/reatualizar PRs #13 e #20 após merge desta branch para validação final na CI.

### 4.5. Regressão em toda a suíte de CI
- [ ] Push da branch `chore/upgrade-kotlin-2.2` e abrir PR.
- [ ] Confirmar que **todos** os checks passam: `ci.yml`, `backend-build.yml`, `code-quality.yml`, `kmp-build.yml`.
- [x] Detekt e build Android validados localmente.

---

## 5. Plano de rollback

Se a versão-alvo introduzir uma regressão pior que o problema atual (ex.: quebra no Compose Compiler, incompatibilidade com SQLDelight/Ktor):
1. Reverter o commit de bump em `libs.versions.toml` (`git revert`).
2. Documentar no PR qual foi a incompatibilidade encontrada, para não repetir a tentativa com a mesma versão.
3. Como mitigação temporária alternativa (não resolve a causa raiz, mas destrava o Dependabot): marcar `KMP Compile Check` como não-bloqueante (`continue-on-error: true`) especificamente para PRs vindas de `dependabot/**`, já que confirmamos que a falha não está relacionada ao conteúdo dessas PRs especificamente.

---

## 6. Riscos conhecidos / pontos de atenção

- **`expect`/`actual` ainda em Beta** (warnings `KT-61573` presentes em `TokenStore.kt`, `DatabaseDriverFactory.kt`, `ConnectivityMonitor.kt`, `NotificationScheduler.kt`) — versões novas do Kotlin podem mudar o comportamento dessa feature; testar esses 4 arquivos especificamente após o upgrade.
- **SQLDelight 2.1.0** e **Ktor 3.1.1** têm suas próprias matrizes de compatibilidade com Kotlin — checar changelogs antes de assumir que funcionam sem ajuste com o Kotlin novo.
- **Compose Multiplatform for Desktop** exige JDK 17+ para empacotamento nativo (`jpackage`) — não afeta este projeto diretamente (sem target desktop de produção), mas vale checar se `apps/student/desktopApp` é afetado.
- O ambiente de build do servidor Hetzner **não é afetado** por este upgrade — o backend Quarkus é Java puro, o Kotlin/Wasm é só o `shared`/web frontend.

---

## 7. Critério de sucesso

- [x] `:shared:compileKotlinWasmJs` builda limpo com cache zerado localmente.
- [ ] `:shared:compileKotlinWasmJs` builda limpo na CI.
- [x] Simulação do conteúdo da PR #20 (`androidxLifecycle 2.11.0`) passa sem crash do compilador.
- [ ] PRs #13 e #20 ficam verdes no `KMP Compile Check` após rebase na `main`.
- [x] Nenhum novo erro introduzido nos builds locais (`Backend Tests`, `Detekt`, `Android`).
- [ ] App Android e Web continuam funcionando normalmente em teste manual pós-upgrade.
