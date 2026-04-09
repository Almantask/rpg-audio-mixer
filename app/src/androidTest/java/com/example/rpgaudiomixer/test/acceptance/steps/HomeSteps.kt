package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.home.HomeTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking

class HomeSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    init {
        runBlocking {
            entryPoint().campaignRepository().clearAll()
            entryPoint().sessionRepository().clearAll()
            entryPoint().sceneRepository().clearAll()
            entryPoint().soundscapeRepository().clearAll()
            entryPoint().fxRepository().clearAll()
        }
    }

    @Given("the last opened scene in the active campaign was {string}")
    fun theLastOpenedSceneInTheActiveCampaignWas(sceneName: String) {
        runBlocking {
            val campaignId = entryPoint().campaignRepository().upsertCampaign(
                Campaign(name = "Curse of Strahd", lastPlayedAt = 5_000L),
            )
            val sessionId = entryPoint().sessionRepository().upsertSession(
                Session(campaignId = campaignId, name = "Session 1", dateMillis = 2_000L),
            )
            val sceneId = entryPoint().sceneRepository().upsertScene(
                Scene(name = sceneName, description = "A foreboding threshold."),
            )
            entryPoint().sessionRepository().linkScenes(sessionId, listOf(sceneId))
            entryPoint().sessionRepository().markSceneOpened(sessionId, sceneId)
        }
    }

    @Given("{string} is shown in the Resume Journey card")
    fun isShownInTheResumeJourneyCard(sceneName: String) {
        theLastOpenedSceneInTheActiveCampaignWas(sceneName)
        iOpenTheHomeScreen()
    }

    @Given("{string} is the most played loopable track globally")
    fun isTheMostPlayedLoopableTrackGlobally(trackName: String) {
        runBlocking {
            val categoryId = entryPoint().soundscapeRepository().createCategory("Tavern")
            entryPoint().soundscapeRepository().upsertTrack(
                SoundscapeTrack(
                    categoryId = categoryId,
                    name = trackName,
                    filePath = "demo://tavern/warmth",
                    playCount = 9,
                ),
            )
            entryPoint().soundscapeRepository().upsertTrack(
                SoundscapeTrack(
                    categoryId = categoryId,
                    name = "Quiet Hearth",
                    filePath = "demo://tavern/hearth",
                    playCount = 1,
                ),
            )
        }
    }

    @Given("{string} is the most played FX globally")
    fun isTheMostPlayedFxGlobally(trackName: String) {
        runBlocking {
            entryPoint().fxRepository().upsertTrack(
                FxTrack(
                    name = trackName,
                    filePath = "demo://fx/thunder",
                    tags = listOf("Storm"),
                    durationMs = 2_000L,
                    playCount = 12,
                ),
            )
            entryPoint().fxRepository().upsertTrack(
                FxTrack(
                    name = "Sword Clash",
                    filePath = "demo://fx/sword",
                    tags = listOf("Combat"),
                    durationMs = 1_000L,
                    playCount = 1,
                ),
            )
        }
    }

    @When("I tap {string} in the Resume Journey card")
    fun iTapInTheResumeJourneyCard(label: String) {
        composeRuleHolder.composeRule.onNodeWithTag(HomeTestTags.RESUME_CARD).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(label).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("I see {string} in the Resume Journey card")
    fun iSeeInTheResumeJourneyCard(sceneName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(HomeTestTags.RESUME_CARD).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(sceneName).assertIsDisplayed()
    }

    @Then("I see {string} in the Top Atmosphere card")
    fun iSeeInTheTopAtmosphereCard(trackName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(HomeTestTags.TOP_ATMOSPHERE_CARD).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(trackName).assertIsDisplayed()
    }

    @Then("I see {string} in the Legendary Action card")
    fun iSeeInTheLegendaryActionCard(trackName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(HomeTestTags.LEGENDARY_ACTION_CARD).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(trackName).assertIsDisplayed()
    }

    @Then("the active campaign area shows a prompt to create a campaign")
    fun theActiveCampaignAreaShowsAPromptToCreateACampaign() {
        composeRuleHolder.composeRule.onNodeWithTag(HomeTestTags.ACTIVE_CAMPAIGN_EMPTY).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText("Create or open a campaign to enter your domain.").assertIsDisplayed()
    }

    @Then("the Resume Journey card is not shown")
    fun theResumeJourneyCardIsNotShown() {
        composeRuleHolder.composeRule.onNodeWithTag(HomeTestTags.RESUME_CARD).assertDoesNotExist()
        composeRuleHolder.composeRule.onNodeWithTag(HomeTestTags.RESUME_EMPTY).assertDoesNotExist()
    }

    private fun iOpenTheHomeScreen() {
        composeRuleHolder.composeRule.onNodeWithText("HOME").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(applicationContext, CampaignDataEntryPoint::class.java)
    }
}
