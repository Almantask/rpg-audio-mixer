Feature: Delete scene

  As a GM
  I want to delete scenes I no longer need
  So that I can keep my scene list organized.

  Scenario: Swiping a scene removes it from the list and makes it temporarily unavailable
    Given I have created a scene named "Old Scene"
    When I swipe right on the "Old Scene" card
    Then "Old Scene" becomes temporarily unavailable
    And I do not see "Old Scene" in my scenes list

  Scenario: Temporarily unavailable scenes are permanently deleted after 7 days
    Given a scene named "Old Scene" is temporarily unavailable
    When 7 days pass
    Then "Old Scene" is permanently deleted

  Scenario: A temporarily unavailable scene can be restored
    Given a scene named "Old Scene" is temporarily unavailable
    When I restore the "Old Scene" scene
    Then "Old Scene" is no longer temporarily unavailable
    And I see "Old Scene" in my scenes list

  Scenario: Deleted scene is removed from the list but others remain
    Given I have created scenes named
      | Scene A |
      | Scene B |
    When I swipe right on the "Scene A" card
    Then "Scene A" becomes temporarily unavailable
    And I still see "Scene B" in my scenes list

  Scenario: Deleting a scene does not affect other scenes
    Given I have created scenes named
      | Scene A |
      | Scene B |
      | Scene C |
    When I swipe right on the "Scene B" card
    Then I have 2 scenes
