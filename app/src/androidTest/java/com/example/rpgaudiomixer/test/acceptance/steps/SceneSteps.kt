package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Ignore

/**
 * Step definitions for:
 *  - view_created_scenes.feature  (@iter3)
 *  - session_scenes.feature       (@iter3)
 *
 * Scenarios marked @Ignore require features not yet implemented
 * (active scene screen, audio playback, scene editing).
 */
class SceneSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val campaignRepository get() = PicoToHiltBridge.campaignRepository
    private val sessionRepository get() = PicoToHiltBridge.sessionRepository
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository

    // ── Helpers ───────────────────────────────────────────

    private fun navigateToScenesTab() {
        composeTestRule.onNodeWithTag("bottomNavItem_SCENES").performClick()
        composeTestRule.waitForIdle()
    }

    private fun navigateToCampaignsTab() {
        composeTestRule.onNodeWithTag("bottomNavItem_CAMPAIGNS").performClick()
        composeTestRule.waitForIdle()
    }

    // ── view_created_scenes.feature ───────────────────────

    @When("I create a new scene named {string}")
    fun createSceneViaUi(name: String) {
        navigateToScenesTab()
        composeTestRule.onNodeWithTag("addSceneFab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("sceneNameInput").performTextInput(name)
        composeTestRule.onNodeWithTag("createSceneButton").performClick()
        composeTestRule.waitForIdle()
    }

    @Then("I see the {string} scene in my scenes list")
    fun seeSceneInList(name: String) {
        navigateToScenesTab()
        composeTestRule.onNodeWithText(name, ignoreCase = true).assertIsDisplayed()
    }

    @Given("I have created scenes named")
    fun haveCreatedScenesNamed(dataTable: io.cucumber.datatable.DataTable) {
        runBlocking {
            dataTable.asList().forEach { sceneName ->
                sceneRepository.createScene(sceneName)
            }
        }
        navigateToScenesTab()
    }

    @When("I view my scenes")
    fun viewMyScenes() {
        navigateToScenesTab()
    }

    @Given("I have created a scene named {string}")
    fun haveCreatedScene(name: String) {
        runBlocking { sceneRepository.createScene(name) }
        navigateToScenesTab()
    }

    @When("I open the {string} scene")
    @Ignore("Active scene screen not yet implemented")
    fun openScene(name: String) {
        // TODO: Active scene screen not yet implemented.
        // When implemented, tap the SceneCard with the given name.
    }

    @Then("I see the {string} tab")
    @Ignore("Active scene screen not yet implemented")
    fun seeTab(tabName: String) {
        // TODO: Active scene screen not yet implemented.
    }

    // ── delete_scene.feature ──────────────────────────────

    @Then("I do not see {string} in my scenes list")
    fun doNotSeeSceneInList(name: String) {
        composeTestRule.onAllNodesWithText(name, ignoreCase = true).assertCountEquals(0)
    }

    @Then("I still see {string} in my scenes list")
    fun stillSeeSceneInList(name: String) {
        composeTestRule.onNodeWithText(name, ignoreCase = true).assertIsDisplayed()
    }

    @Then("I have {int} scenes")
    fun haveNScenes(count: Int) {
        composeTestRule.onAllNodes(hasTestTag("SceneCard")).assertCountEquals(count)
    }

    // ── session_scenes.feature ────────────────────────────

    @Given("I have a session {string} with no scenes")
    fun haveSessionWithNoScenes(sessionName: String) {
        runBlocking {
            campaignRepository.createCampaign("Test Campaign")
            val campaign = campaignRepository.observeAll().first().first()
            sessionRepository.createSession(campaign.id, sessionName)
        }
    }

    @When("I open that session")
    fun openThatSession() {
        // Navigate to campaigns, then tap the only campaign, then tap the only session
        navigateToCampaignsTab()
        val campaigns = runBlocking { campaignRepository.observeAll().first() }
        val campaignName = campaigns.first().name
        composeTestRule.onNodeWithText(campaignName, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
        // Tap first session card
        composeTestRule.onAllNodes(hasTestTag("SessionCard")).onFirst().performClick()
        composeTestRule.waitForIdle()
    }

    @Given("I have a scene {string} in the SCENES tab")
    fun haveSceneInScenesTab(sceneName: String) {
        runBlocking { sceneRepository.createScene(sceneName) }
    }

    @Given("I have scenes {string}, {string}, {string} in the SCENES tab")
    fun haveScenesInScenesTab(scene1: String, scene2: String, scene3: String) {
        runBlocking {
            sceneRepository.createScene(scene1)
            sceneRepository.createScene(scene2)
            sceneRepository.createScene(scene3)
        }
    }

    @When("I open {string} from sessions")
    fun openSessionFromCampaign(sessionName: String) {
        composeTestRule.onNodeWithText(sessionName, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I tap {string} on session scenes screen")
    fun tapImportScene(text: String) {
        composeTestRule.onNodeWithText(text, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I select {string} from the scene picker")
    fun selectSceneFromPicker(sceneName: String) {
        composeTestRule.waitForIdle()
        // Tap the checkbox/row for this scene in the importSceneList
        composeTestRule.onNode(
            hasText(sceneName) and hasAnyAncestor(hasTestTag("importSceneList"))
        ).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I select {string}, {string}, and {string} from the picker")
    fun selectMultipleScenesFromPicker(scene1: String, scene2: String, scene3: String) {
        // Wait for the import scene list to be populated
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasTestTag("importSceneList"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        listOf(scene1, scene2, scene3).forEach { sceneName ->
            // Use the checkbox row — find the text and click its row
            val nodes = composeTestRule.onAllNodes(
                hasText(sceneName) and hasAnyAncestor(hasTestTag("importSceneList"))
            ).fetchSemanticsNodes()
            require(nodes.isNotEmpty()) {
                "Could not find '$sceneName' in importSceneList"
            }
            composeTestRule.onNode(
                hasText(sceneName) and hasAnyAncestor(hasTestTag("importSceneList"))
            ).performClick()
            composeTestRule.waitForIdle()
        }
    }

    @When("I confirm")
    fun confirmImport() {
        composeTestRule.onNodeWithTag("confirmImportButton").performClick()
        composeTestRule.waitForIdle()
    }

    @Then("I see {string} in the session {string}")
    fun seeSceneInSession(sceneName: String, sessionName: String) {
        composeTestRule.onNodeWithText(sceneName, ignoreCase = true).assertIsDisplayed()
    }

    @Then("all three scenes appear in {string}")
    fun allThreeScenesAppearInSession(sessionName: String) {
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodes(hasTestTag("SessionSceneCard"))
                .fetchSemanticsNodes().size == 3
        }
    }

    @Given("{string} is linked to {string}")
    fun sceneLinkedToSession(sceneName: String, sessionName: String) {
        runBlocking {
            // Ensure the scene and session exist, then link them
            sceneRepository.createScene(sceneName)
            val scene = sceneRepository.observeAll().first().first { it.name == sceneName }

            campaignRepository.createCampaign("Test Campaign")
            val campaign = campaignRepository.observeAll().first().first()
            sessionRepository.createSession(campaign.id, sessionName)
            val session = sessionRepository.observeByCampaign(campaign.id).first()
                .first { it.name == sessionName }

            sceneRepository.linkSceneToSession(session.id, scene.id)
        }
        // Navigate to the session scenes screen so linked scenes are visible
        navigateToCampaignsTab()
        composeTestRule.onNodeWithText("Test Campaign", ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(sessionName, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I swipe right on the {string} card to unlink it")
    fun swipeRightToUnlink(sceneName: String) {
        composeTestRule.onNodeWithText(sceneName, ignoreCase = true)
            .onParent()
            .performTouchInput { swipeRight() }
        composeTestRule.waitForIdle()
    }

    @Then("{string} is no longer shown in {string}")
    fun sceneNoLongerInSession(sceneName: String, sessionName: String) {
        composeTestRule.onAllNodes(hasTestTag("SessionSceneCard")).assertCountEquals(0)
    }

    @Then("{string} still appears in the SCENES tab")
    fun sceneStillInScenesTab(sceneName: String) {
        // Navigate back from SessionScenes → Sessions → Campaigns (where bottom nav is visible)
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        navigateToScenesTab()
        composeTestRule.onNodeWithText(sceneName, ignoreCase = true).assertIsDisplayed()
    }

    // ── Scenarios not yet implemented (placeholders) ──────

    @Given("{string} has the soundscape category {string}")
    @Ignore("Soundscape editing not yet implemented")
    fun sceneHasSoundscapeCategory(sceneName: String, category: String) {
        // TODO: Not yet implemented.
    }

    @When("I edit {string} and add the soundscape category {string}")
    @Ignore("Scene editing not yet implemented")
    fun editSceneAndAddCategory(sceneName: String, category: String) {
        // TODO: Scene editing screen not yet implemented.
    }

    @Then("{string} shows both {string} and {string} when viewed from {string}")
    @Ignore("Scene editing not yet implemented")
    fun sceneShowsBothCategories(sceneName: String, cat1: String, cat2: String, sessionName: String) {
        // TODO: Not yet implemented.
    }

    @When("I tap the {string} scene card in {string}")
    @Ignore("Active scene screen not yet implemented")
    fun tapSceneCardInSession(sceneName: String, sessionName: String) {
        // TODO: Active scene screen not yet implemented.
    }

    @Then("I see the Active Scene screen for {string}")
    @Ignore("Active scene screen not yet implemented")
    fun seeActiveSceneScreen(sceneName: String) {
        // TODO: Active scene screen not yet implemented.
    }

    @Then("no audio is playing")
    @Ignore("Audio playback verification not yet implemented")
    fun noAudioIsPlaying() {
        // TODO: Audio playback verification not yet implemented.
    }

    @When("I tap the play button on the {string} scene card in {string}")
    @Ignore("Audio playback not yet implemented")
    fun tapPlayButtonOnSceneCard(sceneName: String, sessionName: String) {
        // TODO: Audio playback not yet implemented.
    }

    @Then("playback begins with a fade-in")
    @Ignore("Audio playback not yet implemented")
    fun playbackBeginsWithFadeIn() {
        // TODO: Audio playback not yet implemented.
    }

    @Given("I have a session {string} with no scenes linked")
    fun haveSessionWithNoScenesLinked(sessionName: String) {
        runBlocking {
            campaignRepository.createCampaign("Test Campaign")
            val campaign = campaignRepository.observeAll().first().first()
            sessionRepository.createSession(campaign.id, sessionName)
        }
    }

    @When("I open {string} session")
    fun openNamedSession(sessionName: String) {
        navigateToCampaignsTab()
        val campaigns = runBlocking { campaignRepository.observeAll().first() }
        composeTestRule.onNodeWithText(campaigns.first().name, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(sessionName, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
    }

    @When("I tap {string} on session scene screen")
    fun tapButtonOnSessionSceneScreen(text: String) {
        composeTestRule.onNodeWithText(text, ignoreCase = true).performClick()
        composeTestRule.waitForIdle()
    }

    @Then("I see an {string} button on session scenes screen")
    fun seeButtonOnSessionScenesScreen(buttonText: String) {
        composeTestRule.onNodeWithTag("emptyStateCta").assertIsDisplayed()
    }
}
