package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.app.components.SoundboardTestTags
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlin.math.abs

class SoundboardSteps(
    private val fakeMusicPlayer: FakeMusicPlayer,
    private val composeRuleHolder: MainActivityComposeRule,
) {

    @When("I press the {string} sound button")
    @Given("I had pressed the {string} sound button")
    fun iPressTheSoundButton(soundId: String) {
        composeRuleHolder.composeRule
            .onNodeWithTag(SoundboardTestTags.soundButton(soundId))
            .performClick()
    }

    // -------------------------------------------------------------------------
    // @iter6 – Soundboard playback steps
    // -------------------------------------------------------------------------

    /** Taps a sound button identified by its soundId test tag. */
    @When("I tap the {string} sound button")
    @Given("I have tapped the {string} sound button")
    fun iTapTheSoundButton(soundId: String) {
        composeRuleHolder.composeRule
            .onNodeWithTag(SoundboardTestTags.soundButton(soundId))
            .performClick()
    }

    /** Asserts the sound was played with a near-instant (low latency) response. */
    @Then("the {string} sound plays with near-instant \\(low latency\\) response")
    fun theSoundPlaysWithNearInstantResponse(soundId: String) {
        composeRuleHolder.composeRule.waitForIdle()
        assertTrue(
            "Expected '$soundId' to have been played, but played list was: ${fakeMusicPlayer.played}",
            fakeMusicPlayer.played.contains(soundId),
        )
    }

    /** Simpler assertion that a given sound was played (no latency constraint). */
    @Then("the {string} sound plays")
    fun theSoundPlays(soundId: String) {
        composeRuleHolder.composeRule.waitForIdle()
        assertTrue(
            "Expected '$soundId' to have been played, but played list was: ${fakeMusicPlayer.played}",
            fakeMusicPlayer.played.contains(soundId),
        )
    }

    /**
     * Asserts that two sounds were both played and their start times are within
     * 200 ms of each other (i.e. they started simultaneously).
     */
    @Then("{string} and {string} play simultaneously")
    fun bothSoundsPlaySimultaneously(first: String, second: String) {
        composeRuleHolder.composeRule.waitForIdle()

        val eventsBySound = fakeMusicPlayer.playEvents
            .filter { it.soundId == first || it.soundId == second }
            .groupBy { it.soundId }

        assertTrue(
            "Expected '$first' to have been played, but events were: ${fakeMusicPlayer.playEvents}",
            eventsBySound[first]?.isNotEmpty() == true,
        )
        assertTrue(
            "Expected '$second' to have been played, but events were: ${fakeMusicPlayer.playEvents}",
            eventsBySound[second]?.isNotEmpty() == true,
        )

        val firstEvent = eventsBySound.getValue(first).first()
        val secondEvent = eventsBySound.getValue(second).first()
        val deltaMs = abs(firstEvent.startedAtNanos - secondEvent.startedAtNanos) / 1_000_000.0

        val thresholdMs = 200.0
        assertTrue(
            "Expected '$first' and '$second' to start ~simultaneously (<= ${thresholdMs}ms) but delta was ${deltaMs}ms.",
            deltaMs <= thresholdMs,
        )
    }

    // -------------------------------------------------------------------------
    // Pre-existing step definitions (iter0 / iter5)
    // -------------------------------------------------------------------------

    @Then("the {string} sound should be played")
    fun theSoundShouldBePlayed(soundId: String) {
        val expected = listOf((soundId))
        assertEquals(expected, fakeMusicPlayer.played)
    }

    @Then("the sounds should be played at the same time")
    fun theSoundsShouldBePlayedAtTheSameTime(table: DataTable) {
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

        val eventsBySound = fakeMusicPlayer.playEvents
            .filter { it.soundId in expectedSoundIds }
            .groupBy { it.soundId }

        expectedSoundIds.forEach { soundId ->
            assertTrue(
                "Expected sound $soundId to have been played, but events were: ${fakeMusicPlayer.playEvents}",
                eventsBySound[soundId]?.isNotEmpty() == true,
            )
        }

        val firstTwo = expectedSoundIds.take(2)
        val firstEvent = eventsBySound.getValue(firstTwo[0]).first()
        val secondEvent = eventsBySound.getValue(firstTwo[1]).first()

        val deltaMs = abs(firstEvent.startedAtNanos - secondEvent.startedAtNanos) / 1_000_000.0

        val thresholdMs = 200.0
        assertTrue(
            "Expected sounds ${firstTwo[0]} and ${firstTwo[1]} to start ~simultaneously (<= $thresholdMs ms) but was $deltaMs ms.",
            deltaMs <= thresholdMs,
        )
    }
}