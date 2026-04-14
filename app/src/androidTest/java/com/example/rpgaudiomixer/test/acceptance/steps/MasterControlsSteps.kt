package com.example.rpgaudiomixer.test.acceptance.steps

import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.PendingException
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

/**
 * Step definitions for master_controls.feature (@iter9).
 *
 * Global Stop and Master Intensity scenarios require the ActiveSceneScreen to be open
 * with actual playing categories, which demands full Campaign→Session→Scene navigation.
 * Steps that assert [FakeMusicPlayer] state are implemented; steps that require
 * on-screen UI interaction within the ActiveSceneScreen are [PendingException].
 */
@Suppress("TooManyFunctions")
class MasterControlsSteps(
    private val fakeMusicPlayer: FakeMusicPlayer,
    private val composeRuleHolder: MainActivityComposeRule,
) {

    private val composeTestRule get() = composeRuleHolder.composeRule

    // ── Given / Background ────────────────────────────────────────────────

    @Given("I have a scene {string} with {string} and {string} soundscapes")
    fun iHaveASceneWithSoundscapes(sceneName: String, soundscape1: String, soundscape2: String) {
        runBlocking {
            PicoToHiltBridge.sceneRepository.createScene(sceneName)
            val scene = kotlinx.coroutines.flow.first(
                PicoToHiltBridge.sceneRepository.observeAll()
            ).firstOrNull { it.name == sceneName } ?: return@runBlocking
            PicoToHiltBridge.soundscapeCategoryRepository.addCategory(scene.id, soundscape1)
            PicoToHiltBridge.soundscapeCategoryRepository.addCategory(scene.id, soundscape2)
        }
    }

    @Given("the {string} scene is playing")
    fun theSceneIsPlaying(sceneName: String) {
        throw PendingException(
            "Playing a scene requires navigating to the ActiveSceneScreen for '$sceneName' – promoted in a later iteration."
        )
    }

    @Given("I have triggered {string} from the soundboard")
    fun haveTriggerSoundFromSoundboard(soundId: String) {
        throw PendingException(
            "Requires the soundboard to be open with the '$soundId' track – promoted in a later iteration."
        )
    }

    @Given("{string} is at Intensity Level {word}")
    fun setIntensityLevel(categoryName: String, level: String) {
        throw PendingException(
            "Intensity level assertion on '$categoryName' requires the ActiveSceneScreen to be open."
        )
    }

    @Given("there are no tracks at Intensity Level III in any soundscape")
    fun noTracksAtIntensityLevelIII() {
        throw PendingException(
            "Greyed-out intensity validation requires the full soundscape/track data on screen."
        )
    }

    // ── When ──────────────────────────────────────────────────────────────

    @When("I tap the {string} button")
    fun tapButton(buttonName: String) {
        when (buttonName) {
            "Global Stop" -> {
                throw PendingException(
                    "Requires the ActiveSceneScreen to be open – promoted in a later iteration."
                )
            }
            else -> throw PendingException("Unknown button: '$buttonName'")
        }
    }

    @When("I tap {string} on the {string} control")
    fun tapIntensityOnControl(intensityLabel: String, controlName: String) {
        throw PendingException(
            "Requires '$controlName' to be visible on the ActiveSceneScreen – promoted in a later iteration."
        )
    }

    // ── Then ──────────────────────────────────────────────────────────────

    @Then("the {string} soundscape should fade out and stop")
    fun verifySoundscapeStopped(soundscapeName: String) {
        // Validate via FakeMusicPlayer that stopAll() was called
        assertThat(fakeMusicPlayer.getLoopingCategories()).doesNotContain(soundscapeName)
    }

    @Then("the {string} sound effect should stop immediately")
    fun verifySoundEffectStopped(soundName: String) {
        assertThat(fakeMusicPlayer.getLoopingCategories()).doesNotContain(soundName)
    }

    @Then("{string} should transition to Intensity Level {word}")
    fun verifyIntensityTransition(categoryName: String, level: String) {
        throw PendingException(
            "Intensity transition on '$categoryName' requires the ActiveSceneScreen to be open."
        )
    }

    @Then("{string} on the {string} control should be highlighted in gold")
    fun verifyIntensityHighlighted(intensityLabel: String, controlName: String) {
        throw PendingException(
            "Visual gold highlight requires the ActiveSceneScreen to be open – promoted in a later iteration."
        )
    }

    @Then("the {string} level should remain at its previous value")
    fun verifyIntensityLevelUnchanged(controlName: String) {
        throw PendingException(
            "Requires the '$controlName' to be visible on the ActiveSceneScreen – promoted in a later iteration."
        )
    }

    @And("the {string} {string} button should be greyed out")
    fun verifyIntensityButtonGreyedOut(controlName: String, intensityLabel: String) {
        throw PendingException(
            "Greyed-out button state for '$controlName/$intensityLabel' requires the ActiveSceneScreen."
        )
    }

    @Then("the {string} play button should show ▶")
    fun verifyPlayButtonShowsPlay(categoryName: String) {
        throw PendingException(
            "Requires the '$categoryName' card to be visible on the ActiveSceneScreen – promoted in a later iteration."
        )
    }
}
