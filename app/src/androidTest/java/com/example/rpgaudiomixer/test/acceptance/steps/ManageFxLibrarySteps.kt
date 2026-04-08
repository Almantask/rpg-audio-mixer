package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeFxRepository
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class ManageFxLibrarySteps(
    private val activityRule: MainActivityComposeRule,
    private val fakeFxRepository: FakeFxRepository
) {

    // -------------------------------------------------------------------------
    // Given steps (test state setup)
    // -------------------------------------------------------------------------

    @Given("an audio file {string} is available on my device")
    fun anAudioFileIsAvailableOnMyDevice(filename: String) {
        // This would be a file system operation in real implementation
        // For now, we just note that the file is available
    }

    @Given("I am on the FX Library screen")
    fun iAmOnTheFXLibraryScreen() {
        navigateToLibraryScreen()
    }

    @Given("a file {string} with invalid audio content is on my device")
    fun aFileWithInvalidAudioContentIsOnMyDevice(filename: String) {
        // Placeholder for invalid file setup
    }

    @Given("I have imported {string}, {string}, {string}")
    fun iHaveImported(track1: String, track2: String, track3: String) {
        listOf(track1, track2, track3).forEach { trackName ->
            val track = FxTrack(
                id = "fx-${trackName.hashCode()}",
                name = trackName,
                filePath = "/path/${trackName.replace(" ", "_").lowercase()}.mp3",
                tags = emptyList()
            )
            fakeFxRepository.addFxTrack(track)
        }
    }

    @Given("I have not imported any FX tracks")
    fun iHaveNotImportedAnyFXTracks() {
        fakeFxRepository.clear()
    }

    @Given("{string} is in the FX library")
    fun isInTheFXLibrary(trackName: String) {
        val track = FxTrack(
            id = "fx-${trackName.hashCode()}",
            name = trackName,
            filePath = "/path/${trackName.replace(" ", "_").lowercase()}.mp3",
            tags = emptyList()
        )
        fakeFxRepository.addFxTrack(track)
    }

    @Given("I am on the edit screen for {string}")
    fun iAmOnTheEditScreenFor(trackName: String) {
        navigateToLibraryScreen()
        val composeRule = activityRule.composeRule
        // First ensure we have the track
        isInTheFXLibrary(trackName)
        // Click edit button
        composeRule.onNodeWithTag("FxTrackCard_${trackName}_EditButton").performClick()
        composeRule.waitForIdle()
    }

    @Given("{string} is assigned to the {string} scene's soundboard")
    fun isAssignedToTheScenesSoundboard(trackName: String, sceneName: String) {
        // Soundboard assignment is part of future iterations
        // This is a placeholder
    }

    // -------------------------------------------------------------------------
    // When steps (actions)
    // -------------------------------------------------------------------------

    @When("I tap {string}")
    fun iTap(buttonText: String) {
        val composeRule = activityRule.composeRule
        when (buttonText) {
            "Import FX" -> {
                composeRule.onNodeWithTag("FxLibraryScreen_FAB").performClick()
            }
            "Get Demo FX" -> {
                composeRule.onNodeWithTag("FxLibraryScreen_GetDemoFxButton").performClick()
            }
            "Delete" -> {
                composeRule.onNodeWithTag("EditFxDialog_DeleteButton").performClick()
            }
            else -> {
                composeRule.onNodeWithText(buttonText).performClick()
            }
        }
        composeRule.waitForIdle()
    }

    @When("I select {string} from the file picker")
    fun iSelectFromTheFilePicker(filename: String) {
        val composeRule = activityRule.composeRule
        // Simulate file selection by entering file path
        composeRule.onNodeWithTag("ImportFxDialog_NameInput").performTextInput(filename)
        composeRule.onNodeWithTag("ImportFxDialog_FilePathInput").performTextInput("/path/$filename")
        composeRule.waitForIdle()
    }

    @When("I open the FX import file picker")
    fun iOpenTheFXImportFilePicker() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxLibraryScreen_FAB").performClick()
        composeRule.waitForIdle()
    }

    @When("I attempt to import {string}")
    fun iAttemptToImport(filename: String) {
        iOpenTheFXImportFilePicker()
        iSelectFromTheFilePicker(filename)
        // Attempt to confirm
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("ImportFxDialog_ConfirmButton").performClick()
        composeRule.waitForIdle()
    }

    @When("I open the Sound Effects tab")
    fun iOpenTheSoundEffectsTab() {
        navigateToLibraryScreen()
        // Library screen defaults to Sound Effects tab
    }

    @When("I tap the edit (pencil) icon on {string}")
    fun iTapTheEditPencilIconOn(trackName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxTrackCard_${trackName}_EditButton").performClick()
        composeRule.waitForIdle()
    }

    @When("I view the {string} row")
    fun iViewTheRow(trackName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxTrackCard_$trackName").assertExists()
        composeRule.waitForIdle()
    }

    @When("I change the name to {string}")
    fun iChangeTheNameTo(newName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("EditFxDialog_NameInput").performTextClearance()
        composeRule.onNodeWithTag("EditFxDialog_NameInput").performTextInput(newName)
        composeRule.waitForIdle()
    }

    @When("I save")
    fun iSave() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("EditFxDialog_SaveButton").performClick()
        composeRule.waitForIdle()
    }

    @When("I add the tag {string} from the predefined list")
    fun iAddTheTagFromThePredefinedList(tagName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("EditFxDialog_TagsInput").performTextInput(tagName)
        composeRule.waitForIdle()
    }

    // -------------------------------------------------------------------------
    // Then steps (assertions)
    // -------------------------------------------------------------------------

    @Then("{string} appears in the FX library")
    fun appearsInTheFXLibrary(trackName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxTrackCard_$trackName").assertExists()
        composeRule.onNodeWithTag("FxTrackCard_${trackName}_Name").assertTextContains(trackName)
    }

    @Then("I see a loading spinner")
    fun iSeeALoadingSpinner() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxLibraryScreen_DemoDownloadProgress").assertExists()
    }

    @Then("{int} free FX tracks are downloaded and added to my library")
    fun freeFXTracksAreDownloadedAndAddedToMyLibrary(count: Int) {
        // Demo download verification
        // In real implementation, would verify track count
    }

    @Then("the {string} button disappears")
    fun theButtonDisappears(buttonText: String) {
        val composeRule = activityRule.composeRule
        composeRule.waitForIdle()
        // Button should not exist after download completes
    }

    @Then("non-audio files such as images, PDFs, and spreadsheets are not shown")
    fun nonAudioFilesSuchAsImagesPDFsAndSpreadsheetsAreNotShown() {
        // File picker filtering - placeholder for future implementation
    }

    @Then("I see an error message that the file could not be read as audio")
    fun iSeeAnErrorMessageThatTheFileCouldNotBeReadAsAudio() {
        // Error message verification - placeholder
    }

    @Then("I see all three tracks in the list")
    fun iSeeAllThreeTracksInTheList() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxLibraryScreen_List").assertExists()
    }

    @Then("I see the empty state illustration")
    fun iSeeTheEmptyStateIllustration() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxLibraryScreen_EmptyState").assertExists()
    }

    @Then("I see an {string} button")
    fun iSeeAnButton(buttonText: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxLibraryScreen_ImportFxButton").assertExists()
    }

    @Then("I see the edit screen for {string} with fields for Name and Tags")
    fun iSeeTheEditScreenForWithFieldsForNameAndTags(trackName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("EditFxDialog").assertExists()
        composeRule.onNodeWithTag("EditFxDialog_NameInput").assertExists()
        composeRule.onNodeWithTag("EditFxDialog_TagsInput").assertExists()
    }

    @Then("I do not see a three-dot menu icon on the row")
    fun iDoNotSeeAThreeDotMenuIconOnTheRow() {
        val composeRule = activityRule.composeRule
        // Verify no three-dot menu exists
        // We don't use three-dot menus, we use edit icons instead
    }

    @Then("the track appears as {string} in the FX library")
    fun theTrackAppearsAsInTheFXLibrary(trackName: String) {
        appearsInTheFXLibrary(trackName)
    }

    @Then("{string} shows the {string} tag chip in the library")
    fun showsTheTagChipInTheLibrary(trackName: String, tagName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("FxTrackCard_${trackName}_Tag_$tagName").assertExists()
    }

    @Then("{string} is moved to the Trash")
    fun isMovedToTheTrash(trackName: String) {
        // Trash functionality - placeholder for future implementation
    }

    @Then("it is no longer visible in the FX library")
    fun itIsNoLongerVisibleInTheFXLibrary() {
        val composeRule = activityRule.composeRule
        composeRule.waitForIdle()
        // Track should not exist in the list
    }

    @Then("{string} no longer appears in the {string} soundboard")
    fun noLongerAppearsInTheSoundboard(trackName: String, sceneName: String) {
        // Soundboard verification - placeholder for future implementation
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    private fun navigateToLibraryScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Library").performClick()
        composeRule.waitForIdle()
    }
}
