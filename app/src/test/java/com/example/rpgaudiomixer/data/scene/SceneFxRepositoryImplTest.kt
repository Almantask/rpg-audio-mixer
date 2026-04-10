package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.data.scene.local.SceneFxDao
import com.example.rpgaudiomixer.data.scene.local.SceneFxRow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneFxRepositoryImplTest {

    private val sceneFxDao: SceneFxDao = mockk()
    private val fxTrackDao: FxTrackDao = mockk()
    private val repository = SceneFxRepositoryImpl(
        sceneFxDao = sceneFxDao,
        fxTrackDao = fxTrackDao,
    )

    @Test
    fun observeSceneFx_maps_join_rows_to_domain_models() = runTest {
        // Arrange
        every { sceneFxDao.observeFxByScene(9L) } returns flowOf(
            listOf(
                SceneFxRow(
                    sceneId = 9L,
                    fxTrackId = 3L,
                    name = "Thunder Crack",
                    filePath = "content://thunder",
                    tags = "storm,combat",
                    durationMs = 1_200L,
                    playCount = 2,
                    isDemoContent = false,
                    displayOrder = 0,
                ),
            ),
        )

        // Act
        val sceneFx = repository.observeSceneFx(9L).first()

        // Assert
        assertThat(sceneFx).containsExactly(
            com.example.rpgaudiomixer.domain.model.SceneFx(
                sceneId = 9L,
                fxTrackId = 3L,
                name = "Thunder Crack",
                filePath = "content://thunder",
                tags = listOf("storm", "combat"),
                durationMs = 1_200L,
                playCount = 2,
                isDemoContent = false,
                displayOrder = 0,
            ),
        )
    }

    @Test
    fun addFxToScene_uses_the_next_display_order() = runTest {
        // Arrange
        coEvery { sceneFxDao.nextDisplayOrder(4L) } returns 2
        coEvery { sceneFxDao.insert(any()) } returns Unit

        // Act
        repository.addFxToScene(sceneId = 4L, fxTrackId = 8L)

        // Assert
        coVerify(exactly = 1) {
            sceneFxDao.insert(
                match { crossRef ->
                    crossRef.sceneId == 4L &&
                        crossRef.fxTrackId == 8L &&
                        crossRef.displayOrder == 2
                },
            )
        }
    }

    @Test
    fun observeAvailableFx_filters_out_already_added_tracks() = runTest {
        // Arrange
        every { sceneFxDao.observeLinkedFxTrackIds(4L) } returns flowOf(listOf(2L))
        every { fxTrackDao.observeAll() } returns flowOf(
            listOf(
                FxTrackEntity(
                    id = 1L,
                    name = "Thunder Crack",
                    filePath = "content://thunder",
                    tags = "storm",
                    durationMs = 1_000L,
                    playCount = 0,
                    isDemoContent = false,
                ),
                FxTrackEntity(
                    id = 2L,
                    name = "Wolf Howl",
                    filePath = "content://wolf",
                    tags = "creature",
                    durationMs = 1_500L,
                    playCount = 0,
                    isDemoContent = false,
                ),
            ),
        )

        // Act
        val availableFx = repository.observeAvailableFx(4L).first()

        // Assert
        assertThat(availableFx.map { it.name }).isEqualTo(listOf("Thunder Crack"))
    }

    @Test
    fun incrementTrackPlayCount_updates_the_selected_fx_track() = runTest {
        // Arrange
        coEvery { fxTrackDao.incrementPlayCount(7L) } returns Unit

        // Act
        repository.incrementTrackPlayCount(trackId = 7L)

        // Assert
        coVerify(exactly = 1) { fxTrackDao.incrementPlayCount(7L) }
    }
}
