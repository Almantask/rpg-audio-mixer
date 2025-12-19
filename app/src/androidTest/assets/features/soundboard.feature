Feature: Soundboard playback

  As a player
  I want each sound button to play its own sound
  So that I can quickly trigger different effects.

  Scenario: Pressing a sound button plays that sound (happy path)
    When I press the "whip" sound button
    Then the "whip" sound should be played
    And I should see now playing as "Whip"

  Scenario: Pressing a different sound button plays a different sound
    When I press the "bark" sound button
    Then the "bark" sound should be played
    And I should see now playing as "Bark"

  Scenario: Pressing two sound buttons plays sounds in order (edge case)
    When I press the "whip" sound button
    And I press the "owl" sound button
    Then the sounds should be played in order: "whip" and then "owl"
    And I should see now playing as "Owl"
