package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*

/**
 * Step definitions for bottom_navigation.feature (@iter0).
 *
 * Also provides shared "Given I am on the … screen/tab" steps
 * that other features (e.g. view_credits) rely on.
 */
class NavigationShellSteps(
    private val composeRuleHolder: MainActivityComposeRule
) {
    private val composeTestRule get() = composeRuleHolder.composeRule

    // ── Helpers ───────────────────────────────────────────

    private fun tapTab(tabName: String) {
        composeTestRule
            .onNodeWithTag("bottomNavItem_${tabName.uppercase()}")
            .performClick()
    }

    // ── Given (shared navigation) ─────────────────────────

    @Given("I am on the Home screen")
    fun navigateToHomeScreen() {
        tapTab("HOME")
        verifyHomeScreen()
    }

    @Given("I am on the Campaigns screen")
    fun navigateToCampaignsScreen() {
        tapTab("CAMPAIGNS")
        verifyCampaignsScreen()
    }

    @Given("I am on the Library screen")
    fun navigateToLibraryScreen() {
        tapTab("LIBRARY")
        verifyLibraryScreen()
    }

    @Given("I am on the SCENES tab")
    fun navigateToScenesTab() {
        tapTab("SCENES")
        verifyScenesScreen()
    }

    // ── When ──────────────────────────────────────────────

    @When("I open the app")
    fun openTheApp() {
        // App is already launched
    }

    @When("I tap the HOME tab")
    fun tapHomeTab() = tapTab("HOME")

    @When("I tap the CAMPAIGNS tab")
    fun tapCampaignsTab() = tapTab("CAMPAIGNS")

    @When("I tap the SCENES tab")
    fun tapScenesTab() = tapTab("SCENES")

    @When("I tap the LIBRARY tab")
    fun tapLibraryTab() = tapTab("LIBRARY")

    // ── Then ──────────────────────────────────────────────

    @Then("I see four tabs: HOME, CAMPAIGNS, SCENES, and LIBRARY")
    fun verifyFourTabsVisible() {
        composeTestRule.onNodeWithTag("bottomNavItem_HOME").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottomNavItem_SCENES").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottomNavItem_LIBRARY").assertIsDisplayed()
    }

    @Then("I see the Home screen")
    fun verifyHomeScreen() {
        composeTestRule.onNodeWithTag("bottomNavItem_HOME").assertIsSelected()
        // verify unique screen content
        composeTestRule.onNodeWithTag("homeScreen").assertIsDisplayed()
    }

    @Then("I see the Campaigns list screen")
    fun verifyCampaignsScreen() {
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").assertIsSelected()
        composeTestRule.onNodeWithTag("campaignsScreen").assertIsDisplayed()
    }

    @Then("I see the SCENES tab screen")
    fun verifyScenesScreen() {
        composeTestRule.onNodeWithTag("bottomNavItem_SCENES").assertIsSelected()
        composeTestRule.onNodeWithTag("soundboardScreen").assertIsDisplayed()
    }

    @Then("I see the Audio Library screen")
    fun verifyLibraryScreen() {
        composeTestRule.onNodeWithTag("libraryScreen").assertIsDisplayed()
    }

    @Then("the SCENES tab icon appears highlighted in gold")
    fun verifyScenesTabHighlighted() {
        composeTestRule.onNodeWithTag("bottomNavItem_SCENES").assertIsSelected()
    }

    @Then("the other three tabs appear inactive")
    fun verifyOtherTabsInactive() {
        composeTestRule.onNodeWithTag("bottomNavItem_HOME").assertIsNotSelected()
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").assertIsNotSelected()
        composeTestRule.onNodeWithTag("bottomNavItem_LIBRARY").assertIsNotSelected()
    }
}
