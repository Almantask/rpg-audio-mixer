package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.PendingException
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking

/**
 * Step definitions for home_screen.feature (@iter4).
 *
 * Design notes:
 * - "Given I have no campaigns" and "When I tap {string}" already live in [CampaignSteps]
 *   and are intentionally NOT redefined here.
 * - Scenarios involving Top Atmosphere, Legendary Action, and Active Scene playback are
 *   Iteration 6 concerns; their steps throw [PendingException] so they are skipped cleanly
 *   without failing the test run.
 * - The HomeViewModel uses `campaignRepository.observeLatest()` and
 *   `sceneRepository.observeLatest()` to populate the UI. Creating any campaign/scene
 *   via the repository is sufficient since DatabaseHooks guarantees a clean slate before
 *   each scenario.
 */
class HomeSteps(
    private val composeRuleHolder: MainActivityComposeRule
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val campaignRepository get() = PicoToHiltBridge.campaignRepository
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository

    // ── Helpers ──────────────────────────────────────────────────────────────────────────────────

    private fun goToHomeTab() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_HOME")
            .performClick()
    }

    // ── Given ────────────────────────────────────────────────────────────────────────────────────

    /**
     * Creates a campaign with the given name (so it becomes the "latest" campaign observed
     * by HomeViewModel) and navigates to the HOME tab to display the HomeScreen.
     */
    @Given("I have played {string} most recently")
    fun havePlayedMostRecently(campaignName: String) {
        runBlocking { campaignRepository.createCampaign(campaignName) }
        goToHomeTab()
    }

    /**
     * Creates a campaign and navigates to HOME so subsequent When steps act on HomeScreen.
     * Used for the "Enter Domain" navigation scenario.
     */
    @Given("{string} is the active campaign")
    fun isTheActiveCampaign(campaignName: String) {
        runBlocking { campaignRepository.createCampaign(campaignName) }
        goToHomeTab()
    }

    /**
     * Creates both a campaign (so HomeViewModel shows the hero card) and a scene with the
     * given name (so HomeViewModel shows the Resume Journey card), then navigates to HOME.
     *
     * Note: SceneRepository.observeLatest() returns the most-recently created non-deleted
     * scene, regardless of campaign linkage, which is sufficient for the HomeScreen preview.
     */
    @Given("the last opened scene in the active campaign was {string}")
    fun lastOpenedSceneWas(sceneName: String) {
        runBlocking {
            campaignRepository.createCampaign("Active Campaign")
            sceneRepository.createScene(sceneName)
        }
        goToHomeTab()
    }

    /**
     * Creates a campaign + scene and navigates to HOME so the Resume Journey card is visible.
     * Used as precondition for the "tap Enter in the Resume Journey card" scenario.
     */
    @Given("{string} is shown in the Resume Journey card")
    fun isShownInResumeJourneyCard(sceneName: String) {
        runBlocking {
            campaignRepository.createCampaign("Active Campaign")
            sceneRepository.createScene(sceneName)
        }
        goToHomeTab()
    }

    /**
     * Iteration 6 concern: track play-count tracking is not yet implemented.
     */
    @Given("{string} is the most played loopable track globally")
    fun isMostPlayedLoopableTrackGlobally(@Suppress("UNUSED_PARAMETER") trackName: String) {
        // TODO Iteration 6 — Top Atmosphere stat tracking is not yet implemented.
        throw PendingException("Top Atmosphere play-count tracking is an Iteration 6 concern.")
    }

    /**
     * Iteration 6 concern: FX play-count tracking is not yet implemented.
     */
    @Given("{string} is the most played FX globally")
    fun isMostPlayedFXGlobally(@Suppress("UNUSED_PARAMETER") trackName: String) {
        // TODO Iteration 6 — Legendary Action stat tracking is not yet implemented.
        throw PendingException("Legendary Action play-count tracking is an Iteration 6 concern.")
    }

    // ── When ─────────────────────────────────────────────────────────────────────────────────────

    /**
     * Taps the HOME bottom-navigation item to reveal the HomeScreen.
     * Used after a Given that may have left the user on a different tab or screen.
     */
    @When("I open the Home screen")
    fun openHomeScreen() {
        goToHomeTab()
    }

    /**
     * Taps a button that is a descendant of the Resume Journey card.
     * Locates the node using a compound semantic matcher so the click is scoped to
     * the correct card, avoiding ambiguity with other "Enter" labels on the screen.
     */
    @When("I tap {string} in the Resume Journey card")
    fun tapInResumeJourneyCard(buttonText: String) {
        composeTestRule
            .onNode(
                hasText(buttonText, ignoreCase = true)
                    and hasAnyAncestor(hasTestTag("resumeJourneyCard"))
            )
            .performClick()
    }

    // ── Then ─────────────────────────────────────────────────────────────────────────────────────

    /**
     * Asserts that the campaign hero card is visible and displays the given campaign name.
     */
    @Then("I see {string} as the active campaign")
    fun seeAsActiveCampaign(campaignName: String) {
        composeTestRule
            .onNodeWithTag("campaignCard")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(campaignName)
            .assertIsDisplayed()
    }

    /**
     * Asserts that tapping "Enter Domain" navigated to the sessions screen for the campaign.
     * The SessionsScreen is tagged "sessionsScreen" and displays the campaign name in its top bar.
     */
    @Then("I see the sessions list for {string}")
    fun seeSessionsListFor(campaignName: String) {
        composeTestRule
            .onNodeWithTag("sessionsScreen")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(campaignName)
            .assertIsDisplayed()
    }

    /**
     * Asserts that the Resume Journey card is displayed and contains the scene name.
     */
    @Then("I see {string} in the Resume Journey card")
    fun seeInResumeJourneyCard(sceneName: String) {
        composeTestRule
            .onNodeWithTag("resumeJourneyCard")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText(sceneName)
                    and hasAnyAncestor(hasTestTag("resumeJourneyCard"))
            )
            .assertIsDisplayed()
    }

    /**
     * Iteration 6 concern: the Active Scene screen does not exist yet.
     */
    @Then("I see the Active Scene screen for {string}")
    fun seeActiveSceneScreenFor(@Suppress("UNUSED_PARAMETER") sceneName: String) {
        // TODO Iteration 6 — Active Scene screen and navigation are not yet implemented.
        throw PendingException("Active Scene screen navigation is an Iteration 6 concern.")
    }

    /**
     * Iteration 6 concern: scene playback with fade-in is not yet implemented.
     */
    @Then("playback begins with a fade-in")
    fun playbackBeginsWithFadeIn() {
        // TODO Iteration 6 — scene playback with fade-in is not yet implemented.
        throw PendingException("Scene playback fade-in is an Iteration 6 concern.")
    }

    /**
     * Iteration 6 concern: Top Atmosphere card is not yet implemented on HomeScreen.
     */
    @Then("I see {string} in the Top Atmosphere card")
    fun seeInTopAtmosphereCard(@Suppress("UNUSED_PARAMETER") trackName: String) {
        // TODO Iteration 6 — Top Atmosphere card is not yet implemented.
        throw PendingException("Top Atmosphere card is an Iteration 6 concern.")
    }

    /**
     * Iteration 6 concern: Legendary Action card is not yet implemented on HomeScreen.
     */
    @Then("I see {string} in the Legendary Action card")
    fun seeInLegendaryActionCard(@Suppress("UNUSED_PARAMETER") trackName: String) {
        // TODO Iteration 6 — Legendary Action card is not yet implemented.
        throw PendingException("Legendary Action card is an Iteration 6 concern.")
    }

    /**
     * Asserts the ArcanumEmptyState is shown inside the campaign card area when no
     * campaigns exist. The empty state renders "No Active Campaign" as its title.
     */
    @Then("the active campaign area shows a prompt to create a campaign")
    fun activeCampaignAreaShowsPrompt() {
        composeTestRule
            .onNodeWithText("No Active Campaign", ignoreCase = true)
            .assertIsDisplayed()
    }

    /**
     * Asserts that the Resume Journey card is NOT rendered when no campaign (or scene) exists.
     * HomeScreen only renders the card when both latestCampaign and latestScene are non-null.
     */
    @Then("the Resume Journey card is not shown")
    fun resumeJourneyCardIsNotShown() {
        composeTestRule
            .onNodeWithTag("resumeJourneyCard")
            .assertDoesNotExist()
    }
}
