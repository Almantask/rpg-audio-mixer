package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.scenes.ActiveSceneTestTags
import com.example.rpgaudiomixer.ui.scenes.ScenesTestTags
import com.example.rpgaudiomixer.ui.sessions.SessionsTestTags
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class SceneSteps(
    private val composeRuleHolder: MainActivityComposeRule,
    private val fakeMusicPlayer: FakeMusicPlayer,
) {
    private var currentSceneName: String = ""
    private var currentSceneId: Long = 0L
    private var currentSessionName: String = ""
    private var currentSessionId: Long = 0L

    init {
        runBlocking {
            entryPoint().campaignRepository().clearAll()
            entryPoint().sessionRepository().clearAll()
            entryPoint().sceneRepository().clearAll()
        }
        entryPoint().sceneTrashRepository().reset()
    }

    @When("I create a new scene named {string}")
    fun iCreateANewSceneNamed(name: String) {
        openScenesTab()
        composeRuleHolder.composeRule.onNodeWithText("Add New Scene").performClick()
        composeRuleHolder.composeRule.onNodeWithTag(ScenesTestTags.NAME_INPUT).performTextInput(name)
        composeRuleHolder.composeRule.onNodeWithText("Create").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentSceneName = name
        currentSceneId = sceneIdByName(name)
    }

    @Given("I have created scenes named")
    fun iHaveCreatedScenesNamed(table: DataTable) {
        val scenes = table.cells().flatten().filter(String::isNotBlank).map(String::trim)
        runBlocking {
            scenes.forEach { name ->
                entryPoint().sceneRepository().upsertScene(Scene(name = name))
            }
        }
    }

    @Given("I have created a scene named {string}")
    fun iHaveCreatedASceneNamed(name: String) {
        currentSceneId = runBlocking { entryPoint().sceneRepository().upsertScene(Scene(name = name)) }
        currentSceneName = name
    }

    @Given("I have a scene {string} in the SCENES tab")
    fun iHaveASceneInTheScenesTab(name: String) {
        iHaveCreatedASceneNamed(name)
    }

    @Given("I have scenes {string}, {string}, {string} in the SCENES tab")
    fun iHaveScenesInTheScenesTab(first: String, second: String, third: String) {
        runBlocking {
            listOf(first, second, third).forEach { name ->
                entryPoint().sceneRepository().upsertScene(Scene(name = name))
            }
        }
    }

    @Given("I have a session {string} with no scenes")
    fun iHaveASessionWithNoScenes(name: String) {
        val campaignId = runBlocking { entryPoint().campaignRepository().upsertCampaign(Campaign(name = "Scene Session Campaign")) }
        currentSessionId = runBlocking {
            entryPoint().sessionRepository().upsertSession(
                Session(campaignId = campaignId, name = name),
            )
        }
        currentSessionName = name
    }

    @Given("{string} is linked to {string}")
    fun isLinkedTo(sceneName: String, sessionName: String) {
        val campaignId = runBlocking { entryPoint().campaignRepository().upsertCampaign(Campaign(name = "Linked Scenes Campaign")) }
        currentSessionId = runBlocking {
            entryPoint().sessionRepository().upsertSession(
                Session(campaignId = campaignId, name = sessionName),
            )
        }
        currentSessionName = sessionName
        currentSceneId = runBlocking { entryPoint().sceneRepository().upsertScene(Scene(name = sceneName)) }
        currentSceneName = sceneName
        runBlocking {
            entryPoint().sessionRepository().linkScenes(currentSessionId, listOf(currentSceneId))
        }
        openSession(currentSessionName)
    }

    @Given("{string} has the soundscape category {string}")
    fun hasTheSoundscapeCategory(sceneName: String, categoryName: String) {
        val sceneId = sceneIdByName(sceneName)
        runBlocking {
            entryPoint().sceneRepository().addSoundscapeCategory(sceneId, categoryName)
        }
    }

    @When("I view my scenes")
    fun iViewMyScenes() {
        openScenesTab()
    }

    @When("I open the {string} scene")
    fun iOpenTheScene(name: String) {
        openScenesTab()
        composeRuleHolder.composeRule.onNodeWithTag(ScenesTestTags.card(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentSceneName = name
        currentSceneId = sceneIdByName(name)
    }

    @When("I open that session")
    fun iOpenThatSession() {
        openSession(currentSessionName)
    }

    @When("I select {string} from the scene picker")
    fun iSelectFromTheScenePicker(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SessionScenesTestTags.pickerOption(name)).performClick()
    }

    @When("I select {string}, {string}, and {string} from the picker")
    fun iSelectMultipleFromThePicker(first: String, second: String, third: String) {
        listOf(first, second, third).forEach { name ->
            composeRuleHolder.composeRule.onNodeWithTag(SessionScenesTestTags.pickerOption(name)).performClick()
        }
    }

    @When("I confirm")
    fun iConfirm() {
        composeRuleHolder.composeRule.onNodeWithText("Confirm").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I swipe right on the {string} card to unlink it")
    fun iSwipeRightOnTheCardToUnlinkIt(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SessionScenesTestTags.card(name)).performTouchInput {
            swipeRight()
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I edit {string} and add the soundscape category {string}")
    fun iEditAndAddTheSoundscapeCategory(sceneName: String, categoryName: String) {
        runBlocking {
            entryPoint().sceneRepository().addSoundscapeCategory(sceneIdByName(sceneName), categoryName)
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the {string} scene card in {string}")
    fun iTapTheSceneCardIn(name: String, sessionName: String) {
        openSession(sessionName)
        composeRuleHolder.composeRule.onNodeWithTag(SessionScenesTestTags.card(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentSceneName = name
        currentSceneId = sceneIdByName(name)
    }

    @When("I tap the play button on the {string} scene card in {string}")
    fun iTapThePlayButtonOnTheSceneCardIn(name: String, sessionName: String) {
        openSession(sessionName)
        composeRuleHolder.composeRule.onNodeWithTag(SessionScenesTestTags.playButton(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentSceneName = name
        currentSceneId = sceneIdByName(name)
    }

    @Then("I see the {string} scene in my scenes list")
    fun iSeeTheSceneInMyScenesList(name: String) {
        openScenesTab()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("I see the {string} tab")
    fun iSeeTheTab(name: String) {
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("I do not see {string} in my scenes list")
    fun iDoNotSeeInMyScenesList(name: String) {
        openScenesTab()
        composeRuleHolder.composeRule.onNodeWithText(name).assertDoesNotExist()
    }

    @Then("I still see {string} in my scenes list")
    fun iStillSeeInMyScenesList(name: String) {
        openScenesTab()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("I have {int} scenes")
    fun iHaveScenes(expectedCount: Int) {
        val scenes = runBlocking { entryPoint().sceneRepository().observeScenes().first() }
        assertThat(scenes).hasSize(expectedCount)
    }

    @Then("I see {string} in the session {string}")
    fun iSeeInTheSession(name: String, sessionName: String) {
        openSession(sessionName)
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("all three scenes appear in {string}")
    fun allThreeScenesAppearIn(sessionName: String) {
        openSession(sessionName)
        listOf("Tavern", "Forest", "Dungeon").forEach { sceneName ->
            composeRuleHolder.composeRule.onNodeWithText(sceneName).assertIsDisplayed()
        }
    }

    @Then("{string} is no longer shown in {string}")
    fun isNoLongerShownIn(sceneName: String, sessionName: String) {
        openSession(sessionName)
        composeRuleHolder.composeRule.onNodeWithText(sceneName).assertDoesNotExist()
    }

    @Then("{string} still appears in the SCENES tab")
    fun stillAppearsInTheScenesTab(sceneName: String) {
        openScenesTab()
        composeRuleHolder.composeRule.onNodeWithText(sceneName).assertIsDisplayed()
    }

    @Then("{string} shows both {string} and {string} when viewed from {string}")
    fun showsBothAndWhenViewedFrom(sceneName: String, first: String, second: String, sessionName: String) {
        openSession(sessionName)
        composeRuleHolder.composeRule.onNodeWithText(sceneName).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(first).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(second).assertIsDisplayed()
    }

    @Then("I see the Active Scene screen for {string}")
    fun iSeeTheActiveSceneScreenFor(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneTestTags.SCREEN).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText("Active Scene: $name").assertIsDisplayed()
    }

    @Then("no audio is playing")
    fun noAudioIsPlaying() {
        assertThat(fakeMusicPlayer.loopingPlayed).isEmpty()
    }

    @Then("playback begins with a fade-in")
    fun playbackBeginsWithAFadeIn() {
        assertThat(fakeMusicPlayer.loopingPlayed).contains("scene:$currentSceneId")
    }

    private fun openScenesTab() {
        composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun openSession(sessionName: String) {
        composeRuleHolder.composeRule.onNodeWithText("CAMPAIGNS").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        if (composeRuleHolder.composeRule.onAllNodesWithTag(SessionsTestTags.card(sessionName)).fetchSemanticsNodes().isEmpty()) {
            val campaignName = runBlocking {
                entryPoint().campaignRepository().observeCampaigns().first().first().name
            }
            composeRuleHolder.composeRule.onNodeWithTag(com.example.rpgaudiomixer.ui.campaigns.CampaignsTestTags.card(campaignName)).performClick()
            composeRuleHolder.composeRule.waitForIdle()
        }
        composeRuleHolder.composeRule.onNodeWithTag(SessionsTestTags.card(sessionName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun sceneIdByName(name: String): Long = runBlocking {
        entryPoint().sceneRepository().observeScenes().first().first { it.name == name }.id
    }

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(
            applicationContext,
            CampaignDataEntryPoint::class.java,
        )
    }
}
