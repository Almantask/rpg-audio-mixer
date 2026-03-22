package com.example.rpgaudiomixer.test.acceptance

import android.graphics.Bitmap
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.Screenshot
import io.cucumber.java.AfterStep
import io.cucumber.java.Scenario
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Cucumber hook that captures a screenshot after every step.
 *
 * Screenshots are:
 *  1. Attached to the Cucumber [Scenario] so the HTML report embeds them inline.
 *  2. Saved as individual PNG files under the app's external-files directory
 *     (`…/Android/data/com.example.rpgaudiomixer/files/screenshots/`) so CI can
 *     pull them with `adb pull` and upload as a standalone artifact even when the
 *     Cucumber HTML plugin embedding is unavailable.
 */
class ScreenshotHooks {

    // PicoContainer creates a fresh instance per scenario, so stepIndex resets automatically.
    private var stepIndex = 0

    @AfterStep
    fun takeScreenshotAfterStep(scenario: Scenario) {
        stepIndex++
        val safeName = scenario.name.replace(Regex("[^A-Za-z0-9_-]"), "_").take(MAX_SCENARIO_NAME_LENGTH)
        val fileName = "step_${stepIndex.toString().padStart(3, '0')}_${safeName}.png"

        try {
            val bitmap: Bitmap = Screenshot.capture().bitmap ?: run {
                Log.w("ScreenshotHooks", "Screenshot bitmap is null for step $stepIndex — skipping")
                return
            }

            ByteArrayOutputStream().use { stream ->
                // For PNG, quality controls the compression level: 0 = no compression,
                // 100 = maximum compression. Use 75 for a balance of size vs. speed.
                bitmap.compress(Bitmap.CompressFormat.PNG, PNG_COMPRESSION_QUALITY, stream)
                val bytes = stream.toByteArray()

                // 1. Embed in the Cucumber HTML report (base64-inline next to the step)
                scenario.attach(bytes, "image/png", fileName)

                // 2. Write to an external file so CI can pull it independently
                saveToFile(fileName, bytes)
            }
        } catch (e: Exception) {
            Log.w("ScreenshotHooks", "Screenshot failed at step $stepIndex: ${e::class.simpleName}: ${e.message}")
        }
    }

    private fun saveToFile(fileName: String, bytes: ByteArray) {
        try {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val dir = File(context.getExternalFilesDir(null), "screenshots")
            dir.mkdirs()
            File(dir, fileName).writeBytes(bytes)
        } catch (e: Exception) {
            Log.w("ScreenshotHooks", "Screenshot file-save failed: ${e::class.simpleName}: ${e.message}")
        }
    }

    private companion object {
        /** Maximum characters taken from the scenario name for the screenshot filename. */
        const val MAX_SCENARIO_NAME_LENGTH = 80

        /** PNG compression level: 75 balances artifact size and encoding speed. */
        const val PNG_COMPRESSION_QUALITY = 75
    }
}
