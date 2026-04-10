package com.example.rpgaudiomixer.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Castle
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainNavDestination(
    val route: String,
    val title: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME(
        route = "home",
        title = "Home",
        label = "HOME",
        icon = Icons.Rounded.Castle,
    ),
    CAMPAIGNS(
        route = "campaigns",
        title = "Campaigns",
        label = "CAMPAIGNS",
        icon = Icons.Rounded.AutoStories,
    ),
    SCENES(
        route = "scenes",
        title = "Scenes",
        label = "SCENES",
        icon = Icons.Rounded.PhotoLibrary,
    ),
    LIBRARY(
        route = "library",
        title = "Library",
        label = "LIBRARY",
        icon = Icons.Rounded.LibraryMusic,
    );

    companion object {
        const val CREDITS_ROUTE = "credits"
        const val CAMPAIGN_ID_ARG = "campaignId"
        const val SESSION_ID_ARG = "sessionId"
        const val SCENE_ID_ARG = "sceneId"
        const val AUTOPLAY_ARG = "autoplay"
        const val CAMPAIGN_SESSIONS_ROUTE = "campaigns/{$CAMPAIGN_ID_ARG}/sessions"
        const val SESSION_SCENES_ROUTE = "sessions/{$SESSION_ID_ARG}/scenes"
        const val ACTIVE_SCENE_ROUTE = "scenes/{$SCENE_ID_ARG}?$AUTOPLAY_ARG={$AUTOPLAY_ARG}"

        fun campaignSessionsRoute(campaignId: Long): String {
            return "campaigns/$campaignId/sessions"
        }

        fun sessionScenesRoute(sessionId: Long): String {
            return "sessions/$sessionId/scenes"
        }

        fun activeSceneRoute(sceneId: Long, autoplay: Boolean): String {
            return "scenes/$sceneId?$AUTOPLAY_ARG=$autoplay"
        }

        fun fromRoute(route: String?): MainNavDestination? {
            return entries.firstOrNull { destination ->
                route == destination.route || route?.startsWith("${destination.route}/") == true
            }
        }
    }
}
