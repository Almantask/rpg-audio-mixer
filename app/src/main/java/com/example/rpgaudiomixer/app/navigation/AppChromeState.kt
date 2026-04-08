package com.example.rpgaudiomixer.app.navigation

data class AppChromeState(
    val title: String,
    val showBackArrow: Boolean,
    val showBottomBar: Boolean,
)

object AppChromeStateResolver {
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

            else -> AppChromeState(
                title = MainNavDestination.HOME.title,
                showBackArrow = false,
                showBottomBar = true,
            )
        }
    }
}
