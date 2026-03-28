Feature: Screen transitions

  As a GM
  I want smooth, consistent animated transitions between screens
  So that the app feels cohesive and polished during play.

  Scenario: Navigating forward uses "The Breath" transition
    Given I am on the Campaigns screen
    When I tap on a campaign to open its Sessions list
    Then the Campaigns screen fades out and scales up slightly
    And the Sessions screen fades in and scales up from slightly smaller

  Scenario: Navigating back uses the reverse of "The Breath" transition
    Given I am on the Sessions screen
    When I tap the back arrow
    Then the Sessions screen fades out and scales down slightly
    And the Campaigns screen fades in and scales down from slightly larger

  Scenario: Transitions are fast and do not block interaction
    When a screen transition occurs
    Then the incoming screen becomes interactive within a short time

  Scenario: The mini player uses "The Breath" animation on entrance
    Given no mini player is visible
    When I tap preview on an FX track
    Then the mini player appears with a scale-up and fade-in

  Scenario: The mini player uses "The Breath" animation on exit
    Given the mini player is visible
    When I navigate away from the Library
    Then the mini player disappears with a scale-down and fade-out


