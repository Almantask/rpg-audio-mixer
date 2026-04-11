# Arcanum Audio — Technical Migration Plan

This document tracks the evolution of the technical stack across iterations, documenting breaking changes and data migration strategies.

## Migration 1: In-Memory to Room Persistence (Iter 1 → Iter 2)

### Current State (Iteration 1)
- **Library Data**: Managed via `StateFlow<List<Uri>>` in `LibraryViewModel`.
- **Persistence**: None. List is lost on process death.
- **File Handling**: URIs refer to original file locations (system picker).

### Target State (Iteration 2)
- **Library Data**: Managed via `AudioTrackDao` in Room.
- **Persistence**: Permanent. SURVIVES app restarts and process death.
- **File Handling**: Imported files are **copied** to the app's internal storage (`context.filesDir`) to ensure availability.

### Breaking Changes
- `LibraryViewModel` constructor will now require `AudioTrackRepository` instead of simple in-memory list.
- `AudioTrack` domain model must be introduced to wrap Room entities.

---

## Technical Debt & Deviations
*This section is maintained by the **Project Historian** to track non-standard implementations.*

- **Error Overlay (Iter 0)**: Using a simple modal overlay instead of Snackbars for MVP speed.
- **SimpleAudioPlayer (Iter 1)**: ViewModel-scoped as per user decision (playback stops on screen exit).
- **SoundPool for FX (Iter 1)**: Using `SoundPool` for soundboard one-shots instead of `ExoPlayer` to ensure near-zero latency.
- **Double-Buffer Playback (Iter 4)**: Implementing a dual-player architecture per category to support seamless 2-second crossfades during intensity/track transitions.
- **Logarithmic Volume (Iter 4)**: Mapping UI slider values to $x^3$ to provide a natural volume progression.
- **Focus Loss Timeout (Iter 4)**: Auto-resume is disabled if the system interruption exceeds 3 minutes.
- **MediaSession (Iter 4)**: Integration with lock screen controls and Bluetooth remotes.
