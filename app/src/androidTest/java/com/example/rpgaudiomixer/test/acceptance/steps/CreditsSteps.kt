package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*

/**
 * Step definitions for view_credits.feature (@iter0 and @iter5).
 *
 * Covers gear-icon visibility, Credits screen content (version, docs link),
 * back-arrow navigation, and Restore Recent Deletes navigation.
 * Shared "Given I am on …" steps live in [NavigationShellSteps].
 */
class CreditsSteps(
    private val composeRuleHolder: MainActivityComposeRule
) {
    private val composeTestRule get() = composeRuleHolder.composeRule

    // ── Given ─────────────────────────────────────────────

    @Given("I navigated to Credits from the Scenes screen")
    fun navigateToCreditsFromScenes() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_SCENES")
            .performClick()
        composeTestRule
            .onNodeWithTag("topBarGear")
            .performClick()
    }

    @Given("I am on the Credits screen")
    fun navigateToCreditsScreen() {
        composeTestRule
            .onNodeWithTag("topBarGear")
            .performClick()
        composeTestRule.waitForIdle()
    }

    // ── When ──────────────────────────────────────────────

    @When("I tap the gear icon")
    fun tapGearIcon() {
        composeTestRule
            .onNodeWithTag("topBarGear")
            .performClick()
    }

    @When("I open the Credits screen")
    fun openCreditsScreen() {
        composeTestRule
            .onNodeWithTag("topBarGear")
            .performClick()
    }

    @When("I tap the back arrow")
    fun tapBackArrow() {
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()
    }

    @When("I tap \"Restore Recent Deletes\"")
    fun tapRestoreRecentDeletes() {
        composeTestRule
            .onNodeWithTag("creditsRestoreDeletes")
            .performClick()
        composeTestRule.waitForIdle()
    }

    // ── Then ──────────────────────────────────────────────

    @Then("I see the gear icon in the top bar")
    fun verifyGearIconVisible() {
        composeTestRule
            .onNodeWithTag("topBarGear")
            .assertIsDisplayed()
    }

    @Then("I see the {string} heading on the Credits screen")
    fun verifyCreditsHeading(heading: String) {
        composeTestRule
            .onNodeWithTag("creditsScreen")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(heading)
            .assertIsDisplayed()
    }

    @Then("I see the app version number")
    fun verifyAppVersion() {
        composeTestRule
            .onNodeWithTag("creditsVersion")
            .assertIsDisplayed()
    }

    @Then("I see the documentation link")
    fun verifyDocumentationLink() {
        composeTestRule
            .onNodeWithTag("creditsLinkDocs")
            .assertIsDisplayed()
    }

    @Then("I am back on the Scenes screen")
    fun verifyBackOnScenesScreen() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_SCENES")
            .assertIsSelected()
        composeTestRule
            .onNodeWithTag("soundboardScreen")
            .assertIsDisplayed()
    }

    @Then("I see the {string} button")
    fun seeTheNamedButton(buttonText: String) {
        composeTestRule
            .onNode(hasText(buttonText, ignoreCase = true, substring = true))
            .assertIsDisplayed()
    }

    @Then("I am navigated to the \"Recent Deletes\" \\(Trash\\) screen")
    fun navigatedToTrashScreen() {
        composeTestRule
            .onNodeWithTag("trashScreen")
            .assertIsDisplayed()
    }
}

