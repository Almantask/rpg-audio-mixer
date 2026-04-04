Feature: System audio handling

  As a GM
  I want the app to handle OS-level audio focus, background playback, and lock screen controls gracefully
  So that my campaign audio does not clash with system events and can be controlled quickly.

  Scenario: Audio focus loss pauses all sounds immediately
    Given the app is playing a soundscape and a soundboard effect
    When the device receives a system audio interruption (e.g., an incoming phone call or alarm)
    Then all playing audio in the app pauses immediately
    And the app visually reflects the paused state on the active playing cards
    
  Scenario: Audio resumes when transient audio focus is regained
    Given audio has been paused by a brief system event (e.g., a notification sound)
    When the system restores audio focus to the app
    Then the previously playing loops and soundscapes resume automatically
    
  Scenario: App plays in background when minimized
    Given the app is playing audio loops on the Active Scene screen
    When I minimize the app to view my notes in another app
    Then the audio continues to play seamlessly in the background

  Scenario: Media controller appears on lock screen and notification shade
    Given the app is playing a soundscape loop
    When I lock the device
    Then the lock screen displays a media player for Arcanum Audio
    And it shows the currently playing scene and master track information

  Scenario: Lock screen player has play/pause and next track functionality
    Given the lock screen media player is visible
    When I tap pause
    Then the app audio pauses
    When I tap next track
    Then the app triggers a random track (d20 behavior) from the currently prominent playing category
