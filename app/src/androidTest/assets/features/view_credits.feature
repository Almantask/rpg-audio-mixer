@iter9
Feature: View Credits

  As a GM
  I want to tap the gear icon to reach the Credits screen
  So that I can access the Trash, find support links, the app version, and contributor information.

  Scenario: The gear icon is visible on every screen
    Given I am on the Home screen
    Then I see the gear icon in the top bar

  Scenario: Tapping the gear icon from any screen navigates to Credits
    Given I am on the Campaigns screen
    When I tap the gear icon
    Then I see the "Behind the Screen" heading on the Credits screen

  Scenario: The Credits screen contains a link to the Trash
    When I open the Credits screen
    Then I see the "Restore Recent Deletes" button

  Scenario: Tapping Restore Recent Deletes opens the Trash screen
    Given I am on the Credits screen
    When I tap "Restore Recent Deletes"
    Then I am navigated to the "Recent Deletes" (Trash) screen

  Scenario: The Credits screen shows the app version
    When I open the Credits screen
    Then I see the app version number

  Scenario: The Credits screen shows a documentation link
    When I open the Credits screen
    Then I see a documentation link that opens in the browser

  Scenario: Tapping the back arrow from Credits returns to the previous screen
    Given I navigated to Credits from the Scenes screen
    When I tap the back arrow
    Then I am back on the Scenes screen