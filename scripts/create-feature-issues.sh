#!/usr/bin/env bash
#
# Creates one GitHub issue per .feature file in app/src/androidTest/assets/features/.
# Prerequisites: gh CLI authenticated (`gh auth login`).
#
# Usage:
#   ./scripts/create-feature-issues.sh                     # dry-run (prints issues)
#   ./scripts/create-feature-issues.sh --execute           # creates the issues
#   ./scripts/create-feature-issues.sh --execute --label "feature"  # with a label

set -euo pipefail

REPO="Almantask/rpg-audio-mixer"
EXECUTE=false
LABEL=""

for arg in "$@"; do
  case "$arg" in
    --execute) EXECUTE=true ;;
    --label)   shift; LABEL="${1:-}" ;;
    --label=*) LABEL="${arg#--label=}" ;;
  esac
done

create_issue() {
  local title="$1"
  local body="$2"

  if [ "$EXECUTE" = true ]; then
    local cmd=(gh issue create --repo "$REPO" --title "$title" --body "$body")
    if [ -n "$LABEL" ]; then
      cmd+=(--label "$LABEL")
    fi
    echo "Creating issue: $title"
    "${cmd[@]}"
    echo ""
  else
    echo "=========================================="
    echo "TITLE: $title"
    echo "------------------------------------------"
    echo "$body"
    echo "=========================================="
    echo ""
  fi
}

# ---------------------------------------------------------------------------
# 1. Add description to scene
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Add description to scene" \
  "## User Story

As a GM, I want to add a description to my scenes so that I can remember the purpose and context of each scene.

## Acceptance Criteria

### Scenario: Can add a description to a scene
- **Given** I have created a scene named \"Tavern\"
- **When** I add the description \"A lively inn with music and chatter\" to the \"Tavern\" scene
- **Then** the \"Tavern\" scene has the description \"A lively inn with music and chatter\"

### Scenario: Scene description is visible when viewing scenes
- **Given** I have created a scene named \"Tavern\" and the \"Tavern\" scene has the description \"A lively inn with music and chatter\"
- **When** I view my scenes
- **Then** I see the description \"A lively inn with music and chatter\" for the \"Tavern\" scene

### Scenario: Can update an existing description
- **Given** I have created a scene named \"Tavern\" and the \"Tavern\" scene has the description \"An old inn\"
- **When** I update the description of the \"Tavern\" scene to \"A lively inn with music and chatter\"
- **Then** the \"Tavern\" scene has the description \"A lively inn with music and chatter\"

### Scenario: Scene description is optional
- **Given** I have created a scene named \"Tavern\"
- **Then** the \"Tavern\" scene has no description

## Feature File
\`app/src/androidTest/assets/features/add_description_to_scene.feature\`"

# ---------------------------------------------------------------------------
# 2. Build your own scene
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Build your own scene" \
  "## User Story

As a GM, I want to build my own scene of sounds so that I can create a custom audio atmosphere for my game.

## Acceptance Criteria

### Scenario: New scene has ambience and soundboard tabs
- **When** I create a new scene
- **Then** I see an \"Ambience\" tab and a \"Soundboard\" tab

### Scenario: Soundboard tab starts empty
- **Given** I have created a new scene
- **When** I open the \"Soundboard\" tab
- **Then** the soundboard has no sounds

### Scenario: Soundboard tab shows an add button when empty
- **Given** I have created a new scene
- **When** I open the \"Soundboard\" tab
- **Then** I see an add button

### Scenario: Add button always appears at the end of the soundboard
- **Given** I have created a new scene and opened the \"Soundboard\" tab
- **When** I add 3 sounds to the soundboard
- **Then** the add button is the last item in the soundboard

### Scenario: Ambience tab starts empty
- **Given** I have created a new scene
- **When** I open the \"Ambience\" tab
- **Then** the ambience has no sounds

### Scenario: Ambience tab shows an add button when empty
- **Given** I have created a new scene
- **When** I open the \"Ambience\" tab
- **Then** I see an add button

### Scenario: Add button always appears at the end of the ambience
- **Given** I have created a new scene and opened the \"Ambience\" tab
- **When** I add 3 sounds to the ambience
- **Then** the add button is the last item in the ambience

### Scenario: Holding a sound in the ambience reveals a remove button
- **Given** I have created a new scene, opened the \"Ambience\" tab, and added a sound
- **When** I hold on the sound
- **Then** a remove button appears on the sound

### Scenario: Holding a sound again in the ambience hides the remove button
- **Given** I have created a new scene, opened the \"Ambience\" tab, added a sound, and a remove button is visible
- **When** I hold on the sound again
- **Then** no remove button is visible on the sound

### Scenario: Clicking the remove button removes the sound from the ambience
- **Given** I have created a new scene, opened the \"Ambience\" tab, added a sound, and am holding on it
- **When** I click the remove button on the sound
- **Then** the ambience has no sounds

### Scenario: Can create more than one scene
- **Given** I have created a new scene
- **When** I create another new scene
- **Then** I have 2 scenes

## Feature File
\`app/src/androidTest/assets/features/build_your_own_scene.feature\`"

# ---------------------------------------------------------------------------
# 3. Buy sounds and ambiences
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Buy sounds and ambiences" \
  "## User Story

As a GM, I want to buy ambience and soundboard sounds so that I can expand my audio library and use them in my games.

## Acceptance Criteria

### Scenario: Buying a soundboard sound automatically downloads it
- **Given** the sound \"sword_clash\" is available in the store
- **When** I buy the sound \"sword_clash\"
- **Then** the sound \"sword_clash\" should be downloaded automatically and marked as purchased

### Scenario: Buying an ambience automatically downloads it
- **Given** the ambience \"dark_forest\" is available in the store
- **When** I buy the ambience \"dark_forest\"
- **Then** the ambience \"dark_forest\" should be downloaded automatically and marked as purchased

### Scenario: Viewing all purchased sounds after buying
- **Given** I have bought the following sounds: sword_clash, fire_crackle, thunder_rumble
- **When** I view my purchased sounds
- **Then** I should see all purchased sounds: sword_clash, fire_crackle, thunder_rumble

### Scenario: Viewing all purchased ambiences after buying
- **Given** I have bought the following ambiences: dark_forest, tavern_night, battlefield
- **When** I view my purchased ambiences
- **Then** I should see all purchased ambiences: dark_forest, tavern_night, battlefield

### Scenario: Not possible to buy an already bought sound
- **Given** I have bought the sound \"sword_clash\"
- **When** I open the shop
- **Then** I should not see \"sword_clash\"

## Feature File
\`app/src/androidTest/assets/features/buy_sounds.feature\`"

# ---------------------------------------------------------------------------
# 4. Cannot modify bought scenes
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Cannot modify bought scenes" \
  "## User Story

As a GM, I cannot modify scenes I have purchased so that the content integrity of bought scenes is preserved.

## Acceptance Criteria

### Scenario: Add button is not shown in a bought scene's soundboard
- **Given** I have a bought scene named \"Epic Battle\"
- **When** I open the \"Soundboard\" tab of the \"Epic Battle\" scene
- **Then** I do not see an add button

### Scenario: Add button is not shown in a bought scene's ambience
- **Given** I have a bought scene named \"Epic Battle\"
- **When** I open the \"Ambience\" tab of the \"Epic Battle\" scene
- **Then** I do not see an add button

### Scenario: Holding a sound in a bought scene does not show a remove button
- **Given** I have a bought scene named \"Epic Battle\" and opened the \"Ambience\" tab
- **When** I hold on a sound
- **Then** no remove button appears on the sound

### Scenario: Bought scenes are visually distinguished from created scenes
- **Given** I have a bought scene named \"Epic Battle\" and a created scene named \"My Scene\"
- **When** I view my scenes
- **Then** the \"Epic Battle\" scene is marked as purchased and the \"My Scene\" scene is not marked as purchased

## Feature File
\`app/src/androidTest/assets/features/cannot_modify_bought_scenes.feature\`"

# ---------------------------------------------------------------------------
# 5. Change volume
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Change volume" \
  "## User Story

As a GM, I want to control volume at different levels so that I can fine-tune the audio atmosphere during my game.

## Acceptance Criteria

### Scenario: Change global volume affects all audio
- **Given** global volume is at 100%, soundboard volume is at 50%, \"forest_ambience\" loopable track is at 100%, and \"dragon roar\" soundboard track is playing
- **When** I set the global volume to 50%
- **Then** \"forest_ambience\" plays at 50% volume and \"dragon roar\" plays at 25% volume

### Scenario Outline: Change the volume of a loopable track individually
- **Given** the loopable track is playing
- **When** I set the volume to a specific percentage
- **Then** it plays at that volume

| track           | volume |
|-----------------|--------|
| forest_ambience | 50     |
| tavern_music    | 75     |
| battle_drums    | 30     |

### Scenario: Local loopable track volume change does not affect global volume
- **Given** the global volume is at 100%
- **When** I set the volume of \"forest_ambience\" to 50%
- **Then** the global volume remains at 100%

### Scenario: Change soundboard volume separately from loopable tracks
- **Given** the soundboard volume is at 100% and the loopable track \"forest_ambience\" is playing at 100%
- **When** I set the soundboard volume to 75%
- **Then** soundboard sounds play at 75% volume and \"forest_ambience\" continues to play at 100% volume

## Feature File
\`app/src/androidTest/assets/features/change_volume.feature\`"

# ---------------------------------------------------------------------------
# 6. Clone scene
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Clone scene" \
  "## User Story

As a GM, I want to clone an existing scene so that I can use it as a starting point for a similar scene.

## Acceptance Criteria

### Scenario: Cloning a scene creates a new scene
- **Given** I have created a scene named \"Forest Night\"
- **When** I clone the \"Forest Night\" scene
- **Then** I have 2 scenes

### Scenario: Cloned scene contains the same sounds as the original
- **Given** I have a scene \"Forest Night\" with \"owl_hooting\" in the ambience
- **When** I clone the \"Forest Night\" scene as \"Forest Dawn\"
- **Then** the \"Forest Dawn\" ambience contains \"owl_hooting\"

### Scenario: Cloned scene is independent of the original
- **Given** I have a scene \"Forest Night\" with \"owl_hooting\" in the soundboard and have cloned it as \"Forest Dawn\"
- **When** I add \"bird_song\" to the \"Forest Dawn\" soundboard
- **Then** the \"Forest Night\" soundboard does not contain \"bird_song\"

### Scenario: Modifying the original does not affect the clone
- **Given** I have a scene \"Forest Night\" with \"owl_hooting\" in the soundboard and have cloned it as \"Forest Dawn\"
- **When** I add \"wolf_howl\" to the \"Forest Night\" soundboard
- **Then** the \"Forest Dawn\" soundboard does not contain \"wolf_howl\"

### Scenario: Cloned scene can be renamed
- **Given** I have created a scene named \"Forest Night\"
- **When** I clone the \"Forest Night\" scene as \"Forest Dawn\"
- **Then** I see the \"Forest Dawn\" scene in my scenes list

## Feature File
\`app/src/androidTest/assets/features/clone_scene.feature\`"

# ---------------------------------------------------------------------------
# 7. Delete scene
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Delete scene" \
  "## User Story

As a GM, I want to delete scenes I no longer need so that I can keep my scene list organized.

## Acceptance Criteria

### Scenario: Can delete an existing scene
- **Given** I have created a scene named \"Old Scene\"
- **When** I delete the \"Old Scene\" scene
- **Then** I do not see \"Old Scene\" in my scenes list

### Scenario: Deleted scene is removed from the list
- **Given** I have created scenes named \"Scene A\" and \"Scene B\"
- **When** I delete the \"Scene A\" scene
- **Then** I do not see \"Scene A\" in my scenes list and I still see \"Scene B\"

### Scenario: Deleting a scene does not affect other scenes
- **Given** I have created scenes named \"Scene A\", \"Scene B\", and \"Scene C\"
- **When** I delete the \"Scene B\" scene
- **Then** I have 2 scenes

## Feature File
\`app/src/androidTest/assets/features/delete_scene.feature\`"

# ---------------------------------------------------------------------------
# 8. Import custom sound
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Import custom sound" \
  "## User Story

As a GM, I want to import custom sound files stored locally on my device so that I can use my own audio in my game sessions.

## Acceptance Criteria

### Scenario Outline: Import sounds from different categories as soundboard buttons
- **Given** a sound file is available on the device
- **When** I import it as a soundboard sound and select a category
- **Then** it is available as a soundboard button under that category

| file             | category          |
|------------------|-------------------|
| owl_hoot.mp3     | forest sound      |
| rain_heavy.mp3   | natural phenomena |
| sword_clash.mp3  | combat            |

### Scenario Outline: Import loopable tracks with different intensity levels
- **Given** a sound file is available on the device
- **When** I import it as a loopable track, select a category and intensity level
- **Then** it is available as a loopable track under that category with that intensity

| file             | category          | intensity |
|------------------|-------------------|-----------|
| campfire.mp3     | forest sound      | low       |
| battle_drums.mp3 | combat            | high      |
| storm_rumble.mp3 | natural phenomena | medium    |

### Scenario: Importing a loopable track requires an intensity level
- **Given** a sound file \"wind.mp3\" is available on the device
- **When** I import \"wind.mp3\" as a loopable track, select a category, but do not select an intensity level
- **Then** I am prompted to select an intensity level before completing the import

### Scenario Outline: Non-audio files are not visible in the import file picker
- **Given** a file is stored on the device
- **When** I open the import file picker
- **Then** the file is not shown in the picker

| file             |
|------------------|
| notes.txt        |
| image.jpg        |
| spreadsheet.xlsx |
| document.pdf     |
| archive.zip      |

### Scenario: File with audio extension but invalid content cannot be imported
- **Given** a file \"fake_audio.mp3\" with invalid audio content is stored on the device
- **When** I attempt to import \"fake_audio.mp3\"
- **Then** I am shown an error message \"The selected file could not be read as audio.\"

## Feature File
\`app/src/androidTest/assets/features/import_custom_sound.feature\`"

# ---------------------------------------------------------------------------
# 9. Modify intensity level of loopable track
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Modify intensity level of loopable track" \
  "## User Story

As a GM, I want to modify the intensity level of a loopable track category so that I can fine-tune the ambience to match the mood of the scene.

## Technical Context
Folder structure: \`Sounds/Ambience/{category}/{intensityLevel}/\`
For example: \`Ambience/rain/1/lightrain.mp3\`
Intensity folders: 1 = low, 2 = medium, 3 = high

## Acceptance Criteria

### Scenario Outline: Each ambience category plays a random track from the correct intensity folder
- **When** I slide the category intensity to a level
- **Then** a random track from the corresponding \`Ambience/{category}/{folder}\` is played

| category | level  | folder |
|----------|--------|--------|
| forest   | low    | 1      |
| forest   | medium | 2      |
| forest   | high   | 3      |
| tavern   | low    | 1      |
| dungeon  | high   | 3      |

### Scenario: A warning message is shown when there are no tracks at the selected intensity level
- **Given** the \"dungeon\" ambience folder for intensity level \"high\" is empty
- **When** I slide the \"dungeon\" intensity to high
- **Then** a warning message is shown

## Feature File
\`app/src/androidTest/assets/features/modify_intensity_level_of_loopable_track.feature\`"

# ---------------------------------------------------------------------------
# 10. Play a sound from soundboard
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Soundboard playback" \
  "## User Story

As a GM, I want each sound button to play its own sound so that I can quickly trigger different effects.

## Acceptance Criteria

### Scenario: Pressing a sound button plays that sound
- **When** I press the \"whip\" sound button
- **Then** the \"whip\" sound should be played

### Scenario: Pressing a different sound button plays a different sound
- **When** I press the \"dog_bark\" sound button
- **Then** the \"dog_bark\" sound should be played

### Scenario: Pressing two sound buttons plays sounds at the same time
- **Given** I had pressed the \"whip\" sound button
- **When** I press the \"owl_hooting\" sound button
- **Then** the sounds should be played at the same time: whip, owl_hooting

## Feature File
\`app/src/androidTest/assets/features/play_a_sound_from_soundboard.feature\`"

# ---------------------------------------------------------------------------
# 11. Play a track in a loop from category pool
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Play a track in a loop from category pool" \
  "## User Story

As a GM, I play a track from a category pool in a loop so that I can have background ambience without having to select a specific track.

## Acceptance Criteria

_Scenarios to be defined._

## Feature File
\`app/src/androidTest/assets/features/play_a_track_in_a_loop_from_category_pool.feature\`"

# ---------------------------------------------------------------------------
# 12. Play mixed track loops and sounds
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Play mixed track loops and sounds" \
  "## User Story

As a GM, I want mixed multiple looping sound tracks and sounds from soundboard so that I can create background ambient soundscapes with an option to add sounds on the fly.

## Acceptance Criteria

_Scenarios to be defined._

## Feature File
\`app/src/androidTest/assets/features/play_mixed_track_loops_and_sounds.feature\`"

# ---------------------------------------------------------------------------
# 13. Search sounds
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Search sounds" \
  "## User Story

As a GM, I want to search and filter sounds and ambiences so that I can quickly find the audio I need for my game.

## Acceptance Criteria

### Scenario: Filter sounds by category
- **Given** there are sounds available in multiple categories (sword_clash/combat, fire_crackle/nature, arrow_shot/combat)
- **When** I filter sounds by category \"combat\"
- **Then** I should only see sounds in category \"combat\": sword_clash, arrow_shot

### Scenario: Filter sounds by name
- **Given** there are sounds available: sword_clash, fire_crackle, sword_slash
- **When** I search sounds by name \"sword\"
- **Then** I should only see sounds matching \"sword\": sword_clash, sword_slash

### Scenario: Filter by type - soundboard
- **Given** there are sounds and ambiences available (sword_clash/soundboard, dark_forest/ambience, fire_crackle/soundboard)
- **When** I filter by type \"soundboard\"
- **Then** I should only see soundboard sounds: sword_clash, fire_crackle

### Scenario: Filter by type - ambience
- **Given** there are sounds and ambiences available (sword_clash/soundboard, dark_forest/ambience, tavern_night/ambience)
- **When** I filter by type \"ambience\"
- **Then** I should only see ambiences: dark_forest, tavern_night

### Scenario: Filter ambiences by intensity level
- **Given** there are ambiences with different intensity levels (dark_forest/low, battle_roar/high, tavern_night/medium)
- **When** I filter ambiences by intensity \"low\"
- **Then** I should only see ambiences with intensity \"low\": dark_forest

### Scenario: Filter sounds by scene
- **Given** there are sounds associated with different scenes (sword_clash/dungeon, fire_crackle/forest, arrow_shot/dungeon)
- **When** I filter sounds by scene \"dungeon\"
- **Then** I should only see sounds in scene \"dungeon\": sword_clash, arrow_shot

## Feature File
\`app/src/androidTest/assets/features/search_sounds.feature\`"

# ---------------------------------------------------------------------------
# 14. See a loopable track progress
# ---------------------------------------------------------------------------
create_issue \
  "Feature: See a loopable track progress" \
  "## User Story

As a GM, I want to see which loopable track is currently playing so that I know what background ambience is active.

## Acceptance Criteria

### Scenario: Starting a loop shows it as now looping
- **When** I press the \"forest\" loop button
- **Then** I should see \"forest\" is now looping

### Scenario: When no loop is playing, nothing is shown as looping
- **Then** I should see nothing is looping

### Scenario: Starting a different loop replaces the current looping indicator
- **Given** I had pressed the \"forest\" loop button
- **When** I press the \"tavern\" loop button
- **Then** I should see \"tavern\" is now looping

## Feature File
\`app/src/androidTest/assets/features/see_a_loopable_track_progress.feature\`"

# ---------------------------------------------------------------------------
# 15. Setup profile
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Setup profile" \
  "## User Story

As a GM, I want to setup my profile so that I can have my preferred soundboard buttons and loop options ready for my game.

## Acceptance Criteria

_Scenarios to be defined._

## Feature File
\`app/src/androidTest/assets/features/setup_profile.feature\`"

# ---------------------------------------------------------------------------
# 16. Setup UI based on profile
# ---------------------------------------------------------------------------
create_issue \
  "Feature: Setup UI based on profile" \
  "## User Story

As a GM, I want to have loop options as well as soundboard buttons to be driven by profiles so that I can quickly switch between different setups for different games.

## Acceptance Criteria

_Scenarios to be defined._

## Feature File
\`app/src/androidTest/assets/features/setup_ui_based_on_profile.feature\`"

# ---------------------------------------------------------------------------
# 17. View created scenes
# ---------------------------------------------------------------------------
create_issue \
  "Feature: View created scenes" \
  "## User Story

As a GM, I want to see all my created scenes so that I can quickly find and open the scene I need.

## Acceptance Criteria

### Scenario: A newly created scene appears in the scenes list
- **When** I create a new scene named \"Tavern\"
- **Then** I see the \"Tavern\" scene in my scenes list

### Scenario: All created scenes appear in the scenes list
- **Given** I have created scenes named \"Tavern\", \"Forest\", and \"Dungeon\"
- **When** I view my scenes
- **Then** I see the \"Tavern\", \"Forest\", and \"Dungeon\" scenes in my scenes list

### Scenario: Opening a scene shows its contents
- **Given** I have created a scene named \"Tavern\"
- **When** I open the \"Tavern\" scene
- **Then** I see the \"Ambience\" tab and the \"Soundboard\" tab

## Feature File
\`app/src/androidTest/assets/features/view_created_scenes.feature\`"

# ---------------------------------------------------------------------------
echo ""
if [ "$EXECUTE" = true ]; then
  echo "Done! All 17 issues have been created."
else
  echo "Dry run complete. 17 issues would be created."
  echo "Run with --execute to actually create the issues:"
  echo "  ./scripts/create-feature-issues.sh --execute"
fi
