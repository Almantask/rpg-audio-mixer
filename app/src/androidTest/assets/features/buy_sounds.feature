Feature: Buy sounds and ambiences

  As a GM
  I want to buy ambience and soundboard sounds
  So that I can expand my audio library and use them in my games.

  Scenario: Buying a soundboard sound automatically downloads it
    Given the sound "sword_clash" is available in the store
    When I buy the sound "sword_clash"
    Then the sound "sword_clash" should be downloaded automatically
    And the sound "sword_clash" should be marked as purchased

  Scenario: Buying an ambience automatically downloads it
    Given the ambience "dark_forest" is available in the store
    When I buy the ambience "dark_forest"
    Then the ambience "dark_forest" should be downloaded automatically
    And the ambience "dark_forest" should be marked as purchased

  Scenario: Viewing all purchased sounds after buying
    Given I have bought the following sounds
      | sword_clash | fire_crackle | thunder_rumble |
    When I view my purchased sounds
    Then I should see all purchased sounds
      | sword_clash | fire_crackle | thunder_rumble |

  Scenario: Viewing all purchased ambiences after buying
    Given I have bought the following ambiences
      | dark_forest | tavern_night | battlefield |
    When I view my purchased ambiences
    Then I should see all purchased ambiences
      | dark_forest | tavern_night | battlefield |

  Scenario: not possible to buy an already baught sound
    Given I have baught the sound "sword_clash"
    When I open the shop
    Then I should not see "sword_clash"
