Feature: Import custom sound

  As a GM
  I want to import custom sound files stored locally on my device
  So that I can use my own audio in my game sessions.

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

  Scenario Outline: Non-audio files are not visible in the import file picker
    Given a file "<file>" is stored on the device
    When I open the import file picker
    Then "<file>" is not shown in the picker

    Examples:
      | file             |
      | notes.txt        |
      | image.jpg        |
      | spreadsheet.xlsx |
      | document.pdf     |
      | archive.zip      |

  Scenario: File with audio extension but invalid content cannot be imported
    Given a file "fake_audio.mp3" with invalid audio content is stored on the device
    When I attempt to import "fake_audio.mp3"
    Then I am shown an error message "The selected file could not be read as audio."
