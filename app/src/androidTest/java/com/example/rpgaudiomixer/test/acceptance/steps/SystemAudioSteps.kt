package com.example.rpgaudiomixer.test.acceptance.steps

import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.PendingException
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Step definitions for system_audio_handling.feature (@iter9).
 *
 * System-level audio events (phone calls, lock screen, Bluetooth remotes) cannot be
 * simulated in instrumented tests without special device permissions or mock OS state.
 * All steps are [PendingException] and document the intent for manual / E2E verification.
 */
class SystemAudioSteps(
    private val fakeMusicPlayer: FakeMusicPlayer,
    private val composeRuleHolder: MainActivityComposeRule,
) {

    @Given("the app is playing a soundscape and a soundboard effect")
    fun appIsPlayingSoundscapeAndSoundboardEffect() {
        throw PendingException(
            "Requires the ActiveSceneScreen to be open with playing categories – promoted in a later iteration."
        )
    }

    @Given("the app is playing audio loops on the Active Scene screen")
    fun appIsPlayingAudioLoops() {
        throw PendingException(
            "Requires the ActiveSceneScreen to be open with playing categories – promoted in a later iteration."
        )
    }

    @Given("the app is playing a soundscape loop")
    fun appIsPlayingSoundscapeLoop() {
        throw PendingException(
            "Requires the ActiveSceneScreen to be open with a playing soundscape – promoted in a later iteration."
        )
    }

    @Given("the media controller or a Bluetooth remote is active")
    fun mediaControllerIsActive() {
        throw PendingException(
            "MediaController/Bluetooth remote cannot be simulated in instrumented tests."
        )
    }

    @When("the device receives a system audio interruption \\(e.g., an incoming phone call or alarm\\)")
    fun systemAudioInterruption() {
        throw PendingException(
            "Audio focus loss cannot be triggered programmatically in instrumented tests."
        )
    }

    @When("an incoming phone call interrupts the app for {int} minutes")
    fun phoneCallInterrupts(minutes: Int) {
        throw PendingException(
            "Phone call simulation is not supported in instrumented tests – manual verification required."
        )
    }

    @When("the phone call ends and focus is regained")
    fun phonCallEndsAndFocusRegained() {
        throw PendingException(
            "Audio focus regain cannot be triggered programmatically in instrumented tests."
        )
    }

    @When("I minimize the app to view my notes in another app")
    fun minimizeApp() {
        throw PendingException(
            "App minimization is a device-level action not automatable in instrumented tests."
        )
    }

    @When("I lock the device")
    fun lockDevice() {
        throw PendingException(
            "Device lock is a system-level action not automatable in instrumented tests."
        )
    }

    @When("I tap pause")
    fun tapPauseOnMediaController() {
        throw PendingException(
            "MediaController tap cannot be simulated in instrumented tests."
        )
    }

    @When("I tap {string} on the lock screen or a Bluetooth remote")
    fun tapOnLockScreenOrRemote(action: String) {
        throw PendingException(
            "Lock screen / Bluetooth remote '$action' cannot be simulated in instrumented tests."
        )
    }

    @Then("all playing audio in the app pauses immediately")
    fun allAudioPausesImmediately() {
        throw PendingException(
            "Audio focus loss scenario requires a device-level interruption – promoted in a later iteration."
        )
    }

    @And("the app visually reflects the paused state on the active playing cards")
    fun appReflectsPausedState() {
        throw PendingException(
            "Visual paused state requires the ActiveSceneScreen to be visible with playing categories."
        )
    }

    @Then("the previously playing loops and soundscapes resume automatically")
    fun previousLoopsResumeAutomatically() {
        throw PendingException(
            "Auto-resume after short focus loss requires device-level simulation – promoted in a later iteration."
        )
    }

    @Then("the app remains paused")
    fun appRemainesPaused() {
        throw PendingException(
            "Long-call pause persistence requires device-level simulation – promoted in a later iteration."
        )
    }

    @And("requires a manual play to resume the soundscape")
    fun requiresManualPlay() {
        throw PendingException(
            "Manual resume verification requires the ActiveSceneScreen to be visible."
        )
    }

    @Then("the audio continues to play seamlessly in the background")
    fun audioContinuesInBackground() {
        throw PendingException(
            "Background playback verification requires actual process backgrounding – cannot be automated in instrumented tests."
        )
    }

    @Then("the lock screen displays a media player for Arcanum Audio")
    fun lockScreenShowsMediaPlayer() {
        throw PendingException(
            "Lock screen media player requires device-level lock state – cannot be automated in instrumented tests."
        )
    }

    @And("it shows the currently playing scene and master track information")
    fun lockScreenShowsSceneInfo() {
        throw PendingException(
            "Lock screen media controller metadata requires device-level lock state – cannot be automated in instrumented tests."
        )
    }

    @Then("the app audio pauses")
    fun appAudioPauses() {
        throw PendingException(
            "Verifying pause via MediaController requires device-level simulation."
        )
    }

    @Then("the app triggers the d20 randomization logic for the active scene")
    fun appTriggersD20Randomization() {
        throw PendingException(
            "D20 trigger via MediaController Next command requires device-level simulation."
        )
    }

    @And("a random track plays from the currently prominent category pool")
    fun randomTrackPlays() {
        throw PendingException(
            "Random track selection verification requires the ActiveSceneScreen to be open with categories."
        )
    }
}
