package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.repository.FXRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manages one-shot sound effects (SFX) that can overlap.
 */
class SoundboardPlayer(
    private val trackFactory: TrackFactory,
    private val fxRepository: FXRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _fxPlayers = mutableMapOf<Long, TrackPlayer>()
    private var masterFxVolume: Float = 1.0f

    /**
     * Triggers a specific FX.
     * Overlap is handled by [ExoOneTimeTrackPlayer].
     */
    fun triggerFx(fxTrack: FXTrack) {
        val player = _fxPlayers.getOrPut(fxTrack.id) {
            trackFactory.createOneTimeTrackPlayer(fxTrack.filePath).apply {
                setVolume(masterFxVolume)
            }
        }
        player.play()
        
        scope.launch {
            fxRepository.incrementPlayCount(fxTrack.id)
        }
    }

    /**
     * Stops all active instances of a specific FX.
     */
    fun stopFx(fxTrackId: Long) {
        _fxPlayers.remove(fxTrackId)?.stop()
    }

    /**
     * Sets the global master FX volume (0.0 to 1.0).
     */
    fun setMasterVolume(volume: Float) {
        masterFxVolume = volume
        _fxPlayers.values.forEach { it.setVolume(masterFxVolume) }
    }

    /**
     * Stops and releases all players.
     */
    fun release() {
        _fxPlayers.values.forEach { it.release() }
        _fxPlayers.clear()
    }
}
