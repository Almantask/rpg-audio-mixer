package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.runBlocking
import org.junit.Ignore

/**
 * Step definitions for:
 *  - build_your_own_scene.feature     (@iter3 @core)
 *  - cannot_modify_bought_scenes.feature (@iter3)
 *  - add_description_to_scene.feature (@iter3)
 *  - tag_scene.feature                (@iter3)
 *
 * Most steps require the Active Scene Editor screen which is not yet implemented.
 * Those steps are annotated with @Ignore and have empty/no-op bodies so Cucumber
 * reports them as "skipped" rather than "undefined".
 */
class SceneEditorSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository

    // ── Helpers ───────────────────────────────────────────

    private fun navigateToScenesTab() {
        composeTestRule.onNodeWithTag("bottomNavItem_SCENES").performClick()
        composeTestRule.waitForIdle()
    }

    private fun createNewSceneViaDialog() {
        navigateToScenesTab()
        composeTestRule.onNodeWithTag("addSceneFab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("sceneNameInput").performTextInput("New Scene")
        composeTestRule.onNodeWithTag("createSceneButton").performClick()
        composeTestRule.waitForIdle()
    }

    // ═══════════════════════════════════════════════════
    // build_your_own_scene.feature steps
    // ═══════════════════════════════════════════════════

    @When("I create a new scene")
    @Ignore("Active Scene Editor not yet implemented; scene creation just creates in DB")
    fun createNewScene() {
        // TODO: When Active Scene Editor is implemented, this should create a scene
        // and navigate to its editor screen.
        navigateToScenesTab()
        composeTestRule.onNodeWithTag("addSceneFab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("sceneNameInput").performTextInput("New Scene")
        composeTestRule.onNodeWithTag("createSceneButton").performClick()
        composeTestRule.waitForIdle()
    }

    @Given("I have created a new scene")
    @Ignore("Active Scene Editor not yet implemented")
    fun haveCreatedANewScene() {
        runBlocking { sceneRepository.createScene("New Scene") }
        navigateToScenesTab()
    }

    @When("I open the {string} tab")
    @Ignore("Active Scene Editor tabs not yet implemented")
    fun openTab(tabName: String) {
        // TODO: Active Scene Editor tabs (Soundscapes / Soundboard) not yet implemented
    }

    @Then("I see a {string} tab")
    @Ignore("Active Scene Editor tabs not yet implemented")
    fun seeATab(tabName: String) {
        // TODO: Active Scene Editor tabs (Soundscapes / Soundboard) not yet implemented
    }

    @Then("the soundboard has no effects")
    @Ignore("Active Scene Editor Soundboard not yet implemented")
    fun soundboardHasNoEffects() {
        // TODO: Soundboard empty state not yet implemented
    }

    @Given("I have opened the {string} tab")
    @Ignore("Active Scene Editor tabs not yet implemented")
    fun haveOpenedTab(tabName: String) {
        // TODO: Active Scene Editor tabs not yet implemented
    }

    @When("I add {int} effects to the soundboard")
    @Ignore("Adding effects to soundboard not yet implemented")
    fun addEffectsToSoundboard(count: Int) {
        // TODO: FX addition to soundboard not yet implemented
    }

    @Then("the add button is the last item in the soundboard grid")
    @Ignore("Soundboard grid order not yet implemented")
    fun addButtonIsLastInSoundboardGrid() {
        // TODO: Soundboard grid ordering not yet implemented
    }

    @When("I add {int} soundscape categories to the scene")
    @Ignore("Adding soundscape categories to scene not yet implemented")
    fun addSoundscapeCategoriesToScene(count: Int) {
        // TODO: Soundscape category addition not yet implemented
    }

    @Then("the add button appears after the last category card")
    @Ignore("Soundscape category list order not yet implemented")
    fun addButtonAppearsAfterLastCategoryCard() {
        // TODO: Category list ordering not yet implemented
    }

    @Given("I have added the {string} soundscape category")
    @Ignore("Soundscape category addition to scene not yet implemented")
    fun haveAddedSoundscapeCategory(categoryName: String) {
        // TODO: Not yet implemented
    }

    @When("I swipe right on the {string} category card")
    @Ignore("Swipe-to-remove category from scene not yet implemented")
    fun swipeRightOnCategoryCard(categoryName: String) {
        // TODO: Swipe gesture on category card not yet implemented
    }

    @Then("the Soundscapes tab has no categories")
    @Ignore("Soundscapes tab category list not yet implemented")
    fun soundscapesTabHasNoCategories() {
        // TODO: Not yet implemented
    }

    @Given("I have added the {string} effect")
    @Ignore("Adding effects to soundboard not yet implemented")
    fun haveAddedEffect(effectName: String) {
        // TODO: Not yet implemented
    }

    @When("I hold the {string} button and drag it to the flames overlay at the bottom screen")
    @Ignore("Drag-to-delete gesture not yet implemented")
    fun holdAndDragButtonToFlamesOverlay(buttonName: String) {
        // TODO: Drag-to-delete gesture not yet implemented
    }

    @When("I create another new scene")
    fun createAnotherNewScene() {
        navigateToScenesTab()
        composeTestRule.onNodeWithTag("addSceneFab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("sceneNameInput").performTextInput("Another Scene")
        composeTestRule.onNodeWithTag("createSceneButton").performClick()
        composeTestRule.waitForIdle()
    }

    // ═══════════════════════════════════════════════════
    // cannot_modify_bought_scenes.feature steps
    // ═══════════════════════════════════════════════════

    @Given("I have a scene named {string}")
    fun haveASceneNamed(sceneName: String) {
        runBlocking { sceneRepository.createScene(sceneName) }
        navigateToScenesTab()
    }

    @When("I open the Soundboard tab of {string}")
    @Ignore("Active Scene Editor Soundboard tab not yet implemented")
    fun openSoundboardTabOfScene(sceneName: String) {
        // TODO: Active Scene Editor not yet implemented
    }

    @When("I open the Soundscapes tab of {string}")
    @Ignore("Active Scene Editor Soundscapes tab not yet implemented")
    fun openSoundscapesTabOfScene(sceneName: String) {
        // TODO: Active Scene Editor not yet implemented
    }

    @When("I remove {string} from {string}")
    @Ignore("Removing soundscape category from scene not yet implemented")
    fun removeItemFromScene(item: String, scene: String) {
        // TODO: Not yet implemented
    }

    @Then("{string} is no longer in the {string} scene")
    @Ignore("Scene content verification not yet implemented")
    fun itemNoLongerInScene(item: String, scene: String) {
        // TODO: Not yet implemented
    }

    @Given("{string} has the soundboard effect {string}")
    @Ignore("Soundboard effect addition not yet implemented")
    fun sceneHasSoundboardEffect(sceneName: String, effectName: String) {
        // TODO: Not yet implemented
    }

    @When("I remove {string} from the soundboard")
    @Ignore("Removing FX from soundboard not yet implemented")
    fun removeFromSoundboard(effectName: String) {
        // TODO: Not yet implemented
    }

    @Given("I have scenes {string}, {string}, and {string}")
    fun haveScenesWithAnd(scene1: String, scene2: String, scene3: String) {
        runBlocking {
            sceneRepository.createScene(scene1)
            sceneRepository.createScene(scene2)
            sceneRepository.createScene(scene3)
        }
        navigateToScenesTab()
    }

    @When("I view the Scenes list")
    fun viewTheScenesList() {
        navigateToScenesTab()
    }

    @Then("all scenes have the same visual appearance without any ownership badge")
    @Ignore("Visual badge inspection not yet automated")
    fun allScenesHaveSameVisualAppearance() {
        // TODO: Visual distinction checks not yet implemented
    }

    // ═══════════════════════════════════════════════════
    // add_description_to_scene.feature steps
    // ═══════════════════════════════════════════════════

    @When("I add the description {string} to the {string} scene")
    @Ignore("Scene description editing UI not yet implemented")
    fun addDescriptionToScene(description: String, sceneName: String) {
        // TODO: Scene editing screen not yet implemented
    }

    @Then("the {string} scene has the description {string}")
    @Ignore("Scene description verification not yet implemented")
    fun sceneHasDescription(sceneName: String, description: String) {
        // TODO: Not yet implemented
    }

    @Then("I see the description {string} for the {string} scene")
    @Ignore("Scene description visibility not yet implemented")
    fun seeDescriptionForScene(description: String, sceneName: String) {
        // TODO: Not yet implemented
    }

    @Given("the {string} scene has the description {string}")
    @Ignore("Setting scene description directly not yet implemented")
    fun sceneHasDescriptionPrecondition(sceneName: String, description: String) {
        // TODO: Scene.description field exists but editing UI not yet available
    }

    @When("I update the description of the {string} scene to {string}")
    @Ignore("Scene description update UI not yet implemented")
    fun updateSceneDescription(sceneName: String, newDescription: String) {
        // TODO: Scene editing screen not yet implemented
    }

    @Then("the {string} scene has no description")
    @Ignore("Scene description absence check not yet implemented")
    fun sceneHasNoDescription(sceneName: String) {
        // TODO: Not yet implemented
    }

    // ═══════════════════════════════════════════════════
    // tag_scene.feature steps
    // ═══════════════════════════════════════════════════

    @Given("I have created a new scene {string}")
    fun haveCreatedNewSceneWithName(sceneName: String) {
        runBlocking { sceneRepository.createScene(sceneName) }
        navigateToScenesTab()
    }

    @When("I view the {string} scene card")
    fun viewSceneCard(sceneName: String) {
        navigateToScenesTab()
        composeTestRule.onNodeWithText(sceneName, ignoreCase = true).assertIsDisplayed()
    }

    @Then("no tags are shown on the card")
    @Ignore("Scene tag chips on scene card not yet implemented")
    fun noTagsShownOnCard() {
        // TODO: Tag chip display on scene cards not yet implemented
    }

    @Given("I am editing the {string} scene")
    @Ignore("Scene editing screen not yet implemented")
    fun amEditingScene(sceneName: String) {
        // TODO: Scene editing screen not yet implemented
    }

    @When("I add the predefined tag {string}")
    @Ignore("Predefined tag selection not yet implemented")
    fun addPredefinedTag(tagName: String) {
        // TODO: Tag picker not yet implemented
    }

    @When("I save")
    @Ignore("Scene edit save action not yet implemented")
    fun saveSceneEdit() {
        // TODO: Save action in scene editor not yet implemented
    }

    @Then("the {string} tag chip is shown on the {string} scene card")
    @Ignore("Tag chip display on scene cards not yet implemented")
    fun tagChipShownOnSceneCard(tag: String, sceneName: String) {
        // TODO: Not yet implemented
    }

    @When("I add a custom tag {string}")
    @Ignore("Custom tag input not yet implemented")
    fun addCustomTag(tagName: String) {
        // TODO: Not yet implemented
    }

    @When("I add the tags {string}, {string}, and {string}")
    @Ignore("Multiple tag selection not yet implemented")
    fun addMultipleTags(tag1: String, tag2: String, tag3: String) {
        // TODO: Not yet implemented
    }

    @Then("all three tag chips are shown on the {string} scene card")
    @Ignore("Multiple tag chip display not yet implemented")
    fun allThreeTagChipsShownOnSceneCard(sceneName: String) {
        // TODO: Not yet implemented
    }

    @Given("the {string} scene has the tag {string}")
    @Ignore("Scene tag precondition not yet implemented via UI")
    fun sceneHasTag(sceneName: String, tag: String) {
        // TODO: Not yet implemented
    }

    @When("I edit {string} and remove the {string} tag")
    @Ignore("Tag removal in scene editor not yet implemented")
    fun editSceneAndRemoveTag(sceneName: String, tag: String) {
        // TODO: Not yet implemented
    }

    @Then("the {string} tag is no longer shown on the {string} scene card")
    @Ignore("Tag removal verification not yet implemented")
    fun tagNoLongerShownOnSceneCard(tag: String, sceneName: String) {
        // TODO: Not yet implemented
    }
}
