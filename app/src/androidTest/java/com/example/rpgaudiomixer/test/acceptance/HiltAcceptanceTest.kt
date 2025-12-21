package com.example.rpgaudiomixer.test.acceptance

import com.example.rpgaudiomixer.app.di.PlayerModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Empty "anchor" test that ensures Hilt's standard instrumentation flow is used.
 *
 * - Creates the Hilt component.
 * - Applies @UninstallModules so androidTest bindings can replace prod bindings.
 *
 * Cucumber scenarios are still executed by [CucumberJunitRunner], but this test guarantees
 * the Hilt test harness is configured correctly and will catch regressions early.
 */
@HiltAndroidTest
@UninstallModules(PlayerModule::class)
class HiltAcceptanceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun hilt_instrumentation_harness_bootstraps() {
        // No-op: if Hilt can't build the component graph, this test will fail.
    }
}

