package com.example.rpgaudiomixer.ui.trash

import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import com.example.rpgaudiomixer.domain.trash.TrashRepository
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class TrashViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun restoreItem_removes_the_restored_item_from_the_success_state() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeTrashRepository(
            items = listOf(
                TrashItem(id = 3L, title = "Cursed Catacombs", type = TrashItemType.SCENE, deletedAt = 200L),
            ),
        )
        val viewModel = TrashViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.restoreItem(itemId = 3L, itemType = TrashItemType.SCENE)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as TrashUiState.Success
        assertThat(state.items).isEmpty()
    }

    @Test
    fun emptyVault_clears_all_deleted_items() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeTrashRepository(
            items = listOf(
                TrashItem(id = 1L, title = "Old Campaign", type = TrashItemType.CAMPAIGN, deletedAt = 100L),
                TrashItem(id = 2L, title = "Dragon Roar", type = TrashItemType.FX, deletedAt = 200L),
            ),
        )
        val viewModel = TrashViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.emptyVault()
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as TrashUiState.Success
        assertThat(state.items).isEmpty()
    }

    private class FakeTrashRepository(
        items: List<TrashItem>,
    ) : TrashRepository {
        private val itemsFlow = MutableStateFlow(items)

        override fun observeDeletedItems(): Flow<List<TrashItem>> = itemsFlow

        override suspend fun restoreItem(itemId: Long, itemType: TrashItemType) {
            itemsFlow.value = itemsFlow.value.filterNot { it.id == itemId && it.type == itemType }
        }

        override suspend fun permanentlyDeleteItem(itemId: Long, itemType: TrashItemType) {
            itemsFlow.value = itemsFlow.value.filterNot { it.id == itemId && it.type == itemType }
        }

        override suspend fun emptyVault() {
            itemsFlow.value = emptyList()
        }

        override suspend fun purgeExpiredItems(currentTimeMillis: Long) = Unit
    }
}
