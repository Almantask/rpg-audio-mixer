Feature: Retrigger soundboard effect

  As a GM
  I want tapping a playing effect to restart it as a new instance
  So that I can layer multiple rapid-fire stabs of the same sound during combat.

  Scenario: Tapping a playing effect button does not stop the current instance
    Given I have tapped "Thunder Crack" and it is currently playing
    When I tap "Thunder Crack" again
    Then the first "Thunder Crack" instance continues playing
    And a second "Thunder Crack" instance starts from the beginning

  Scenario: Multiple re-triggers of the same effect all play simultaneously
    Given I tap "Sword Clash" three times in quick succession
    Then three simultaneous instances of "Sword Clash" are playing

  Scenario: Re-triggering one effect does not affect other playing effects
    Given "Thunder Crack" and "Wolf Howl" are both playing
    When I tap "Thunder Crack" again
    Then a new "Thunder Crack" instance starts
    And "Wolf Howl" continues uninterrupted

  Scenario: Tapping stop on an effect stops only that instance
    Given "Thunder Crack" is playing (showing the pause icon)
    When I tap the pause icon on "Thunder Crack"
    Then "Thunder Crack" stops and the button returns to the idle state
