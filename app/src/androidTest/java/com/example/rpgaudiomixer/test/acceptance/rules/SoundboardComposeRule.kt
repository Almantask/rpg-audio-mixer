package com.example.rpgaudiomixer.test.acceptance.rules

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.rpgaudiomixer.app.MainActivity
import com.example.rpgaudiomixer.app.di.ServiceLocator
import com.example.rpgaudiomixer.test.acceptance.world.SoundboardWorld
import io.cucumber.junit.WithJunitRule
import org.junit.Rule

/**
 * Central place to create a Compose rule in a way compatible with Cucumber's @WithJunitRule.
 */
@WithJunitRule
class SoundboardComposeRule(private val world: SoundboardWorld) {

    @get:Rule
    val composeRule: AndroidComposeTestRule<*, MainActivity> = createAndroidComposeRule<MainActivity>().also {
        // Ensure the Activity uses the per-scenario fake.
        ServiceLocator.musicPlayerFactory = { world.fakeMusicPlayer }
        world.reset()
    }
}
