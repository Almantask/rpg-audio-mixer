Feature: See a loopable track progress

  As a GM
  I want to see which loopable track is currently playing
  So that I know what background ambience is active.

  Scenario: Starting a loop shows it as now looping
    When I press the "forest" loop button
    Then I should see "forest" is now looping

  Scenario: When no loop is playing, nothing is shown as looping
    Then I should see nothing is looping

  Scenario: Starting a different loop replaces the current looping indicator
    Given I had pressed the "forest" loop button
    When I press the "tavern" loop button
    Then I should see "tavern" is now looping
