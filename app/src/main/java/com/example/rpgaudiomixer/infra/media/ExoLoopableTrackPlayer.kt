package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExoLoopableTrackPlayer(
    private val track: String,
    private val appContext: Context,
) : TrackPlayer {

    private var player: ExoPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var currentVolume: Float = 1.0f

    override fun playTrack() {
        if (player == null) {
            val uri = resolveTrackUri(track)
            player = ExoPlayer.Builder(appContext).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                setMediaItem(MediaItem.fromUri(uri))
                volume = currentVolume
                prepare()
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }
                })
            }
        }
        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
        _isPlaying.value = false
    }

    override fun resume() {
        player?.play()
    }

    override fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        player?.volume = currentVolume
    }

    override fun release() {
        player?.release()
        player = null
        _isPlaying.value = false
    }

    private fun resolveTrackUri(track: String): Uri {
        // If it's already a full URI (ex: file:///android_asset/... or content://...), just use it.
        if ("://" in track) return Uri.parse(track)

        // If it's a raw resource name (ex: dog_bark), map it to android.resource://...
        val rawResId = appContext.resources.getIdentifier(track, "raw", appContext.packageName)
        if (rawResId != 0) return rawResourceUri(rawResId)

        throw TrackNotFoundException(
            "Unable to resolve track '$track'. Provide a full URI (file:///android_asset/...) or a valid raw resource name."
        )
    }

    private fun rawResourceUri(@RawRes resId: Int): Uri =
        Uri.parse("android.resource://${appContext.packageName}/$resId")
}
