# Testing Reference — Android Feature Skill

## Unit Test Conventions

- Framework: **JUnit 5** (Jupiter)
- Mocking: **MockK** (`mockk(relaxed = true)` for dependencies)
- Assertions: **AssertJ** — use `assertThat(...)` exclusively, never JUnit `assertEquals`
- Flow testing: **Turbine**
- Structure: every test must have `// Arrange`, `// Act`, `// Assert` block comments

### ViewModel Unit Test Template

```kotlin
package com.example.rpgaudiomixer.<feature>

import app.cash.turbine.turbineScope
import com.example.rpgaudiomixer.domain.<feature>.<UseCase>
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class <Feature>ViewModelTest {

    private val useCase: <UseCase> = mockk(relaxed = true)
    private val viewModel = <Feature>ViewModel(useCase)

    @Test
    fun `initial state is Loading`() = runTest {
        turbineScope {
            // Arrange
            val states = viewModel.uiState.testIn(this)

            // Act — nothing; just observe initial emission

            // Assert
            assertThat(states.awaitItem()).isInstanceOf(<Feature>UiState.Loading::class.java)
            states.cancel()
        }
    }
}
```

### Repository Unit Test Template

```kotlin
package com.example.rpgaudiomixer.infra.<feature>

import com.example.rpgaudiomixer.domain.<feature>.<Entity>
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class <Feature>RepositoryImplTest {

    private val dao: <Feature>Dao = mockk(relaxed = true)
    private val repository = <Feature>RepositoryImpl(dao)

    @Test
    fun `getAll returns mapped domain list`() = runTest {
        // Arrange
        val entities = listOf(/* test data */)
        coEvery { dao.getAll() } returns entities

        // Act
        val result = repository.getAll()

        // Assert
        assertThat(result).hasSize(entities.size)
    }
}
```

---

## Cucumber Acceptance Tests

### File Locations

| Artifact | Path |
|---|---|
| `.feature` files | `src/androidTest/assets/features/<name>.feature` |
| Step definitions | `src/androidTest/java/com/example/rpgaudiomixer/test/acceptance/` |
| Cucumber runner | `src/androidTest/java/com/example/rpgaudiomixer/test/CucumberOptions.kt` |

### Scenario Coverage Checklist

Every feature file must include at minimum:

- [ ] **Happy path** — the core user journey succeeds
- [ ] **Validation / error path** — invalid input or failure state is handled
- [ ] **Edge case** — one meaninful boundary (empty list, duplicate, offline, etc.)

### Feature File Template

```gherkin
Feature: <Feature name in business language>
  As a <role>
  I want to <goal>
  So that <benefit>

  Scenario: Happy path — <description>
    Given <precondition>
    When <action>
    Then <observable outcome>

  Scenario: Error path — <description>
    Given <precondition with invalid data>
    When <action>
    Then <error message or state is shown>

  Scenario: Edge case — <description>
    Given <boundary condition>
    When <action>
    Then <correct handling>
```

### Step Definition Template

```kotlin
package com.example.rpgaudiomixer.test.acceptance

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.app.MainActivity
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class <Feature>Steps {

    private val composeRule = createAndroidComposeRule<MainActivity>()

    @Given("^<precondition regex>$")
    fun givenPrecondition() {
        // set up state
    }

    @When("^<action regex>$")
    fun whenAction() {
        composeRule.onNodeWithText("Label").performClick()
    }

    @Then("^<outcome regex>$")
    fun thenOutcome() {
        composeRule.onNodeWithText("Expected text").assertExists()
    }
}
```

### Stable Synchronization

- Prefer semantic assertions (`assertExists`, `assertIsDisplayed`) over timing sleeps.
- Register `ComposeIdlingResource` if animations or async operations may cause flakiness.
- Use `composeRule.waitUntil(timeoutMillis = 3000) { ... }` for async state changes, not `Thread.sleep`.

### How to Run Cucumber Tests

**CLI (Gradle):**
```bash
./gradlew connectedDebugAndroidTest
```

**Android Studio:**
Right-click the `.feature` file → *Run Feature* (requires Cucumber for Java plugin).

**Filter a single feature:**
```bash
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.tags="@your-tag"
```
