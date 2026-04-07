package com.example.rpgaudiomixer.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SessionTest {

    @Test
    fun `Session with all fields creates correctly`() {
        // Arrange
        val id = 1L
        val campaignId = 10L
        val name = "Session 1: Into the Dungeon"
        val date = 123456789L
        val coverArtUri = "content://cover.jpg"

        // Act
        val session = Session(
            id = id,
            campaignId = campaignId,
            name = name,
            date = date,
            coverArtUri = coverArtUri
        )

        // Assert
        assertThat(session.id).isEqualTo(id)
        assertThat(session.campaignId).isEqualTo(campaignId)
        assertThat(session.name).isEqualTo(name)
        assertThat(session.date).isEqualTo(date)
        assertThat(session.coverArtUri).isEqualTo(coverArtUri)
    }

    @Test
    fun `Session with null coverArtUri creates correctly`() {
        // Arrange
        val campaignId = 5L
        val name = "Session 2"

        // Act
        val session = Session(campaignId = campaignId, name = name, coverArtUri = null)

        // Assert
        assertThat(session.campaignId).isEqualTo(campaignId)
        assertThat(session.name).isEqualTo(name)
        assertThat(session.coverArtUri).isNull()
    }

    @Test
    fun `Session uses default id of 0`() {
        // Arrange & Act
        val session = Session(campaignId = 1L, name = "Test Session")

        // Assert
        assertThat(session.id).isEqualTo(0L)
    }

    @Test
    fun `Session uses default date timestamp`() {
        // Arrange
        val beforeCreation = System.currentTimeMillis()

        // Act
        val session = Session(campaignId = 1L, name = "Test Session")

        // Assert
        val afterCreation = System.currentTimeMillis()
        assertThat(session.date).isBetween(beforeCreation, afterCreation)
    }

    @Test
    fun `two sessions with same data are equal`() {
        // Arrange
        val session1 = Session(
            id = 1L,
            campaignId = 10L,
            name = "Test",
            date = 1000L,
            coverArtUri = "uri"
        )
        val session2 = Session(
            id = 1L,
            campaignId = 10L,
            name = "Test",
            date = 1000L,
            coverArtUri = "uri"
        )

        // Act & Assert
        assertThat(session1).isEqualTo(session2)
    }

    @Test
    fun `two sessions with different data are not equal`() {
        // Arrange
        val session1 = Session(id = 1L, campaignId = 1L, name = "Session 1")
        val session2 = Session(id = 2L, campaignId = 1L, name = "Session 2")

        // Act & Assert
        assertThat(session1).isNotEqualTo(session2)
    }
}
