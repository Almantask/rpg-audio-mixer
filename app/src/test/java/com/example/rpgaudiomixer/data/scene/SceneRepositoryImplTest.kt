package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneRepositoryImplTest {

    private val sceneDao: SceneDao = mockk()
    private val repository = SceneRepositoryImpl(sceneDao)

    @Test
    fun observeScenes_maps_entities_to_domain_models_with_split_tags() = runTest {
        // Arrange
        every { sceneDao.observeAll() } returns flowOf(
            listOf(
                SceneEntity(
                    id = 7L,
                    name = "Tavern",
                    description = "Warm and noisy",
                    tags = "social,indoors",
                ),
            ),
        )

        // Act
        val scenes = repository.observeScenes().first()

        // Assert
        assertThat(scenes).containsExactly(
            com.example.rpgaudiomixer.domain.model.Scene(
                id = 7L,
                name = "Tavern",
                description = "Warm and noisy",
                tags = listOf("social", "indoors"),
            ),
        )
    }

    @Test
    fun createScene_persists_trimmed_name_and_joined_tags() = runTest {
        // Arrange
        coEvery { sceneDao.upsert(any()) } returns 11L

        // Act
        repository.createScene(
            name = "  Forest Edge  ",
            description = "Moonlit path",
            tags = listOf("outdoors", "night"),
        )

        // Assert
        coVerify(exactly = 1) {
            sceneDao.upsert(
                SceneEntity(
                    id = 0L,
                    name = "Forest Edge",
                    description = "Moonlit path",
                    tags = "outdoors,night",
                ),
            )
        }
    }
}
