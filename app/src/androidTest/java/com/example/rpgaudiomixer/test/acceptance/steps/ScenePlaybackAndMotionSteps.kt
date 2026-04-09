package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.app.components.ArcanumTopBarTestTags
import com.example.rpgaudiomixer.app.components.BottomNavTestTags
import com.example.rpgaudiomixer.app.motion.MotionTransitionType
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.campaigns.CampaignsTestTags
import com.example.rpgaudiomixer.ui.fx.FxLibraryTestTags
import com.example.rpgaudiomixer.ui.scenes.ActiveSceneSoundscapesTestTags
import com.example.rpgaudiomixer.ui.scenes.ScenesTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class ScenePlaybackAndMotionSteps(
    private val composeRuleHolder: MainActivityComposeRule,
    private val fakeMusicPlayer: FakeMusicPlayer,
) {
    init {
        fakeMusicPlayer.reset()
    }

    @Given("I have a scene {string} with soundscape categories")
    fun iHaveASceneWithSoundscapeCategories(sceneName: String) {
        ensureSceneWithSoundscapes(sceneName)
        openScenesTab()
    }

    @Given("{string} is the current playing scene")
    fun isTheCurrentPlayingScene(sceneName: String) {
        ensureSceneWithSoundscapes(sceneName)
        runBlocking {
            entryPoint().scenePlaybackController().playScene(sceneIdByName(sceneName))
        }
    }

    @Given("{string} has a saved Master Atmosphere value of {int}%")
    fun hasASavedMasterAtmosphereValueOf(sceneName: String, percent: Int) {
        ensureSceneWithSoundscapes(sceneName)
        runBlocking {
            entryPoint().sceneRepository().updateSceneAtmosphereVolume(sceneIdByName(sceneName), percent)
        }
    }

    @Given("I am on the Home tab")
    fun iAmOnTheHomeTab() {
        composeRuleHolder.composeRule.onNodeWithText("HOME").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("I am on any main screen")
    fun iAmOnAnyMainScreen() {
        iAmOnTheHomeTab()
    }

    @Given("no mini player is visible")
    fun noMiniPlayerIsVisible() {
        openSoundEffectsTab()
        assertThat(composeRuleHolder.composeRule.onAllNodesWithTag(FxLibraryTestTags.MINI_PLAYER).fetchSemanticsNodes()).isEmpty()
    }

    @Given("the mini player is visible")
    fun theMiniPlayerIsVisible() {
        ensureFxTrack("Thunder Crack")
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.playButton("Thunder Crack")).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the play button on the {string} scene card")
    fun iTapThePlayButtonOnTheSceneCard(sceneName: String) {
        ensureSceneWithSoundscapes(sceneName)
        openScenesTab()
        composeRuleHolder.composeRule.onNodeWithTag(ScenesTestTags.playButton(sceneName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the {string} scene card \\(not the play button\\)")
    fun iTapTheSceneCardNotThePlayButton(sceneName: String) {
        ensureSceneWithSoundscapes(sceneName)
        openScenesTab()
        composeRuleHolder.composeRule.onNodeWithTag(ScenesTestTags.card(sceneName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I navigate back to the scenes list")
    fun iNavigateBackToTheScenesList() {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.BACK_ARROW).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap on a campaign card to open its Sessions list")
    fun iTapOnACampaignCardToOpenItsSessionsList() {
        ensureCampaignWithSession("Stormlight", "Session 1")
        composeRuleHolder.composeRule.onNodeWithTag(CampaignsTestTags.card("Stormlight")).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the Campaigns tab in the bottom bar")
    fun iTapTheCampaignsTabInTheBottomBar() {
        composeRuleHolder.composeRule.onNodeWithTag(BottomNavTestTags.item(MainNavDestination.CAMPAIGNS)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the settings gear to open the Credits")
    fun iTapTheSettingsGearToOpenTheCredits() {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.GEAR_ICON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("a screen transition occurs")
    fun aScreenTransitionOccurs() {
        iTapTheCampaignsTabInTheBottomBar()
    }

    @When("I tap preview on an FX track")
    fun iTapPreviewOnAnFxTrack() {
        ensureFxTrack("Thunder Crack")
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.playButton("Thunder Crack")).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the close button or navigate away")
    fun iTapTheCloseButtonOrNavigateAway() {
        composeRuleHolder.composeRule.onNodeWithTag(BottomNavTestTags.item(MainNavDestination.SCENES)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the scene's soundscapes begin playing with a fade-in")
    fun theScenesSoundscapesBeginPlayingWithAFadeIn() {
        assertThat(fakeMusicPlayer.loopingPlayed.lastOrNull()).isEqualTo("scene:${sceneIdByName(currentActiveSceneName())}")
    }

    @Then("the {string} audio fades out while the {string} audio fades in simultaneously")
    fun theAudioFadesOutWhileTheAudioFadesInSimultaneously(previousScene: String, nextScene: String) {
        val playbackState = entryPoint().scenePlaybackController().state.value
        assertThat(playbackState.previousSceneName).isEqualTo(previousScene)
        assertThat(playbackState.currentSceneName).isEqualTo(nextScene)
        assertThat(fakeMusicPlayer.loopingPlayed.takeLast(2)).contains("scene:${sceneIdByName(previousScene)}", "scene:${sceneIdByName(nextScene)}")
    }

    @Then("{string} audio is not playing")
    fun audioIsNotPlaying(sceneName: String) {
        assertThat(fakeMusicPlayer.loopingPlayed).doesNotContain("scene:${sceneIdByName(sceneName)}")
    }

    @Then("{string} audio continues playing in the background")
    fun audioContinuesPlayingInTheBackground(sceneName: String) {
        assertThat(fakeMusicPlayer.loopingPlayed).contains("scene:${sceneIdByName(sceneName)}")
    }

    @Then("the Master Atmosphere slider is immediately at {int}% with no animation")
    fun theMasterAtmosphereSliderIsImmediatelyAtWithNoAnimation(percent: Int) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.MASTER_SLIDER).assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                androidx.compose.ui.semantics.ProgressBarRangeInfo(percent.toFloat(), 0f..100f),
            ),
        )
        composeRuleHolder.composeRule.onNodeWithText("$percent%").assertIsDisplayed()
    }

    @Then("the campaign card expands smoothly to fill the screen background")
    fun theCampaignCardExpandsSmoothlyToFillTheScreenBackground() {
        assertThat(entryPoint().motionSystemStateRepository().lastTransition.value.type)
            .isEqualTo(MotionTransitionType.CONTAINER_TRANSFORM)
    }

    @Then("the top and bottom navigation bars remain fixed")
    fun theTopAndBottomNavigationBarsRemainFixed() {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.TITLE).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(BottomNavTestTags.item(MainNavDestination.CAMPAIGNS)).assertIsDisplayed()
    }

    @Then("the Home screen fades and slides out horizontally")
    fun theHomeScreenFadesAndSlidesOutHorizontally() {
        val transition = entryPoint().motionSystemStateRepository().lastTransition.value
        assertThat(transition.type).isEqualTo(MotionTransitionType.SHARED_X_AXIS)
        assertThat(transition.source).isEqualTo(MainNavDestination.HOME.route)
    }

    @Then("the Campaigns screen fades and slides in horizontally from the right")
    fun theCampaignsScreenFadesAndSlidesInHorizontallyFromTheRight() {
        val transition = entryPoint().motionSystemStateRepository().lastTransition.value
        assertThat(transition.target).isEqualTo(MainNavDestination.CAMPAIGNS.route)
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.CAMPAIGNS).assertIsDisplayed()
    }

    @Then("the outgoing screen fades out and scales up slightly")
    fun theOutgoingScreenFadesOutAndScalesUpSlightly() {
        assertThat(entryPoint().motionSystemStateRepository().lastTransition.value.type)
            .isEqualTo(MotionTransitionType.SHARED_Z_AXIS)
    }

    @Then("the Credits screen fades in and scales up from slightly smaller")
    fun theCreditsScreenFadesInAndScalesUpFromSlightlySmaller() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.SETTINGS).assertIsDisplayed()
    }

    @Then("the incoming screen becomes interactive within a short time")
    fun theIncomingScreenBecomesInteractiveWithinAShortTime() {
        composeRuleHolder.composeRule.onNodeWithTag(BottomNavTestTags.item(MainNavDestination.CAMPAIGNS)).assertIsDisplayed()
    }

    @Then("the mini player slides up smoothly from the bottom navigation bar")
    fun theMiniPlayerSlidesUpSmoothlyFromTheBottomNavigationBar() {
        assertThat(entryPoint().motionSystemStateRepository().lastTransition.value.type)
            .isEqualTo(MotionTransitionType.SHARED_Y_AXIS_ENTER)
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.MINI_PLAYER).assertIsDisplayed()
    }

    @Then("the mini player slides down smoothly to disappear")
    fun theMiniPlayerSlidesDownSmoothlyToDisappear() {
        assertThat(entryPoint().motionSystemStateRepository().lastTransition.value.type)
            .isEqualTo(MotionTransitionType.SHARED_Y_AXIS_EXIT)
    }

    private fun currentActiveSceneName(): String = runBlocking {
        entryPoint().scenePlaybackController().state.value.currentSceneName ?: entryPoint().sceneRepository().observeScenes().first().first().name
    }

    private fun ensureSceneWithSoundscapes(sceneName: String) {
        runBlocking {
            val repository = entryPoint().sceneRepository()
            val sceneId = repository.observeScenes().first().firstOrNull { it.name == sceneName }?.id
                ?: repository.upsertScene(Scene(name = sceneName))
            val categoryNames = listOf("$sceneName Atmosphere")
            categoryNames.forEach { categoryName ->
                val soundscapeRepository = entryPoint().soundscapeRepository()
                val existingCategory = soundscapeRepository.observeCategories().first().firstOrNull { it.name == categoryName }
                val categoryId = existingCategory?.id ?: soundscapeRepository.createCategory(categoryName)
                if (existingCategory == null) {
                    soundscapeRepository.upsertTrack(
                        SoundscapeTrack(
                            categoryId = categoryId,
                            name = "$categoryName Loop",
                            filePath = "demo://$categoryName/loop",
                        ),
                    )
                }
                repository.addSoundscapeCategory(sceneId, categoryName)
            }
        }
    }

    private fun ensureCampaignWithSession(campaignName: String, sessionName: String) {
        runBlocking {
            val campaignId = entryPoint().campaignRepository().observeCampaigns().first().firstOrNull { it.name == campaignName }?.id
                ?: entryPoint().campaignRepository().upsertCampaign(Campaign(name = campaignName))
            if (entryPoint().sessionRepository().observeSessions(campaignId).first().none { it.name == sessionName }) {
                entryPoint().sessionRepository().upsertSession(Session(campaignId = campaignId, name = sessionName))
            }
        }
    }

    private fun ensureFxTrack(trackName: String) {
        runBlocking {
            val repository = entryPoint().fxRepository()
            if (repository.observeTracks().first().none { it.name == trackName }) {
                repository.upsertTrack(FxTrack(name = trackName, filePath = "demo://fx/$trackName", durationMs = 3_000L))
            }
        }
    }

    private fun openScenesTab() {
        composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun openSoundEffectsTab() {
        composeRuleHolder.composeRule.onNodeWithText("LIBRARY").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithText("Sound Effects").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun sceneIdByName(name: String): Long = runBlocking {
        entryPoint().sceneRepository().observeScenes().first().first { it.name == name }.id
    }

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(applicationContext, CampaignDataEntryPoint::class.java)
    }
}
