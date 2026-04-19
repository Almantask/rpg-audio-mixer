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

### Docs to reference
- `docs/designs/active-scene-soundscapes-design.md`, `docs/designs/active-scene-soundboard-design.md`
- `docs/designs/add-fx-or-soundscape-to-scene-design.md`

