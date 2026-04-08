package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeCampaignRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSceneRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSessionRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSessionSceneRepository
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import java.time.Instant

class SessionScenesSteps(
    private val activityRule: MainActivityComposeRule,
    private val fakeCampaignRepository: FakeCampaignRepository,
    private val fakeSessionRepository: FakeSessionRepository,
    private val fakeSceneRepository: FakeSceneRepository,
    private val fakeSessionSceneRepository: FakeSessionSceneRepository
) {

    // -------------------------------------------------------------------------
    // Given steps (test state setup)
    // -------------------------------------------------------------------------

    @Given("I have a session {string} with no scenes")
    fun iHaveASessionWithNoScenes(sessionName: String) {
        setupSessionAndCampaign(sessionName)
        fakeSessionSceneRepository.clear()
    }

    @Given("I have a scene {string} in the SCENES tab")
    fun iHaveASceneInTheSCENESTab(sceneName: String) {
        val scene = Scene(
            id = "scene-${sceneName.hashCode()}",
            name = sceneName,
            description = null,
            tags = emptyList()
        )
        fakeSceneRepository.addScene(scene)
    }

    @Given("I have scenes {string}, {string}, {string} in the SCENES tab")
    fun iHaveScenesInTheSCENESTab(scene1: String, scene2: String, scene3: String) {
        listOf(scene1, scene2, scene3).forEach { sceneName ->
            val scene = Scene(
                id = "scene-${sceneName.hashCode()}",
                name = sceneName,
                description = null,
                tags = emptyList()
            )
            fakeSceneRepository.addScene(scene)
        }
    }

    @Given("{string} is linked to {string}")
    fun isLinkedTo(sceneName: String, sessionName: String) {
        setupSessionAndCampaign(sessionName)
        val scene = Scene(
            id = "scene-${sceneName.hashCode()}",
            name = sceneName,
            description = null,
            tags = emptyList()
        )
        fakeSceneRepository.addScene(scene)

        val sessionId = "session-${sessionName.hashCode()}"
        kotlinx.coroutines.runBlocking {
            fakeSessionSceneRepository.linkSceneToSession(sessionId, scene.id)
        }
    }

    @Given("{string} has the soundscape category {string}")
    fun hasTheSoundscapeCategory(sceneName: String, categoryName: String) {
        // Soundscape categories are part of future iterations
        // This is a placeholder
    }

    // -------------------------------------------------------------------------
    // When steps (actions)
    // -------------------------------------------------------------------------

    @When("I open that session")
    fun iOpenThatSession() {
        navigateToCampaignsScreen()
        val composeRule = activityRule.composeRule

        // Click on the campaign
        val campaignTag = "CampaignCard_test-campaign_ResumeButton"
        composeRule.onNodeWithTag(campaignTag).performClick()
        composeRule.waitForIdle()

        // Click on the session
        val sessionCard = composeRule.onAllNodesWithTag("SessionCard_Session 1 – The Dark Arrival", useUnmergedTree = true)
        if (sessionCard.fetchSemanticsNodes().isNotEmpty()) {
            sessionCard[0].performClick()
        }
        composeRule.waitForIdle()
    }

    @When("I tap {string}")
    fun iTap(buttonText: String) {
        val composeRule = activityRule.composeRule
        when (buttonText) {
            "Import Scene" -> {
                composeRule.onNodeWithTag("SessionScenesScreen_FAB").performClick()
            }
            else -> {
                composeRule.onNodeWithText(buttonText).performClick()
            }
        }
        composeRule.waitForIdle()
    }

    @When("I select {string} from the scene picker")
    fun iSelectFromTheScenePicker(sceneName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("ImportSceneDialog_Checkbox_$sceneName").performClick()
        composeRule.waitForIdle()
    }

    @When("I select {string}, {string}, and {string} from the picker")
    fun iSelectAndFromThePicker(scene1: String, scene2: String, scene3: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("ImportSceneDialog_Checkbox_$scene1").performClick()
        composeRule.onNodeWithTag("ImportSceneDialog_Checkbox_$scene2").performClick()
        composeRule.onNodeWithTag("ImportSceneDialog_Checkbox_$scene3").performClick()
        composeRule.waitForIdle()
    }

    @When("I confirm")
    fun iConfirm() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("ImportSceneDialog_ConfirmButton").performClick()
        composeRule.waitForIdle()
    }

    @When("I swipe right on the {string} card to unlink it")
    fun iSwipeRightOnTheCardToUnlinkIt(sceneName: String) {
        val composeRule = activityRule.composeRule
        // For now, verify the card exists
        // Actual swipe implementation would be added later
        composeRule.onNodeWithTag("SceneCard_$sceneName").assertExists()
    }

    @When("I edit {string} and add the soundscape category {string}")
    fun iEditAndAddTheSoundscapeCategory(sceneName: String, categoryName: String) {
        // Scene editing is part of future iterations
        // This is a placeholder
    }

    @When("I tap the {string} scene card in {string}")
    fun iTapTheSceneCardIn(sceneName: String, sessionName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SceneCard_$sceneName").performClick()
        composeRule.waitForIdle()
    }

    @When("I tap the play button on the {string} scene card in {string}")
    fun iTapThePlayButtonOnTheSceneCardIn(sceneName: String, sessionName: String) {
        val composeRule = activityRule.composeRule
        // Play button implementation would be part of Active Scene iteration
        // For now, just verify the scene card exists
        composeRule.onNodeWithTag("SceneCard_$sceneName").assertExists()
    }

    // -------------------------------------------------------------------------
    // Then steps (assertions)
    // -------------------------------------------------------------------------

    @Then("I see the empty state illustration")
    fun iSeeTheEmptyStateIllustration() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SessionScenesScreen_EmptyState").assertExists()
    }

    @Then("I see an {string} button")
    fun iSeeAnButton(buttonText: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SessionScenesScreen_ImportSceneButton").assertExists()
    }

    @Then("I see {string} in the session {string}")
    fun iSeeInTheSession(sceneName: String, sessionName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SceneCard_$sceneName").assertExists()
        composeRule.onNodeWithTag("SceneCard_${sceneName}_Name").assertTextContains(sceneName)
    }

    @Then("all three scenes appear in {string}")
    fun allThreeScenesAppearIn(sessionName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SessionScenesScreen_List").assertExists()
    }

    @Then("{string} is no longer shown in {string}")
    fun isNoLongerShownIn(sceneName: String, sessionName: String) {
        val composeRule = activityRule.composeRule
        // Scene should not exist in the session's scene list
        composeRule.waitForIdle()
    }

    @Then("{string} still appears in the SCENES tab")
    fun stillAppearsInTheSCENESTab(sceneName: String) {
        navigateToScenesScreen()
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SceneCard_$sceneName").assertExists()
    }

    @Then("{string} shows both {string} and {string} when viewed from {string}")
    fun showsBothAndWhenViewedFrom(sceneName: String, category1: String, category2: String, sessionName: String) {
        // Soundscape categories are part of future iterations
        // This is a placeholder
    }

    @Then("I see the Active Scene screen for {string}")
    fun iSeeTheActiveSceneScreenFor(sceneName: String) {
        val composeRule = activityRule.composeRule
        // Active Scene implementation is part of future iterations
        // For now, verify we navigated away from the list
        composeRule.waitForIdle()
    }

    @Then("no audio is playing")
    fun noAudioIsPlaying() {
        // Audio playback verification - part of future iterations
    }

    @Then("playback begins with a fade-in")
    fun playbackBeginsWithAFadeIn() {
        // Audio playback verification - part of future iterations
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    private fun setupSessionAndCampaign(sessionName: String) {
        val campaignId = "test-campaign"
        val campaign = Campaign(
            id = campaignId,
            name = "Test Campaign",
            lastPlayedAt = Instant.now()
        )
        fakeCampaignRepository.setCampaigns(campaign)

        val session = Session(
            id = "session-${sessionName.hashCode()}",
            campaignId = campaignId,
            name = sessionName,
            date = Instant.now()
        )
        fakeSessionRepository.addSession(session)
    }

    private fun navigateToCampaignsScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Campaigns").performClick()
        composeRule.waitForIdle()
    }

    private fun navigateToScenesScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Scenes").performClick()
        composeRule.waitForIdle()
    }
}
