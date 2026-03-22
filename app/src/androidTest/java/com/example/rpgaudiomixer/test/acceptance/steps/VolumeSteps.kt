package com.example.rpgaudiomixer.test.acceptance.steps

import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert.assertEquals

class VolumeSteps(
    private val fakeMusicPlayer: FakeMusicPlayer,
) {

    // -------------------------------------------------------------------------
    // Given steps (test state setup)
    // -------------------------------------------------------------------------

    @Given("the global volume is at {int}%")
    fun theGlobalVolumeIsAt(volumePercent: Int) {
        fakeMusicPlayer.setGlobalVolumePercent(volumePercent)
    }

    /** Matches "soundboard volume is at X%" (no leading "the"). */
    @Given("soundboard volume is at {int}%")
    fun soundboardVolumeIsAt(volumePercent: Int) {
        fakeMusicPlayer.setSoundboardVolumePercent(volumePercent)
    }

    /** Matches "the soundboard volume is at X%" (with leading "the"). */
    @Given("the soundboard volume is at {int}%")
    fun theSoundboardVolumeIsAt(volumePercent: Int) {
        fakeMusicPlayer.setSoundboardVolumePercent(volumePercent)
    }

    @Given("{string} loopable track is at {int}%")
    fun loopableTrackIsAt(trackId: String, volumePercent: Int) {
        fakeMusicPlayer.registerLoopingTrack(trackId, volumePercent)
    }

    @Given("{string} soundboard track is playing")
    fun soundboardTrackIsPlaying(trackId: String) {
        fakeMusicPlayer.registerSoundboardTrack(trackId)
    }

    @Given("the loopable track {string} is playing")
    fun theLoopableTrackIsPlaying(trackId: String) {
        fakeMusicPlayer.registerLoopingTrack(trackId, 100)
    }

    @Given("the loopable track {string} is playing at {int}% volume")
    fun theLoopableTrackIsPlayingAt(trackId: String, volumePercent: Int) {
        fakeMusicPlayer.registerLoopingTrack(trackId, volumePercent)
    }

    // -------------------------------------------------------------------------
    // When steps (actions)
    // -------------------------------------------------------------------------

    @When("I set the global volume to {int}%")
    fun iSetTheGlobalVolumeTo(volumePercent: Int) {
        fakeMusicPlayer.setGlobalVolumePercent(volumePercent)
    }

    @When("I set the volume of {string} to {int}%")
    fun iSetTheVolumeOfTo(trackId: String, volumePercent: Int) {
        fakeMusicPlayer.setLoopingTrackVolumePercent(trackId, volumePercent)
    }

    @When("I set the soundboard volume to {int}%")
    fun iSetTheSoundboardVolumeTo(volumePercent: Int) {
        fakeMusicPlayer.setSoundboardVolumePercent(volumePercent)
    }

    // -------------------------------------------------------------------------
    // Then steps (assertions)
    // -------------------------------------------------------------------------

    @Then("{string} plays at {int}% volume")
    fun playsAtVolume(trackId: String, expectedVolumePercent: Int) {
        assertEquals(expectedVolumePercent, fakeMusicPlayer.getEffectiveVolumePercent(trackId))
    }

    @Then("{string} continues to play at {int}% volume")
    fun continuesToPlayAtVolume(trackId: String, expectedVolumePercent: Int) {
        assertEquals(expectedVolumePercent, fakeMusicPlayer.getEffectiveVolumePercent(trackId))
    }

    @Then("the global volume remains at {int}%")
    fun theGlobalVolumeRemainsAt(expectedVolumePercent: Int) {
        assertEquals(expectedVolumePercent, fakeMusicPlayer.getGlobalVolumePercent())
    }

    @Then("soundboard sounds play at {int}% volume")
    fun soundboardSoundsPlayAtVolume(expectedVolumePercent: Int) {
        assertEquals(expectedVolumePercent, fakeMusicPlayer.getEffectiveSoundboardVolumePercent())
    }
}
