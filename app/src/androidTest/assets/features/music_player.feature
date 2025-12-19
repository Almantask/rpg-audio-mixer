Feature: Music playback by genre

  Scenario: Start playback
    Given the app is launched
    When I tap the "Play" button
    Then playback should start