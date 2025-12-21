package com.example.rpgaudiomixer.test.acceptance

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltTestApplication
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import java.io.File

/**
 * This class is configured as `testInstrumentationRunner` in build.gradle.kts
 * and is required to:
 * 1. Replace the Application with [HiltTestApplication] for Hilt DI
 * 2. Configure Cucumber test execution and reporting
 *
 * Despite appearing unused in code, this class is discovered and instantiated
 * by the Android test framework at runtime.
 */
@Suppress("unused")
class CucumberJunitRunner : CucumberAndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application = super.newApplication(cl, HiltTestApplication::class.java.name, context)

    override fun onCreate(bundle: Bundle) {
        InstrumentationRegistry.registerInstance(this, bundle)

        val existingPlugin = bundle.getString("plugin")
        val mergedPlugin = sequenceOf(existingPlugin, pluginConfigurationString)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .joinToString(separator = ",")
        bundle.putString("plugin", mergedPlugin)

        File(absoluteFilesPath).mkdirs()
        super.onCreate(bundle)
    }

    private val pluginConfigurationString: String
        get() {
            val cucumber = "cucumber"
            return "html:" + cucumber.getCucumberHtml()
        }

    private fun String.getCucumberHtml(): String = "$absoluteFilesPath/$this.html"

    private val absoluteFilesPath: String
        get() {
            val directory = targetContext.getExternalFilesDir(null)
            return File(directory, "reports").absolutePath
        }
}