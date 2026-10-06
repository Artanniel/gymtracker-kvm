# Guia de Publicação na Google Play Store — GymTracker

## ✅ Pré-requisitos

1. **Conta de Desenvolvedor Google Play**
   - Taxa única de **US$ 25**.
   - Cadastro em: https://play.google.com/console
   - Verificação de identidade e DUNS (para empresas) podem ser exigidos.

2. **Keystore de release**
   - O arquivo `gymtracker-release-key.jks` já foi gerado na raiz do projeto.
   - **IMPORTANTE:** guarde este arquivo e as senhas em local seguro. Sem ele, você não poderá atualizar o app no futuro.
   - Senhas atuais (altere assim que possível):
     - Store password: `gymtracker`
     - Key password: `gymtracker`
     - Key alias: `gymtracker`

3. **Android App Bundle (AAB)**
   - A Google Play exige AAB para novos apps.
   - Comando para gerar: `./gradlew :apps:student:androidApp:bundleRelease`
   - Saída: `apps/student/androidApp/build/outputs/bundle/release/androidApp-release.aab`

4. **Política de Privacidade**
   - Arquivo: `PRIVACY_POLICY.md`
   - Publique em uma URL pública (ex.: GitHub Pages, site do app) e informe o link no Console do Google Play.

## 🎨 Assets da loja

Você precisará preparar:

| Asset | Especificação | Obrigatório |
|---|---|---|
| Ícone do app | 512 x 512 px (PNG) | ✅ |
| Feature graphic | 1024 x 500 px (PNG/JPG) | ✅ |
| Screenshots | 16:9 ou 9:16, mínimo 2 por formato | ✅ |
| Vídeo promocional | Opcional, YouTube | ❌ |

**Dica:** use o Figma ou Canva para criar os gráficos seguindo a identidade visual do app (verde `#1E9E3E` e azul `#5C8DD6`).

## 📝 Informações do app

Preencha no Console do Google Play:

- **Título:** GymTracker — Treinos e Dieta
- **Descrição curta:** Até 80 caracteres.
- **Descrição completa:** Até 4.000 caracteres, destacando funcionalidades.
- **Categoria:** Saúde e fitness
- **Tags:** treino, academia, dieta, fitness, musculação
- **E-mail de contato:** suporte@gymtracker.app
- **Site:** https://gymtracker.app

## 🚀 Passo a passo de publicação

### 1. Gerar o AAB assinado

```bash
export JAVA_HOME=/home/artanniel/.jdks/jbr-21.0.6
export KEYSTORE_FILE=/home/artanniel/git/kotlin/gymtracker-kvm/gymtracker-release-key.jks
export KEYSTORE_PASSWORD=sua_senha_segura
export KEY_ALIAS=gymtracker
export KEY_PASSWORD=sua_senha_segura

./gradlew :apps:student:androidApp:bundleRelease
```

### 2. Criar a release no Google Play Console

1. Acesse https://play.google.com/console
2. Clique em **Criar app**
3. Preencha as informações básicas
4. Vá em **Produção > Criar nova release**
5. Faça upload do AAB
6. Preencha as notas da release (ex.: "Correção de crash no compilador K2/Wasm e melhorias de estabilidade")

### 3. Configurar assinatura do app (App Signing by Google Play)

- Ao fazer upload do primeiro AAB, o Google Play oferecerá a opção **"Deixar o Google gerenciar e proteger sua chave de assinatura do app"**.
- **Recomendado:** opte por deixar o Google gerenciar. Assim, a Google protege sua chave de assinatura.
- Você ainda precisará do keystore local para gerar os AABs futuros.

### 4. Preencher o formulário de classificação de conteúdo

- Responda o questionário de classificação de conteúdo (ESRB/PEGI/CLASSIND).
- O app provavelmente será classificado como **Livre** ou **+10**.

### 5. Definir preço e distribuição

- Países: selecione Brasil e outros países desejados.
- Preço: gratuito ou pago.
- Concordância com as políticas do Google Play.

### 6. Enviar para revisão

- Após preencher tudo, clique em **Enviar para revisão**.
- A revisão pode levar de algumas horas a alguns dias.

## 🧪 Recomendação: testar antes de produção

Antes de publicar em produção, use as faixas de teste:

1. **Teste interno:** para você e equipe.
2. **Teste fechado:** para um grupo limitado de usuários.
3. **Teste aberto:** para qualquer usuário que queira participar.

Isso permite encontrar bugs antes da release pública.

## ⚠️ Checklist antes de publicar

- [ ] Keystore salvo em local seguro
- [ ] AAB gerado e assinado corretamente
- [ ] Política de privacidade publicada online
- [ ] Ícone, screenshots e feature graphic prontos
- [ ] Descrição do app revisada
- [ ] Testes internos realizados
- [ ] Classificação de conteúdo preenchida
- [ ] Preço e países definidos
- [ ] Termos de serviço (opcional, mas recomendado)

## 🔄 Atualizações futuras

Para cada nova versão:

1. Atualize `versionCode` e `versionName` em `apps/student/androidApp/build.gradle.kts`.
2. Gere um novo AAB com o mesmo keystore.
3. Faça upload no Google Play Console.
4. Adicione notas da release.
5. Envie para revisão.

## 📚 Links úteis

- [Google Play Console](https://play.google.com/console)
- [Políticas do Google Play](https://play.google.com/about/developer-content-policy/)
- [Requisitos de gráficos da loja](https://support.google.com/googleplay/android-developer/answer/9866151)
- [Assinatura de apps Android](https://developer.android.com/studio/publish/app-signing)
