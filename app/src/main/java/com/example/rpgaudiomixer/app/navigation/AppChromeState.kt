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
            AppRoute.CAMPAIGN_SESSIONS -> AppChromeState(
                title = "Sessions",
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
