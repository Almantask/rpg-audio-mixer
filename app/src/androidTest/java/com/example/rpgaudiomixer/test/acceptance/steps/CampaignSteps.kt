package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.campaigns.CampaignCoverArtSelectionRepository
import com.example.rpgaudiomixer.ui.campaigns.CampaignsTestTags
import com.example.rpgaudiomixer.ui.fx.FxLibraryTestTags
import com.example.rpgaudiomixer.ui.scenes.ScenesTestTags
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeLibraryTestTags
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

class CampaignSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {

    init {
        runBlocking {
            campaignRepository().clearAll()
        }
        campaignTrashRepository().reset()
        coverArtSelectionRepository().reset()
        sessionCoverArtSelectionRepository().reset()
    }

    @When("I enter the name {string}")
    fun iEnterTheName(name: String) {
        val inputTag = listOf(
            CampaignsTestTags.NAME_INPUT,
            SoundscapeLibraryTestTags.NAME_INPUT,
        ).first { tag ->
            composeRuleHolder.composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
        composeRuleHolder.composeRule.onNodeWithTag(inputTag).performTextInput(name)
    }

    @When("I confirm creation")
    fun iConfirmCreation() {
        composeRuleHolder.composeRule.onNodeWithText("Create").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Given("I have no campaigns")
    fun iHaveNoCampaigns() {
        runBlocking {
            campaignRepository().clearAll()
        }
    }

    @When("I open the Campaigns screen")
    fun iOpenTheCampaignsScreen() {
        composeRuleHolder.composeRule.onNodeWithText("CAMPAIGNS").performClick()
    }

    @When("I open the Home screen")
    fun iOpenTheHomeScreen() {
        composeRuleHolder.composeRule.onNodeWithText("HOME").performClick()
    }

    @Then("I see {string} in my campaigns list")
    fun iSeeInMyCampaignsList(name: String) {
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Then("I see the empty state illustration")
    fun iSeeTheEmptyStateIllustration() {
        listOf(
            CampaignsTestTags.EMPTY_ILLUSTRATION,
            SoundscapeLibraryTestTags.EMPTY_ILLUSTRATION,
            FxLibraryTestTags.EMPTY_ILLUSTRATION,
        ).firstOrNull { tag ->
            composeRuleHolder.composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }?.let { visibleTag ->
            composeRuleHolder.composeRule.onNodeWithTag(visibleTag).assertIsDisplayed()
        } ?: error("No known empty-state illustration is visible.")
    }

    @Then("I see a {string} button")
    @Then("I see an {string} button")
    fun iSeeAButton(label: String) {
        composeRuleHolder.composeRule.onNodeWithText(label).assertIsDisplayed()
    }

    @Given("I have created campaigns named")
    fun iHaveCreatedCampaignsNamed(table: DataTable) {
        val campaigns = table.cells().flatten().filter { it.isNotBlank() }
        require(campaigns.isNotEmpty()) {
            "Campaign table must contain at least one non-blank campaign name."
        }

        runBlocking {
            campaignRepository().clearAll()
            campaigns.forEachIndexed { index, name ->
                campaignRepository().upsertCampaign(
                    Campaign(
                        name = name.trim(),
                        lastPlayedAt = 1_000L + index,
                    ),
                )
            }
        }
    }

    @Then("I see all three campaigns in the list")
    fun iSeeAllThreeCampaignsInTheList() {
        listOf("The Shattered Throne", "Curse of Strahd", "The Wild Beyond").forEach { name ->
            composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @Given("I have campaigns {string} and {string}")
    fun iHaveCampaigns(oldCampaign: String, newCampaign: String) {
        runBlocking {
            campaignRepository().clearAll()
            campaignRepository().upsertCampaign(Campaign(name = oldCampaign, lastPlayedAt = 100L))
            campaignRepository().upsertCampaign(Campaign(name = newCampaign, lastPlayedAt = 200L))
        }
    }

    @Given("{string} was played more recently")
    fun wasPlayedMoreRecently(name: String) {
        runBlocking {
            val campaigns = campaignRepository().observeCampaigns().first()
            campaigns.forEach { campaign ->
                val updatedTimestamp = if (campaign.name == name) 5_000L else 1_000L
                campaignRepository().upsertCampaign(campaign.copy(lastPlayedAt = updatedTimestamp))
            }
        }
    }

    @Then("{string} appears above {string}")
    fun appearsAbove(firstCampaign: String, secondCampaign: String) {
        val campaigns = runBlocking { campaignRepository().observeCampaigns().first() }
        val campaignNames = campaigns.map(Campaign::name)
        if (campaignNames.contains(firstCampaign) && campaignNames.contains(secondCampaign)) {
            assertThat(campaignNames).containsSubsequence(firstCampaign, secondCampaign)
            return
        }

        val sessionNames = runBlocking {
            campaigns.flatMap { campaign ->
                entryPoint().sessionRepository().observeSessions(campaign.id).first().map { session -> session.name }
            }
        }
        assertThat(sessionNames).containsSubsequence(firstCampaign, secondCampaign)
    }

    @Given("I have played {string} most recently")
    fun iHavePlayedMostRecently(name: String) {
        runBlocking {
            campaignRepository().clearAll()
            campaignRepository().upsertCampaign(Campaign(name = "Fallback Campaign", lastPlayedAt = 50L))
            campaignRepository().upsertCampaign(Campaign(name = name, lastPlayedAt = 500L))
        }
    }

    @Then("{string} is shown as the active campaign")
    fun isShownAsTheActiveCampaign(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.HOME).assertTextContains(name)
    }

    @Given("I have a campaign {string} with at least one session")
    fun iHaveACampaignWithAtLeastOneSession(name: String) {
        runBlocking {
            campaignRepository().clearAll()
            campaignRepository().upsertCampaign(Campaign(name = name, lastPlayedAt = 999L))
        }
    }

    @When("I tap {string} on {string}")
    fun iTapOn(label: String, name: String) {
        if (label == "Resume") {
            composeRuleHolder.composeRule
                .onNodeWithTag(CampaignsTestTags.resumeButton(name))
                .performClick()
        } else {
            composeRuleHolder.composeRule.onNodeWithText(label).performClick()
        }
    }

    @Then("I see the sessions list for {string}")
    fun iSeeTheSessionsListFor(name: String) {
        composeRuleHolder.composeRule.onNodeWithTag(SessionsTestTags.SCREEN).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Given("I am creating a new campaign {string}")
    fun iAmCreatingANewCampaign(name: String) {
        iOpenTheCampaignsScreen()
        composeRuleHolder.composeRule.onNodeWithText("New Campaign").performClick()
        iEnterTheName(name)
    }

    @When("I tap the cover art area")
    fun iTapTheCoverArtArea() {
        composeRuleHolder.composeRule.onNodeWithTag(CampaignsTestTags.COVER_ART_PICKER).performClick()
    }

    @When("I select a photo from the device's photo library")
    fun iSelectAPhotoFromTheDevicesPhotoLibrary() {
        coverArtSelectionRepository().updateSelectedCoverArt("content://test/cover-art.jpg")
        sessionCoverArtSelectionRepository().updateSelectedCoverArt("content://test/cover-art.jpg")
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the selected photo is shown as the campaign's cover art")
    fun theSelectedPhotoIsShownAsTheCampaignSCoverArt() {
        composeRuleHolder.composeRule.onNodeWithTag(CampaignsTestTags.COVER_ART_PREVIEW).assertIsDisplayed()
    }

    @Given("I have a campaign {string}")
    fun iHaveACampaign(name: String) {
        runBlocking {
            campaignRepository().clearAll()
            campaignRepository().upsertCampaign(Campaign(name = name, lastPlayedAt = 700L))
        }
        iOpenTheCampaignsScreen()
    }

    @When("I swipe right on the {string} card")
    fun iSwipeRightOnTheCard(name: String) {
        var existingTag = listOf(
            CampaignsTestTags.card(name),
            SessionsTestTags.card(name),
            ScenesTestTags.card(name),
            SessionScenesTestTags.card(name),
            SoundscapeLibraryTestTags.card(name),
            FxLibraryTestTags.row(name),
        ).firstOrNull { tag ->
            composeRuleHolder.composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }

        if (existingTag == null) {
            val sceneExists = runBlocking {
                entryPoint().sceneRepository().observeScenes().first().any { scene -> scene.name == name }
            }
            val sessionExists = runBlocking {
                entryPoint().campaignRepository().observeCampaigns().first().any { campaign ->
                    entryPoint().sessionRepository().observeSessions(campaign.id).first().any { session -> session.name == name }
                }
            }
            val soundscapeExists = runBlocking {
                entryPoint().soundscapeRepository().observeCategories().first().any { category -> category.name == name }
            }
            val fxExists = runBlocking {
                entryPoint().fxRepository().observeTracks().first().any { track -> track.name == name }
            }

            when {
                fxExists -> {
                    composeRuleHolder.composeRule.onNodeWithText("LIBRARY").performClick()
                    composeRuleHolder.composeRule.waitForIdle()
                    composeRuleHolder.composeRule.onNodeWithText("Sound Effects").performClick()
                    composeRuleHolder.composeRule.waitForIdle()
                    existingTag = FxLibraryTestTags.row(name)
                }

                soundscapeExists -> {
                    composeRuleHolder.composeRule.onNodeWithText("LIBRARY").performClick()
                    composeRuleHolder.composeRule.waitForIdle()
                    existingTag = SoundscapeLibraryTestTags.card(name)
                }

                sceneExists -> {
                    composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
                    composeRuleHolder.composeRule.waitForIdle()
                    existingTag = ScenesTestTags.card(name)
                }

                sessionExists -> {
                    composeRuleHolder.composeRule.onNodeWithText("CAMPAIGNS").performClick()
                    composeRuleHolder.composeRule.waitForIdle()
                    if (composeRuleHolder.composeRule.onAllNodesWithTag(SessionsTestTags.card(name))
                            .fetchSemanticsNodes().isEmpty()
                    ) {
                        val firstCampaignName = runBlocking {
                            entryPoint().campaignRepository().observeCampaigns().first().first().name
                        }
                        composeRuleHolder.composeRule.onNodeWithTag(CampaignsTestTags.card(firstCampaignName)).performClick()
                        composeRuleHolder.composeRule.waitForIdle()
                    }
                    existingTag = SessionsTestTags.card(name)
                }

                else -> {
                    composeRuleHolder.composeRule.onNodeWithText("CAMPAIGNS").performClick()
                    composeRuleHolder.composeRule.waitForIdle()
                    existingTag = CampaignsTestTags.card(name)
                }
            }
        }

        composeRuleHolder.composeRule.onNodeWithTag(requireNotNull(existingTag)).performTouchInput {
            swipeRight()
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("{string} is moved to the Trash")
    fun isMovedToTheTrash(name: String) {
        assertThat(
            campaignTrashRepository().containsDeletedCampaign(name) ||
                entryPoint().sessionTrashRepository().containsDeletedSession(name) ||
                entryPoint().sceneTrashRepository().containsDeletedScene(name) ||
                entryPoint().fxTrackTrashRepository().containsDeletedTrack(name) ||
                entryPoint().soundscapeCategoryTrashRepository().containsDeletedCategory(name),
        ).isTrue()
    }

    @Then("it is no longer in my campaigns list")
    fun itIsNoLongerInMyCampaignsList() {
        val campaigns = runBlocking { campaignRepository().observeCampaigns().first() }
        assertThat(campaigns).isEmpty()
    }

    private fun campaignRepository(): CampaignRepository = entryPoint().campaignRepository()

    private fun campaignTrashRepository(): CampaignTrashRepository = entryPoint().campaignTrashRepository()

    private fun coverArtSelectionRepository(): CampaignCoverArtSelectionRepository =
        entryPoint().campaignCoverArtSelectionRepository()

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
