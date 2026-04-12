
# Cucumber acceptance tests (Android)

This project runs Cucumber `.feature` files as **Android instrumented tests** (Instrumentation).

## Where features live

- Feature files: `app/src/androidTest/assets/features/*.feature`
- Step definitions + hooks: `app/src/androidTest/java/com/example/rpgaudiomixer/test/acceptance/**`

## Iteration tags

Every feature file is tagged with its iteration (e.g. `@iter0`, `@iter1`, `@iter2`).
CI runs only **completed iterations** — currently `@iter0` and `@iter1`.

When a new iteration is completed (all step definitions implemented and passing):
1. Update the `-PcucumberTags` filter in `.github/workflows/ci.yml` to include the new tag.
2. Update this section to reflect the newly completed iteration.

## How to run (CLI)

Start an emulator or connect a device, then run:

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew.bat :app:connectedDebugAndroidTest
```

To run only specific iterations (as CI does):

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest -PcucumberTags="@iter0 or @iter1"
```

To run a single feature file:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest -PcucumberFeatures="features/can_launch.feature"
```

## How to run (Android Studio)

Run them as **Android Instrumented Tests**:

1. Open the **Gradle** tool window
2. Run `app > Tasks > verification > connectedDebugAndroidTest`

Notes:
- Do **not** use the IDE's “Run” action on a `.feature` file.
- The instrumentation runner is configured in `app/build.gradle.kts` via `testInstrumentationRunner`.

