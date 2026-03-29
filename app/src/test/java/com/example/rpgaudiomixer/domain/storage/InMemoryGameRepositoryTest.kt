package com.example.rpgaudiomixer.domain.storage

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.infra.storage.InMemoryGameRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InMemoryGameRepositoryTest {

    private lateinit var repository: InMemoryGameRepository

    @BeforeEach
    fun setUp() {
        repository = InMemoryGameRepository()
    }

    @Test
    fun `getActiveCampaign returns campaign with latest lastPlayedAt`() {
        // Arrange
        val c1 = Campaign(id = "c1", name = "Adventure 1", lastPlayedAt = 1000L)
        val c2 = Campaign(id = "c2", name = "Adventure 2", lastPlayedAt = 2000L)
        repository.addCampaign(c1)
        repository.addCampaign(c2)

        // Act
        val active = repository.getActiveCampaign()

        // Assert
        assertThat(active).isNotNull
        assertThat(active?.id).isEqualTo("c2")
    }

    @Test
    fun `getAllCampaigns sorts by lastPlayedAt descending`() {
        // Arrange
        repository.addCampaign(Campaign(id = "c1", name = "A", lastPlayedAt = 100L))
        repository.addCampaign(Campaign(id = "c2", name = "B", lastPlayedAt = 500L))
        repository.addCampaign(Campaign(id = "c3", name = "C", lastPlayedAt = 200L))

        // Act
        val campaigns = repository.getAllCampaigns()

        // Assert
        assertThat(campaigns.map { it.id }).containsExactly("c2", "c3", "c1")
    }

    @Test
    fun `getAllScenes returns all scenes`() {
        // Arrange
        val sceneA = Scene(id = "s1", name = "Forest")
        val sceneB = Scene(id = "s2", name = "Dungeon")
        repository.addScene(sceneA)
        repository.addScene(sceneB)

        // Act
        val scenes = repository.getAllScenes()

        // Assert
        assertThat(scenes).containsExactlyInAnyOrder(sceneA, sceneB)
    }
}
