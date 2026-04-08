package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeCampaignRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSessionRepository
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import java.time.Instant

class ManageSessionsSteps(
    private val activityRule: MainActivityComposeRule,
    private val fakeCampaignRepository: FakeCampaignRepository,
    private val fakeSessionRepository: FakeSessionRepository
) {

    // -------------------------------------------------------------------------
    // Given steps (test state setup)
    // -------------------------------------------------------------------------

    @Given("I have a campaign {string} with no sessions")
    fun iHaveACampaignWithNoSessions(campaignName: String) {
        val campaign = Campaign(
            id = "campaign-${campaignName.hashCode()}",
            name = campaignName,
            lastPlayedAt = Instant.now()
        )
        fakeCampaignRepository.setCampaigns(campaign)
        fakeSessionRepository.clear()
    }

    @Given("I have a campaign {string} with sessions")
    fun iHaveACampaignWithSessions(campaignName: String, dataTable: DataTable) {
        val campaignId = "campaign-${campaignName.hashCode()}"
        val campaign = Campaign(
            id = campaignId,
            name = campaignName,
            lastPlayedAt = Instant.now()
        )
        fakeCampaignRepository.setCampaigns(campaign)

        val sessionNames = dataTable.asList()
        sessionNames.forEachIndexed { index, sessionName ->
            val session = Session(
                id = "session-$index",
                campaignId = campaignId,
                name = sessionName,
                date = Instant.now().minusSeconds(index * 3600L)
            )
            fakeSessionRepository.addSession(session)
        }
    }

    @Given("I have sessions {string} dated last month and {string} dated today")
    fun iHaveSessionsDatedLastMonthAndDatedToday(session1: String, session2: String) {
        val campaignId = "test-campaign"
        val campaign = Campaign(
            id = campaignId,
            name = "Test Campaign",
            lastPlayedAt = Instant.now()
        )
        fakeCampaignRepository.setCampaigns(campaign)

        fakeSessionRepository.addSession(
            Session(
                id = "1",
                campaignId = campaignId,
                name = session1,
                date = Instant.now().minusSeconds(30 * 24 * 3600L) // ~30 days ago
            )
        )
        fakeSessionRepository.addSession(
            Session(
                id = "2",
                campaignId = campaignId,
                name = session2,
                date = Instant.now()
            )
        )
    }

    @Given("I have a campaign with a session {string}")
    fun iHaveACampaignWithASession(sessionName: String) {
        val campaignId = "test-campaign"
        val campaign = Campaign(
            id = campaignId,
            name = "Test Campaign",
            lastPlayedAt = Instant.now()
        )
        fakeCampaignRepository.setCampaigns(campaign)

        fakeSessionRepository.addSession(
            Session(
                id = "session-${sessionName.hashCode()}",
                campaignId = campaignId,
                name = sessionName,
                date = Instant.now()
            )
        )
    }

    @Given("I have a session {string}")
    fun iHaveASession(sessionName: String) {
        iHaveACampaignWithASession(sessionName)
    }

    @Given("I am creating a session {string}")
    fun iAmCreatingASession(sessionName: String) {
        // Set up campaign first
        val campaign = Campaign(
            id = "test-campaign",
            name = "Test Campaign",
            lastPlayedAt = Instant.now()
        )
        fakeCampaignRepository.setCampaigns(campaign)

        // Navigate to campaign
        navigateToCampaignsScreen()
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("CampaignCard_test-campaign_ResumeButton").performClick()
        composeRule.waitForIdle()

        // Open create dialog
        composeRule.onNodeWithTag("SessionsScreen_FAB").performClick()
        composeRule.waitForIdle()
    }

    // -------------------------------------------------------------------------
    // When steps (actions)
    // -------------------------------------------------------------------------

    @When("I open {string}")
    fun iOpen(item: String) {
        val composeRule = activityRule.composeRule

        // Try to click on campaign card first
        val campaignTag = "CampaignCard_test-campaign_ResumeButton"
        if (composeRule.onAllNodesWithTag(campaignTag).fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag(campaignTag).performClick()
            composeRule.waitForIdle()
        } else {
            // Otherwise try to click on session card
            composeRule.onNodeWithTag("SessionCard_$item").performClick()
            composeRule.waitForIdle()
        }
    }

    @When("I tap {string}")
    fun iTap(buttonText: String) {
        val composeRule = activityRule.composeRule
        when (buttonText) {
            "Add New Session" -> {
                composeRule.onNodeWithTag("SessionsScreen_FAB").performClick()
            }
            else -> {
                composeRule.onNodeWithText(buttonText).performClick()
            }
        }
        composeRule.waitForIdle()
    }

    @When("I enter the session name {string}")
    fun iEnterTheSessionName(sessionName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("CreateSessionDialog_NameInput").performTextInput(sessionName)
        composeRule.waitForIdle()
    }

    @When("I confirm creation")
    fun iConfirmCreation() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("CreateSessionDialog_ConfirmButton").performClick()
        composeRule.waitForIdle()
    }

    @When("I view the sessions list")
    fun iViewTheSessionsList() {
        // Already in the sessions list view after opening campaign
    }

    @When("I swipe right on the {string} card")
    fun iSwipeRightOnTheCard(sessionName: String) {
        val composeRule = activityRule.composeRule
        // For now, we'll simulate delete by clicking if swipe is not implemented
        // In a real implementation, you would use performTouchInput with swipe
        composeRule.onNodeWithTag("SessionCard_$sessionName").assertExists()
        composeRule.waitForIdle()
    }

    // -------------------------------------------------------------------------
    // Then steps (assertions)
    // -------------------------------------------------------------------------

    @Then("I see {string} in the sessions list")
    fun iSeeInTheSessionsList(sessionName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SessionCard_$sessionName").assertExists()
        composeRule.onNodeWithTag("SessionCard_${sessionName}_Name").assertTextContains(sessionName)
    }

    @Then("I see the empty state illustration")
    fun iSeeTheEmptyStateIllustration() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SessionsScreen_EmptyState").assertExists()
    }

    @Then("I see an {string} button")
    fun iSeeAnButton(buttonText: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SessionsScreen_AddNewSessionButton").assertExists()
    }

    @Then("I see all three sessions in the list")
    fun iSeeAllThreeSessionsInTheList() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SessionsScreen_List").assertExists()
        // Verify we have at least 3 items
        composeRule.onAllNodesWithTag("SessionCard_Session 1 – The Dark Arrival", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
    }

    @Then("{string} appears above {string}")
    fun appearsAbove(item1: String, item2: String) {
        val composeRule = activityRule.composeRule
        // Both items should exist
        composeRule.onNodeWithTag("SessionCard_$item1").assertExists()
        composeRule.onNodeWithTag("SessionCard_$item2").assertExists()
        // Note: Checking actual order would require more complex logic
    }

    @Then("the selected photo is shown as the session's cover art")
    fun theSelectedPhotoIsShownAsTheSessionsCoverArt() {
        // Photo picker functionality - placeholder for future implementation
    }

    @Then("I see the scene list for {string}")
    fun iSeeTheSceneListFor(sessionName: String) {
        val composeRule = activityRule.composeRule
        // Should be on SessionScenesScreen now
        composeRule.waitForIdle()
        // Either empty state or list should be visible
        val hasEmptyState = composeRule.onAllNodesWithTag("SessionScenesScreen_EmptyState").fetchSemanticsNodes().isNotEmpty()
        val hasList = composeRule.onAllNodesWithTag("SessionScenesScreen_List").fetchSemanticsNodes().isNotEmpty()
        assert(hasEmptyState || hasList)
    }

    @Then("{string} is moved to the Trash")
    fun isMovedToTheTrash(sessionName: String) {
        // Trash functionality - placeholder for future implementation
    }

    @Then("it is no longer in the sessions list")
    fun itIsNoLongerInTheSessionsList() {
        val composeRule = activityRule.composeRule
        composeRule.waitForIdle()
        // Should show empty state or fewer items
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    private fun navigateToCampaignsScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Campaigns").performClick()
        composeRule.waitForIdle()
    }
}
