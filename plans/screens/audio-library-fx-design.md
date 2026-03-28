# Audio Library — Sound Effects Tab — Screen Design

**Design References:**
- [`docs/designs/AudioLibrary-FX.html`](../../docs/designs/AudioLibrary-FX.html)
- [`docs/designs/AudioLibrary-FX.png`](../../docs/designs/AudioLibrary-FX.png)

---

## Purpose

The Sound Effects tab of the Audio Library is the global catalogue of all one-shot FX tracks. The GM can import new audio files from the device, preview tracks via a mini player, and edit or delete individual tracks.

This screen is reached via the **🎵 LIBRARY** bottom nav tab → Sound Effects sub-tab.

---

## Layout

```
┌─────────────────────────────────────┐
│  Library                       [⚙️]  │
├─────────────────────────────────────┤
│  [Soundscapes]  |  [Sound Effects]  │  ← Library tab strip
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐    │
│  │  [▶]  FX name    [✏️]       │    │
│  │       Tags chips            │    │
│  └─────────────────────────────┘    │
│  ┌─────────────────────────────┐    │
│  │  [▶]  FX name    [✏️]       │    │
│  │       Tags chips            │    │
│  └─────────────────────────────┘    │
│  …                                  │
│                                     │
│  [ + IMPORT FX ]                    │
│                                     │
├─────────────────────────────────────┤
│  ┌─ Mini Player ───────────────┐    │
│  │ [‖ II]  FX name  ══◉══════ │    │
│  └─────────────────────────────┘    │
│  🏰 HOME  📖 CAMPAIGNS  🖼 SCENES  🎵 LIBRARY │
└─────────────────────────────────────┘
```

---

## Components

### Top Bar
- Screen title "Library"
- ⚙️ gear icon top-right

### Library Tab Strip
- **Soundscapes** | **Sound Effects** (active)

### FX Track Row (repeating)
| Element | Description |
|---|---|
| **▶ / ⏸** button | Tap to preview the track in the mini player; tapping the playing track's ⏸ stops it |
| FX name | Display name in gold typography |
| ✏️ pencil icon | Opens the Track Edit screen for this track |
| Tags | Displayed as chips below the name |

- ~~⋮ three-dot menu~~ — replaced with ✏️ pencil icon
- ~~Heart / favourite icon~~ — removed (design mistake)
- ~~BUY MORE button~~ — removed (out of scope)

### Track Edit Screen
Reached by tapping ✏️ on any track row:
- **Name** field — editable text
- **Tags** — add/remove from predefined list + custom
- **Delete** — removes track from the global FX library (with confirmation)
- Back arrow — returns to FX Library without saving if no changes; prompts to save if modified

### Import FX Button
- **+ IMPORT FX** at the bottom of the list
- Opens the device's native file picker, filtered to audio files only
- Selected file is added to the global FX Library with the file's name as default, no tags

### Mini Player
- Visible only on the Library screen (both sub-tabs)
- Appears when the GM taps ▶ on an FX track
- Shows: play/pause toggle, track name, scrub/progress bar
- **Entrance / Exit:** uses "The Breath" animation (scales + fades)
- Navigating away from the Library tab stops playback and hides the mini player

### Empty State
- Centred illustration
- **Import FX** button

### Bottom Navigation Bar
- 🎵 LIBRARY tab is active

---

## Interactions & Behaviour

| Interaction | Result |
|---|---|
| Tap **▶** on a row | Starts preview in mini player; mini player appears with "The Breath" animation |
| Tap **⏸** in mini player | Stops preview; mini player remains visible until dismissed or navigation away |
| Tap ✏️ on a row | Navigate to Track Edit screen |
| Tap **+ IMPORT FX** | Open device file picker; imported file appears in list |
| Tap "Soundscapes" tab | Switch to Soundscapes tab (mini player stops + hides) |
| Navigate away from Library | Mini player stops playback and hides |

---

## States

### Populated list
One row per FX track with ▶, name, ✏️, and tag chips.

### Mini player visible
Anchored above the bottom navigation bar; shows the currently previewing track.

### Empty state
Illustration + "Import FX" button.

### Track Edit screen
Full-screen edit form: Name, Tags, Delete action.

---

## Navigation

| Destination | Trigger |
|---|---|
| Track Edit screen | Tap ✏️ on any track row |
| Device file picker (OS overlay) | + IMPORT FX |
| Audio Library — Soundscapes tab | Tap "Soundscapes" in tab strip |
| Credits | ⚙️ gear icon |
| Home tab | 🏰 bottom nav |
| Campaigns tab | 📖 bottom nav |
| Scenes tab | 🖼 bottom nav |
