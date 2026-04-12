After a lib is added - do a gradle sync. This should enable the run button.

$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew assemble - builds code

$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew build - builds code & runs tests

---

## 🏛️ Project Governance & Skill Consolidation (2026-04-11)

### Consolidation of Agent Skills
- **Context**: The project had redundant skill definitions in `.github/skills/`, `.agents/skills/SKILL.md`, and `.agents/skills/[role]/SKILL.md`.
- **Action**: Consolidated all specialized skills into `.agents/skills/[role]/SKILL.md` for Gemini CLI and synced to `.github/skills/` for Antigravity/Copilot compatibility.
- **Outcome**: Resolved "Skill conflict detected" warnings and established a single source of truth for technical standards.

### 🛠️ DevOps Audit Findings
- **Inconsistency**: Version Catalog (`libs.versions.toml`) had mismatched versions between definitions and library entries (e.g., `activityCompose`, `lifecycleRuntimeKtx`).
- **Optimization**: CI pipeline lacks explicit Gradle caching for `~/.gradle/caches`, causing slower runs.
- **Next Steps**: Standardize version catalog and implement robust CI caching.

### 🎧 Audio Infrastructure Audit
- **MP3 Limitation**: Current `.mp3` assets in `res/raw/` suffer from format-induced silence at loop points, affecting seamless soundscapes.
- **Compression**: Soundscape files are large (~4MB each), threatening APK size limits.
- **Recommendation**: Transition to **Ogg/Opus** for perfect looping and superior quality-to-size ratio.

### 👥 Expanded Collaboration Model
Added specialized roles to bridge the gap between development and delivery:
- **DevOps Engineer**: Build/CI/Release ownership.
- **Audio Specialist**: Low-latency, high-fidelity playback engine and resource optimization.
- **Project Historian**: Maintaining this file and the `CUCUMBER_TESTING_GUIDE.md`.

---

## 🎧 Audio Asset Optimization: MP3 to Ogg/Opus (2026-04-11)

### Technical Rationale
- **Gapless Looping**: Opus in an Ogg container supports sample-accurate looping, eliminating the tiny silent gaps found in MP3 files.
- **Superior Compression**: Opus provides higher audio quality at lower bitrates (e.g., 96kbps Opus vs. 128kbps MP3).
- **Lower Latency**: Opus is optimized for low-delay playback, improving the responsiveness of soundboard triggers.

### Conversion Script
The optimized conversion process is managed by a standalone script:
- **Script Path**: `.agents/skills/audio-specialist/scripts/convert_to_ogg.ps1`
- **Usage**:
  ```powershell
  cd app/src/main/res/raw
  pwsh .agents/skills/audio-specialist/scripts/convert_to_ogg.ps1
  ```

### Requirements
- **FFmpeg**: Must be installed and available in the system PATH. 
  - Install via PowerShell: `winget install ffmpeg`

---

## 💾 File Ownership Shift: System URIs to Local Copies (2026-04-11)

### Architectural Decision
Between Iteration 1 and Iteration 2, the app transitions from referencing external files via system-provided URIs to making local copies in the app's internal storage (`context.filesDir`).

### Technical Rationale
- **Reliability**: External URIs are fragile. Users can delete or move the original files, and Android's URI permission persistence is not always guaranteed (especially after reboots or OS updates).
- **Control**: By copying files to `filesDir/audio_library/`, the app becomes the owner of the file lifecycle, ensuring that soundscapes and FX are always available when needed.
- **Performance**: Accessing internal storage is generally more predictable than cross-process URI resolution.

### Risks & Mitigation
- **Stale URIs**: Addressed by immediate copying upon import.
- **Storage Bloat**: High-quality audio files can consume significant internal storage. 
  - *Mitigation Strategy*: Implement a cleanup utility and eventually provide an "Import and Delete Original" option.
- **Migration Path**: Existing Iteration 1 URIs must be resolved (while permissions still hold) and copied to local storage during the first launch of Iteration 2.


