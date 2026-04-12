# Arcanum Audio — Iterative Build Plan

> Each iteration builds on the previous and is designed for **minimal context**. You can assume everything from prior iterations works. Each section tells you exactly what exists, what to build, and which docs to reference.

---

## 🚨 Current Implementation Status & Gaps
Before starting new iterations, the following gaps from early iterations MUST be resolved:
- **Iteration 0 Gaps**: `Theme.kt` is still using default Material 3 templates. `ArcanumTopBar`, `PermissionGate`, and `MainBottomNavBar` are incomplete/missing. `MainNavDestination` incorrectly includes `SETTINGS` (must be removed). `ArcanumEmptyState` component is missing.
- **Iteration 1 Gaps**: The entire `LibraryScreen` and `LibraryViewModel` are missing. `res/raw` has an invalid folder structure (`soundscapes/Forest/...`) that breaks Android resource resolution. `LocalTrackRepository` relies on an `assets/` folder that doesn't exist. **QA Risk**: Existing acceptance tests use a `FakeMusicPlayer`; these MUST be transitioned to the **Real Audio Stack** (Media3) as per Design Mandate 11.2.
- **Iteration 2 Gaps**: `SessionEntity` and `AudioTrackEntity` are missing from the Room DB. Entities must include an `isDeleted` flag (soft delete). The Sessions Screen is missing. `CampaignsScreen` is missing swipe-to-delete (this is a "Phantom" feature in current tests that fails in reality). External file copying logic (`filesDir`) in the repository is missing.

---

## Iteration 0 — Design System & App Shell

### Relies on
- Empty scaffold with placeholder bottom nav and nav host (already exists)
- Default Material theme (already exists — needs replacing)

### Goal
Replace the default template theme with the Arcanum Audio design system and wire up the bottom navigation shell (barebones) so all future screens plug in.

### Build

**1. Theme & Design Tokens** (`app/theme/`)
- `Color.kt` — black backgrounds, gold/amber (`#F2CA50`), purple/pink accents, surface/card tones, error reds (`#FFB4AB`)
- `Type.kt` — Newsreader (serif display) + Manrope (body), gold heading style
- `Theme.kt` — dark-only `MaterialTheme`, no dynamic color, custom `ColorScheme`
- **Motion Tokens**: Define standard durations and easings for the "Arcanum Motion System" (Container Transform, Shared X-Axis).

**2. Shared Transition Scaffolding**
- Wrap the `MainNavHost` or screen content in `SharedTransitionLayout` (Compose 1.7+) to support cross-screen morphing (e.g., Campaign Card → Sessions List).

**3. Bottom Nav Bar** (`app/components/MainBottomNavBar.kt`)
- 4 tabs: 🏰 HOME, 📖 CAMPAIGNS, 🖼 SCENES, 🎵 LIBRARY
- Gold selected icon, muted unselected
- Persists across all main screens

**4. Top App Bar** — reusable `ArcanumTopBar` composable
- Params: `title`, `showBackArrow`, `onBack`, `onGearClick`
- ⚙️ gear icon always present → navigates to Credits (Do NOT use a separate Settings tab)
- Gold title typography

**5. Arcanum Components**
- **`ArcanumEmptyState`**: Large gold Material 3 icon + title + CTA button (per Design Spec Section 8).
- **`PermissionGate`**: Generic wrapper for `READ_EXTERNAL_STORAGE` (Legacy) or `READ_MEDIA_AUDIO` (Android 13+). Handles rationale UI and "Settings" redirection via the "Arcanum Error Overlay" style.

**6. Navigation graph** (`app/navigation/`)
- Update `MainNavDestination` enum: `HOME, CAMPAIGNS, SCENES, LIBRARY` (Remove `SETTINGS`).
- Update `MainNavHost` with placeholder composables for each tab.
- Wire `Scaffold` in `MainActivity` with top bar + bottom nav + nav host.

**7. Hilt DI Baseline** (`app/di/`)
- Root `AppModule` providing `ApplicationContext`.
- `MainActivity` and `Application` class entry points (`@HiltAndroidApp`, `@AndroidEntryPoint`).

---

## Iteration 1 — Sound Library & Simple Playback (Real Stack)

### Relies on
- Design system & app shell (Iteration 0)

### Goal
Implement the core audio library UI and playback logic using the **Real Audio Stack**. Acceptance tests MUST verify PCM data/state via the real `ExoPlayer` engine.

### Build

**1. Audio Engine (Simple)** (`infra/media/`)
- **`ExoPlayer`** implementation for long-running, loopable Soundscapes.
- **`SoundPool`** implementation for FX one-shots to ensure near-zero latency.
- **`SimpleAudioPlayer`** class to handle `play(uri)`, `stop()`, and `pause()`.
- **Scope**: **ViewModel-scoped** — playback stops when the user navigates away from the Library screen.

**2. Library ViewModel** (`ui/library/`)
- `LibraryViewModel` — holds a `StateFlow<List<Uri>>` of "currently picked" sounds.
- **Persistence**: Strictly **In-Memory** — list is lost on process death.
- Actions: `pickSound(uri)`, `playTrack(uri)`, `stopPlayback()`.

**3. Library Screen** (`ui/library/LibraryScreen.kt`)
- Simple list of picked tracks (showing filename from URI).
- **Play/Stop button** on each track for immediate preview.
- **Import Button**: `ActivityResultContracts.OpenDocument` (audio/*) to add a URI to the session list.

**4. Hilt Module** — Provide `SimpleAudioPlayer` as a singleton.

**5. CI Audio Verification (Real Stack)**
- Inject real `SimpleAudioPlayer` and expose its internal `ExoPlayer` state for tests.
- **Mandate**: Remove `FakeMusicPlayer`. Update all Cucumber steps to use `IdlingResource` waiting for `Player.STATE_READY`.
- Update GitHub Actions workflow to run emulator with PulseAudio dummy sink for headless execution.

---

## Iteration 2 — Room Database, Persistence & Campaign CRUD

### Relies on
- Library UI & Audio Engine (Iteration 1)
- Design system

### Goal
Stand up the Room database to persist the sound library and manage Campaign/Session data with soft-delete support.

### Build

**1. Room Database Setup** (`data/local/`)
- `AppDatabase.kt` — Room DB with version 1.
- `AudioTrackEntity` — `id`, `name`, `filePath`, `type`, **`isDeleted`**.
- `CampaignEntity` — `id`, `name`, `coverArtUri?`, `lastPlayedAt`, **`isDeleted`**.
- `SessionEntity` — `id`, `campaignId (FK)`, `name`, `date`, `coverArtUri?`, **`isDeleted`**.
- DAOs: `AudioTrackDao`, `CampaignDao`, `SessionDao`.

**2. Migration to Persistence** (Refer to `docs/MIGRATION_PLAN.md`)
- Update `LibraryViewModel` to save URIs to the database.
- **Copy to Internal Storage**: Implement `FileStorageManager` to copy selected URIs to `filesDir/audio/` to ensure persistent access even if the original file is moved or deleted. **Ownership shift is mandatory.**
- Load tracks from `AudioTrackDao` instead of in-memory list.

**3. ViewModels**
- `CampaignsViewModel` — CRUD for campaigns (implement `isDeleted` logic).
- `SessionsViewModel` — CRUD for sessions within a campaign.

**4. Screens**
- **Campaigns Screen** — List of campaign cards, + NEW CAMPAIGN, **Swipe-to-Delete** (using `SwipeToDismissBox`).
- **Sessions Screen** — List of sessions for a given campaign, + NEW SESSION, **Swipe-to-Delete**.

---

## Iteration 3 — Scenes, Soundscape Categories & Session-Scenes

### Relies on
- Room DB, Design system
- Sessions (Iteration 2)

### Goal
Implement Scene management, Soundscape Category definitions, and link global scenes to specific sessions.

### Build

**1. Entities & DAOs**
- `SceneEntity` — `id`, `name`, `description?`, **`isDeleted`**.
- `SoundscapeCategoryEntity` — `id`, `name`, `iconResId?`, **`isDeleted`**.
- `SessionSceneCrossRef` — Many-to-many relationship linking Sessions to Scenes.
- `SceneDao`, `SoundscapeCategoryDao`

**2. ViewModels**
- `ScenesViewModel` — global scenes list
- `SoundscapeLibraryViewModel` — manage categories
- `SessionScenesViewModel` — manage scenes linked to a specific session

**3. Screens**
- **Scenes List Screen** — global scene cards, + NEW SCENE, swipe-delete
- **Soundscape Library** — browse and manage categories
- **Session Scenes Screen** (`ui/sessions/SessionScenesScreen.kt`) — list of global scenes linked to a session, swipe-to-unlink, + IMPORT SCENE (links existing scenes to session).

---

## Iteration 4 — Audio Engine: Mixing & Intensity

### Relies on
- Simple Audio Engine (Iteration 1)
- Soundscape data (Iteration 3)

### Goal
Upgrade the audio engine to support multiple simultaneous looping tracks with per-category MIX volume, master volume, and **Double-Buffer Intensity transitions**.

### Build

**1. `SceneAudioEngine`** (`infra/media/`)
- Orchestrates multiple `CategoryPlayer` instances based on the active scene.
- **Cubic Volume Mapping**: Converts linear slider values (0.0–1.0) to $Gain = SliderValue^3$ before applying to players to ensure a natural hearing progression.
- **Master Atmosphere**: Applies global volume multiplier to all active categories.

**2. System Integration**
- **MediaSession Integration**: Lock screen controls + Bluetooth "Next" → d20 Randomization.
- **Audio Focus & 3-Minute Timeout**: Auto-resume only if interruption < 3 mins.

**3. `CategoryPlayer` (Double-Buffer Architecture)**
- **Architecture**: Maintains two `ExoPlayer` instances: `ActivePlayer` and `StagingPlayer`.
- **2-Second Crossfade**: Triggered on intensity switch or random track request (`d20`).
- **Intensity Logic**: If a level has 0 tracks, it is **greyed out** in the UI and non-interactive. Add `Semantics` to announce the reason (e.g. "Level II — No tracks").

---

## Iteration 5 — Active Scene: Soundscapes Tab & Add-to-Scene Library Picker

### Relies on
- `SceneAudioEngine` (Iteration 4)
- Scene + Category data

### Goal
Build the primary gameplay screen's Soundscapes tab with live audio mixing, and the "Add to Scene" library picker.

### Build

**1. Add-to-Scene Library Picker** (`ui/library/AddToSceneScreen.kt`)
- Full-screen list displaying either Soundscape Categories or FX tracks.
- `+` button to instantly add to the active scene (no confirm).
- Indicator (`⚡`) for items already in the scene.
- Footer card to `IMPORT NEW` directly from device.

**2. ViewModel** — `ActiveSceneSoundscapesViewModel`
- Load scene's categories and current mix/intensity states

**3. Screen** — `ActiveSceneSoundscapesScreen.kt`
- Master Atmosphere slider.
- Category cards with MIX sliders and intensity selectors (grey out 0-track levels).
- `ADD NEW SOUNDSCAPE` button navigating to the Library Picker.

---

## Iteration 6 — Active Scene: Soundboard Tab

### Relies on
- `SimpleAudioPlayer` (Iteration 1)
- FX data
- Add-to-Scene Library Picker (Iteration 5)

### Goal
Build the Soundboard tab with the FX button grid (SoundPool powered).

### Build

**1. ViewModel** — `ActiveSceneSoundboardViewModel`
- Load scene's FX tracks

**2. Screen** — `ActiveSceneSoundboardScreen.kt`
- Grid of FX buttons for one-shot triggering.
- `ADD NEW EFFECT` button navigating to the Library Picker (FX variant).

---

## Iteration 7 — Scene Switching & Arcanum Motion System

### Relies on
- Audio Engine (Iteration 4)
- Navigation shell & Scaffolding (Iteration 0)

### Goal
Implement scene switching with crossfade and the Arcanum Motion System transitions using `SharedTransitionLayout`.

---

## Iteration 8 — Home Screen Dashboard

### Relies on
- All data entities

### Goal
Build the Home dashboard for resuming journeys and viewing quick stats (Top Atmosphere, Legendary Action).

---

## Iteration 9 — Credits, Trash & Polish

### Relies on
- All previous iterations

### Goal
Final pass — handle soft-deletes (Trash), Credits, and overall UI/UX polish.

### Build

**1. Soft Deletes (Trash)**
- Implement Trash screen to restore or permanently delete `isDeleted` items.
- 7-day warning logic (visual only for this version).

**2. Credits & Legal**
- Credits screen with attribution for icons/fonts.

**3. Release Readiness**
- R8/Minification verification and Signing Configuration.
