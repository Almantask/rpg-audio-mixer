package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.fx.FxAudioSelectionRepository
import com.example.rpgaudiomixer.ui.fx.FxLibraryTestTags
import com.example.rpgaudiomixer.ui.library.AudioLibraryTestTags
import com.example.rpgaudiomixer.ui.scenes.ScenesTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class FxLibrarySteps(
    private val composeRuleHolder: MainActivityComposeRule,
    private val fakeMusicPlayer: FakeMusicPlayer,
) {
    private val invalidFiles = mutableSetOf<String>()
    private var currentTrackName: String = ""

    init {
        runBlocking {
            entryPoint().fxRepository().clearAll()
            entryPoint().sceneRepository().clearAll()
        }
        entryPoint().fxTrackTrashRepository().reset()
        fxAudioSelectionRepository().reset()
        fakeMusicPlayer.stopPreview()
    }

    @Given("an audio file {string} is available on my device")
    fun anAudioFileIsAvailableOnMyDevice(fileName: String) {
        invalidFiles -= fileName
    }

    @Given("a file {string} with invalid audio content is on my device")
    fun aFileWithInvalidAudioContentIsOnMyDevice(fileName: String) {
        invalidFiles += fileName
    }

    @Given("I am on the FX Library screen")
    fun iAmOnTheFxLibraryScreen() {
        openSoundEffectsTab()
    }

    @When("I open the Sound Effects tab")
    fun iOpenTheSoundEffectsTab() {
        openSoundEffectsTab()
    }

    @When("I open the FX import file picker")
    fun iOpenTheFxImportFilePicker() {
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithText("Import FX").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I select {string} from the file picker")
    fun iSelectFromTheFilePicker(fileName: String) {
        if (fileName in invalidFiles) {
            fxAudioSelectionRepository().submitInvalidSelection(fileName, "file:///tmp/$fileName")
        } else {
            fxAudioSelectionRepository().submitSelection(fileName, "file:///tmp/$fileName")
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I attempt to import {string}")
    fun iAttemptToImport(fileName: String) {
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithText("Import FX").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        iSelectFromTheFilePicker(fileName)
    }

    @Then("{string} appears in the FX library")
    fun appearsInTheFxLibrary(name: String) {
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("100 free FX tracks are downloaded and added to my library")
    fun freeFxTracksAreDownloadedAndAddedToMyLibrary() {
        composeRuleHolder.composeRule.waitForIdle()
        val tracks = runBlocking { entryPoint().fxRepository().observeTracks().first() }
        assertThat(tracks).hasSize(100)
    }

    @Then("non-audio files such as images, PDFs, and spreadsheets are not shown")
    fun nonAudioFilesAreNotShown() {
        assertThat(fxAudioSelectionRepository().requestedMimeTypes.value).containsExactly("audio/*")
    }

    @Then("I see an error message that the file could not be read as audio")
    fun iSeeAnErrorMessageThatTheFileCouldNotBeReadAsAudio() {
        composeRuleHolder.composeRule.onNodeWithText("The file could not be read as audio.").assertIsDisplayed()
    }

    @Given("I have imported {string}, {string}, {string}")
    fun iHaveImported(first: String, second: String, third: String) {
        runBlocking {
            listOf(first, second, third).forEach { name ->
                entryPoint().fxRepository().upsertTrack(FxTrack(name = name, filePath = "file:///tmp/$name", durationMs = 3_000L))
            }
        }
    }

    @Then("I see all three tracks in the list")
    fun iSeeAllThreeTracksInTheList() {
        listOf("Wolf Howl", "Thunder Crack", "Door Creak").forEach { name ->
            composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @Given("I have not imported any FX tracks")
    fun iHaveNotImportedAnyFxTracks() {
        runBlocking {
            entryPoint().fxRepository().clearAll()
        }
    }

    @Given("{string} is in the FX library")
    fun isInTheFxLibrary(name: String) {
        runBlocking {
            ensureFxTrack(name)
        }
        currentTrackName = name
    }

    @Then("I see the edit screen for {string} with fields for Name and Tags")
    fun iSeeTheEditScreenForWithFieldsForNameAndTags(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.EDIT_DIALOG).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.NAME_INPUT).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.TAGS_SECTION).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @When("I view the {string} row")
    fun iViewTheRow(name: String) {
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.row(name)).assertIsDisplayed()
    }

    @Then("I do not see a three-dot menu icon on the row")
    fun iDoNotSeeAThreeDotMenuIconOnTheRow() {
        composeRuleHolder.composeRule.onNodeWithText("⋮").assertDoesNotExist()
    }

    @Given("I am on the edit screen for {string}")
    fun iAmOnTheEditScreenFor(name: String) {
        isInTheFxLibrary(name)
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.editButton(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I change the name to {string}")
    fun iChangeTheNameTo(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.NAME_INPUT).performTextClearance()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.NAME_INPUT).performTextInput(name)
        currentTrackName = name
    }

    @When("I add the tag {string} from the predefined list")
    fun iAddTheTagFromThePredefinedList(tag: String) {
        composeRuleHolder.composeRule.onNodeWithText(tag).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I add a custom tag {string}")
    fun iAddACustomTag(tag: String) {
        val isFxEditOpen = composeRuleHolder.composeRule
            .onAllNodesWithTag(FxLibraryTestTags.CUSTOM_TAG_INPUT)
            .fetchSemanticsNodes().isNotEmpty()
        val inputTag = if (isFxEditOpen) FxLibraryTestTags.CUSTOM_TAG_INPUT else ScenesTestTags.EDIT_CUSTOM_TAG_INPUT
        val addButtonTag = if (isFxEditOpen) FxLibraryTestTags.CUSTOM_TAG_ADD else ScenesTestTags.EDIT_CUSTOM_TAG_ADD
        composeRuleHolder.composeRule.onNodeWithTag(inputTag).performTextInput(tag)
        composeRuleHolder.composeRule.onNodeWithTag(addButtonTag).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I save")
    fun iSave() {
        composeRuleHolder.composeRule.onNodeWithText("Save").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the track appears as {string} in the FX library")
    fun theTrackAppearsAsInTheFxLibrary(name: String) {
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("{string} shows the {string} tag chip in the library")
    fun showsTheTagChipInTheLibrary(name: String, tag: String) {
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.tagChip(name, tag)).assertIsDisplayed()
    }

    @Then("it is no longer visible in the FX library")
    fun itIsNoLongerVisibleInTheFxLibrary() {
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithText(currentTrackName).assertDoesNotExist()
    }

    @Given("{string} is assigned to the {string} scene's soundboard")
    fun isAssignedToTheSceneSSoundboard(trackName: String, sceneName: String) {
        val sceneId = runBlocking {
            entryPoint().sceneRepository().upsertScene(Scene(name = sceneName, soundboardEffectNames = listOf(trackName)))
        }
        runBlocking {
            entryPoint().sceneRepository().addSoundboardEffect(sceneId, trackName)
            ensureFxTrack(trackName)
        }
        currentTrackName = trackName
    }

    @When("I tap {string} on {string} in the FX library")
    fun iTapOnInTheFxLibrary(action: String, trackName: String) {
        if (action == "Delete") {
            iAmOnTheEditScreenFor(trackName)
            composeRuleHolder.composeRule.onNodeWithText("Delete").performClick()
            composeRuleHolder.composeRule.waitForIdle()
            currentTrackName = trackName
        }
    }

    @Then("{string} no longer appears in the {string} soundboard")
    fun noLongerAppearsInTheSoundboard(trackName: String, sceneName: String) {
        val scene = runBlocking {
            entryPoint().sceneRepository().observeScenes().first().first { it.name == sceneName }
        }
        assertThat(scene.soundboardEffectNames).doesNotContain(trackName)
    }

    @When("I tap the play button on the {string} row")
    fun iTapThePlayButtonOnTheRow(name: String) {
        openSoundEffectsTab()
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.playButton(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentTrackName = name
    }

    @Then("the mini player appears at the bottom of the screen")
    fun theMiniPlayerAppearsAtTheBottomOfTheScreen() {
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.MINI_PLAYER).assertIsDisplayed()
    }

    @Then("{string} begins playing")
    fun beginsPlaying(name: String) {
        assertThat(fakeMusicPlayer.previewedTrack).isEqualTo(trackFilePath(name))
        assertThat(fakeMusicPlayer.isPreviewPlaying).isTrue()
    }

    @Given("the mini player is showing after tapping {string}")
    fun theMiniPlayerIsShowingAfterTapping(name: String) {
        isInTheFxLibrary(name)
        iTapThePlayButtonOnTheRow(name)
    }

    @Then("the mini player displays {string} as the track name")
    fun theMiniPlayerDisplaysAsTheTrackName(name: String) {
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Given("the mini player is showing and {string} is playing")
    fun theMiniPlayerIsShowingAndIsPlaying(name: String) {
        theMiniPlayerIsShowingAfterTapping(name)
    }

    @When("I tap the pause button in the mini player")
    fun iTapThePauseButtonInTheMiniPlayer() {
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.MINI_PLAYER_PAUSE).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("{string} stops playing")
    fun stopsPlaying(name: String) {
        assertThat(fakeMusicPlayer.previewedTrack).isEqualTo(trackFilePath(name))
        assertThat(fakeMusicPlayer.isPreviewPlaying).isFalse()
    }

    @Then("the mini player remains visible")
    fun theMiniPlayerRemainsVisible() {
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.MINI_PLAYER).assertIsDisplayed()
    }

    @Given("the mini player is visible while previewing {string}")
    fun theMiniPlayerIsVisibleWhilePreviewing(name: String) {
        theMiniPlayerIsShowingAfterTapping(name)
    }

    @When("I navigate to the Scenes tab")
    fun iNavigateToTheScenesTab() {
        composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the mini player is no longer visible")
    fun theMiniPlayerIsNoLongerVisible() {
        assertThat(
            composeRuleHolder.composeRule.onAllNodesWithTag(FxLibraryTestTags.MINI_PLAYER).fetchSemanticsNodes(),
        ).isEmpty()
    }

    @Then("{string} has stopped playing")
    fun hasStoppedPlaying(name: String) {
        assertThat(fakeMusicPlayer.previewedTrack).isNull()
        assertThat(fakeMusicPlayer.isPreviewPlaying).isFalse()
    }

    @Given("the mini player is showing {string}")
    fun theMiniPlayerIsShowing(name: String) {
        theMiniPlayerIsShowingAfterTapping(name)
    }

    @When("I tap the play button on {string}")
    fun iTapThePlayButtonOn(name: String) {
        iTapThePlayButtonOnTheRow(name)
    }

    @Then("{string} stops")
    fun stops(name: String) {
        assertThat(fakeMusicPlayer.previewedTrack).isNotEqualTo(trackFilePath(name))
    }

    @Then("the mini player updates to show {string}")
    fun theMiniPlayerUpdatesToShow(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.MINI_PLAYER).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Given("the mini player is visible while previewing an FX track")
    fun theMiniPlayerIsVisibleWhilePreviewingAnFxTrack() {
        theMiniPlayerIsShowingAfterTapping("Thunder Crack")
    }

    @When("I tap the Soundscapes tab")
    fun iTapTheSoundscapesTab() {
        composeRuleHolder.composeRule.onNodeWithTag(AudioLibraryTestTags.SOUND_SCAPES_TAB).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the mini player disappears")
    fun theMiniPlayerDisappears() {
        theMiniPlayerIsNoLongerVisible()
    }

    @Then("audio playback stops")
    fun audioPlaybackStops() {
        assertThat(fakeMusicPlayer.previewedTrack).isNull()
        assertThat(fakeMusicPlayer.isPreviewPlaying).isFalse()
    }

    private fun openSoundEffectsTab() {
        composeRuleHolder.composeRule.onNodeWithText("LIBRARY").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(AudioLibraryTestTags.SOUND_EFFECTS_TAB).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun fxAudioSelectionRepository(): FxAudioSelectionRepository = entryPoint().fxAudioSelectionRepository()

    private fun trackFilePath(name: String): String = runBlocking {
        entryPoint().fxRepository().observeTracks().first().first { it.name == name }.filePath
    }

    private suspend fun ensureFxTrack(name: String): Long {
        return entryPoint().fxRepository().observeTracks().first().firstOrNull { track -> track.name == name }?.id
            ?: entryPoint().fxRepository().upsertTrack(FxTrack(name = name, filePath = "file:///tmp/$name", durationMs = 3_000L))
    }

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(
            applicationContext,
            CampaignDataEntryPoint::class.java,
        )
    }
}
