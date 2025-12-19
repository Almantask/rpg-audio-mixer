package com.example.rpgaudiomixer.acceptance

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.example.rpgaudiomixer.app.MainActivity
import io.cucumber.java.After
import io.cucumber.java.Before

class CucumberScenarioHolder {

    private var scenario: ActivityScenario<*>? = null

    /**This function will execute before each scenario,
     * allowing for any initial setup, including granting necessary app permissions.*/
    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val intent = Intent(instrumentation.targetContext, MainActivity::class.java)

        scenario = ActivityScenario.launch<Activity>(intent).onActivity { activity ->
            instrumentation.uiAutomation.apply {
                //Declare necessary permissions here
            }
        }

    }

    /**This function will be executed after each scenario, during which we will close the Scenario Holder.*/
    @After
    fun tearDown() {
        scenario?.close()
    }
}