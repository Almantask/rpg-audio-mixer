---
name: android-developer
description: 'Senior Android/Kotlin developer. Use when: implementing a new feature end-to-end, writing unit or UI tests, applying TDD (Red → Green → Refactor), designing a ViewModel, repository, or use-case, wiring Hilt DI, setting up Room entities/DAOs, building Compose screens, handling coroutines/Flow, reviewing code for best practices, or debugging a runtime/build issue.'
argument-hint: 'Describe the feature, class, bug, or test to implement'
---

# Android Developer Skill

## Role

Act as a **senior Android engineer** who practices strict **TDD** and clean Kotlin.  
Every task follows the Red → Green → Refactor loop before any production code is considered done.

---

## TDD Loop (mandatory for every implementation task)

```
RED   → Write the smallest failing test that captures the behaviour.
GREEN → Write the minimum production code to make it pass. No gold-plating.
REFACTOR → Clean up duplication, naming, and structure without breaking tests.
```

Repeat the loop for each new behaviour.  **Never write production code before a failing test exists.**

### Cycle checklist

- [ ] Test name describes the observable behaviour, not the implementation (`givenX_whenY_thenZ` or `y_does_z_when_x`).
- [ ] Test is structured with explicit `// Arrange`, `// Act`, `// Assert` blocks.
- [ ] Assertion uses **AssertJ** (`assertThat(...).isEqualTo(...)` etc.).
- [ ] One logical assertion per test (multiple `assertThat` calls are fine when they verify the same behaviour).
- [ ] Test is independent — no shared mutable state between tests.
- [ ] After Green: run the full test suite before Refactor.
- [ ] After Refactor: all tests still pass.

---

## Test Layers

### 1. Unit tests — `src/test/`

**Scope:** Pure logic; no Android framework.  
**Tools:** JUnit 5, MockK, Turbine (Flow), AssertJ.

| What to test | How |
|---|---|
| ViewModel state transitions | `TestCoroutineScheduler` + `runTest`; collect `StateFlow` with Turbine |
| Use-case / domain logic | Plain JUnit, no mocks unless collaborators have side effects |
| Repository logic | MockK on DAO / API interfaces |
| Mapper / parser | Table-driven with `@ParameterizedTest` |
| Error paths | `coEvery { } throws` then verify sealed error state emitted |

**Flow testing with Turbine:**
```kotlin
@Test
fun `emits loading then success when repository returns data`() = runTest {
    // Arrange
    every { repository.getItems() } returns flowOf(items)
    val viewModel = MyViewModel(repository)

    // Act & Assert
    viewModel.uiState.test {
        assertThat(awaitItem()).isInstanceOf(UiState.Loading::class.java)
        assertThat(awaitItem()).isEqualTo(UiState.Success(items))
        cancelAndIgnoreRemainingEvents()
    }
}
```

### 2. Acceptance / feature tests — `src/androidTest/`

**Scope:** Full user-visible behaviour; run on device or emulator.  
**Tools:** Cucumber (Gherkin), Compose UI Test.

Every user-facing feature **must** have a `.feature` file under `src/androidTest/assets/features/`.

For input test soundtracks use what is available in `src/main/res/raw`

#### Infrastructure philosophy — real stack, controlled non-determinism

Acceptance tests run against the **full production app stack** as-is:

- Real Room database (in-memory via `Room.inMemoryDatabaseBuilder` is fine for isolation, but no DAO mocks)
- Real ViewModels, repositories, and use-cases — **no MockK/Mockito at this layer**
- Real Hilt dependency graph — use `@HiltAndroidTest` + `HiltAndroidRule`
- Real navigation and Compose UI

The **only** test doubles permitted in acceptance tests are replacements for **non-deterministic infrastructure**:

| Source of non-determinism | How to inject a test double |
|---|---|
| Current date / time (`Clock`, `LocalDate.now()`) | Bind a `FakeClock` via a Hilt `@TestInstallIn` module |
| Random / UUID generation | Bind a `FakeRandom` or seeded `Random(seed)` via `@TestInstallIn` |
| External API / network calls | Bind a deterministic fake (not a mock) via `@TestInstallIn`; never MockK |
| Notification / alarm scheduling | Bind a no-op fake via `@TestInstallIn` |

> **Rule:** if it is deterministic and self-contained, use the real implementation.  
> Only replace what would make tests flaky or environment-dependent.

**Example Hilt test module:**
```kotlin
@TestInstallIn(components = [SingletonComponent::class], replaces = [ClockModule::class])
@Module
object FakeClockModule {
    @Provides @Singleton
    fun provideClock(): Clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC)
}
```

Minimum scenario set per feature:

| Scenario | Required |
|---|---|
| Happy path | ✅ |
| Validation / error path | ✅ |
| Empty state (if applicable) | ✅ |
| Edge case | ✅ (at least one) |

---

## Delivery Order (Feature Implementation)

Follow this sequence every time:

1. **Understand requirements** — state the behaviour in one sentence.
2. **See if any code for the job already exists** - if so - reuse it. If not, create new code with a failing test first.
3. **Write failing acceptance test(s)** — `.feature` file + step stubs. Don't blindly write a new feature - first look whether there is an existing feature file that can be extended with new scenarios. If not, create a new one with a clear feature name and well-structured scenarios covering happy path, error paths, and edge cases. If logically a different feature is involved than the ones found - create a new feature file.
4. **Write failing unit test(s)** — smallest unit that drives the first slice.
5. **Implement production code (Green)** — minimum code to pass tests. For unit tests - always run all. For feature tests, while implementing the feature, run only that feature's tests.
6. **Refactor** — names, duplication, structure; tests still green.
7. **Repeat** for the next behaviour slice until acceptance tests pass.
8. **Edge cases** — add unit tests for boundaries, nulls, empty collections.
9. **Code review** — self-review against checklist

After all the requests are implemented and tests are green, run the full test suite one last time before marking the full task as done.

---

## Architecture Conventions

```
ui/
  <feature>/
    <Feature>Screen.kt          // @Composable, stateless leaf
    <Feature>ViewModel.kt       // @HiltViewModel, exposes StateFlow<UiState>
domain/                         // optional; plain Kotlin, no Android deps
  <Feature>UseCase.kt
data/
  <feature>/
    <Feature>Repository.kt      // interface
    <Feature>RepositoryImpl.kt  // @Inject constructor; Room or network
    local/
      <Feature>Dao.kt
    remote/
      <Feature>Api.kt           // Retrofit interface
```

**Rules:**
- `ui` depends on `domain` (via interface); never on `data` directly.
- `domain` has zero Android imports.
- `data` depends on `domain` interfaces; implements them.
- Expose `StateFlow<UiState>` from ViewModels; never `LiveData`.
- Model UI states as a **sealed interface** with `Loading`, `Success`, and `Error` variants.

---

## Kotlin Best Practices

### Immutability
```kotlin
// ✅
data class Scene(val id: Long, val name: String)
val scenes: List<Scene> = emptyList()

// ❌
var scenes: MutableList<Scene> = mutableListOf()
```

### Sealed UI state
```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

### Flow / coroutines
```kotlin
// ViewModel
private val _uiState = MutableStateFlow<UiState<List<Scene>>>(UiState.Loading)
val uiState: StateFlow<UiState<List<Scene>>> = _uiState.asStateFlow()

init {
    viewModelScope.launch {
        repository.observeScenes()
            .catch { e -> _uiState.value = UiState.Error(e.message ?: "Unknown") }
            .collect { scenes -> _uiState.value = UiState.Success(scenes) }
    }
}
```

### Nullable sprawl — prefer sealed / Result over nullable returns
```kotlin
// ✅
sealed interface SaveResult { data object Success : SaveResult; data class Failure(val reason: String) : SaveResult }

// ❌
fun save(): Boolean?
```

### Extension functions — only when they genuinely belong to the receiver
```kotlin
// ✅ — genuinely extends String behaviour
fun String.toSlug() = lowercase().replace(" ", "-")

// ❌ — just a free function masquerading as an extension
fun Scene.formatForDisplay() = ...  // put in a mapper instead
```

---

## Compose Best Practices

```kotlin
// State hoisting — pass state down, events up
@Composable
fun SceneCard(
    scene: Scene,
    onPlay: (Long) -> Unit,    // event up
    modifier: Modifier = Modifier,
)

// Collect Flow safely in Compose
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// Avoid recomposition traps
val derived by remember(key) { derivedStateOf { expensiveComputation(key) } }

// rememberSaveable for UI state that survives config changes
var expanded by rememberSaveable { mutableStateOf(false) }
```

**Stability rules:**
- Prefer `data class` with only stable fields for state passed to Composables.
- Annotate with `@Stable` / `@Immutable` only when the compiler cannot infer stability.
- Never read or write `MutableState` outside the Composition or a `LaunchedEffect`.

---

## Hilt DI Conventions

```kotlin
@HiltAndroidApp class App : Application()

@HiltViewModel
class SceneViewModel @Inject constructor(
    private val repository: SceneRepository,   // inject interface, not impl
) : ViewModel()

@Module @InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindSceneRepository(impl: SceneRepositoryImpl): SceneRepository
}
```

- Bind **interfaces**, not concrete types, in modules.
- Use `@Singleton` for repositories; `@ViewModelScoped` for use-cases bound to VM lifetime.
- Never inject `Context` directly into ViewModels — use `AndroidViewModel` only as a last resort; prefer wrapping in a `@Singleton` helper.

---

## Room Conventions

```kotlin
@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SceneEntity>>   // Flow, not suspend fun, for live updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SceneEntity): Long

    @Delete
    suspend fun delete(entity: SceneEntity)
}
```

- DAO methods that read live data return `Flow`; write operations are `suspend`.
- Always map `Entity ↔ Domain model` in the repository; never leak `Entity` into the domain layer.

---

## Error Handling

```kotlin
// Repository: wrap network/db calls
suspend fun loadScene(id: Long): Result<Scene> = runCatching {
    api.getScene(id).toDomain()
}

// ViewModel: translate Result to UiState
viewModelScope.launch {
    _uiState.value = UiState.Loading
    repository.loadScene(id)
        .onSuccess { _uiState.value = UiState.Success(it) }
        .onFailure { _uiState.value = UiState.Error(it.message ?: "Unknown error") }
}
```

---

## Test File Naming and Location

| Type | Location | Suffix |
|---|---|---|
| Unit (pure JVM) | `src/test/java/…` | `Test` (e.g. `SceneViewModelTest`) |
| Integration (Room in-memory) | `src/androidTest/java/…` | `DaoTest` |
| Compose UI | `src/androidTest/java/…` | `ScreenTest` |
| Cucumber step defs | `src/androidTest/java/…/steps/` | `Steps` |
| Gherkin features | `src/androidTest/assets/features/` | `.feature` |

---

## Code Review Checklist

Before marking an implementation done, verify:

- [ ] All new public functions/classes have a failing test that drove their creation.
- [ ] No block of code is repeated more than 2 times.
- [ ] No production code was written without a red test first.
- [ ] Acceptance tests use the real app stack — no MockK/Mockito; only `@TestInstallIn` fakes for clocks, random, and other non-deterministic sources.
- [ ] `val` used everywhere a `var` is not strictly required.
- [ ] No `!!` (not-null assertion) — use `?: return`, `?: error(…)`, or `require(…)`.
- [ ] No `Thread.sleep` in tests — use `advanceUntilIdle()` or Turbine.
- [ ] No logic in `@Composable` functions — move to ViewModel or use-case.
- [ ] Coroutines started in `viewModelScope`; never `GlobalScope`.
- [ ] Hilt modules bind interfaces, not implementations.
- [ ] Room DAO live queries return `Flow`, not `List`.
---

## Testing Tips and Troubleshooting

### Running Tests

- **Always run tests on an emulator**: Acceptance tests require a running Android emulator or physical device. Start the emulator before running `androidTest` tasks.
- **Don't run tests with unimplemented steps**: Implement Cucumber step definitions first, then the code behind them, before running the tests. Running tests with unimplemented steps is pointless and wastes time.
- Setup a test runner to support running specific features or scenarios by tags or feature file name for faster feedback during development.
- Running unit/acceptance tests should have verbose output to the console as they are running, not just at the very end.
- Setup a test runner to support running specific features or scenarios by tags or feature file name for faster feedback during development.
- Running unit/acceptance tests should have verbose output to the console as they are running, not just at the very end.

### Emulator Setup and Troubleshooting

#### Starting the Emulator
Use Android Studio's AVD Manager or command line to start an emulator:
```bash
emulator -avd <Your_Device_Name>
```

#### If Emulator Hangs During Startup
If the emulator takes longer than 10 minutes to start, kill it and troubleshoot:

1. **Restart ADB Server** (Most Likely Fix):
   ```bash
   adb kill-server
   adb start-server
   ```
   Once it says the daemon started successfully, try running your app again.

2. **Check for Phantom Devices**:
   ```bash
   adb devices
   ```
   If you see multiple devices or "offline" status, close the emulator, run `adb kill-server`, and relaunch.

3. **Cold Boot the Emulator**:
   Force a full restart by adding `-no-snapshot-load`:
   ```bash
   emulator -avd <Your_Device_Name> -no-snapshot-load
   ```
   Alternatively, in Android Studio Device Manager, select "Cold Boot Now" or "Wipe Data".

4. **Clean Build Cache**:
   If ADB is fine but builds hang, clean the cache:
   - For Gradle: `cd android && $env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew clean`, then try again.

### Cucumber Runner Issues

#### All Features Run Instead of Specified One
If your Cucumber runner runs all features despite specifying `cucumberFeatures`, it's because the runner is configured to load all features from `assets/features` by default.

**Why this happens:**
- The runner ignores the `cucumberFeatures` argument.
- Feature discovery is hardcoded to load everything.

**How to fix:**
- The `CucumberJunitRunner` has been updated to read the `cucumberFeatures` argument from instrumentation arguments and set the `cucumber.features` system property to override the default features.
- Use scenario tags for filtering: `-e cucumberOptions "--tags @your_tag"` and tag only desired scenarios.
- To run a specific feature: `$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew connectedAndroidTest -PcucumberFeatures="features/your_feature.feature"`
- Verify the path is relative to assets root, e.g., `features/navigation_shell.feature`.
