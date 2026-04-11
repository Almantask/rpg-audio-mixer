@iter4
Feature: Intensity level of soundscape category

  As a GM
  I want to change the intensity level of a soundscape category
  So that the right tracks play for the mood of the scene.

  Scenario Outline: Changing intensity plays tracks from the matching pool
    Given the "<category>" category has tracks at intensity level <level>
    When I set the "<category>" intensity to <level>
    Then a track from the intensity <level> pool plays

    Examples:
      | category | level |
      | Weather  | I     |
      | Weather  | II    |
      | Weather  | III   |
      | Interior | I     |
      | Combat   | III   |

  Scenario: Changing intensity on a playing category transitions with a 2-second crossfade
    Given the "Weather" category is playing at intensity level I
    When I change the intensity to level II
    Then the new intensity level II track begins playing immediately
    And the previous intensity level I track remains audible while fading out
    And the 2-second crossfade allows both tracks to be heard simultaneously during the transition

  Scenario: Empty intensity levels are non-interactive
    Given the "Dungeon" category has no tracks at intensity level III
    Then the intensity button for level III on the "Dungeon" card is greyed out
    And tapping the intensity button for level III has no effect
