package com.example.rpgaudiomixer.test.acceptance

import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import java.io.File

class CucumberJunitRunner : CucumberAndroidJUnitRunner() {
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