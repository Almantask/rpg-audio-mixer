package com.example.rpgaudiomixer.app.audio

/**
 * Factory for creating [AudioPlayerControl] instances.
 *
 * Production implementations wrap ExoPlayer; tests inject a mock factory.
 */
interface AudioPlayerFactory {

    /** Creates a looping player for the given track file path. */
    fun createLoopingPlayer(trackPath: String): AudioPlayerControl

    /** Creates a one-shot player for the given track file path. */
    fun createOneShotPlayer(trackPath: String): AudioPlayerControl
}
