package com.example.rpgaudiomixer.test.acceptance.steps

import android.content.Context
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import com.example.rpgaudiomixer.app.components.ArcanumTopBarTestTags
import com.example.rpgaudiomixer.app.screens.TrashTestTags
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.test.acceptance.di.CampaignDataEntryPoint
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.ui.scenes.ScenesTestTags
import com.example.rpgaudiomixer.ui.sessions.SessionScenesTestTags
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.util.concurrent.TimeUnit

class TrashRecoverySteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private var standardItemName: String = "Standard Scene"
    private var cleanupReferenceTimeMillis: Long = 0L
    private var currentTrashItemName: String = ""

    init {
        runBlocking {
            entryPoint().campaignRepository().clearAll()
            entryPoint().sessionRepository().clearAll()
            entryPoint().sceneRepository().clearAll()
            entryPoint().soundscapeRepository().clearAll()
            entryPoint().fxRepository().clearAll()
        }
        entryPoint().trashVaultRepository().reset()
    }

    @Given("I have a standard item \\(Campaign, Session, Scene, Soundscape Category, or FX track)")
    fun iHaveAStandardItem() {
        standardItemName = "Standard Scene"
        runBlocking {
            entryPoint().sceneRepository().upsertScene(Scene(name = standardItemName))
        }
    }

    @When("I delete or swipe to remove the item")
    fun iDeleteOrSwipeToRemoveTheItem() {
        composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(ScenesTestTags.card(standardItemName)).performTouchInput {
            swipeRight()
        }
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("the item is moved to the {string} \\(Trash) screen")
    fun theItemIsMovedToTheTrashScreen(screenTitle: String) {
        openTrash()
        composeRuleHolder.composeRule.onNodeWithText(screenTitle).assertIsDisplayed()
        composeRuleHolder.composeRule.onNodeWithText(standardItemName).assertIsDisplayed()
    }

    @And("it becomes temporarily unavailable in the main app lists")
    fun itBecomesTemporarilyUnavailableInTheMainAppLists() {
        composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithText(standardItemName).assertDoesNotExist()
    }

    @And("no instant permanent deletion dialog is shown unless it's a destructive cascade")
    fun noInstantPermanentDeletionDialogIsShownUnlessItsADestructiveCascade() {
        composeRuleHolder.composeRule.onNodeWithText("Delete permanently").assertDoesNotExist()
    }

    @Given("I have deleted {string} \\(Soundscape), {string} \\(Scene), and {string} \\(FX)")
    fun iHaveDeletedItems(soundscapeName: String, sceneName: String, fxName: String) {
        runBlocking {
            val categoryId = entryPoint().soundscapeRepository().createCategory(soundscapeName)
            entryPoint().soundscapeRepository().upsertTrack(
                SoundscapeTrack(categoryId = categoryId, name = "$soundscapeName Loop", filePath = "demo://$soundscapeName"),
            )
            entryPoint().trashVaultRepository().trashSoundscapeCategory(categoryId)
            entryPoint().soundscapeRepository().deleteCategory(categoryId)

            val sceneId = entryPoint().sceneRepository().upsertScene(Scene(name = sceneName))
            entryPoint().trashVaultRepository().trashScene(sceneId)
            entryPoint().sceneRepository().deleteScene(sceneId)

            val fxId = entryPoint().fxRepository().upsertTrack(FxTrack(name = fxName, filePath = "demo://$fxName"))
            entryPoint().trashVaultRepository().trashFxTrack(fxId)
            entryPoint().fxRepository().deleteTrack(fxId)
        }
    }

    @When("I navigate to the {string} screen from Settings")
    fun iNavigateToTheScreenFromSettings(buttonLabel: String) {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.GEAR_ICON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithText(buttonLabel).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("I see a list containing {string}, {string}, and {string}")
    fun iSeeAListContaining(first: String, second: String, third: String) {
        listOf(first, second, third).forEach { name ->
            composeRuleHolder.composeRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @And("each item card displays how many days ago it was deleted")
    fun eachItemCardDisplaysHowManyDaysAgoItWasDeleted() {
        composeRuleHolder.composeRule.onNodeWithText("Deleted 0 days ago").assertIsDisplayed()
    }

    @And("each card shows the item's original type \\(e.g. Soundscape, Scene, FX)")
    fun eachCardShowsTheItemsOriginalType() {
        listOf("Soundscape", "Scene", "FX").forEach { type ->
            composeRuleHolder.composeRule.onNodeWithText(type).assertIsDisplayed()
        }
    }

    @Given("{string} \\(Scene) is in the Trash")
    fun sceneIsInTheTrash(sceneName: String) {
        runBlocking {
            val campaignId = entryPoint().campaignRepository().upsertCampaign(Campaign(name = "Trash Campaign"))
            val sessionId = entryPoint().sessionRepository().upsertSession(Session(campaignId = campaignId, name = "Linked Session"))
            val sceneId = entryPoint().sceneRepository().upsertScene(Scene(name = sceneName))
            entryPoint().sessionRepository().linkScenes(sessionId, listOf(sceneId))
            entryPoint().trashVaultRepository().trashScene(sceneId)
            entryPoint().sceneRepository().deleteScene(sceneId)
        }
        openTrash()
    }

    @When("I tap the {string} button on the {string} card")
    fun iTapTheButtonOnTheCard(buttonLabel: String, itemName: String) {
        val tag = if (buttonLabel == "Restore") {
            TrashTestTags.restoreButton(itemName)
        } else {
            TrashTestTags.deleteButton(itemName)
        }
        composeRuleHolder.composeRule.onNodeWithTag(tag).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("{string} is removed from the Trash")
    fun isRemovedFromTheTrash(itemName: String) {
        composeRuleHolder.composeRule.onNodeWithTag(TrashTestTags.card(itemName)).assertDoesNotExist()
        assertThat(runBlocking { entryPoint().trashVaultRepository().observeItems().first().map { it.name } })
            .doesNotContain(itemName)
    }

    @And("it reappears exactly as it was in my Scenes list and any linked Sessions")
    fun itReappearsExactlyAsItWasInMyScenesListAndAnyLinkedSessions() {
        composeRuleHolder.composeRule.onNodeWithText("SCENES").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithText("Cursed Catacombs").assertIsDisplayed()

        composeRuleHolder.composeRule.onNodeWithText("CAMPAIGNS").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithText("Trash Campaign").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithText("Linked Session").performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithTag(SessionScenesTestTags.card("Cursed Catacombs")).assertIsDisplayed()
    }

    @Given("{string} is in the Trash")
    fun itemIsInTheTrash(itemName: String) {
        currentTrashItemName = itemName
        runBlocking {
            val fxId = entryPoint().fxRepository().upsertTrack(FxTrack(name = itemName, filePath = "demo://$itemName"))
            entryPoint().trashVaultRepository().trashFxTrack(fxId)
            entryPoint().fxRepository().deleteTrack(fxId)
        }
        openTrash()
    }

    @When("I tap the {string} button on its card in the Trash")
    fun iTapTheButtonOnItsCardInTheTrash(buttonLabel: String) {
        val tag = if (buttonLabel == "Delete") {
            TrashTestTags.deleteButton(currentTrashItemName)
        } else {
            TrashTestTags.restoreButton(currentTrashItemName)
        }
        composeRuleHolder.composeRule.onNodeWithTag(tag).performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("{string} is permanently deleted from the app")
    fun isPermanentlyDeletedFromTheApp(itemName: String) {
        assertThat(runBlocking { entryPoint().fxRepository().observeTracks().first().map { it.name } })
            .doesNotContain(itemName)
    }

    @And("it can no longer be restored")
    fun itCanNoLongerBeRestored() {
        assertThat(runBlocking { entryPoint().trashVaultRepository().observeItems().first() }).isEmpty()
    }

    @Given("{string} was moved to the Trash 7 days ago")
    fun wasMovedToTheTrash7DaysAgo(itemName: String) {
        currentTrashItemName = itemName
        cleanupReferenceTimeMillis = System.currentTimeMillis()
        runBlocking {
            val categoryId = entryPoint().soundscapeRepository().createCategory(itemName)
            entryPoint().soundscapeRepository().upsertTrack(
                SoundscapeTrack(categoryId = categoryId, name = "$itemName Loop", filePath = "demo://$itemName"),
            )
            entryPoint().trashVaultRepository().trashSoundscapeCategory(categoryId)
            entryPoint().soundscapeRepository().deleteCategory(categoryId)
        }
    }

    @When("the app runs its background cleanup or is launched")
    fun theAppRunsItsBackgroundCleanupOrIsLaunched() {
        runBlocking {
            entryPoint().trashVaultRepository().purgeExpired(
                nowMillis = cleanupReferenceTimeMillis + TimeUnit.DAYS.toMillis(7),
            )
        }
        openTrash()
    }

    @Then("{string} is permanently deleted")
    fun isPermanentlyDeleted(itemName: String) {
        assertThat(runBlocking { entryPoint().trashVaultRepository().observeItems().first().map { it.name } })
            .doesNotContain(itemName)
    }

    @And("it no longer appears in the Trash list")
    fun itNoLongerAppearsInTheTrashList() {
        composeRuleHolder.composeRule.onNodeWithText(currentTrashItemName).assertDoesNotExist()
    }

    @Given("the Trash contains multiple items")
    fun theTrashContainsMultipleItems() {
        runBlocking {
            val sceneId = entryPoint().sceneRepository().upsertScene(Scene(name = "Vault Scene"))
            entryPoint().trashVaultRepository().trashScene(sceneId)
            entryPoint().sceneRepository().deleteScene(sceneId)

            val fxId = entryPoint().fxRepository().upsertTrack(FxTrack(name = "Vault FX", filePath = "demo://vault_fx"))
            entryPoint().trashVaultRepository().trashFxTrack(fxId)
            entryPoint().fxRepository().deleteTrack(fxId)
        }
        openTrash()
    }

    @And("I confirm the destructive action")
    fun iConfirmTheDestructiveAction() {
        composeRuleHolder.composeRule.onNodeWithText("Confirm").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("all items in the Trash are permanently deleted")
    fun allItemsInTheTrashArePermanentlyDeleted() {
        assertThat(runBlocking { entryPoint().trashVaultRepository().observeItems().first() }).isEmpty()
    }

    @And("the Trash screen shows an empty state illustration")
    fun theTrashScreenShowsAnEmptyStateIllustration() {
        composeRuleHolder.composeRule.onNodeWithTag(TrashTestTags.EMPTY_STATE).assertIsDisplayed()
    }

    private fun openTrash() {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.GEAR_ICON).performClick()
        composeRuleHolder.composeRule.waitForIdle()
        composeRuleHolder.composeRule.onNodeWithText("Restore Recent Deletes").performClick()
        composeRuleHolder.composeRule.waitForIdle()
    }

    private fun entryPoint(): CampaignDataEntryPoint {
        val applicationContext: Context = ApplicationProvider.getApplicationContext()
        return EntryPointAccessors.fromApplication(applicationContext, CampaignDataEntryPoint::class.java)
    }
}
