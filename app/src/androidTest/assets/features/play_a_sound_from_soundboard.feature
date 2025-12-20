Feature: Soundboard playback

  As a GM
  I want each sound button to play its own sound
  So that I can quickly trigger different effects.

  Scenario: Pressing a sound button plays that sound
    When I press the "whip" sound button
    Then the "whip" sound should be played

  Scenario: Pressing a different sound button plays a different sound
    When I press the "dog-bark" sound button
    Then the "dog-bark" sound should be played

  Scenario: Pressing two sound buttons plays sounds at the same time
    Given I had pressed the "whip" sound button
    When I press the "owl-hooting" sound button
    Then the sounds should be played at the same time
      | whip | owl-hooting |