Feature: Delete scene

  As a GM
  I want to delete scenes I no longer need
  So that I can keep my scene list organized.

  Scenario: Can delete an existing scene
    Given I have created a scene named "Old Scene"
    When I delete the "Old Scene" scene
    Then I do not see "Old Scene" in my scenes list

  Scenario: Deleted scene is removed from the list
    Given I have created scenes named
      | Scene A |
      | Scene B |
    When I delete the "Scene A" scene
    Then I do not see "Scene A" in my scenes list
    And I still see "Scene B" in my scenes list

  Scenario: Deleting a scene does not affect other scenes
    Given I have created scenes named
      | Scene A |
      | Scene B |
      | Scene C |
    When I delete the "Scene B" scene
    Then I have 2 scenes
