package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.scenes.ActiveSceneSoundscapesTestTags
import com.example.rpgaudiomixer.ui.scenes.ScenesTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class ActiveSceneSoundscapeSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private var currentSceneName: String = "Active Scene"
    private var currentSceneId: Long = 0L

    @Given("I am on the Active Scene — Soundscapes tab")
    fun iAmOnTheActiveSceneSoundscapesTab() {
        ensureLibraryCategory("Weather")
        openScene("Active Scene")
        composeRuleHolder.composeRule.onNodeWithText("Soundscapes").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("the Soundscape selection screen is open")
    fun theSoundscapeSelectionScreenIsOpen() {
        iAmOnTheActiveSceneSoundscapesTab()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.ADD_BUTTON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("my library has the category {string}")
    fun myLibraryHasTheCategory(categoryName: String) {
        ensureLibraryCategory(categoryName)
    }

    @Given("{string} is already in the current scene")
    fun isAlreadyInTheCurrentScene(categoryName: String) {
        ensureLibraryCategory(categoryName)
        iAmOnTheActiveSceneSoundscapesTab()
        runBlocking {
            entryPoint().sceneRepository().addSoundscapeCategory(currentSceneId, categoryName)
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("I have tapped + on {string} in the selection screen")
    fun iHaveTappedOnInTheSelectionScreen(categoryName: String) {
        theSoundscapeSelectionScreenIsOpen()
        ensureLibraryCategory(categoryName)
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.selectionAdd(categoryName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("I have added {string} and {string} from the selection screen")
    fun iHaveAddedAndFromTheSelectionScreen(first: String, second: String) {
        theSoundscapeSelectionScreenIsOpen()
        listOf(first, second).forEach { categoryName ->
            ensureLibraryCategory(categoryName)
            composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.selectionAdd(categoryName)).performClick()
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the + button on the {string} row")
    fun iTapTheButtonOnTheRow(categoryName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.selectionAdd(categoryName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I tap the already-added indicator on the {string} row")
    fun iTapTheAlreadyAddedIndicatorOnTheRow(categoryName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.selectionAdded(categoryName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I open the Soundscape selection screen")
    fun iOpenTheSoundscapeSelectionScreen() {
        iAmOnTheActiveSceneSoundscapesTab()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.ADD_BUTTON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I open the Soundscapes tab of {string}")
    fun iOpenTheSoundscapesTabOf(sceneName: String) {
        openScene(sceneName)
        composeRuleHolder.composeRule.onNodeWithText("Soundscapes").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I remove {string} from {string}")
    fun iRemoveFrom(categoryName: String, sceneName: String) {
        openScene(sceneName)
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.removeButton(categoryName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("I see the Soundscape category selection screen with a back arrow")
    fun iSeeTheSoundscapeCategorySelectionScreenWithABackArrow() {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.SELECTION_SHEET).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.SELECTION_BACK).assertIsDisplayed()
    }

    @Then("the {string} row displays a + button")
    fun theRowDisplaysAButton(categoryName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.selectionAdd(categoryName)).assertIsDisplayed()
    }

    @Then("{string} is instantly added to the active scene")
    fun isInstantlyAddedToTheActiveScene(categoryName: String) {
        val scene = runBlocking { entryPoint().sceneRepository().observeScene(currentSceneId).first() }
        assertThat(scene?.soundscapeCategoryNames).contains(categoryName)
    }

    @Then("I see the already-added indicator on the {string} row")
    @Then("the {string} row shows the already-added indicator instead of a + button")
    fun iSeeTheAlreadyAddedIndicatorOnTheRow(categoryName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.selectionAdded(categoryName)).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.selectionAdd(categoryName)).assertDoesNotExist()
    }

    @Then("{string} is added to the scene immediately without any confirmation dialog")
    fun isAddedToTheSceneImmediatelyWithoutAnyConfirmationDialog(categoryName: String) {
        isInstantlyAddedToTheActiveScene(categoryName)
        composeRuleHolder.composeRule.onNodeWithText("Confirm").assertDoesNotExist()
    }

    @Then("all three categories appear in the active scene's Soundscapes tab")
    fun allThreeCategoriesAppearInTheActiveSceneSoundscapesTab() {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.SELECTION_BACK).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        listOf("Weather", "Interior", "Monsters").forEach { categoryName ->
            composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.card(categoryName)).assertIsDisplayed()
        }
    }

    @Then("{string} is not duplicated in the scene")
    fun isNotDuplicatedInTheScene(categoryName: String) {
        val scene = runBlocking { entryPoint().sceneRepository().observeScene(currentSceneId).first() }
        assertThat(scene?.soundscapeCategoryNames.orEmpty().count { name -> name == categoryName }).isEqualTo(1)
    }

    @Then("I see the Active Scene — Soundscapes tab")
    fun iSeeTheActiveSceneSoundscapesTab() {
        composeRuleHolder.composeRule.onNodeWithText("Soundscapes").assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.ADD_BUTTON).assertIsDisplayed()
    }

    @And("both {string} and {string} are present as category cards")
    fun bothAndArePresentAsCategoryCards(first: String, second: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.card(first)).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithTag(ActiveSceneSoundscapesTestTags.card(second)).assertIsDisplayed()
    }

    @Then("{string} is no longer in the {string} scene")
    fun isNoLongerInTheScene(categoryName: String, sceneName: String) {
        val scene = runBlocking {
            entryPoint().sceneRepository().observeScenes().first().first { scene -> scene.name == sceneName }
        }
        assertThat(scene.soundscapeCategoryNames).doesNotContain(categoryName)
    }

    private fun openScene(sceneName: String) {
        ensureLibraryCategory("Weather")
        currentSceneId = runBlocking {
            entryPoint().sceneRepository().observeScenes().first().firstOrNull { it.name == sceneName }?.id
                ?: entryPoint().sceneRepository().upsertScene(Scene(name = sceneName))
        }
        currentSceneName = sceneName
        composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(ScenesTestTags.card(sceneName)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun ensureLibraryCategory(categoryName: String) {
        runBlocking {
            val repository = entryPoint().soundscapeRepository()
            val existing = repository.observeCategories().first().firstOrNull { category -> category.name == categoryName }
            if (existing == null) {
                val categoryId = repository.createCategory(categoryName)
                repository.upsertTrack(
                    SoundscapeTrack(
                        categoryId = categoryId,
                        name = "$categoryName Loop",
                        filePath = "demo://$categoryName/loop",
                        intensityLevel = IntensityLevel.I,
                        mixVolumePercent = 100,
                    ),
                )
            }
        }
    }

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(applicationContext, CampaignDataEntryPoint::class.java)
    }
}
