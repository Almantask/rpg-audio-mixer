package com.example.rpgaudiomixer.test.acceptance.steps

import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import io.cucumber.java.PendingException
import io.cucumber.java.en.And
import io.cucumber.java.en.But
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

/**
 * Step definitions for @iter6 soundscape-category, soundscape-volume,
 * mixed-loops, retrigger, reorder, and intensity feature files.
 *
 * Audio state assertions use [FakeMusicPlayer] exclusively – no real-device audio.
 * Steps that require full UI navigation (ActiveSceneScreen flow) are marked
 * [PendingException] and will be deferred to a later iteration.
 */
class SoundscapeCategorySteps(
    private val fakeMusicPlayer: FakeMusicPlayer,
) {

    // =========================================================================
    // soundscape_volume_control.feature
    // =========================================================================

    // ---- Given (setup) -------------------------------------------------------

    /**
     * Sets up two soundscape categories with their respective MIX volumes.
     * E.g. "Given a scene has categories "Weather" at MIX 100% and "Interior" at MIX 50%"
     */
    @Given("a scene has categories {string} at MIX {int}% and {string} at MIX {int}%")
    fun aSceneHasCategoriesWithMixVolumes(
        category1: String,
        mix1: Int,
        category2: String,
        mix2: Int,
    ) {
        fakeMusicPlayer.registerLoopingTrack(category1, mix1)
        fakeMusicPlayer.registerLoopingTrack(category2, mix2)
    }

    /** Sets the global / Master Atmosphere volume. */
    @Given("Master Atmosphere is at {int}%")
    fun masterAtmosphereIsAt(volumePercent: Int) {
        fakeMusicPlayer.setGlobalVolumePercent(volumePercent)
    }

    /**
     * Registers a single soundscape category as playing with explicit Master and MIX volumes.
     * E.g. "Given "Weather" is playing with Master at 100% and MIX at 100%"
     */
    @Given("{string} is playing with Master at {int}% and MIX at {int}%")
    fun categoryIsPlayingWithMasterAndMix(category: String, master: Int, mix: Int) {
        fakeMusicPlayer.setGlobalVolumePercent(master)
        fakeMusicPlayer.registerLoopingTrack(category, mix)
    }

    /**
     * Registers two soundscape categories with their respective MIX volumes
     * (without per-category "at MIX" phrasing used in Scenario 1).
     * E.g. "Given "Weather" has MIX at 80% and "Interior" has MIX at 40%"
     */
    @Given("{string} has MIX at {int}% and {string} has MIX at {int}%")
    fun categoriesHaveMixVolumes(
        category1: String,
        mix1: Int,
        category2: String,
        mix2: Int,
    ) {
        fakeMusicPlayer.registerLoopingTrack(category1, mix1)
        fakeMusicPlayer.registerLoopingTrack(category2, mix2)
    }

    /** Sets the soundboard Master volume.
     *  E.g. "Given the soundboard Master volume is at 100%"
     */
    @Given("the soundboard Master volume is at {int}%")
    fun theSoundboardMasterVolumeIsAt(volumePercent: Int) {
        fakeMusicPlayer.setSoundboardVolumePercent(volumePercent)
    }

    /** Short form: "Given the soundboard Master is at 50%". */
    @Given("the soundboard Master is at {int}%")
    fun theSoundboardMasterIsAt(volumePercent: Int) {
        fakeMusicPlayer.setSoundboardVolumePercent(volumePercent)
    }

    /**
     * Registers a track as a soundboard (one-shot) track.
     * E.g. "And "Thunder Crack" is playing from the soundboard"
     */
    @Given("{string} is playing from the soundboard")
    fun trackIsPlayingFromTheSoundboard(trackId: String) {
        fakeMusicPlayer.registerSoundboardTrack(trackId)
    }

    /**
     * Registers a soundscape looping track with explicit MIX and Master volumes.
     * E.g. "And "Forest Loop" is playing as a soundscape at MIX 100%, Master 100%"
     */
    @Given("{string} is playing as a soundscape at MIX {int}%, Master {int}%")
    fun trackIsPlayingAsSoundscape(trackId: String, mix: Int, master: Int) {
        fakeMusicPlayer.setGlobalVolumePercent(master)
        fakeMusicPlayer.registerLoopingTrack(trackId, mix)
    }

    // ---- When (actions) -----------------------------------------------------

    /** Adjusts the per-category MIX slider.
     *  E.g. "When I set the "Weather" MIX slider to 50%"
     */
    @When("I set the {string} MIX slider to {int}%")
    fun iSetTheMixSliderTo(category: String, volumePercent: Int) {
        fakeMusicPlayer.setLoopingTrackVolumePercent(category, volumePercent)
    }

    /** Adjusts the global Master Atmosphere volume.
     *  E.g. "When I set Master Atmosphere to 50%"
     */
    @When("I set Master Atmosphere to {int}%")
    fun iSetMasterAtmosphereTo(volumePercent: Int) {
        fakeMusicPlayer.setGlobalVolumePercent(volumePercent)
    }

    /** Reduces the global Master Atmosphere volume (alias for the same action).
     *  E.g. "When I reduce the Master Atmosphere to 30%"
     */
    @When("I reduce the Master Atmosphere to {int}%")
    fun iReduceTheMasterAtmosphereTo(volumePercent: Int) {
        fakeMusicPlayer.setGlobalVolumePercent(volumePercent)
    }

    /** Adjusts the soundboard Master volume.
     *  E.g. "When I set the soundboard Master to 50%"
     */
    @When("I set the soundboard Master to {int}%")
    fun iSetTheSoundboardMasterTo(volumePercent: Int) {
        fakeMusicPlayer.setSoundboardVolumePercent(volumePercent)
    }

    // ---- Then (assertions) --------------------------------------------------

    /**
     * Asserts the effective output volume for a named track.
     * Covers both soundscape and soundboard tracks via [FakeMusicPlayer.getEffectiveVolumePercent].
     * E.g. "Then "Weather" plays at 80% output"
     */
    @Then("{string} plays at {int}% output")
    fun playsAtOutput(trackId: String, expectedPercent: Int) {
        assertEquals(
            "Effective output volume for '$trackId'",
            expectedPercent,
            fakeMusicPlayer.getEffectiveVolumePercent(trackId),
        )
    }

    /**
     * Asserts a category's volume expressed as a percentage of the Master output.
     * Since Master is a factor in getEffectiveVolumePercent, this reduces to the same
     * check as [playsAtOutput] when Master is already set.
     * E.g. "Then "Weather" plays at 30% of Master output"
     */
    @Then("{string} plays at {int}% of Master output")
    fun playsAtPercentOfMasterOutput(trackId: String, expectedPercent: Int) {
        assertEquals(
            "Effective output (as % of Master) for '$trackId'",
            expectedPercent,
            fakeMusicPlayer.getEffectiveVolumePercent(trackId),
        )
    }

    /**
     * Variant with "still" prefix – asserts unchanged volume for a second category.
     * E.g. "And "Interior" still plays at 100% of Master output"
     */
    @Then("{string} still plays at {int}% of Master output")
    fun stillPlaysAtPercentOfMasterOutput(trackId: String, expectedPercent: Int) {
        assertEquals(
            "Effective output (still unchanged) for '$trackId'",
            expectedPercent,
            fakeMusicPlayer.getEffectiveVolumePercent(trackId),
        )
    }

    /**
     * Asserts that a soundscape track's volume is unaffected by the soundboard Master.
     * Soundscape tracks use loopingTrackVolume × globalVolume, which is independent of
     * soundboardVolume, so this is verifiable via [FakeMusicPlayer.getEffectiveVolumePercent].
     * E.g. "Then "Forest Loop" plays at 100% output regardless of the soundboard Master"
     */
    @Then("{string} plays at {int}% output regardless of the soundboard Master")
    fun playsAtOutputRegardlessOfSoundboardMaster(trackId: String, expectedPercent: Int) {
        assertEquals(
            "Soundscape '$trackId' should be unaffected by soundboard Master",
            expectedPercent,
            fakeMusicPlayer.getEffectiveVolumePercent(trackId),
        )
    }

    // =========================================================================
    // play_mixed_track_loops_and_sounds.feature
    // =========================================================================

    // ---- Given ---------------------------------------------------------------

    /** Starts looping a soundscape category via FakeMusicPlayer.
     *  E.g. "Given the "Weather" category is looping"
     */
    @Given("the {string} category is looping")
    fun theCategoryIsLooping(category: String) {
        fakeMusicPlayer.playLoopingSound(category)
    }

    /** Starts two soundscape categories looping simultaneously.
     *  E.g. "Given "Weather" and "Interior" categories are both looping"
     */
    @Given("{string} and {string} categories are both looping")
    fun twoCategoriesAreLooping(category1: String, category2: String) {
        fakeMusicPlayer.playLoopingSound(category1)
        fakeMusicPlayer.playLoopingSound(category2)
    }

    /**
     * Sets up a looping category and a soundboard one-shot sound already playing.
     * E.g. "Given "Weather" is looping and "Sword Clash" is playing from the soundboard"
     */
    @Given("{string} is looping and {string} is playing from the soundboard")
    fun categoryIsLoopingAndSoundboardIsPlaying(category: String, soundboardTrack: String) {
        fakeMusicPlayer.playLoopingSound(category)
        fakeMusicPlayer.playSingleSound(soundboardTrack)
    }

    /**
     * Complex setup for Master Atmosphere vs soundboard independence test.
     * Deferred to Iteration 7 – the FakeMusicPlayer volume model conflates
     * globalVolume with both soundscape and soundboard tracks.
     */
    @Given("{string} is playing at Master {int}% and {string} is on the soundboard")
    fun complexMasterSoundboardSetup(
        @Suppress("UNUSED_PARAMETER") soundscapeTrack: String,
        @Suppress("UNUSED_PARAMETER") masterPercent: Int,
        @Suppress("UNUSED_PARAMETER") soundboardTrack: String,
    ) {
        throw PendingException(
            "Separate Master Atmosphere vs Soundboard Master volume model is an Iteration 7 concern.",
        )
    }

    /**
     * Concurrency limit setup for soundscapes – deferred to a future iteration.
     */
    @Given("there are {int} soundscape categories currently looping")
    fun thereAreSoundscapeCategoriesLooping(
        @Suppress("UNUSED_PARAMETER") count: Int,
    ) {
        throw PendingException("Soundscape concurrency limits are a future iteration concern.")
    }

    /**
     * Concurrency limit setup for soundboard effects – deferred to a future iteration.
     */
    @Given("there are {int} soundboard effects currently playing simultaneously")
    fun thereAreSoundboardEffectsPlaying(
        @Suppress("UNUSED_PARAMETER") count: Int,
    ) {
        throw PendingException("Soundboard effect concurrency limits are a future iteration concern.")
    }

    // ---- When ---------------------------------------------------------------

    /**
     * Plays a one-shot soundboard sound directly via FakeMusicPlayer.
     * E.g. "When I tap "Thunder Crack" on the soundboard"
     */
    @When("I tap {string} on the soundboard")
    fun iTapOnTheSoundboard(soundId: String) {
        fakeMusicPlayer.playSingleSound(soundId)
    }

    /** Pauses a currently looping category.
     *  E.g. "When I pause the "Weather" category"
     */
    @When("I pause the {string} category")
    fun iPauseTheCategory(category: String) {
        fakeMusicPlayer.pauseLoopingSound(category)
    }

    /** Attempts to start an additional soundscape beyond the current limit – deferred. */
    @When("I attempt to play an {int}th soundscape category")
    fun iAttemptToPlayAnAdditionalSoundscape(
        @Suppress("UNUSED_PARAMETER") n: Int,
    ) {
        throw PendingException("Soundscape concurrency enforcement is a future iteration concern.")
    }

    /** Triggers a soundboard effect beyond the current concurrency limit – deferred. */
    @When("I trigger a {int}th soundboard effect")
    fun iTriggerAnAdditionalSoundboardEffect(
        @Suppress("UNUSED_PARAMETER") n: Int,
    ) {
        throw PendingException("Soundboard effect concurrency enforcement is a future iteration concern.")
    }

    // ---- Then ---------------------------------------------------------------

    /**
     * Asserts a one-shot sound was played AND a looping category is still active.
     * E.g. "Then "Thunder Crack" plays simultaneously with the "Weather" loop"
     */
    @Then("{string} plays simultaneously with the {string} loop")
    fun soundPlaysSimultaneouslyWithLoop(soundId: String, loopCategory: String) {
        assertTrue(
            "Expected '$soundId' to have been played, but played list was: ${fakeMusicPlayer.played}",
            fakeMusicPlayer.played.contains(soundId),
        )
        assertTrue(
            "Expected '$loopCategory' to still be looping",
            fakeMusicPlayer.isLooping(loopCategory),
        )
    }

    /**
     * Asserts both soundscape categories are still looping (not interrupted by a soundboard tap).
     * E.g. "Then "Weather" and "Interior" continue looping uninterrupted"
     */
    @Then("{string} and {string} continue looping uninterrupted")
    fun twoCategoriesContinueLooping(category1: String, category2: String) {
        assertTrue("Expected '$category1' to still be looping", fakeMusicPlayer.isLooping(category1))
        assertTrue("Expected '$category2' to still be looping", fakeMusicPlayer.isLooping(category2))
    }

    /**
     * Asserts that a previously played sound is still present in the played events.
     * E.g. "Then "Sword Clash" continues to play"
     */
    @Then("{string} continues to play")
    fun soundContinuesToPlay(soundId: String) {
        assertTrue(
            "Expected '$soundId' to have been played (and remain unaffected), " +
                "but played list was: ${fakeMusicPlayer.played}",
            fakeMusicPlayer.played.contains(soundId),
        )
    }

    /**
     * Asserts that exactly the named category has been paused (stopped looping).
     * E.g. "And only "Weather" has stopped"
     */
    @Then("only {string} has stopped")
    fun onlyCategoryHasStopped(category: String) {
        assertFalse(
            "Expected '$category' to have stopped looping, but it is still active",
            fakeMusicPlayer.isLooping(category),
        )
    }

    /** Pending: reduced-level assertion requires separate atmosphere/soundboard volume model. */
    @Then("{string} plays at the reduced level")
    fun playsAtTheReducedLevel(
        @Suppress("UNUSED_PARAMETER") trackId: String,
    ) {
        throw PendingException(
            "Separate Master Atmosphere vs Soundboard Master volume model is an Iteration 7 concern.",
        )
    }

    /** Pending: soundboard independence from Master Atmosphere requires separate volume model. */
    @But("{string} is unaffected by the Master Atmosphere slider")
    fun isUnaffectedByMasterAtmosphereSlider(
        @Suppress("UNUSED_PARAMETER") trackId: String,
    ) {
        throw PendingException(
            "Separate Master Atmosphere vs Soundboard Master volume model is an Iteration 7 concern.",
        )
    }

    /** Pending: concurrency automatic-stop behaviour is a future iteration concern. */
    @Then("the oldest playing soundscape category loop automatically stops")
    fun oldestSoundscapeLoopStops() {
        throw PendingException("Soundscape concurrency enforcement is a future iteration concern.")
    }

    /** Pending: concurrency – new soundscape starts after eviction. */
    @Then("the new {int}th soundscape begins playing")
    fun newSoundscapeBegins(
        @Suppress("UNUSED_PARAMETER") n: Int,
    ) {
        throw PendingException("Soundscape concurrency enforcement is a future iteration concern.")
    }

    /** Pending: concurrency automatic-stop for soundboard effects. */
    @Then("the oldest playing soundboard effect instantly stops")
    fun oldestSoundboardEffectStops() {
        throw PendingException("Soundboard effect concurrency enforcement is a future iteration concern.")
    }

    /** Pending: concurrency – new soundboard effect starts after eviction. */
    @Then("the new {int}th soundboard effect begins playing")
    fun newSoundboardEffectBegins(
        @Suppress("UNUSED_PARAMETER") n: Int,
    ) {
        throw PendingException("Soundboard effect concurrency enforcement is a future iteration concern.")
    }

    // =========================================================================
    // retrigger_soundboard_effect.feature
    // =========================================================================

    // ---- Given ---------------------------------------------------------------

    /**
     * Plays a single sound once so it is "currently playing".
     * E.g. "Given I have tapped "Thunder Crack" and it is currently playing"
     */
    @Given("I have tapped {string} and it is currently playing")
    fun iHaveTappedAndItIsPlaying(soundId: String) {
        fakeMusicPlayer.playSingleSound(soundId)
    }

    /**
     * Plays two one-shot sounds so both are in the played list.
     * E.g. "Given "Thunder Crack" and "Wolf Howl" are both playing"
     */
    @Given("{string} and {string} are both playing")
    fun twoSoundsAreBothPlaying(sound1: String, sound2: String) {
        fakeMusicPlayer.playSingleSound(sound1)
        fakeMusicPlayer.playSingleSound(sound2)
    }

    /** Pending: global FX concurrency limit is not yet implemented. */
    @Given("the global FX concurrency limit is {int}")
    fun theGlobalFxConcurrencyLimitIs(
        @Suppress("UNUSED_PARAMETER") limit: Int,
    ) {
        throw PendingException("Global FX concurrency limits are a future iteration concern.")
    }

    /** Pending: multi-instance tracking of a single sound is a future iteration concern. */
    @Given("I have three instances of {string} playing simultaneously")
    fun iHaveThreeInstancesPlaying(
        @Suppress("UNUSED_PARAMETER") soundId: String,
    ) {
        throw PendingException("Multi-instance soundboard effect tracking is a future iteration concern.")
    }

    // ---- When ---------------------------------------------------------------

    /**
     * Re-triggers a sound (taps it again).
     * E.g. "When I tap "Thunder Crack" again"
     */
    @When("I tap {string} again")
    fun iTapSoundAgain(soundId: String) {
        fakeMusicPlayer.playSingleSound(soundId)
    }

    /** Pending: rapid six-tap concurrency test requires concurrency limit implementation. */
    @When("I tap {string} six times in quick succession")
    fun iTapSixTimesInQuickSuccession(
        @Suppress("UNUSED_PARAMETER") soundId: String,
    ) {
        throw PendingException("Rapid-fire concurrency testing is a future iteration concern.")
    }

    /** Pending: pause icon on a soundboard button is a future iteration concern. */
    @When("I tap the pause icon on the {string} button")
    fun iTapThePauseIconOnButton(
        @Suppress("UNUSED_PARAMETER") soundId: String,
    ) {
        throw PendingException("Pause-all-instances for a soundboard button is a future iteration concern.")
    }

    // ---- Then ---------------------------------------------------------------

    /**
     * Asserts at least two play events for the same sound, confirming the retrigger
     * started a new instance from the beginning with near-instant response.
     */
    @Then("the second {string} instance starts from the beginning with near-instant \\(low latency\\) response")
    fun secondInstanceStartsFromBeginning(soundId: String) {
        val count = fakeMusicPlayer.played.count { it == soundId }
        assertTrue(
            "Expected at least 2 play events for '$soundId' (retrigger), but found $count. " +
                "Played: ${fakeMusicPlayer.played}",
            count >= 2,
        )
    }

    /**
     * Asserts the first instance is still represented in the played list (i.e. it was not stopped).
     * FakeMusicPlayer never removes entries from [FakeMusicPlayer.played], so count >= 2 is sufficient.
     */
    @Then("the first {string} instance continues playing simultaneously")
    fun firstInstanceContinuesPlaying(soundId: String) {
        val count = fakeMusicPlayer.played.count { it == soundId }
        assertTrue(
            "Expected the first instance of '$soundId' to still be playing (count >= 2), " +
                "but played list was: ${fakeMusicPlayer.played}",
            count >= 2,
        )
    }

    /** Pending: concurrency enforcement for rapid retriggers. */
    @Then("the first instance of {string} stops immediately")
    fun firstInstanceStops(
        @Suppress("UNUSED_PARAMETER") soundId: String,
    ) {
        throw PendingException("FX concurrency eviction is a future iteration concern.")
    }

    /** Pending: concurrency limit assertion. */
    @Then("only {int} simultaneous instances of {string} are playing")
    fun onlyNSimultaneousInstancesArePlaying(
        @Suppress("UNUSED_PARAMETER") limit: Int,
        @Suppress("UNUSED_PARAMETER") soundId: String,
    ) {
        throw PendingException("FX concurrency limit enforcement is a future iteration concern.")
    }

    /**
     * Asserts a new instance of a sound was triggered (count increased to ≥ 2).
     * E.g. "Then a new "Thunder Crack" instance starts"
     */
    @Then("a new {string} instance starts")
    fun aNewInstanceStarts(soundId: String) {
        val count = fakeMusicPlayer.played.count { it == soundId }
        assertTrue(
            "Expected a new instance of '$soundId' to have started (count >= 2), " +
                "but played list was: ${fakeMusicPlayer.played}",
            count >= 2,
        )
    }

    /**
     * Asserts a sound continues uninterrupted (still present in the played list).
     * E.g. "And "Wolf Howl" continues uninterrupted"
     */
    @Then("{string} continues uninterrupted")
    fun soundContinuesUninterrupted(soundId: String) {
        assertTrue(
            "Expected '$soundId' to remain in the played list (uninterrupted), " +
                "but played list was: ${fakeMusicPlayer.played}",
            fakeMusicPlayer.played.contains(soundId),
        )
    }

    /** Pending: stopping all instances requires multi-instance tracking. */
    @Then("all three {string} instances stop immediately")
    fun allThreeInstancesStop(
        @Suppress("UNUSED_PARAMETER") soundId: String,
    ) {
        throw PendingException("Multi-instance stop-all is a future iteration concern.")
    }

    /** Pending: UI idle-state assertion. */
    @Then("the button returns to the idle state")
    fun theButtonReturnsToIdleState() {
        throw PendingException("Soundboard button idle-state UI is a future iteration concern.")
    }

    // =========================================================================
    // play_a_track_in_a_loop_from_category_pool.feature
    // All scenarios require tapping category cards on the ActiveSceneScreen,
    // which is reached via a complex navigation chain (Campaign → Session →
    // tap scene card).  Deferred to Iteration 7.
    // =========================================================================

    @Given("a scene has the category {string} with tracks {string} and {string}")
    fun aSceneHasCategoryWithTracks(
        @Suppress("UNUSED_PARAMETER") category: String,
        @Suppress("UNUSED_PARAMETER") track1: String,
        @Suppress("UNUSED_PARAMETER") track2: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @When("I tap play on the {string} category")
    fun iTapPlayOnTheCategory(
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Then("a track from {string} starts looping")
    fun aTrackFromCategoryStartsLooping(
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Given("a scene has the category {string}")
    fun aSceneHasTheCategory(
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Then("the {string} card shows the coloured glow playing state")
    fun theCardShowsGlowPlayingState(
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Given("the {string} category is currently looping")
    fun theCategoryIsCurrentlyLooping(
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @When("I tap pause on the {string} category")
    fun iTapPauseOnTheCategory(
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Then("the loop stops")
    fun theLoopStops() {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Then("the {string} card no longer shows the playing state")
    fun theCardNoLongerShowsPlayingState(
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    /** Two-arg "a scene has categories X and Y" (no volume info – different from the four-arg volume variant). */
    @Given("a scene has categories {string} and {string}")
    fun aSceneHasTwoCategories(
        @Suppress("UNUSED_PARAMETER") category1: String,
        @Suppress("UNUSED_PARAMETER") category2: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @When("I tap play on {string}")
    fun iTapPlayOn(
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Then("{string} and {string} are both looping at the same time")
    fun twoCategoriesAreBothLoopingAtTheSameTime(
        @Suppress("UNUSED_PARAMETER") category1: String,
        @Suppress("UNUSED_PARAMETER") category2: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Given("the {string} category has the intensity set to {word}")
    fun theCategoryHasIntensitySet(
        @Suppress("UNUSED_PARAMETER") category: String,
        @Suppress("UNUSED_PARAMETER") level: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @And("{string} has tracks {string} at level {string} and {string} at level {string}")
    fun categoryHasTracksAtLevels(
        @Suppress("UNUSED_PARAMETER") category: String,
        @Suppress("UNUSED_PARAMETER") track1: String,
        @Suppress("UNUSED_PARAMETER") level1: String,
        @Suppress("UNUSED_PARAMETER") track2: String,
        @Suppress("UNUSED_PARAMETER") level2: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    @Then("a track from intensity level {word} plays \\(not from level {word}\\)")
    fun aTrackFromIntensityLevelPlays(
        @Suppress("UNUSED_PARAMETER") playedLevel: String,
        @Suppress("UNUSED_PARAMETER") skippedLevel: String,
    ) {
        throw PendingException(
            "ActiveSceneScreen navigation via acceptance test is an Iteration 7 concern.",
        )
    }

    // =========================================================================
    // modify_intensity_level_of_loopable_track.feature
    // All steps require the ActiveSceneScreen with intensity buttons – deferred.
    // =========================================================================

    @Given("the {string} category has tracks at intensity level {word}")
    fun theCategoryHasTracksAtIntensityLevel(
        @Suppress("UNUSED_PARAMETER") category: String,
        @Suppress("UNUSED_PARAMETER") level: String,
    ) {
        throw PendingException(
            "Intensity level selection on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @When("I tap {string} on the {string} category")
    fun iTapOnTheCategory(
        @Suppress("UNUSED_PARAMETER") label: String,
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "Intensity level selection on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Then("a track from the intensity {word} pool plays")
    fun aTrackFromIntensityPoolPlays(
        @Suppress("UNUSED_PARAMETER") level: String,
    ) {
        throw PendingException(
            "Intensity level selection on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Then("{string} should be highlighted in gold on the {string} category")
    fun labelShouldBeHighlightedInGold(
        @Suppress("UNUSED_PARAMETER") label: String,
        @Suppress("UNUSED_PARAMETER") category: String,
    ) {
        throw PendingException(
            "Intensity button gold highlight on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Given("the {string} category is playing at intensity level {word}")
    fun theCategoryIsPlayingAtIntensityLevel(
        @Suppress("UNUSED_PARAMETER") category: String,
        @Suppress("UNUSED_PARAMETER") level: String,
    ) {
        throw PendingException(
            "Intensity level crossfade on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Then("the new intensity level {word} track begins playing immediately")
    fun newIntensityTrackBeginsPlaying(
        @Suppress("UNUSED_PARAMETER") level: String,
    ) {
        throw PendingException(
            "Intensity level crossfade on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Then("the previous intensity level {word} track remains audible while fading out")
    fun previousIntensityTrackFadesOut(
        @Suppress("UNUSED_PARAMETER") level: String,
    ) {
        throw PendingException(
            "Intensity level crossfade on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Then("the {int}-second crossfade allows both tracks to be heard simultaneously during the transition")
    fun crossfadeAllowsBothTracksDuringTransition(
        @Suppress("UNUSED_PARAMETER") durationSeconds: Int,
    ) {
        throw PendingException(
            "Intensity level crossfade on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Given("the {string} category is currently at {string}")
    fun theCategoryIsCurrentlyAtIntensityLevel(
        @Suppress("UNUSED_PARAMETER") category: String,
        @Suppress("UNUSED_PARAMETER") intensityLabel: String,
    ) {
        throw PendingException(
            "Intensity level state on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Given("the {string} category has no tracks at {string}")
    fun theCategoryHasNoTracksAt(
        @Suppress("UNUSED_PARAMETER") category: String,
        @Suppress("UNUSED_PARAMETER") intensityLabel: String,
    ) {
        throw PendingException(
            "Intensity level empty-pool on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Then("the active intensity level should remain {string}")
    fun activeIntensityLevelShouldRemain(
        @Suppress("UNUSED_PARAMETER") intensityLabel: String,
    ) {
        throw PendingException(
            "Intensity level state on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    @Then("{string} on the {string} card should be greyed out")
    fun labelOnCardShouldBeGreyedOut(
        @Suppress("UNUSED_PARAMETER") label: String,
        @Suppress("UNUSED_PARAMETER") card: String,
    ) {
        throw PendingException(
            "Intensity button greyed-out state on ActiveSceneScreen is an Iteration 7 concern.",
        )
    }

    // =========================================================================
    // reorder_soundboard_effects.feature
    // Drag-and-drop reordering is not yet implemented – all steps deferred.
    // =========================================================================

    @Given("there are at least two effect buttons in the soundboard")
    fun thereAreAtLeastTwoEffectButtons() {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @When("I long-press on the {string} button")
    fun iLongPressOnButton(
        @Suppress("UNUSED_PARAMETER") label: String,
    ) {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @Then("the button enters drag mode and can be repositioned")
    fun theButtonEntersDragMode() {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @Given("the soundboard has buttons in the order {string}, {string}, {string}")
    fun theSoundboardHasButtonsInOrder(
        @Suppress("UNUSED_PARAMETER") first: String,
        @Suppress("UNUSED_PARAMETER") second: String,
        @Suppress("UNUSED_PARAMETER") third: String,
    ) {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @When("I drag {string} to the first position")
    fun iDragToFirstPosition(
        @Suppress("UNUSED_PARAMETER") label: String,
    ) {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @Then("the order becomes {string}, {string}, {string}")
    fun theOrderBecomes(
        @Suppress("UNUSED_PARAMETER") first: String,
        @Suppress("UNUSED_PARAMETER") second: String,
        @Suppress("UNUSED_PARAMETER") third: String,
    ) {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @Given("{string} is the first button in the soundboard")
    fun isTheFirstButtonInTheSoundboard(
        @Suppress("UNUSED_PARAMETER") label: String,
    ) {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @And("I close and reopen the scene")
    fun iCloseAndReopenTheScene() {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @Then("{string} is still the first button")
    fun isStillTheFirstButton(
        @Suppress("UNUSED_PARAMETER") label: String,
    ) {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @Given("{string} is currently playing from the soundboard")
    fun isCurrentlyPlayingFromTheSoundboard(
        @Suppress("UNUSED_PARAMETER") label: String,
    ) {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @When("I reorder other effect buttons around it")
    fun iReorderOtherEffectButtonsAroundIt() {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }

    @Then("{string} continues playing uninterrupted")
    fun continuesPlayingUninterrupted(
        @Suppress("UNUSED_PARAMETER") label: String,
    ) {
        throw PendingException("Soundboard drag-and-drop reordering is a future iteration concern.")
    }
}
