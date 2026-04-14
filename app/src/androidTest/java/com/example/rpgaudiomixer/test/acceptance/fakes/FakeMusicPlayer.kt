package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Pure fake implementation with no external dependencies.
 *
 * PicoContainer constructs this automatically and injects it into step definitions and rules.
 */
class FakeMusicPlayer : MixedMusicPlayer {

    data class PlayEvent(
        val soundId: String,
        val startedAtNanos: Long,
    )

    private val _played = CopyOnWriteArrayList<String>()
    val played: List<String> get() = _played.toList()

    private val _playEvents = CopyOnWriteArrayList<PlayEvent>()
    val playEvents: List<PlayEvent> get() = _playEvents.toList()

    private val _loopingCategories = CopyOnWriteArraySet<String>()

    // --- Volume state (used by acceptance step definitions) ---

    private var _globalVolume = 100
    private var _soundboardVolume = 100
    private val _loopingTrackVolumes = mutableMapOf<String, Int>()
    private val _soundboardTrackIds = mutableSetOf<String>()

    fun setGlobalVolumePercent(volumePercent: Int) {
        _globalVolume = volumePercent
    }

    fun setSoundboardVolumePercent(volumePercent: Int) {
        _soundboardVolume = volumePercent
    }

    /** Registers a track as a loopable track with an optional initial volume (defaults to 100%). */
    fun registerLoopingTrack(trackId: String, volumePercent: Int = 100) {
        _loopingTrackVolumes[trackId] = volumePercent
    }

    /** Sets the per-track volume for a loopable track (registers it implicitly if not yet known). */
    fun setLoopingTrackVolumePercent(trackId: String, volumePercent: Int) {
        _loopingTrackVolumes[trackId] = volumePercent
    }

    /** Registers a track as a soundboard (one-shot) track so volume assertions can find it. */
    fun registerSoundboardTrack(trackId: String) {
        _soundboardTrackIds += trackId
    }

    fun getGlobalVolumePercent(): Int = _globalVolume

    fun getSoundboardVolumePercent(): Int = _soundboardVolume

    /**
     * Returns the effective playback volume for a track.
     *
     * Loopable tracks: per-track volume × global volume / 100
     * Soundboard tracks: soundboard volume × global volume / 100
     */
    fun getEffectiveVolumePercent(trackId: String): Int {
        return when {
            trackId in _loopingTrackVolumes ->
                _loopingTrackVolumes.getValue(trackId) * _globalVolume / 100
            trackId in _soundboardTrackIds ->
                _soundboardVolume * _globalVolume / 100
            else -> error("Track '$trackId' was not registered in FakeMusicPlayer")
        }
    }

    /** Effective soundboard volume = soundboard volume × global volume / 100. */
    fun getEffectiveSoundboardVolumePercent(): Int = _soundboardVolume * _globalVolume / 100

    /** Returns the set of category IDs currently looping. */
    fun getLoopingCategories(): Set<String> = _loopingCategories.toSet()

    // --- MixedMusicPlayer implementation ---

    override fun playSingleSound(soundId: String) {
        _played += soundId
        _playEvents += PlayEvent(soundId = soundId, startedAtNanos = System.nanoTime())
    }

    override fun playLoopingSound(categoryId: String) {
        _loopingCategories += categoryId
    }

    override fun pauseLoopingSound(categoryId: String) {
        _loopingCategories -= categoryId
    }

    override fun stopAll() {
        _loopingCategories.clear()
    }

    override fun isLooping(categoryId: String): Boolean = categoryId in _loopingCategories

    fun reset() {
        _played.clear()
        _playEvents.clear()
        _loopingCategories.clear()
        _globalVolume = 100
        _soundboardVolume = 100
        _loopingTrackVolumes.clear()
        _soundboardTrackIds.clear()
    }
}