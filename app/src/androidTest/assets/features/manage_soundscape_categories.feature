Feature: Manage soundscape categories in library

  As a GM
  I want to view and navigate my soundscape categories in the library
  So that I can keep my audio collection organised and up to date.

  Scenario: Soundscape categories list shows all created categories
    Given I have created categories "Weather", "Interior", "Monsters"
    When I open the Library — Soundscapes tab
    Then I see "Weather", "Interior", and "Monsters" in the list

  Scenario: Each category card shows the track count per intensity level
    Given "Weather" has 3 tracks at level I, 5 at level II, and 2 at level III
    When I open the Library — Soundscapes tab
    Then the "Weather" card shows "I: 3 · II: 5 · III: 2"

  Scenario: Tapping the edit icon on a category opens the Soundscape Category Composer
    Given "Weather" is in the soundscape categories list
    When I tap the edit (pencil) icon on "Weather"
    Then I see the Soundscape Category Composer for "Weather"

  Scenario: Tapping a category card body also opens the Soundscape Category Composer
    Given "Interior" is in the soundscape categories list
    When I tap the "Interior" card body
    Then I see the Soundscape Category Composer for "Interior"

  Scenario: Soundscape categories list is empty before any categories are created
    Given I have not created any soundscape categories
    When I open the Library — Soundscapes tab
    Then I see the empty state illustration
    And I see a prompt to create my first category

  Scenario: Creating a new category lands in the Soundscape Category Composer
    When I tap "Create Category"
    And I enter the name "Arcane"
    And I confirm
    Then I see the Soundscape Category Composer for "Arcane"

  Scenario: The Archivist's Choice section is not shown
    When I open the Library — Soundscapes tab
    Then I do not see any "Archivist's Choice" section

  Scenario: Swiping a category moves it to the Trash
    Given "Weather" is in the soundscape categories list
    When I swipe right on the "Weather" card
    Then "Weather" is moved to the Trash
    And "Weather" is no longer in the soundscape categories list
