# Arcanum Audio — Iterative Build Plan

> Each iteration builds on the previous and is designed for **minimal context**. You can assume everything from prior iterations works. Each section tells you exactly what exists, what to build, and which docs to reference.

## Iteration 0 — Design System & App Shell (Foundation + Simple) — COMPLETED

### Relies on
- Empty scaffold with placeholder bottom nav and nav host (already exists)
- Default Material theme (already exists — needs replacing)

### Goal
Establish the app's visual identity, navigation shell, and resource infrastructure. Build the simplest screens first.

### Build
**1. Resource Infrastructure Fix**
- Update `LocalTrackRepository` to resolve `file:///android_asset/audio/` URIs.

**2. Theme & Design Tokens** (`app/theme/`)
- `Color.kt` — black backgrounds, gold/amber (`#F2CA50`), purple/pink accents, surface/card tones, error reds (`#FFB4AB`)
- `Type.kt` — Newsreader (serif display) + Manrope (body), gold heading style
- `Theme.kt` — dark-only `MaterialTheme`, no dynamic color, custom `ColorScheme`
- `Shape.kt` — rounded corner tokens for cards, buttons *(Restored from previous plan)*
- **Motion Tokens**: Define standard durations and easings for the "Arcanum Motion System" (Container Transform, Shared X-Axis).

**3. Arcanum Components**
- **`ArcanumEmptyState`**: Large gold Material 3 icon + title + CTA button (per Design Spec Section 8).
- **`ArcanumTopBar`**: Reusable top bar with gold title and ⚙️ gear icon.
- **`MainBottomNavBar`**: 4 tabs (HOME, CAMPAIGNS, SCENES, LIBRARY) with gold selected state.
- **`PermissionGate`**: Wrapper for `READ_MEDIA_AUDIO` using the Arcanum error overlay style.
- **Error Overlay**: reusable `ErrorDialog` composable *(Restored from previous plan)*

**4. Screens (Simple)**
- **Credits Screen**: Static list of developer credits and links (reached via ⚙️).
  - *Ambiguity/Contradiction Highlight: Previous plan had Credits in Iteration 10, including a VAULT OF ECHOES button. Ensure it is added back in the Trash iteration if omitted here.*

**5. Navigation & DI Foundation**
- Update `MainNavDestination` enum: `HOME, CAMPAIGNS, SCENES, LIBRARY`.
- Wrap `MainNavHost` in `SharedTransitionLayout` (Compose 1.7+) for future motion support.
- Root `AppModule` providing `ApplicationContext`.

### Docs to reference *(Restored from previous plan)*
- `docs/design-overall.md` §1 (Branding), §2 (Navigation), §6 (Animation), §9 (Error Handling)

---

## ✅ Iteration 1 — Sound Library & Simple Playback (Foundation) — COMPLETED

### Relies on
- Design system & app shell (Iteration 0)

### Goal
Implement the core audio library UI and playback logic using the **Real Audio Stack**.

### Build (DONE)
**1. Audio Engine (Simple)** (`infra/media/`)
- **`ExoPlayer`** for loopable Soundscapes.
- **`SoundPool`** for FX one-shots (near-zero latency).
- **`SimpleAudioPlayer`** class to handle `play(uri)`, `stop()`, and `pause()`.
- **Scope**: ViewModel-scoped — playback stops on screen exit.

**2. Library UI** (`ui/library/`)
- **Library Screen**: List of audio files with Play/Stop preview buttons.
- **Import Button**: `ActivityResultContracts.OpenDocument` to pick files.
- **Library ViewModel**: Manage the "currently picked" sounds in-memory.
- *Ambiguity/Contradiction Highlight: Previous plan split Library into Soundscapes (with a Composer screen) and FX (with a mini-player, tags, search). The completed Iteration 1 here is much simpler. The missing complex features (Composer, Mini-player, Tags, Search) are re-introduced in Iterations 3 and 3.5 to not lose fidelity.*

**3. CI Audio Verification (Real Stack)**
- **Mandate**: Remove `FakeMusicPlayer`. Update all Cucumber steps to use `IdlingResource` waiting for `Player.STATE_READY`.

---

## Iteration 2 — Persistence & Campaign CRUD (Foundation)

### Relies on
- Library UI & Audio Engine (Iteration 1)

### Goal
Stand up the Room database with support for Campaigns, Sessions, and soft-deletion.

### Build
**1. Room Database Setup** (`data/local/`)
- `AppDatabase.kt` — Room DB version 1.
- `AudioTrackEntity`, `CampaignEntity`, `SessionEntity` — include **`isDeleted`** flag.
  - *Details from previous plan:* `CampaignEntity` (`id`, `name`, `coverArtUri`, `lastPlayedAt`), `SessionEntity` (`id`, `campaignId`, `name`, `date`, `coverArtUri`).
- DAOs: `AudioTrackDao`, `CampaignDao`, `SessionDao`.

**2. Migration to Persistence**
- **Copy to Internal Storage**: Implement `FileStorageManager` to copy selected URIs to `filesDir/audio/`.
- Load tracks from `AudioTrackDao` instead of in-memory list.

**3. Campaigns & Sessions UI**
- **Campaigns Screen**: List of campaign cards, + NEW CAMPAIGN, **Swipe-to-Delete**. Photo picker for cover art via `rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia())` *(Restored from previous plan)*.
- **Sessions Screen**: List of sessions for a campaign, + NEW SESSION, **Swipe-to-Delete**.

### Docs to reference *(Restored from previous plan)*
- `docs/designs/campaigns-design.md`, `docs/designs/campaign-sessions-design.md`
- `docs/design-overall.md` §4.2, §8

---

## Iteration 3 — Scenes & Soundscape Categories (Foundation)

### Relies on
- Room DB, Design system
- Sessions (Iteration 2)

### Goal
Implement Scene management and link global scenes to sessions. Restore full Soundscape Category & Composer fidelity.

### Build
**1. Entities & DAOs**
- `SceneEntity` (with `description`, `tags`), `SoundscapeCategoryEntity`.
- **`SessionSceneCrossRef`**: Many-to-many relationship linking Sessions to Scenes.
- DAOs: `SceneDao`, `SessionSceneDao`, `SoundscapeCategoryDao`.

**2. Scenes UI**
- **Scenes List Screen**: Global scene cards, + NEW SCENE, swipe-delete. Tags as chips.
- **Session Scenes Screen**: List of global scenes linked to a specific session, swipe-to-unlink, + IMPORT SCENE (multi-select picker).

**3. Soundscape Category Composer** *(Restored from previous plan)*
- **Library — Soundscapes Tab**: Bento grid of category cards showing track counts per level. ✏️ edit → Composer, + NEW COMPOSITION.
- **Composer Screen**: List of soundscape cards (name, intensity picker, MIX slider), + INVOKE NEW SOUNDSCAPE (audio file picker), SAVE COMPOSITION, swipe-delete tracks, unsaved-changes dialog.

### Docs to reference *(Restored from previous plan)*
- `docs/designs/scenes-list-design.md`, `docs/designs/session-scenes-design.md`
- `docs/designs/audio-library-soundscapes-design.md`, `docs/designs/soundscape-category-composer-design.md`
- `docs/design-overall.md` §4.3–4.5, §4.9–4.10

---

## Iteration 3.5 — Audio Library: FX Library (Restored from previous plan)

### Relies on
- Room DB, Design system

### Goal
Restore the full fidelity of the FX library from the previous plan (search, tags, mini-player).

### Build
- **Entities**: `FxTrackEntity` (id, name, filePath, tags, durationMs, playCount).
- **Library — Sound Effects Tab**: Search bar + filter/sort controls.
  - Track list items: thumbnail, name, tags, duration, ✏️ edit icon.
  - Edit dialog: rename, edit tags, delete.
- **Mini-player**: `MiniPlayerBar` composable (anchored to bottom, slide-up animation, play/pause, skip prev/next, title). Uses `ExoPlayer` via `MixedMusicPlayer` for preview. Navigating away stops playback and hides it.

### Docs to reference
- `docs/designs/audio-library-fx-design.md`
- `docs/design-overall.md` §4.11, §5

---

## Iteration 4 — Home Screen Dashboard (Simple)

### Relies on
- All data entities (Iteration 2, 3, & 3.5)

### Goal
Build the Home dashboard using existing data, preparing for Playback Statistics.

### Build
- **Home Screen**: Resume card (last scene), Campaign hero card (last campaign), and basic stats (Top Atmosphere, Legendary Action - relying on play count metrics).
- *Ambiguity/Contradiction Highlight: Previous plan had Home Screen late (Iteration 9) because it relied on play count stats. Implementing it early here (Iteration 4) means stats will be static/mocked until Playback Statistics (Iteration 6) is fully implemented.*

### Docs to reference
- `docs/designs/home-design.md`, `docs/design-overall.md` §4.1

---

## Iteration 5 — Trash Screen & Credits Integration (Simple)

### Relies on
- `isDeleted` flag (Iteration 2)

### Goal
Implement the "Vault of Echoes" for restoring deleted items, and wire it to Credits.

### Build
- **Trash Screen**: List of items with `isDeleted = true` (Campaigns, Sessions, Scenes, Categories, FX), Restore button (gold), Permanent Delete button (red), Empty Vault button. Footer about 7-day auto-purge.
- **Credits Integration**: Ensure Credits screen (from Iteration 0) has the "RESTORE RECENT DELETES" button to navigate to Trash.

### Docs to reference
- `docs/designs/trash-design.md`, `docs/designs/credits-design.md`

---

## Iteration 6 — Advanced Audio Engine & Statistics (Complexity)

### Relies on
- Simple Audio Engine (Iteration 1)
- Soundscape & FX data (Iterations 3 & 3.5)

### Goal
Upgrade to a multi-channel mixing engine with Intensity support, and implement play count tracking.

### Build
**1. Advanced Audio Engine**
- **`SceneAudioEngine`**: Orchestrates multiple `CategoryPlayer` instances.
- **Cubic Volume Mapping**: $Gain = SliderValue^3$.
- **`CategoryPlayer` (Double-Buffer)**: 2-second crossfade between tracks/intensities.
- **Intensity Logic**: Grey out 0-track levels in UI and announce via `Semantics`.
- **`SoundboardPlayer`**: Holds list of active one-shot players for FX, with master volume.

**2. Playback Statistics** *(Restored from previous plan Iteration 11)*
- Track play counts to populate Home screen stats and Add-to-Scene counters.
- Increment `playCount` on `SoundscapeTrackEntity` (in `CategoryPlayer`) and `FxTrackEntity` (in `SoundboardPlayer`).
- Update `lastPlayedAt` on Campaign/Session when a scene is opened.

### Docs to reference
- `docs/design-overall.md` §3, §4.6, §4.7, §4.8

---

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

---

## Iteration 8 — Arcanum Motion System & Polish (Complexity)

### Relies on
- All previous iterations
- SharedTransition Scaffolding (Iteration 0)

### Goal
Final polish, high-fidelity animations, scene switching crossfades, and edge cases.

### Build
- **Shared Transitions**: Implement Container Transform (Card → Detail) and Shared X-Axis (Tab switching) using `SharedTransitionLayout`.
- **Scene Switching**: Crossfade logic in `SceneAudioEngine` (fade out current over 2-3s while fading in new). ▶ button on scene cards autoplays.
- **Final UI Polish & Edge Cases** *(Restored from previous plan Iteration 12)*:
  - Empty state illustrations (scroll, parchment, map, wand, crystal ball, silent room).
  - All-intensities-empty category handling (disable play/dice, grey out intensities).
  - Loading states (centred spinner).
  - Predefined tag system (Tavern, Forest, Combat, etc.) + custom tags.
  - Verify ExoPlayer cleanup and no audio leaks.
  - Accessibility content descriptions and contrast review.
  - R8/Minification verification.

### Docs to reference
- `docs/design-overall.md` §6, §8, §9

---

## Iteration 9 — Session Excellence & Mastering (Complexity)

### Relies on
- All previous iterations
- Active Scene UI (Iteration 7)

### Goal
Elevate the session experience with master controls, audio refinement, and campaign portability.

### Build
**1. Master Control Logic**
- **Global Stop**: A single prominent button to fade out all soundscapes and silence all FX immediately.
- **Master Intensity Switcher**: A global selector (I, II, III) that updates the intensity level for *all* soundscape categories in the scene simultaneously.

**2. Audio Engine Upgrades**
- **Auto-Ducking**: Automatically lower soundscape volume when an FX is triggered, then smoothly restore it.
- **Global Limiter**: Implement a look-ahead limiter in the `SceneAudioEngine` to prevent clipping when multiple tracks peak.
- **Equal-Power Crossfading**: Upgrade the `CategoryPlayer` double-buffer to use equal-power crossfade curves ($sin/cos$) for constant perceived loudness during transitions.

**3. FX Randomization**
- **Pitch/Volume Jitter**: Add optional randomization settings for FX triggers (e.g., +/- 10% pitch, +/- 5% volume) to avoid "machine-gun effect" on repeated sounds.

**4. UI Features**
- **Session Lock**: A toggle to prevent accidental scene changes or volume adjustments during live play.
- **Scene Cloning**: Ability to duplicate an existing Scene (including all linked tracks and intensities).
- **Scene Notes**: A markdown-capable text area for each Scene to store DM descriptions or cues.

**5. Data Portability**
- **Campaign Export/Import**: Package a Campaign, its Scenes, and all associated local audio files into a single `.arcanum` (ZIP) file for sharing or backup.
