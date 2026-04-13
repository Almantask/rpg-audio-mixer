package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Step definitions for manage_sessions.feature (@iter2).
 *
 * Covers:
 *  - Add a new session to a campaign
 *  - Sessions list is empty when a campaign has no sessions
 *  - Multiple sessions appear in the sessions list
 *  - Tapping a session opens its scene list
 *  - Swipe to move a session to the Trash
 */
class SessionSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val campaignRepository get() = PicoToHiltBridge.campaignRepository
    private val sessionRepository get() = PicoToHiltBridge.sessionRepository

    // ── State held between steps ──────────────────────────

    /** Tracks the campaignId created by setup steps, so navigation steps can tap the right card. */
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
        // Navigate to campaigns tab and open the campaign to arrive at sessions screen,
        // so subsequent "When I tap <session>" steps can find the session card.
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

    // ── When ──────────────────────────────────────────────

    @When("I open {string}")
    fun openCampaignOrSession(name: String) {
        // Ensure we're on Campaigns tab first
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").performClick()
        composeTestRule.waitForIdle()
        // Try to tap the item by name — works for both campaign names and session names
        // visible on the current screen. If the name is a session (not a campaign), first
        // tap the only available campaign card to navigate to the sessions screen.
        val matchingNodes = composeTestRule.onAllNodesWithText(name, ignoreCase = true)
            .fetchSemanticsNodes()
        if (matchingNodes.isNotEmpty()) {
            composeTestRule.onNodeWithText(name, ignoreCase = true).performClick()
        } else {
            // Not a campaign name — navigate into the first available campaign then find the session
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
        // After tapping a session card, we should be on the Session Scenes screen
        composeTestRule.onNodeWithTag("sessionSceneList").assertExists()
    }

    @Then("{string} is moved to the Trash")
    fun sessionMovedToTrash(sessionName: String) {
        // Verify via repository that the session is soft-deleted
        val deletedSessions = runBlocking { sessionRepository.observeDeleted().first() }
        assert(deletedSessions.any { it.name == sessionName }) {
            "Session '$sessionName' was not found in the deleted sessions"
        }
    }

    @Then("it is no longer in the sessions list")
    fun sessionNoLongerInList() {
        composeTestRule.onAllNodes(hasTestTag("SessionCard")).assertCountEquals(0)
    }
}
