package com.example.rpgaudiomixer.acceptance.steps

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.acceptance.rules.ComposeRuleHolder
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class MusicPlayerSteps(
    private val composeRuleHolder: ComposeRuleHolder
) : SemanticsNodeInteractionsProvider by composeRuleHolder.composeRule {

    @Given("the app is launched")
    fun appLaunched() {
        // Typically handled by your ActivityScenario holder or default launch; keep minimal for now.
    }

    @When("""I tap the "Play" button""")
    fun tapPlay() {
        onNodeWithText("Play").assertIsDisplayed().performClick()
    }

    @Then("playback should start")
    fun playbackStarted() {
        // Assert a visible UI state change (e.g., "Pause" button shown, "Now Playing" label, etc.)
        onNodeWithText("Pause").assertIsDisplayed()
    }
}