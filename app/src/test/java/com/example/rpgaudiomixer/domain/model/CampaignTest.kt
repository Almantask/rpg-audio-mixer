package com.example.rpgaudiomixer.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignTest {

    @Test
    fun `Campaign with all fields creates correctly`() {
        // Arrange
        val id = 1L
        val name = "Dragon Heist"
        val coverArtUri = "content://cover.jpg"
        val lastPlayedAt = 123456789L

        // Act
        val campaign = Campaign(
            id = id,
            name = name,
            coverArtUri = coverArtUri,
            lastPlayedAt = lastPlayedAt
        )

        // Assert
        assertThat(campaign.id).isEqualTo(id)
        assertThat(campaign.name).isEqualTo(name)
        assertThat(campaign.coverArtUri).isEqualTo(coverArtUri)
        assertThat(campaign.lastPlayedAt).isEqualTo(lastPlayedAt)
    }

    @Test
    fun `Campaign with null coverArtUri creates correctly`() {
        // Arrange
        val name = "Curse of Strahd"

        // Act
        val campaign = Campaign(name = name, coverArtUri = null)

        // Assert
        assertThat(campaign.name).isEqualTo(name)
        assertThat(campaign.coverArtUri).isNull()
    }

    @Test
    fun `Campaign uses default id of 0`() {
        // Arrange & Act
        val campaign = Campaign(name = "Test Campaign")

        // Assert
        assertThat(campaign.id).isEqualTo(0L)
    }

    @Test
    fun `Campaign uses default lastPlayedAt timestamp`() {
        // Arrange
        val beforeCreation = System.currentTimeMillis()

        // Act
        val campaign = Campaign(name = "Test Campaign")

        // Assert
        val afterCreation = System.currentTimeMillis()
        assertThat(campaign.lastPlayedAt).isBetween(beforeCreation, afterCreation)
    }

    @Test
    fun `two campaigns with same data are equal`() {
        // Arrange
        val campaign1 = Campaign(
            id = 1L,
            name = "Test",
            coverArtUri = "uri",
            lastPlayedAt = 1000L
        )
        val campaign2 = Campaign(
            id = 1L,
            name = "Test",
            coverArtUri = "uri",
            lastPlayedAt = 1000L
        )

        // Act & Assert
        assertThat(campaign1).isEqualTo(campaign2)
    }

    @Test
    fun `two campaigns with different data are not equal`() {
        // Arrange
        val campaign1 = Campaign(id = 1L, name = "Campaign 1")
        val campaign2 = Campaign(id = 2L, name = "Campaign 2")

        // Act & Assert
        assertThat(campaign1).isNotEqualTo(campaign2)
    }
}
