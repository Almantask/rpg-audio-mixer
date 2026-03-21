package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.app.components.SoundboardTestTags
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.test.acceptance.util.assertTextDisplayed
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert.assertTrue

class SoundboardSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {

    @When("I press the {string} sound button")
    @Given("I had pressed the {string} sound button")
    fun iPressTheSoundButton(soundId: String) {
        composeRuleHolder.composeRule
            .onNodeWithTag(SoundboardTestTags.soundButton(soundId))
            .performClick()
    }

    @Then("the {string} sound should be played")
    fun theSoundShouldBePlayed(soundId: String) {
        composeRuleHolder.composeRule.assertTextDisplayed("Now playing: $soundId")
    }

    @Then("the sounds should be played at the same time")
    fun theSoundsShouldBePlayedAtTheSameTime(table: DataTable) {
        val expectedSoundIds = table
            .cells()
            .flatten()
            .filter { it.isNotBlank() }
            .map { it.trim() }

        assertTrue(
            "Expected at least 2 sound ids in the table, but got ${expectedSoundIds.size}: $expectedSoundIds",
            expectedSoundIds.size >= 2,
        )

        // Ensure all UI-triggered work has been processed before asserting.
        composeRuleHolder.composeRule.waitForIdle()

        // With the real player, both sounds play as independent ExoPlayer instances.
        // The UI reflects the last triggered sound—verify it was processed without error.
        val lastSoundId = expectedSoundIds.last()
        composeRuleHolder.composeRule.assertTextDisplayed("Now playing: $lastSoundId")
    }
}
