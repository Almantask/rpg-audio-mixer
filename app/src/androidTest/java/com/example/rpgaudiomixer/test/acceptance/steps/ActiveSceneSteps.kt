package com.example.rpgaudiomixer.test.acceptance.steps

import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.PendingException
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue

/**
 * Step definitions for the @iter7 feature files:
 *  - category_playing_state.feature
 *  - play_scene.feature
 *  - add_soundscape_to_scene.feature
 *  - add_fx_to_soundboard.feature
 *  - play_random_track.feature
 *
 * Most steps that require the ActiveSceneScreen to be open via full
 * Campaign → Session → Scene navigation are marked [PendingException] and will be
 * promoted to real assertions in a later iteration once end-to-end navigation is in place.
 *
 * Steps that are already defined in other files are intentionally omitted:
 *  - "{string} and {string} are both playing"            → SoundscapeCategorySteps
 *  - "the {string} card no longer shows the playing state" → SoundscapeCategorySteps
 *  - "no audio is playing"                                 → SceneSteps
 *  - "I see the Active Scene screen for {string}"          → HomeSteps
 *  - "I tap the back arrow"                                → CreditsSteps
 *  - "I open the {string} scene"                           → SceneSteps
 *  - "I tap the play button on the {string} scene card in {string}" → SceneSteps
 *  - "I tap the {string} scene card in {string}"           → SceneSteps
 */
@Suppress("TooManyFunctions", "LongParameterList")
class ActiveSceneSteps(
    private val fakeMusicPlayer: FakeMusicPlayer,
    private val composeRuleHolder: MainActivityComposeRule,
) {

    // =========================================================================
    // category_playing_state.feature
    // Requires: ActiveSceneScreen open with a real navigated-to scene.
    // All scenarios use PendingException – full navigation covered in iter8.
    // =========================================================================

    @When("I start playback on the {string} category")
    fun iStartPlaybackOnTheCategory(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("the {string} card shows the playing state \\(coloured glow border\\)")
    fun theCardShowsThePlayingStateColouredGlowBorder(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Given("the {string} category is not playing")
    fun theCategoryIsNotPlaying(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("the {string} card does not show the glow border")
    fun theCardDoesNotShowTheGlowBorder(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Given("the {string} category is currently playing")
    fun theCategoryIsCurrentlyPlaying(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    /**
     * "I tap pause on {string}" (note: no "the … category" suffix).
     * Distinct from the existing "I tap pause on the {string} category" in SoundscapeCategorySteps.
     */
    @When("I tap pause on {string}")
    fun iTapPauseOn(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("both the {string} and {string} cards show the playing state")
    fun bothCardsShowThePlayingState(category1: String, category2: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @When("I pause {string}")
    fun iPause(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("only {string} shows the playing state")
    fun onlyShowsThePlayingState(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("{string} does not show the playing state")
    fun doesNotShowThePlayingState(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    // =========================================================================
    // play_scene.feature
    // =========================================================================

    /**
     * Seeds a scene in the database. Note: [SceneRepository.createScene] does not return
     * an ID so soundscape categories cannot be seeded here without an additional query.
     * The step name describes the intent; full category seeding is deferred to iter8.
     */
    @Given("I have a scene {string} with soundscape categories")
    fun iHaveASceneWithSoundscapeCategories(sceneName: String) {
        runBlocking {
            PicoToHiltBridge.sceneRepository.createScene(sceneName)
        }
    }

    /**
     * Taps a scene card by name inside the SessionScenesScreen.
     * Distinct from the existing "I tap the {string} scene card in {string}" (SceneSteps)
     * which also navigates to the session first.
     */
    @When("I tap the {string} scene card")
    fun iTapTheSceneCard(sceneName: String) {
        throw PendingException(
            "Requires full Campaign → Session → SessionScenesScreen navigation – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    /**
     * Taps the play button on a scene card.
     * Distinct from the existing "I tap the play button on the {string} scene card in {string}"
     * (SceneSteps) which also includes the session navigation parameter.
     */
    @When("I tap the play button on the {string} scene card")
    fun iTapThePlayButtonOnTheSceneCard(sceneName: String) {
        throw PendingException(
            "Requires full Campaign → Session → SessionScenesScreen navigation – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("the scene's soundscapes begin playing with a fade-in")
    fun theScenesSoundscapesBeginPlayingWithAFadeIn() {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – end-to-end navigation covered in iter8.",
        )
    }

    @Given("{string} is the current playing scene")
    fun isTheCurrentPlayingScene(sceneName: String) {
        throw PendingException(
            "Requires full scene navigation and audio playback setup – covered in iter8.",
        )
    }

    @When("I navigate back to the scenes list")
    fun iNavigateBackToTheScenesList() {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – end-to-end navigation covered in iter8.",
        )
    }

    @When("I tap the {string} scene card \\(not the play button\\)")
    fun iTapTheSceneCardNotThePlayButton(sceneName: String) {
        throw PendingException(
            "Requires full Campaign → Session → SessionScenesScreen navigation – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("the {string} audio fades out while the {string} audio fades in simultaneously")
    fun theAudioFadesOutWhileOtherFadesIn(fadingOut: String, fadingIn: String) {
        throw PendingException(
            "Crossfade assertion requires real audio timing – covered in iter8.",
        )
    }

    @Then("there should be no dip in perceived volume during the crossfade")
    fun thereIsNoDipInPerceivedVolume() {
        throw PendingException(
            "Crossfade assertion requires real audio timing – covered in iter8.",
        )
    }

    @Then("{string} audio is not playing")
    fun audioIsNotPlaying(sceneName: String) {
        throw PendingException(
            "Requires knowledge of which scene's audio is active – covered in iter8.",
        )
    }

    @Then("{string} audio continues playing in the background")
    fun audioContinuesPlayingInBackground(sceneName: String) {
        throw PendingException(
            "Requires background audio state tracking – covered in iter8.",
        )
    }

    @Given("{string} has a saved Master Atmosphere value of {int}%")
    fun hasSavedMasterAtmosphereValue(sceneName: String, volumePercent: Int) {
        throw PendingException(
            "Requires persisted scene settings – covered in iter8.",
        )
    }

    @Then("the Master Atmosphere slider is immediately at {int}% with no animation")
    fun masterAtmosphereSliderIsAt(volumePercent: Int) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – end-to-end navigation covered in iter8.",
        )
    }

    @Given("{string} is playing with soundscapes at {string} volume")
    fun isPlayingWithSoundscapesAtVolume(sceneName: String, volume: String) {
        throw PendingException(
            "Requires full scene navigation and audio state setup – covered in iter8.",
        )
    }

    @When("I trigger the {string} sound effect from the soundboard")
    fun iTriggerTheSoundEffectFromTheSoundboard(soundEffect: String) {
        throw PendingException(
            "Requires ActiveSceneScreen (Soundboard tab) to be open – covered in iter8.",
        )
    }

    @Then("the soundscape volume should duck to {string}")
    fun soundscapeVolumeShouldDuckTo(volume: String) {
        throw PendingException(
            "Requires real audio ducking implementation – covered in iter8.",
        )
    }

    @And("when the {string} sound effect finishes")
    fun whenTheSoundEffectFinishes(soundEffect: String) {
        throw PendingException(
            "Requires real audio completion callbacks – covered in iter8.",
        )
    }

    @Then("the soundscape volume should smoothly restore to {string}")
    fun soundscapeVolumeShouldSmoothlyRestoreTo(volume: String) {
        throw PendingException(
            "Requires real audio ducking / restore implementation – covered in iter8.",
        )
    }

    // =========================================================================
    // add_soundscape_to_scene.feature
    // =========================================================================

    @Given("I am on the Active Scene — Soundscapes tab")
    fun iAmOnTheActiveSceneSoundscapesTab() {
        throw PendingException(
            "Requires full Campaign → Session → Scene → ActiveSceneScreen navigation – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("I see the Soundscape category selection screen with a back arrow")
    fun iSeeTheSoundscapeCategorySelectionScreenWithBackArrow() {
        throw PendingException(
            "Requires AddSoundscapeScreen to be open via navigation – covered in iter8.",
        )
    }

    @Given("the Soundscape selection screen is open")
    fun theSoundscapeSelectionScreenIsOpen() {
        throw PendingException(
            "Requires AddSoundscapeScreen to be open via navigation – covered in iter8.",
        )
    }

    @And("my library has the category {string}")
    fun myLibraryHasTheCategory(categoryName: String) {
        throw PendingException(
            "Requires a library category seed + AddSoundscapeScreen open – covered in iter8.",
        )
    }

    @Then("the {string} row displays a + button")
    fun theRowDisplaysAPlusButton(displayName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen or AddFxScreen to be open – covered in iter8.",
        )
    }

    @And("{string} is not yet in the current scene")
    fun isNotYetInTheCurrentScene(categoryName: String) {
        throw PendingException(
            "Requires scene context from ActiveSceneScreen – covered in iter8.",
        )
    }

    @When("I tap the + button on the {string} row")
    fun iTapThePlusButtonOnRow(displayName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen or AddFxScreen to be open – covered in iter8.",
        )
    }

    @Then("{string} is instantly added to the active scene")
    fun isInstantlyAddedToTheActiveScene(categoryName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen to be open – covered in iter8.",
        )
    }

    @And("I see the already-added indicator on the {string} row")
    fun iSeeTheAlreadyAddedIndicatorOnRow(displayName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen or AddFxScreen to be open – covered in iter8.",
        )
    }

    @Given("I have tapped + on {string} in the selection screen")
    fun iHaveTappedPlusOnInTheSelectionScreen(categoryName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen to be open – covered in iter8.",
        )
    }

    @Then("{string} is added to the scene immediately without any confirmation dialog")
    fun isAddedToSceneImmediatelyWithoutConfirmDialog(categoryName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen to be open – covered in iter8.",
        )
    }

    @When("I tap + on {string}")
    fun iTapPlusOn(itemName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen or AddFxScreen to be open – covered in iter8.",
        )
    }

    @Then("all three categories appear in the active scene's Soundscapes tab")
    fun allThreeCategoriesAppearInSoundscapesTab() {
        throw PendingException(
            "Requires ActiveSceneScreen (Soundscapes tab) to be open – covered in iter8.",
        )
    }

    @Given("{string} is already in the current scene")
    fun isAlreadyInTheCurrentScene(categoryName: String) {
        throw PendingException(
            "Requires scene context from ActiveSceneScreen – covered in iter8.",
        )
    }

    @When("I open the Soundscape selection screen")
    fun iOpenTheSoundscapeSelectionScreen() {
        throw PendingException(
            "Requires ActiveSceneScreen (Soundscapes tab) to be open – covered in iter8.",
        )
    }

    @Then("the {string} row shows the already-added indicator instead of a + button")
    fun theRowShowsAlreadyAddedIndicatorInsteadOfPlusButton(displayName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen or AddFxScreen to be open – covered in iter8.",
        )
    }

    @When("I tap the already-added indicator on the {string} row")
    fun iTapTheAlreadyAddedIndicatorOnRow(displayName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen or AddFxScreen to be open – covered in iter8.",
        )
    }

    @Then("{string} is not duplicated in the scene")
    fun isNotDuplicatedInTheScene(categoryName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen to be open – covered in iter8.",
        )
    }

    @Given("I have added {string} and {string} from the selection screen")
    fun iHaveAddedTwoFromTheSelectionScreen(category1: String, category2: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen flow – covered in iter8.",
        )
    }

    @Then("I see the Active Scene — Soundscapes tab")
    fun iSeeTheActiveSceneSoundscapesTab() {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – covered in iter8.",
        )
    }

    @And("both {string} and {string} are present as category cards")
    fun bothArePresentAsCategoryCards(category1: String, category2: String) {
        throw PendingException(
            "Requires ActiveSceneScreen (Soundscapes tab) to be open – covered in iter8.",
        )
    }

    // ── Shared: Android version / storage permission steps ───────────────────

    @Given("I am on Android 13 or higher")
    fun iAmOnAndroid13OrHigher() {
        throw PendingException(
            "OS-level permission flow – not automatable via Compose UI tests.",
        )
    }

    @And("the app has been granted {string} permission")
    fun theAppHasBeenGrantedPermission(permission: String) {
        throw PendingException(
            "OS-level permission grant – not automatable via Compose UI tests.",
        )
    }

    @When("I tap {string} in the footer card")
    fun iTapInTheFooterCard(label: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen or AddFxScreen to be open – covered in iter8.",
        )
    }

    @Then("the device's native audio file picker opens")
    fun theDevicesNativeAudioFilePickerOpens() {
        throw PendingException(
            "OS-level file picker – not automatable via Compose UI tests.",
        )
    }

    @And("the app has been denied {string} permission")
    fun theAppHasBeenDeniedPermission(permission: String) {
        throw PendingException(
            "OS-level permission denial – not automatable via Compose UI tests.",
        )
    }

    @Then("I see a permission request dialog for audio access")
    fun iSeeAPermissionRequestDialogForAudioAccess() {
        throw PendingException(
            "OS-level permission dialog – not automatable via Compose UI tests.",
        )
    }

    @And("if I deny the permission, I see a message explaining why the permission is needed for imports")
    fun ifIDenyThePermissionISeeAMessage() {
        throw PendingException(
            "OS-level permission denial flow – not automatable via Compose UI tests.",
        )
    }

    @Given("I am on Android 12 or lower")
    fun iAmOnAndroid12OrLower() {
        throw PendingException(
            "OS-level version check – not automatable via Compose UI tests.",
        )
    }

    @Given("the device file picker is open from the selection screen")
    fun theDeviceFilePickerIsOpenFromTheSelectionScreen() {
        throw PendingException(
            "OS-level file picker – not automatable via Compose UI tests.",
        )
    }

    @And("the app has been granted necessary storage permissions")
    fun theAppHasBeenGrantedNecessaryStoragePermissions() {
        throw PendingException(
            "OS-level storage permission – not automatable via Compose UI tests.",
        )
    }

    @When("I select {string}")
    fun iSelect(fileName: String) {
        throw PendingException(
            "OS-level file picker selection – not automatable via Compose UI tests.",
        )
    }

    @Then("a new soundscape layer {string} is created")
    fun aNewSoundscapeLayerIsCreated(fileName: String) {
        throw PendingException(
            "Requires AddSoundscapeScreen + import flow – covered in iter8.",
        )
    }

    @And("I am taken to the Soundscape Category Composer to configure it")
    fun iAmTakenToTheSoundscapeCategoryComposer() {
        throw PendingException(
            "Soundscape Category Composer navigation – covered in iter8.",
        )
    }

    // =========================================================================
    // add_fx_to_soundboard.feature
    // =========================================================================

    @Given("I am on the Active Scene — Soundboard tab")
    fun iAmOnTheActiveSceneSoundboardTab() {
        throw PendingException(
            "Requires full Campaign → Session → Scene → ActiveSceneScreen navigation – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("I see the FX selection screen with a back arrow")
    fun iSeeTheFxSelectionScreenWithABackArrow() {
        throw PendingException(
            "Requires AddFxScreen to be open via navigation – covered in iter8.",
        )
    }

    @Given("the FX selection screen is open")
    fun theFxSelectionScreenIsOpen() {
        throw PendingException(
            "Requires AddFxScreen to be open via navigation – covered in iter8.",
        )
    }

    @And("the FX library has {string}")
    fun theFxLibraryHas(fxName: String) {
        throw PendingException(
            "Requires FX library seed + AddFxScreen open – covered in iter8.",
        )
    }

    @And("{string} is not yet in the current scene's soundboard")
    fun isNotYetInTheCurrentScenesSoundboard(fxName: String) {
        throw PendingException(
            "Requires scene context from ActiveSceneScreen – covered in iter8.",
        )
    }

    @Then("{string} is instantly added to the soundboard")
    fun isInstantlyAddedToTheSoundboard(fxName: String) {
        throw PendingException(
            "Requires AddFxScreen to be open – covered in iter8.",
        )
    }

    @Given("I have tapped + on {string} in the FX selection screen")
    fun iHaveTappedPlusOnInTheFxSelectionScreen(fxName: String) {
        throw PendingException(
            "Requires AddFxScreen to be open – covered in iter8.",
        )
    }

    @Then("{string} is added to the soundboard immediately without any confirmation dialog")
    fun isAddedToSoundboardImmediatelyWithoutConfirmDialog(fxName: String) {
        throw PendingException(
            "Requires AddFxScreen to be open – covered in iter8.",
        )
    }

    @Then("all three effects appear as buttons in the active scene's soundboard")
    fun allThreeEffectsAppearAsSoundboardButtons() {
        throw PendingException(
            "Requires ActiveSceneScreen (Soundboard tab) to be open – covered in iter8.",
        )
    }

    @Given("{string} is already in the current scene's soundboard")
    fun isAlreadyInTheCurrentScenesSoundboard(fxName: String) {
        throw PendingException(
            "Requires scene context from ActiveSceneScreen – covered in iter8.",
        )
    }

    @When("I open the FX selection screen")
    fun iOpenTheFxSelectionScreen() {
        throw PendingException(
            "Requires ActiveSceneScreen (Soundboard tab) to be open – covered in iter8.",
        )
    }

    @Then("{string} is not duplicated in the soundboard")
    fun isNotDuplicatedInTheSoundboard(fxName: String) {
        throw PendingException(
            "Requires AddFxScreen to be open – covered in iter8.",
        )
    }

    @Given("I have added {string} and {string} from the FX selection screen")
    fun iHaveAddedTwoFromTheFxSelectionScreen(fx1: String, fx2: String) {
        throw PendingException(
            "Requires AddFxScreen flow – covered in iter8.",
        )
    }

    @Then("I see the Active Scene — Soundboard tab")
    fun iSeeTheActiveSceneSoundboardTab() {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – covered in iter8.",
        )
    }

    @And("both {string} and {string} appear as buttons in the soundboard grid")
    fun bothAppearAsSoundboardGridButtons(fx1: String, fx2: String) {
        throw PendingException(
            "Requires ActiveSceneScreen (Soundboard tab) to be open – covered in iter8.",
        )
    }

    @Given("the device file picker is open from the FX selection screen")
    fun theDeviceFilePickerIsOpenFromTheFxSelectionScreen() {
        throw PendingException(
            "OS-level file picker – not automatable via Compose UI tests.",
        )
    }

    @Then("{string} appears in the FX selection list")
    fun appearsInTheFxSelectionList(fileName: String) {
        throw PendingException(
            "Requires AddFxScreen to be open after import – covered in iter8.",
        )
    }

    @And("it can be added to the scene with a + tap")
    fun itCanBeAddedWithAPlusTap() {
        throw PendingException(
            "Requires AddFxScreen to be open – covered in iter8.",
        )
    }

    // =========================================================================
    // play_random_track.feature
    // =========================================================================

    /**
     * Seeds the intensity-to-track mapping in the fake.
     * The format in Gherkin is:
     *   Given a category "Weather" has tracks at intensity level I: "Light Rain", "Drizzle"
     */
    @Given("a category {string} has tracks at intensity level {word}: {string}, {string}")
    fun aCategoryHasTracksAtIntensityLevel(
        categoryName: String,
        intensityLevel: String,
        track1: String,
        track2: String,
    ) {
        // State-only setup; the actual d20 button requires ActiveSceneScreen open.
        // Stored in FakeMusicPlayer registration so later assertions have context.
        fakeMusicPlayer.registerLoopingTrack(track1)
        fakeMusicPlayer.registerLoopingTrack(track2)
    }

    @And("the intensity is set to {word}")
    fun theIntensityIsSetTo(intensityLevel: String) {
        throw PendingException(
            "Intensity selector requires ActiveSceneScreen to be open – covered in iter8.",
        )
    }

    @When("I tap the d20 button on {string}")
    fun iTapTheD20ButtonOn(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @Then("one of {string} or {string} begins playing")
    fun oneOfOrBeginsPlaying(track1: String, track2: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – covered in iter8.",
        )
    }

    @And("no track from intensity level {word} or {word} is selected")
    fun noTrackFromIntensityLevelIsSelected(level1: String, level2: String) {
        throw PendingException(
            "Intensity-pool filtering assertion requires ActiveSceneScreen – covered in iter8.",
        )
    }

    /**
     * Sets up a specific track as playing inside a named category.
     * Distinct from "{string} is playing from the soundboard" (SoundscapeCategorySteps)
     * and other existing "is playing" steps.
     */
    @Given("{string} is playing in the {string} category")
    fun isPlayingInTheCategory(trackName: String, categoryName: String) {
        // Register both so volume assertions can locate them if needed.
        fakeMusicPlayer.registerLoopingTrack(trackName)
        fakeMusicPlayer.playLoopingSound(categoryName)
    }

    @Then("{string} stops")
    fun stops(trackName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open to verify playback stopped – covered in iter8.",
        )
    }

    @Then("a new random track from {string} begins playing")
    fun aNewRandomTrackFromCategoryBeginsPlaying(categoryName: String) {
        assertTrue(
            "Expected a random track play request for '$categoryName' but got: " +
                "${fakeMusicPlayer.getRandomTrackPlays()}",
            fakeMusicPlayer.getRandomTrackPlays().contains(categoryName),
        )
    }

    /**
     * "the {string} card shows the playing state (glow border)"
     * Distinct from "the {string} card shows the playing state (coloured glow border)"
     * (category_playing_state.feature) and "the {string} card shows the coloured glow playing state"
     * (SoundscapeCategorySteps).
     */
    @Then("the {string} card shows the playing state \\(glow border\\)")
    fun theCardShowsThePlayingStateGlowBorder(categoryName: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – end-to-end navigation covered in iter8.",
        )
    }

    @Given("{string} has no tracks at intensity level {word}")
    fun hasNoTracksAtIntensityLevel(categoryName: String, intensityLevel: String) {
        // No-op: signals absence of tracks; assertion happens in the Then step.
    }

    @And("the intensity on {string} is set to {word}")
    fun theIntensityOnCategoryIsSetTo(categoryName: String, intensityLevel: String) {
        throw PendingException(
            "Intensity selector requires ActiveSceneScreen to be open – covered in iter8.",
        )
    }

    @Then("a warning message is shown indicating no tracks are available at that intensity")
    fun aWarningMessageIsShownForEmptyPool() {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – end-to-end navigation covered in iter8.",
        )
    }

    /**
     * "… categories are both playing in the active scene"
     * Distinct from "{string} and {string} categories are both looping" (SoundscapeCategorySteps).
     */
    @Given("{string} and {string} categories are both playing in the active scene")
    fun categoriesAreBothPlayingInTheActiveScene(category1: String, category2: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open with a navigated-to scene – " +
                "end-to-end navigation covered in iter8.",
        )
    }

    @When("I tap the {string} button on a Bluetooth remote")
    fun iTapTheButtonOnABluetoothRemote(buttonLabel: String) {
        throw PendingException(
            "Bluetooth remote integration is not yet implemented.",
        )
    }

    @Then("a new random track begins playing for both {string} and {string}")
    fun aNewRandomTrackBeginsPlayingForBothCategories(category1: String, category2: String) {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – covered in iter8.",
        )
    }

    @And("the intensity levels are preserved")
    fun theIntensityLevelsArePreserved() {
        throw PendingException(
            "Requires ActiveSceneScreen to be open – covered in iter8.",
        )
    }
}
