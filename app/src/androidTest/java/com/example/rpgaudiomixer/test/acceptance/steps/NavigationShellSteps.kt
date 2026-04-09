package com.example.rpgaudiomixer.test.acceptance.steps

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.rpgaudiomixer.app.components.ArcanumTopBarTestTags
import com.example.rpgaudiomixer.app.components.BottomNavTestTags
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags
import com.example.rpgaudiomixer.app.screens.SettingsSyncState
import com.example.rpgaudiomixer.test.acceptance.rules.MainActivityComposeRule
import com.example.rpgaudiomixer.test.acceptance.util.assertTextDisplayed
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class NavigationShellSteps(
    private val composeRuleHolder: MainActivityComposeRule,
) {

    init {
        SettingsSyncState.reset()
    }

    @When("I open the app")
    fun iOpenTheApp() {
        composeRuleHolder.composeRule.waitForIdle()
    }

    @Then("I see four tabs: HOME, CAMPAIGNS, SCENES, and LIBRARY")
    fun iSeeFourTabs() {
        listOf(
            MainNavDestination.HOME,
            MainNavDestination.CAMPAIGNS,
            MainNavDestination.SCENES,
            MainNavDestination.LIBRARY,
        ).forEach { destination ->
            composeRuleHolder.composeRule.onNodeWithTag(BottomNavTestTags.item(destination)).assertIsDisplayed()
            composeRuleHolder.composeRule.assertTextDisplayed(destination.label)
        }
    }

    @Given("I am on the Library screen")
    fun iAmOnTheLibraryScreen() {
        tapTab(MainNavDestination.LIBRARY)
    }

    @Given("I am on the Home screen")
    fun iAmOnTheHomeScreen() {
        tapTab(MainNavDestination.HOME)
    }

    @Given("I am on the Campaigns screen")
    fun iAmOnTheCampaignsScreen() {
        tapTab(MainNavDestination.CAMPAIGNS)
    }

    @Given("I am on the SCENES tab")
    fun iAmOnTheScenesTab() {
        tapTab(MainNavDestination.SCENES)
    }

    @Given("I navigated to Settings from the Scenes screen")
    fun iNavigatedToSettingsFromTheScenesScreen() {
        tapTab(MainNavDestination.SCENES)
        tapGearIcon()
    }

    @Given("I am on the Settings screen")
    fun iAmOnTheSettingsScreen() {
        tapGearIcon()
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.SETTINGS).assertIsDisplayed()
    }

    @Given("I successfully synced my tracks less than 24 hours ago")
    fun iSuccessfullySyncedMyTracksLessThan24HoursAgo() {
        SettingsSyncState.markSynced(System.currentTimeMillis() - 60 * 60 * 1000L)
    }

    @Given("I successfully synced my tracks more than 24 hours ago")
    fun iSuccessfullySyncedMyTracksMoreThan24HoursAgo() {
        SettingsSyncState.markSynced(System.currentTimeMillis() - 25 * 60 * 60 * 1000L)
    }

    @When("I tap the HOME tab")
    fun iTapTheHomeTab() {
        tapTab(MainNavDestination.HOME)
    }

    @When("I tap the CAMPAIGNS tab")
    fun iTapTheCampaignsTab() {
        tapTab(MainNavDestination.CAMPAIGNS)
    }

    @When("I tap the SCENES tab")
    fun iTapTheScenesTab() {
        tapTab(MainNavDestination.SCENES)
    }

    @When("I tap the LIBRARY tab")
    fun iTapTheLibraryTab() {
        tapTab(MainNavDestination.LIBRARY)
    }

    @When("I tap the gear icon")
    fun iTapTheGearIcon() {
        tapGearIcon()
    }

    @When("I open the Settings screen")
    fun iOpenTheSettingsScreen() {
        tapGearIcon()
    }

    @When("I tap {string}")
    fun iTap(text: String) {
        composeRuleHolder.composeRule.onNodeWithText(text).performClick()
    }

    @When("I tap the back arrow")
    fun iTapTheBackArrow() {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.BACK_ARROW).performClick()
    }

    @Then("I see the Home screen")
    fun iSeeTheHomeScreen() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.HOME).assertIsDisplayed()
    }

    @Then("I see the Campaigns list screen")
    fun iSeeTheCampaignsListScreen() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.CAMPAIGNS).assertIsDisplayed()
    }

    @Then("I see the SCENES tab screen")
    fun iSeeTheScenesTabScreen() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.SCENES).assertIsDisplayed()
    }

    @Then("I see the Audio Library screen")
    fun iSeeTheAudioLibraryScreen() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.LIBRARY).assertIsDisplayed()
    }

    @Then("the SCENES tab icon appears highlighted in gold")
    fun theScenesTabIconAppearsHighlightedInGold() {
        composeRuleHolder.composeRule.onNodeWithTag(BottomNavTestTags.item(MainNavDestination.SCENES))
            .assertIsSelected()
    }

    @Then("the other three tabs appear inactive")
    fun theOtherThreeTabsAppearInactive() {
        listOf(
            MainNavDestination.HOME,
            MainNavDestination.CAMPAIGNS,
            MainNavDestination.LIBRARY,
        ).forEach { destination ->
            composeRuleHolder.composeRule.onNodeWithTag(BottomNavTestTags.item(destination))
                .assertIsDisplayed()
                .assertIsNotSelected()
        }
    }

    @Then("I see the gear \\(settings) icon in the top bar")
    fun iSeeTheGearSettingsIconInTheTopBar() {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.GEAR_ICON).assertIsDisplayed()
    }

    @Then("I see the {string} heading on the Settings screen")
    fun iSeeTheHeadingOnTheSettingsScreen(heading: String) {
        composeRuleHolder.composeRule.assertTextDisplayed(heading)
    }

    @Then("I see the {string} button")
    fun iSeeTheButton(label: String) {
        composeRuleHolder.composeRule.assertTextDisplayed(label)
    }

    @Then("I am navigated to the {string} \\(Trash) screen")
    fun iAmNavigatedToTheTrashScreen(screenTitle: String) {
        composeRuleHolder.composeRule.assertTextDisplayed(screenTitle)
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.TRASH).assertIsDisplayed()
    }

    @Then("missing purchases and free tracks are downloaded")
    fun missingPurchasesAndFreeTracksAreDownloaded() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.SETTINGS_SYNC).assertIsDisplayed()
    }

    @Then("the button becomes disabled and greyed out")
    fun theButtonBecomesDisabledAndGreyedOut() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.SETTINGS_SYNC).assertIsNotEnabled()
    }

    @Then("the {string} button is greyed out")
    fun theButtonIsGreyedOut(label: String) {
        composeRuleHolder.composeRule.onNodeWithText(label).assertIsNotEnabled()
    }

    @Then("the {string} button is enabled")
    fun theButtonIsEnabled(label: String) {
        composeRuleHolder.composeRule.onNodeWithText(label).assertIsEnabled()
    }

    @Then("I see the app version number")
    fun iSeeTheAppVersionNumber() {
        composeRuleHolder.composeRule.assertTextDisplayed("Version 1.0")
    }

    @Then("I see a documentation link that opens in the browser")
    fun iSeeADocumentationLinkThatOpensInTheBrowser() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.SETTINGS_DOCUMENTATION)
            .assertIsDisplayed()
    }

    @Then("I am back on the Scenes screen")
    fun iAmBackOnTheScenesScreen() {
        composeRuleHolder.composeRule.onNodeWithTag(MainScreenTestTags.SCENES).assertIsDisplayed()
    }

    private fun tapTab(destination: MainNavDestination) {
        composeRuleHolder.composeRule.onNodeWithTag(BottomNavTestTags.item(destination)).performClick()
    }

    private fun tapGearIcon() {
        composeRuleHolder.composeRule.onNodeWithTag(ArcanumTopBarTestTags.GEAR_ICON).performClick()
    }
}
