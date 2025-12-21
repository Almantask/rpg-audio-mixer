package com.example.rpgaudiomixer.test.acceptance.rules

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.rpgaudiomixer.app.MainActivity
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import io.cucumber.junit.WithJunitRule
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

/**
 * Bridges PicoContainer-injected [FakeMusicPlayer] with Hilt-injected Activity dependencies.
 *
 * Flow:
 * 1. PicoContainer creates [FakeMusicPlayer] per scenario
 * 2. PicoContainer injects this rule into step definitions
 * 3. Rule sets [PicoToHiltBridge.player] to the per-scenario fake
 * 4. Activity launches → Hilt reads from holder → Activity uses scenario's fake
 */
@WithJunitRule
class SoundboardComposeRule(private val fakeMusicPlayer: FakeMusicPlayer) {

    private val androidComposeRule: AndroidComposeTestRule<*, MainActivity> = createAndroidComposeRule<MainActivity>().also {
        PicoToHiltBridge.player = fakeMusicPlayer
        fakeMusicPlayer.reset()
    }

    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(CucumberHiltRule())
        .around(androidComposeRule)

    val composeRule: AndroidComposeTestRule<*, MainActivity>
        get() = androidComposeRule
}
