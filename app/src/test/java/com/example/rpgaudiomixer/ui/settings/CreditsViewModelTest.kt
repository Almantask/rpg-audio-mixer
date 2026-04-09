package com.example.rpgaudiomixer.ui.settings

import com.example.rpgaudiomixer.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreditsViewModelTest {

    @Test
    fun init_enables_sync_when_no_successful_sync_has_been_recorded() = runTest {
        // Arrange
        val repository = FakeSettingsRepository()

        // Act
        val viewModel = CreditsViewModel(
            settingsRepository = repository,
            currentTimeProvider = { 1_000L },
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.isSyncEnabled).isTrue()
    }

    @Test
    fun init_disables_sync_when_the_last_successful_sync_was_less_than_24_hours_ago() = runTest {
        // Arrange
        val repository = FakeSettingsRepository().apply {
            lastSuccessfulSyncAtFlow.value = 10_000L
        }

        // Act
        val viewModel = CreditsViewModel(
            settingsRepository = repository,
            currentTimeProvider = { 10_000L + ONE_HOUR_IN_MILLIS },
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.isSyncEnabled).isFalse()
    }

    @Test
    fun init_enables_sync_when_the_last_successful_sync_was_more_than_24_hours_ago() = runTest {
        // Arrange
        val repository = FakeSettingsRepository().apply {
            lastSuccessfulSyncAtFlow.value = 10_000L
        }

        // Act
        val viewModel = CreditsViewModel(
            settingsRepository = repository,
            currentTimeProvider = { 10_000L + ONE_DAY_IN_MILLIS + 1L },
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.isSyncEnabled).isTrue()
    }

    @Test
    fun syncPurchasesAndFreeTracks_delegates_to_the_repository_and_disables_the_button_after_success() = runTest {
        // Arrange
        val repository = FakeSettingsRepository()
        val now = 50_000L
        val viewModel = CreditsViewModel(
            settingsRepository = repository,
            currentTimeProvider = { now },
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.syncPurchasesAndFreeTracks()
        advanceUntilIdle()

        // Assert
        assertThat(repository.syncInvocationCount).isEqualTo(1)
        assertThat(viewModel.uiState.value.isSyncEnabled).isFalse()
    }

    private class FakeSettingsRepository : SettingsRepository {
        val lastSuccessfulSyncAtFlow = MutableStateFlow<Long?>(null)
        var syncInvocationCount = 0

        override fun observeLastSuccessfulSyncAt(): Flow<Long?> = lastSuccessfulSyncAtFlow

        override suspend fun syncPurchasesAndFreeTracks() {
            syncInvocationCount += 1
            lastSuccessfulSyncAtFlow.value = 50_000L
        }
    }

    private companion object {
        const val ONE_HOUR_IN_MILLIS = 60 * 60 * 1000L
        const val ONE_DAY_IN_MILLIS = 24 * ONE_HOUR_IN_MILLIS
    }
}
