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

  Scenario: Changing intensity on a playing category transitions immediately to the new pool
    Given the "Weather" category is playing at intensity level I
    When I change the intensity to level II
    Then a track from intensity level II starts playing
    And the previous intensity level I track stops

  Scenario: A warning is shown when no tracks exist at the selected intensity level
    Given the "Dungeon" category has no tracks at intensity level III
    When I set the "Dungeon" intensity to III
    Then a warning message is shown indicating no tracks are available at that intensity
