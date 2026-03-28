---
name: android-feature
description: 'Senior Android developer. Use when: implementing a new feature, adding a Compose screen, wiring MVVM ViewModel, writing a Hilt module, implementing a Room or DataStore repository, writing unit tests with MockK and AssertJ, writing Cucumber/Gherkin acceptance tests, writing Compose UI tests, debugging Flow or coroutine lifecycle issues, troubleshooting Hilt injection, modeling sealed UI state, setting up Retrofit networking, fixing a bug with regression tests.'
argument-hint: 'Describe the feature, screen, or bug to address'
---

# Android Feature Implementation Skill

## When to Use

Invoke this skill for any user-facing feature or infrastructure addition on this project. It enforces the team stack (Hilt, Compose, MVVM, Coroutines + Flow, Room/DataStore, Cucumber) and production-readiness standards.

---

## Delivery Template (features)

Follow this order when implementing a new feature:

1. **Requirements** — Restate the feature in user-visible behaviour terms.
2. **Data model** — Define Kotlin data classes / entities (immutable `val`, sealed types for states).
3. **API / Repository** — Interface first; implementation injected via Hilt. Network via Retrofit + OkHttp; persistence via Room or DataStore.
4. **Domain (if warranted)** — Use-case classes for non-trivial business logic; keep them pure and testable.
5. **ViewModel** — `StateFlow` / `SharedFlow`; `viewModelScope`; sealed `UiState`; unidirectional data flow.
6. **UI** — Jetpack Compose + Material 3; `collectAsStateWithLifecycle`; state hoisting; `rememberSaveable` where appropriate.
7. **Tests** — See [Testing Reference](./references/testing.md):
   - Unit: ViewModel + use-case + repository (MockK, Turbine, AssertJ)
   - UI: Compose UI tests
   - Acceptance: Cucumber `.feature` + step definitions
8. **Edge cases** — Configuration changes, empty states, error states, permission denials, offline scenarios.

---

## Delivery Template (bug fixes)

1. **Symptoms** — Describe the observable failure.
2. **Hypotheses** — List likely root causes (lifecycle, threading, state, null-safety, DI).
3. **Checks** — Minimal steps to confirm/deny each hypothesis.
4. **Fix** — Targeted change; no unrelated refactors.
5. **Regression tests** — Add a unit or UI test that would have caught the bug.
6. **Monitoring / verification notes** — How to verify the fix in a running app or CI.

---

## Key Defaults (apply unless explicitly overridden)

| Concern | Default |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| State | `StateFlow` in ViewModel, `collectAsStateWithLifecycle` in Compose |
| DI | Hilt (`@HiltViewModel`, `@Inject`, modules in `:di`) |
| Async | Kotlin Coroutines; `viewModelScope`; `Dispatchers.IO` for I/O |
| Network | Retrofit + OkHttp + Kotlinx Serialization |
| Persistence | Room for structured data; DataStore for key-value / proto |
| Error modelling | Sealed `Result` / sealed `UiState` |
| Kotlin style | `val`-first; no nullable sprawl; sealed types over booleans |

---

## Architecture Checklist

- [ ] Is the Repository behind an interface? (testability)
- [ ] Does the ViewModel expose only `StateFlow` / `SharedFlow`? (no raw `LiveData` unless forced by legacy)
- [ ] Are coroutines launched in the correct scope with proper cancellation?
- [ ] Is heavy work off the main thread (`Dispatchers.IO` / `Dispatchers.Default`)?
- [ ] Are Hilt scopes correct (`@Singleton`, `@ActivityRetainedScoped`, `@ViewModelScoped`)?
- [ ] Does the UI collect Flow with `collectAsStateWithLifecycle`?
- [ ] Does sealed `UiState` cover Loading / Success / Error?

---

## Testing Quick Rules

- All unit test assertions → **AssertJ** (`assertThat(...)`)
- All tests → `// Arrange`, `// Act`, `// Assert` block comments
- Flow assertions → **Turbine** (`turbineScope { ... }`)
- Mocking → **MockK** (`mockk(relaxed = true)`)
- Acceptance → **Cucumber** (`.feature` in `androidTest/assets/features/`, step definitions in `androidTest/java/.../acceptance/`)

See [Testing Reference](./references/testing.md) for full templates including Cucumber setup.
