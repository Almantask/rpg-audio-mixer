package com.example.rpgaudiomixer.test.acceptance.rules

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.rpgaudiomixer.app.MainActivity
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import io.cucumber.junit.WithJunitRule
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

import com.example.rpgaudiomixer.test.acceptance.rules.CucumberHiltRule

import dagger.hilt.android.EntryPointAccessors
import com.example.rpgaudiomixer.test.acceptance.di.RepositoryEntryPoint

import androidx.test.platform.app.InstrumentationRegistry

/**
 * Bridges PicoContainer-injected dependencies with Hilt-injected Activity dependencies.
 */
@WithJunitRule
class MainActivityComposeRule {
    private val androidComposeRule: AndroidComposeTestRule<*, MainActivity> = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(CucumberHiltRule())
        .around(androidComposeRule)

    val composeRule: AndroidComposeTestRule<*, MainActivity>
        get() = androidComposeRule
}
