package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import com.example.rpgaudiomixer.app.soundboard.SoundboardTestTags
import com.example.rpgaudiomixer.test.acceptance.rules.SoundboardComposeRule
import io.cucumber.java.en.Then

class SoundboardUiSteps(
    private val composeRuleHolder: SoundboardComposeRule,
) {

    @Then("I should see now playing as {string}")
    fun iShouldSeeNowPlayingAs(label: String) {
        composeRuleHolder.composeRule
            .onNodeWithTag(SoundboardTestTags.nowPlayingText)
            .assertTextEquals("Now playing: $label")
    }

    @Then("I should see now playing as none")
    fun iShouldSeeNowPlayingAsNone() {
        composeRuleHolder.composeRule
            .onNodeWithTag(SoundboardTestTags.nowPlayingText)
            .assertTextEquals("Now playing: (none)")
    }
}

