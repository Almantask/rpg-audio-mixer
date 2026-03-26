Feature: Build your own scene

  As a GM
  I want to build my own scene of sounds
  So that I can create a custom audio atmosphere for my game.

  Scenario: New scene has ambience and soundboard tabs
    When I create a new scene
    Then I see an "Ambience" tab
    And I see a "Soundboard" tab

  Scenario: Soundboard tab starts empty
    Given I have created a new scene
    When I open the "Soundboard" tab
    Then the soundboard has no sounds

  Scenario: Soundboard tab shows an add button when empty
    Given I have created a new scene
    When I open the "Soundboard" tab
    Then I see an add button

  Scenario: Add button always appears at the end of the soundboard
    Given I have created a new scene
    And I have opened the "Soundboard" tab
    When I add 3 sounds to the soundboard
    Then the add button is the last item in the soundboard

  Scenario: Ambience tab starts empty
    Given I have created a new scene
    When I open the "Ambience" tab
    Then the ambience has no sounds

  Scenario: Ambience tab shows an add button when empty
    Given I have created a new scene
    When I open the "Ambience" tab
    Then I see an add button

  Scenario: Add button always appears at the end of the ambience
    Given I have created a new scene
    And I have opened the "Ambience" tab
    When I add 3 sounds to the ambience
    Then the add button is the last item in the ambience

  Scenario: Holding a sound in the ambience reveals a remove button
    Given I have created a new scene
    And I have opened the "Ambience" tab
    And I have added a sound to the ambience
    When I hold on the sound
    Then a remove button appears on the sound

  Scenario: Holding a sound again in the ambience hides the remove button
    Given I have created a new scene
    And I have opened the "Ambience" tab
    And I have added a sound to the ambience
    And a remove button is visible on the sound
    When I hold on the sound again
    Then no remove button is visible on the sound

  Scenario: Clicking the remove button removes the sound from the ambience
    Given I have created a new scene
    And I have opened the "Ambience" tab
    And I have added a sound to the ambience
    And I am holding on the sound
    When I click the remove button on the sound
    Then the ambience has no sounds

  Scenario: Can create more than one scene
    Given I have created a new scene
    When I create another new scene
    Then I have 2 scenes
