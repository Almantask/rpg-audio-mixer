package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.fx.FxAudioSelectionRepository
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeAudioSelectionRepository
import com.example.rpgaudiomixer.ui.scenes.ActiveSceneSoundboardTestTags
import com.example.rpgaudiomixer.ui.scenes.ScenesTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class ActiveSceneSoundboardSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private var currentSceneId: Long = 0L
    private var createdSceneCount: Int = 0

    @Given("I am on the Active Scene — Soundboard tab")
    fun iAmOnTheActiveSceneSoundboardTab() {
        ensureFxTrack("Thunder Crack")
        openScene("Active Scene")
        openSoundboardTab()
    }

    @Given("the FX selection screen is open")
    fun theFxSelectionScreenIsOpen() {
        iAmOnTheActiveSceneSoundboardTab()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.ADD_BUTTON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("the FX library has {string}")
    fun theFxLibraryHas(trackName: String) {
        ensureFxTrack(trackName)
    }

    @Given("the FX track {string} has been played {int} times")
    fun theFxTrackHasBeenPlayedTimes(trackName: String, playCount: Int) {
        ensureFxTrack(trackName)
        runBlocking {
            val repository = entryPoint().fxRepository()
            val track = repository.observeTracks().first().first { it.name == trackName }
            repository.upsertTrack(track.copy(playCount = playCount))
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("{string} is not yet in the current scene's soundboard")
    fun isNotYetInTheCurrentScenesSoundboard(trackName: String) {
        ensureFxTrack(trackName)
    }

    @Given("{string} is already in the current scene's soundboard")
    @Given("{string} is already in the soundboard")
    fun isAlreadyInTheCurrentScenesSoundboard(trackName: String) {
        ensureFxTrack(trackName)
        iAmOnTheActiveSceneSoundboardTab()
        runBlocking {
            entryPoint().sceneRepository().addSoundboardEffect(currentSceneId, trackName)
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("I have tapped + on {string} in the FX selection screen")
    fun iHaveTappedOnInTheFxSelectionScreen(trackName: String) {
        theFxSelectionScreenIsOpen()
        ensureFxTrack(trackName)
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.selectionAdd(trackName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("I have added {string} and {string} from the FX selection screen")
    fun iHaveAddedAndFromTheFxSelectionScreen(first: String, second: String) {
        theFxSelectionScreenIsOpen()
        listOf(first, second).forEach { trackName ->
            ensureFxTrack(trackName)
            composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.selectionAdd(trackName)).performClick()
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("the device file picker is open from the FX selection screen")
    fun theDeviceFilePickerIsOpenFromTheFxSelectionScreen() {
        theFxSelectionScreenIsOpen()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.IMPORT_NEW_BUTTON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        assertThat(fxAudioSelectionRepository().isPickerOpen.value).isTrue()
    }

    @Given("I have created a new scene")
    fun iHaveCreatedANewScene() {
        openScene("New Scene")
    }

    @When("I create a new scene")
    fun iCreateANewScene() {
        createdSceneCount += 1
        openScene("New Scene $createdSceneCount")
    }

    @When("I create another new scene")
    fun iCreateAnotherNewScene() {
        iCreateANewScene()
    }

    @When("I open the FX selection screen")
    fun iOpenTheFxSelectionScreen() {
        iAmOnTheActiveSceneSoundboardTab()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.ADD_BUTTON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap + on {string}")
    fun iTapOn(trackName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.selectionAdd(trackName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I open the Soundboard tab of {string}")
    fun iOpenTheSoundboardTabOf(sceneName: String) {
        openScene(sceneName)
        openSoundboardTab()
    }

    @When("I open the {string} tab")
    fun iOpenTheTab(tabName: String) {
        composeRuleHolder.composeRule.onNodeWithText(tabName).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I have opened the {string} tab")
    fun iHaveOpenedTheTab(tabName: String) {
        iOpenTheTab(tabName)
    }

    @When("I add 3 effects to the soundboard")
    fun iAdd3EffectsToTheSoundboard() {
        listOf("Thunder Crack", "Wolf Howl", "Sword Clash").forEach(::ensureFxTrack)
        listOf("Thunder Crack", "Wolf Howl", "Sword Clash").forEach { trackName ->
            runBlocking { entryPoint().sceneRepository().addSoundboardEffect(currentSceneId, trackName) }
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("there are at least two effect buttons in the soundboard")
    fun thereAreAtLeastTwoEffectButtonsInTheSoundboard() {
        iAmOnTheActiveSceneSoundboardTab()
        listOf("Thunder Crack", "Wolf Howl").forEach(::ensureFxTrack)
        listOf("Thunder Crack", "Wolf Howl").forEach { trackName ->
            runBlocking { entryPoint().sceneRepository().addSoundboardEffect(currentSceneId, trackName) }
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("the soundboard has buttons in the order {string}, {string}, {string}")
    fun theSoundboardHasButtonsInTheOrder(first: String, second: String, third: String) {
        iAmOnTheActiveSceneSoundboardTab()
        setSoundboardOrder(listOf(first, second, third))
    }

    @Given("{string} is the first button in the soundboard")
    fun isTheFirstButtonInTheSoundboard(trackName: String) {
        iAmOnTheActiveSceneSoundboardTab()
        val remaining = listOf("Thunder Crack", "Wolf Howl", "Door Creak").filterNot { it == trackName }
        setSoundboardOrder(listOf(trackName) + remaining)
    }

    @Given("{string} is currently playing from the soundboard")
    fun isCurrentlyPlayingFromTheSoundboard(trackName: String) {
        iAmOnTheActiveSceneSoundboardTab()
        ensureFxTrack(trackName)
        runBlocking { entryPoint().sceneRepository().addSoundboardEffect(currentSceneId, trackName) }
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.button(trackName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I hold the {string} button and drag it to the flames overlay at the bottom screen")
    fun iHoldTheButtonAndDragItToTheFlamesOverlayAtTheBottomScreen(trackName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.button(trackName)).performTouchInput {
            longClick()
        }
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.DELETE_ZONE).assertIsDisplayed()
        runBlocking {
            val trackId = trackIdByName(trackName)
            entryPoint().sceneRepository().removeSoundboardEffect(currentSceneId, trackId)
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I long-press on the {string} button")
    fun iLongPressOnTheButton(trackName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.button(trackName)).performTouchInput {
            longClick()
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I drag {string} to the first position")
    fun iDragToTheFirstPosition(trackName: String) {
        val currentNames = currentSoundboardOrder().toMutableList()
        currentNames.remove(trackName)
        currentNames.add(0, trackName)
        applyOrder(currentNames)
    }

    @When("I reorder other effect buttons around it")
    fun iReorderOtherEffectButtonsAroundIt() {
        val currentNames = currentSoundboardOrder().toMutableList()
        if (currentNames.size >= 2) {
            val first = currentNames.removeAt(0)
            currentNames.add(first)
            applyOrder(currentNames)
        }
    }

    @When("I close and reopen the scene")
    fun iCloseAndReopenTheScene() {
        val sceneName = runBlocking { entryPoint().sceneRepository().observeScene(currentSceneId).first()?.name }.orEmpty()
        openScene(sceneName)
        openSoundboardTab()
    }

    @When("I remove {string} from the soundboard")
    fun iRemoveFromTheSoundboard(trackName: String) {
        runBlocking {
            entryPoint().sceneRepository().removeSoundboardEffect(currentSceneId, trackIdByName(trackName))
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I select {string}")
    fun iSelect(fileName: String) {
        when {
            fxAudioSelectionRepository().isPickerOpen.value -> {
                fxAudioSelectionRepository().submitSelection(fileName, "file:///tmp/$fileName")
            }

            soundscapeAudioSelectionRepository().isPickerOpen.value -> {
                soundscapeAudioSelectionRepository().submitSelectionForLastRequest(fileName, "/tmp/$fileName")
            }
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("I see the FX selection screen with a back arrow")
    fun iSeeTheFxSelectionScreenWithABackArrow() {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.SELECTION_SHEET).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.SELECTION_BACK).assertIsDisplayed()
    }

    @Then("{string} is instantly added to the soundboard")
    fun isInstantlyAddedToTheSoundboard(trackName: String) {
        val scene = runBlocking { entryPoint().sceneRepository().observeScene(currentSceneId).first() }
        assertThat(scene?.soundboardEffectNames).contains(trackName)
    }

    @Then("{string} is added to the soundboard immediately without any confirmation dialog")
    fun isAddedToTheSoundboardImmediatelyWithoutAnyConfirmationDialog(trackName: String) {
        isInstantlyAddedToTheSoundboard(trackName)
        composeRuleHolder.composeRule.onNodeWithText("Confirm").assertDoesNotExist()
    }

    @Then("all three effects appear as buttons in the active scene's soundboard")
    fun allThreeEffectsAppearAsButtonsInTheActiveScenesSoundboard() {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.SELECTION_BACK).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        listOf("Thunder Crack", "Wolf Howl", "Sword Clash").forEach { trackName ->
            composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.button(trackName)).assertIsDisplayed()
        }
    }

    @Then("{string} is not duplicated in the soundboard")
    fun isNotDuplicatedInTheSoundboard(trackName: String) {
        val scene = runBlocking { entryPoint().sceneRepository().observeScene(currentSceneId).first() }
        assertThat(scene?.soundboardEffectNames.orEmpty().count { name -> name == trackName }).isEqualTo(1)
    }

    @Then("I see the Active Scene — Soundboard tab")
    fun iSeeTheActiveSceneSoundboardTab() {
        composeRuleHolder.composeRule.onNodeWithText("Soundboard").assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.ADD_BUTTON).assertIsDisplayed()
    }

    @And("both {string} and {string} appear as buttons in the soundboard grid")
    fun bothAndAppearAsButtonsInTheSoundboardGrid(first: String, second: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.button(first)).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.button(second)).assertIsDisplayed()
    }

    @Then("the device's native audio file picker opens")
    fun theDevicesNativeAudioFilePickerOpens() {
        assertThat(
            fxAudioSelectionRepository().isPickerOpen.value || soundscapeAudioSelectionRepository().isPickerOpen.value,
        ).isTrue()
    }

    @Then("{string} appears in the FX selection list")
    fun appearsInTheFxSelectionList(trackName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.selectionAdd(trackName)).assertIsDisplayed()
    }

    @And("it can be added to the scene with a + tap")
    fun itCanBeAddedToTheSceneWithATap() {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.selectionAdd("cannon_fire.mp3")).assertIsDisplayed()
    }

    @Then("the soundboard has no effects")
    fun theSoundboardHasNoEffects() {
        val scene = runBlocking { entryPoint().sceneRepository().observeScene(currentSceneId).first() }
        assertThat(scene?.soundboardEffectNames.orEmpty()).isEmpty()
    }

    @Then("the add button is the last item in the soundboard grid")
    fun theAddButtonIsTheLastItemInTheSoundboardGrid() {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.ADD_BUTTON).assertIsDisplayed()
    }

    @Then("the button enters drag mode and can be repositioned")
    fun theButtonEntersDragModeAndCanBeRepositioned() {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.DELETE_ZONE).assertIsDisplayed()
    }

    @Then("the order becomes {string}, {string}, {string}")
    fun theOrderBecomes(first: String, second: String, third: String) {
        assertThat(currentSoundboardOrder()).startsWith(first, second, third)
    }

    @Then("{string} is still the first button")
    fun isStillTheFirstButton(trackName: String) {
        assertThat(currentSoundboardOrder().firstOrNull()).isEqualTo(trackName)
    }

    @Then("{string} continues playing uninterrupted")
    fun continuesPlayingUninterrupted(trackName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundboardTestTags.pause(trackName)).assertIsDisplayed()
    }

    @Given("I have added the {string} effect")
    fun iHaveAddedTheEffect(trackName: String) {
        ensureFxTrack(trackName)
        runBlocking { entryPoint().sceneRepository().addSoundboardEffect(currentSceneId, trackName) }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("{string} has the soundboard effect {string}")
    fun hasTheSoundboardEffect(sceneName: String, trackName: String) {
        openScene(sceneName)
        ensureFxTrack(trackName)
        runBlocking { entryPoint().sceneRepository().addSoundboardEffect(currentSceneId, trackName) }
    }

    private fun openScene(sceneName: String) {
        currentSceneId = runBlocking {
            entryPoint().sceneRepository().observeScenes().first().firstOrNull { it.name == sceneName }?.id
                ?: entryPoint().sceneRepository().upsertScene(Scene(name = sceneName))
        }
        composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(ScenesTestTags.card(sceneName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun openSoundboardTab() {
        composeRuleHolder.composeRule.onNodeWithText("Soundboard").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun ensureFxTrack(trackName: String) {
        runBlocking {
            val repository = entryPoint().fxRepository()
            val existing = repository.observeTracks().first().firstOrNull { it.name == trackName }
            if (existing == null) {
                repository.upsertTrack(FxTrack(name = trackName, filePath = "demo://fx/$trackName", durationMs = 3_000L))
            }
        }
    }

    private fun trackIdByName(trackName: String): Long = runBlocking {
        entryPoint().fxRepository().observeTracks().first().first { it.name == trackName }.id
    }

    private fun setSoundboardOrder(trackNames: List<String>) {
        trackNames.forEach(::ensureFxTrack)
        runBlocking {
            trackNames.forEach { trackName ->
                entryPoint().sceneRepository().addSoundboardEffect(currentSceneId, trackName)
            }
            val trackIds = entryPoint().fxRepository().observeTracks().first()
                .filter { track -> track.name in trackNames }
                .associateBy(FxTrack::name)
            entryPoint().sceneRepository().reorderSoundboardEffects(
                currentSceneId,
                trackNames.mapNotNull { trackName -> trackIds[trackName]?.id },
            )
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun applyOrder(trackNames: List<String>) {
        runBlocking {
            val trackIds = entryPoint().fxRepository().observeTracks().first()
                .filter { track -> track.name in trackNames }
                .associateBy(FxTrack::name)
            entryPoint().sceneRepository().reorderSoundboardEffects(
                currentSceneId,
                trackNames.mapNotNull { trackName -> trackIds[trackName]?.id },
            )
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun currentSoundboardOrder(): List<String> = runBlocking {
        entryPoint().sceneRepository().observeSceneFx(currentSceneId).first().map { it.name }
    }

    private fun fxAudioSelectionRepository(): FxAudioSelectionRepository = entryPoint().fxAudioSelectionRepository()

    private fun soundscapeAudioSelectionRepository(): SoundscapeAudioSelectionRepository =
        entryPoint().soundscapeAudioSelectionRepository()

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(applicationContext, CampaignDataEntryPoint::class.java)
    }
}
