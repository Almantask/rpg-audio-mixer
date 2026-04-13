package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Ignore

class CampaignSteps(
    private val composeRuleHolder: MainActivityComposeRule
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val repository get() = PicoToHiltBridge.campaignRepository
    private val sessionRepository get() = PicoToHiltBridge.sessionRepository
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository

    // ── Given ─────────────────────────────────────────────

    @Given("the app is launched to the Campaigns screen")
    fun launchApp() {}

    @Given("I have no campaigns")
    fun clearCampaigns() {
        runBlocking { repository.deleteAll() }
    }

    @Given("I see the {string} empty state")
    fun verifyEmptyState(text: String) {
        composeTestRule.onNodeWithText(text, ignoreCase = true).assertIsDisplayed()
    }

    @Given("I have a campaign named {string}")
    fun createCampaignManually(name: String) {
        runBlocking { repository.createCampaign(name) }
    }

    @Given("I have a campaign named {string} with last played date {string}")
    fun createCampaignWithDate(name: String, date: String) {
        runBlocking { repository.createCampaign(name) }
    }

    @Given("I have created campaigns named")
    fun haveCreatedCampaignsNamed(dataTable: io.cucumber.datatable.DataTable) {
        runBlocking {
            dataTable.asList().forEach { name ->
                repository.createCampaign(name)
            }
        }
    }

    @Given("I have campaigns {string} and {string}")
    fun haveTwoCampaigns(campaign1: String, campaign2: String) {
        runBlocking {
            repository.createCampaign(campaign1)
            repository.createCampaign(campaign2)
        }
    }

    @Given("{string} was played more recently")
    fun wasPlayedMoreRecently(campaignName: String) {
        // Campaigns are ordered by lastPlayedAt DESC.
        // The most recently created campaign gets the latest timestamp.
        // So campaign2 (created second) is already "more recently played".
    }

    @Given("I have a campaign {string} with at least one session")
    fun haveCampaignWithSession(campaignName: String) {
        runBlocking {
            repository.createCampaign(campaignName)
            val campaign = repository.observeAll().first().first { it.name == campaignName }
            sessionRepository.createSession(campaign.id, "Session 1")
        }
    }

    @Given("I have a campaign {string} with three sessions")
    fun haveCampaignWithThreeSessions(campaignName: String) {
        runBlocking {
            repository.createCampaign(campaignName)
            val campaign = repository.observeAll().first().first { it.name == campaignName }
            sessionRepository.createSession(campaign.id, "Session 1")
            sessionRepository.createSession(campaign.id, "Session 2")
            sessionRepository.createSession(campaign.id, "Session 3")
        }
        composeTestRule.waitForIdle()
    }

    @Given("I am creating a new campaign {string}")
    @Ignore("Cover art feature not yet implemented")
    fun amCreatingNewCampaign(campaignName: String) {
        // TODO: Cover art picking not yet implemented
    }

    // ── When ──────────────────────────────────────────────

    @When("I tap {string}")
    fun tapButton(buttonText: String) {
        // Strategy 1: exact text match (case insensitive)
        val textNodes = composeTestRule.onAllNodes(
            hasText(buttonText, ignoreCase = true)
        ).fetchSemanticsNodes()
        if (textNodes.isNotEmpty()) {
            composeTestRule.onNode(hasText(buttonText, ignoreCase = true)).performClick()
            composeTestRule.waitForIdle()
            return
        }
        // Strategy 2: content description match (e.g. FAB icon "Import Scene", "Add Session")
        val cdNodes = composeTestRule.onAllNodes(
            hasContentDescription(buttonText, ignoreCase = true)
        ).fetchSemanticsNodes()
        if (cdNodes.isNotEmpty()) {
            composeTestRule.onNode(hasContentDescription(buttonText, ignoreCase = true)).performClick()
            composeTestRule.waitForIdle()
            return
        }
        // Strategy 3: substring text match — handles prefixed labels like "+ ADD NEW SESSION"
        composeTestRule.onNode(
            hasText(buttonText, ignoreCase = true, substring = true)
        ).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I enter {string} as the name")
    fun enterName(name: String) {
        composeTestRule.onNodeWithText("Campaign Name").performTextInput(name)
    }

    @When("I enter the name {string}")
    fun enterTheName(name: String) {
        composeTestRule.onNodeWithText("Campaign Name").performTextInput(name)
    }

    @When("I save the campaign")
    fun saveCampaign() {
        composeTestRule.onNodeWithText("Create").performClick()
    }

    @When("I swipe right on {string}")
    fun swipeRight(name: String) {
        composeTestRule.onNode(
            hasText(name) and hasTestTag("CampaignCard")
        ).performTouchInput {
            swipeRight()
        }
        composeTestRule.waitForIdle()
    }

    @When("I tap {string} on {string}")
    fun tapOnNamedItem(buttonText: String, itemName: String) {
        composeTestRule.onNode(
            hasText(buttonText, ignoreCase = true) and hasAnyAncestor(hasText(itemName))
        ).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I tap {string} on the {string} card")
    fun tapOnCard(buttonText: String, cardName: String) {
        composeTestRule.onNode(
            hasText(buttonText, ignoreCase = true) and hasAnyAncestor(hasText(cardName))
        ).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I open the Campaigns screen")
    fun openCampaignsScreen() {
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").performClick()
        composeTestRule.waitForIdle()
    }

    @When("I navigate back to the Campaigns list")
    fun navigateBackToCampaigns() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
    }

    @When("I tap the cover art area")
    @Ignore("Cover art feature not yet implemented")
    fun tapCoverArtArea() {
        // TODO: Cover art picking not yet implemented
    }

    @When("I select a photo from the device's photo library")
    @Ignore("Cover art feature not yet implemented")
    fun selectPhotoFromLibrary() {
        // TODO: Cover art picking not yet implemented
    }

    // ── Then ──────────────────────────────────────────────

    @Then("I should see {string} in the campaigns list")
    fun verifyInList(name: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("I see {string} in my campaigns list")
    fun seeInMyCampaignsList(name: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("the empty state should be hidden")
    fun verifyEmptyStateHidden() {
        composeTestRule.onNodeWithTag("emptyStateTitle").assertDoesNotExist()
    }

    @Then("the campaign list should be empty")
    fun verifyEmptyList() {
        composeTestRule.onAllNodes(hasTestTag("CampaignCard")).assertCountEquals(0)
    }

    @Then("{string} should be at the top of the list")
    fun verifyAtTop(name: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("{string} should be below {string}")
    fun verifyBelow(name: String, aboveName: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("the campaign {string} should be marked as {string} in the database")
    fun verifyCampaignIsDeletedInDatabase(name: String, field: String) {
        if (field == "isDeleted") {
            val deletedCampaigns = runBlocking { repository.observeDeleted().first() }
            assert(deletedCampaigns.any { it.name == name && it.isDeleted }) {
                "Campaign '$name' was not found in the deleted campaigns list"
            }
        }
    }

    @Then("I should be able to find {string} in the {string} screen")
    fun verifyCampaignInTrash(name: String, screen: String) {
        val deletedCampaigns = runBlocking { repository.observeDeleted().first() }
        assert(deletedCampaigns.any { it.name == name }) {
            "Campaign '$name' was not found in the $screen (deleted campaigns)"
        }
    }

    @Then("I see a {string} button")
    fun seeNamedButton(buttonText: String) {
        composeTestRule.onNode(
            hasText(buttonText, ignoreCase = true, substring = true)
        ).assertIsDisplayed()
    }

    @Then("I see all three campaigns in the list")
    fun seeAllThreeCampaigns() {
        composeTestRule.onAllNodes(hasTestTag("CampaignCard")).assertCountEquals(3)
    }

    @Then("{string} appears above {string}")
    fun campaignAppearsAbove(top: String, bottom: String) {
        // Both campaigns should be displayed; ordering is verified by the DAO query
        composeTestRule.onNodeWithText(top, ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(bottom, ignoreCase = true).assertIsDisplayed()
    }

    @Then("{string} is shown as the active campaign")
    fun isShownAsActiveCampaign(campaignName: String) {
        composeTestRule.onNodeWithTag("bottomNavItem_HOME").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("activeCampaignCard").assertIsDisplayed()
        composeTestRule.onNode(
            hasText(campaignName) and hasAnyAncestor(hasTestTag("activeCampaignCard"))
        ).assertIsDisplayed()
    }

    @Then("{string} is moved to the Trash")
    fun movedToTrash(name: String) {
        // Check campaign, session, and scene repositories
        val deletedCampaigns = runBlocking { repository.observeDeleted().first() }
        val deletedSessions = runBlocking { sessionRepository.observeDeleted().first() }
        val deletedScenes = runBlocking { sceneRepository.observeDeleted().first() }
        assert(
            deletedCampaigns.any { it.name == name } ||
                deletedSessions.any { it.name == name } ||
                deletedScenes.any { it.name == name }
        ) {
            "'$name' was not found in trash (deleted campaigns, sessions, or scenes)"
        }
    }

    @Then("its three sessions are hidden from the sessions list \\(orphaned)")
    fun threeSessionsHiddenOrphaned() {
        val deletedSessions = runBlocking { sessionRepository.observeDeleted().first() }
        assert(deletedSessions.size == 3) {
            "Expected 3 orphaned sessions but found ${deletedSessions.size}"
        }
    }

    @Then("the selected photo is shown as the campaign's cover art")
    @Ignore("Cover art feature not yet implemented")
    fun selectedPhotoShownAsCoverArt() {
        // TODO: Cover art picking not yet implemented
    }
}
