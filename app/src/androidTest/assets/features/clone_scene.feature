Feature: Clone scene

  As a GM
  I want to clone an existing scene
  So that I can use it as a starting point for a similar scene.

  Scenario: Cloning a scene creates a new scene
    Given I have created a scene named "Forest Night"
    When I clone the "Forest Night" scene
    Then I have 2 scenes

  Scenario: Cloned scene contains the same sounds as the original
    Given I have a scene "Forest Night" with "owl_hooting" in the ambience
    When I clone the "Forest Night" scene as "Forest Dawn"
    Then the "Forest Dawn" ambience contains "owl_hooting"

  Scenario: Cloned scene is independent of the original
    Given I have a scene "Forest Night" with "owl_hooting" in the soundboard
    And I have cloned the "Forest Night" scene as "Forest Dawn"
    When I add "bird_song" to the "Forest Dawn" soundboard
    Then the "Forest Night" soundboard does not contain "bird_song"

  Scenario: Modifying the original does not affect the clone
    Given I have a scene "Forest Night" with "owl_hooting" in the soundboard
    And I have cloned the "Forest Night" scene as "Forest Dawn"
    When I add "wolf_howl" to the "Forest Night" soundboard
    Then the "Forest Dawn" soundboard does not contain "wolf_howl"

  Scenario: Cloned scene can be renamed
    Given I have created a scene named "Forest Night"
    When I clone the "Forest Night" scene as "Forest Dawn"
    Then I see the "Forest Dawn" scene in my scenes list
