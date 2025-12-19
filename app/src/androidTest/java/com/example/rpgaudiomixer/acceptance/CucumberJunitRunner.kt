package com.example.rpgaudiomixer.acceptance;

import android.os.Bundle
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import java.io.File

@RunWith(Cucumber::class)
@CucumberOptions
class CucumberJunitRunner : CucumberAndroidJUnitRunner() {
    override fun onCreate(bundle: Bundle) {
        bundle.putString(
            "plugin", pluginConfigurationString
        )

        File(absoluteFilesPath).mkdirs()
        super.onCreate(bundle)
    }

    private val pluginConfigurationString: String
        get() {
            val cucumber = "cucumber"
            val separator = "--"
            return "junit:" + cucumber.getCucumberXml() + separator + "html:" + cucumber.getCucumberHtml()
        }

    private fun String.getCucumberHtml(): String {
        return "$absoluteFilesPath/$this.html"
    }

    private fun String.getCucumberXml(): String {
        return "$absoluteFilesPath/$this.xml"
    }

    private val absoluteFilesPath: String
        get() {
            val directory = targetContext.getExternalFilesDir(null)
            return File(directory, "reports").absolutePath
        }
}