package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.And
import io.cucumber.java.en.But
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Step definitions for view_created_scenes.feature, delete_scene.feature, and
 * session_scenes.feature (@iter3).
 *
 * Design notes:
 * - All swipe-to-dismiss gestures use swipeLeft() because both ScenesScreen and
 *   SessionScenesScreen configure SwipeToDismissBox with enableDismissFromStartToEnd = false
 *   (EndToStart only). The Gherkin wording "swipe right" is a spec/UI mismatch – noted inline.
 * - The ImportSceneDialog has no explicit confirm button; tapping a scene name immediately
 *   links it and closes the dialog. The "I confirm" step is therefore a documented no-op.
 * - "I open the {string} scene" taps the scene name text. Navigation to the scene detail
 *   screen (with Soundscapes/Soundboard tabs) depends on a click handler being wired in
 *   SceneCard; a missing handler will surface as a test failure, not a compilation error.
 */
class SceneSteps(
    private val composeRuleHolder: MainActivityComposeRule
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository
    private val campaignRepository get() = PicoToHiltBridge.campaignRepository
    private val sessionRepository get() = PicoToHiltBridge.sessionRepository

    /**
     * Internal campaign name used when a scene-related scenario needs to own a session
     * (session_scenes.feature). Stored so that "I open that session" can navigate into it.
     */
    private var internalCampaignName: String = ""

    /**
     * The session name most recently created by "I have a session {string} with no scenes".
     * Used by "I open that session" to tap the correct session card.
     */
    private var currentSessionName: String = ""

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /** Navigates to the SCENES top-level tab. */
    private fun goToScenesTab() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_SCENES")
            .performClick()
    }

    /** Navigates to the CAMPAIGNS top-level tab. */
    private fun goToCampaignsTab() {
        composeTestRule
            .onNodeWithTag("bottomNavItem_CAMPAIGNS")
            .performClick()
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // view_created_scenes.feature  &  delete_scene.feature — Given
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /**
     * Seeds a single scene via the repository and navigates to the SCENES tab so that
     * subsequent When steps (swipe, tap) can interact with the visible list.
     */
    @Given("I have created a scene named {string}")
    fun haveCreatedSceneNamed(name: String) {
        runBlocking { sceneRepository.createScene(name) }
        goToScenesTab()
    }

    /**
     * Seeds multiple scenes from a single-column DataTable and navigates to the SCENES tab.
     *
     * Feature usage:
     * ```
     * Given I have created scenes named
     *   | Tavern  |
     *   | Forest  |
     *   | Dungeon |
     * ```
     */
    @Given("I have created scenes named")
    fun haveCreatedScenesNamed(table: DataTable) {
        runBlocking {
            table.asList().forEach { sceneName ->
                sceneRepository.createScene(sceneName)
            }
        }
        goToScenesTab()
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // view_created_scenes.feature — When / Then
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /**
     * Full UI flow: navigate to SCENES tab → tap the FAB → enter scene name in the dialog →
     * tap Create.
     */
    @When("I create a new scene named {string}")
    fun createNewSceneNamed(name: String) {
        goToScenesTab()
        // Tap the FAB ("Add Scene" content-description)
        composeTestRule
            .onNodeWithContentDescription("Add Scene")
            .performClick()
        // Enter the name into the "Scene Name" field inside the CreateSceneDialog
        composeTestRule
            .onNodeWithText("Scene Name")
            .performTextInput(name)
        // Tap the confirm button
        composeTestRule
            .onNodeWithText("Create")
            .performClick()
    }

    /** Asserts the named scene is visible in the scenes list. */
    @Then("I see the {string} scene in my scenes list")
    fun seeSceneInList(name: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }

    /** Navigates to the SCENES tab (e.g. after a repository-seeded Given step). */
    @When("I view my scenes")
    fun viewMyScenes() {
        goToScenesTab()
    }

    /**
     * Taps the scene card identified by [sceneName] to open the scene detail.
     *
     * ⚠ SceneCard currently has no click handler; tapping the scene name text inside the Card
     * will not trigger navigation to a scene-detail screen until the developer wires up an
     * onClick callback. This step will surface that gap as a runtime assertion failure.
     */
    @When("I open the {string} scene")
    fun openScene(sceneName: String) {
        goToScenesTab()
        composeTestRule.onNodeWithText(sceneName).performClick()
    }

    /**
     * Asserts that a tab with the given label is visible (e.g. "Soundscapes", "Soundboard").
     * Used after navigating to a scene-detail screen.
     */
    @Then("I see the {string} tab")
    fun seeTab(tabName: String) {
        composeTestRule.onNodeWithText(tabName).assertIsDisplayed()
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // delete_scene.feature — When / Then
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /**
     * Swipes LEFT on the card identified by [cardName] to trigger the EndToStart dismissal.
     *
     * ⚠ Spec wording: "swipe right on the {string} card".
     *   Actual UI: SwipeableSceneCard has enableDismissFromStartToEnd = false, so only an
     *   EndToStart (right-to-left / swipeLeft) gesture triggers the delete callback. The step
     *   issues swipeLeft() to match the real behaviour. The spec label should be reconciled with
     *   the developer.
     *
     * Note: `@When("I swipe right on the {string} card")` is already defined in SessionSteps and
     * reused here by Cucumber — it performs swipeLeft() on the found node, which is correct for
     * scenes too. This dedicated step covers only the scene-unlink variant (different text).
     */
    @When("I swipe right on the {string} card to unlink it")
    fun swipeRightToUnlink(cardName: String) {
        composeTestRule
            .onNodeWithText(cardName)
            .performTouchInput { swipeLeft() }
    }

    /** Asserts the named scene is NOT present in the scenes list. */
    @Then("I do not see {string} in my scenes list")
    fun doNotSeeInScenesList(name: String) {
        composeTestRule.onNodeWithText(name).assertDoesNotExist()
    }

    /** Asserts the named scene IS still present in the scenes list. */
    @Then("I still see {string} in my scenes list")
    fun stillSeeInScenesList(name: String) {
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }

    /**
     * Counts the visible SceneCard nodes and asserts the expected number.
     * Uses the "SceneCard" testTag set on each Card in ScenesScreen.
     */
    @Then("I have {int} scenes")
    fun haveSceneCount(expectedCount: Int) {
        composeTestRule
            .onAllNodes(hasTestTag("SceneCard"))
            .assertCountEquals(expectedCount)
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // session_scenes.feature — Given
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /**
     * Creates an internal campaign and a session named [sessionName] with no scenes linked.
     * Navigates to the sessions screen so subsequent steps can tap the session card.
     */
    @Given("I have a session {string} with no scenes")
    fun haveSessionWithNoScenes(sessionName: String) {
        internalCampaignName = "__SceneSessionTestCampaign__"
        currentSessionName = sessionName
        runBlocking {
            campaignRepository.createCampaign(internalCampaignName)
            val campaign = campaignRepository.observeAll().first()
                .first { it.name == internalCampaignName }
            sessionRepository.createSession(campaign.id, sessionName)
        }
        // Navigate so the session card is visible for subsequent steps.
        goToCampaignsTab()
        composeTestRule.onNodeWithText(internalCampaignName).performClick()
    }

    /**
     * Creates a scene via the repository (no UI navigation required). The scene appears
     * in the SCENES tab and is available for importing into sessions.
     */
    @Given("I have a scene {string} in the SCENES tab")
    fun haveSceneInScenesTab(sceneName: String) {
        runBlocking { sceneRepository.createScene(sceneName) }
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // session_scenes.feature — When
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /**
     * Taps the session card for the session most recently set up by
     * "I have a session {string} with no scenes", navigating to the SessionScenesScreen.
     *
     * Pre-condition: the sessions screen for [internalCampaignName] must already be visible
     * (guaranteed by [haveSessionWithNoScenes]).
     */
    @When("I open that session")
    fun openThatSession() {
        composeTestRule.onNodeWithText(currentSessionName).performClick()
    }

    /**
     * Taps the named scene inside the ImportSceneDialog picker to select and immediately
     * link it to the current session.
     *
     * ⚠ ImportSceneDialog has no separate "Confirm" button; tapping the scene name calls
     * onSelect() which links the scene and closes the dialog immediately.
     */
    @When("I select {string} from the scene picker")
    fun selectFromScenePicker(sceneName: String) {
        composeTestRule.onNodeWithText(sceneName).performClick()
    }

    /**
     * No-op step.
     *
     * ⚠ The ImportSceneDialog does not have a dedicated confirm button — scene selection is
     * immediate (tapping a scene name in the picker triggers the link and closes the dialog).
     * This step is retained as a documented placeholder so the feature file remains valid
     * and can be wired up if a multi-select + confirm pattern is introduced later.
     */
    @When("I confirm")
    fun confirm() {
        // No-op: ImportSceneDialog links the scene immediately on selection; no confirm action needed.
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // session_scenes.feature — Then
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /**
     * Asserts that [sceneName] appears in the session-scenes list.
     * At the point this step runs, the UI should already be showing the SessionScenesScreen
     * for [sessionName] (navigated via "I open {string}" or "I open that session").
     *
     * The [sessionName] parameter is accepted for documentation clarity but is not used in the
     * assertion; the screen context is established by prior navigation steps.
     */
    @Then("I see {string} in the session {string}")
    fun seeSceneInSession(sceneName: String, @Suppress("UNUSED_PARAMETER") sessionName: String) {
        composeTestRule.onNodeWithText(sceneName).assertIsDisplayed()
    }

    /**
     * Asserts that [sceneName] is no longer visible in the current session-scenes screen.
     * The [sessionName] parameter is accepted for readability but not used directly.
     */
    @Then("{string} is no longer shown in {string}")
    fun sceneNoLongerShownInSession(
        sceneName: String,
        @Suppress("UNUSED_PARAMETER") sessionName: String
    ) {
        composeTestRule.onNodeWithText(sceneName).assertDoesNotExist()
    }

    /**
     * Navigates to the SCENES tab and asserts [sceneName] is still listed there, confirming
     * that unlinking from a session does not delete the scene globally.
     */
    @But("{string} still appears in the SCENES tab")
    fun sceneStillAppearsInScenesTab(sceneName: String) {
        goToScenesTab()
        composeTestRule.onNodeWithText(sceneName).assertIsDisplayed()
    }
}
