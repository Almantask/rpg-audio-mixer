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

### Linked Features
- `app/src/androidTest/assets/features/manage_fx_library.feature`
- `app/src/androidTest/assets/features/search_sounds.feature`
- `app/src/androidTest/assets/features/preview_fx_track.feature`

### Linked Designs
- `docs/designs/audio-library-fx-design.md`
- `docs/designs/AudioLibrary-FX.html`
- `docs/design-overall.md` §4.11, §5

### Android & Testing Implementation Details
- **Android**: Implement complex search and filtering logic via `Flow.combine()` in the ViewModel. Manage a singleton preview player (`MixedMusicPlayer` wrapping `ExoPlayer`), providing states to a global `MiniPlayerBar` driven by `AnimatedVisibility` for slide-up motion.
- **Testing**: Unit tests covering search strings and tag filters in the ViewModel. UI test asserting the `MiniPlayerBar` dismisses safely.

