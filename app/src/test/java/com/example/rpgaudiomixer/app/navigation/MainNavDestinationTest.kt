package com.example.rpgaudiomixer.app.navigation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MainNavDestinationTest {

    @Test
    fun mainTabs_exposes_the_four_primary_destinations_in_design_order() {
        // Arrange
        val expectedTabs = listOf(
            MainNavDestination.HOME,
            MainNavDestination.CAMPAIGNS,
            MainNavDestination.SCENES,
            MainNavDestination.LIBRARY,
        )

        // Act
        val result = MainNavDestination.mainTabs

        // Assert
        assertThat(result).containsExactlyElementsOf(expectedTabs)
    }

    @Test
    fun each_destination_exposes_the_expected_route_label_and_title() {
        // Arrange
        val destination = MainNavDestination.CAMPAIGNS

        // Act
        val route = destination.route
        val label = destination.label
        val title = destination.title

        // Assert
        assertThat(route).isEqualTo("campaigns")
        assertThat(label).isEqualTo("CAMPAIGNS")
        assertThat(title).isEqualTo("Campaigns")
    }

    @Test
    fun fromRoute_returns_the_matching_destination_for_a_main_route() {
        // Arrange
        val route = "library"

        // Act
        val result = MainNavDestination.fromRoute(route)

        // Assert
        assertThat(result).isEqualTo(MainNavDestination.LIBRARY)
    }

    @Test
    fun fromRoute_returns_null_for_unknown_routes() {
        // Arrange
        val route = "credits"

        // Act
        val result = MainNavDestination.fromRoute(route)

        // Assert
        assertThat(result).isNull()
    }
}
