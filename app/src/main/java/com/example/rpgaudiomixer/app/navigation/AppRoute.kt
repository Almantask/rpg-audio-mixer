package com.example.rpgaudiomixer.app.navigation

object AppRoute {
    const val CREDITS = "credits"
    const val TRASH = "credits/trash"
    const val CAMPAIGN_ID_ARG = "campaignId"
    const val SESSION_ID_ARG = "sessionId"
    const val SCENE_ID_ARG = "sceneId"
    const val AUTOPLAY_ARG = "autoplay"
    const val SOUNDSCAPE_CATEGORY_ID_ARG = "soundscapeCategoryId"
    const val CAMPAIGN_SESSIONS = "campaigns/{$CAMPAIGN_ID_ARG}/sessions"
    const val SESSION_SCENES = "sessions/{$SESSION_ID_ARG}/scenes"
    const val SCENE_DETAILS = "scenes/{$SCENE_ID_ARG}?$AUTOPLAY_ARG={$AUTOPLAY_ARG}"
    const val SOUNDSCAPE_LIBRARY = "library/soundscapes"
    const val SOUNDSCAPE_CATEGORY_COMPOSER =
        "library/soundscapes/{$SOUNDSCAPE_CATEGORY_ID_ARG}/compose"

    fun campaignSessions(campaignId: Long): String = "campaigns/$campaignId/sessions"

    fun sessionScenes(sessionId: Long): String = "sessions/$sessionId/scenes"

    fun sceneDetails(sceneId: Long, autoplay: Boolean): String =
        "scenes/$sceneId?$AUTOPLAY_ARG=$autoplay"

    fun soundscapeCategoryComposer(categoryId: Long): String =
        "library/soundscapes/$categoryId/compose"
}
