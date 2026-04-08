package com.example.rpgaudiomixer.test.acceptance.rules

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.rpgaudiomixer.app.MainActivity
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeCampaignRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeFxRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSceneRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSessionRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSessionSceneRepository
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeTrackStatsRepository
import io.cucumber.junit.WithJunitRule
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

/**
 * Bridges PicoContainer-injected fakes with Hilt-injected Activity dependencies.
 *
 * Flow:
 * 1. PicoContainer creates fakes per scenario
 * 2. PicoContainer injects this rule into step definitions
 * 3. Rule sets [PicoToHiltBridge] properties to the per-scenario fakes
 * 4. Activity launches → Hilt reads from holder → Activity uses scenario's fakes
 */
@WithJunitRule
class MainActivityComposeRule(
    private val fakeMusicPlayer: FakeMusicPlayer,
    private val fakeCampaignRepository: FakeCampaignRepository,
    private val fakeSessionRepository: FakeSessionRepository,
    private val fakeSceneRepository: FakeSceneRepository,
    private val fakeSessionSceneRepository: FakeSessionSceneRepository,
    private val fakeTrackStatsRepository: FakeTrackStatsRepository,
    private val fakeFxRepository: FakeFxRepository
) {
    // Consider making it more generic when more windows come.
    private val androidComposeRule: AndroidComposeTestRule<*, MainActivity> = createAndroidComposeRule<MainActivity>().also {
        PicoToHiltBridge.player = fakeMusicPlayer
        PicoToHiltBridge.campaignRepository = fakeCampaignRepository
        PicoToHiltBridge.sessionRepository = fakeSessionRepository
        PicoToHiltBridge.sceneRepository = fakeSceneRepository
        PicoToHiltBridge.sessionSceneRepository = fakeSessionSceneRepository
        PicoToHiltBridge.trackStatsRepository = fakeTrackStatsRepository
        PicoToHiltBridge.fxRepository = fakeFxRepository
        fakeMusicPlayer.reset()
    }

    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(CucumberHiltRule())
        .around(androidComposeRule)

    val composeRule: AndroidComposeTestRule<*, MainActivity>
        get() = androidComposeRule
}
