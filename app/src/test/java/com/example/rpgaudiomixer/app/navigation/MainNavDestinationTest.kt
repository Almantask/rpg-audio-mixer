package com.example.rpgaudiomixer.app.navigation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MainNavDestinationTest {

    @Test
    fun `entries contain exactly the four bottom nav tabs`() {
        // Arrange
        val expectedTabs = listOf("HOME", "CAMPAIGNS", "SCENES", "LIBRARY")

        // Act
        val actualNames = MainNavDestination.entries.map { it.name }

        // Assert
        assertThat(actualNames).containsExactlyElementsOf(expectedTabs)
    }

    @Test
    fun `SETTINGS is not a valid destination`() {
        // Arrange
        val allNames = MainNavDestination.entries.map { it.name }

        // Act & Assert
        assertThat(allNames).doesNotContain("SETTINGS")
    }

    @Test
    fun `CREDITS_ROUTE is a string constant for the credits screen`() {
        // Arrange & Act
        val route = MainNavDestination.CREDITS_ROUTE

        // Assert
        assertThat(route).isEqualTo("credits")
    }

    @Test
    fun `entries count is exactly four`() {
        // Arrange & Act
        val count = MainNavDestination.entries.size

        // Assert
        assertThat(count).isEqualTo(4)
    }

    @Test
    fun `CREDITS_ROUTE does not collide with any enum name`() {
        // Arrange
        val enumNames = MainNavDestination.entries.map { it.name }

        // Act & Assert
        assertThat(enumNames).doesNotContain(MainNavDestination.CREDITS_ROUTE)
    }
}
