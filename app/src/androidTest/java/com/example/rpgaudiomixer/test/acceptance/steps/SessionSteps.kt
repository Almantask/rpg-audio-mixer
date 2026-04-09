package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.campaigns.CampaignsTestTags
import com.example.rpgaudiomixer.ui.sessions.SessionCoverArtSelectionRepository
import com.example.rpgaudiomixer.ui.sessions.SessionsTestTags
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class SessionSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private var currentCampaignName: String = ""
    private var currentCampaignId: Long = 0L
    private var currentSessionName: String = ""

    init {
        runBlocking {
            entryPoint().campaignRepository().clearAll()
            entryPoint().sessionRepository().clearAll()
            entryPoint().sceneRepository().clearAll()
        }
        entryPoint().sessionTrashRepository().reset()
        sessionCoverArtSelectionRepository().reset()
    }

    @Given("I have a campaign {string} with no sessions")
    fun iHaveACampaignWithNoSessions(name: String) {
        currentCampaignId = createCampaign(name)
        currentCampaignName = name
    }

    @Given("I have a campaign {string} with sessions")
    fun iHaveACampaignWithSessions(name: String, table: DataTable) {
        currentCampaignId = createCampaign(name)
        currentCampaignName = name
        val sessions = table.cells().flatten().filter(String::isNotBlank).map(String::trim)
        runBlocking {
            sessions.forEachIndexed { index, sessionName ->
                entryPoint().sessionRepository().upsertSession(
                    Session(
                        campaignId = currentCampaignId,
                        name = sessionName,
                        dateMillis = 1_000L + index,
                    ),
                )
            }
        }
    }

    @Given("I have sessions {string} dated last month and {string} dated today")
    fun iHaveSessionsDatedLastMonthAndToday(first: String, second: String) {
        currentCampaignId = createCampaign("Sorting Campaign")
        currentCampaignName = "Sorting Campaign"
        runBlocking {
            entryPoint().sessionRepository().upsertSession(
                Session(campaignId = currentCampaignId, name = first, dateMillis = 1_000L),
            )
            entryPoint().sessionRepository().upsertSession(
                Session(campaignId = currentCampaignId, name = second, dateMillis = 5_000L),
            )
        }
    }

    @Given("I am creating a session {string}")
    fun iAmCreatingASession(name: String) {
        currentCampaignId = createCampaign("Session Creation Campaign")
        currentCampaignName = "Session Creation Campaign"
        openCampaign(currentCampaignName)
        composeRuleHolder.composeRule.onNodeWithText("Add New Session").performClick()
        composeRuleHolder.composeRule.onNodeWithTag(SessionsTestTags.NAME_INPUT).performTextInput(name)
        currentSessionName = name
    }

    @Given("I have a campaign with a session {string}")
    fun iHaveACampaignWithASession(name: String) {
        currentCampaignId = createCampaign("Linked Campaign")
        currentCampaignName = "Linked Campaign"
        currentSessionName = name
        runBlocking {
            entryPoint().sessionRepository().upsertSession(
                Session(campaignId = currentCampaignId, name = name, dateMillis = 3_000L),
            )
        }
        openCampaign(currentCampaignName)
    }

    @Given("I have a session {string}")
    fun iHaveASession(name: String) {
        iHaveACampaignWithASession(name)
    }

    @When("I open {string}")
    fun iOpen(name: String) {
        if (name == currentCampaignName || composeRuleHolder.composeRule.onAllNodesWithTag(CampaignsTestTags.card(name))
                .fetchSemanticsNodes().isNotEmpty()
        ) {
            openCampaign(name)
            return
        }

        if (composeRuleHolder.composeRule.onAllNodesWithTag(SessionsTestTags.card(name)).fetchSemanticsNodes().isEmpty()) {
            val campaignName = runBlocking {
                entryPoint().campaignRepository().observeCampaigns().first().first().name
            }
            openCampaign(campaignName)
        }
        composeRuleHolder.composeRule.onNodeWithTag(SessionsTestTags.card(name)).performClick()
        currentSessionName = name
        composeRuleHolder.composeRule.waitForIdle()
    }

    @When("I enter the session name {string}")
    fun iEnterTheSessionName(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SessionsTestTags.NAME_INPUT).performTextInput(name)
        currentSessionName = name
    }

    @When("I view the sessions list")
    fun iViewTheSessionsList() {
        openCampaign(currentCampaignName)
    }

    @Then("I see {string} in the sessions list")
    fun iSeeInTheSessionsList(name: String) {
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("I see all three sessions in the list")
    fun iSeeAllThreeSessionsInTheList() {
        listOf(
            "Session 1 – The Dark Arrival",
            "Session 2 – Castle Ravenloft",
            "Session 3 – The Final Battle",
        ).forEach { sessionName ->
            composeRuleHolder.composeRule.onNodeWithText(sessionName).assertIsDisplayed()
        }
    }

    @Then("the selected photo is shown as the session's cover art")
    fun theSelectedPhotoIsShownAsTheSessionsCoverArt() {
        composeRuleHolder.composeRule.onNodeWithTag(SessionsTestTags.COVER_ART_PREVIEW).assertIsDisplayed()
    }

    @Then("I see the scene list for {string}")
    fun iSeeTheSceneListFor(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SessionScenesTestTags.SCREEN).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("it is no longer in the sessions list")
    fun itIsNoLongerInTheSessionsList() {
        val sessions = runBlocking { entryPoint().sessionRepository().observeSessions(currentCampaignId).first() }
        assertThat(sessions.map(Session::name)).doesNotContain(currentSessionName)
    }

    private fun openCampaign(name: String) {
        composeRuleHolder.composeRule.onNodeWithText("CAMPAIGNS").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(CampaignsTestTags.card(name)).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        currentCampaignName = name
        currentCampaignId = runBlocking {
            entryPoint().campaignRepository().observeCampaigns().first().first { it.name == name }.id
        }
    }

    private fun createCampaign(name: String): Long = runBlocking {
        entryPoint().campaignRepository().upsertCampaign(Campaign(name = name))
    }

    private fun sessionCoverArtSelectionRepository(): SessionCoverArtSelectionRepository =
        entryPoint().sessionCoverArtSelectionRepository()

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(
            applicationContext,
            CampaignDataEntryPoint::class.java,
        )
    }
}
