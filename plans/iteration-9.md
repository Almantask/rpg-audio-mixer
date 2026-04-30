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

### Linked Features
- `app/src/androidTest/assets/features/master_controls.feature`
- `app/src/androidTest/assets/features/session_lock.feature`
- `app/src/androidTest/assets/features/scene_cloning.feature`

### Linked Designs
- (Inherits Active Scene UI Designs from Iteration 7)

### Android & Testing Implementation Details
- **Android**: `java.util.zip.ZipOutputStream` / `ZipInputStream` generating `.arcanum` archives bundling `CampaignEntity` serialization and local SQLite tables with raw `.wav`/`.mp3` blobs. `SceneAudioEngine` look-ahead limiting via volume reduction logic triggered recursively on intense thresholds.
- **Testing**: Validate ZIP integrity and checksums in Unit Tests. Espresso assertions verifying session lock (`isLocked = true`) reliably disables interactive Reorder/Delete modifiers.
