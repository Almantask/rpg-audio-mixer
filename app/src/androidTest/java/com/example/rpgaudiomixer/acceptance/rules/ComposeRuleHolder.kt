package com.example.rpgaudiomixer.acceptance.rules

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import io.cucumber.junit.WithJunitRule
import org.junit.Rule

@WithJunitRule
class ComposeRuleHolder {
    @get:Rule val composeRule = createEmptyComposeRule()
}