package com.example.rpgaudiomixer.infra.media

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoOneTimeTrackPlayer(
    private val track: String,
    private val appContext: Context,
) : TrackPlayer {
    private var player: ExoPlayer? = null

    override val isPlaying: Boolean
        get() = player?.isPlaying == true

    override fun play() {
        release()
        player = ExoPlayer.Builder(appContext).build().apply {
            addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            release()
                            player = null
                        }
                    }
                },
            )
            setMediaItem(MediaItem.fromUri(TrackUriResolver.resolve(track, appContext)))
            prepare()
            play()
        }
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
        release()
    }

    override fun resume() {
        player?.play()
    }

    override fun setVolume(volume: Float) {
        player?.volume = volume.coerceIn(0f, 1f)
    }

    override fun release() {
        player?.release()
        player = null
    }
}
