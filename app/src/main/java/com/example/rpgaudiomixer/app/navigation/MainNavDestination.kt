package com.example.rpgaudiomixer.app.navigation

enum class MainNavDestination(
    val route: String,
    val label: String,
    val title: String,
) {
    HOME(
        route = "home",
        label = "HOME",
        title = "Home",
    ),
    CAMPAIGNS(
        route = "campaigns",
        label = "CAMPAIGNS",
        title = "Campaigns",
    ),
    SCENES(
        route = "scenes",
        label = "SCENES",
        title = "Scenes",
    ),
    LIBRARY(
        route = "library",
        label = "LIBRARY",
        title = "Library",
    ),
    ;

    companion object {
        val mainTabs: List<MainNavDestination> = listOf(
            HOME,
            CAMPAIGNS,
            SCENES,
            LIBRARY,
        )

        fun fromRoute(route: String?): MainNavDestination? = mainTabs.firstOrNull { it.route == route }
    }
}
