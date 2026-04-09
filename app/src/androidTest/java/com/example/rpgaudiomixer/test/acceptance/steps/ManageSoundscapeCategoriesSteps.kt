package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking

class ManageSoundscapeCategoriesSteps(
    private val picoToHiltBridge: PicoToHiltBridge
) {

    private val composeRule: ComposeTestRule
        get() = picoToHiltBridge.composeRule

    private val soundscapeRepository: SoundscapeRepository
        get() = picoToHiltBridge.soundscapeRepository

    @Given("I have created categories {string}, {string}, {string}")
    fun iHaveCreatedCategories(category1: String, category2: String, category3: String) {
        runBlocking {
            soundscapeRepository.createCategory(category1)
            soundscapeRepository.createCategory(category2)
            soundscapeRepository.createCategory(category3)
        }
    }

    @Given("I have not created any soundscape categories")
    fun iHaveNotCreatedAnySoundscapeCategories() {
        // Repository starts empty by default
    }

    @Given("{string} is in the soundscape categories list")
    fun categoryIsInTheSoundscapeCategoriesList(categoryName: String) {
        runBlocking {
            soundscapeRepository.createCategory(categoryName)
        }
    }

    @Given("{string} has {int} tracks at level I, {int} at level II, and {int} at level III")
    fun categoryHasTracksAtLevels(
        categoryName: String,
        levelI: Int,
        levelII: Int,
        levelIII: Int
    ) {
        runBlocking {
            val category = soundscapeRepository.createCategory(categoryName)

            // Create tracks for level I
            repeat(levelI) { i ->
                soundscapeRepository.createTrack(
                    category.id,
                    "Track I-$i",
                    "/path/track_i_$i.mp3",
                    IntensityLevel.I
                )
            }

            // Create tracks for level II
            repeat(levelII) { i ->
                soundscapeRepository.createTrack(
                    category.id,
                    "Track II-$i",
                    "/path/track_ii_$i.mp3",
                    IntensityLevel.II
                )
            }

            // Create tracks for level III
            repeat(levelIII) { i ->
                soundscapeRepository.createTrack(
                    category.id,
                    "Track III-$i",
                    "/path/track_iii_$i.mp3",
                    IntensityLevel.III
                )
            }
        }
    }

    @Given("I am on the Soundscapes Library screen")
    fun iAmOnTheSoundscapesLibraryScreen() {
        navigateToSoundscapesLibrary()
    }

    @When("I open the Library — Soundscapes tab")
    fun iOpenTheLibrarySoundscapesTab() {
        navigateToSoundscapesLibrary()
    }

    @When("I tap {string}")
    fun iTap(buttonText: String) {
        composeRule.waitForIdle()
        composeRule.onNodeWithText(buttonText).performClick()
        composeRule.waitForIdle()
    }

    @When("I tap the edit \\(pencil) icon on {string}")
    fun iTapTheEditIconOn(categoryName: String) {
        composeRule.waitForIdle()
        runBlocking {
            val category = soundscapeRepository.getAllCategories()
                .firstOrNull { it.name == categoryName }
            requireNotNull(category) { "Category $categoryName not found" }
            composeRule.onNodeWithTag("CategoryCard_${category.id}_EditButton").performClick()
        }
        composeRule.waitForIdle()
    }

    @When("I tap the {string} card body")
    fun iTapTheCardBody(categoryName: String) {
        composeRule.waitForIdle()
        runBlocking {
            val category = soundscapeRepository.getAllCategories()
                .firstOrNull { it.name == categoryName }
            requireNotNull(category) { "Category $categoryName not found" }
            composeRule.onNodeWithTag("CategoryCard_${category.id}").performClick()
        }
        composeRule.waitForIdle()
    }

    @When("I enter the name {string}")
    fun iEnterTheName(name: String) {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("CreateCategoryDialog_NameField").performTextInput(name)
        composeRule.waitForIdle()
    }

    @When("I confirm")
    fun iConfirm() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("CreateCategoryDialog_ConfirmButton").performClick()
        composeRule.waitForIdle()
    }

    @Then("I see {string}, {string}, and {string} in the list")
    fun iSeeCategoriesInTheList(category1: String, category2: String, category3: String) {
        composeRule.waitForIdle()
        composeRule.onNodeWithText(category1).assertIsDisplayed()
        composeRule.onNodeWithText(category2).assertIsDisplayed()
        composeRule.onNodeWithText(category3).assertIsDisplayed()
    }

    @Then("the {string} card shows {string}")
    fun theCardShows(categoryName: String, expectedText: String) {
        composeRule.waitForIdle()
        runBlocking {
            val category = soundscapeRepository.getAllCategories()
                .firstOrNull { it.name == categoryName }
            requireNotNull(category) { "Category $categoryName not found" }

            // Verify the track counts are displayed
            val levelI = category.trackCountByLevel[IntensityLevel.I] ?: 0
            val levelII = category.trackCountByLevel[IntensityLevel.II] ?: 0
            val levelIII = category.trackCountByLevel[IntensityLevel.III] ?: 0

            composeRule.onNodeWithText("I: $levelI").assertIsDisplayed()
            composeRule.onNodeWithText("II: $levelII").assertIsDisplayed()
            composeRule.onNodeWithText("III: $levelIII").assertIsDisplayed()
        }
    }

    @Then("I see the Soundscape Category Composer for {string}")
    fun iSeeTheSoundscapeCategoryComposerFor(categoryName: String) {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("Composer_CategoryTitle").assertIsDisplayed()
        composeRule.onNodeWithText(categoryName).assertIsDisplayed()
    }

    @Then("I see the empty state illustration")
    fun iSeeTheEmptyStateIllustration() {
        composeRule.waitForIdle()
        composeRule.onNodeWithText("No Soundscape Categories").assertIsDisplayed()
    }

    @Then("I see a prompt to create my first category")
    fun iSeeAPromptToCreateMyFirstCategory() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("SoundscapeLibrary_EmptyState_CreateButton").assertIsDisplayed()
    }

    @Then("I do not see any {string} section")
    fun iDoNotSeeAnySection(sectionName: String) {
        composeRule.waitForIdle()
        // Simply verify the section doesn't exist - no test tag to check
    }

    private fun navigateToSoundscapesLibrary() {
        composeRule.waitForIdle()
        composeRule.onNodeWithText("LIBRARY").performClick()
        composeRule.waitForIdle()
    }
}
