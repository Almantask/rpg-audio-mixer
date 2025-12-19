package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.app.soundboard.SoundboardTestTags
import com.example.rpgaudiomixer.domain.media.SoundId
import com.example.rpgaudiomixer.test.acceptance.rules.SoundboardComposeRule
import com.example.rpgaudiomixer.test.acceptance.world.SoundboardWorld
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert.assertEquals

class MusicPlayerSteps(
    private val world: SoundboardWorld,
    private val composeRuleHolder: SoundboardComposeRule,
) {

    @When("I press the {string} sound button")
    fun iPressTheSoundButton(soundId: String) {
        composeRuleHolder.composeRule
            .onNodeWithTag(SoundboardTestTags.soundButton(SoundId(soundId)))
            .performClick()
    }

    @Then("the {string} sound should be played")
    fun theSoundShouldBePlayed(soundId: String) {
        val expected = listOf(SoundId(soundId))
        assertEquals(expected, world.fakeMusicPlayer.played)
    }

    @Then("the sounds should be played in order: {string} and then {string}")
    fun theSoundsShouldBePlayedInOrder(first: String, second: String) {
        val expected = listOf(SoundId(first), SoundId(second))
        assertEquals(expected, world.fakeMusicPlayer.played)
    }
}