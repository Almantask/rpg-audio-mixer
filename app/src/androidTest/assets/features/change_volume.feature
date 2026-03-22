Feature: Change volume

  As a GM
  I want to control volume at different levels
  So that I can fine-tune the audio atmosphere during my game.

  Scenario: Change global volume affects all audio
    Given the global volume is at 100%
    And soundboard volume is at 50%
    And "forest_ambience" loopable track is at 100%
    And "dragon roar" soundboard track is playing
    When I set the global volume to 50%
    Then "forest_ambience" plays at 50% volume
    And "dragon roar" plays at 25% volume

  Scenario Outline: Change the volume of a loopable track individually
    Given the loopable track "<track>" is playing
    When I set the volume of "<track>" to <volume>%
    Then "<track>" plays at <volume>% volume

    Examples:
      | track           | volume |
      | forest_ambience | 50     |
      | tavern_music    | 75     |
      | battle_drums    | 30     |

  Scenario: Local loopable track volume change does not affect global volume
    Given the global volume is at 100%
    When I set the volume of "forest_ambience" to 50%
    Then the global volume remains at 100%

  Scenario: Change soundboard volume separately from loopable tracks
    Given the soundboard volume is at 100%
    And the loopable track "forest_ambience" is playing at 100% volume
    When I set the soundboard volume to 75%
    Then soundboard sounds play at 75% volume
    And "forest_ambience" continues to play at 100% volume
