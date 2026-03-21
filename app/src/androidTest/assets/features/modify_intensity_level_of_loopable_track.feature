Feature: Intensity level of loopable track

  As a GM
  I want to modify the intensity level of a loopable track category
  So that I can fine-tune the ambience to match the mood of the scene.

  Scenario: Ambience category starts at medium intensity by default
    Then the "forest" intensity level should be medium

  Scenario: I can increase the intensity to high
    When I slide the "forest" intensity to high
    Then the "forest" intensity level should be high

  Scenario: I can decrease the intensity to low
    When I slide the "forest" intensity to low
    Then the "forest" intensity level should be low

  Scenario: Each ambience category has its own independent intensity level
    When I slide the "forest" intensity to high
    And I slide the "tavern" intensity to low
    Then the "forest" intensity level should be high
    And the "tavern" intensity level should be low

  Scenario Outline: Each ambience category supports all 3 intensity levels
    When I slide the "<category>" intensity to <level>
    Then the "<category>" intensity level should be <level>

    Examples:
      | category | level  |
      | forest   | low    |
      | forest   | medium |
      | forest   | high   |
      | tavern   | low    |
      | dungeon  | high   |
