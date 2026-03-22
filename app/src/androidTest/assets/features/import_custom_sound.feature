Feature: Import custom sound

  As a GM
  I want to import custom sound files stored locally on my device
  So that I can use my own audio in my game sessions.

  Scenario: Import a local sound file as a soundboard button
    Given a sound file "thunderstrike.mp3" is available on the device
    When I import "thunderstrike.mp3" as a soundboard sound
    And I select the category "natural phenomena"
    Then "thunderstrike.mp3" is available as a soundboard button under "natural phenomena"

  Scenario: Import a local sound file as a loopable track
    Given a sound file "forest_birds.mp3" is available on the device
    When I import "forest_birds.mp3" as a loopable track
    And I select the category "forest sound"
    And I select the intensity level "medium"
    Then "forest_birds.mp3" is available as a loopable track under "forest sound" with intensity "medium"

  Scenario Outline: Import sounds from different categories as soundboard buttons
    Given a sound file "<file>" is available on the device
    When I import "<file>" as a soundboard sound
    And I select the category "<category>"
    Then "<file>" is available as a soundboard button under "<category>"

    Examples:
      | file               | category             |
      | owl_hoot.mp3       | forest sound         |
      | rain_heavy.mp3     | natural phenomena    |
      | sword_clash.mp3    | combat               |

  Scenario Outline: Import loopable tracks with different intensity levels
    Given a sound file "<file>" is available on the device
    When I import "<file>" as a loopable track
    And I select the category "<category>"
    And I select the intensity level "<intensity>"
    Then "<file>" is available as a loopable track under "<category>" with intensity "<intensity>"

    Examples:
      | file               | category    | intensity |
      | campfire.mp3       | forest sound | low       |
      | battle_drums.mp3   | combat       | high      |
      | storm_rumble.mp3   | natural phenomena   | medium |

  Scenario: Importing a loopable track requires an intensity level
    Given a sound file "wind.mp3" is available on the device
    When I import "wind.mp3" as a loopable track
    And I select the category "natural phenomena"
    And I do not select an intensity level
    Then I am prompted to select an intensity level before completing the import

  Scenario: Attempt to import an unsupported file format
    Given a sound file "notes.txt" is available on the device
    When I attempt to import "notes.txt"
    Then I am shown an error message "Unsupported file format. Please select an audio file."

  Scenario: Import is cancelled by the user
    Given a sound file "dungeon_ambience.mp3" is available on the device
    When I begin importing "dungeon_ambience.mp3"
    And I cancel the import
    Then "dungeon_ambience.mp3" is not added to the soundboard or loopable tracks
