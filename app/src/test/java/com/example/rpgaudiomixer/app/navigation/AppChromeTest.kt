package com.example.rpgaudiomixer.app.navigation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AppChromeTest {

    @Test
    fun mainDestinations_exposes_the_four_primary_tabs_in_feature_order() {
        // Arrange
        val tabs = MainNavDestination.mainDestinations

        // Act
        val labels = tabs.map { it.label }

        // Assert
        assertThat(labels).containsExactly("HOME", "CAMPAIGNS", "SCENES", "LIBRARY")
    }

    @Test
    fun fromRoute_returns_a_back_enabled_configuration_for_settings() {
        // Arrange
        val route = AppRoute.Settings.route

        // Act
        val chrome = AppChrome.fromRoute(route)

        // Assert
        assertThat(chrome).isEqualTo(
            AppChrome(
                title = "Behind the Screen",
                showBackArrow = true,
                showBottomBar = false,
            ),
        )
    }

    @Test
    fun fromRoute_returns_scenes_title_for_the_scenes_tab() {
        // Arrange
        val route = MainNavDestination.SCENES.route

        // Act
        val chrome = AppChrome.fromRoute(route)

        // Assert
        assertThat(chrome).isEqualTo(
            AppChrome(
                title = "Scenes",
                showBackArrow = false,
                showBottomBar = true,
            ),
        )
    }
}
