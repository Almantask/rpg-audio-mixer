package com.example.rpgaudiomixer.app.navigation

data class AppChrome(
    val title: String,
    val showBackArrow: Boolean,
    val showBottomBar: Boolean,
) {
    companion object {
        fun fromRoute(route: String?): AppChrome {
            MainNavDestination.fromRoute(route)?.let { destination ->
                return AppChrome(
                    title = destination.title,
                    showBackArrow = false,
                    showBottomBar = true,
                )
            }

            return when (route?.substringBefore("?")) {
                AppRoute.Settings.route -> AppChrome(
                    title = AppRoute.Settings.title,
                    showBackArrow = AppRoute.Settings.showBackArrow,
                    showBottomBar = AppRoute.Settings.showBottomBar,
                )
                AppRoute.Trash.route -> AppChrome(
                    title = AppRoute.Trash.title,
                    showBackArrow = AppRoute.Trash.showBackArrow,
                    showBottomBar = AppRoute.Trash.showBottomBar,
                )
                AppRoute.CampaignSessions.route.substringBefore("?") -> AppChrome(
                    title = AppRoute.CampaignSessions.title,
                    showBackArrow = AppRoute.CampaignSessions.showBackArrow,
                    showBottomBar = AppRoute.CampaignSessions.showBottomBar,
                )
                AppRoute.SessionScenes.route.substringBefore("?") -> AppChrome(
                    title = AppRoute.SessionScenes.title,
                    showBackArrow = AppRoute.SessionScenes.showBackArrow,
                    showBottomBar = AppRoute.SessionScenes.showBottomBar,
                )
                AppRoute.ActiveScene.route.substringBefore("?") -> AppChrome(
                    title = AppRoute.ActiveScene.title,
                    showBackArrow = AppRoute.ActiveScene.showBackArrow,
                    showBottomBar = AppRoute.ActiveScene.showBottomBar,
                )
                AppRoute.SoundscapeComposer.route.substringBefore("?") -> AppChrome(
                    title = AppRoute.SoundscapeComposer.title,
                    showBackArrow = AppRoute.SoundscapeComposer.showBackArrow,
                    showBottomBar = AppRoute.SoundscapeComposer.showBottomBar,
                )
                else -> AppChrome(
                    title = MainNavDestination.HOME.title,
                    showBackArrow = false,
                    showBottomBar = true,
                )
            }
        }
    }
}

sealed class AppRoute(
    val route: String,
    val title: String,
    val showBackArrow: Boolean,
    val showBottomBar: Boolean,
) {
    data object Settings : AppRoute(
        route = "settings",
        title = "Behind the Screen",
        showBackArrow = true,
        showBottomBar = false,
    )

    data object Trash : AppRoute(
        route = "trash",
        title = "Recent Deletes",
        showBackArrow = true,
        showBottomBar = false,
    )

    data object CampaignSessions : AppRoute(
        route = "campaigns/{campaignId}/sessions?campaignName={campaignName}",
        title = "Sessions",
        showBackArrow = true,
        showBottomBar = true,
    ) {
        fun createRoute(campaignId: Long, campaignName: String): String {
            return "campaigns/$campaignId/sessions?campaignName=${android.net.Uri.encode(campaignName)}"
        }
    }

    data object SessionScenes : AppRoute(
        route = "sessions/{sessionId}/scenes?sessionName={sessionName}",
        title = "Session Scenes",
        showBackArrow = true,
        showBottomBar = true,
    ) {
        fun createRoute(sessionId: Long, sessionName: String): String {
            return "sessions/$sessionId/scenes?sessionName=${android.net.Uri.encode(sessionName)}"
        }
    }

    data object ActiveScene : AppRoute(
        route = "scenes/{sceneId}?sceneName={sceneName}&autoplay={autoplay}",
        title = "Active Scene",
        showBackArrow = true,
        showBottomBar = true,
    ) {
        fun createRoute(sceneId: Long, sceneName: String, autoplay: Boolean): String {
            return "scenes/$sceneId?sceneName=${android.net.Uri.encode(sceneName)}&autoplay=$autoplay"
        }
    }

    data object SoundscapeComposer : AppRoute(
        route = "library/soundscapes/{categoryId}?categoryName={categoryName}",
        title = "Soundscape Composer",
        showBackArrow = true,
        showBottomBar = true,
    ) {
        fun createRoute(categoryId: Long, categoryName: String): String {
            return "library/soundscapes/$categoryId?categoryName=${android.net.Uri.encode(categoryName)}"
        }
    }
}
