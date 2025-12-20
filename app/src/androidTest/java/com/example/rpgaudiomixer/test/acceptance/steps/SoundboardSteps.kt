package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.app.soundboard.SoundboardTestTags
import com.example.rpgaudiomixer.test.acceptance.rules.SoundboardComposeRule
import com.example.rpgaudiomixer.test.acceptance.world.SoundboardWorld
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlin.math.abs

class SoundboardSteps(
    private val world: SoundboardWorld,
    private val composeRuleHolder: SoundboardComposeRule,
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
        val expected = listOf((soundId))
        assertEquals(expected, world.fakeMusicPlayer.played)
    }

    @Then("the sounds should be played in order: {string} and then {string}")
    fun theSoundsShouldBePlayedInOrder(first: String, second: String) {
        val expected = listOf((first), (second))
        assertEquals(expected, world.fakeMusicPlayer.played)
    }

    @Then("the sounds should be played at the same time")
    fun theSoundsShouldBePlayedAtTheSameTime(table: DataTable) {
        // Feature uses a single-row table: | whip | owl |
        val expectedSoundIds = table
            .cells()
            .flatten()
            .filter { it.isNotBlank() }
            .map { (it.trim()) }

        assertTrue(
            "Expected at least 2 sound ids in the table, but got ${expectedSoundIds.size}: $expectedSoundIds",
            expectedSoundIds.size >= 2,
        )

        // Ensure all UI-triggered work has been processed before reading the fake.
        composeRuleHolder.composeRule.waitForIdle()

        val eventsBySound = world.fakeMusicPlayer.playEvents
            .filter { it.soundId in expectedSoundIds }
            .groupBy { it.soundId }

        expectedSoundIds.forEach { soundId ->
            assertTrue(
                "Expected sound $soundId to have been played, but events were: ${world.fakeMusicPlayer.playEvents}",
                eventsBySound[soundId]?.isNotEmpty() == true,
            )
        }

        // Compare the first play event for each sound id.
        val firstTwo = expectedSoundIds.take(2)
        val firstEvent = eventsBySound.getValue(firstTwo[0]).first()
        val secondEvent = eventsBySound.getValue(firstTwo[1]).first()

        val deltaMs = abs(firstEvent.startedAtNanos - secondEvent.startedAtNanos) / 1_000_000.0

        // This is measuring time inside the fake player, so it's typically very tight.
        val thresholdMs = 100.0
        assertTrue(
            "Expected sounds ${firstTwo[0]} and ${firstTwo[1]} to start ~simultaneously (<= $thresholdMs ms) but was $deltaMs ms.",
            deltaMs <= thresholdMs,
        )
    }
}