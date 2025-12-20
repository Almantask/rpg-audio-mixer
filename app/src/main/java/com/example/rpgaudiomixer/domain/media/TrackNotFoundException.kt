package com.example.rpgaudiomixer.domain.media

/**
 * Thrown when a [TrackPlayer] is asked to play a track that can't be resolved.
 */
class TrackNotFoundException(
    message: String,
    cause: Throwable? = null,
) : IllegalArgumentException(message, cause)

