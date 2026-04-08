package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.FxRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.repository.SessionSceneRepository
import com.example.rpgaudiomixer.domain.repository.TrackStatsRepository
import java.util.concurrent.atomic.AtomicReference

/**
 * Global bridge between PicoContainer's per-scenario lifecycle and Hilt's singleton graph.
 *
 * ## Why this exists
 * - **Hilt**: Creates SingletonComponent once per test class (expensive to recreate)
 * - **Cucumber + PicoContainer**: Creates fresh instances per scenario via pure DI
 * - **This holder**: Allows scenarios to swap fakes without restarting Hilt
 *
 * ## Pure DI Flow
 * ```
 * PicoContainer constructs FakeMusicPlayer
 *       ↓
 * PicoContainer injects into SoundboardComposeRule(fakeMusicPlayer)
 *       ↓
 * Rule sets: AcceptanceTestPlayerHolder.player = fakeMusicPlayer
 *       ↓
 * Hilt's FakeMixedMusicPlayerModule reads from holder
 *       ↓
 * Activity receives the per-scenario fake
 * ```
 *
 * No manual instantiation—PicoContainer manages the entire graph.
 */
object PicoToHiltBridge {

    private val playerRef: AtomicReference<MixedMusicPlayer?> = AtomicReference(null)
    private val campaignRepoRef: AtomicReference<CampaignRepository?> = AtomicReference(null)
    private val sessionRepoRef: AtomicReference<SessionRepository?> = AtomicReference(null)
    private val sceneRepoRef: AtomicReference<SceneRepository?> = AtomicReference(null)
    private val sessionSceneRepoRef: AtomicReference<SessionSceneRepository?> = AtomicReference(null)
    private val trackStatsRepoRef: AtomicReference<TrackStatsRepository?> = AtomicReference(null)
    private val fxRepoRef: AtomicReference<FxRepository?> = AtomicReference(null)

    var player: MixedMusicPlayer
        get() = playerRef.get()
            ?: error(
                "AcceptanceTestPlayerHolder.player was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            playerRef.set(value)
        }

    var campaignRepository: CampaignRepository
        get() = campaignRepoRef.get()
            ?: error(
                "PicoToHiltBridge.campaignRepository was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            campaignRepoRef.set(value)
        }

    var sessionRepository: SessionRepository
        get() = sessionRepoRef.get()
            ?: error(
                "PicoToHiltBridge.sessionRepository was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            sessionRepoRef.set(value)
        }

    var sceneRepository: SceneRepository
        get() = sceneRepoRef.get()
            ?: error(
                "PicoToHiltBridge.sceneRepository was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            sceneRepoRef.set(value)
        }

    var sessionSceneRepository: SessionSceneRepository
        get() = sessionSceneRepoRef.get()
            ?: error(
                "PicoToHiltBridge.sessionSceneRepository was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            sessionSceneRepoRef.set(value)
        }

    var trackStatsRepository: TrackStatsRepository
        get() = trackStatsRepoRef.get()
            ?: error(
                "PicoToHiltBridge.trackStatsRepository was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            trackStatsRepoRef.set(value)
        }

    var fxRepository: FxRepository
        get() = fxRepoRef.get()
            ?: error(
                "PicoToHiltBridge.fxRepository was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            fxRepoRef.set(value)
        }
}


