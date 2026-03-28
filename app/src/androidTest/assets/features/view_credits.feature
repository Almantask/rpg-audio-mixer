Feature: View credits

  As a GM
  I want to tap the gear icon to reach the Credits screen
  So that I can find support links, the app version, and contributor information.

  Scenario: The gear icon is visible on every screen
    Given I am on the Home screen
    Then I see the gear (settings) icon in the top bar

  Scenario: Tapping the gear icon from any screen navigates to Credits
    Given I am on the Campaigns screen
    When I tap the gear icon
    Then I see the "Behind the Screen" credits screen

  Scenario: The Credits screen shows the app version
    When I open the Credits screen
    Then I see the app version number

  Scenario: The Credits screen shows a documentation link
    When I open the Credits screen
    Then I see a documentation link that opens in the browser

  Scenario: The Credits screen shows a Discord link
    When I open the Credits screen
    Then I see a Discord community link

  Scenario: The Credits screen shows a contact email link
    When I open the Credits screen
    Then I see a contact or support email link

  Scenario: Tapping the back arrow from Credits returns to the previous screen
    Given I navigated to Credits from the Scenes screen
    When I tap the back arrow
    Then I am back on the Scenes screen

  Scenario: There is no separate Settings screen
    When I tap the gear icon from any screen
    Then I land directly on the Credits screen, not a Settings screen
