package com.example.rpgaudiomixer.app.navigation

object AppRoute {
    const val CREDITS = "credits"
    const val TRASH = "trash"
    const val CAMPAIGN_ID_ARG = "campaignId"
    const val SESSION_ID_ARG = "sessionId"
    const val SCENE_ID_ARG = "sceneId"
    const val AUTOPLAY_ARG = "autoplay"
    const val CAMPAIGN_SESSIONS = "campaigns/{$CAMPAIGN_ID_ARG}/sessions"
    const val SESSION_SCENES = "sessions/{$SESSION_ID_ARG}/scenes"
    const val SCENE_DETAILS = "scenes/{$SCENE_ID_ARG}?$AUTOPLAY_ARG={$AUTOPLAY_ARG}"

    fun campaignSessions(campaignId: Long): String = "campaigns/$campaignId/sessions"

    fun sessionScenes(sessionId: Long): String = "sessions/$sessionId/scenes"

    fun sceneDetails(sceneId: Long, autoplay: Boolean): String =
        "scenes/$sceneId?$AUTOPLAY_ARG=$autoplay"
}
