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

