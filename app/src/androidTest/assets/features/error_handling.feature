Feature: Error handling dialog

  As a GM
  I want to be clearly notified when something goes wrong in the app
  So that I understand what happened and can continue my session without being stuck.

  Scenario: An error dialog appears as a modal overlay when an error occurs
    Given I am on the Home screen
    When the app encounters an error with the message "Something went wrong. Please try again."
    Then an error dialog appears as a modal overlay on top of the current screen
    And the rest of the screen content is obscured behind the dialog

  Scenario: The error dialog shows the error message to the GM
    Given the app has encountered an error with a short message
    When the error dialog is displayed
    Then I can read the full error message inside the dialog

  Scenario: The error dialog provides a scrollable area for long error messages
    Given the app has encountered an error with a very long message
    When the error dialog is displayed
    Then the message area is scrollable
    And I can scroll down to read the full message

  Scenario: The dismiss button is always visible and reachable
    Given the error dialog is showing a very long error message
    When I view the error dialog
    Then the dismiss button is visible without scrolling
    And I can tap the dismiss button

  Scenario: Dismissing the error dialog closes it and keeps the GM on the same screen
    Given I am on the Campaigns list screen
    And an error dialog is displayed
    When I tap the dismiss button
    Then the error dialog closes
    And I remain on the Campaigns list screen

  Scenario: Dismissing the error dialog from the Scenes screen keeps context
    Given I am on the SCENES tab screen
    And an error dialog is displayed
    When I tap the dismiss button
    Then the error dialog closes
    And I remain on the SCENES tab screen

  Scenario: No error dialog appears when the error message is null
    Given the app triggers an error event with a null message
    Then no error dialog is displayed
    And the current screen remains unchanged

  Scenario: No error dialog appears when the error message is empty
    Given the app triggers an error event with an empty message
    Then no error dialog is displayed
    And the current screen remains unchanged
