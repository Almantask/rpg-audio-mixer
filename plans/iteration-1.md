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
- *Ambiguity/Contradiction Highlight: Previous plan split Library into Soundscapes (with a Composer screen) and FX (with a mini-player, tags, search). The completed Iteration 1 here is much simpler. The missing complex features (Composer, Mini-player, Tags, Search) are re-introduced in Iterations 3 and 3.5 to not lose fidelity.*

**3. CI Audio Verification (Real Stack)**
- **Mandate**: Remove `FakeMusicPlayer`. Update all Cucumber steps to use `IdlingResource` waiting for `Player.STATE_READY`.

### Linked Features
- `app/src/androidTest/assets/features/system_audio_handling.feature` (baseline playback)

### Linked Designs
- `docs/design-overall.md`

### Android & Testing Implementation Details
- **Android**: `SimpleAudioPlayer` wrapping `androidx.media3.exoplayer.ExoPlayer` and `android.media.SoundPool`. Manage basic play/pause/stop functionality scoped to ViewModel lifecycle.
- **Testing**: JUnit tests for audio state transitions. Implement custom Espresso `IdlingResource` waiting for `ExoPlayer.STATE_READY` status.

