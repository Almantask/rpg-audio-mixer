package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.PendingException
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

/**
 * Step definitions for scene_cloning.feature (@iter9).
 *
 * Clone scenarios that interact with the ScenesScreen UI (tap clone button, fill dialog)
 * are implemented. Steps that require verifying per-category state on a cloned scene's
 * ActiveSceneScreen (e.g., checking MIX % values) are [PendingException] until end-to-end
 * navigation is wired.
 */
@Suppress("TooManyFunctions")
class SceneCloningSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {

    private val composeTestRule get() = composeRuleHolder.composeRule
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository
    private val soundscapeCategoryRepository get() = PicoToHiltBridge.soundscapeCategoryRepository

    // ── Given / Background ────────────────────────────────────────────────

    @Given("{string} has {string} soundscape category at MIX {int}%")
    fun sceneHasSoundscapeAtMix(sceneName: String, categoryName: String, mixPercent: Int) {
        throw PendingException(
            "MIX percentage persistence requires a 'mix' column on SoundscapeCategoryEntity – promoted in a later iteration."
        )
    }

    @Given("{string} has {string} sound effect at MIX {int}%")
    fun sceneHasSoundEffectAtMix(sceneName: String, soundName: String, mixPercent: Int) {
        throw PendingException(
            "MIX percentage persistence requires a 'mix' column on SoundscapeCategoryEntity – promoted in a later iteration."
        )
    }

    @Given("{string} has {string} tag")
    fun sceneHasTag(sceneName: String, tag: String) {
        throw PendingException(
            "Scene tags are not yet persisted – requires a 'tags' field on SceneEntity – promoted in a later iteration."
        )
    }

    @Given("I have cloned the {string} scene as {string}")
    fun iHaveClonedScene(sourceSceneName: String, clonedSceneName: String) {
        val source = runBlocking {
            sceneRepository.observeAll().first().firstOrNull { it.name == sourceSceneName }
        } ?: error("Source scene '$sourceSceneName' not found in database")

        runBlocking { sceneRepository.cloneScene(source.id, clonedSceneName) }
    }

    // ── When ──────────────────────────────────────────────────────────────

    @When("I clone the {string} scene as {string}")
    fun cloneScene(sourceSceneName: String, clonedSceneName: String) {
        // Navigate to the SCENES tab
        composeTestRule
            .onNodeWithTag("bottomNavItem_SCENES")
            .performClick()
        composeTestRule.waitForIdle()

        // Tap the clone button on the source scene card
        composeTestRule
            .onNodeWithTag("cloneButton_$sourceSceneName")
            .performClick()
        composeTestRule.waitForIdle()

        // Fill the clone name dialog
        composeTestRule
            .onNodeWithTag("cloneSceneNameField")
            .performTextClearance()
        composeTestRule
            .onNodeWithTag("cloneSceneNameField")
            .performTextInput(clonedSceneName)
        composeTestRule
            .onNodeWithTag("cloneSceneConfirmButton")
            .performClick()
        composeTestRule.waitForIdle()
    }

    @When("I add {string} to the {string} soundscape categories")
    fun addCategoryToScene(categoryName: String, sceneName: String) {
        throw PendingException(
            "Adding a soundscape category requires navigating to the AddSoundscapeScreen for '$sceneName'."
        )
    }

    @When("I change {string} MIX to {int}% on {string}")
    fun changeMixPercent(categoryName: String, mixPercent: Int, sceneName: String) {
        throw PendingException(
            "MIX slider requires the ActiveSceneScreen to be open for '$sceneName' – promoted in a later iteration."
        )
    }

    // ── Then ──────────────────────────────────────────────────────────────

    @Then("I see the {string} scene in my scenes list")
    fun verifySceneInList(sceneName: String) {
        composeTestRule
            .onNodeWithTag("bottomNavItem_SCENES")
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText(sceneName)
            .assertIsDisplayed()
    }

    @Then("the {string} scene should have {string} at MIX {int}%")
    fun verifyClonedSceneHasCategoryAtMix(sceneName: String, categoryName: String, mixPercent: Int) {
        throw PendingException(
            "MIX percentage assertion requires a 'mix' column on SoundscapeCategoryEntity – promoted in a later iteration."
        )
    }

    @Then("the {string} scene should have {string} sound effect")
    fun verifyClonedSceneHasSoundEffect(sceneName: String, soundName: String) {
        // Assert via repository that the cloned scene has the expected category
        val cloned = runBlocking {
            sceneRepository.observeAll().first().firstOrNull { it.name == sceneName }
        }
        assertThat(cloned).isNotNull

        val categories = runBlocking {
            soundscapeCategoryRepository.observeByScene(cloned!!.id)
                .first()
        }
        assertThat(categories.map { it.name }).contains(soundName)
    }

    @Then("the {string} scene should have {string} tag")
    fun verifyClonedSceneHasTag(sceneName: String, tag: String) {
        throw PendingException(
            "Scene tags are not yet persisted – requires a 'tags' field on SceneEntity – promoted in a later iteration."
        )
    }

    @Then("the original {string} scene should not contain {string}")
    fun verifyOriginalSceneDoesNotContain(sceneName: String, categoryName: String) {
        val original = runBlocking {
            sceneRepository.observeAll().first().firstOrNull { it.name == sceneName }
        }
        assertThat(original).isNotNull

        val categories = runBlocking {
            soundscapeCategoryRepository.observeByScene(original!!.id)
                .first()
        }
        assertThat(categories.map { it.name }).doesNotContain(categoryName)
    }

    @And("the {string} scene should still have {string} at MIX {int}%")
    fun verifyOriginalSceneHasCategoryAtMix(sceneName: String, categoryName: String, mixPercent: Int) {
        throw PendingException(
            "MIX percentage assertion requires a 'mix' column on SoundscapeCategoryEntity – promoted in a later iteration."
        )
    }
}
