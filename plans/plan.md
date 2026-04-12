# Arcanum Audio — Iterative Build Plan

> Each iteration builds on the previous and is designed for **minimal context**. You can assume everything from prior iterations works. Each section tells you exactly what exists, what to build, and which docs to reference.

---

## 🚨 Current Implementation Status & Gaps
Before starting new iterations, the following gaps from early iterations MUST be resolved:
- **Iteration 0 Gaps**: `Theme.kt` is still using default Material 3 templates. `ArcanumTopBar`, `PermissionGate`, and `MainBottomNavBar` are incomplete/missing. `MainNavDestination` incorrectly includes `SETTINGS`. `ArcanumEmptyState` component is missing.
- **Resource Infrastructure Fix**: `res/raw` has an invalid folder structure (`soundscapes/Forest/...`) that breaks Android resource resolution. **Fix**: Move all bundled audio to `app/src/main/assets/audio/` and create the missing `assets` directory. Update `LocalTrackRepository` to resolve via `AssetManager`.
- **Iteration 1 Gaps**: The entire `LibraryScreen` and `LibraryViewModel` are missing. **QA Risk**: Existing acceptance tests use a `FakeMusicPlayer`; these MUST be transitioned to the **Real Audio Stack** (Media3) as per Design Mandate 11.2.
- **Iteration 2 Gaps**: `SessionEntity` and `AudioTrackEntity` are missing from the Room DB. Entities must include an `isDeleted` flag (soft delete). The Sessions Screen is missing. `CampaignsScreen` is missing swipe-to-delete. External file copying logic (`filesDir`) in the repository is missing.

---

## Iteration 0 — Design System & App Shell (Foundation + Simple)

### Relies on
- Empty scaffold with placeholder bottom nav and nav host (already exists)
- Default Material theme (already exists — needs replacing)

### Goal
Establish the app's visual identity, navigation shell, and resource infrastructure. Build the simplest screens first.

### Build

**1. Resource Infrastructure Fix**
- Create `app/src/main/assets/audio/`.
- Move all audio from `res/raw/` to `assets/audio/` (preserving folder hierarchy for intensities).
- Update `LocalTrackRepository` to resolve `file:///android_asset/audio/` URIs.

**2. Theme & Design Tokens** (`app/theme/`)
- `Color.kt` — black backgrounds, gold/amber (`#F2CA50`), purple/pink accents, surface/card tones, error reds (`#FFB4AB`)
- `Type.kt` — Newsreader (serif display) + Manrope (body), gold heading style
- `Theme.kt` — dark-only `MaterialTheme`, no dynamic color, custom `ColorScheme`
- **Motion Tokens**: Define standard durations and easings for the "Arcanum Motion System" (Container Transform, Shared X-Axis).

**3. Arcanum Components**
- **`ArcanumEmptyState`**: Large gold Material 3 icon + title + CTA button (per Design Spec Section 8).
- **`ArcanumTopBar`**: Reusable top bar with gold title and ⚙️ gear icon.
- **`MainBottomNavBar`**: 4 tabs (HOME, CAMPAIGNS, SCENES, LIBRARY) with gold selected state.
- **`PermissionGate`**: Wrapper for `READ_MEDIA_AUDIO` using the Arcanum error overlay style.

**4. Screens (Simple)**
- **Credits Screen**: Static list of developer credits and links (reached via ⚙️).

**5. Navigation & DI Foundation**
- Update `MainNavDestination` enum: `HOME, CAMPAIGNS, SCENES, LIBRARY`.
- Wrap `MainNavHost` in `SharedTransitionLayout` (Compose 1.7+) for future motion support.
- Root `AppModule` providing `ApplicationContext`.

---

## Iteration 1 — Sound Library & Simple Playback (Foundation)

### Relies on
- Design system & app shell (Iteration 0)

### Goal
Implement the core audio library UI and playback logic using the **Real Audio Stack**.

### Build

**1. Audio Engine (Simple)** (`infra/media/`)
- **`ExoPlayer`** for loopable Soundscapes.
- **`SoundPool`** for FX one-shots (near-zero latency).
- **`SimpleAudioPlayer`** class to handle `play(uri)`, `stop()`, and `pause()`.
- **Scope**: ViewModel-scoped — playback stops on screen exit.

**2. Library UI** (`ui/library/`)
- **Library Screen**: List of audio files with Play/Stop preview buttons.
- **Import Button**: `ActivityResultContracts.OpenDocument` to pick files.
- **Library ViewModel**: Manage the "currently picked" sounds in-memory.

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
- DAOs: `AudioTrackDao`, `CampaignDao`, `SessionDao`.

**2. Migration to Persistence**
- **Copy to Internal Storage**: Implement `FileStorageManager` to copy selected URIs to `filesDir/audio/`.
- Load tracks from `AudioTrackDao` instead of in-memory list.

**3. Campaigns & Sessions UI**
- **Campaigns Screen**: List of campaign cards, + NEW CAMPAIGN, **Swipe-to-Delete**.
- **Sessions Screen**: List of sessions for a campaign, + NEW SESSION, **Swipe-to-Delete**.

---

## Iteration 3 — Scenes & Soundscape Categories (Foundation)

### Relies on
- Room DB, Design system
- Sessions (Iteration 2)

### Goal
Implement Scene management and link global scenes to sessions.

### Build

**1. Entities & DAOs**
- `SceneEntity`, `SoundscapeCategoryEntity`.
- **`SessionSceneCrossRef`**: Many-to-many relationship linking Sessions to Scenes.

**2. Scenes UI**
- **Scenes List Screen**: Global scene cards, + NEW SCENE, swipe-delete.
- **Session Scenes Screen**: List of global scenes linked to a specific session, swipe-to-unlink, + IMPORT SCENE.

---

## Iteration 4 — Home Screen Dashboard (Simple)

### Relies on
- All data entities (Iteration 2 & 3)

### Goal
Build the Home dashboard using existing data.

### Build
- **Home Screen**: Resume card (last scene), Campaign hero card (last campaign), and basic stats (Top Atmosphere).

---

## Iteration 5 — Trash Screen (Simple)

### Relies on
- `isDeleted` flag (Iteration 2)

### Goal
Implement the "Vault of Echoes" for restoring deleted items.

### Build
- **Trash Screen**: List of items with `isDeleted = true`, Restore button, and Permanent Delete button.

---

## Iteration 6 — Advanced Audio Engine (Complexity)

### Relies on
- Simple Audio Engine (Iteration 1)
- Soundscape data (Iteration 3)

### Goal
Upgrade to a multi-channel mixing engine with Intensity support.

### Build
- **`SceneAudioEngine`**: Orchestrates multiple `CategoryPlayer` instances.
- **Cubic Volume Mapping**: $Gain = SliderValue^3$.
- **`CategoryPlayer` (Double-Buffer)**: 2-second crossfade between tracks/intensities.
- **Intensity Logic**: Grey out 0-track levels in UI and announce via `Semantics`.

---

## Iteration 7 — Active Scene UI (Complexity)

### Relies on
- Advanced Audio Engine (Iteration 6)
- Add-to-Scene Picker

### Goal
Build the primary gameplay interface with live mixing and soundboard triggers.

### Build
- **Add-to-Scene Library Picker**: UI for assigning soundscapes/FX to a scene.
- **Soundscapes Tab**: Category cards with MIX sliders and intensity selectors.
- **Soundboard Tab**: 4-column grid of FX buttons (SoundPool).
- **Master Sliders**: Atmosphere and FX volume controls.

---

## Iteration 8 — Arcanum Motion System & Polish (Complexity)

### Relies on
- All previous iterations
- SharedTransition Scaffolding (Iteration 0)

### Goal
Final polish and high-fidelity animations.

### Build
- **Shared Transitions**: Implement Container Transform (Card → Detail) and Shared X-Axis (Tab switching) using `SharedTransitionLayout`.
- **Final UI Polish**: Visual cues (playing glow), refined typography, and R8/Minification verification.

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
- **Master Intensity**: A global slider that offsets all individual soundscape intensities (relative adjustment).

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
