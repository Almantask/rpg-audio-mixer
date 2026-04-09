package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.app.components.ArcanumTopBarTestTags
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.fx.FxLibraryTestTags
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeAudioSelectionRepository
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeComposerTestTags
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeLibraryTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class SoundscapeSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private var currentCategoryName: String = ""
    private var currentCategoryId: Long = 0L

    init {
        runBlocking {
            entryPoint().soundscapeRepository().clearAll()
        }
        entryPoint().soundscapeCategoryTrashRepository().reset()
        audioSelectionRepository().reset()
    }

    @Given("I have created categories {string}, {string}, {string}")
    fun iHaveCreatedCategories(first: String, second: String, third: String) {
        runBlocking {
            listOf(first, second, third).forEach { name ->
                entryPoint().soundscapeRepository().createCategory(name)
            }
        }
    }

    @Given("{string} has {int} tracks at level I, {int} at level II, and {int} at level III")
    fun categoryHasTracksAtLevels(name: String, levelI: Int, levelII: Int, levelIII: Int) {
        val categoryId = ensureCategory(name)
        runBlocking {
            repeat(levelI) { index ->
                entryPoint().soundscapeRepository().upsertTrack(
                    SoundscapeTrack(categoryId = categoryId, name = "$name-I-$index", filePath = "demo://$name/I/$index", intensityLevel = IntensityLevel.I),
                )
            }
            repeat(levelII) { index ->
                entryPoint().soundscapeRepository().upsertTrack(
                    SoundscapeTrack(categoryId = categoryId, name = "$name-II-$index", filePath = "demo://$name/II/$index", intensityLevel = IntensityLevel.II),
                )
            }
            repeat(levelIII) { index ->
                entryPoint().soundscapeRepository().upsertTrack(
                    SoundscapeTrack(categoryId = categoryId, name = "$name-III-$index", filePath = "demo://$name/III/$index", intensityLevel = IntensityLevel.III),
                )
            }
        }
    }

    @Given("{string} is in the soundscape categories list")
    fun isInTheSoundscapeCategoriesList(name: String) {
        currentCategoryId = ensureCategory(name)
        currentCategoryName = name
    }

    @Given("I have not created any soundscape categories")
    fun iHaveNotCreatedAnySoundscapeCategories() {
        runBlocking {
            entryPoint().soundscapeRepository().clearAll()
        }
    }

    @Given("I am on the Soundscapes Library screen")
    @When("I open the Library — Soundscapes tab")
    fun iOpenTheLibrarySoundscapesTab() {
        composeRuleHolder.composeRule.onNodeWithText("LIBRARY").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the edit \\(pencil\\) icon on {string}")
    fun iTapTheEditPencilIconOn(name: String) {
        val fxExists = runBlocking {
            entryPoint().fxRepository().observeTracks().first().any { track -> track.name == name }
        }
        if (fxExists) {
            composeRuleHolder.composeRule.onNodeWithText("LIBRARY").performClick()
            composeRuleHolder.composeRule.waitForIdle()
            composeRuleHolder.composeRule.onNodeWithText("Sound Effects").performClick()
            composeRuleHolder.composeRule.waitForIdle()
            composeRuleHolder.composeRule.onNodeWithTag(FxLibraryTestTags.editButton(name)).performClick()
            composeRuleHolder.composeRule.waitForIdle()
            return
        }

        iOpenTheLibrarySoundscapesTab()
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeLibraryTestTags.editButton(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentCategoryName = name
        currentCategoryId = categoryIdByName(name)
    }

    @When("I tap the {string} card body")
    fun iTapTheCardBody(name: String) {
        iOpenTheLibrarySoundscapesTab()
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeLibraryTestTags.card(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentCategoryName = name
        currentCategoryId = categoryIdByName(name)
    }

    @Then("I see {string}, {string}, and {string} in the list")
    fun iSeeAndInTheList(first: String, second: String, third: String) {
        listOf(first, second, third).forEach { name ->
            composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @Then("the {string} card shows {string}")
    fun theCardShows(name: String, text: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeLibraryTestTags.card(name)).assertTextContains(text)
    }

    @Then("I see the Soundscape Category Composer for {string}")
    fun iSeeTheSoundscapeCategoryComposerFor(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.SCREEN).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
        currentCategoryName = name
        currentCategoryId = categoryIdByName(name)
    }

    @Then("I see a prompt to create my first category")
    fun iSeeAPromptToCreateMyFirstCategory() {
        composeRuleHolder.composeRule.onNodeWithText("Create your first category to begin composing layered ambience.").assertIsDisplayed()
    }

    @Then("I do not see any {string} section")
    fun iDoNotSeeAnySection(sectionName: String) {
        composeRuleHolder.composeRule.onNodeWithText(sectionName).assertDoesNotExist()
    }

    @Then("I see a loading spinner")
    fun iSeeALoadingSpinner() {
        listOf(
            SoundscapeLibraryTestTags.DEMO_LOADING,
            FxLibraryTestTags.DEMO_LOADING,
        ).firstOrNull { tag ->
            composeRuleHolder.composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }?.let { visibleTag ->
            composeRuleHolder.composeRule.onNodeWithTag(visibleTag).assertIsDisplayed()
        } ?: error("No loading spinner is visible.")
    }

    @Then("100 free soundscape tracks are downloaded and added to new categories")
    fun freeSoundscapeTracksAreDownloadedAndAddedToNewCategories() {
        composeRuleHolder.composeRule.waitForIdle()
        val categories = runBlocking { entryPoint().soundscapeRepository().observeCategories().first() }
        val totalTracks = categories.sumOf { category -> category.tracks.size }
        assertThat(categories).isNotEmpty()
        assertThat(totalTracks).isEqualTo(100)
    }

    @Then("the {string} button disappears")
    fun theButtonDisappears(label: String) {
        composeRuleHolder.composeRule.onNodeWithText(label).assertDoesNotExist()
    }

    @Then("{string} is no longer in the soundscape categories list")
    fun isNoLongerInTheSoundscapeCategoriesList(name: String) {
        val categories = runBlocking { entryPoint().soundscapeRepository().observeCategories().first() }
        assertThat(categories.map { it.name }).doesNotContain(name)
    }

    @Given("I am in the Soundscape Category Composer for {string}")
    fun iAmInTheSoundscapeCategoryComposerFor(name: String) {
        val categoryId = ensureCategory(name)
        iOpenTheLibrarySoundscapesTab()
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeLibraryTestTags.card(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentCategoryId = categoryId
        currentCategoryName = name
    }

    @Then("the device's native file picker opens")
    fun theDevicesNativeFilePickerOpens() {
        assertThat(audioSelectionRepository().isPickerOpen.value).isTrue()
    }

    @Given("the file picker is open")
    fun theFilePickerIsOpen() {
        if (!audioSelectionRepository().isPickerOpen.value) {
            iAmInTheSoundscapeCategoryComposerFor(currentCategoryName.ifBlank { "Weather" })
            composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.INVOKE_BUTTON).performClick()
            composeRuleHolder.composeRule.waitForIdle()
        }
    }

    @When("I select {string} from the device")
    fun iSelectFromTheDevice(fileName: String) {
        audioSelectionRepository().submitSelectionForLastRequest(fileName, "/tmp/$fileName")
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("a new soundscape card named {string} appears in the composer")
    fun aNewSoundscapeCardNamedAppearsInTheComposer(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.card(name)).assertIsDisplayed()
    }

    @Then("it defaults to intensity level I")
    fun itDefaultsToIntensityLevelI() {
        composeRuleHolder.composeRule.onNodeWithText("Intensity: I").assertIsDisplayed()
    }

    @When("I open the file picker from the composer")
    fun iOpenTheFilePickerFromTheComposer() {
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.INVOKE_BUTTON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("only audio files are visible \\(non-audio files are filtered out\\)")
    fun onlyAudioFilesAreVisible() {
        assertThat(audioSelectionRepository().requestedMimeTypes.value).containsExactly("audio/*")
    }

    @Given("a soundscape {string} exists with intensity level I")
    fun aSoundscapeExistsWithIntensityLevelI(name: String) {
        val categoryId = ensureCategory(currentCategoryName.ifBlank { "Weather" })
        runBlocking {
            entryPoint().soundscapeRepository().upsertTrack(
                SoundscapeTrack(categoryId = categoryId, name = name, filePath = "/tmp/$name", intensityLevel = IntensityLevel.I),
            )
        }
        iAmInTheSoundscapeCategoryComposerFor(currentCategoryName.ifBlank { "Weather" })
    }

    @When("I change the intensity level to III")
    fun iChangeTheIntensityLevelToIII() {
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.intensity(currentTrackName(), IntensityLevel.III)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the soundscape shows intensity level III")
    fun theSoundscapeShowsIntensityLevelIII() {
        composeRuleHolder.composeRule.onNodeWithText("Intensity: III").assertIsDisplayed()
    }

    @Given("a soundscape {string} exists in the composer")
    fun aSoundscapeExistsInTheComposer(name: String) {
        aSoundscapeExistsWithIntensityLevelI(name)
    }

    @When("I set its MIX slider to {int}%")
    fun iSetItsMixSliderTo(percent: Int) {
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.mixSlider(currentTrackName()))
            .performSemanticsAction(SemanticsActions.SetProgress) { action ->
                action(percent.toFloat())
            }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the soundscape shows a MIX value of {int}%")
    fun theSoundscapeShowsAMixValueOf(percent: Int) {
        composeRuleHolder.composeRule.onNodeWithText("MIX $percent%").assertIsDisplayed()
    }

    @Given("a soundscape {string} exists in the {string} composer")
    fun aSoundscapeExistsInTheComposer(name: String, categoryName: String) {
        currentCategoryId = ensureCategory(categoryName)
        currentCategoryName = categoryName
        runBlocking {
            entryPoint().soundscapeRepository().upsertTrack(
                SoundscapeTrack(categoryId = currentCategoryId, name = name, filePath = "/tmp/$name"),
            )
        }
        iAmInTheSoundscapeCategoryComposerFor(categoryName)
    }

    @When("I swipe right on the {string} soundscape card")
    fun iSwipeRightOnTheSoundscapeCard(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.card(name)).performTouchInput {
            swipeRight()
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("{string} is no longer shown in the composer")
    fun isNoLongerShownInTheComposer(name: String) {
        assertThat(
            composeRuleHolder.composeRule.onAllNodesWithTag(SoundscapeComposerTestTags.card(name)).fetchSemanticsNodes(),
        ).isEmpty()
    }

    @Given("I have added a layer {string} at intensity III in {string}")
    fun iHaveAddedALayerAtIntensityIiiIn(fileName: String, categoryName: String) {
        currentCategoryName = categoryName
        currentCategoryId = ensureCategory(categoryName)
        runBlocking {
            entryPoint().sceneRepository().upsertScene(Scene(name = "$categoryName Scene", soundscapeCategoryNames = listOf(categoryName)))
        }
        iAmInTheSoundscapeCategoryComposerFor(categoryName)
        audioSelectionRepository().submitSelection(currentCategoryId, fileName, "/tmp/$fileName")
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.intensity(fileName, IntensityLevel.III)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the {string} category is updated globally")
    fun theCategoryIsUpdatedGlobally(name: String) {
        composeRuleHolder.composeRule.waitForIdle()
        val category = runBlocking { entryPoint().soundscapeRepository().observeCategory(categoryIdByName(name)).first() }
        assertThat(category?.tracks).isNotEmpty()
    }

    @Then("any scene using {string} reflects the new layer")
    fun anySceneUsingReflectsTheNewLayer(categoryName: String) {
        composeRuleHolder.composeRule.waitForIdle()
        val scenes = runBlocking { entryPoint().sceneRepository().observeScenes().first() }
        assertThat(scenes.any { scene -> categoryName in scene.soundscapeCategoryNames }).isTrue()
        val category = runBlocking { entryPoint().soundscapeRepository().observeCategory(categoryIdByName(categoryName)).first() }
        assertThat(category?.tracks).isNotEmpty()
    }

    @Given("the {string} composer already has the soundscape {string}")
    fun theComposerAlreadyHasTheSoundscape(categoryName: String, soundscapeName: String) {
        aSoundscapeExistsInTheComposer(soundscapeName, categoryName)
    }

    @When("I add {string} and {string} as new soundscapes")
    fun iAddAndAsNewSoundscapes(first: String, second: String) {
        audioSelectionRepository().submitSelection(currentCategoryId, first, "/tmp/$first")
        composeRuleHolder.composeRule.waitForIdle()
        audioSelectionRepository().submitSelection(currentCategoryId, second, "/tmp/$second")
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("all three soundscapes are visible in the composer")
    fun allThreeSoundscapesAreVisibleInTheComposer() {
        listOf("Light Rain", "Thunderstorm", "Drizzle").forEach { name ->
            composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @Given("I have made changes in the composer without saving")
    fun iHaveMadeChangesInTheComposerWithoutSaving() {
        iAmInTheSoundscapeCategoryComposerFor(currentCategoryName.ifBlank { "Weather" })
        audioSelectionRepository().submitSelection(currentCategoryId, "unsaved.mp3", "/tmp/unsaved.mp3")
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the back button")
    fun iTapTheBackButton() {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.BACK_ARROW).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("I see a confirmation dialog asking whether to discard changes")
    fun iSeeAConfirmationDialogAskingWhetherToDiscardChanges() {
        composeRuleHolder.composeRule.onNodeWithTag(SoundscapeComposerTestTags.DISCARD_DIALOG).assertIsDisplayed()
    }

    private fun ensureCategory(name: String): Long = runBlocking {
        entryPoint().soundscapeRepository().observeCategories().first().firstOrNull { category -> category.name == name }?.id
            ?: entryPoint().soundscapeRepository().createCategory(name)
    }

    private fun categoryIdByName(name: String): Long = runBlocking {
        entryPoint().soundscapeRepository().observeCategories().first().first { category -> category.name == name }.id
    }

    private fun currentTrackName(): String = runBlocking {
        entryPoint().soundscapeRepository().observeCategory(currentCategoryId).first()?.tracks?.first()?.name.orEmpty()
    }

    private fun audioSelectionRepository(): SoundscapeAudioSelectionRepository = entryPoint().soundscapeAudioSelectionRepository()

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(
            applicationContext,
            CampaignDataEntryPoint::class.java,
        )
    }
}
