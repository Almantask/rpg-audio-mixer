package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import java.util.concurrent.atomic.AtomicReference

/**
 * Global bridge between PicoContainer's per-scenario lifecycle and Hilt's singleton graph.
 */
object PicoToHiltBridge {

    private val playerRef: AtomicReference<MixedMusicPlayer?> = AtomicReference(null)
    private val repoRef: AtomicReference<CampaignRepository?> = AtomicReference(null)
    private val sessionRepoRef: AtomicReference<SessionRepository?> = AtomicReference(null)

    var player: MixedMusicPlayer
        get() = playerRef.get()
            ?: error("PicoToHiltBridge.player was not set.")
        set(value) { playerRef.set(value) }

    var campaignRepository: CampaignRepository
        get() = repoRef.get()
            ?: error("PicoToHiltBridge.campaignRepository was not set.")
        set(value) { repoRef.set(value) }

    var sessionRepository: SessionRepository
        get() = sessionRepoRef.get()
            ?: error("PicoToHiltBridge.sessionRepository was not set.")
        set(value) { sessionRepoRef.set(value) }
}
