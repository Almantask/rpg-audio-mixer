package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class NavigationShellSteps(
    private val activityRule: MainActivityComposeRule,
) {

    // -------------------------------------------------------------------------
    // Given steps (test state setup)
    // -------------------------------------------------------------------------

    @Given("I am on the Home screen")
    fun iAmOnTheHomeScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Home").performClick()
        composeRule.waitForIdle()
    }

    @Given("I am on the Library screen")
    fun iAmOnTheLibraryScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Library").performClick()
        composeRule.waitForIdle()
    }

    @Given("I am on the SCENES tab")
    fun iAmOnTheScenesTab() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Scenes").performClick()
        composeRule.waitForIdle()
    }

    // -------------------------------------------------------------------------
    // When steps (actions)
    // -------------------------------------------------------------------------

    @When("I open the app")
    fun iOpenTheApp() {
        // App is already launched by the rule
        activityRule.composeRule.waitForIdle()
    }

    @When("I tap the HOME tab")
    fun iTapTheHomeTab() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Home").performClick()
        composeRule.waitForIdle()
    }

    @When("I tap the CAMPAIGNS tab")
    fun iTapTheCampaignsTab() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Campaigns").performClick()
        composeRule.waitForIdle()
    }

    @When("I tap the SCENES tab")
    fun iTapTheScenesTab() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Scenes").performClick()
        composeRule.waitForIdle()
    }

    @When("I tap the LIBRARY tab")
    fun iTapTheLibraryTab() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Library").performClick()
        composeRule.waitForIdle()
    }

    // -------------------------------------------------------------------------
    // Then steps (assertions)
    // -------------------------------------------------------------------------

    @Then("I see four tabs: HOME, CAMPAIGNS, SCENES, and LIBRARY")
    fun iSeeFourTabsHomeCampaignsScenesAndLibrary() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Home").assertIsDisplayed()
        composeRule.onNodeWithTag("BottomNav_Campaigns").assertIsDisplayed()
        composeRule.onNodeWithTag("BottomNav_Scenes").assertIsDisplayed()
        composeRule.onNodeWithTag("BottomNav_Library").assertIsDisplayed()
    }

    @Then("I see the Home screen")
    fun iSeeTheHomeScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Then("I see the Campaigns list screen")
    fun iSeeTheCampaignsListScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithText("Campaigns").assertIsDisplayed()
    }

    @Then("I see the SCENES tab screen")
    fun iSeeTheScenesTabScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithText("Scenes").assertIsDisplayed()
    }

    @Then("I see the Audio Library screen")
    fun iSeeTheAudioLibraryScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithText("Library").assertIsDisplayed()
    }

    @Then("the SCENES tab icon appears highlighted in gold")
    fun theScenesTabIconAppearsHighlightedInGold() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Scenes").assertIsSelected()
    }

    @Then("the other three tabs appear inactive")
    fun theOtherThreeTabsAppearInactive() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Home").assertIsNotSelected()
        composeRule.onNodeWithTag("BottomNav_Campaigns").assertIsNotSelected()
        composeRule.onNodeWithTag("BottomNav_Library").assertIsNotSelected()
    }
}
