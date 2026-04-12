# CI/CD Setup Guide for Arcanum Audio

This document explains the automated CI/CD pipeline and the manual steps required to enable deployment to the Google Play Store.

## 1. Automated CI (Continuous Integration)
The `.github/workflows/ci.yml` workflow runs on every push and pull request. It performs the following:
- **Linting:** Runs `detekt` for static analysis.
- **Unit Tests:** Executes local JVM tests with coverage reporting.
- **Acceptance Tests:** Runs Cucumber/Espresso tests on a real Android emulator (API 34) with KVM and PulseAudio enabled for audio verification.

## 2. Automated CD (Continuous Deployment)
The `.github/workflows/release.yml` workflow is a **manual process** triggered via the **Actions** tab in GitHub (workflow_dispatch). It deploys the app to the **Internal Testing** track on Google Play.

### How to Trigger a Deployment
1. Go to your GitHub repository.
2. Navigate to the **Actions** tab.
3. Select the **Release** workflow from the sidebar.
4. Click **Run workflow**, select the branch/tag you wish to deploy, and confirm.

### Required GitHub Secrets
To enable deployment, you must add the following secrets in **Settings > Secrets and variables > Actions**:

| Secret Name | Description |
| ----------- | ----------- |
| `RELEASE_KEYSTORE_BASE64` | The contents of your `.jks` or `.keystore` file, encoded in Base64 (`base64 -w 0 your_keystore.jks`). |
| `RELEASE_KEYSTORE_PASSWORD` | The password for your keystore file. |
| `RELEASE_KEY_ALIAS` | The alias for your signing key. |
| `RELEASE_KEY_PASSWORD` | The password for your signing key. |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | The JSON key for a Google Cloud Service Account with "Release Manager" permissions on Google Play. |

## 3. Google Play Track Information
We use the **Internal Testing** track for the following reasons:
- **Private:** Only testers you invite via email can see or download the app.
- **Fast:** Builds are usually available within minutes of upload.
- **Capacity:** Supports up to 100 testers per app.

### How to Invite Testers
1. Go to the [Google Play Console](https://play.google.com/console/).
2. Select your app.
3. Navigate to **Testing > Internal testing**.
4. In the **Testers** tab, create or select an email list of your beta testers.

## 4. Local Release Build
To manually build a signed AAB locally:
```powershell
./gradlew bundleRelease `
  -Pandroid.injected.signing.store.file=your_path_to/release.keystore `
  -Pandroid.injected.signing.store.password=your_password `
  -Pandroid.injected.signing.key.alias=your_alias `
  -Pandroid.injected.signing.key.password=your_password
```
