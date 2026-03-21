package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import com.example.rpgaudiomixer.app.components.SoundboardTestTags
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert.assertEquals

class IntensitySteps(
    private val fakeMusicPlayer: FakeMusicPlayer,
    private val composeRuleHolder: MainActivityComposeRule,
) {

    @When("I slide the {string} intensity to {word}")
    fun iSlideTheIntensityTo(categoryId: String, level: String) {
        val sliderValue = IntensityLevel.fromString(level).toSliderValue()
        composeRuleHolder.composeRule
            .onNodeWithTag(SoundboardTestTags.intensitySlider(categoryId))
            .performSemanticsAction(SemanticsActions.SetProgress) { it(sliderValue) }
    }

    @Then("the {string} intensity level should be {word}")
    fun theIntensityLevelShouldBe(categoryId: String, level: String) {
        composeRuleHolder.composeRule.waitForIdle()
        val expected = IntensityLevel.fromString(level)
        assertEquals(expected, fakeMusicPlayer.getIntensityLevel(categoryId))
    }
}
