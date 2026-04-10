package com.example.rpgaudiomixer.domain.media

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.storage.TrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Placeholder Android implementation.
 *
 * A real implementation would use SoundPool/MediaPlayer/ExoPlayer depending on requirements.
 */
class MixedMusicPlayerImpl(
    private val appContext: Context,
    private val trackFactory: TrackFactory,
    private val trackRepository: TrackRepository,
) : MixedMusicPlayer {

    private val _previewState = MutableStateFlow(PreviewPlaybackState())
    override val previewState: StateFlow<PreviewPlaybackState> = _previewState.asStateFlow()

    private var previewPlayer: ExoPlayer? = null

    override fun playSingleSound(soundId: String) {
        val soundFilePath = trackRepository.getTrackFilePath(soundId)
        val trackPlayer = trackFactory.createOneTimeTrackPlayer(soundFilePath)
        trackPlayer.play()
    }

    override fun playLoopingSound(categoryId: String) {
        TODO("Not yet implemented")
    }

    override fun startPreview(queue: List<PreviewQueueItem>, startIndex: Int) {
        if (queue.isEmpty() || startIndex !in queue.indices) {
            stopPreview()
            return
        }

        _previewState.value = PreviewPlaybackState(
            queue = queue,
            currentIndex = startIndex,
            isPlaying = true,
        )
        playCurrentPreview()
    }

    override fun togglePreviewPlayback() {
        val player = previewPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            _previewState.value = _previewState.value.copy(isPlaying = false)
        } else {
            player.play()
            _previewState.value = _previewState.value.copy(isPlaying = true)
        }
    }

    override fun playNextPreview() {
        val nextIndex = _previewState.value.currentIndex + 1
        if (nextIndex !in _previewState.value.queue.indices) return

        _previewState.value = _previewState.value.copy(
            currentIndex = nextIndex,
            isPlaying = true,
        )
        playCurrentPreview()
    }

    override fun playPreviousPreview() {
        val previousIndex = _previewState.value.currentIndex - 1
        if (previousIndex !in _previewState.value.queue.indices) return

        _previewState.value = _previewState.value.copy(
            currentIndex = previousIndex,
            isPlaying = true,
        )
        playCurrentPreview()
    }

    override fun stopPreview() {
        previewPlayer?.release()
        previewPlayer = null
        _previewState.value = PreviewPlaybackState()
    }

    private fun playCurrentPreview() {
        val currentItem = _previewState.value.currentItem ?: return
        val soundFilePath = trackRepository.getTrackFilePath(currentItem.soundId)
        val mediaItem = MediaItem.fromUri(resolveTrackUri(soundFilePath))
        val player = previewPlayer ?: buildPreviewPlayer().also { createdPlayer ->
            previewPlayer = createdPlayer
        }

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun buildPreviewPlayer(): ExoPlayer {
        return ExoPlayer.Builder(appContext).build().apply {
            addListener(
                object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _previewState.value = _previewState.value.copy(isPlaying = isPlaying)
                    }
                },
            )
        }
    }

    private fun resolveTrackUri(track: String): Uri {
        if ("://" in track) return Uri.parse(track)
        if (track.startsWith("/")) return Uri.parse("file://$track")

        val rawResId = appContext.resources.getIdentifier(track, "raw", appContext.packageName)
        if (rawResId != 0) {
            return Uri.parse("android.resource://${appContext.packageName}/$rawResId")
        }

        val resolvedTrack = trackRepository.getTrackFilePath(track)
        if ("://" in resolvedTrack) return Uri.parse(resolvedTrack)
        if (resolvedTrack.startsWith("/")) return Uri.parse("file://$resolvedTrack")
        return Uri.parse(resolvedTrack)
    }
}
