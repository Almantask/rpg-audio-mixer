# Arcanum Audio — Design Specification

> Derived from design review sessions. All decisions below are confirmed and ready for implementation.

---

## 1. Branding & Theme

- App name: **Arcanum Audio**
- Theme: dark only
- Colour palette: black backgrounds, gold/amber typography and accents, purple/pink/gold gradient sliders

---

## 2. Navigation

### Bottom Navigation Bar

| Tab | Icon | Label |
|---|---|---|
| 1 | 🏰 Castle | HOME |
| 2 | 📖 Storybook | CAMPAIGNS |
| 3 | 🖼 Picture frame | SCENES |
| 4 | 🎵 Music note | LIBRARY |

### Screen Hierarchy

```
Home
└── Campaigns (tab)
    └── Campaign → Sessions list
        └── Session → Session Scenes list
                       └── Active Scene (Soundscapes + Soundboard tabs)

Scenes (tab) — global flat list of all scenes
Library (tab)
    ├── Soundscapes tab → Soundscape Categories list
    │   └── Soundscape Category Composer
    └── Sound Effects tab → FX Library
```

### Back Navigation

The back arrow always navigates to the previous screen — no contextual parent jumps.

### Gear Icon (⚙️)

The ⚙️ gear icon present on every screen navigates to the **Credits** screen. No separate Settings screen.

---

## 3. Data Model

### Hierarchy

- **Campaign** — list of sessions
- **Session** — list of scenes
- **Scene** — a reusable location/moment. Scenes are **global** (not scoped to a session). The same scene can be added to multiple sessions; editing it updates it everywhere.

### Audio Concepts

| Term | Definition |
|---|---|
| **Track** | A single playable audio file |
| **Soundscape / Layer** | Synonymous — a named composition of multiple tracks |
| **Category** | A named group (e.g. Weather) that holds one active Soundscape at a time |
| **FX / Sound Effect** | A one-shot audio file played from the Soundboard |

### Soundscape Categories

Entirely user-defined — created, named, and managed through the Soundscape Category Composer. The examples in the designs (Weather, Interior, Monsters, Arcane) are illustrative only.

### Intensity Levels (I / II / III)

Represent dramatic stakes: Level I = calm, Level III = tense/climactic. The DM switches between levels manually — no automatic triggering.

---

## 4. Screen-by-Screen Specification

### 4.1 Home

- **Active Campaign hero card:** always the most recently played campaign (automatic, no manual control). Tapping **ENTER** navigates to that campaign's Sessions list.
- **Resume Journey card:** shows the last scene opened in the active campaign. Tapping **ENTER** starts that scene fresh with a ~2–3 s fade-in. Remove the "Progress: 65%" element — design mistake.
- **Top Atmosphere:** global all-time most-played loopable track.
- **Legendary Action:** global all-time most-played FX.

### 4.2 Campaigns

- List sorted by most recently played, most recent at top.
- **RESUME** button on any campaign card navigates to that campaign's Sessions list (same behaviour as ENTER).
- **Empty state:** illustration + "Scribe New Tale" prompt button.
- Cover art: user picks an image from the device's photo library.
- Remove the CURRENT badge inconsistency — active campaign is always the most recently played one.

### 4.3 Sessions (within a Campaign)

- Sorted by date, most recent at top.
- Remove the **FILTER** button — no explicit filtering needed.
- Cover art: user picks from device photo library.
- **ADD NEW SESSION** button at bottom.

### 4.4 Scenes (Global — SCENES Tab)

- Flat list of all scenes ever created, regardless of campaign/session.
- **Tapping a scene card** → opens the Active Scene screen (no playback starts).
- **Tapping ▶ on a scene card** → opens the Active Scene screen AND starts playback (fresh start, ~2–3 s fade-in).
- Scene **tags:** user picks from a fixed list + can add custom tags.
- **Add New Scene** button at bottom.

### 4.5 Session Scenes (within a Session)

- Same card behaviour as global Scenes list (card = open only, ▶ = open + play).
- **Import Scene** button at bottom to add existing global scenes to the session.

### 4.6 Active Scene — Soundscapes Tab

- **Master Atmosphere slider:** controls overall output. Final volume per category = Master × MIX (multiplicative).
- **Per-category MIX slider:** controls relative balance of that category.
- **Sliders on scene load:** snap instantly to saved values (no animation).
- **d20 die button** (replaces ↺): picks a random track from that category and starts playing it.
- **Play/pause button:** plays or pauses the current track in the category.
- **Playing state:** coloured glow / highlight border around the card.
- **Category cards:** drag to reorder.
- **ADD NEW SOUNDSCAPE:** opens a simplified Soundscape Categories selection view (not the full Library) with a back button and multi-selection — user picks one or more categories to add to the scene.

### 4.7 Active Scene — Soundboard Tab

- **Master slider:** single volume control for all effects (no per-effect volume).
- **Effect buttons:** 4-column grid; drag to reorder; no grouping.
- **Tapping an effect while playing:** re-triggers — a new instance starts from the beginning, overlapping.
- **ADD NEW EFFECT:** opens a simplified FX selection view with a back button and multi-selection.
- **Playing state:** button glows/pulses and switches to ⏸; tapping ⏸ stops and reverts to ▶.

### 4.8 Switching Scenes

Back to Scenes list → select new scene → old scene crossfades out while new scene fades in simultaneously over **~2–3 seconds**.

### 4.9 Audio Library — Soundscapes Tab

- Lists all user-created Soundscape Categories with track counts per intensity level.
- Remove **"The Archivist's Choice"** section — design mistake.
- **✏️ edit icon** on a category → opens the Soundscape Category Composer for that category.

### 4.10 Soundscape Category Composer

- Shows current layers (Soundscapes) with intensity level and individual MIX sliders.
- **INVOKE NEW LAYER:** opens the device's native file picker to select a local audio file.
- No limit on number of layers.
- **SAVE COMPOSITION:** saves globally to the category — updates everywhere that category is used (no per-scene versioning).

### 4.11 Audio Library — Sound Effects Tab

- Remove **BUY MORE** button (out of scope).
- Remove **heart / favourite** icon — design mistake.
- Replace **⋮ three-dot menu** with a **✏️ pencil icon** → opens a track edit screen with: Name, Tags, Delete.
- **IMPORT FX** button: opens device's native file picker; imported track is added to the global FX library.
- **Mini player** (bottom bar): visible on Library screen only; navigating away stops playback and hides it.

### 4.12 Credits ("Behind the Screen")

- Reached via the ⚙️ gear icon from any screen.
- Contains: developer credits, app version, support link, docs link, Discord link, email link.

---

## 5. Design Corrections (Remove from Designs)

| Element | Location | Action |
|---|---|---|
| Progress: 65% bar | Home — Resume Journey card | Remove |
| The Archivist's Choice | Audio Library — Soundscapes | Remove |
| Heart / favourite icon | Audio Library — FX track rows | Remove |
| ⋮ three-dot menu | Audio Library — FX track rows | Replace with ✏️ |
| ↺ refresh icon | Active Scene — Soundscape category cards | Replace with d20 icon |
| FILTER button | Sessions screen | Remove |
| BUY MORE / BUY SOUNDS buttons | FX Library, various | Remove (out of scope) |
| CURRENT badge | Campaigns screen | Remove |

---

## 6. Animation

### Screen Transitions — "The Breath"

Applied uniformly to all screen transitions. Fast.

| Direction | Outgoing screen | Incoming screen |
|---|---|---|
| **Forward (A → B)** | Fades out + scales up 100% → 102% | Fades in + scales up 98% → 100% |
| **Back (B → A)** | Fades out + scales down 100% → 98% | Fades in + scales down 102% → 100% |

No shared-element hero transitions. The Breath handles everything.

### Component Animations

| Component | Behaviour |
|---|---|
| Sliders (programmatic change) | Instant snap — no animation |
| ▶ Play button (playing state) | Glow/pulse + switches to ⏸ |
| Soundscape category card (playing) | Coloured glow / highlight border |
| Mini player entrance / exit | The Breath: scales + fades (98% → 100%) in; reverses on dismissal |
| Loading state | Centred spinner |

---

## 7. Monetisation

Out of scope for this version. Remove all purchase-related UI.

---

## 8. Empty States

| Screen | Empty state |
|---|---|
| Campaigns | Illustration + "Scribe New Tale" button |
| Sessions | Illustration + "Add New Session" button |
| Scenes (global) | Illustration + "Add New Scene" button |
| FX Library | Illustration + "Import FX" button |
| Soundscape Categories | Illustration + prompt to create first category |
