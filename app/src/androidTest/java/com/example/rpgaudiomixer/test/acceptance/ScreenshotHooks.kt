package com.example.rpgaudiomixer.test.acceptance

import android.graphics.Bitmap
import android.util.Log
import androidx.test.runner.screenshot.Screenshot
import io.cucumber.java.AfterStep
import io.cucumber.java.Scenario
import java.io.ByteArrayOutputStream

/**
 * Cucumber hook that captures a screenshot after every step and attaches it to the
 * scenario as a PNG embedding. The Cucumber HTML report plugin then renders each
 * screenshot inline next to the step that produced it.
 */
class ScreenshotHooks {

    @AfterStep
    fun takeScreenshotAfterStep(scenario: Scenario) {
        try {
            val capture = Screenshot.capture()
            ByteArrayOutputStream().use { stream ->
                capture.bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
                scenario.attach(stream.toByteArray(), "image/png", "step-screenshot")
            }
        } catch (e: Exception) {
            Log.w("ScreenshotHooks", "Screenshot capture failed: ${e::class.simpleName}: ${e.message}")
        }
    }
}
