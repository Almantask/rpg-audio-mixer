package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TrashRepositoryImplTest {

    private val campaignDao: CampaignDao = mockk()
    private val sessionDao: SessionDao = mockk()
    private val sceneDao: SceneDao = mockk()
    private val soundscapeCategoryDao: SoundscapeCategoryDao = mockk()
    private val fxTrackDao: FxTrackDao = mockk()
    private val repository = TrashRepositoryImpl(
        campaignDao = campaignDao,
        sessionDao = sessionDao,
        sceneDao = sceneDao,
        soundscapeCategoryDao = soundscapeCategoryDao,
        fxTrackDao = fxTrackDao,
    )

    @Test
    fun observeDeletedItems_combines_and_sorts_all_soft_deleted_items() = runTest {
        // Arrange
        every { campaignDao.observeDeleted() } returns flowOf(
            listOf(CampaignEntity(id = 1L, name = "Old Campaign", coverArtUri = null, lastPlayedAt = 0L, deletedAt = 100L)),
        )
        every { sessionDao.observeDeleted() } returns flowOf(
            listOf(
                SessionEntity(
                    id = 2L,
                    campaignId = 1L,
                    name = "Session 1",
                    dateMillis = 200L,
                    coverArtUri = null,
                    lastOpenedSceneId = null,
                    lastSceneOpenedAt = null,
                    deletedAt = 300L,
                ),
            ),
        )
        every { sceneDao.observeDeleted() } returns flowOf(
            listOf(SceneEntity(id = 3L, name = "Cursed Catacombs", description = null, tags = "", masterVolume = 1f, deletedAt = 200L)),
        )
        every { soundscapeCategoryDao.observeDeleted() } returns flowOf(
            listOf(SoundscapeCategoryEntity(id = 4L, name = "Winter's Breath", iconResId = null, themeLabel = null, isDemoContent = false, deletedAt = 400L)),
        )
        every { fxTrackDao.observeDeleted() } returns flowOf(
            listOf(FxTrackEntity(id = 5L, name = "Dragon Roar", filePath = "content://dragon", tags = "", durationMs = 1_000L, playCount = 0, isDemoContent = false, deletedAt = 250L)),
        )

        // Act
        val result = repository.observeDeletedItems().first()

        // Assert
        assertThat(result).containsExactly(
            TrashItem(id = 4L, title = "Winter's Breath", type = TrashItemType.SOUNDSCAPE, deletedAt = 400L),
            TrashItem(id = 2L, title = "Session 1", type = TrashItemType.SESSION, deletedAt = 300L),
            TrashItem(id = 5L, title = "Dragon Roar", type = TrashItemType.FX, deletedAt = 250L),
            TrashItem(id = 3L, title = "Cursed Catacombs", type = TrashItemType.SCENE, deletedAt = 200L),
            TrashItem(id = 1L, title = "Old Campaign", type = TrashItemType.CAMPAIGN, deletedAt = 100L),
        )
    }

    @Test
    fun restoreItem_restores_the_requested_soft_deleted_item() = runTest {
        // Arrange
        coEvery { sceneDao.restore(3L) } returns Unit

        // Act
        repository.restoreItem(itemId = 3L, itemType = TrashItemType.SCENE)

        // Assert
        coVerify(exactly = 1) { sceneDao.restore(3L) }
    }

    @Test
    fun emptyVault_permanently_deletes_all_soft_deleted_items() = runTest {
        // Arrange
        coEvery { campaignDao.deleteAllDeleted() } returns Unit
        coEvery { sessionDao.deleteAllDeleted() } returns Unit
        coEvery { sceneDao.deleteAllDeleted() } returns Unit
        coEvery { soundscapeCategoryDao.deleteAllDeleted() } returns Unit
        coEvery { fxTrackDao.deleteAllDeleted() } returns Unit

        // Act
        repository.emptyVault()

        // Assert
        coVerify(exactly = 1) { campaignDao.deleteAllDeleted() }
        coVerify(exactly = 1) { sessionDao.deleteAllDeleted() }
        coVerify(exactly = 1) { sceneDao.deleteAllDeleted() }
        coVerify(exactly = 1) { soundscapeCategoryDao.deleteAllDeleted() }
        coVerify(exactly = 1) { fxTrackDao.deleteAllDeleted() }
    }
}
