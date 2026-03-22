Feature: Modify scene

  As a GM
  I want to modify my scenes
  So that I can update and improve my audio setups over time.

  Scenario: Can add a sound to an existing scene's soundboard
    Given I have created a scene named "Tavern"
    When I add a sound to the "Tavern" scene's soundboard
    Then the "Tavern" soundboard has 1 sound

  Scenario: Can add a sound to an existing scene's ambience
    Given I have created a scene named "Tavern"
    When I add a sound to the "Tavern" scene's ambience
    Then the "Tavern" ambience has 1 sound

  Scenario: Can add multiple sounds to a scene's soundboard
    Given I have created a scene named "Tavern"
    When I add 3 sounds to the "Tavern" scene's soundboard
    Then the "Tavern" soundboard has 3 sounds

  Scenario: Can remove a sound from an existing scene's ambience
    Given I have created a scene named "Tavern"
    And the "Tavern" scene's ambience has a sound
    When I remove the sound from the "Tavern" scene's ambience
    Then the "Tavern" ambience has no sounds

  Scenario: Removing one sound does not remove other sounds
    Given I have created a scene named "Tavern"
    And the "Tavern" scene's ambience has sounds "forest_ambience" and "rain"
    When I remove "rain" from the "Tavern" scene's ambience
    Then the "Tavern" ambience still contains "forest_ambience"
