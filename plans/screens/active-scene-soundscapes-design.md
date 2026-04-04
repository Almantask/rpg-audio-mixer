# Active Scene — Soundscapes Tab — Screen Design

**Design References:**
- [`docs/designs/ActiveScene-Soundscapes.html`](../../docs/designs/ActiveScene-Soundscapes.html)
- [`docs/designs/ActiveScene-Soundscapes.png`](../../docs/designs/ActiveScene-Soundscapes.png)

---

## Purpose

The primary scene-control screen during a game session. The Soundscapes tab lets the GM manage looping atmospheric categories: starting/stopping playback, adjusting volume, picking random tracks, and switching intensity levels.

---

## Layout

```
┌─────────────────────────────────────┐
│  ← [Scene Name]                [⚙️]  │
├─────────────────────────────────────┤
│  [Soundscapes]  |  [Soundboard]     │  ← tab strip
├─────────────────────────────────────┤
│  Master Atmosphere                  │
│  ════════════════◉═══════           │  ← Master slider
│                                     │
│  ┌───────────────────────────────┐  │
│  │ Category name    [🎲] [▶/⏸]  │  │
│  │ Current track name            │  │
│  │ MIX  ════════◉════════        │  │
│  │ I ──────── II ──────── III    │  │  ← Intensity selector
│  └───────────────────────────────┘  │
│  ┌─ playing (glow border) ────────┐  │
│  │ Category name    [🎲] [⏸]    │  │
│  │ Current track name            │  │
│  │ MIX  ══════════◉══════        │  │
│  │ I ──────── II ──────── III    │  │
│  └───────────────────────────────┘  │
│  …                                  │
│                                     │
│  [ + ADD NEW SOUNDSCAPE ]           │
├─────────────────────────────────────┤
│  🏰 HOME  📖 CAMPAIGNS  🖼 SCENES  🎵 LIBRARY │
└─────────────────────────────────────┘
```

---

## Components

### Top Bar
- Back arrow → returns to previous screen (Scenes list or Session Scenes)
- Scene name as title
- ⚙️ gear icon top-right

### Tab Strip
- **Soundscapes** (active) | **Soundboard**
- Switching tabs does not affect playback

### Master Atmosphere Slider
- Full-width horizontal slider
- Controls the overall output volume for all soundscape categories
- Final volume per category = **Master × MIX** (multiplicative)
- Snaps instantly to saved value on scene load — no animation

### Soundscape Category Card (repeating)
Each category card contains:

| Element | Description |
|---|---|
| Category name | Displayed prominently |
| 🎲 d20 icon button | Picks a random track from this category and plays it immediately |
| ▶ / ⏸ button | Play or pause the current track |
| Current track name | Name of the track currently loaded/playing |
| MIX slider | Per-category relative volume; multiplicative with Master |
| Intensity selector | Three-position toggle: **I · II · III** — changes which tracks are eligible to play |

**Playing state:** the card shows a coloured glow / highlight border when audio is active.

**MIX slider snap:** snaps instantly to saved value on scene load.

### Drag-to-Reorder
Category cards can be long-pressed or dragged via a handle to reorder them.

### Add New Soundscape Button
- **+ ADD NEW SOUNDSCAPE** at the bottom of the category list
- Opens the Soundscape Selection view (see below)

### Bottom Navigation Bar
- No tab is highlighted (Active Scene is not a tab-level screen)

---

## Interactions & Behaviour

| Interaction | Result |
|---|---|
| Drag Master slider | Adjusts overall output volume for all categories in real time |
| Tap 🎲 d20 | Picks a random track from that category's current intensity pool; starts playing |
| Tap ▶ | Starts playing current/last track in that category |
| Tap ⏸ | Pauses current track; card loses playing state |
| Drag MIX slider | Adjusts that category's relative volume in real time |
| Tap I / II / III | Changes intensity level; next play picks from the new level's track pool |
| Drag card by handle | Reorders categories in the list |
| Long-press card | Drag card by handle to reorder categories in the list |
| Swipe right on card | Removes the category from the Scene |
| Tap **+ ADD NEW SOUNDSCAPE** | Opens the Soundscape Selection overlay |

### Volume Formula
`Actual output = Master × MIX` for each category independently.

### Intensity Levels
- **I** = calm/ambient — least tense
- **II** = moderate tension
- **III** = climactic/dramatic — most tense
- The GM switches manually; there is no automatic trigger

### Soundscape Selection View (ADD NEW SOUNDSCAPE)
- A simplified overlay with:
  - Back button (closes overlay, returns to Active Scene)
  - Scrollable list of all Soundscape Categories from the Library
  - Multi-select: GM taps one or more categories to add them to the scene
  - Confirm/add button

---

## States

### Scene loaded, no playback
All category cards show ▶. No glow borders. Sliders at saved positions.

### One or more categories playing
Relevant cards show ⏸ and a coloured glow border.

### No categories in scene
Empty area + **+ ADD NEW SOUNDSCAPE** as the primary CTA.

### Loading (scene opening)
Centred spinner until scene data is ready; sliders then snap to saved values.

---

## Navigation

| Destination | Trigger |
|---|---|
| Soundboard tab | Tap "Soundboard" in tab strip |
| Soundscape Selection overlay | Tap + ADD NEW SOUNDSCAPE |
| Previous screen | Back arrow |
| Credits | ⚙️ gear icon |
