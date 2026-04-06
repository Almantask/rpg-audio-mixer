package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.domain.model.Campaign
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignMapperTest {

    @Test
    fun `toDomain maps entity fields correctly`() {
        // Arrange
        val entity = CampaignEntity(
            id = 1L,
            name = "The Lost Mines",
            coverArtUri = "content://some/image.jpg",
            lastPlayedAt = 1234567890L,
        )

        // Act
        val domain = entity.toDomain()

        // Assert
        assertThat(domain.id).isEqualTo(1L)
        assertThat(domain.name).isEqualTo("The Lost Mines")
        assertThat(domain.coverArtUri).isEqualTo("content://some/image.jpg")
        assertThat(domain.lastPlayedAt).isEqualTo(1234567890L)
    }

    @Test
    fun `toDomain maps null coverArtUri correctly`() {
        // Arrange
        val entity = CampaignEntity(id = 2L, name = "Curse of Strahd", coverArtUri = null)

        // Act
        val domain = entity.toDomain()

        // Assert
        assertThat(domain.coverArtUri).isNull()
    }

    @Test
    fun `toEntity maps domain fields correctly`() {
        // Arrange
        val campaign = Campaign(
            id = 5L,
            name = "Dragon Heist",
            coverArtUri = null,
            lastPlayedAt = 9999L,
        )

        // Act
        val entity = campaign.toEntity()

        // Assert
        assertThat(entity.id).isEqualTo(5L)
        assertThat(entity.name).isEqualTo("Dragon Heist")
        assertThat(entity.coverArtUri).isNull()
        assertThat(entity.lastPlayedAt).isEqualTo(9999L)
    }
}
