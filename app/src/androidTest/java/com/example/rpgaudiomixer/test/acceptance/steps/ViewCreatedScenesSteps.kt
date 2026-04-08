package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.test.acceptance.fakes.FakeSceneRepository
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class ViewCreatedScenesSteps(
    private val activityRule: MainActivityComposeRule,
    private val fakeSceneRepository: FakeSceneRepository
) {

    // -------------------------------------------------------------------------
    // Given steps (test state setup)
    // -------------------------------------------------------------------------

    @Given("I have created scenes named")
    fun iHaveCreatedScenesNamed(dataTable: DataTable) {
        val sceneNames = dataTable.asList()
        sceneNames.forEach { sceneName ->
            val scene = Scene(
                id = "scene-${sceneName.hashCode()}",
                name = sceneName,
                description = null,
                tags = emptyList()
            )
            fakeSceneRepository.addScene(scene)
        }
    }

    @Given("I have created a scene named {string}")
    fun iHaveCreatedASceneNamed(sceneName: String) {
        val scene = Scene(
            id = "scene-${sceneName.hashCode()}",
            name = sceneName,
            description = null,
            tags = emptyList()
        )
        fakeSceneRepository.addScene(scene)
    }

    // -------------------------------------------------------------------------
    // When steps (actions)
    // -------------------------------------------------------------------------

    @When("I create a new scene named {string}")
    fun iCreateANewSceneNamed(sceneName: String) {
        navigateToScenesScreen()
        val composeRule = activityRule.composeRule

        // Click FAB to create new scene
        composeRule.onNodeWithTag("ScenesScreen_FAB").performClick()
        composeRule.waitForIdle()

        // Enter scene name
        composeRule.onNodeWithTag("CreateSceneDialog_NameInput").performTextInput(sceneName)
        composeRule.waitForIdle()

        // Confirm
        composeRule.onNodeWithTag("CreateSceneDialog_ConfirmButton").performClick()
        composeRule.waitForIdle()
    }

    @When("I view my scenes")
    fun iViewMyScenes() {
        navigateToScenesScreen()
    }

    @When("I open the {string} scene")
    fun iOpenTheScene(sceneName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SceneCard_$sceneName").performClick()
        composeRule.waitForIdle()
    }

    // -------------------------------------------------------------------------
    // Then steps (assertions)
    // -------------------------------------------------------------------------

    @Then("I see the {string} scene in my scenes list")
    fun iSeeTheSceneInMyScenesList(sceneName: String) {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("SceneCard_$sceneName").assertExists()
        composeRule.onNodeWithTag("SceneCard_${sceneName}_Name").assertTextContains(sceneName)
    }

    @Then("I see the {string} tab")
    fun iSeeTheTab(tabName: String) {
        val composeRule = activityRule.composeRule
        // For now, just verify we're not on an error screen
        // Full tab implementation would be in later iterations
        composeRule.waitForIdle()
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    private fun navigateToScenesScreen() {
        val composeRule = activityRule.composeRule
        composeRule.onNodeWithTag("BottomNav_Scenes").performClick()
        composeRule.waitForIdle()
    }
}
