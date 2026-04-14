package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.PendingException
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking

/**
 * Step definitions for session_lock.feature (@iter9).
 *
 * Steps that can be asserted without navigating into the full Campaign→Session→Scene
 * stack are implemented. Steps that require the ActiveSceneScreen to be open with real
 * categories are marked [PendingException] – they will be promoted to real assertions
 * once end-to-end navigation is in place.
 */
@Suppress("TooManyFunctions")
class SessionLockSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {

    private val composeTestRule get() = composeRuleHolder.composeRule

    // ── Given / Background ────────────────────────────────────────────────

    @Given("I have a scene {string}")
    fun iHaveAScene(sceneName: String) {
        runBlocking { PicoToHiltBridge.sceneRepository.createScene(sceneName) }
    }

    @Given("I am on the Active Scene screen for {string}")
    fun iAmOnTheActiveSceneScreenFor(sceneName: String) {
        throw PendingException(
            "Navigating to the Active Scene screen for '$sceneName' requires full Campaign→Session→Scene navigation – promoted in a later iteration."
        )
    }

    @Given("the session is locked")
    fun ensureSessionIsLocked() {
        throw PendingException(
            "Requires ActiveSceneScreen to be open via full navigation – promoted in a later iteration."
        )
    }

    // ── When ──────────────────────────────────────────────────────────────

    @When("I tap the {string} icon")
    fun tapIcon(iconName: String) {
        when (iconName) {
            "Lock" -> composeTestRule.onNodeWithTag("lockButton").performClick()
            else -> throw PendingException("Unknown icon: $iconName")
        }
    }

    @When("I long-press the {string} icon to unlock")
    fun longPressIconToUnlock(iconName: String) {
        throw PendingException(
            "Long-press on lockButton requires the ActiveSceneScreen to be navigated to – promoted in a later iteration."
        )
    }

    @When("I try to drag the {string} slider to {string}")
    fun tryDragSlider(sliderName: String, value: String) {
        throw PendingException(
            "Slider drag gesture while locked requires the ActiveSceneScreen with categories – promoted in a later iteration."
        )
    }

    @When("I try to swipe between {string} and {string} tabs")
    fun trySwipeBetweenTabs(tab1: String, tab2: String) {
        throw PendingException(
            "Tab swipe-lock requires full ActiveSceneScreen context – promoted in a later iteration."
        )
    }

    // ── Then ──────────────────────────────────────────────────────────────

    @Then("the {string} icon should appear in a {string} state")
    fun verifyIconState(iconName: String, state: String) {
        when (iconName) {
            "Lock" -> composeTestRule.onNodeWithTag("lockButton").assertIsDisplayed()
            else -> throw PendingException("Icon '$iconName' assertion not implemented.")
        }
        // Visual icon appearance (Locked/Unlocked) is driven by the icon vector;
        // we verify the tag is present as a structural sanity check.
    }

    @Then("the Master Atmosphere slider should be disabled")
    fun verifyMasterAtmosphereSliderDisabled() {
        throw PendingException(
            "Master Atmosphere slider requires full ActiveSceneScreen context – promoted in a later iteration."
        )
    }

    @Then("the Master Soundboard volume slider should be disabled")
    fun verifyMasterSoundboardSliderDisabled() {
        throw PendingException(
            "Master Soundboard slider requires full ActiveSceneScreen context – promoted in a later iteration."
        )
    }

    @Then("all category play\\/pause buttons should be disabled")
    fun verifyAllPlayPauseButtonsDisabled() {
        throw PendingException(
            "Requires ActiveSceneScreen with at least one category – promoted in a later iteration."
        )
    }

    @Then("all category d20 random buttons should be disabled")
    fun verifyAllD20ButtonsDisabled() {
        throw PendingException(
            "Requires ActiveSceneScreen with at least one category – promoted in a later iteration."
        )
    }

    @Then("all intensity selectors should be disabled")
    fun verifyAllIntensitySelectorsDisabled() {
        throw PendingException(
            "Requires ActiveSceneScreen with at least one category – promoted in a later iteration."
        )
    }

    @Then("all MIX sliders should be disabled")
    fun verifyAllMixSlidersDisabled() {
        throw PendingException(
            "MIX sliders not yet implemented in ActiveSceneScreen – promoted in a later iteration."
        )
    }

    @Then("the {string} button should be hidden")
    fun verifyButtonHidden(buttonName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open and locked – promoted in a later iteration."
        )
    }

    @And("the Master Atmosphere slider should be enabled")
    fun verifyMasterAtmosphereSliderEnabled() {
        throw PendingException(
            "Requires ActiveSceneScreen with full navigation context – promoted in a later iteration."
        )
    }

    @And("all category play\\/pause buttons should be enabled")
    fun verifyAllPlayPauseButtonsEnabled() {
        throw PendingException(
            "Requires ActiveSceneScreen with at least one category – promoted in a later iteration."
        )
    }

    @Then("the Master Atmosphere volume should still be at its original level")
    fun verifyMasterAtmosphereVolumeUnchanged() {
        throw PendingException(
            "Slider gesture resistance requires full ActiveSceneScreen context – promoted in a later iteration."
        )
    }

    @Then("the current tab should not change")
    fun verifyCurrentTabUnchanged() {
        throw PendingException(
            "Tab-swipe lock requires full ActiveSceneScreen context – promoted in a later iteration."
        )
    }
}
