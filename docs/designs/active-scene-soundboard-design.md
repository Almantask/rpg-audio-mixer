# Active Scene — Soundboard Tab — Screen Design

**Design References:**
- [`docs/designs/ActiveScene-Soundboard.html`](../../docs/designs/ActiveScene-Soundboard.html)
- [`docs/designs/ActiveScene-Soundboard.png`](../../docs/designs/ActiveScene-Soundboard.png)

---

## Purpose

The Soundboard tab sits alongside the Soundscapes tab within an Active Scene. It gives the GM a grid of one-shot FX buttons to trigger sound effects instantly during play — thunder, door creaks, combat sounds, etc.

---

## Layout

```
┌─────────────────────────────────────┐
│  ← [Scene Name]                [⚙️]  │
├─────────────────────────────────────┤
│  [Soundscapes]  |  [Soundboard]     │  ← tab strip
├─────────────────────────────────────┤
│  Master Volume                      │
│  ════════════════◉═══════           │  ← Master slider
│                                     │
│  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐ │
│  │ FX 1 │  │ FX 2 │  │ FX 3 │  │ FX 4 │ │
│  └──────┘  └──────┘  └──────┘  └──────┘ │
│  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐ │
│  │ FX 5 │  │ FX 6 │  │ FX 7 │  │ FX 8 │ │
│  └──────┘  └──────┘  └──────┘  └──────┘ │
│  …                                   │
│                                     │
│  [ + ADD NEW EFFECT ]               │
├─────────────────────────────────────┤
│  🏰 HOME  📖 CAMPAIGNS  🖼 SCENES  🎵 LIBRARY │
└─────────────────────────────────────┘
```

---

## Components

### Top Bar
- Back arrow → returns to previous screen
- Scene name as title
- **Session Lock Toggle** (🔒) → Disables destructive gestures (reordering, deleting) and scene switching.
- ⚙️ gear icon top-right → navigates to Credits

### Session Control Bar
- **Global Master Stop (Panic Button)**: A prominent button that immediately fades out all soundscapes and stops all sound effects.

### Scene Notes
- Expandable markdown-capable text area for storing DM cues, descriptions, and reminders specific to the scene.

### Master Volume Slider
- A single horizontal slider controlling the output volume for **all** effects equally
- There is **no** per-effect volume control — Master only
- **Natural Volume Progression**: Uses a **Cubic ($x^3$) mapping** for the volume scale to ensure a natural hearing progression.
- **Arcanum Motion**: Sliders use specific motion tokens for tactile feedback.
- Snaps instantly to saved value on scene load — no animation

### Effect Button Grid
- 4-column grid layout
- Each button shows the FX name (truncated if needed)
- No category grouping — all effects appear in one flat grid
- **Low-Latency Soundboard**: All FX MUST be played via **SoundPool** to ensure near-zero latency response when triggered.

**Playing state:** when a sound is currently playing, the button glows/pulses and shows ⏸.

**Re-trigger behaviour:** tapping a button that is already playing starts a new instance from the beginning — the in-progress instance continues alongside the new one (overlap, not replace).

**Stop behaviour:** tapping ⏸ on a button stops that instance and reverts the button to ▶.

### Drag-to-Reorder
Effect buttons can be long-pressed or dragged to reorder their position in the grid. **Disabled when Session Lock is active.**

### Add New Effect Button
- **+ ADD NEW EFFECT** pinned at the end of the grid (or as a dedicated button below the grid)
- Opens the FX Selection view (see below)

### Bottom Navigation Bar
- No tab highlighted (Active Scene is not a top-level tab screen)

---

## Interactions & Behaviour

| Interaction | Result |
|---|---|
| Drag Master slider | Adjusts output volume for all effects in real time |
| Tap an effect button (idle) | Starts playing; button glows/pulses and shows ⏸ |
| Tap an effect button (playing) | **Re-triggers** — new instance starts from beginning; prior instance continues |
| Tap ⏸ on a button | Stops that effect's current instance; button reverts to ▶ / idle state |
| Long-press and drag a button | Reorders it in the grid. **Disabled if Session Lock is ON.** |
| Hold and drag button to Flames area | Hold the button until a "Trash" zone with flames overlay appears at the bottom screen; dropping removes the effect from scene. **Disabled if Session Lock is ON.** |
| Tap **+ ADD NEW EFFECT** | Opens the FX Selection overlay |

### FX Selection View (ADD NEW EFFECT)
- A simplified overlay/sheet presenting:
  - Back button (closes without selecting)
  - Scrollable list or grid of all FX tracks from the global FX Library
  - Multi-select: GM picks one or more to add to the scene's soundboard
  - Confirm button adds selected effects

---

## States

### Populated grid
Effects shown in 4-column grid. Some may be playing (glowing) simultaneously.

### Empty grid
Empty area with **+ ADD NEW EFFECT** as the primary CTA.

### Loading
Centred spinner until scene data is ready.

### Error state
If an error occurs (e.g. audio file not found, playback failure), a **scrollable message box** appears as an overlay. The message box contains the error details and a dismiss button. Other effects that are playing are not interrupted.

---

## Navigation

| Destination | Trigger |
|---|---|
| Soundscapes tab | Tap "Soundscapes" in tab strip |
| FX Selection overlay | Tap + ADD NEW EFFECT |
| Previous screen | Back arrow |
| Credits | ⚙️ gear icon |
