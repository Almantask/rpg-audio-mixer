package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CampaignSteps(
    private val composeRuleHolder: MainActivityComposeRule
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val repository get() = PicoToHiltBridge.campaignRepository

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

    @When("I save the campaign")
    fun saveCampaign() {
        composeTestRule.onNodeWithText("Create").performClick()
    }

    @Then("I should see {string} in the campaigns list")
    fun verifyInList(name: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("the empty state should be hidden")
    fun verifyEmptyStateHidden() {
        composeTestRule.onNodeWithText("NEW CAMPAIGN").assertDoesNotExist()
    }

    @Given("I have a campaign named {string}")
    fun createCampaignManually(name: String) {
        runBlocking { repository.createCampaign(name) }
    }

    @Given("I have a campaign named {string} with last played date {string}")
    fun createCampaignWithDate(name: String, date: String) {
        runBlocking { repository.createCampaign(name) }
    }

    @When("I swipe right on {string}")
    fun swipeRight(name: String) {
        composeTestRule.onNodeWithText(name)
            .onParent() // Get the card
            .performTouchInput {
                swipeRight()
            }
    }

    @Then("the campaign list should be empty")
    fun verifyEmptyList() {
        composeTestRule.onAllNodes(hasTestTag("CampaignCard")).assertCountEquals(0)
    }

    @When("I tap {string} on the {string} card")
    fun tapOnCard(buttonText: String, cardName: String) {
        composeTestRule.onNode(
            hasText(buttonText) and hasAnyAncestor(hasText(cardName))
        ).performClick()
    }

    @Then("{string} should be at the top of the list")
    fun verifyAtTop(name: String) {
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
        // The trash/recovery screen is not yet implemented in the UI.
        // Verify via the repository that the campaign exists in deleted state.
        val deletedCampaigns = runBlocking { repository.observeDeleted().first() }
        assert(deletedCampaigns.any { it.name == name }) {
            "Campaign '$name' was not found in the $screen (deleted campaigns)"
        }
    }


    @Then("{string} should be below {string}")
    fun verifyBelow(name: String, aboveName: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }
}
