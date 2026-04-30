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

**3. Foreground Service UI**
- Introduce **`PermissionGate`**: Prompt sequence for `POST_NOTIFICATIONS` on Android 13+ to ensure background audio playback features remain controllable via the notification shade.

### Linked Features
- `app/src/androidTest/assets/features/system_audio_handling.feature` (Foreground service logic, lock screen media controls)

### Linked Designs
- `docs/design-overall.md` §3, §4.6, §4.7, §4.8

### Android & Testing Implementation Details
- **Android**: Introduce `ForegroundService` and `MediaSessionCompat` to keep audio alive. Implement Android 13 `POST_NOTIFICATIONS` gating. Construct `CategoryPlayer` double buffer equal-power crossfade algorithm using $sin/cos$ mathematical bounds within standard `VolumeProvider` modifications.
- **Testing**: Robolectric and Mockito for Audio focus loss and restoration behaviors. JUnit boundary testing equal power fade values to ensure valid decibel reduction curves.

