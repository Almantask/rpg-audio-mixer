package com.example.rpgaudiomixer.test.acceptance.rules

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

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
