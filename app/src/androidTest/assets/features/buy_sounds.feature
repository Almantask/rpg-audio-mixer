Feature: Import audio from device

  As a GM
  I want to import custom audio files stored on my device as either FX or soundscape layers
  So that I can use my own recordings and purchased tracks in my game.

  # Note: In-app purchases are out of scope. All audio is user-imported from the device.

  Scenario: Import an audio file as an FX track from the Sound Effects tab
    Given an audio file "wolf_howl.mp3" is on my device
    When I tap "Import FX" and select "wolf_howl.mp3"
    Then "wolf_howl.mp3" appears in the Sound Effects library

  Scenario: Import an audio file as a soundscape layer via the Composer
    Given an audio file "light_rain.mp3" is on my device
    And I am in the Soundscape Category Composer for "Weather"
    When I tap "Invoke New Layer" and select "light_rain.mp3"
    Then a new layer "light_rain.mp3" appears in the Composer

  Scenario: The file picker filters out non-audio files
    When I open any import file picker
    Then only audio files are shown (images, documents, and archives are not visible)

  Scenario: Importing an FX track with a duplicate name still adds it as a new entry
    Given "wolf_howl.mp3" already exists in the FX library
    When I import another file also named "wolf_howl.mp3"
    Then both entries appear in the library

  Scenario: An invalid audio file cannot be imported and shows an error
    Given "fake_audio.mp3" has invalid audio content
    When I attempt to import "fake_audio.mp3"
    Then I see an error message that the file could not be read as audio
    And the library is unchanged
