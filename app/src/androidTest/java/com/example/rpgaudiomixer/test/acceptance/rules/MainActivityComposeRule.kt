package com.example.rpgaudiomixer.test.acceptance.rules

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.rpgaudiomixer.app.MainActivity
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeRandomiser
import io.cucumber.junit.WithJunitRule
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

/**
 * Bridges PicoContainer-injected [FakeRandomiser] with Hilt-injected Activity dependencies.
 *
 * Flow:
 * 1. PicoContainer creates [FakeRandomiser] per scenario
 * 2. PicoContainer injects this rule into step definitions
 * 3. Rule sets [PicoToHiltBridge.randomiser] to the per-scenario fake
 * 4. Activity launches → Hilt reads from holder → Activity uses real player with scenario's fake randomiser
 */
@WithJunitRule
class MainActivityComposeRule(private val fakeRandomiser: FakeRandomiser) {
    // Consider making it more generic when more windows come.
    private val androidComposeRule: AndroidComposeTestRule<*, MainActivity> = createAndroidComposeRule<MainActivity>().also {
        PicoToHiltBridge.randomiser = fakeRandomiser
    }

    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(CucumberHiltRule())
        .around(androidComposeRule)

    val composeRule: AndroidComposeTestRule<*, MainActivity>
        get() = androidComposeRule
}
