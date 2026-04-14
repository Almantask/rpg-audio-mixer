package com.example.rpgaudiomixer.app.navigation

enum class MainNavDestination {
    HOME,
    CAMPAIGNS,
    SCENES,
    LIBRARY;

    companion object {
        const val CREDITS_ROUTE = "credits"
        const val TRASH_ROUTE = "trash"
        const val SESSIONS_ROUTE = "sessions/{campaignId}"
        const val SESSION_SCENES_ROUTE = "sessionScenes/{sessionId}"
        const val ACTIVE_SCENE_ROUTE = "activeScene/{sceneId}"
    }
}
