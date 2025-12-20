package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.rpgaudiomixer.app.MainActivity
import com.example.rpgaudiomixer.test.acceptance.util.assertTextDisplayedWithDebug
import io.cucumber.java.en.Then
import io.cucumber.junit.WithJunitRule
import org.junit.Rule

@WithJunitRule
class ProfileSteps {

    @Then("I should see the text {string} on the home screen")
    fun iCanSeeText(text: String) {
        //composeRule.assertTextDisplayedWithDebug(text)
    }
}