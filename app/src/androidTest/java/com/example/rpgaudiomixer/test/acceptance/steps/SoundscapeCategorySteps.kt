package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.runBlocking
import org.junit.Ignore

/**
 * Step definitions for:
 *  - manage_soundscape_categories.feature (@iter3)
 *  - compose_soundscape.feature          (@iter3 @core)
 *
 * Most steps require the Soundscape Category Composer screen
 * which is not yet implemented. Steps are marked @Ignore with empty bodies.
 */
class SoundscapeCategorySteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository

    // ── Helpers ───────────────────────────────────────────

    private fun navigateToLibraryTab() {
        composeTestRule.onNodeWithTag("bottomNavItem_LIBRARY").performClick()
        composeTestRule.waitForIdle()
    }

    // ═══════════════════════════════════════════════════
    // manage_soundscape_categories.feature steps
    // ═══════════════════════════════════════════════════

    @Given("I have created categories {string}, {string}, {string}")
    @Ignore("Soundscape category creation via repository not yet exposed in test bridge")
    fun haveCreatedCategories(cat1: String, cat2: String, cat3: String) {
        // TODO: SoundscapeCategoryRepository not in PicoToHiltBridge yet
    }

    @When("I open the Library — Soundscapes tab")
    @Ignore("Library Soundscapes sub-tab not yet implemented")
    fun openLibrarySoundscapesTab() {
        navigateToLibraryTab()
        // TODO: Soundscapes sub-tab in Library not yet implemented
    }

    @Then("I see {string}, {string}, and {string} in the list")
    @Ignore("Soundscape category list display not yet implemented")
    fun seeThreeItemsInList(item1: String, item2: String, item3: String) {
        // TODO: Not yet implemented
    }

    @Given("I am on the Soundscapes Library screen")
    @Ignore("Soundscapes Library sub-screen not yet implemented")
    fun amOnSoundscapesLibraryScreen() {
        navigateToLibraryTab()
        // TODO: Soundscapes sub-tab not yet implemented
    }

    @Then("I see a loading spinner")
    @Ignore("Loading spinner assertion not yet implemented")
    fun seeLoadingSpinner() {
        // TODO: Not yet implemented
    }

    @Then("{int} free soundscape tracks are downloaded and added to new categories")
    @Ignore("Demo soundscape download not yet implemented")
    fun freeSoundscapeTracksDownloaded(count: Int) {
        // TODO: Not yet implemented
    }

    @Then("the {string} button disappears")
    @Ignore("Button disappearance assertion not yet implemented")
    fun buttonDisappears(buttonText: String) {
        // TODO: Not yet implemented
    }

    @Given("{string} has {int} tracks at level I, {int} at level II, and {int} at level III")
    @Ignore("Soundscape category track count configuration not yet implemented")
    fun categoryHasTracksAtLevels(
        categoryName: String,
        levelICount: Int,
        levelIICount: Int,
        levelIIICount: Int,
    ) {
        // TODO: Not yet implemented
    }

    @Then("the {string} card shows {string}")
    @Ignore("Soundscape category card track count display not yet implemented")
    fun cardShows(cardName: String, displayText: String) {
        // TODO: Not yet implemented
    }

    @Given("{string} is in the soundscape categories list")
    @Ignore("Soundscape category list precondition not yet implemented")
    fun isInSoundscapeCategoriesList(categoryName: String) {
        // TODO: SoundscapeCategoryRepository not in PicoToHiltBridge yet
    }

    @When("I tap the edit \\(pencil\\) icon on {string}")
    @Ignore("Edit icon tap on soundscape category not yet implemented")
    fun tapEditIconOn(categoryName: String) {
        // TODO: Not yet implemented
    }

    @Then("I see the Soundscape Category Composer for {string}")
    @Ignore("Soundscape Category Composer screen not yet implemented")
    fun seeSoundscapeCategoryComposerFor(categoryName: String) {
        // TODO: Not yet implemented
    }

    @When("I tap the {string} card body")
    @Ignore("Card body tap for soundscape category not yet implemented")
    fun tapCardBody(cardName: String) {
        // TODO: Not yet implemented
    }

    @Given("I have not created any soundscape categories")
    @Ignore("Soundscape category empty state precondition not yet implemented")
    fun haveNotCreatedAnySoundscapeCategories() {
        // TODO: SoundscapeCategoryRepository not in PicoToHiltBridge yet
    }

    @Then("{string} is no longer in the soundscape categories list")
    @Ignore("Soundscape category removal from list not yet implemented")
    fun noLongerInSoundscapeCategoriesList(categoryName: String) {
        // TODO: Not yet implemented
    }

    // ═══════════════════════════════════════════════════
    // compose_soundscape.feature steps
    // ═══════════════════════════════════════════════════

    @Given("I am in the Soundscape Category Composer for {string}")
    @Ignore("Soundscape Category Composer not yet implemented")
    fun amInSoundscapeCategoryComposerFor(categoryName: String) {
        // TODO: Not yet implemented
    }

    @Then("the device's native file picker opens")
    @Ignore("File picker assertion not yet implemented (system UI)")
    fun deviceNativeFilePickerOpens() {
        // TODO: Cannot reliably assert system file picker in Compose tests
    }

    @Given("the file picker is open")
    @Ignore("File picker state not yet controllable in tests")
    fun filePickerIsOpen() {
        // TODO: Not yet implemented
    }

    @When("I select {string} from the device")
    @Ignore("File selection from device not yet implemented in tests")
    fun selectFromDevice(fileName: String) {
        // TODO: System file picker interaction not yet supported
    }

    @Then("a new soundscape card named {string} appears in the composer")
    @Ignore("Soundscape card in composer not yet implemented")
    fun newSoundscapeCardAppearsInComposer(name: String) {
        // TODO: Not yet implemented
    }

    @Then("it defaults to intensity level I")
    @Ignore("Default intensity level I assertion not yet implemented")
    fun defaultsToIntensityLevelI() {
        // TODO: Not yet implemented
    }

    @When("I open the file picker from the composer")
    @Ignore("File picker from composer not yet implemented")
    fun openFilePickerFromComposer() {
        // TODO: Not yet implemented
    }

    @Then("only audio files are visible \\(non-audio files are filtered out\\)")
    @Ignore("File picker audio filter assertion not yet implemented (system UI)")
    fun onlyAudioFilesVisible() {
        // TODO: System file picker MIME filter cannot be asserted via Compose tests
    }

    @Given("a soundscape {string} exists with intensity level I")
    @Ignore("Soundscape intensity precondition not yet implemented")
    fun soundscapeExistsWithIntensityLevelI(name: String) {
        // TODO: Not yet implemented
    }

    @When("I tap {string} on the {string} soundscape card")
    @Ignore("Intensity tap on soundscape card not yet implemented")
    fun tapOnSoundscapeCard(action: String, cardName: String) {
        // TODO: Not yet implemented
    }

    @Then("the {string} soundscape card shows {string} as active")
    @Ignore("Soundscape card active state not yet implemented")
    fun soundscapeCardShowsAsActive(cardName: String, state: String) {
        // TODO: Not yet implemented
    }

    @Then("{string} should be highlighted in gold on the {string} card")
    @Ignore("Gold highlight assertion not yet implemented")
    fun shouldBeHighlightedInGoldOnCard(text: String, cardName: String) {
        // TODO: Not yet implemented
    }

    @Given("a soundscape {string} exists in the composer")
    @Ignore("Soundscape in composer precondition not yet implemented")
    fun soundscapeExistsInComposer(name: String) {
        // TODO: Not yet implemented
    }

    @When("I set its MIX slider to {int}%")
    @Ignore("MIX slider interaction not yet implemented")
    fun setMixSliderTo(percent: Int) {
        // TODO: Not yet implemented
    }

    @Then("the soundscape shows a MIX value of {int}%")
    @Ignore("MIX value display assertion not yet implemented")
    fun soundscapeShowsMixValue(percent: Int) {
        // TODO: Not yet implemented
    }

    @Given("a soundscape {string} exists in the {string} composer")
    @Ignore("Named soundscape in named composer not yet implemented")
    fun soundscapeExistsInNamedComposer(soundscape: String, composer: String) {
        // TODO: Not yet implemented
    }

    @When("I swipe right on the {string} soundscape card")
    @Ignore("Swipe-to-delete soundscape card not yet implemented")
    fun swipeRightOnSoundscapeCard(cardName: String) {
        // TODO: Not yet implemented
    }

    @Then("{string} is no longer shown in the composer")
    @Ignore("Soundscape removal from composer not yet implemented")
    fun noLongerShownInComposer(name: String) {
        // TODO: Not yet implemented
    }

    @Given("I have added a layer {string} at intensity III in {string}")
    @Ignore("Soundscape layer addition not yet implemented")
    fun haveAddedLayerAtIntensityInComposer(layer: String, composer: String) {
        // TODO: Not yet implemented
    }

    @Then("the {string} category is updated globally")
    @Ignore("Category global update assertion not yet implemented")
    fun categoryUpdatedGlobally(categoryName: String) {
        // TODO: Not yet implemented
    }

    @Then("any scene using {string} reflects the new layer")
    @Ignore("Scene-category update propagation not yet implemented")
    fun sceneReflectsNewLayer(categoryName: String) {
        // TODO: Not yet implemented
    }

    @Given("the {string} composer already has the soundscape {string}")
    @Ignore("Composer with existing soundscape not yet implemented")
    fun composerAlreadyHasSoundscape(composer: String, soundscape: String) {
        // TODO: Not yet implemented
    }

    @When("I add {string} and {string} as new soundscapes")
    @Ignore("Adding multiple soundscapes not yet implemented")
    fun addNewSoundscapes(soundscape1: String, soundscape2: String) {
        // TODO: Not yet implemented
    }

    @Then("all three soundscapes are visible in the composer")
    @Ignore("Multiple soundscape visibility in composer not yet implemented")
    fun allThreeSoundscapesVisibleInComposer() {
        // TODO: Not yet implemented
    }

    @Given("I have made changes in the composer without saving")
    @Ignore("Unsaved changes state in composer not yet implemented")
    fun haveMadeChangesWithoutSaving() {
        // TODO: Not yet implemented
    }

    @Then("I see a confirmation dialog asking whether to discard changes")
    @Ignore("Discard changes dialog not yet implemented")
    fun seeConfirmationDialogToDiscardChanges() {
        // TODO: Not yet implemented
    }

    @When("I tap the back button")
    @Ignore("Composer back navigation not yet implemented")
    fun tapTheBackButton() {
        // TODO: Not yet implemented
    }

    @Then("I see a prompt to create my first category")
    @Ignore("First category prompt not yet implemented")
    fun seePromptToCreateFirstCategory() {
        // TODO: Not yet implemented
    }
}
