package com.example.rpgaudiomixer.app.navigation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MainNavDestinationTest {

    @Test
    fun `HOME has correct route and label`() {
        // Arrange
        val destination = MainNavDestination.HOME

        // Act & Assert
        assertThat(destination.route).isEqualTo("home")
        assertThat(destination.label).isEqualTo("Home")
    }

    @Test
    fun `CAMPAIGNS has correct route and label`() {
        // Arrange
        val destination = MainNavDestination.CAMPAIGNS

        // Act & Assert
        assertThat(destination.route).isEqualTo("campaigns")
        assertThat(destination.label).isEqualTo("Campaigns")
    }

    @Test
    fun `SCENES has correct route and label`() {
        // Arrange
        val destination = MainNavDestination.SCENES

        // Act & Assert
        assertThat(destination.route).isEqualTo("scenes")
        assertThat(destination.label).isEqualTo("Scenes")
    }

    @Test
    fun `LIBRARY has correct route and label`() {
        // Arrange
        val destination = MainNavDestination.LIBRARY

        // Act & Assert
        assertThat(destination.route).isEqualTo("library")
        assertThat(destination.label).isEqualTo("Library")
    }

    @Test
    fun `all destinations have unique routes`() {
        // Arrange
        val destinations = MainNavDestination.entries

        // Act
        val routes = destinations.map { it.route }

        // Assert
        assertThat(routes).doesNotHaveDuplicates()
    }

    @Test
    fun `all destinations have unique labels`() {
        // Arrange
        val destinations = MainNavDestination.entries

        // Act
        val labels = destinations.map { it.label }

        // Assert
        assertThat(labels).doesNotHaveDuplicates()
    }

    @Test
    fun `enum has exactly 4 destinations`() {
        // Arrange & Act
        val count = MainNavDestination.entries.size

        // Assert
        assertThat(count).isEqualTo(4)
    }
}
