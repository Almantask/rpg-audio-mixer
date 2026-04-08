package com.example.rpgaudiomixer.app.navigation

data class AppChromeState(
    val title: String,
    val showBackArrow: Boolean,
    val showBottomBar: Boolean,
)

object AppChromeStateResolver {
    private val defaultState = AppChromeState(
        title = MainNavDestination.HOME.title,
        showBackArrow = false,
        showBottomBar = true,
    )

    fun resolve(route: String?): AppChromeState {
        val mainDestination = MainNavDestination.fromRoute(route)
        if (mainDestination != null) {
            return AppChromeState(
                title = mainDestination.title,
                showBackArrow = false,
                showBottomBar = true,
            )
        }

        return when (route) {
            AppRoute.SOUNDSCAPE_LIBRARY -> AppChromeState(
                title = "Library",
                showBackArrow = false,
                showBottomBar = true,
            )

            AppRoute.CAMPAIGN_SESSIONS -> AppChromeState(
                title = "Sessions",
                showBackArrow = true,
                showBottomBar = false,
            )

            AppRoute.SESSION_SCENES -> AppChromeState(
                title = "Session Scenes",
                showBackArrow = true,
                showBottomBar = false,
            )

            AppRoute.SCENE_DETAILS -> AppChromeState(
                title = "Active Scene",
                showBackArrow = true,
                showBottomBar = false,
            )

            AppRoute.SOUNDSCAPE_CATEGORY_COMPOSER -> AppChromeState(
                title = "Soundscape Composer",
                showBackArrow = true,
                showBottomBar = false,
            )

            AppRoute.TRASH -> AppChromeState(
                title = "Recent Deletes",
                showBackArrow = true,
                showBottomBar = false,
            )

            AppRoute.CREDITS -> AppChromeState(
                title = "Behind the Screen",
                showBackArrow = true,
                showBottomBar = false,
            )

            else -> defaultState
        }
    }
}
