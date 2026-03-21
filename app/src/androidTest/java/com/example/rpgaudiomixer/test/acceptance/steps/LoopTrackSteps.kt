package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.app.components.SoundboardTestTags
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.test.acceptance.util.assertTextDisplayed
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class LoopTrackSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {

    @When("I press the {string} loop button")
    @Given("I had pressed the {string} loop button")
    fun iPressTheLoopButton(loopId: String) {
        composeRuleHolder.composeRule
            .onNodeWithTag(SoundboardTestTags.loopButton(loopId))
            .performClick()
    }

    @Then("I should see {string} is now looping")
    fun iShouldSeeIsNowLooping(loopId: String) {
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.assertTextDisplayed("Now looping: $loopId")
    }

    @Then("I should see nothing is looping")
    fun iShouldSeeNothingIsLooping() {
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.assertTextDisplayed("Now looping: (none)")
    }
}
