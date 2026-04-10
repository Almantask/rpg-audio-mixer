package com.example.rpgaudiomixer.test.acceptance.steps

import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.Then
import org.junit.Assert.assertNotNull

class HelloWorldSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    @Then("the app opens without any errors")
    fun theAppOpensWithoutAnyErrors() {
        org.junit.Assert.assertNotNull(composeRuleHolder.composeRule.activity)
    }
}
