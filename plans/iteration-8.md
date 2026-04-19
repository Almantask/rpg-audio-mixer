## Iteration 8 — Arcanum Motion System & Polish (Complexity)

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

### Docs to reference
- `docs/design-overall.md` §6, §8, §9

