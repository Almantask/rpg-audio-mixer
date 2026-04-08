package com.example.rpgaudiomixer.app.navigation

object AppRoute {
    const val CREDITS = "credits"
    const val TRASH = "trash"
    const val CAMPAIGN_ID_ARG = "campaignId"
    const val CAMPAIGN_SESSIONS = "campaigns/{$CAMPAIGN_ID_ARG}/sessions"

    fun campaignSessions(campaignId: Long): String = "campaigns/$campaignId/sessions"
}
