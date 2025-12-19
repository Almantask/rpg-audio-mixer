package com.example.rpgaudiomixer.acceptance.steps

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then


class HomeSteps {

    @Given("I am on the home screen")
    fun iAmInHomeScreen() {
    }

    @Then("I should see the text {string} on the home screen")
    fun iCanSeeText(text: String) {
        Espresso.onView(withText(text)).check(
            matches(
                isDisplayed()
            )
        )
    }
}