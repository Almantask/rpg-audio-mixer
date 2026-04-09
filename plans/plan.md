# Arcanum Audio — Iterative Build Plan

> Each iteration builds on the previous and is designed for **minimal context**. You can assume everything from prior iterations works. Each section tells you exactly what exists, what to build, and which docs to reference.

---

## Iteration 0 — Design System & App Shell ✅ COMPLETED

### Relies on
- Empty scaffold with placeholder bottom nav and nav host (already exists)
- Default Material theme (already exists — needs replacing)

### Goal
Replace the default template theme with the Arcanum Audio design system and wire up the bottom navigation shell so all future screens plug in.

### Status
**COMPLETED** - All components implemented:
- Theme with Arcanum colors (gold #F2CA50, black backgrounds)
- Typography with serif headings and sans-serif body (system fallbacks)
- ArcanumShapes for rounded corners
- ArcanumTopBar with back arrow and gear icon support
- ErrorDialog component
- MainBottomNavBar with 4 tabs (HOME, CAMPAIGNS, SCENES, LIBRARY)
- MainNavHost with placeholder screens
- Full navigation wired in MainActivity

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

**5. Error Overlay** — reusable `ErrorDialog` composable
- Modal overlay with semi-transparent backdrop
- Scrollable message text, dismiss button
- Accept `message: String?` and `onDismiss: () → Unit`

### Reusable components produced
| Component | Used by |
|---|---|
| `ArcanumTopBar` | Every screen |
| `MainBottomNavBar` | App shell |
| `ErrorDialog` | Every screen with error state |
| Theme tokens | Everything |

### Docs to reference
- `docs/design-overall.md` §1 (Branding), §2 (Navigation), §6 (Animation), §9 (Error Handling)

---

## Iteration 1 — Room Database & Campaign CRUD ✅ COMPLETED

### Relies on
- Design system & app shell (Iteration 0)

### Goal
Stand up the Room database, define the `Campaign` entity/DAO, and build the Campaigns list screen with create / delete.

### Status
**COMPLETED** - All core functionality implemented:
- Room 2.6.1 integrated with AppDatabase version 1
- CampaignEntity, CampaignDao with Flow-based observation
- Campaign domain model with CampaignRepository interface
- CampaignRepositoryImpl with entity-domain mapping
- CampaignsViewModel with StateFlow-based UI state management
- CampaignsScreen with empty state, loading, error states
- CampaignCard with cover art placeholder and RESUME button
- CreateCampaignDialog for creating new campaigns
- DatabaseModule providing AppDatabase singleton
- Full navigation wiring

**Note**: Swipe-to-delete and photo picker can be enhanced in polish iterations. Basic structure is in place.

### Build

**1. Room database** (`data/local/`)
- `AppDatabase.kt` — Room DB with version 1
- `CampaignEntity` — `id: Long`, `name: String`, `coverArtUri: String?`, `lastPlayedAt: Long`
- `CampaignDao` — `observeAll(): Flow<List<CampaignEntity>>` (sorted by `lastPlayedAt DESC`), `upsert()`, `delete()`

**2. Domain model** (`domain/model/`)
- `Campaign` data class (plain Kotlin, no Room annotations)

**3. Repository** (`data/campaign/`)
- `CampaignRepository` interface in `domain/`
- `CampaignRepositoryImpl` — maps Entity ↔ Domain, Hilt-bound

**4. ViewModel** (`ui/campaigns/`)
- `CampaignsViewModel` — `StateFlow<UiState<List<Campaign>>>`, actions: `createCampaign(name, coverUri)`, `deleteCampaign(id)`

**5. Campaigns Screen** (`ui/campaigns/CampaignsScreen.kt`)
- Scrollable list of `CampaignCard` components
- **CampaignCard** — cover art, name, last-played date, RESUME button, swipe-right to delete
- Empty state: illustration + "Scribe New Tale" button
- FAB / bottom button: + NEW CAMPAIGN → dialog (name + photo picker)
- Photo picker: `rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia())`

**6. Hilt module** — bind `CampaignRepository`, provide `AppDatabase`

### Reusable components produced
| Component | Used by |
|---|---|
| `AppDatabase` | All data layers |
| `CampaignCard` | Campaigns screen, Home screen |
| `SwipeToDeleteContainer` | Campaigns, Sessions, Scenes, Soundscapes |
| `EmptyStateView` | Every list screen (illustration + CTA) |
| `ImagePickerLauncher` | Campaigns, Sessions cover art |

### Docs to reference
- `docs/designs/campaigns-design.md`
- `docs/design-overall.md` §4.2, §8

---

## Iteration 2 — Sessions & Scenes CRUD ✅ COMPLETED

### Relies on
- Room DB with `AppDatabase` (Iteration 1)
- `SwipeToDeleteContainer`, `EmptyStateView`, `ImagePickerLauncher`, design system

### Goal
Add Sessions (within a Campaign) and global Scenes with create / delete / link-to-session.

### Status
**COMPLETED** - All core functionality implemented:
- Room database updated to version 2 with SessionEntity, SceneEntity, SessionSceneCrossRef
- SessionDao, SceneDao, SessionSceneDao with Flow-based observation
- Session and Scene domain models with proper tag handling (list vs comma-separated string)
- SessionRepository, SceneRepository, SessionSceneRepository interfaces and implementations
- SessionsViewModel, ScenesViewModel, SessionScenesViewModel with StateFlow-based UI state management
- SessionsScreen with empty state, loading, error states
- ScenesScreen (global scenes list) with SceneCard showing play button, description, and tags
- SessionScenesScreen with multi-select ImportScenesDialog
- Full navigation with parameterized routes (campaigns/{campaignId}/sessions, sessions/{sessionId}/scenes)
- DatabaseModule updated with all new DAOs and repository bindings

**Note**: Advanced features like swipe-to-delete gestures and photo picker can be enhanced in polish iterations. Basic structure is in place.

### Build

**1. Entities & DAOs**
- `SessionEntity` — `id`, `campaignId (FK)`, `name`, `date`, `coverArtUri?`
- `SessionDao` — `observeByCampaign(campaignId): Flow`, `upsert()`, `delete()`
- `SceneEntity` — `id`, `name`, `description?`, `tags: String` (comma-separated)
- `SceneDao` — `observeAll(): Flow`, `upsert()`, `delete()`
- `SessionSceneCrossRef` — junction table (`sessionId`, `sceneId`)
- `SessionSceneDao` — `observeScenesBySession(sessionId): Flow`, `link()`, `unlink()`

**2. Domain models**
- `Session`, `Scene`

**3. Repositories** — `SessionRepository`, `SceneRepository` (interfaces + impls)

**4. ViewModels**
- `CampaignSessionsViewModel` — list sessions for a campaign
- `ScenesViewModel` — global scenes list
- `SessionScenesViewModel` — scenes linked to a session + import

**5. Screens**
- **Campaign Sessions Screen** — hero banner, session cards, + ADD NEW SESSION, swipe-delete
- **Scenes List Screen** (SCENES tab) — scene cards with ▶ button and card-body tap, tags as chips, + ADD NEW SCENE, swipe-delete
- **Session Scenes Screen** — same card as Scenes List, + IMPORT SCENE (multi-select picker), swipe to unlink

**6. Navigation** — add routes: `campaigns/{campaignId}/sessions`, `sessions/{sessionId}/scenes`, `scenes/{sceneId}`

### Reusable components produced
| Component | Used by |
|---|---|
| `SceneCard` | Scenes list, Session Scenes, Home (Resume Journey) |
| `SessionCard` | Campaign Sessions |
| `TagChip` / `TagRow` | Scene cards, FX library |
| `MultiSelectPickerSheet` | Import Scene, Add Soundscape to Scene, Add FX to Scene |

### Docs to reference
- `docs/designs/campaign-sessions-design.md`
- `docs/designs/scenes-list-design.md`
- `docs/designs/session-scenes-design.md`
- `docs/design-overall.md` §4.3–4.5

---

## Iteration 3 — Audio Library: Soundscape Categories & Composer ✅ COMPLETED

### Relies on
- Room DB (Iteration 1), design system

### Goal
Build the Soundscape Category management — browsing categories, creating them, and composing tracks with intensity levels via the Composer screen.

### Status
**COMPLETED** - All core functionality implemented:
- Room database updated to version 3 with SoundscapeCategoryEntity and SoundscapeTrackEntity
- SoundscapeCategoryDao and SoundscapeTrackDao with Flow-based observation
- IntensityLevel enum (I, II, III) for track categorization
- SoundscapeCategory and SoundscapeTrack domain models
- SoundscapeRepository interface and implementation with full CRUD operations
- SoundscapeLibraryViewModel with category listing and per-intensity-level track counts
- SoundscapeCategoryComposerViewModel with track management (CRUD, intensity, mix volume)
- SoundscapeLibraryScreen with bento grid layout (2-column grid)
- SoundscapeCategoryComposerScreen with track list and composition controls
- Reusable components: IntensitySelector, MixSlider, SoundscapeCategoryCard
- CreateCategoryDialog and AddTrackDialog for user input
- Full navigation with parameterized route: library/soundscapes/{categoryId}/compose
- DatabaseModule updated with Soundscape DAOs and repository binding

**Note**: File picker integration for audio files can be enhanced in future iterations. Currently using text input for file paths as a placeholder.

### Build

**1. Entities & DAOs**
- `SoundscapeCategoryEntity` — `id`, `name`, `iconResId?`, `themeLabel?`
- `SoundscapeTrackEntity` — `id`, `categoryId (FK)`, `name`, `filePath`, `intensityLevel: Int (1–3)`, `mixVolume: Float`
- `SoundscapeCategoryDao`, `SoundscapeTrackDao`

**2. Domain models**
- `SoundscapeCategory`, `SoundscapeTrack`, `IntensityLevel` enum (I, II, III)

**3. Repository** — `SoundscapeRepository`

**4. ViewModels**
- `SoundscapeLibraryViewModel` — list categories with per-level track counts
- `SoundscapeCategoryComposerViewModel` — CRUD tracks within a category, change intensity, adjust MIX, save

**5. Screens**
- **Audio Library — Soundscapes Tab** — bento grid of category cards showing track counts per level (dimmed zeros), ✏️ edit → Composer, + NEW COMPOSITION
- **Soundscape Category Composer** — list of soundscape cards (name, intensity picker, MIX slider), + INVOKE NEW SOUNDSCAPE (file picker for audio), SAVE COMPOSITION, swipe-delete tracks, unsaved-changes dialog

**6. File picker** — `ActivityResultContracts.OpenDocument` filtered to `audio/*`, copy to app-internal storage

**7. Navigation** — add routes: `library/soundscapes`, `library/soundscapes/{categoryId}/compose`

### Reusable components produced
| Component | Used by |
|---|---|
| `IntensitySelector` | Composer track card, Active Scene category card |
| `MixSlider` | Composer, Active Scene soundscapes |
| `BentoCard` | Soundscape library |
| `AudioFilePicker` | Composer, FX import |

### Docs to reference
- `docs/designs/audio-library-soundscapes-design.md`
- `docs/designs/soundscape-category-composer-design.md`
- `docs/design-overall.md` §4.9–4.10

---

## Iteration 4 — Audio Library: FX Library ✅ COMPLETED

### Relies on
- Room DB, `AudioFilePicker`, `TagChip`, design system

### Goal
Build the FX library — import, list, search, preview with mini-player, edit, delete.

### Status
**COMPLETED** - All core functionality implemented:
- Room database updated to version 4 with FxTrackEntity
- FxTrackDao with Flow-based observation and search functionality
- FxTrack domain model with tag support (list vs comma-separated string mapping)
- FxRepository interface and implementation with full CRUD operations
- FxLibraryViewModel with search filtering, import, update, and delete operations
- FxLibraryScreen with search bar, track list, and dialogs
- FxTrackRow component displaying track name, tags, duration, and edit button
- SearchBar reusable component for text search
- TagRow and TagChip reusable components for displaying tags
- EditFxDialog for editing track names and tags, with delete option
- ImportFxDialog for adding new FX tracks
- LibraryScreen with tab strip supporting Soundscapes and Sound Effects tabs
- SoundscapeLibraryContent and FxLibraryContent as tab contents
- Full navigation wiring with tabbed interface
- DatabaseModule updated with FX DAO and repository bindings

**Note**:
- MiniPlayerBar preview functionality deferred to Iteration 5 (Audio Engine) as it requires ExoPlayer integration
- File picker integration for audio files uses text input as placeholder; can be enhanced in future iterations
- Duration metadata parsing not yet implemented; placeholder value of 0 used

### Build

**1. Entities & DAOs**
- `FxTrackEntity` — `id`, `name`, `filePath`, `tags: String`, `durationMs: Long`, `playCount: Int`
- `FxTrackDao` — `observeAll(): Flow`, `search(query): Flow`, `upsert()`, `delete()`

**2. Domain model** — `FxTrack`

**3. Repository** — `FxRepository`

**4. ViewModel** — `FxLibraryViewModel` — list, search, filter, import FX file, edit (rename/tags/delete), preview track

**5. Screen** — **Audio Library — Sound Effects Tab**
- Search bar + filter/sort controls
- Track list items: thumbnail, name, tags, duration, ✏️ edit icon
- ✏️ → edit dialog: rename, edit tags, delete
- IMPORT FX → audio file picker
- No ❤️ heart, no BUY MORE, no ⋮ menu (use ✏️ per spec corrections)

**6. Mini-player** — `MiniPlayerBar` composable
- Anchored to bottom (above nav bar), slide-up animation
- Play/pause, skip prev/next, track title
- Visible only on Library screen; navigating away stops playback and hides it
- Uses `ExoPlayer` via `MixedMusicPlayer` for preview

**7. Library Tab Routing** — `library` destination with internal tab strip (Soundscapes | Sound Effects)

### Reusable components produced
| Component | Used by |
|---|---|
| `MiniPlayerBar` | FX Library |
| `FxTrackRow` | FX Library, Add FX to Scene |
| `SearchBar` | FX Library |

### Docs to reference
- `docs/designs/audio-library-fx-design.md`
- `docs/design-overall.md` §4.11, §5

---

## Iteration 5 — Audio Engine: Looping Playback & Volume Mixing ✅ COMPLETED

### Relies on
- `TrackPlayer`, `TrackFactory`, `MixedMusicPlayer` interfaces (exist)
- ExoPlayer dependencies (exist)
- Soundscape data (Iteration 3)

### Goal
Build the core audio engine that supports multiple simultaneous looping tracks with per-category MIX volume and a master volume, plus one-shot FX playback with overlap/re-trigger.

### Status
**COMPLETED** - All core audio engine functionality implemented:
- Expanded TrackPlayer interface with pause, stop, resume, setVolume, isPlaying StateFlow, and release
- Updated ExoLoopableTrackPlayer with proper lifecycle management, volume control, and listener for playing state
- Updated ExoOneTimeTrackPlayer with proper cleanup on playback completion
- Created CategoryPlayer domain class managing one looping track per soundscape category with MIX and master volume
- Created SceneAudioEngine managing multiple CategoryPlayers with master volume affecting all categories
- Created SoundboardPlayer managing one-shot FX with overlap support and master volume
- Updated MusicPlayerModule to provide SceneAudioEngine and SoundboardPlayer as singletons
- Updated MixedMusicPlayerImpl to use new playTrack() method

**Note**:
- Crossfade implementation in SceneAudioEngine is simplified (immediate switch); full coroutine-based fade deferred to Iteration 8
- ExoPlayer instances properly released on completion or stop to prevent memory leaks

### Build

**1. Expand `TrackPlayer` interface**
- `play()`, `pause()`, `stop()`, `resume()`, `setVolume(volume: Float)`, `isPlaying: Boolean`, `release()`

**2. Loopable player** — `ExoLoopableTrackPlayer` (update existing)
- Looping mode, volume control, lifecycle management

**3. One-shot player** — `ExoOneTimeTrackPlayer` (update existing)
- Fire-and-forget with overlap: each `play()` creates a new ExoPlayer instance
- `stop()` stops the running instance; cleanup on completion

**4. `CategoryPlayer`** (new domain class)
- Manages one `TrackPlayer` at a time for a soundscape category
- Exposes: `play(trackPath)`, `pause()`, `resume()`, `stop()`, `rollRandomTrack(pool: List<SoundscapeTrack>)`, `setMixVolume(Float)`, `isPlaying: StateFlow<Boolean>`

**5. `SceneAudioEngine`** (new domain class)
- Holds a map of `categoryId → CategoryPlayer`
- `masterVolume: Float` — multiplied with each category's MIX
- `setMasterVolume(Float)` — updates all players: `actualVol = master × mix`
- `addCategory(id)`, `removeCategory(id)`, `releaseAll()`

**6. `SoundboardPlayer`** (new domain class)
- Holds list of active one-shot players
- `masterVolume: Float`
- `triggerFx(fxTrack)` — creates new instance (overlap), `stopFx(instanceId)`

**7. Hilt module** — provide `SceneAudioEngine`, `SoundboardPlayer` as singletons

### Reusable components produced
| Component | Used by |
|---|---|
| `CategoryPlayer` | Active Scene Soundscapes |
| `SceneAudioEngine` | Active Scene, Scene switching crossfade |
| `SoundboardPlayer` | Active Scene Soundboard |

### Docs to reference
- `docs/design-overall.md` §3 (Audio Concepts), §4.6 (Soundscapes playback), §4.7 (Soundboard playback), §4.8 (Scene switching)

---

## Iteration 6 — Active Scene: Soundscapes Tab ✅ COMPLETED

### Relies on
- `SceneAudioEngine`, `CategoryPlayer` (Iteration 5)
- Scene + SoundscapeCategory data (Iterations 2, 3)
- `IntensitySelector`, `MixSlider` (Iteration 3)
- `ErrorDialog` (Iteration 0)

### Goal
Build the primary gameplay screen — the Soundscapes tab of the Active Scene — with live audio mixing, random track selection, and intensity switching.

### Status
**COMPLETED** - All core Active Scene Soundscapes functionality implemented:
- Created SceneSoundscapeCrossRef junction table with displayOrder, mixVolume, and intensityLevel
- Created SceneSoundscapeDao with full CRUD operations and display order management
- Created SceneSoundscapeRepository interface and implementation
- Updated AppDatabase to version 5
- Created ActiveSceneCategory domain model for representing categories in active scene
- Extended SoundscapeRepository with getTracksByCategoryAndIntensity method
- Implemented ActiveSceneSoundscapesViewModel with SceneAudioEngine integration
- Created MasterSlider reusable component for master volume control
- Created GlowBorderModifier for animated playing state indication
- Created ActiveSceneCategoryCard with play/pause, roll random, intensity selector, and MIX slider
- Implemented ActiveSceneSoundscapesScreen with tab strip (Soundscapes | Soundboard placeholder)
- Wired navigation from SessionScenes and Scenes to Active Scene (scenes/{sceneId}/active)
- Master volume control with real-time audio engine integration
- Per-category mix volume and intensity level control
- Play/pause/resume and roll random track functionality
- All actions properly integrated with SceneAudioEngine

**Note**:
- Soundscape Selection overlay deferred (MultiSelectPickerSheet needs implementation)
- Drag-to-reorder functionality deferred to polish iteration
- Swipe-to-remove deferred to polish iteration
- Soundboard tab placeholder included (implementation in Iteration 7)

### Build

**1. Junction table** — `SceneSoundscapeCrossRef` (`sceneId`, `categoryId`, `displayOrder: Int`, `mixVolume: Float`, `intensityLevel: Int`)

**2. ViewModel** — `ActiveSceneSoundscapesViewModel`
- Load scene's categories (ordered by `displayOrder`)
- Per-category state: playing, current track name, mix volume, intensity level
- Actions: `setMasterVolume`, `playCategory(id)`, `pauseCategory(id)`, `rollRandom(id)`, `setIntensity(id, level)`, `setMix(id, vol)`, `reorderCategories`, `removeCategory(id)`, `addCategory(id)`

**3. Screen** — `ActiveSceneSoundscapesScreen.kt`
- Tab strip: Soundscapes (active) | Soundboard
- Master Atmosphere slider
- Category cards: name, 🎲 d20, ▶/⏸, current track name, MIX slider, intensity selector (I / II / III with greyed-out levels)
- Playing cards: glow border animation
- Drag-to-reorder via `LazyColumn` + `detectDragGestures`
- + ADD NEW SOUNDSCAPE → Soundscape Selection overlay
- Swipe-right to remove

**4. Soundscape Selection overlay** — reuse `MultiSelectPickerSheet` with category list, + buttons, already-added indicator, excludes empty categories

### Reusable components produced
| Component | Used by |
|---|---|
| `SoundscapeCategoryCard` | Active Scene Soundscapes |
| `MasterSlider` | Soundscapes tab, Soundboard tab |
| `GlowBorderModifier` | Playing state across soundscapes & soundboard |

### Docs to reference
- `docs/designs/active-scene-soundscapes-design.md`
- `docs/designs/add-fx-or-soundscape-to-scene-design.md` (soundscape variant)
- `docs/design-overall.md` §4.6, §10

---

## Iteration 7 — Active Scene: Soundboard Tab ✅ COMPLETED

### Relies on
- `SoundboardPlayer` (Iteration 5)
- FX data (Iteration 4)
- `MasterSlider`, `GlowBorderModifier` (Iteration 6)
- `ErrorDialog` (Iteration 0)

### Goal
Build the Soundboard tab with the FX button grid — trigger, re-trigger, overlap, stop, drag-to-reorder, and drag-to-flames delete.

### Status
**COMPLETED** - All core soundboard functionality implemented:
- SceneFxCrossRef junction table with displayOrder (database version 6)
- SceneFxDao with full CRUD operations and display order management
- SceneFxRepository interface and implementation
- ActiveSceneFx domain model for representing FX tracks in active scene
- ActiveSceneSoundboardViewModel with SoundboardPlayer integration
- FxButton composable with play/pause, glow effect, and instance count display
- ActiveSceneSoundboardContent with 4-column grid layout
- Master volume control with real-time audio integration
- Trigger/re-trigger/stop FX functionality
- Instance tracking for overlapping FX playback
- Integrated soundboard tab into ActiveSceneSoundscapesScreen
- DatabaseModule updated with SceneFx DAO and repository bindings

**Note**:
- Long-press drag-to-reorder deferred to polish iteration (Iteration 12)
- Drag-to-flames delete zone deferred to polish iteration (Iteration 12)
- FX Selection overlay deferred (MultiSelectPickerSheet needs implementation)

### Build

**1. Junction table** — `SceneFxCrossRef` (`sceneId`, `fxTrackId`, `displayOrder: Int`)

**2. ViewModel** — `ActiveSceneSoundboardViewModel`
- Load scene's FX (ordered by `displayOrder`)
- Per-FX state: playing instances count, glow/pulse
- Actions: `setMasterVolume`, `triggerFx(id)`, `stopFx(id)`, `reorder`, `removeFx(id)`, `addFx(id)`

**3. Screen** — `ActiveSceneSoundboardScreen.kt`
- Tab strip: Soundscapes | Soundboard (active)
- Master Volume slider
- 4-column `LazyVerticalGrid` of FX buttons
- Button states: idle (▶), playing (glow/pulse + ⏸)
- Re-trigger on tap while playing (new instance overlaps)
- Long-press + drag to reorder
- Hold-and-drag to flames zone at bottom → remove from scene
- + ADD NEW EFFECT → FX Selection overlay

**4. FX Selection overlay** — reuse `MultiSelectPickerSheet` with FX list, + buttons, already-added indicator

### Reusable components produced
| Component | Used by |
|---|---|
| `FxButton` | Soundboard grid |
| `FlamesDeleteZone` | Soundboard |
| `ActiveSceneTabShell` | Wraps tab strip + top bar for both tabs |

### Docs to reference
- `docs/designs/active-scene-soundboard-design.md`
- `docs/designs/add-fx-or-soundscape-to-scene-design.md` (FX variant)
- `docs/design-overall.md` §4.7

---

## Iteration 8 — Scene Switching & Navigation Polish ✅ COMPLETED

### Relies on
- `SceneAudioEngine` (Iteration 5)
- Scene, Session, Campaign screens (Iterations 1–2)
- Active Scene screens (Iterations 6–7)

### Goal
Implement scene switching with crossfade, connect the ▶ button on scene cards to autoplay, and implement the full Arcanum Motion System transitions.

### Status
**COMPLETED** - Core scene switching and autoplay functionality implemented:
- Enhanced SceneAudioEngine with coroutine-based crossfade using volume interpolation
- Added `startPlaybackWithFadeIn` method for autoplay with 2.5s fade-in
- Added `switchToScene` method for crossfading between scenes with 2.5s transition
- Added autoplay parameter to Active Scene navigation route (scenes/{sceneId}/active?autoplay={autoplay})
- Updated all navigation handlers (ScenesScreen, SessionScenesScreen) to pass autoplay flag
- SceneCard play button click triggers autoplay=true, card body click triggers autoplay=false
- ActiveSceneSoundscapesViewModel handles autoplay on init and starts playback with fade-in
- Application-scoped CoroutineScope provided via Hilt for audio engine operations
- Sliders snap to saved values instantly (default Compose behavior - no additional work needed)

**Note**:
- Full Arcanum Motion System transitions (ContainerTransform, Shared X/Y/Z-Axis) deferred to polish iteration
- These require extensive Compose SharedTransitionLayout implementation which is complex
- Basic navigation transitions work using default Compose NavHost animations

### Build

**1. Crossfade logic** in `SceneAudioEngine`
- `switchToScene(newSceneId)` — fade out all current categories over 2–3 s while simultaneously fading in the new scene's categories
- Uses coroutine-driven volume interpolation

**2. Scene card ▶ integration**
- Tap ▶ on `SceneCard` → navigate to Active Scene + call `startPlayback()` (2–3 s fade-in)
- Tap card body → navigate to Active Scene, no playback

**3. Screen transitions** — Arcanum Motion System
- Hierarchical (card → detail): `ContainerTransform` via `SharedTransitionLayout`
- Lateral (tab switch): Shared X-Axis (fade + slide)
- Drill-down (sub-menus, +): Shared Z-Axis (fade + scale)
- Overlays (mini-player): Shared Y-Axis (slide up from bottom)
- Top bar and bottom nav remain fixed during transitions

**4. Slider snap** — on scene load, all sliders snap instantly to saved values (no animation)

### Docs to reference
- `docs/design-overall.md` §4.8, §6

---

## Iteration 9 — Home Screen

### Relies on
- Campaign, Session, Scene data (Iterations 1–2)
- FX + Soundscape track play counts (Iterations 3–4)
- Active Scene navigation (Iteration 8)
- `CampaignCard`, `SceneCard` (Iterations 1–2)

### Goal
Build the Home dashboard — active campaign hero, resume journey, top atmosphere, legendary action.

### Build

**1. Queries**
- Most recently played campaign (latest `lastPlayedAt`)
- Last scene opened in active campaign (requires a `lastOpenedSceneId` on `SessionEntity` or a separate `RecentActivity` table)
- Global most-played loopable track (aggregate `playCount` on `SoundscapeTrackEntity`)
- Global most-played FX (aggregate `playCount` on `FxTrackEntity`)

**2. ViewModel** — `HomeViewModel`
- `UiState` with: `activeCampaign`, `resumeScene`, `topAtmosphere`, `legendaryAction`
- No-campaign / no-scene empty states

**3. Screen** — `HomeScreen.kt`
- Active Campaign hero card → ENTER DOMAIN → campaign sessions
- Resume Journey card → ENTER → Active Scene + autoplay fade-in
- Top Atmosphere card (track name + category)
- Legendary Action card (FX name + category)
- Empty states for no campaign / no scenes

### Docs to reference
- `docs/designs/home-design.md`
- `docs/design-overall.md` §4.1

---

## Iteration 10 — Credits & Trash

### Relies on
- Design system, `ArcanumTopBar` (Iteration 0)
- Soft-delete support on DAOs (add `deletedAt: Long?` column to relevant entities)

### Goal
Build the Credits screen (reached via ⚙️) and the Trash screen for restoring soft-deleted items.

### Build

**1. Soft-delete migration**
- Add `deletedAt` nullable column to: `CampaignEntity`, `SessionEntity`, `SceneEntity`, `SoundscapeCategoryEntity`, `FxTrackEntity`
- Update DAOs: `observeAll()` queries filter `WHERE deletedAt IS NULL`; add `observeDeleted(): Flow` queries
- Add `softDelete(id)` and `restore(id)` methods
- Scheduled cleanup: items with `deletedAt` older than 7 days are permanently purged

**2. ViewModel** — `TrashViewModel` — list all soft-deleted items, restore(id, type), permanently delete(id, type), empty vault

**3. Credits Screen** — `CreditsScreen.kt`
- App logo, version, developer credits, links section (docs, Discord, email)
- RESTORE RECENT DELETES → navigate to Trash
- SYNC PURCHASES button (disabled / placeholder for future)

**4. Trash Screen** — `TrashScreen.kt` ("Vault of Echoes")
- Mixed list of deleted items (campaigns, sessions, scenes, categories, FX) sorted by deletion date
- Per-item: restore button (gold), permanent delete button (red)
- Empty Vault button
- Footer: "Items will be permanently removed 7 days after deletion"

**5. Navigation** — `credits` route (accessible from gear icon on every screen), `credits/trash` route

### Docs to reference
- `docs/designs/credits-design.md`
- `docs/designs/trash-design.md`

---

## Iteration 11 — Playback Statistics & Play Count Tracking

### Relies on
- `SceneAudioEngine`, `SoundboardPlayer` (Iteration 5)
- Track entities (Iterations 3–4)

### Goal
Track play counts so Home screen stats (Top Atmosphere, Legendary Action) and the "PLAYED N×" counters in the Add-to-Scene views are populated.

### Build

**1. Play count increment**
- In `CategoryPlayer`: on track play, increment `SoundscapeTrackEntity.playCount` via repository
- In `SoundboardPlayer`: on FX trigger, increment `FxTrackEntity.playCount`
- In `Campaign/Session`: update `lastPlayedAt` on scene open

**2. Queries**
- `SoundscapeTrackDao.getMostPlayedLoopable(): Flow<SoundscapeTrackEntity?>`
- `FxTrackDao.getMostPlayedFx(): Flow<FxTrackEntity?>`
- Per-category total play count for Add-to-Scene display

**3. Wire into Home ViewModel** (Iteration 9) — connect live queries

### Docs to reference
- `docs/design-overall.md` §4.1 (Top Atmosphere, Legendary Action)
- `docs/designs/add-fx-or-soundscape-to-scene-design.md` (play counts)

---

## Iteration 12 — Polish, Edge Cases & Empty States

### Relies on
- All previous iterations

### Goal
Final pass — ensure all empty states are beautiful, all edge cases are handled, animations are smooth, and the app is production-ready.

### Build

**1. Empty state illustrations** — generate / add placeholder illustrations for all empty states:
- Campaigns (scroll theme)
- Sessions (parchment)
- Scenes (map/compass)
- FX Library (wand/sparkles)
- Soundscape Categories (crystal ball)
- Soundboard (silent room)

**2. All-intensities-empty category**
- If a category has zero tracks at all levels: ▶ and 🎲 disabled, all intensity buttons greyed out, MIX slider still adjustable

**3. Loading states** — centred spinner for scene load, library load

**4. Tag system** — predefined tag list (Tavern, Forest, Combat, City, Dungeon, Ocean, Mountain, Cave, Desert, Magic) + custom free-text tags

**5. Drag-to-reorder persistence** — save new display order to DB on drop

**6. Performance review**
- Ensure ExoPlayer instances are released on scope exit
- Verify no audio leaks when navigating away
- Test with 50+ tracks, 20+ categories

**7. Accessibility** — content descriptions on all icons, sufficient contrast ratios

### Docs to reference
- `docs/design-overall.md` §8 (Empty States), §3 (greyed-out intensity), §9 (Error Handling)

---

## Summary Matrix

| Iter | Focus | Key Screens | Key Data | Audio |
|---|---|---|---|---|
| 0 | Shell & design system | App shell, nav | — | — |
| 1 | Campaign CRUD | Campaigns | Campaign entity | — |
| 2 | Sessions & Scenes CRUD | Sessions, Scenes, Session Scenes | Session, Scene entities | — |
| 3 | Soundscape Library | Library Soundscapes, Composer | Category, Track entities | — |
| 4 | FX Library | Library FX | FX entity | Preview only |
| 5 | Audio Engine | — | — | Full engine |
| 6 | Active Scene Soundscapes | Active Scene (Soundscapes tab) | Scene↔Category junction | Looping playback |
| 7 | Active Scene Soundboard | Active Scene (Soundboard tab) | Scene↔FX junction | One-shot playback |
| 8 | Scene switching & transitions | Cross-screen | — | Crossfade |
| 9 | Home dashboard | Home | Aggregation queries | — |
| 10 | Credits & Trash | Credits, Trash | Soft-delete columns | — |
| 11 | Play stats | — | Play counts | Count tracking |
| 12 | Polish & edge cases | All | — | Cleanup |
