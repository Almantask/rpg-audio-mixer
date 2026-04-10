package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.FxTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class SoundboardPlayer(
    private val trackFactory: TrackFactory,
    private val fxRepository: FxRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val fxPlayers = ConcurrentHashMap<Long, TrackPlayer>()
    private var masterVolume: Float = 1f

    fun triggerFx(fxTrack: FxTrack) {
        val player = fxPlayers.getOrPut(fxTrack.id) {
            trackFactory.createOneTimeTrackPlayer(fxTrack.filePath).also {
                it.setVolume(masterVolume)
            }
        }
        player.play()

        // Increment play count
        scope.launch {
            fxRepository.incrementPlayCount(fxTrack.id)
        }
    }

    fun stopFx(fxTrackId: Long) {
        fxPlayers[fxTrackId]?.stop()
    }

    fun setMasterVolume(volume: Float) {
        this.masterVolume = volume
        fxPlayers.values.forEach { it.setVolume(volume) }
    }

    fun releaseAll() {
        fxPlayers.values.forEach { it.release() }
        fxPlayers.clear()
        scope.cancel()
    }
}
