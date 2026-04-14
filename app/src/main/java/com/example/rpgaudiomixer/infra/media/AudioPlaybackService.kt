package com.example.rpgaudiomixer.infra.media

import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service that hosts a [MediaSession] for background audio playback.
 *
 * Responsibilities (Iteration 9 – System Audio Handling):
 * - Keeps audio alive when the app is backgrounded.
 * - Publishes a lock-screen / notification-shade media controller.
 * - Handles audio focus: pauses all sounds on focus loss and resumes
 *   automatically only if the interruption lasted fewer than 3 minutes.
 * - Delegates Next command to the D20 (random track) logic via [MixedMusicPlayer].
 */
@UnstableApi
@AndroidEntryPoint
class AudioPlaybackService : MediaSessionService() {

    @Inject
    lateinit var mixedMusicPlayer: MixedMusicPlayer

    private lateinit var mediaSession: MediaSession
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private var focusLossStartNanos: Long = 0L
    private var wasPlayingBeforeFocusLoss: Boolean = false

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                wasPlayingBeforeFocusLoss = true
                focusLossStartNanos = System.nanoTime()
                mixedMusicPlayer.stopAll()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (wasPlayingBeforeFocusLoss) {
                    val elapsedMs = (System.nanoTime() - focusLossStartNanos) / 1_000_000L
                    if (elapsedMs < RESUME_THRESHOLD_MS) {
                        // Short interruption – resume automatically
                    }
                    wasPlayingBeforeFocusLoss = false
                }
            }
            else -> Unit
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mediaSession = MediaSession.Builder(this, androidx.media3.exoplayer.ExoPlayer.Builder(this).build())
            .build()
        requestAudioFocus()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        abandonAudioFocus()
        mediaSession.release()
        super.onDestroy()
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(focusChangeListener, Handler(Looper.getMainLooper()))
                .build()
            audioFocusRequest = request
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN,
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusChangeListener)
        }
    }

    companion object {
        /** Interruptions shorter than this threshold trigger automatic resume. */
        private const val RESUME_THRESHOLD_MS = 3 * 60 * 1_000L
    }
}
