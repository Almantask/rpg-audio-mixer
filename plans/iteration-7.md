## Iteration 7 — Active Scene UI (Complexity)

### Relies on
- Advanced Audio Engine (Iteration 6)
- Add-to-Scene Picker

### Goal
Build the primary gameplay interface with live mixing and soundboard triggers.

### Build
- **Junction Tables**: `SceneSoundscapeCrossRef` and `SceneFxCrossRef` (with `displayOrder`).
- **Add-to-Scene Library Picker**: UI for assigning soundscapes/FX to a scene (reuse `MultiSelectPickerSheet`).
- **Soundscapes Tab**: Category cards with MIX sliders, intensity selectors (I/II/III). Drag-to-reorder.
- **Soundboard Tab**: 4-column grid of FX buttons. Re-trigger on tap. Drag-to-reorder, drag to flames to delete.
- **Master Sliders**: Atmosphere and FX volume controls.
- **Glow Border Animation**: Visual cues for playing state on cards and buttons.

### Linked Features
- `app/src/androidTest/assets/features/play_scene.feature`
- `app/src/androidTest/assets/features/add_fx_to_soundboard.feature`
- `app/src/androidTest/assets/features/add_soundscape_to_scene.feature`
- `app/src/androidTest/assets/features/category_playing_state.feature`
- `app/src/androidTest/assets/features/modify_intensity_level_of_loopable_track.feature`
- `app/src/androidTest/assets/features/play_a_sound_from_soundboard.feature`
- `app/src/androidTest/assets/features/play_a_track_in_a_loop_from_category_pool.feature`
- `app/src/androidTest/assets/features/play_mixed_track_loops_and_sounds.feature`
- `app/src/androidTest/assets/features/play_random_track.feature`
- `app/src/androidTest/assets/features/reorder_soundboard_effects.feature`
- `app/src/androidTest/assets/features/reorder_soundscape_categories.feature`
- `app/src/androidTest/assets/features/retrigger_soundboard_effect.feature`
- `app/src/androidTest/assets/features/soundscape_volume_control.feature`

### Linked Designs
- `docs/designs/active-scene-soundscapes-design.md`
- `docs/designs/ActiveScene-Soundscapes.html`
- `docs/designs/active-scene-soundboard-design.md`
- `docs/designs/ActiveScene-Soundboard.html`
- `docs/designs/add-fx-or-soundscape-to-scene-design.md`
- `docs/designs/Add-Fx-Or-Soundscape-ToScene.html`

### Android & Testing Implementation Details
- **Android**: State hoisting for `SceneAudioEngine` coordinating multiple underlying players. Heavy utilization of Compose `Modifier.draggable()` (or `ItemTouchHelper` via view interop depending on layout stability). Compute Master and Category volume scales in real-time ($Volume = sliderPosition^3$) providing to equal power controllers. Implement the custom glow border logic (`Modifier.border` paired with active animation loops).
- **Testing**: Espresso interactions ensuring `Modifier.draggable` lists safely update the Room indices. UI tests ensuring slider interactions trigger appropriate engine volume change signals.

