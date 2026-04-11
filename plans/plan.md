# Arcanum Audio — Iterative Build Plan

> Each iteration builds on the previous and is designed for **minimal context**. You can assume everything from prior iterations works. Each section tells you exactly what exists, what to build, and which docs to reference.

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
- `Shape.kt` — rounded corner tokens for cards, buttons

**2. Bottom Nav Bar** (`app/components/MainBottomNavBar.kt`)
- 4 tabs: 🏰 HOME, 📖 CAMPAIGNS, 🖼 SCENES, 🎵 LIBRARY
- Gold selected icon, muted unselected
- Persists across all main screens

**3. Top App Bar** — reusable `ArcanumTopBar` composable
- Params: `title`, `showBackArrow`, `onBack`, `onGearClick`
- ⚙️ gear icon always present → navigates to Credits
- Gold title typography

**4. Navigation graph** (`app/navigation/`)
- Update `MainNavDestination` enum: `HOME, CAMPAIGNS, SCENES, LIBRARY`
- Update `MainNavHost` with placeholder composables for each tab
- Wire `Scaffold` in `MainActivity` with top bar + bottom nav + nav host

**5. Hilt DI Baseline** (`app/di/`)
- Root `AppModule` providing `ApplicationContext`.
- `MainActivity` and `Application` class entry points (`@HiltAndroidApp`, `@AndroidEntryPoint`).

**6. Permission Scaffolding** (`ui/components/PermissionGate.kt`)
- Generic wrapper for `READ_EXTERNAL_STORAGE` (Legacy) or `READ_MEDIA_AUDIO` (Android 13+).
- Handle rationale UI and "Settings" redirection.

### Reusable components produced
| Component | Used by |
|---|---|
| `ArcanumTopBar` | Every screen |
| `MainBottomNavBar` | App shell |
| Theme tokens | Everything |

---

## Iteration 1 — Sound Library & Simple Playback (In-Memory)

### Relies on
- Design system & app shell (Iteration 0)

### Goal
Implement the core audio library UI and playback logic. Users can pick audio files and play them immediately. At this stage, data is **not** persisted to a database; it is kept in-memory for immediate "Pick & Play" testing.

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

### Reusable components produced
| Component | Used by |
|---|---|
| `SimpleAudioPlayer` | Library, Active Scene |
| `AudioFilePicker` | Library import |

### Docs to reference
- `docs/designs/audio-library-fx-design.md`
- `app/Learnings.md` (Audio Optimization section)

---

## Iteration 2 — Room Database, Persistence & Campaign CRUD

### Relies on
- Library UI & Audio Engine (Iteration 1)
- Design system

### Goal
Stand up the Room database to persist the sound library and manage Campaign/Session data.

### Build

**1. Room Database Setup** (`data/local/`)
- `AppDatabase.kt` — Room DB with version 1.
- `AudioTrackEntity` — `id`, `name`, `filePath`, `type` (FX/SOUNDSCAPE).
- `CampaignEntity` — `id`, `name`, `coverArtUri?`, `lastPlayedAt`.
- `SessionEntity` — `id`, `campaignId (FK)`, `name`, `date`, `coverArtUri?`.
- DAOs: `AudioTrackDao`, `CampaignDao`, `SessionDao`.

**2. Migration to Persistence** (Refer to `docs/MIGRATION_PLAN.md`)
- Update `LibraryViewModel` to save URIs to the database and copy files to internal storage.
- Load tracks from `AudioTrackDao` instead of in-memory list.

**3. ViewModels**
- `CampaignsViewModel` — CRUD for campaigns.
- `SessionsViewModel` — CRUD for sessions within a campaign.

**4. Screens**
- **Campaigns Screen** — List of campaign cards, + NEW CAMPAIGN, swipe-delete.
- **Sessions Screen** — List of sessions, + NEW SESSION, swipe-delete.

### Reusable components produced
| Component | Used by |
|---|---|
| `AppDatabase` | All data layers |
| `CampaignCard` | Campaigns screen |

---

## Iteration 3 — Scenes & Soundscape Categories

### Relies on
- Room DB, Design system

### Goal
Implement Scene management and Soundscape Category definitions.

### Build

**1. Entities & DAOs**
- `SceneEntity` — `id`, `name`, `description?`
- `SoundscapeCategoryEntity` — `id`, `name`, `iconResId?`
- `SceneDao`, `SoundscapeCategoryDao`

**2. ViewModels**
- `ScenesViewModel` — global scenes list
- `SoundscapeLibraryViewModel` — manage categories

**3. Screens**
- **Scenes List Screen** — scene cards, + NEW SCENE, swipe-delete
- **Soundscape Library** — browse and manage categories

---

## Iteration 4 — Audio Engine: Mixing & Intensity

### Relies on
- Simple Audio Engine (Iteration 1)
- Soundscape data (Iteration 3)

### Goal
Upgrade the audio engine to support multiple simultaneous looping tracks with per-category MIX volume and master volume, and integrate system-level media controls.

### Build

**1. `SceneAudioEngine`** (`infra/media/`)
- Orchestrates multiple `CategoryPlayer` instances based on the active scene.
- **Cubic Volume Mapping**: Converts linear slider values (0.0–1.0) to $Gain = SliderValue^3$ before applying to players to ensure a natural hearing progression.
- **Master Atmosphere**: Applies global volume multiplier to all active categories.

**2. System Integration**
- **MediaSession Integration**:
    - Basic setup to support lock screen controls and Bluetooth remotes.
    - **d20 Logic**: Map `onSkipToNext` (MediaSession) to trigger a random d20 roll (1-20) notification.
- **Audio Focus & 3-Minute Timeout**:
    - Implement `AudioFocusRequest` handling.
    - **Smart Auto-Resume**: If playback is interrupted (e.g., phone call), auto-resume ONLY if the interruption lasts **< 3 minutes**.

**3. `CategoryPlayer` (Double-Buffer Architecture)**
- **Architecture**: Maintains two `ExoPlayer` instances: `ActivePlayer` and `StagingPlayer`.
- **2-Second Crossfade**:
    - Triggered on intensity switch or random track request (`d20`).
    - 1. Prepare `StagingPlayer` with the new track (randomly selected from the current intensity pool).
    - 2. Simultaneously fade out `ActivePlayer` and fade in `StagingPlayer` over 2000ms.
    - 3. On completion, stop/reset `ActivePlayer` and swap references so `StagingPlayer` becomes the new `ActivePlayer`.
- **State Management**: Handles random track selection within the current Intensity pool (I, II, III).

---

## Iteration 5 — Active Scene: Soundscapes Tab

### Relies on
- `SceneAudioEngine` (Iteration 4)
- Scene + Category data

### Goal
Build the primary gameplay screen's Soundscapes tab with live audio mixing.

### Build

**1. ViewModel** — `ActiveSceneSoundscapesViewModel`
- Load scene's categories and current mix/intensity states

**2. Screen** — `ActiveSceneSoundscapesScreen.kt`
- Master Atmosphere slider
- Category cards with MIX sliders and intensity selectors

---

## Iteration 6 — Active Scene: Soundboard Tab

### Relies on
- `SimpleAudioPlayer` (Iteration 1)
- FX data

### Goal
Build the Soundboard tab with the FX button grid.

### Build

**1. ViewModel** — `ActiveSceneSoundboardViewModel`
- Load scene's FX tracks

**2. Screen** — `ActiveSceneSoundboardScreen.kt`
- Grid of FX buttons for one-shot triggering

---

## Iteration 7 — Scene Switching & Motion System

### Relies on
- Audio Engine (Iteration 4)
- Navigation shell

### Goal
Implement scene switching with crossfade and the Arcanum Motion System transitions.

---

## Iteration 8 — Home Screen Dashboard

### Relies on
- All data entities

### Goal
Build the Home dashboard for resuming journeys and viewing quick stats.

---

## Iteration 9 — Credits, Trash & Polish

### Relies on
- All previous iterations

### Goal
Final pass — handle soft-deletes (Trash), Credits, and overall UI/UX polish.
