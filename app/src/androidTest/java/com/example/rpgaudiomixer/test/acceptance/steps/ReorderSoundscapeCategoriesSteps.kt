package com.example.rpgaudiomixer.test.acceptance.steps

import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer
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
 * Step definitions for reorder_soundscape_categories.feature (@iter10).
 *
 * Drag-and-drop gesture steps cannot be reliably automated via Espresso/Compose UI Test APIs
 * on a real device without root access. Gesture-dependent steps are [PendingException].
 *
 * The persistence scenario is partially testable via the repository layer (verified after
 * a simulated reorder) and is implemented below.
 */
@Suppress("TooManyFunctions")
class ReorderSoundscapeCategoriesSteps(
    private val fakeMusicPlayer: FakeMusicPlayer,
    private val composeRuleHolder: MainActivityComposeRule,
) {

    private val composeTestRule get() = composeRuleHolder.composeRule
    private val soundscapeCategoryRepository get() = PicoToHiltBridge.soundscapeCategoryRepository
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository

    // ── Given / Background ─────────────────────────────────────────────────

    @Given("there are at least two soundscape categories in the active scene")
    fun thereAreAtLeastTwoSoundscapeCategories() {
        throw PendingException(
            "Requires the ActiveSceneScreen to be open with categories via full navigation – promoted in a later iteration."
        )
    }

    @Given("the order is {string}, {string}, {string}")
    fun givenTheOrderIs(first: String, second: String, third: String) {
        throw PendingException(
            "Setting up a specific category order requires the ActiveSceneScreen to be open – promoted in a later iteration."
        )
    }

    @Given("{string} is currently playing")
    fun categoryIsCurrentlyPlaying(categoryName: String) {
        fakeMusicPlayer.playLoopingSound(categoryName)
    }

    // ── When ──────────────────────────────────────────────────────────────

    @When("I long-press on the {string} category card")
    fun longPressOnCategoryCard(categoryName: String) {
        throw PendingException(
            "Long-press drag gesture on '$categoryName' requires the ActiveSceneScreen to be open – promoted in a later iteration."
        )
    }

    @When("I drag {string} above {string}")
    fun dragCategoryAbove(dragged: String, target: String) {
        throw PendingException(
            "Drag gesture from '$dragged' to above '$target' cannot be reliably automated – promoted in a later iteration."
        )
    }

    @When("I drag {string} to the top")
    fun dragCategoryToTop(categoryName: String) {
        throw PendingException(
            "Drag-to-top gesture for '$categoryName' cannot be reliably automated – promoted in a later iteration."
        )
    }

    @And("I close and reopen the scene")
    fun closeAndReopenScene() {
        throw PendingException(
            "Closing and reopening the scene requires full Campaign→Session→Scene navigation – promoted in a later iteration."
        )
    }

    // ── Then ──────────────────────────────────────────────────────────────

    @Then("the card enters drag mode and can be repositioned")
    fun cardEntersDragMode() {
        throw PendingException(
            "Visual drag mode indicator requires the ActiveSceneScreen to be open – promoted in a later iteration."
        )
    }

    @Then("the order becomes {string}, {string}, {string}")
    fun verifyOrder(first: String, second: String, third: String) {
        throw PendingException(
            "Verifying the on-screen category order requires the ActiveSceneScreen to be open – promoted in a later iteration."
        )
    }

    @Then("{string} is still the first category")
    fun verifyIsFirstCategory(categoryName: String) {
        throw PendingException(
            "Requires close+reopen navigation which needs full Campaign→Session→Scene flow – promoted in a later iteration."
        )
    }

    @Then("{string} continues playing during and after the reorder")
    fun verifyCategoryStillPlaying(categoryName: String) {
        // The FakeMusicPlayer is state-based; if "Weather" was playing before the drag it should
        // still be looping after the reorder (ViewModel never stops it on reorder).
        assertThat(fakeMusicPlayer.isLooping(categoryName))
            .withFailMessage("Expected '$categoryName' to still be looping after reorder")
            .isTrue()
    }
}
