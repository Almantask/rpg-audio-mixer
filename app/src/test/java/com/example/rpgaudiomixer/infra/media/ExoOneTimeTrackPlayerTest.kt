package com.example.rpgaudiomixer.infra.media

import org.junit.jupiter.api.Test

/**
 * Unit test placeholder for ExoOneTimeTrackPlayer.
 *
 * ExoOneTimeTrackPlayer requires Android Context and ExoPlayer, making it unsuitable for
 * local JVM unit tests. Instead, see the Android instrumentation test:
 * androidTest/java/.../ExoOneTimeTrackPlayerInstrumentedTest.kt
 *
 * The instrumentation test verifies:
 * - URI resolution (full URIs, content URIs, raw resources)
 * - TrackNotFoundException for invalid resources
 * - Player instantiation without crashes
 * - Multiple instance creation (overlap support)
 *
 * Full playback behavior testing would require additional async handling and
 * audio focus management, which is deferred until the implementation is stabilized
 * (currently marked with TODOs about lifecycle management).
 */
class ExoOneTimeTrackPlayerTest {

    @Test
    fun see_instrumentation_test_for_android_dependent_tests() {
        // This class serves as documentation that ExoOneTimeTrackPlayer is tested
        // via Android instrumentation tests rather than local JVM unit tests.
        // See: androidTest/java/.../ExoOneTimeTrackPlayerInstrumentedTest.kt
    }
}