## Iteration 9 — Arcanum Motion System & Polish (Complexity)

### Relies on
- All previous iterations
- SharedTransition Scaffolding (Iteration 0)

### Goal
Final polish, high-fidelity animations, scene switching crossfades, and edge cases.

### Build
- **Shared Transitions**: Implement Container Transform (Card → Detail) and Shared X-Axis (Tab switching) using `SharedTransitionLayout`.
- **Scene Switching**: Crossfade logic in `SceneAudioEngine` (fade out current over 2-3s while fading in new). ▶ button on scene cards autoplays.
- **Final UI Polish & Edge Cases** *(Restored from previous plan Iteration 12)*:
  - Empty state illustrations (scroll, parchment, map, wand, crystal ball, silent room).
  - All-intensities-empty category handling (disable play/dice, grey out intensities).
  - Loading states (centred spinner).
  - Predefined tag system (Tavern, Forest, Combat, etc.) + custom tags.
  - Verify ExoPlayer cleanup and no audio leaks.
  - Accessibility content descriptions and contrast review.
  - R8/Minification verification.

### Linked Features
- `app/src/androidTest/assets/features/screen_transitions.feature`
- `app/src/androidTest/assets/features/play_scene.feature`
- `app/src/androidTest/assets/features/session_scenes.feature`
- `app/src/androidTest/assets/features/preview_fx_track.feature`
- `app/src/androidTest/assets/features/tag_scene.feature`

### Linked Designs
- `docs/design-overall.md` §6, §8, §9
- `docs/designs/home-design.md`
- `docs/designs/Home.html`
- `docs/designs/campaigns-design.md`
- `docs/designs/Campaigns.html`
- `docs/designs/campaign-sessions-design.md`
- `docs/designs/CampaignSessions.html`
- `docs/designs/session-scenes-design.md`
- `docs/designs/SessionScenes.html`
- `docs/designs/scenes-list-design.md`
- `docs/designs/ScenesList.html`
- `docs/designs/credits-design.md`
- `docs/designs/Credits.html`
- `docs/designs/audio-library-fx-design.md`
- `docs/designs/AudioLibrary-FX.html`
- `docs/designs/active-scene-soundscapes-design.md`
- `docs/designs/ActiveScene-Soundscapes.html`

### Android & Testing Implementation Details
- **Android**: Compose 1.7+ `SharedTransitionLayout`. Target Container Transform behaviors migrating from list items (`Card`) to detail pages. Cross-fade coroutine interpolation built within `SceneAudioEngine` scaling down target players while scaling up outgoing players over ~2500ms.
- **Testing**: Espresso NavHost destination testing. UI component test ensuring no memory leaks in `ExoPlayer` post-transition.

