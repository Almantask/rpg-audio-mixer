Feature: Cannot modify bought scenes

  As a GM
  I cannot modify scenes I have purchased
  So that the content integrity of bought scenes is preserved.

  Scenario: Add button is not shown in a bought scene's soundboard
    Given I have a bought scene named "Epic Battle"
    When I open the "Soundboard" tab of the "Epic Battle" scene
    Then I do not see an add button

  Scenario: Add button is not shown in a bought scene's ambience
    Given I have a bought scene named "Epic Battle"
    When I open the "Ambience" tab of the "Epic Battle" scene
    Then I do not see an add button

  Scenario: Holding a sound in a bought scene does not show a remove button
    Given I have a bought scene named "Epic Battle"
    And I have opened the "Ambience" tab of the "Epic Battle" scene
    When I hold on a sound
    Then no remove button appears on the sound

  Scenario: Bought scenes are visually distinguished from created scenes
    Given I have a bought scene named "Epic Battle"
    And I have created a scene named "My Scene"
    When I view my scenes
    Then the "Epic Battle" scene is marked as purchased
    And the "My Scene" scene is not marked as purchased
