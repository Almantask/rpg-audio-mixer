Feature: Create many scenes

  As a GM
  I want to create multiple scenes
  So that I can have different audio setups for different game situations.

  Scenario: Can create a second scene
    Given I have created a scene named "Tavern"
    When I create a new scene named "Forest"
    Then I have 2 scenes

  Scenario Outline: Can create many scenes
    Given I have created <count> scenes
    Then I have <count> scenes

    Examples:
      | count |
      | 3     |
      | 5     |
      | 10    |

  Scenario: Each created scene has its own name
    Given I have created scenes named
      | Tavern  |
      | Forest  |
      | Dungeon |
    Then I see the "Tavern" scene
    And I see the "Forest" scene
    And I see the "Dungeon" scene
