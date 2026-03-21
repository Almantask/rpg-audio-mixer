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

  Scenario: Viewing purchased sounds filtered by category
    Given I have bought sounds from multiple categories
      | sound        | category |
      | sword_clash  | combat   |
      | fire_crackle | nature   |
      | arrow_shot   | combat   |
    When I filter my purchased sounds by category "combat"
    Then I should only see sounds in category "combat"
      | sword_clash | arrow_shot |

  Scenario: Viewing purchased ambiences filtered by intensity level
    Given I have bought ambiences with different intensity levels
      | ambience     | intensity |
      | dark_forest  | low       |
      | battle_roar  | high      |
      | tavern_night | medium    |
    When I filter my purchased ambiences by intensity "low"
    Then I should only see ambiences with intensity "low"
      | dark_forest |

  Scenario: Attempting to buy a sound that is already purchased
    Given I have already bought the sound "sword_clash"
    When I try to buy the sound "sword_clash" again
    Then I should see a message that "sword_clash" is already purchased
    And the sound "sword_clash" should not be downloaded again
    And I should not be charged again for the sound "sword_clash"
