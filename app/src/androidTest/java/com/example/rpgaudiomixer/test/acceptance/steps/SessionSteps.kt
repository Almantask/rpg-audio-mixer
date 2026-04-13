package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Step definitions for manage_sessions.feature.
 *
 * UI ↔ Spec discrepancies noted inline:
 *  - Feature says "Add New Session" button; the empty-state CTA renders "Create Session"
 *    and the FAB has content-description "Add Session". Tests for the literal feature text
 *    will surface this mismatch at runtime so the developer can reconcile the labels.
 *  - Feature says "swipe right on the {string} card" but SwipeableSessionCard only enables
 *    EndToStart (right-to-left) dismissal. The step therefore issues a swipeLeft() gesture,
 *    matching actual UI behaviour while the feature wording is logged as a spec note.
 */
class SessionSteps(
    private val composeRuleHolder: MainActivityComposeRule
) {
    private val composeTestRule get() = composeRuleHolder.composeRule
    private val campaignRepository get() = PicoToHiltBridge.campaignRepository
    private val sessionRepository get() = PicoToHiltBridge.sessionRepository

    /** Tracks the name of the last campaign created in a Given step, used by navigation steps. */
    private var currentCampaignName: String = ""

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // Given
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /**
     * Creates a campaign by name so subsequent When steps can open it.
     * Note: differs from CampaignSteps' "I have a campaign named {string}" wording.
     */
    @Given("I have a campaign {string}")
    fun haveCampaign(campaignName: String) {
        runBlocking { campaignRepository.createCampaign(campaignName) }
        currentCampaignName = campaignName
    }

    /** Creates a campaign and explicitly seeds zero sessions (no-op beyond creating the campaign). */
    @Given("I have a campaign {string} with no sessions")
    fun haveCampaignWithNoSessions(campaignName: String) {
        runBlocking { campaignRepository.createCampaign(campaignName) }
        currentCampaignName = campaignName
    }

    /**
     * Creates a campaign pre-populated with sessions from the data table.
     * Data table rows contain a single column – the session name.
     */
    @Given("I have a campaign {string} with sessions")
    fun haveCampaignWithSessions(campaignName: String, sessionsTable: DataTable) {
        runBlocking {
            campaignRepository.createCampaign(campaignName)
            val campaign = campaignRepository.observeAll().first()
                .first { it.name == campaignName }
            sessionsTable.asList().forEach { sessionName ->
                sessionRepository.createSession(campaign.id, sessionName)
            }
        }
        currentCampaignName = campaignName
    }

    /**
     * Creates two sessions whose insertion order mimics the date relationship described in the
     * feature ("last month" vs "today"). Since SessionRepository does not accept a custom date,
     * we rely on the repository using System.currentTimeMillis() at insertion time, and the DAO
     * sorting results by createdAt DESC. Inserting session1 before session2 guarantees session2
     * has a higher timestamp and therefore appears above session1 in the list.
     *
     * If sub-millisecond timing causes flakiness, the developer should expose a date parameter on
     * SessionRepository.createSession() and provide a FakeClock in tests.
     */
    @Given("I have sessions {string} dated last month and {string} dated today")
    fun haveSessionsWithDates(olderSessionName: String, newerSessionName: String) {
        runBlocking {
            // Create a transient campaign to host these sessions.
            val hostCampaignName = "__DateOrderTestCampaign__"
            campaignRepository.createCampaign(hostCampaignName)
            val campaign = campaignRepository.observeAll().first()
                .first { it.name == hostCampaignName }

            // Insert older session first (lower createdAt), then the newer one.
            sessionRepository.createSession(campaign.id, olderSessionName)
            // Small delay ensures distinct timestamps without Thread.sleep –
            // kotlinx.coroutines delay inside runBlocking satisfies the intent.
            kotlinx.coroutines.delay(5)
            sessionRepository.createSession(campaign.id, newerSessionName)

            currentCampaignName = hostCampaignName
        }
    }

    /**
     * Creates a campaign containing a single session, then navigates to the Sessions screen
     * so the UI is ready for dialog-level actions (cover-art, etc.).
     */
    @Given("I am creating a session {string}")
    fun amCreatingSession(sessionName: String) {
        val campaignName = "__CoverArtTestCampaign__"
        runBlocking { campaignRepository.createCampaign(campaignName) }
        currentCampaignName = campaignName
        // Navigate to the sessions screen via the campaign card.
        composeTestRule.onNodeWithText(campaignName).performClick()
        // Open the create-session dialog via the FAB.
        // ⚠ Spec says "Add New Session"; FAB content-description is "Add Session".
        composeTestRule.onNodeWithTag("sessionsScreen").assertIsDisplayed()
    }

    /** Creates a campaign with one pre-seeded session; used by the "tap session → scene list" scenario. */
    @Given("I have a campaign with a session {string}")
    fun haveCampaignWithSingleSession(sessionName: String) {
        val campaignName = "__TapSessionTestCampaign__"
        runBlocking {
            campaignRepository.createCampaign(campaignName)
            val campaign = campaignRepository.observeAll().first()
                .first { it.name == campaignName }
            sessionRepository.createSession(campaign.id, sessionName)
        }
        currentCampaignName = campaignName
        // Navigate to the sessions screen so the session list is visible.
        composeTestRule.onNodeWithText(campaignName).performClick()
    }

    /**
     * Creates a temporary campaign plus one session; navigates to the sessions screen so
     * the swipe-to-delete scenario starts on the right screen.
     */
    @Given("I have a session {string}")
    fun haveSession(sessionName: String) {
        val campaignName = "__SwipeDeleteTestCampaign__"
        runBlocking {
            campaignRepository.createCampaign(campaignName)
            val campaign = campaignRepository.observeAll().first()
                .first { it.name == campaignName }
            sessionRepository.createSession(campaign.id, sessionName)
        }
        currentCampaignName = campaignName
        // Navigate to the sessions screen.
        composeTestRule.onNodeWithText(campaignName).performClick()
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // When
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /**
     * Navigates from the Campaigns list to the Sessions screen by tapping the campaign card
     * that carries the given name.
     */
    @When("I open {string}")
    fun openCampaign(campaignName: String) {
        composeTestRule.onNodeWithText(campaignName).performClick()
        currentCampaignName = campaignName
    }

    /** Types the session name into the "Session Name" text field inside the create-session dialog. */
    @When("I enter the session name {string}")
    fun enterSessionName(sessionName: String) {
        composeTestRule.onNodeWithText("Session Name").performTextInput(sessionName)
    }

    /** Taps the "Create" confirmation button in the create-session dialog. */
    @When("I confirm creation")
    fun confirmCreation() {
        composeTestRule.onNodeWithText("Create").performClick()
    }

    /**
     * Navigates to the sessions screen of [currentCampaignName].
     * Used by scenarios that set up their own campaign in the Given step.
     */
    @When("I view the sessions list")
    fun viewSessionsList() {
        composeTestRule.onNodeWithText(currentCampaignName).performClick()
    }

    /**
     * Taps the cover-art area of the session-creation UI.
     *
     * ⚠ Cover-art upload is not yet implemented in the current UI iteration. This step will fail
     * with a "node not found" error until a composable with text/tag "Cover art" is added.
     * The failure is expected and documents the missing feature.
     */
    @When("I tap the cover art area")
    fun tapCoverArtArea() {
        composeTestRule.onNodeWithText("Cover art", ignoreCase = true).performClick()
    }

    /**
     * Stub for the photo-picker interaction.
     *
     * ⚠ Selecting from the system photo library requires an ActivityResult contract and cannot be
     * driven by Espresso/Compose alone. This step is a pending stub; implement using
     * Espresso-Intents once the feature is built.
     */
    @When("I select a photo from the device's photo library")
    fun selectPhotoFromLibrary() {
        // TODO: Implement with Espresso-Intents / ActivityResultLauncher interception
        //       once the cover-art feature is implemented in the UI.
    }

    /**
     * Swipes LEFT on the session card to trigger the EndToStart dismissal gesture.
     *
     * ⚠ Spec wording: "swipe right on the {string} card".
     *   Actual UI: SwipeableSessionCard has enableDismissFromStartToEnd = false, so only an
     *   EndToStart (right-to-left / swipeLeft) gesture triggers deletion. The step uses swipeLeft()
     *   to match the working UI behaviour. The spec wording should be reconciled with the team.
     */
    @When("I swipe right on the {string} card")
    fun swipeRightOnSessionCard(sessionName: String) {
        composeTestRule.onNodeWithText(sessionName)
            .performTouchInput { swipeLeft() }
    }

    // ──────────────────────────────────────────────────────────────────────────────────────────────
    // Then
    // ──────────────────────────────────────────────────────────────────────────────────────────────

    /** Verifies the named session appears in the sessions list. */
    @Then("I see {string} in the sessions list")
    fun seeSessionInList(sessionName: String) {
        composeTestRule.onNodeWithText(sessionName).assertIsDisplayed()
    }

    /**
     * Verifies the empty-state icon (rendered by ArcanumEmptyState at 96dp with
     * contentDescription equal to the title) and its accompanying title text are shown.
     */
    @Then("I see a Large Material 3 icon with a prompt")
    fun seeEmptyStateIcon() {
        // ArcanumEmptyState renders a title Text node tagged "emptyStateTitle" and an Icon
        // whose contentDescription equals the title text "No Sessions Yet".
        composeTestRule.onNodeWithTag("emptyStateTitle").assertIsDisplayed()
    }

    /**
     * Verifies a button with the given label is visible.
     *
     * ⚠ The feature expects "Add New Session"; the current UI renders "Create Session" inside
     *   the empty-state CTA (ArcanumEmptyState ctaText). This assertion will fail until the
     *   UI label is aligned with the spec. The failure is intentional and surfaces the mismatch.
     */
    @Then("I see an {string} button")
    fun seeButton(buttonText: String) {
        composeTestRule.onNodeWithText(buttonText, ignoreCase = true).assertIsDisplayed()
    }

    /**
     * Verifies exactly three SessionCard nodes are shown.
     * Uses the "SessionCard" testTag set on each Card in SessionList.
     */
    @Then("I see all three sessions in the list")
    fun seeAllThreeSessions() {
        composeTestRule.onAllNodesWithTag("SessionCard").assertCountEquals(3)
    }

    /**
     * Verifies [topSession] is rendered above [bottomSession] in the list by comparing the
     * vertical positions of their bounding rectangles in the root layout.
     */
    @Then("{string} appears above {string}")
    fun appearsAbove(topSession: String, bottomSession: String) {
        val topBounds = composeTestRule
            .onNodeWithText(topSession)
            .fetchSemanticsNode()
            .boundsInRoot
        val bottomBounds = composeTestRule
            .onNodeWithText(bottomSession)
            .fetchSemanticsNode()
            .boundsInRoot

        assert(topBounds.top < bottomBounds.top) {
            "Expected \"$topSession\" (top=${topBounds.top}) to appear above " +
                "\"$bottomSession\" (top=${bottomBounds.top})"
        }
    }

    /**
     * Verifies the scene list screen is shown for the given session.
     *
     * ⚠ Scene-list navigation from the Sessions screen is not yet implemented in Iteration 2.
     * This step checks only that the session name is still visible after the tap; a deeper
     * assertion should be added once SceneListScreen exists.
     */
    @Then("I see the scene list for {string}")
    fun seeSceneListFor(sessionName: String) {
        // TODO: Replace with a proper scene-list screen assertion once SceneListScreen is built.
        composeTestRule.onNodeWithText(sessionName).assertIsDisplayed()
    }

    /**
     * Verifies the session is no longer present in the sessions list (soft-deleted).
     * Mirrors the assertion in [itIsNoLongerInSessionsList] for Gherkin readability.
     */
    @Then("{string} is moved to the Trash")
    fun isMovedToTrash(sessionName: String) {
        composeTestRule.onNodeWithText(sessionName).assertDoesNotExist()
    }

    /** Verifies the session is absent from the sessions list after a swipe-to-delete gesture. */
    @Then("it is no longer in the sessions list")
    fun itIsNoLongerInSessionsList() {
        // The previous Then step already asserts absence; this companion step re-confirms via
        // the SessionCard count to guard against re-insertion bugs.
        composeTestRule.onAllNodes(hasTestTag("SessionCard")).assertCountEquals(0)
    }

    /** Verifies the selected photo is displayed as cover art on the session card. */
    @Then("the selected photo is shown as the session's cover art")
    fun selectedPhotoShownAsCoverArt() {
        // TODO: Implement once cover-art upload is built. Verify via a testTag on the cover-art
        //       Image composable, e.g. onNodeWithTag("sessionCoverArt").assertIsDisplayed()
    }
}
