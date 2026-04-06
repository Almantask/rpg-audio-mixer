Feature: Top app bar

  As a GM
  I want a consistent top bar on every screen
  So that I always know where I am and can navigate back when needed.

  Scenario: The top bar is visible on the Home screen
    Given I am on the Home screen
    Then I see the top app bar
    And the top bar title reads "Home"

  Scenario: The top bar is visible on the Campaigns screen
    Given I am on the Campaigns list screen
    Then I see the top app bar
    And the top bar title reads "Campaigns"

  Scenario: The top bar is visible on the Scenes screen
    Given I am on the SCENES tab screen
    Then I see the top app bar
    And the top bar title reads "Scenes"

  Scenario: The top bar is visible on the Library screen
    Given I am on the Audio Library screen
    Then I see the top app bar
    And the top bar title reads "Library"

  Scenario: The top bar title is displayed in gold on main screens
    Given I am on the Home screen
    Then the top bar title text is rendered in the app's gold colour

  Scenario: The back arrow is not shown on root tab screens
    Given I am on the Home screen
    Then I do not see a back arrow in the top bar

  Scenario: The back arrow is not shown on the Campaigns root screen
    Given I am on the Campaigns list screen
    Then I do not see a back arrow in the top bar

  Scenario: The back arrow is not shown on the Scenes root screen
    Given I am on the SCENES tab screen
    Then I do not see a back arrow in the top bar

  Scenario: The back arrow is not shown on the Library root screen
    Given I am on the Audio Library screen
    Then I do not see a back arrow in the top bar

  Scenario: The back arrow is shown on the Settings screen
    Given I am on the Home screen
    When I tap the gear icon
    Then I see the Settings screen
    And a back arrow is visible in the top bar

  Scenario: Tapping the back arrow on the Settings screen returns to the previous screen
    Given I navigated to the Settings screen from the Home screen
    When I tap the back arrow in the top bar
    Then I am back on the Home screen
    And the back arrow is no longer visible

  Scenario: The gear icon is present on the Home screen
    Given I am on the Home screen
    Then I see the gear icon in the top bar

  Scenario: The gear icon is present on the Campaigns screen
    Given I am on the Campaigns list screen
    Then I see the gear icon in the top bar

  Scenario: The gear icon is present on the Scenes screen
    Given I am on the SCENES tab screen
    Then I see the gear icon in the top bar

  Scenario: The gear icon is present on the Library screen
    Given I am on the Audio Library screen
    Then I see the gear icon in the top bar
