package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
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
        composeTestRule.onNodeWithText(buttonText, ignoreCase = true).performClick()
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

    @Then("{string} should be below {string}")
    fun verifyBelow(name: String, aboveName: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }
}
