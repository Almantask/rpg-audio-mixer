package com.example.rpgaudiomixer.domain.media

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TrackNotFoundExceptionTest {

    @Test
    fun constructor_with_message_creates_exception_with_message() {
        // Arrange
        val message = "Track 'epic_battle' not found"

        // Act
        val exception = TrackNotFoundException(message)

        // Assert
        assertThat(exception.message).isEqualTo(message)
        assertThat(exception.cause).isNull()
    }

    @Test
    fun constructor_with_message_and_cause_creates_exception_with_both() {
        // Arrange
        val message = "Failed to load track"
        val cause = RuntimeException("IO error")

        // Act
        val exception = TrackNotFoundException(message, cause)

        // Assert
        assertThat(exception.message).isEqualTo(message)
        assertThat(exception.cause).isEqualTo(cause)
    }

    @Test
    fun exception_is_instance_of_IllegalArgumentException() {
        // Arrange
        val exception = TrackNotFoundException("test")

        // Act & Assert
        assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun exception_can_be_thrown_and_caught() {
        // Arrange
        val message = "Track not found in database"

        // Act
        val thrownException = runCatching {
            throw TrackNotFoundException(message)
        }.exceptionOrNull()

        // Assert
        assertThat(thrownException).isNotNull()
        assertThat(thrownException).isInstanceOf(TrackNotFoundException::class.java)
        assertThat(thrownException?.message).isEqualTo(message)
    }

    @Test
    fun exception_preserves_stack_trace() {
        // Arrange & Act
        val exception = try {
            throw TrackNotFoundException("test track")
        } catch (e: TrackNotFoundException) {
            e
        }

        // Assert
        assertThat(exception.stackTrace).isNotEmpty()
        assertThat(exception.stackTrace.first().className)
            .isEqualTo("com.example.rpgaudiomixer.domain.media.TrackNotFoundExceptionTest")
    }
}
