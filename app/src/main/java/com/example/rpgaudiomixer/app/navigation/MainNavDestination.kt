package com.example.rpgaudiomixer.app.navigation

enum class MainNavDestination {
    HOME,
    CAMPAIGNS,
    SCENES,
    LIBRARY;

    companion object {
        const val CREDITS_ROUTE = "credits"
        const val SESSIONS_ROUTE = "sessions/{campaignId}"
        const val SESSION_SCENES_ROUTE = "sessionScenes/{sessionId}"
    }
}
