Feature: View settings

  As a GM
  I want to tap the gear icon to reach the Settings screen
  So that I can access the Trash, find support links, the app version, and contributor information.

  Scenario: The gear icon is visible on every screen
    Given I am on the Home screen
    Then I see the gear (settings) icon in the top bar

  Scenario: Tapping the gear icon from any screen navigates to Settings
    Given I am on the Campaigns screen
    When I tap the gear icon
    Then I see the "Behind the Screen" heading on the Settings screen

  Scenario: The Settings screen contains a link to the Trash
    When I open the Settings screen
    Then I see the "Restore Recent Deletes" button

  Scenario: Tapping Restore Recent Deletes opens the Trash screen
    Given I am on the Settings screen
    When I tap "Restore Recent Deletes"
    Then I am navigated to the "Recent Deletes" (Trash) screen

  Scenario: The Settings screen contains a Sync button
    When I open the Settings screen
    Then I see the "Sync Purchases & Free Tracks" button

  Scenario: Tapping Sync downloads missing purchases and free tracks
    Given I am on the Settings screen
    When I tap "Sync Purchases & Free Tracks"
    Then missing purchases and free tracks are downloaded
    And the button becomes disabled and greyed out

  Scenario: Sync button remains disabled for 24 hours after use
    Given I successfully synced my tracks less than 24 hours ago
    When I open the Settings screen
    Then the "Sync Purchases & Free Tracks" button is greyed out

  Scenario: Sync button is active again after 24 hours
    Given I successfully synced my tracks more than 24 hours ago
    When I open the Settings screen
    Then the "Sync Purchases & Free Tracks" button is enabled

  Scenario: The Settings screen shows the app version
    When I open the Settings screen
    Then I see the app version number

  Scenario: The Settings screen shows a documentation link
    When I open the Settings screen
    Then I see a documentation link that opens in the browser

  Scenario: Tapping the back arrow from Settings returns to the previous screen
    Given I navigated to Settings from the Scenes screen
    When I tap the back arrow
    Then I am back on the Scenes screen
