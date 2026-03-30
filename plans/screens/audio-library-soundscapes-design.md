# Audio Library — Soundscapes Tab — Screen Design

**Design References:**
- [`docs/designs/AudioLibrary-Soundscape-Categories.html`](../../docs/designs/AudioLibrary-Soundscape-Categories.html)
- [`docs/designs/AudioLibrary-Soundscape-Categories.png`](../../docs/designs/AudioLibrary-Soundscape-Categories.png)

---

## Purpose

The Soundscapes tab of the Audio Library is the master catalogue of all Soundscape Categories the GM has created. From here the GM can review, edit, and organise their soundscape content. Editing a category opens the Soundscape Category Composer.

This screen is reached via the **🎵 LIBRARY** bottom nav tab → Soundscapes sub-tab.

---

## Layout

```
┌─────────────────────────────────────┐
│  Library                       [⚙️]  │
├─────────────────────────────────────┤
│  [Soundscapes]  |  [Sound Effects]  │  ← Library tab strip
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐    │
│  │  Category name        [✏️]  │    │
│  │  I: 3 tracks  II: 5  III: 2 │    │
│  └─────────────────────────────┘    │
│  ┌─────────────────────────────┐    │
│  │  Category name        [✏️]  │    │
│  │  I: 1 track   II: 0  III: 4 │    │
│  └─────────────────────────────┘    │
│  …                                  │
│                                     │
│  [ + CREATE CATEGORY ]              │
├─────────────────────────────────────┤
│  🏰 HOME  📖 CAMPAIGNS  🖼 SCENES  🎵 LIBRARY │
└─────────────────────────────────────┘
```

---

## Components

### Top Bar
- Screen title "Library"
- ⚙️ gear icon top-right

### Library Tab Strip
- **Soundscapes** (active) | **Sound Effects**

### Soundscape Category Card (repeating)
- Category name in gold typography
- Track count per intensity level: **I: N tracks · II: N tracks · III: N tracks**
- **✏️ pencil icon** on the right → opens the Soundscape Category Composer for that category
- ~~The Archivist's Choice section~~ — removed (design mistake)

### Empty State
- Centred illustration (parchment / arcane theme)
- Friendly prompt: *"No categories yet — build your first soundscape"*
- **Create Category** button

### Create Category Button
- **+ CREATE CATEGORY** at the bottom of the list
- Opens: name input → immediately lands in the Soundscape Category Composer with an empty composer

### Bottom Navigation Bar
- 🎵 LIBRARY tab is active

---

## Interactions & Behaviour

| Interaction | Result |
|---|---|
| Tap ✏️ on a category card | Navigate to Soundscape Category Composer (edit mode) |
| Tap card body | Navigate to Soundscape Category Composer (same as ✏️) |
| Swipe right on card | Instantly moves category to temporarily unavailable (permanently deleted after 7 days) |
| Tap **+ CREATE CATEGORY** | Prompt for name → open Soundscape Category Composer (new) |
| Tap ⚙️ | Navigate to Credits screen |
| Tap "Sound Effects" tab | Navigate to Audio Library — Sound Effects tab |

---

## States

### Populated list
One card per category with track counts.

### Empty state
Illustration + prompt + Create Category button.

---

## Navigation

| Destination | Trigger |
|---|---|
| Soundscape Category Composer | Tap ✏️ or card body |
| Audio Library — Sound Effects tab | Tap "Sound Effects" in tab strip |
| New category → Composer | + CREATE CATEGORY |
| Credits | ⚙️ gear icon |
| Home tab | 🏰 bottom nav |
| Campaigns tab | 📖 bottom nav |
| Scenes tab | 🖼 bottom nav |
