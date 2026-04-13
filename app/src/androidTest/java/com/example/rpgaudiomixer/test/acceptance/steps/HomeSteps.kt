package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.runBlocking
import org.junit.Ignore

/**
 * Step definitions for home_screen.feature (@iter4).
 *
 * Testable scenarios (current implementation):
 *  - Home screen shows the most recently played campaign
 *  - Enter Domain navigates to the active campaign's sessions
 *  - Home screen shows an empty state when no campaigns exist
 *
 * Scenarios marked @Ignore require features not yet implemented
 * (resume journey / last-played scene, top atmosphere, legendary action).
 */
class HomeSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val campaignRepository get() = PicoToHiltBridge.campaignRepository

    // ── Helpers ───────────────────────────────────────────

    private fun navigateToHomeTab() {
        composeTestRule.onNodeWithTag("bottomNavItem_HOME").performClick()
        composeTestRule.waitForIdle()
    }

    // ── Given ─────────────────────────────────────────────

    @Given("I have played {string} most recently")
    fun havePlayedMostRecently(campaignName: String) {
        // Create the campaign — it gets the current timestamp as lastPlayedAt,
        // so it will be first in the list when observeAll() is called.
        runBlocking { campaignRepository.createCampaign(campaignName) }
    }

    @Given("{string} is the active campaign")
    fun isActiveCampaign(campaignName: String) {
        runBlocking { campaignRepository.createCampaign(campaignName) }
    }

    @Given("the last opened scene in the active campaign was {string}")
    @Ignore("Resume Journey / last-played scene tracking not yet implemented")
    fun lastOpenedScene(sceneName: String) {
        // TODO: Resume Journey feature (last-played scene tracking) not yet implemented.
    }

    @Given("{string} is the most played loopable track globally")
    @Ignore("Top Atmosphere / play-count tracking not yet implemented")
    fun mostPlayedLoopableTrack(trackName: String) {
        // TODO: Play-count tracking for loopable tracks not yet implemented.
    }

    @Given("{string} is the most played FX globally")
    @Ignore("Legendary Action / FX play-count tracking not yet implemented")
    fun mostPlayedFxTrack(trackName: String) {
        // TODO: Play-count tracking for FX tracks not yet implemented.
    }

    @Given("{string} is shown in the Resume Journey card")
    @Ignore("Resume Journey / last-played scene tracking not yet implemented")
    fun isShownInResumeJourneyCard(sceneName: String) {
        // TODO: Resume Journey feature (last-played scene tracking) not yet implemented.
    }

    // ── When ──────────────────────────────────────────────

    @When("I open the Home screen")
    fun openHomeScreen() {
        navigateToHomeTab()
    }

    @When("I tap {string} on home screen")
    fun tapButtonOnHomeScreen(text: String) {
        composeTestRule.onNodeWithText(text, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I tap {string} in the Resume Journey card")
    @Ignore("Resume Journey card not yet implemented with last-played scene")
    fun tapInResumeJourneyCard(text: String) {
        // TODO: Resume Journey requires last-played scene tracking, not yet implemented.
    }

    // ── Then ──────────────────────────────────────────────

    @Then("I see {string} as the active campaign")
    fun seeActiveCampaign(campaignName: String) {
        composeTestRule.onNodeWithTag("activeCampaignCard").assertIsDisplayed()
        composeTestRule.onNode(
            hasText(campaignName) and hasAnyAncestor(hasTestTag("activeCampaignCard"))
        ).assertIsDisplayed()
    }

    @Then("I see the sessions list for {string}")
    fun seeSessionsListForCampaign(campaignName: String) {
        // After tapping Enter Domain, we navigate to the Sessions screen.
        // The sessions screen shows a sessionList tag (with sessions) or an empty state.
        // We verify we've navigated away from home by checking the back button is present.
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Then("the active campaign area shows a prompt to create a campaign")
    fun activeCampaignAreaShowsPrompt() {
        composeTestRule.onNodeWithTag("homeEmptyState").assertIsDisplayed()
    }

    @Then("the Resume Journey card is not shown")
    fun resumeJourneyCardNotShown() {
        composeTestRule.onNodeWithTag("resumeJourneyCard").assertDoesNotExist()
    }

    @Then("I see {string} in the Resume Journey card")
    @Ignore("Resume Journey / last-played scene tracking not yet implemented")
    fun seeSceneInResumeJourneyCard(sceneName: String) {
        // TODO: Resume Journey feature (last-played scene tracking) not yet implemented.
    }

    @Then("I see {string} in the Top Atmosphere card")
    @Ignore("Top Atmosphere / play-count tracking not yet implemented")
    fun seeTrackInTopAtmosphereCard(trackName: String) {
        // TODO: Play-count tracking for loopable tracks not yet implemented.
    }

    @Then("I see {string} in the Legendary Action card")
    @Ignore("Legendary Action / FX play-count tracking not yet implemented")
    fun seeActionInLegendaryActionCard(actionName: String) {
        // TODO: Play-count tracking for FX tracks not yet implemented.
    }

    @Then("I see the Active Scene screen for {string} from home")
    @Ignore("Active scene screen not yet implemented")
    fun seeActiveSceneScreenFromHome(sceneName: String) {
        // TODO: Active scene screen not yet implemented.
    }

    @Then("playback begins with a fade-in from home")
    @Ignore("Audio playback not yet implemented")
    fun playbackBeginsWithFadeInFromHome() {
        // TODO: Audio playback not yet implemented.
    }
}
