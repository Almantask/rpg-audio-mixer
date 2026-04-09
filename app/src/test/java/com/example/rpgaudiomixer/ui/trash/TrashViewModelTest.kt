package com.example.rpgaudiomixer.ui.trash

import com.example.rpgaudiomixer.domain.trash.DeletedItem
import com.example.rpgaudiomixer.domain.trash.DeletedItemType
import com.example.rpgaudiomixer.domain.trash.TrashRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TrashViewModelTest {

    @Test
    fun init_exposes_deleted_items_sorted_by_most_recently_deleted_first() = runTest {
        // Arrange
        val repository = FakeTrashRepository().apply {
            deletedItemsFlow.value = listOf(
                deletedItem(id = 1L, name = "Dragon Roar", type = DeletedItemType.FX, deletedAt = 100L),
                deletedItem(id = 2L, name = "Winter's Breath", type = DeletedItemType.SOUNDSCAPE, deletedAt = 300L),
                deletedItem(id = 3L, name = "Cursed Catacombs", type = DeletedItemType.SCENE, deletedAt = 200L),
            )
        }

        // Act
        val viewModel = TrashViewModel(
            trashRepository = repository,
            currentTimeProvider = { 1_000L },
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.items.map(TrashItemUiState::name)).containsExactly(
            "Winter's Breath",
            "Cursed Catacombs",
            "Dragon Roar",
        )
        assertThat(repository.lastPurgedBefore).isEqualTo(1_000L - SEVEN_DAYS_IN_MILLIS)
    }

    @Test
    fun restoreItem_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeTrashRepository()
        val viewModel = TrashViewModel(
            trashRepository = repository,
            currentTimeProvider = { 1_000L },
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        val item = deletedItem(id = 3L, name = "Cursed Catacombs", type = DeletedItemType.SCENE, deletedAt = 200L)

        // Act
        viewModel.restoreItem(item.id, item.type)
        advanceUntilIdle()

        // Assert
        assertThat(repository.restoredItems).containsExactly(DeletedItemType.SCENE to 3L)
    }

    @Test
    fun permanentlyDeleteItem_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeTrashRepository()
        val viewModel = TrashViewModel(
            trashRepository = repository,
            currentTimeProvider = { 1_000L },
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.permanentlyDeleteItem(7L, DeletedItemType.FX)
        advanceUntilIdle()

        // Assert
        assertThat(repository.permanentlyDeletedItems).containsExactly(DeletedItemType.FX to 7L)
    }

    @Test
    fun emptyVault_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeTrashRepository()
        val viewModel = TrashViewModel(
            trashRepository = repository,
            currentTimeProvider = { 1_000L },
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.emptyVault()
        advanceUntilIdle()

        // Assert
        assertThat(repository.emptyVaultInvocationCount).isEqualTo(1)
    }

    private class FakeTrashRepository : TrashRepository {
        val deletedItemsFlow = MutableStateFlow<List<DeletedItem>>(emptyList())
        val restoredItems = mutableListOf<Pair<DeletedItemType, Long>>()
        val permanentlyDeletedItems = mutableListOf<Pair<DeletedItemType, Long>>()
        var emptyVaultInvocationCount = 0
        var lastPurgedBefore: Long? = null

        override fun observeDeletedItems(): Flow<List<DeletedItem>> = deletedItemsFlow

        override suspend fun restoreItem(itemId: Long, type: DeletedItemType) {
            restoredItems += type to itemId
        }

        override suspend fun permanentlyDeleteItem(itemId: Long, type: DeletedItemType) {
            permanentlyDeletedItems += type to itemId
        }

        override suspend fun emptyVault() {
            emptyVaultInvocationCount += 1
        }

        override suspend fun purgeItemsDeletedBefore(cutoffTimeMillis: Long) {
            lastPurgedBefore = cutoffTimeMillis
        }
    }

    private fun deletedItem(
        id: Long,
        name: String,
        type: DeletedItemType,
        deletedAt: Long,
    ) = DeletedItem(
        id = id,
        name = name,
        type = type,
        deletedAt = deletedAt,
    )

    private companion object {
        const val SEVEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L
    }
}
