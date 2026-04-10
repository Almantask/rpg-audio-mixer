package com.example.rpgaudiomixer.app.navigation

enum class MainNavDestination(
    val route: String,
    val label: String,
    val title: String,
    val testTag: String,
) {
    HOME(
        route = "home",
        label = "HOME",
        title = "Home",
        testTag = "BottomNav_HOME",
    ),
    CAMPAIGNS(
        route = "campaigns",
        label = "CAMPAIGNS",
        title = "Campaigns",
        testTag = "BottomNav_CAMPAIGNS",
    ),
    SCENES(
        route = "scenes",
        label = "SCENES",
        title = "Scenes",
        testTag = "BottomNav_SCENES",
    ),
    LIBRARY(
        route = "library",
        label = "LIBRARY",
        title = "Library",
        testTag = "BottomNav_LIBRARY",
    );

    companion object {
        val mainDestinations: List<MainNavDestination> = entries.toList()

        fun fromRoute(route: String?): MainNavDestination? {
            val normalizedRoute = route?.substringBefore("?")
            return mainDestinations.firstOrNull { destination ->
                normalizedRoute == destination.route || normalizedRoute?.startsWith("${destination.route}/") == true
            }
        }
    }
}
