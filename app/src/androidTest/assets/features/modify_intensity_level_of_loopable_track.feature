Feature: Intensity level of loopable track

  As a GM
  I want to modify the intensity level of a loopable track category
  So that I can fine-tune the ambience to match the mood of the scene.

  # Folder structure: Sounds/Ambience/{category}/{intensityLevel}/
  # For example: Ambience/rain/1/lightrain.mp3
  # Intensity folders: 1 = low, 2 = medium, 3 = high

  Scenario Outline: Each ambience category plays a random track from the correct intensity folder
    When I slide the "<category>" intensity to <level>
    Then a random track from "Ambience/<category>/<folder>" is played

    Examples:
      | category | level  | folder |
      | forest   | low    | 1      |
      | forest   | medium | 2      |
      | forest   | high   | 3      |
      | tavern   | low    | 1      |
      | dungeon  | high   | 3      |

  Scenario: A warning message is shown when there are no tracks at the selected intensity level
    Given the "dungeon" ambience folder for intensity level "high" is empty
    When I slide the "dungeon" intensity to high
    Then a warning message is shown
