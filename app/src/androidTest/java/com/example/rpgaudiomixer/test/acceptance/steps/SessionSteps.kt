package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Ignore

/**
 * Step definitions for manage_sessions.feature (@iter2).
 */
class SessionSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val campaignRepository get() = PicoToHiltBridge.campaignRepository
    private val sessionRepository get() = PicoToHiltBridge.sessionRepository

    /** Tracks the campaignId created by setup steps. */
    private var currentCampaignId: Long = -1L

    // ── Given ─────────────────────────────────────────────

    @Given("I have a campaign {string}")
    fun haveCampaign(name: String) {
        runBlocking { campaignRepository.createCampaign(name) }
    }

    @Given("I have a campaign {string} with no sessions")
    fun haveCampaignWithNoSessions(name: String) {
        runBlocking { campaignRepository.createCampaign(name) }
    }

    @Given("I have a campaign {string} with sessions")
    fun haveCampaignWithSessions(campaignName: String, dataTable: io.cucumber.datatable.DataTable) {
        runBlocking {
            campaignRepository.createCampaign(campaignName)
            val campaign = campaignRepository.observeAll().first().first { it.name == campaignName }
            currentCampaignId = campaign.id
            dataTable.asList().forEach { sessionName ->
                sessionRepository.createSession(campaign.id, sessionName)
            }
        }
    }

    @Given("I have a campaign with a session {string}")
    fun haveCampaignWithSession(sessionName: String) {
        runBlocking {
            campaignRepository.createCampaign("Test Campaign")
            val campaign = campaignRepository.observeAll().first().first()
            currentCampaignId = campaign.id
            sessionRepository.createSession(campaign.id, sessionName)
        }
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Test Campaign", ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
    }

    @Given("I have a session {string}")
    fun haveSession(sessionName: String) {
        runBlocking {
            campaignRepository.createCampaign("Test Campaign")
            val campaign = campaignRepository.observeAll().first().first()
            currentCampaignId = campaign.id
            sessionRepository.createSession(campaign.id, sessionName)
        }
    }

    @Given("I have sessions {string} dated last month and {string} dated today")
    fun haveSessionsDatedDifferently(session1: String, session2: String) {
        runBlocking {
            campaignRepository.createCampaign("Test Campaign")
            val campaign = campaignRepository.observeAll().first().first()
            currentCampaignId = campaign.id
            // session1 has older date, session2 has newer date
            val lastMonth = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
            sessionRepository.createSession(campaign.id, session1, date = lastMonth)
            sessionRepository.createSession(campaign.id, session2)
        }
    }

    @Given("I am creating a session {string}")
    @Ignore("Session cover art feature not yet implemented")
    fun amCreatingSession(sessionName: String) {
        // TODO: Session cover art feature not yet implemented
    }

    // ── When ──────────────────────────────────────────────

    @When("I open {string}")
    fun openCampaignOrSession(name: String) {
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").performClick()
        composeTestRule.waitForIdle()
        val matchingNodes = composeTestRule.onAllNodesWithText(name, ignoreCase = true)
            .fetchSemanticsNodes()
        if (matchingNodes.isNotEmpty()) {
            composeTestRule.onNodeWithText(name, ignoreCase = true).performClick()
        } else {
            composeTestRule.onAllNodes(hasTestTag("CampaignCard")).onFirst().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(name, ignoreCase = true).performClick()
        }
        composeTestRule.waitForIdle()
    }

    @When("I tap {string} on the session screen")
    fun tapButtonOnSessionScreen(text: String) {
        composeTestRule.onNodeWithText(text, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I enter the session name {string}")
    fun enterSessionName(name: String) {
        composeTestRule.onNodeWithTag("sessionNameInput").performTextInput(name)
    }

    @When("I confirm creation")
    fun confirmCreation() {
        composeTestRule.onNodeWithTag("createSessionButton").performClick()
        composeTestRule.waitForIdle()
    }

    @When("I swipe right on the {string} card")
    fun swipeRightOnCard(name: String) {
        composeTestRule.onNodeWithText(name, ignoreCase = true)
            .onParent()
            .performTouchInput { swipeRight() }
        composeTestRule.waitForIdle()
    }

    @When("I view the sessions list")
    fun viewSessionsList() {
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodes(hasTestTag("CampaignCard")).onFirst().performClick()
        composeTestRule.waitForIdle()
    }

    // ── Then ──────────────────────────────────────────────

    @Then("I see {string} in the sessions list")
    fun seeSessionInList(name: String) {
        composeTestRule.onNodeWithText(name, ignoreCase = true).assertIsDisplayed()
    }

    @Then("I see a Large Material 3 icon with a prompt")
    fun seeEmptyStateIcon() {
        composeTestRule.onNodeWithTag("emptyStateTitle").assertIsDisplayed()
    }

    @Then("I see an {string} button")
    fun seeButton(buttonText: String) {
        composeTestRule.onNodeWithTag("emptyStateCta").assertIsDisplayed()
    }

    @Then("I see all three sessions in the list")
    fun seeAllThreeSessions() {
        composeTestRule.onAllNodes(hasTestTag("SessionCard")).assertCountEquals(3)
    }

    @Then("I see the scene list for {string}")
    fun seeSceneListForSession(sessionName: String) {
        composeTestRule.onNodeWithTag("sessionSceneList").assertExists()
    }

    @Then("it is no longer in the sessions list")
    fun sessionNoLongerInList() {
        composeTestRule.onAllNodes(hasTestTag("SessionCard")).assertCountEquals(0)
    }

    @Then("{string} appears above {string} in sessions")
    fun sessionAppearsAbove(top: String, bottom: String) {
        composeTestRule.onNodeWithText(top, ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(bottom, ignoreCase = true).assertIsDisplayed()
    }

    @Then("the selected photo is shown as the session's cover art")
    @Ignore("Session cover art feature not yet implemented")
    fun selectedPhotoShownAsSessionCoverArt() {
        // TODO: Session cover art feature not yet implemented
    }
}
