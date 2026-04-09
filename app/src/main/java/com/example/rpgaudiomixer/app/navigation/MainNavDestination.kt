package com.example.rpgaudiomixer.app.navigation

enum class MainNavDestination(
    val route: String,
    val label: String,
    val screenTitle: String,
) {
    HOME(
        route = "home",
        label = "HOME",
        screenTitle = "Home",
    ),
    CAMPAIGNS(
        route = "campaigns",
        label = "CAMPAIGNS",
        screenTitle = "Campaigns",
    ),
    SCENES(
        route = "scenes",
        label = "SCENES",
        screenTitle = "Scenes",
    ),
    LIBRARY(
        route = "library",
        label = "LIBRARY",
        screenTitle = "Library",
    ),
    SETTINGS(
        route = "settings",
        label = "SETTINGS",
        screenTitle = "Behind the Screen",
    ),
    TRASH(
        route = "settings/trash",
        label = "TRASH",
        screenTitle = "Recent Deletes",
    ),
    CAMPAIGN_SESSIONS(
        route = "campaigns/{campaignId}/sessions",
        label = "SESSIONS",
        screenTitle = "Sessions",
    ),
}
