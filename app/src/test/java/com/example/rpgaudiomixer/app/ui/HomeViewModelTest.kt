package com.example.rpgaudiomixer.app.ui

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SoundEffect
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.infra.storage.InMemoryGameRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HomeViewModelTest {

    private lateinit var repository: InMemoryGameRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setUp() {
        repository = InMemoryGameRepository()
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `uiState resolves active and top items`() {
        // Arrange
        repository.addCampaign(Campaign(name = "Alpha", lastPlayedAt = 50L))
        repository.addCampaign(Campaign(name = "Beta", lastPlayedAt = 100L))
        repository.addScene(Scene(name = "Forest", lastPlayedAt = 1000L))
        repository.addScene(Scene(name = "Dungeon", lastPlayedAt = 900L))
        repository.addSoundscapeCategory(SoundscapeCategory(name = "Storm", playCount = 20))
        repository.addSoundscapeCategory(SoundscapeCategory(name = "Tavern", playCount = 10))
        repository.addSoundEffect(SoundEffect(name = "Sword", trackId = "sword", playCount = 15))
        repository.addSoundEffect(SoundEffect(name = "Roar", trackId = "roar", playCount = 25))

        // Act
        val state = viewModel.uiState

        // Assert
        assertThat(state.activeCampaign?.name).isEqualTo("Beta")
        assertThat(state.resumeScene?.name).isEqualTo("Forest")
        assertThat(state.topAtmosphere?.name).isEqualTo("Storm")
        assertThat(state.legendaryAction?.name).isEqualTo("Roar")
    }
}
