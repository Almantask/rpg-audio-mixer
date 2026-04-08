package com.example.rpgaudiomixer.app.navigation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AppChromeStateResolverTest {

    @Test
    fun resolve_returns_a_non_back_navigation_state_for_main_tabs() {
        // Arrange
        val route = MainNavDestination.HOME.route

        // Act
        val result = AppChromeStateResolver.resolve(route)

        // Assert
        assertThat(result).isEqualTo(
            AppChromeState(
                title = "Home",
                showBackArrow = false,
                showBottomBar = true,
            )
        )
    }

    @Test
    fun resolve_returns_credits_state_for_the_credits_route() {
        // Arrange
        val route = AppRoute.CREDITS

        // Act
        val result = AppChromeStateResolver.resolve(route)

        // Assert
        assertThat(result).isEqualTo(
            AppChromeState(
                title = "Behind the Screen",
                showBackArrow = true,
                showBottomBar = false,
            )
        )
    }

    @Test
    fun resolve_returns_trash_state_for_the_trash_route() {
        // Arrange
        val route = AppRoute.TRASH

        // Act
        val result = AppChromeStateResolver.resolve(route)

        // Assert
        assertThat(result).isEqualTo(
            AppChromeState(
                title = "Recent Deletes",
                showBackArrow = true,
                showBottomBar = false,
            )
        )
    }
}
