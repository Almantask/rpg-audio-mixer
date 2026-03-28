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
- ⚙️ gear icon top-right

### Tab Strip
- **Soundscapes** | **Soundboard** (active)
- Switching tabs does not affect ongoing playback

### Master Volume Slider
- A single horizontal slider controlling the output volume for **all** effects equally
- There is **no** per-effect volume control — Master only
- Snaps instantly to saved value on scene load — no animation

### Effect Button Grid
- 4-column grid layout
- Each button shows the FX name (truncated if needed)
- No category grouping — all effects appear in one flat grid

**Playing state:** when a sound is currently playing, the button glows/pulses and shows ⏸.

**Re-trigger behaviour:** tapping a button that is already playing starts a new instance from the beginning — the in-progress instance continues alongside the new one (overlap, not replace).

**Stop behaviour:** tapping ⏸ on a button fades that instance out and reverts the button to ▶.

### Drag-to-Reorder
Effect buttons can be long-pressed or dragged to reorder their position in the grid.

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
| Long-press and drag a button | Reorders it in the grid |
| Tap **+ ADD NEW EFFECT** | Opens the FX Selection overlay |

### FX Selection View (ADD NEW EFFECT)

Refer to: [add-fx-or-soundscape-to-scene-design.md](add-fx-or-soundscape-to-scene-design.md) 

---

## States

### Populated grid
Effects shown in 4-column grid. Some may be playing (glowing) simultaneously.

### Empty grid
Empty area with **+ ADD NEW EFFECT** as the primary CTA.

### Loading
Centred spinner until scene data is ready.

---

## Navigation

| Destination | Trigger |
|---|---|
| Soundscapes tab | Tap "Soundscapes" in tab strip |
| FX Selection overlay | Tap + ADD NEW EFFECT |
| Previous screen | Back arrow |
| Credits | ⚙️ gear icon |
