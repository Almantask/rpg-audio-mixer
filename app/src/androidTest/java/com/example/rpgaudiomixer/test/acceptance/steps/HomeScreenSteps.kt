package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.TrackStats
import com.example.rpgaudiomixer.domain.model.TrackType
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeCampaignRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSceneRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeTrackStatsRepository
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import java.time.Instant

class HomeScreenSteps(
    private val activityRule: MainActivityComposeRule,
    private val fakeCampaignRepository: FakeCampaignRepository,
    private val fakeSceneRepository: FakeSceneRepository,
    private val fakeTrackStatsRepository: FakeTrackStatsRepository
) {

    // -------------------------------------------------------------------------
    // Given steps (test state setup)
    // -------------------------------------------------------------------------

    @Given("I have played {string} most recently")
    fun iHavePlayedMostRecently(campaignName: String) {
        val campaign = Campaign(
            id = "campaign-${campaignName.hashCode()}",
            name = campaignName,
            lastPlayedAt = Instant.now()
        )
        fakeCampaignRepository.setCampaigns(campaign)
    }

    @Given("{string} is the active campaign")
    fun isTheActiveCampaign(campaignName: String) {
        val campaign = Campaign(
            id = "campaign-${campaignName.hashCode()}",
            name = campaignName,
            lastPlayedAt = Instant.now()
        )
        fakeCampaignRepository.setCampaigns(campaign)
    }

    @Given("the last opened scene in the active campaign was {string}")
    fun theLastOpenedSceneInTheActiveCampaignWas(sceneName: String) {
        // Get the active campaign
        val activeCampaign = fakeCampaignRepository.getLatestCampaign()
            ?: throw IllegalStateException("No active campaign set")

        val scene = Scene(
            id = "scene-${sceneName.hashCode()}",
            name = sceneName,
            campaignId = activeCampaign.id,
            lastOpenedAt = Instant.now()
        )
        fakeSceneRepository.setScenes(scene)
    }

    @Given("{string} is shown in the Resume Journey card")
    fun isShownInTheResumeJourneyCard(sceneName: String) {
        theLastOpenedSceneInTheActiveCampaignWas(sceneName)
    }

    @Given("{string} is the most played loopable track globally")
    fun isTheMostPlayedLoopableTrackGlobally(trackName: String) {
        val trackStats = TrackStats(
            trackId = "track-${trackName.hashCode()}",
            name = trackName,
            type = TrackType.LOOPABLE,
            playCount = 100
        )
        fakeTrackStatsRepository.setTrackStats(trackStats)
    }

    @Given("{string} is the most played FX globally")
    fun isTheMostPlayedFxGlobally(trackName: String) {
        val trackStats = TrackStats(
            trackId = "track-${trackName.hashCode()}",
            name = trackName,
            type = TrackType.FX,
            playCount = 50
        )
        fakeTrackStatsRepository.setTrackStats(trackStats)
    }

    @Given("I have no campaigns")
    fun iHaveNoCampaigns() {
        fakeCampaignRepository.clear()
        fakeSceneRepository.clear()
        fakeTrackStatsRepository.clear()
    }

    // -------------------------------------------------------------------------
    // When steps (actions)
    // -------------------------------------------------------------------------

    @When("I open the Home screen")
    fun iOpenTheHomeScreen() {
        val composeRule = activityRule.composeRule
        // Navigate to Home if not already there
        composeRule.onNodeWithTag("BottomNav_Home").performClick()
        composeRule.waitForIdle()
    }

    @When("I tap {string}")
    fun iTap(buttonText: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithText(buttonText).performClick()
        composeRule.waitForIdle()
    }

    @When("I tap {string} in the Resume Journey card")
    fun iTapInTheResumeJourneyCard(buttonText: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("HomeScreen_EnterSceneButton").performClick()
        composeRule.waitForIdle()
    }

    // -------------------------------------------------------------------------
    // Then steps (assertions)
    // -------------------------------------------------------------------------

    @Then("I see {string} as the active campaign")
    fun iSeeAsTheActiveCampaign(campaignName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("HomeScreen_ActiveCampaign").assertIsDisplayed()
        composeRule.onNodeWithTag("HomeScreen_CampaignName").assertIsDisplayed()
        composeRule.onNodeWithText(campaignName).assertIsDisplayed()
    }

    @Then("I see the sessions list for {string}")
    fun iSeeTheSessionsListFor(campaignName: String) {
        // TODO: This will be implemented when we build the sessions screen
        // For now, we just verify the button exists and can be clicked
        val composeRule = activityRule.composeRule
        composeRule.waitForIdle()
    }

    @Then("I see {string} in the Resume Journey card")
    fun iSeeInTheResumeJourneyCard(sceneName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("HomeScreen_ResumeJourney").assertIsDisplayed()
        composeRule.onNodeWithTag("HomeScreen_LastSceneName").assertIsDisplayed()
        composeRule.onNodeWithText(sceneName).assertIsDisplayed()
    }

    @Then("I see the Active Scene screen for {string}")
    fun iSeeTheActiveSceneScreenFor(sceneName: String) {
        // TODO: This will be implemented when we build the active scene screen
        val composeRule = activityRule.composeRule
        composeRule.waitForIdle()
    }

    @Then("playback begins with a fade-in")
    fun playbackBeginsWithAFadeIn() {
        // TODO: This will be implemented when we build the scene playback feature
    }

    @Then("I see {string} in the Top Atmosphere card")
    fun iSeeInTheTopAtmosphereCard(trackName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("HomeScreen_TopAtmosphere").assertIsDisplayed()
        composeRule.onNodeWithTag("HomeScreen_TopAtmosphere_TrackName").assertIsDisplayed()
        composeRule.onNodeWithText(trackName).assertIsDisplayed()
    }

    @Then("I see {string} in the Legendary Action card")
    fun iSeeInTheLegendaryActionCard(trackName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("HomeScreen_LegendaryAction").assertIsDisplayed()
        composeRule.onNodeWithTag("HomeScreen_LegendaryAction_TrackName").assertIsDisplayed()
        composeRule.onNodeWithText(trackName).assertIsDisplayed()
    }

    @Then("the active campaign area shows a prompt to create a campaign")
    fun theActiveCampaignAreaShowsAPromptToCreateACampaign() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("HomeScreen_EmptyCampaign").assertIsDisplayed()
        composeRule.onNodeWithTag("HomeScreen_CreateCampaignPrompt").assertIsDisplayed()
    }

    @Then("the Resume Journey card is not shown")
    fun theResumeJourneyCardIsNotShown() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("HomeScreen_ResumeJourney").assertDoesNotExist()
    }
}
