package com.example.rpgaudiomixer.app.settings

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SettingsSyncPolicyTest {

    private val policy = SettingsSyncPolicy()

    @Test
    fun isSyncAvailable_returns_false_when_the_last_sync_was_less_than_24_hours_ago() {
        // Arrange
        val lastSyncedAtMillis = 1_000L
        val currentTimeMillis = lastSyncedAtMillis + SettingsSyncPolicy.COOLDOWN_MILLIS - 1L

        // Act
        val result = policy.isSyncAvailable(
            currentTimeMillis = currentTimeMillis,
            lastSyncedAtMillis = lastSyncedAtMillis,
        )

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun isSyncAvailable_returns_true_when_the_last_sync_was_24_hours_ago_or_more() {
        // Arrange
        val lastSyncedAtMillis = 1_000L
        val currentTimeMillis = lastSyncedAtMillis + SettingsSyncPolicy.COOLDOWN_MILLIS

        // Act
        val result = policy.isSyncAvailable(
            currentTimeMillis = currentTimeMillis,
            lastSyncedAtMillis = lastSyncedAtMillis,
        )

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun isSyncAvailable_returns_true_when_tracks_have_never_been_synced() {
        // Arrange
        val currentTimeMillis = 10_000L

        // Act
        val result = policy.isSyncAvailable(
            currentTimeMillis = currentTimeMillis,
            lastSyncedAtMillis = null,
        )

        // Assert
        assertThat(result).isTrue()
    }
}
