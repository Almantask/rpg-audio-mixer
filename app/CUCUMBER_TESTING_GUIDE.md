
# Cucumber acceptance tests (Android)

This project runs Cucumber `.feature` files as **Android instrumented tests** (Instrumentation).

## Where features live

- Feature files: `app/src/androidTest/assets/features/*.feature`
- Step definitions + hooks: `app/src/androidTest/java/com/example/rpgaudiomixer/test/acceptance/**`

## How to run (CLI)

Start an emulator or connect a device, then run:

```powershell
cd c:\Users\ITWORK\source\repos\rpg-audio-mixer
.\gradlew.bat :app:connectedDebugAndroidTest
```

If you use a different build variant, use `:app:connectedAndroidTest`.

## How to run (Android Studio)

Run them as **Android Instrumented Tests**:

1. Open the **Gradle** tool window
2. Run `app > Tasks > verification > connectedDebugAndroidTest`

Notes:
- Do **not** use the IDE's “Run” action on a `.feature` file.
- The instrumentation runner is configured in `app/build.gradle.kts` via `testInstrumentationRunner`.

