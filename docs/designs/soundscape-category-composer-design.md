# Soundscape Category Composer — Screen Design

**Design References:**
- [`docs/designs/Soundscape-Category-Composer.html`](../../docs/designs/Soundscape-Category-Composer.html)
- [`docs/designs/Soundscape-Category-Composer.png`](../../docs/designs/Soundscape-Category-Composer.png)

---

## Purpose

The Composer is where the GM assembles a Soundscape Category by layering multiple track lists, one per intensity level. Adding a new layer means importing an audio file from the device. Saving applies the composition globally — any scene using this category will immediately reflect the change.

---

## Layout

```
┌─────────────────────────────────────┐
│  ← [Category Name]             [⚙️]  │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐    │
│  │  Layer / Soundscape name    │    │
│  │  Intensity: II              │    │
│  │  MIX  ════════◉═══════      │    │
│  │  [Track list: track1, ...]  │    │
│  └─────────────────────────────┘    │
│  ┌─────────────────────────────┐    │
│  │  Layer / Soundscape name    │    │
│  │  Intensity: I               │    │
│  │  MIX  ═══◉═════════════     │    │
│  └─────────────────────────────┘    │
│  …                                  │
│                                     │
│  [ + INVOKE NEW LAYER ]             │
│                                     │
│  [ SAVE COMPOSITION ]               │
├─────────────────────────────────────┤
│  🏰 HOME  📖 CAMPAIGNS  🖼 SCENES  🎵 LIBRARY │
└─────────────────────────────────────┘
```

---

## Components

### Top Bar
- Back arrow → returns to Audio Library — Soundscapes tab
- Category name as title
- ⚙️ gear icon top-right

### Layer Card (repeating)
Each layer (also called a "Soundscape") within the category has:

| Element | Description |
|---|---|
| Layer name | Editable name for this soundscape/layer |
| Intensity level | Which intensity pool this layer belongs to (I, II, or III) |
| MIX slider | Per-layer relative volume; used in the Active Scene's multiplicative calculation |
| Track list | Names of audio files associated with this layer |

### Add Layer Button
- **+ INVOKE NEW LAYER** — opens the device's native file picker, filtered to audio files only
- On file selection, a new layer is created using that file's name, default intensity I, default MIX 100%
- There is **no limit** on number of layers

### Save Composition Button
- **SAVE COMPOSITION** — saves the entire category composition globally
- Changes are reflected everywhere this category is used — no per-scene versioning

---

## Interactions & Behaviour

| Interaction | Result |
|---|---|
| Tap **+ INVOKE NEW LAYER** | Opens device file picker; selected audio file becomes a new layer |
| Adjust a MIX slider | Changes that layer's relative volume (live preview if the category is currently in a playing scene) |
| Change intensity level on a layer | Reassigns that layer to a different intensity pool |
| Edit layer name | In-line text edit |
| Remove a layer | Swipe-to-dismiss or a delete icon on the layer card |
| Tap **SAVE COMPOSITION** | Persists the composition globally; navigates back or shows success confirmation |
| Tap back arrow | Returns to Soundscapes Library (prompts to save if unsaved changes) |
| Tap ⚙️ | Navigate to Credits screen |

---

## States

### New category (empty)
Empty layer list with **+ INVOKE NEW LAYER** and **SAVE COMPOSITION** as primary CTAs.

### One or more layers present
Layers displayed as cards, draggable to reorder.

### File picker open
Native OS picker overlay; composer screen waits behind it.

### Unsaved changes
Back navigation shows a discard-changes confirmation dialog.

---

## Navigation

| Destination | Trigger |
|---|---|
| Audio Library — Soundscapes tab | Back arrow (after save or discard confirm) |
| Device file picker (OS overlay) | + INVOKE NEW LAYER |
| Credits | ⚙️ gear icon |
