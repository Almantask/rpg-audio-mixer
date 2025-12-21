package com.example.rpgaudiomixer.test.acceptance.rules

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.rpgaudiomixer.app.MainActivity
import com.example.rpgaudiomixer.test.acceptance.di.AcceptanceTestPlayerHolder
import com.example.rpgaudiomixer.test.acceptance.world.SoundboardWorld
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.cucumber.junit.WithJunitRule
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Bridges PicoContainer-injected [SoundboardWorld] with Hilt-injected Activity dependencies.
 *
 * Flow:
 * 1. PicoContainer creates [SoundboardWorld] with a fresh [FakeMusicPlayer]
 * 2. PicoContainer injects this rule into step definitions
 * 3. Rule sets [AcceptanceTestPlayerHolder.player] to the per-scenario fake
 * 4. Activity launches → Hilt reads from holder → Activity uses scenario's fake
 */
@WithJunitRule
class SoundboardComposeRule(private val world: SoundboardWorld) {

    private val androidComposeRule: AndroidComposeTestRule<*, MainActivity> = createAndroidComposeRule<MainActivity>().also {
        // Bridge: Connect PicoContainer's per-scenario fake to Hilt's singleton graph
        AcceptanceTestPlayerHolder.player = world.fakeMusicPlayer
        world.reset()
    }

    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(CucumberHiltRule())
        .around(androidComposeRule)

    val composeRule: AndroidComposeTestRule<*, MainActivity>
        get() = androidComposeRule

    /**
     * Runs Hilt's test rule in a way compatible with Cucumber.
     *
     * Cucumber's JUnit integration may supply a [Description] without a testClass, but
     * Hilt's internal rule expects one (and will NPE otherwise). We work around that by
     * applying the underlying [HiltAndroidRule] with a synthetic [Description] that
     * contains a real test class.
     *
     * Note: HiltAndroidRule(owner) is deprecated in favor of HiltAndroidRule(this),
     * but that pattern only works when the rule is a field in a test class.
     * This wrapper pattern is the correct approach for Cucumber integration.
     */
    class CucumberHiltRule : TestRule {

        @HiltAndroidTest
        class HiltRuleOwner

        private val owner = HiltRuleOwner()
        
        @Suppress("DEPRECATION")
        private val hiltRule = HiltAndroidRule(owner)

        override fun apply(base: Statement, description: Description): Statement {
            val safeDescription = Description.createTestDescription(
                owner::class.java,
                description.displayName,
            )

            val injectingBase = object : Statement() {
                override fun evaluate() {
                    hiltRule.inject()
                    base.evaluate()
                }
            }

            return hiltRule.apply(injectingBase, safeDescription)
        }
    }
}
