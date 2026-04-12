@iter9
Feature: Session Lock

  As a GM
  I want to lock the active scene controls
  So that I don't accidentally change the audio during a gameplay session

  Background:
    Given I have a scene "Dungeon Depth"
    And I am on the Active Scene screen for "Dungeon Depth"

  Scenario: Locking the session disables all interactive controls
    When I tap the "Lock" icon
    Then the "Lock" icon should appear in a "Locked" state
    And the Master Atmosphere slider should be disabled
    And the Master Soundboard volume slider should be disabled
    And all category play/pause buttons should be disabled
    And all category d20 random buttons should be disabled
    And all intensity selectors should be disabled
    And all MIX sliders should be disabled
    And the "Add New Soundscape" button should be hidden
    And the "Add New Effect" button should be hidden

  Scenario: Unlocking the session restores control
    Given the session is locked
    When I long-press the "Lock" icon to unlock
    Then the "Lock" icon should appear in an "Unlocked" state
    And the Master Atmosphere slider should be enabled
    And all category play/pause buttons should be enabled

  Scenario: Gestures are ignored while locked
    Given the session is locked
    When I try to drag the "Master Atmosphere" slider to "0%"
    Then the Master Atmosphere volume should still be at its original level
    When I try to swipe between "Soundscapes" and "Soundboard" tabs
    Then the current tab should not change
