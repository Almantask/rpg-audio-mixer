## Iteration 3 — Scenes & Soundscape Categories (Foundation)

### Relies on
- Room DB, Design system
- Sessions (Iteration 2)

### Goal
Implement Scene management and link global scenes to sessions. Restore full Soundscape Category & Composer fidelity.

### Build
**1. Entities & DAOs**
- `SceneEntity` (with `description`, `tags`), `SoundscapeCategoryEntity`.
- **`SessionSceneCrossRef`**: Many-to-many relationship linking Sessions to Scenes.
- DAOs: `SceneDao`, `SessionSceneDao`, `SoundscapeCategoryDao`.

**2. Scenes UI**
- **Scenes List Screen**: Global scene cards, + NEW SCENE, swipe-delete. Tags as chips.
- **Session Scenes Screen**: List of global scenes linked to a specific session, swipe-to-unlink, + IMPORT SCENE (multi-select picker).

**3. Soundscape Category Composer** *(Restored from previous plan)*
- **Library — Soundscapes Tab**: Bento grid of category cards showing track counts per level. ✏️ edit → Composer, + NEW COMPOSITION.
- **Composer Screen**: List of soundscape cards (name, intensity picker, MIX slider), + INVOKE NEW SOUNDSCAPE (audio file picker), SAVE COMPOSITION, swipe-delete tracks, unsaved-changes dialog.

### Docs to reference *(Restored from previous plan)*
- `docs/designs/scenes-list-design.md`, `docs/designs/session-scenes-design.md`
- `docs/designs/audio-library-soundscapes-design.md`, `docs/designs/soundscape-category-composer-design.md`
- `docs/design-overall.md` §4.3–4.5, §4.9–4.10

