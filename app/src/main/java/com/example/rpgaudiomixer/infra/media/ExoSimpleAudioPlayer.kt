package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayer

/**
 * ExoPlayer-backed [SimpleAudioPlayer] for single-URI preview playback.
 *
 * The player instance is lazy-created on the first [play] call.
 * Call [release] when the owner (typically a ViewModel) is cleared.
 *
 * @param context      application context for ExoPlayer creation
 * @param playerProvider test seam — overridden in unit tests to inject a mock ExoPlayer
 */
class ExoSimpleAudioPlayer @VisibleForTesting internal constructor(
    private val context: Context,
    private val playerProvider: () -> ExoPlayer,
) : SimpleAudioPlayer {

    /** Production constructor — uses real ExoPlayer. */
    constructor(context: Context) : this(
        context = context,
        playerProvider = { ExoPlayer.Builder(context).build() },
    )

    private var player: ExoPlayer? = null
    private var isPaused: Boolean = false

    override var currentUri: Uri? = null
        private set

    override val isPlaying: Boolean
        get() = player?.isPlaying ?: false

    override fun play(uri: Uri) {
        val exoPlayer = getOrCreatePlayer()

        if (uri == currentUri && isPaused) {
            exoPlayer.play()
            isPaused = false
            return
        }

        // If something else was loaded, stop it first
        if (currentUri != null) {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        }

        currentUri = uri
        isPaused = false
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun pause() {
        player?.pause()
        if (player != null) isPaused = true
    }

    override fun stop() {
        player?.stop()
        player?.clearMediaItems()
        currentUri = null
        isPaused = false
    }

    override fun release() {
        player?.release()
        player = null
        currentUri = null
        isPaused = false
    }

    private fun getOrCreatePlayer(): ExoPlayer =
        player ?: playerProvider().also { player = it }
}
