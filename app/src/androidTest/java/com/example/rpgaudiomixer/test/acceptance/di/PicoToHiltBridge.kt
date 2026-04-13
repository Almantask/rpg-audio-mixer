package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import java.util.concurrent.atomic.AtomicReference

/**
 * Global bridge between PicoContainer's per-scenario lifecycle and Hilt's singleton graph.
 */
object PicoToHiltBridge {

    private val playerRef: AtomicReference<MixedMusicPlayer?> = AtomicReference(null)
    private val campaignRepoRef: AtomicReference<CampaignRepository?> = AtomicReference(null)
    private val sessionRepoRef: AtomicReference<SessionRepository?> = AtomicReference(null)
    private val sceneRepoRef: AtomicReference<SceneRepository?> = AtomicReference(null)

    var player: MixedMusicPlayer
        get() = playerRef.get()
            ?: error("PicoToHiltBridge.player was not set.")
        set(value) { playerRef.set(value) }

    var campaignRepository: CampaignRepository
        get() = campaignRepoRef.get()
            ?: error("PicoToHiltBridge.campaignRepository was not set.")
        set(value) { campaignRepoRef.set(value) }

    var sessionRepository: SessionRepository
        get() = sessionRepoRef.get()
            ?: error("PicoToHiltBridge.sessionRepository was not set.")
        set(value) { sessionRepoRef.set(value) }

    var sceneRepository: SceneRepository
        get() = sceneRepoRef.get()
            ?: error("PicoToHiltBridge.sceneRepository was not set.")
        set(value) { sceneRepoRef.set(value) }
}
