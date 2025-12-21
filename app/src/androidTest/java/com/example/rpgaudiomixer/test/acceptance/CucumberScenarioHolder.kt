package com.example.rpgaudiomixer.test.acceptance

import androidx.test.platform.app.InstrumentationRegistry
import com.example.rpgaudiomixer.test.acceptance.di.AcceptanceTestPlayerHolder
import io.cucumber.java.After
import io.cucumber.java.Before

class CucumberScenarioHolder {

    /**
     * Executes before each scenario.
     * Keep this for global setup such as granting runtime permissions.
     */
    @Before
    fun setUp() {
        val instrumentation = try {
            InstrumentationRegistry.getInstrumentation()
        } catch (t: Throwable) {
            throw IllegalStateException(
                "Android instrumentation is not available. " +
                    "Cucumber feature tests in this project must run as Android instrumented tests " +
                    "(e.g. via :app:connectedDebugAndroidTest / :app:connectedAndroidTest). " +
                    "If you ran a .feature from the IDE Cucumber runner, it likely executed as a local JVM test.",
                t
            )
        }
        instrumentation.uiAutomation.apply {
            // Declare necessary permissions here
        }
    }

    @After
    fun tearDown() {
        AcceptanceTestPlayerHolder.clear()
    }
}