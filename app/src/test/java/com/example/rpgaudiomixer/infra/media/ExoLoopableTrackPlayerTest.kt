package com.example.rpgaudiomixer.infra.media

import org.junit.jupiter.api.Test

/**
 * Unit test placeholder for ExoLoopableTrackPlayer.
 *
 * ExoLoopableTrackPlayer requires Android Context and ExoPlayer with REPEAT_MODE_ONE,
 * making it unsuitable for local JVM unit tests. Instead, see the Android instrumentation test:
 * androidTest/java/.../ExoLoopableTrackPlayerInstrumentedTest.kt
 *
 * The instrumentation test verifies:
 * - URI resolution (full URIs, content URIs, raw resources)
 * - TrackNotFoundException for invalid resources
 * - Player instantiation with loop mode
 * - Multiple instance creation for simultaneous loops
 *
 * Full playback behavior testing (looping verification, volume control, lifecycle)
 * would require additional async handling, which is deferred until the implementation
 * is stabilized (currently marked with TODOs about lifecycle management).
 */
class ExoLoopableTrackPlayerTest {

    @Test
    fun see_instrumentation_test_for_android_dependent_tests() {
        // This class serves as documentation that ExoLoopableTrackPlayer is tested
        // via Android instrumentation tests rather than local JVM unit tests.
        // See: androidTest/java/.../ExoLoopableTrackPlayerInstrumentedTest.kt
    }
}
