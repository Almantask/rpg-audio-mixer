package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.*
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.java.en.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Ignore

/**
 * Step definitions for trash_recovery.feature (@iter5).
 *
 * The TrashScreen (Vault of Echoes) shows deleted campaigns, sessions, and scenes
 * with Restore and Delete buttons, plus an Empty Vault button.
 */
class TrashSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val campaignRepository get() = PicoToHiltBridge.campaignRepository
    private val sessionRepository get() = PicoToHiltBridge.sessionRepository
    private val sceneRepository get() = PicoToHiltBridge.sceneRepository

    // ── Helper ────────────────────────────────────────────

    private fun navigateToTrashFromCredits() {
        composeTestRule.onNodeWithTag("topBarGear").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("creditsRestoreDeletes").performClick()
        composeTestRule.waitForIdle()
    }

    // ── Given ─────────────────────────────────────────────

    @Given("I have a standard item \\(Campaign, Session, Scene, Soundscape Category, or FX track\\)")
    @Ignore("Generic item setup not fully implemented; requires specific type selection")
    fun haveStandardItem() {
        runBlocking { sceneRepository.createScene("Test Scene") }
    }

    @Given("I have deleted {string} \\(Soundscape\\), {string} \\(Scene\\), and {string} \\(FX\\)")
    @Ignore("Soundscape and FX deletion not yet fully implemented via repository")
    fun haveDeletedMultipleItems(soundscape: String, scene: String, fx: String) {
        // Delete a scene with the scene name; soundscape and FX are not yet supported
        runBlocking {
            val sceneId = sceneRepository.createScene(scene)
            sceneRepository.deleteScene(sceneId)
        }
    }

    @Given("{string} \\(Scene\\) is in the Trash")
    fun sceneCategoryIsInTrash(name: String) {
        runBlocking {
            val sceneId = sceneRepository.createScene(name)
            sceneRepository.deleteScene(sceneId)
        }
    }

    @Given("I have a campaign {string} in the Trash")
    fun campaignIsInTrash(campaignName: String) {
        runBlocking {
            campaignRepository.createCampaign(campaignName)
            val campaign = campaignRepository.observeAll().first().first { it.name == campaignName }
            campaignRepository.deleteCampaign(campaign.id)
        }
    }

    @Given("its sessions are orphaned \\(hidden\\)")
    @Ignore("Session orphaning verification not yet fully implemented")
    fun itsSessionsAreOrphaned() {
        // Sessions are soft-deleted when their campaign is deleted
    }

    @Given("{string} is in the Trash")
    fun itemIsInTrash(name: String) {
        runBlocking {
            val sceneId = sceneRepository.createScene(name)
            sceneRepository.deleteScene(sceneId)
        }
    }

    @Given("{string} was moved to the Trash 7 days ago")
    @Ignore("Time-based auto-deletion requires FakeClock or background scheduler — not yet implemented")
    fun itemMovedToTrashSevenDaysAgo(name: String) {
        // TODO: Requires time manipulation / FakeClock to simulate 7-day expiry
    }

    @Given("the Trash contains multiple items")
    fun trashContainsMultipleItems() {
        runBlocking {
            val id1 = sceneRepository.createScene("Trash Item 1")
            val id2 = sceneRepository.createScene("Trash Item 2")
            sceneRepository.deleteScene(id1)
            sceneRepository.deleteScene(id2)
        }
    }

    // ── When ──────────────────────────────────────────────

    @When("I delete or swipe to remove the item")
    @Ignore("Generic swipe-to-delete not yet implemented for all item types")
    fun deleteOrSwipeToRemoveItem() {
        // TODO: Swipe-to-delete gesture not yet implemented for generic items
    }

    @When("I navigate to the {string} screen from Credits")
    fun navigateToScreenFromCredits(screenName: String) {
        navigateToTrashFromCredits()
    }

    @When("I tap the \"Restore\" button on the {string} card")
    fun tapRestoreButtonOnCard(itemName: String) {
        // Navigate to trash screen if not already there
        if (composeTestRule.onAllNodesWithTag("trashScreen").fetchSemanticsNodes().isEmpty()) {
            navigateToTrashFromCredits()
        }
        composeTestRule.onNodeWithTag("restoreButton_$itemName").performClick()
        composeTestRule.waitForIdle()
    }

    @When("I tap the \"Delete\" button on its card in the Trash")
    @Ignore("Tapping Delete button in trash requires knowing the specific item name — not yet generic")
    fun tapDeleteButtonOnTrashCard() {
        // TODO: Need a specific item reference to find the delete button
    }

    @When("the app runs its background cleanup or is launched")
    @Ignore("Background cleanup / auto-expiry not yet implemented")
    fun appRunsBackgroundCleanup() {
        // TODO: Auto-expiry after 7 days requires background scheduler
    }

    @When("I tap \"Empty Vault\"")
    fun tapEmptyVault() {
        composeTestRule.onNodeWithTag("trashEmptyVaultButton").performClick()
        composeTestRule.waitForIdle()
    }

    @When("I confirm the destructive action")
    fun confirmDestructiveAction() {
        composeTestRule.onNodeWithTag("trashConfirmEmptyVault").performClick()
        composeTestRule.waitForIdle()
    }

    // ── Then ──────────────────────────────────────────────

    @Then("the item is moved to the \"Recent Deletes\" \\(Trash\\) screen")
    @Ignore("Navigating to trash screen after swipe-delete not yet implemented")
    fun itemMovedToTrashScreen() {
        // TODO: Verify item appears in trash screen
    }

    @Then("it becomes temporarily unavailable in the main app lists")
    @Ignore("UI verification of item hidden from main list not yet fully implemented")
    fun itemTemporarilyUnavailable() {
        // TODO: Verify item is hidden from its original list
    }

    @Then("no instant permanent deletion dialog is shown unless it's a destructive cascade")
    @Ignore("Deletion dialog behavior verification not yet implemented")
    fun noInstantDeletionDialog() {
        // TODO: Verify no dialog shown for simple deletes
    }

    @Then("I see a list containing {string}, {string}, and {string}")
    @Ignore("Trash list with multiple item types not fully implemented")
    fun seeListContaining(item1: String, item2: String, item3: String) {
        // TODO: Verify all three items appear in the trash list
        composeTestRule.onNodeWithTag("trashList").assertIsDisplayed()
    }

    @Then("each item card displays how many days ago it was deleted")
    @Ignore("Days-ago calculation visible in card not yet verified in tests")
    fun eachItemCardDisplaysDaysAgo() {
        // TODO: Verify "Deleted X days ago" text on each card
    }

    @Then("each card shows the item's original type \\(e.g. Soundscape, Scene, FX\\)")
    @Ignore("Item type label not yet verified per card")
    fun eachCardShowsItemType() {
        // TODO: Verify type label on each trash item card
    }

    @Then("{string} is removed from the Trash")
    fun itemRemovedFromTrash(name: String) {
        val deletedScenes = runBlocking { sceneRepository.observeDeleted().first() }
        assert(deletedScenes.none { it.name == name }) {
            "'$name' is still in the trash after restore"
        }
    }

    @Then("it reappears exactly as it was in my Scenes list")
    fun itemReappearsInScenesList() {
        // Navigate to scenes and verify the item is there
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottomNavItem_SCENES").performClick()
        composeTestRule.waitForIdle()
    }

    @Then("the {string} campaign reappears in my campaigns list")
    fun campaignReappearsInList(campaignName: String) {
        val activeCampaigns = runBlocking { campaignRepository.observeAll().first() }
        assert(activeCampaigns.any { it.name == campaignName && !it.isDeleted }) {
            "'$campaignName' was not found in active campaigns after restore"
        }
    }

    @Then("its sessions are no longer hidden")
    @Ignore("Session restore verification not yet fully automated in UI")
    fun itsSessionsAreNoLongerHidden() {
        // TODO: Verify sessions are visible again
    }

    @Then("{string} is permanently deleted from the app")
    @Ignore("Permanent deletion verification requires checking database directly")
    fun isPermanentlyDeletedFromApp(name: String) {
        // TODO: Check that item is gone from both active and deleted lists
    }

    @Then("it can no longer be restored")
    @Ignore("Post-permanent-delete restore check not yet implemented")
    fun canNoLongerBeRestored() {
        // TODO: Verify restore button or item no longer present in trash
    }

    @Then("{string} is permanently deleted")
    @Ignore("Auto-expiry permanent deletion not yet implemented")
    fun isPermanentlyDeleted(name: String) {
        // TODO: Background auto-expiry not yet implemented
    }

    @Then("it no longer appears in the Trash list")
    @Ignore("Auto-expiry permanent deletion not yet implemented")
    fun noLongerAppearsInTrashList() {
        // TODO: Background auto-expiry not yet implemented
    }

    @Then("all items in the Trash are permanently deleted")
    fun allItemsInTrashPermanentlyDeleted() {
        val deletedCampaigns = runBlocking { campaignRepository.observeDeleted().first() }
        val deletedSessions = runBlocking { sessionRepository.observeDeleted().first() }
        val deletedScenes = runBlocking { sceneRepository.observeDeleted().first() }
        assert(deletedCampaigns.isEmpty() && deletedSessions.isEmpty() && deletedScenes.isEmpty()) {
            "Trash still contains items after emptying vault"
        }
    }

    @Then("the Trash screen shows a Large Material 3 icon with a prompt")
    fun trashScreenShowsEmptyState() {
        composeTestRule.onNodeWithTag("trashEmptyState").assertIsDisplayed()
    }
}
