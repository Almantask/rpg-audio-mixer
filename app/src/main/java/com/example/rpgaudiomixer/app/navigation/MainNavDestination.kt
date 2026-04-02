package com.example.rpgaudiomixer.app.navigation

enum class MainNavDestination(val route: String) {
    HOME("home"),
    CAMPAIGNS("campaigns"),
    SCENES("scenes"),
    LIBRARY("library"),
}

object Routes {
    const val HOME = "home"
    const val CAMPAIGNS = "campaigns"
    const val SCENES = "scenes"
    const val LIBRARY = "library"
    const val CREDITS = "credits"

    const val SESSIONS = "sessions/{campaignId}"
    fun sessions(campaignId: Long) = "sessions/$campaignId"

    const val SESSION_SCENES = "session_scenes/{sessionId}"
    fun sessionScenes(sessionId: Long) = "session_scenes/$sessionId"

    const val ACTIVE_SCENE = "active_scene/{sceneId}/{autoPlay}"
    fun activeScene(sceneId: Long, autoPlay: Boolean = false) = "active_scene/$sceneId/$autoPlay"

    const val SOUNDSCAPE_COMPOSER = "soundscape_composer/{categoryId}"
    fun soundscapeComposer(categoryId: Long = -1L) = "soundscape_composer/$categoryId"

    const val ADD_TO_SCENE = "add_to_scene/{sceneId}/{mode}"
    fun addSoundscapeToScene(sceneId: Long) = "add_to_scene/$sceneId/soundscape"
    fun addFXToScene(sceneId: Long) = "add_to_scene/$sceneId/fx"
}

