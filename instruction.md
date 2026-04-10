# CI Reports — How to View Test Results

Every CI run (push or pull request) uploads three downloadable artifacts. You can find them at the bottom of any [GitHub Actions run summary](https://github.com/Almantask/rpg-audio-mixer/actions) page under the **Artifacts** section.

---

## Artifacts at a Glance

| Artifact | Contents | Entry point |
|---|---|---|
| `unit-test-report` | Gradle HTML test report (pass/fail per test, stack traces) | `debugUnitTest/index.html` |
| `code-coverage-report` | JaCoCo HTML coverage report (class / method / line %) | `index.html` |
| `acceptance-test-report` | AGP HTML report + Cucumber HTML with per-step screenshots + individual PNG files | see sections below |

---

## Step-by-step: Viewing Reports

### 1. Open the CI run

1. Go to **Actions** → pick the workflow run you want to inspect.
2. Scroll to the bottom of the run summary page.
3. Under **Artifacts**, click the artifact you want to download.

---

### 2. Unit Test Results

**Artifact:** `unit-test-report`

1. Download and unzip the artifact.
2. Open `debugUnitTest/index.html` in your browser.
3. The report shows a full pass/fail breakdown with test names, durations, and stack traces for any failures.

---

### 3. Code Coverage

**Artifact:** `code-coverage-report`

1. Download and unzip the artifact.
2. Open `index.html` in your browser.
3. The JaCoCo report shows class, method, and line coverage percentages with colour-coded source highlighting (green = covered, red = uncovered).

---

### 4. Acceptance Test Results — Full Report with Screenshots

**Artifact:** `acceptance-test-report`

The artifact contains three directories:

```
acceptance-test-report/
├── androidTests/          ← AGP HTML report (summary view)
│   └── connected/
│       └── debug/
│           └── index.html
├── cucumber/              ← Cucumber HTML with embedded per-step screenshots
│   └── reports/
│       └── cucumber.html
└── screenshots/           ← Individual PNG files, one per step
    ├── step_001_<scenario_name>.png
    ├── step_002_<scenario_name>.png
    └── ...
```

#### 4a. AGP summary report

Open `androidTests/connected/debug/index.html` — shows overall pass/fail counts and per-device breakdown.

#### 4b. Cucumber report with screenshots  *(recommended)*

Open `cucumber/reports/cucumber.html` — this is the primary report:

- Each **feature** is expanded into **scenarios**, and each **scenario** into individual **Given / When / Then steps**.
- After every step there is an embedded **PNG screenshot** of the emulator screen at that exact moment, displayed inline.
- Use this to visually verify what the UI looked like during each assertion.

#### 4c. Individual screenshot PNGs

If the Cucumber HTML is unavailable or you want to inspect specific screenshots directly:

- Browse the `screenshots/` folder.
- Files are named `step_NNN_<scenario_name>.png` where `NNN` is a zero-padded step index within the scenario (e.g., `step_003_User_changes_global_volume.png`).
- Open any PNG directly in your browser or image viewer.

---

## Running Tests Locally

### Unit tests

```bash
# If needed, set the override: $env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"
./gradlew testDebugUnitTest
```

The HTML report is generated at:
```
app/build/reports/tests/debugUnitTest/index.html
```

### Unit tests with coverage

```bash
# If needed, set the override: $env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"
./gradlew testDebugUnitTest createDebugUnitTestCoverageReport
```

Coverage report:
```
app/build/reports/coverage/test/debug/index.html
```

### Acceptance tests (requires a connected emulator or device)

You can run individual features or logical suites without executing the entire suite:

**By Tag (Recommended):**
```bash
# General core tests
# General core tests (with override if needed)
$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew connectedDebugAndroidTest -PcucumberTags="@core"

# Specific feature test with local JAVA_HOME override (if needed)
$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew connectedDebugAndroidTest -PcucumberTags="@can_launch"
```

**By File Path:**
```bash
$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew connectedDebugAndroidTest -PcucumberFeatures="features/play_scene.feature"
```

### High-Scale Execution & Configuration
When running features on CI, utilize these configurations:
1. **Sharding**: We split tests dynamically across multiple isolated emulators via Gradle Managed Devices (e.g. `pixel4Api33GroupDebugAndroidTest`).
2. **Auto-Retries**: We use `org.gradle.test-retry` to fight emulator flakiness. Flaky tests are retried automatically.
3. **Report Aggregation**: We use `test-report-aggregator` to merge parallel parallel HTML reports into one unified view in `build/reports/allTests/index.html`.

**To run the full suite:**
```bash
$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew connectedDebugAndroidTest
```

The AGP HTML report is generated at:
```
app/build/reports/androidTests/connected/debug/index.html
```

After the run, pull the Cucumber report and screenshots from the device:

```bash
adb pull /sdcard/Android/data/com.example.rpgaudiomixer/files/reports  app/build/reports/cucumber
adb pull /sdcard/Android/data/com.example.rpgaudiomixer/files/screenshots  app/build/reports/screenshots
```

Then open `app/build/reports/cucumber/reports/cucumber.html` for the full step-by-step report with screenshots.
