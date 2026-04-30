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

### Linked Features
- `app/src/androidTest/assets/features/view_created_scenes.feature`
- `app/src/androidTest/assets/features/delete_scene.feature`
- `app/src/androidTest/assets/features/session_scenes.feature`
- `app/src/androidTest/assets/features/build_your_own_scene.feature`
- `app/src/androidTest/assets/features/manage_soundscape_categories.feature`
- `app/src/androidTest/assets/features/compose_soundscape.feature`
- `app/src/androidTest/assets/features/cannot_modify_bought_scenes.feature`
- `app/src/androidTest/assets/features/tag_scene.feature`
- `app/src/androidTest/assets/features/add_description_to_scene.feature`

### Linked Designs
- `docs/designs/scenes-list-design.md`
- `docs/designs/ScenesList.html`
- `docs/designs/session-scenes-design.md`
- `docs/designs/SessionScenes.html`
- `docs/designs/audio-library-soundscapes-design.md`
- `docs/designs/AudioLibrary-Soundscape-Categories.html`
- `docs/designs/soundscape-category-composer-design.md`
- `docs/designs/Soundscape-Category-Composer.html`
- `docs/design-overall.md` §4.3–4.5, §4.9–4.10

### Android & Testing Implementation Details
- **Android**: Implement Room junction tables (`SessionSceneCrossRef`) for many-to-many relationships. Use `LazyVerticalGrid` to build Bento grid UIs for the Composer and Library tabs. State hoisting to track unsaved composer changes.
- **Testing**: Espresso UI tests targeting composer lists, asserting unlinked scenes do not show in Session views, and swipe-to-delete integration.

