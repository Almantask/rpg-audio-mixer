package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.fx.FxPreviewPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoFxPreviewPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
) : FxPreviewPlayer {
    private var player: ExoPlayer? = null

    override fun play(filePath: String) {
        val exoPlayer = player ?: ExoPlayer.Builder(context).build().also { player = it }
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(filePath)))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
        player?.clearMediaItems()
    }
}
