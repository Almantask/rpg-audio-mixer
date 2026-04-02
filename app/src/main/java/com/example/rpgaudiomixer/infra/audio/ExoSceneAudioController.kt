package com.example.rpgaudiomixer.infra.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.audio.SceneAudioController
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExoSceneAudioController @Inject constructor(
    @ApplicationContext private val context: Context,
) : SceneAudioController {

    private data class CategoryPlayer(
        val player: ExoPlayer,
        var volume: Float = 1.0f,
        var currentTrackName: String? = null,
    )

    private data class FXPlayer(val player: ExoPlayer)

    private val categoryPlayers = mutableMapOf<Long, CategoryPlayer>()
    private val fxPlayers = mutableMapOf<Long, FXPlayer>()

    private var masterSoundscapeVolume = 1.0f
    private var masterFXVolume = 1.0f

    // ── Soundscape ────────────────────────────────────────────────────────────

    override fun playCategory(categoryId: Long, intensityLevel: IntensityLevel, filePaths: List<String>) {
        if (filePaths.isEmpty()) return

        val trackPath = filePaths.random()
        val uri = Uri.parse(trackPath)

        val existing = categoryPlayers[categoryId]
        if (existing != null) {
            existing.player.setMediaItem(MediaItem.fromUri(uri))
            existing.player.prepare()
            existing.player.play()
            existing.currentTrackName = trackPath.substringAfterLast("/")
        } else {
            val player = ExoPlayer.Builder(context).build().also { p ->
                p.setMediaItem(MediaItem.fromUri(uri))
                p.repeatMode = Player.REPEAT_MODE_ONE
                p.volume = masterSoundscapeVolume
                p.prepare()
                p.play()
            }
            categoryPlayers[categoryId] = CategoryPlayer(
                player = player,
                volume = 1.0f,
                currentTrackName = trackPath.substringAfterLast("/"),
            )
        }
        applyMasterSoundscapeVolume()
    }

    override fun stopCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.player?.stop()
    }

    override fun isCategoryPlaying(categoryId: Long): Boolean =
        categoryPlayers[categoryId]?.player?.isPlaying == true

    override fun currentTrackForCategory(categoryId: Long): String? =
        categoryPlayers[categoryId]?.currentTrackName

    override fun setCategoryVolume(categoryId: Long, volume: Float) {
        categoryPlayers[categoryId]?.let { cp ->
            cp.volume = volume.coerceIn(0f, 1f)
            cp.player.volume = (masterSoundscapeVolume * cp.volume).coerceIn(0f, 1f)
        }
    }

    override fun setMasterSoundscapeVolume(volume: Float) {
        masterSoundscapeVolume = volume.coerceIn(0f, 1f)
        applyMasterSoundscapeVolume()
    }

    private fun applyMasterSoundscapeVolume() {
        categoryPlayers.values.forEach { cp ->
            cp.player.volume = (masterSoundscapeVolume * cp.volume).coerceIn(0f, 1f)
        }
    }

    // ── Soundboard ────────────────────────────────────────────────────────────

    override fun playFX(fxId: Long, filePath: String) {
        val uri = Uri.parse(filePath)

        // Re-trigger: stop existing instance and create a new one
        fxPlayers[fxId]?.player?.let { old ->
            old.stop()
            old.release()
        }

        val player = ExoPlayer.Builder(context).build().also { p ->
            p.setMediaItem(MediaItem.fromUri(uri))
            p.repeatMode = Player.REPEAT_MODE_OFF
            p.volume = masterFXVolume
            p.prepare()
            p.play()
            // Release when done
            p.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        p.release()
                        fxPlayers.remove(fxId)
                    }
                }
            })
        }
        fxPlayers[fxId] = FXPlayer(player)
    }

    override fun stopFX(fxId: Long) {
        fxPlayers[fxId]?.player?.let { p ->
            p.stop()
            p.release()
        }
        fxPlayers.remove(fxId)
    }

    override fun isFxPlaying(fxId: Long): Boolean =
        fxPlayers[fxId]?.player?.isPlaying == true

    override fun setMasterFXVolume(volume: Float) {
        masterFXVolume = volume.coerceIn(0f, 1f)
        fxPlayers.values.forEach { it.player.volume = masterFXVolume }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun release() {
        categoryPlayers.values.forEach { it.player.release() }
        categoryPlayers.clear()
        fxPlayers.values.forEach { it.player.release() }
        fxPlayers.clear()
    }
}
