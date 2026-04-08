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

    @Test
    fun resolve_returns_session_navigation_chrome_for_campaign_session_routes() {
        // Arrange
        val route = AppRoute.CAMPAIGN_SESSIONS

        // Act
        val result = AppChromeStateResolver.resolve(route)

        // Assert
        assertThat(result).isEqualTo(
            AppChromeState(
                title = "Sessions",
                showBackArrow = true,
                showBottomBar = false,
            )
        )
    }

    @Test
    fun resolve_returns_scene_navigation_chrome_for_session_scene_routes() {
        // Arrange
        val route = AppRoute.SESSION_SCENES

        // Act
        val result = AppChromeStateResolver.resolve(route)

        // Assert
        assertThat(result).isEqualTo(
            AppChromeState(
                title = "Session Scenes",
                showBackArrow = true,
                showBottomBar = false,
            )
        )
    }

    @Test
    fun resolve_returns_scene_detail_chrome_for_active_scene_routes() {
        // Arrange
        val route = AppRoute.SCENE_DETAILS

        // Act
        val result = AppChromeStateResolver.resolve(route)

        // Assert
        assertThat(result).isEqualTo(
            AppChromeState(
                title = "Active Scene",
                showBackArrow = true,
                showBottomBar = false,
            )
        )
    }
}
