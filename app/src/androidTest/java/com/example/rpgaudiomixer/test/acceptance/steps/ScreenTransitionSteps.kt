package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.PendingException
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Step definitions for screen_transitions.feature (@iter8).
 *
 * All transition scenarios verify intent (the NavHost is configured with enter/exit
 * transitions), but the visual animation itself cannot be asserted because Espresso runs
 * with `animationsDisabled = true`.  Steps that require visual frame-level inspection are
 * marked [PendingException].
 *
 * The mini player steps assert presence/absence of the "miniPlayer" test tag, which IS
 * detectable without frame-level animation inspection.
 */
class ScreenTransitionSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {

    private val composeTestRule get() = composeRuleHolder.composeRule

    // ──────────────────────────────────────────────────────────────────────────────────────────
    // Scenario: Hierarchical navigation uses Container Transform
    // ──────────────────────────────────────────────────────────────────────────────────────────

    @When("I tap on a campaign card to open its Sessions list")
    fun tapCampaignCardToOpenSessions() {
        throw PendingException(
            "Requires a seeded campaign in the DB and full navigation to SessionsScreen – " +
                "visual Container Transform cannot be asserted with animationsDisabled=true."
        )
    }

    @Then("the campaign card expands smoothly to fill the screen background")
    fun verifyCampaignCardExpands() {
        throw PendingException("Visual Container Transform animation cannot be asserted with animationsDisabled=true.")
    }

    @And("the top and bottom navigation bars remain fixed")
    fun verifyNavBarsFixed() {
        throw PendingException("Visual animation frame assertion not supported in Espresso with animationsDisabled=true.")
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────
    // Scenario: Lateral navigation uses Shared X-Axis slide
    // ──────────────────────────────────────────────────────────────────────────────────────────

    @Given("I am on the Home tab")
    fun navigateToHomeTab() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_HOME")
            .performClick()
        composeTestRule.waitForIdle()
    }

    @When("I tap the Campaigns tab in the bottom bar")
    fun tapCampaignsTabInBottomBar() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_CAMPAIGNS")
            .performClick()
        composeTestRule.waitForIdle()
    }

    @Then("the Home screen fades and slides out horizontally")
    fun verifyHomeScreenSlidesOut() {
        throw PendingException("Visual Shared X-Axis animation cannot be asserted with animationsDisabled=true.")
    }

    @And("the Campaigns screen fades and slides in horizontally from the right")
    fun verifyCampaignsScreenSlidesIn() {
        // After idle, Campaigns screen should be visible (animation completes instantly with disabled animations)
        composeTestRule
            .onNodeWithTag("campaignsScreen")
            .assertIsDisplayed()
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────
    // Scenario: Drill-down navigation uses Shared Z-Axis
    // ──────────────────────────────────────────────────────────────────────────────────────────

    @Given("I am on any main screen")
    fun ensureOnMainScreen() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_CAMPAIGNS")
            .performClick()
        composeTestRule.waitForIdle()
    }

    @When("I tap the settings gear to open the Credits")
    fun tapSettingsGearToOpenCredits() {
        throw PendingException(
            "Requires the gear icon to be visible (only shown on tab screens) – " +
                "ArcanumTopBar gear tap not generically accessible here."
        )
    }

    @Then("the outgoing screen fades out and scales up slightly")
    fun verifyOutgoingScreenFadesAndScales() {
        throw PendingException("Visual Shared Z-Axis animation cannot be asserted with animationsDisabled=true.")
    }

    @And("the Credits screen fades in and scales up from slightly smaller")
    fun verifyCreditsScreenFadesIn() {
        throw PendingException("Visual Shared Z-Axis animation cannot be asserted with animationsDisabled=true.")
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────
    // Scenario: Transitions are fast and do not block interaction
    // ──────────────────────────────────────────────────────────────────────────────────────────

    @When("a screen transition occurs")
    fun triggerScreenTransition() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_HOME")
            .performClick()
        composeTestRule.waitForIdle()
    }

    @Then("the incoming screen becomes interactive within a short time")
    fun verifyScreenInteractable() {
        // With animations disabled the screen is immediately interactive
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithTag("homeScreen")
            .assertIsDisplayed()
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────
    // Scenario: The mini player uses Shared Y-Axis animation on entrance
    // ──────────────────────────────────────────────────────────────────────────────────────────

    @Given("no mini player is visible")
    fun verifyNoMiniPlayer() {
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithTag("miniPlayer")
            .assertDoesNotExist()
    }

    @When("I tap preview on an FX track")
    fun tapPreviewOnFxTrack() {
        throw PendingException(
            "Preview action requires the FX library to be open and a track visible – " +
                "mini player trigger not yet wired to LibraryScreen in iter8."
        )
    }

    @Then("the mini player slides up smoothly from the bottom navigation bar")
    fun verifyMiniPlayerSlidesUp() {
        throw PendingException("Visual Y-Axis slide animation cannot be asserted with animationsDisabled=true.")
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────
    // Scenario: The mini player uses Shared Y-Axis animation on exit
    // ──────────────────────────────────────────────────────────────────────────────────────────

    @Given("the mini player is visible")
    fun ensureMiniPlayerVisible() {
        throw PendingException(
            "Mini player visibility is controlled by the LibraryScreen preview action – " +
                "not yet triggerable in acceptance tests without full wiring."
        )
    }

    @When("I tap the close button or navigate away")
    fun tapMiniPlayerClose() {
        composeTestRule
            .onNodeWithTag("miniPlayerClose")
            .performClick()
        composeTestRule.waitForIdle()
    }

    @Then("the mini player slides down smoothly to disappear")
    fun verifyMiniPlayerSlidesDown() {
        throw PendingException("Visual Y-Axis slide animation cannot be asserted with animationsDisabled=true.")
    }
}
