package com.example.rpgaudiomixer.test.acceptance.steps

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.test.acceptance.fakes.audio.AudioEngineWorld
import io.cucumber.java.en.And
import io.cucumber.java.en.But
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat

class AudioEngineSteps(
    private val world: AudioEngineWorld,
) {
    private var currentCategoryName: String = ""

    init {
        world.reset()
    }

    @Given("a scene has the category {string} with tracks {string} and {string}")
    fun aSceneHasTheCategoryWithTracks(firstCategory: String, firstTrack: String, secondTrack: String) {
        world.addCategoryWithTracks(firstCategory, listOf(firstTrack, secondTrack))
    }

    @Given("a scene has the category {string}")
    fun aSceneHasTheCategory(categoryName: String) {
        world.addCategoryWithTracks(categoryName, listOf("$categoryName Loop"))
    }

    @Given("a scene has categories {string} and {string}")
    fun aSceneHasCategories(firstCategory: String, secondCategory: String) {
        world.addCategoryWithTracks(firstCategory, listOf("$firstCategory Loop"))
        world.addCategoryWithTracks(secondCategory, listOf("$secondCategory Loop"))
    }

    @When("I tap play on the {string} category")
    fun iTapPlayOnTheCategory(categoryName: String) {
        currentCategoryName = categoryName
        world.playCategory(categoryName)
    }

    @Then("a track from {string} starts looping")
    fun aTrackFromStartsLooping(categoryName: String) {
        assertThat(world.latestPlayedTrack(categoryName)?.name.orEmpty()).isNotBlank()
        assertThat(world.isCategoryPlaying(categoryName)).isTrue()
    }

    @Then("the {string} card shows the coloured glow playing state")
    @Then("the {string} card shows the playing state \\(glow border\\)")
    @Then("the {string} card shows the playing state \\(coloured glow border\\)")
    fun theCardShowsThePlayingState(categoryName: String) {
        assertThat(world.isCategoryPlaying(categoryName)).isTrue()
    }

    @Given("the {string} category is currently looping")
    @Given("the {string} category is looping")
    fun theCategoryIsCurrentlyLooping(categoryName: String) {
        currentCategoryName = categoryName
        world.ensureCategory(categoryName)
        if (world.latestPlayedTrack(categoryName) == null) {
            world.addCategoryWithTracks(categoryName, listOf("$categoryName Loop"))
            world.playCategory(categoryName)
        }
    }

    @When("I tap pause on the {string} category")
    @When("I pause the {string} category")
    fun iTapPauseOnTheCategory(categoryName: String) {
        currentCategoryName = categoryName
        world.pauseCategory(categoryName)
    }

    @Then("the loop stops")
    fun theLoopStops() {
        assertThat(world.allPlayingCategories()).isEmpty()
    }

    @Then("the {string} card no longer shows the playing state")
    @Then("the {string} card no longer shows the glow border")
    @Then("the {string} card does not show the glow border")
    @Then("{string} does not show the playing state")
    fun theCardNoLongerShowsThePlayingState(categoryName: String) {
        assertThat(world.isCategoryPlaying(categoryName)).isFalse()
    }

    @When("I tap play on {string}")
    fun iTapPlayOn(categoryName: String) {
        currentCategoryName = categoryName
        world.playCategory(categoryName)
    }

    @Then("{string} and {string} are both looping at the same time")
    fun areBothLoopingAtTheSameTime(firstCategory: String, secondCategory: String) {
        assertThat(world.isCategoryPlaying(firstCategory)).isTrue()
        assertThat(world.isCategoryPlaying(secondCategory)).isTrue()
    }

    @Given("the {string} category has the intensity set to II")
    fun theCategoryHasTheIntensitySetToIi(categoryName: String) {
        world.setCategoryIntensity(categoryName, IntensityLevel.II)
    }

    @And("{string} has tracks {string} at level II and {string} at level I")
    fun hasTracksAtLevelIiAndLevelI(categoryName: String, levelIiTrack: String, levelITrack: String) {
        world.addCategoryWithTracks(categoryName, listOf(levelIiTrack), IntensityLevel.II)
        world.addCategoryWithTracks(categoryName, listOf(levelITrack), IntensityLevel.I)
    }

    @Then("a track from intensity level II plays \\(not from level I\\)")
    fun aTrackFromIntensityLevelIiPlays() {
        val track = world.latestPlayedTrack(currentCategoryName)
        assertThat(track?.intensityLevel).isEqualTo(IntensityLevel.II)
        assertThat(track?.name.orEmpty()).isNotBlank()
    }

    @When("I tap {string} on the soundboard")
    fun iTapOnTheSoundboard(trackName: String) {
        world.triggerFx(trackName)
    }

    @Then("{string} plays simultaneously with the {string} loop")
    fun playsSimultaneouslyWithTheLoop(trackName: String, categoryName: String) {
        assertThat(world.latestSoundboardPlayer(trackName)?.isPlaying).isEqualTo(true)
        assertThat(world.isCategoryPlaying(categoryName)).isTrue()
    }

    @Given("{string} and {string} categories are both looping")
    fun categoriesAreBothLooping(firstCategory: String, secondCategory: String) {
        world.addCategoryWithTracks(firstCategory, listOf("$firstCategory Loop"))
        world.addCategoryWithTracks(secondCategory, listOf("$secondCategory Loop"))
        world.playCategory(firstCategory)
        world.playCategory(secondCategory)
    }

    @Then("{string} and {string} continue looping uninterrupted")
    fun continueLoopingUninterrupted(firstCategory: String, secondCategory: String) {
        assertThat(world.isCategoryPlaying(firstCategory)).isTrue()
        assertThat(world.isCategoryPlaying(secondCategory)).isTrue()
    }

    @Given("{string} is looping and {string} is playing from the soundboard")
    fun isLoopingAndIsPlayingFromTheSoundboard(categoryName: String, trackName: String) {
        theCategoryIsCurrentlyLooping(categoryName)
        world.triggerFx(trackName)
    }

    @Then("{string} continues to play")
    fun continuesToPlay(trackName: String) {
        assertThat(world.latestSoundboardPlayer(trackName)?.isPlaying).isEqualTo(true)
    }

    @Then("only {string} has stopped")
    fun onlyHasStopped(categoryName: String) {
        assertThat(world.isCategoryPlaying(categoryName)).isFalse()
    }

    @Given("{string} is playing at Master {int}% and {string} is on the soundboard")
    fun isPlayingAtMasterAndIsOnTheSoundboard(categoryName: String, masterPercent: Int, trackName: String) {
        world.addCategoryWithTracks(categoryName, listOf(categoryName))
        world.setCategoryMix(categoryName, 100)
        world.setMasterAtmosphere(masterPercent)
        world.playCategory(categoryName)
        world.triggerFx(trackName)
    }

    @When("I reduce the Master Atmosphere to {int}%")
    @When("I set Master Atmosphere to {int}%")
    fun iReduceTheMasterAtmosphereTo(percent: Int) {
        world.setMasterAtmosphere(percent)
    }

    @Then("{string} plays at the reduced level")
    fun playsAtTheReducedLevel(categoryName: String) {
        currentCategoryName = categoryName
        assertThat(world.categoryOutputPercent(categoryName)).isEqualTo(30)
    }

    @But("{string} is unaffected by the Master Atmosphere slider")
    fun isUnaffectedByTheMasterAtmosphereSlider(trackName: String) {
        assertThat(world.soundboardOutputPercent(trackName)).isEqualTo(100)
    }

    @Given("there are {int} soundscape categories currently looping")
    fun thereAreSoundscapeCategoriesCurrentlyLooping(count: Int) {
        repeat(count) { index ->
            val categoryName = "Category ${index + 1}"
            world.addCategoryWithTracks(categoryName, listOf("Track ${index + 1}"))
            world.playCategory(categoryName)
        }
    }

    @When("I attempt to play an 11th soundscape category")
    fun iAttemptToPlayAn11thSoundscapeCategory() {
        world.addCategoryWithTracks("Category 11", listOf("Track 11"))
        world.playCategory("Category 11")
    }

    @Then("the oldest playing soundscape category loop automatically stops")
    fun theOldestPlayingSoundscapeCategoryLoopAutomaticallyStops() {
        assertThat(world.oldestStoppedCategoryName()).isEqualTo("Category 1")
        assertThat(world.isCategoryPlaying("Category 1")).isFalse()
    }

    @And("the new 11th soundscape begins playing")
    fun theNew11thSoundscapeBeginsPlaying() {
        assertThat(world.isCategoryPlaying("Category 11")).isTrue()
    }

    @Given("there are {int} soundboard effects currently playing simultaneously")
    fun thereAreSoundboardEffectsCurrentlyPlayingSimultaneously(count: Int) {
        repeat(count) { index ->
            world.triggerFx("FX ${index + 1}")
        }
    }

    @When("I trigger a 6th soundboard effect")
    fun iTriggerA6thSoundboardEffect() {
        world.triggerFx("FX 6")
    }

    @Then("the oldest playing soundboard effect instantly stops")
    fun theOldestPlayingSoundboardEffectInstantlyStops() {
        assertThat(world.oldestStoppedFxTrackName()).isEqualTo("FX 1")
    }

    @And("the new 6th soundboard effect begins playing")
    fun theNew6thSoundboardEffectBeginsPlaying() {
        assertThat(world.latestSoundboardPlayer("FX 6")?.isPlaying).isEqualTo(true)
    }

    @Given("a scene has categories {string} at MIX {int}% and {string} at MIX {int}%")
    fun aSceneHasCategoriesAtMix(firstCategory: String, firstMix: Int, secondCategory: String, secondMix: Int) {
        world.addCategoryWithTracks(firstCategory, listOf(firstCategory))
        world.addCategoryWithTracks(secondCategory, listOf(secondCategory))
        world.setCategoryMix(firstCategory, firstMix)
        world.setCategoryMix(secondCategory, secondMix)
        world.playCategory(firstCategory)
        world.playCategory(secondCategory)
    }

    @And("Master Atmosphere is at {int}%")
    fun masterAtmosphereIsAt(percent: Int) {
        world.setMasterAtmosphere(percent)
    }

    @Then("{string} plays at {int}% output")
    fun playsAtOutput(name: String, expectedPercent: Int) {
        val actualPercent = if (world.latestPlayedTrack(name) != null || world.isCategoryPlaying(name)) {
            world.categoryOutputPercent(name)
        } else {
            world.soundboardOutputPercent(name)
        }
        assertThat(actualPercent).isEqualTo(expectedPercent)
    }

    @Given("{string} is playing with Master at {int}% and MIX at {int}%")
    fun isPlayingWithMasterAtAndMixAt(categoryName: String, masterPercent: Int, mixPercent: Int) {
        world.addCategoryWithTracks(categoryName, listOf(categoryName))
        world.setCategoryMix(categoryName, mixPercent)
        world.setMasterAtmosphere(masterPercent)
        world.playCategory(categoryName)
    }

    @When("I set the {string} MIX slider to {int}%")
    fun iSetTheMixSliderTo(categoryName: String, percent: Int) {
        world.setCategoryMix(categoryName, percent)
    }

    @Given("{string} has MIX at {int}% and {string} has MIX at {int}%")
    fun hasMixAtAndHasMixAt(firstCategory: String, firstMix: Int, secondCategory: String, secondMix: Int) {
        aSceneHasCategoriesAtMix(firstCategory, firstMix, secondCategory, secondMix)
        world.setMasterAtmosphere(100)
    }

    @Then("{string} plays at {int}% of Master output")
    fun playsAtOfMasterOutput(categoryName: String, expectedPercent: Int) {
        assertThat(world.categoryOutputPercent(categoryName)).isEqualTo(expectedPercent)
    }

    @And("{string} still plays at {int}% of Master output")
    fun stillPlaysAtOfMasterOutput(categoryName: String, expectedPercent: Int) {
        assertThat(world.categoryOutputPercent(categoryName)).isEqualTo(expectedPercent)
    }

    @Given("the soundboard Master volume is at {int}%")
    @Given("the soundboard Master is at {int}%")
    fun theSoundboardMasterVolumeIsAt(percent: Int) {
        world.setSoundboardMaster(percent)
    }

    @And("{string} is playing from the soundboard")
    fun isPlayingFromTheSoundboard(trackName: String) {
        world.triggerFx(trackName)
    }

    @When("I set the soundboard Master to {int}%")
    fun iSetTheSoundboardMasterTo(percent: Int) {
        world.setSoundboardMaster(percent)
    }

    @Given("{string} is playing as a soundscape at MIX {int}%, Master {int}%")
    fun isPlayingAsASoundscapeAtMixMaster(categoryName: String, mixPercent: Int, masterPercent: Int) {
        world.addCategoryWithTracks(categoryName, listOf(categoryName))
        world.setCategoryMix(categoryName, mixPercent)
        world.setMasterAtmosphere(masterPercent)
        world.playCategory(categoryName)
    }

    @Then("{string} plays at {int}% output regardless of the soundboard Master")
    fun playsAtOutputRegardlessOfTheSoundboardMaster(categoryName: String, expectedPercent: Int) {
        assertThat(world.categoryOutputPercent(categoryName)).isEqualTo(expectedPercent)
    }

    @Given("I have tapped {string} and it is currently playing")
    fun iHaveTappedAndItIsCurrentlyPlaying(trackName: String) {
        world.triggerFx(trackName)
    }

    @When("I tap {string} again")
    fun iTapAgain(trackName: String) {
        world.triggerFx(trackName)
    }

    @Then("the first {string} instance continues playing")
    fun theFirstInstanceContinuesPlaying(trackName: String) {
        assertThat(world.activeFxCount(trackName)).isGreaterThanOrEqualTo(1)
    }

    @And("a second {string} instance starts from the beginning")
    fun aSecondInstanceStartsFromTheBeginning(trackName: String) {
        assertThat(world.activeFxCount(trackName)).isGreaterThanOrEqualTo(2)
    }

    @Given("the global FX concurrency limit is {int}")
    fun theGlobalFxConcurrencyLimitIs(limit: Int) {
        world.reconfigureSoundboardLimit(limit)
    }

    @When("I tap {string} six times in quick succession")
    fun iTapSixTimesInQuickSuccession(trackName: String) {
        repeat(6) {
            world.triggerFx(trackName)
        }
    }

    @Then("the first instance of {string} stops immediately")
    fun theFirstInstanceOfStopsImmediately(trackName: String) {
        assertThat(world.oldestStoppedFxTrackName()).isEqualTo(trackName)
    }

    @And("only {int} simultaneous instances of {string} are playing")
    fun onlySimultaneousInstancesOfArePlaying(expectedCount: Int, trackName: String) {
        assertThat(world.activeFxCount(trackName)).isEqualTo(expectedCount)
    }

    @Given("{string} and {string} are both playing")
    fun areBothPlaying(firstTrack: String, secondTrack: String) {
        world.triggerFx(firstTrack)
        world.triggerFx(secondTrack)
    }

    @Then("a new {string} instance starts")
    fun aNewInstanceStarts(trackName: String) {
        assertThat(world.activeFxCount(trackName)).isGreaterThanOrEqualTo(2)
    }

    @And("{string} continues uninterrupted")
    fun continuesUninterrupted(trackName: String) {
        assertThat(world.activeFxCount(trackName)).isGreaterThanOrEqualTo(1)
    }

    @Given("{string} is playing \\(showing the pause icon\\)")
    fun isPlayingShowingThePauseIcon(trackName: String) {
        world.triggerFx(trackName)
    }

    @When("I tap the pause icon on {string}")
    fun iTapThePauseIconOn(trackName: String) {
        world.stopLatestFx(trackName)
    }

    @Then("{string} stops and the button returns to the idle state")
    fun stopsAndTheButtonReturnsToTheIdleState(trackName: String) {
        assertThat(world.activeFxCount(trackName)).isZero()
    }
}
