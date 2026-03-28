Feature: Change volume

  As a GM
  I want to control volume at different levels
  So that I can fine-tune the audio atmosphere during my game.

  # Volume model: Actual output per category = Master Atmosphere × per-category MIX
  # Soundboard has its own separate Master slider.

  Scenario: Master Atmosphere at 100% and MIX at 50% plays at 50% output
    Given the Master Atmosphere slider is at 100%
    And the "Weather" MIX slider is at 50%
    And "Weather" is playing
    Then "Weather" plays at 50% actual output

  Scenario: Master Atmosphere at 80% and MIX at 50% plays at 40% output
    Given the Master Atmosphere slider is at 80%
    And the "Interior" MIX slider is at 50%
    And "Interior" is playing
    Then "Interior" plays at 40% actual output

  Scenario Outline: Changing MIX for a category adjusts its volume independently
    Given the Master Atmosphere is at 100%
    And a category "<category>" is playing
    When I set the "<category>" MIX slider to <mix>%
    Then "<category>" plays at <mix>% actual output

    Examples:
      | category  | mix |
      | Weather   | 50  |
      | Interior  | 75  |
      | Monsters  | 30  |

  Scenario: Changing MIX for one category does not affect other categories
    Given the Master Atmosphere is at 100%
    And "Weather" has MIX at 100% and "Interior" has MIX at 100%
    When I set the "Weather" MIX slider to 30%
    Then "Weather" plays at 30% actual output
    And "Interior" still plays at 100% actual output

  Scenario: Soundboard Master slider controls all soundboard effects
    Given the soundboard Master is at 100%
    When I set the soundboard Master to 75%
    Then soundboard effects play at 75% output

  Scenario: Soundboard Master does not affect soundscape category volumes
    Given the soundboard Master is at 50%
    And the Master Atmosphere is at 100%
    And "Forest Loop" is playing at MIX 100%
    Then "Forest Loop" plays at 100% output
    And the soundboard Master has no effect on it

