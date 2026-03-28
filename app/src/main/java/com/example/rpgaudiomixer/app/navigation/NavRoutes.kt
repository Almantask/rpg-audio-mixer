package com.example.rpgaudiomixer.app.navigation

object NavRoutes {
    const val HOME = "home"
    const val CAMPAIGNS = "campaigns"
    const val SESSIONS = "sessions/{campaignId}"
    const val SESSION_SCENES = "session_scenes/{sessionId}"
    const val ACTIVE_SCENE = "active_scene/{sceneId}"
    const val SCENES = "scenes"
    const val LIBRARY = "library"
    const val SOUNDSCAPE_COMPOSER = "soundscape_composer/{categoryId}"
    const val CREDITS = "credits"
    const val ADD_SOUNDSCAPE_TO_SCENE = "add_soundscape/{sceneId}"
    const val ADD_FX_TO_SCENE = "add_fx/{sceneId}"

    fun sessions(campaignId: Long) = "sessions/$campaignId"
    fun sessionScenes(sessionId: Long) = "session_scenes/$sessionId"
    fun activeScene(sceneId: Long) = "active_scene/$sceneId"
    fun soundscapeComposer(categoryId: Long) = "soundscape_composer/$categoryId"
    fun addSoundscapeToScene(sceneId: Long) = "add_soundscape/$sceneId"
    fun addFxToScene(sceneId: Long) = "add_fx/$sceneId"
}

/** Bottom navigation tabs. */
enum class BottomTab(val route: String, val label: String) {
    HOME(NavRoutes.HOME, "HOME"),
    CAMPAIGNS(NavRoutes.CAMPAIGNS, "CAMPAIGNS"),
    SCENES(NavRoutes.SCENES, "SCENES"),
    LIBRARY(NavRoutes.LIBRARY, "LIBRARY"),
}
