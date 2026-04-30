## Iteration 5 — Home Screen Dashboard (Simple)

### Relies on
- All data entities (Iteration 2, 3, & 4)

### Goal
Build the Home dashboard using existing data, preparing for Playback Statistics.

### Build
- **Home Screen**: Resume card (last scene), Campaign hero card (last campaign), and basic stats (Top Atmosphere, Legendary Action - relying on play count metrics).
- *Ambiguity/Contradiction Highlight: Previous plan had Home Screen late (Iteration 10) because it relied on play count stats. Implementing it early here (Iteration 5) means stats will be static/mocked until Playback Statistics (Iteration 6) is fully implemented.*

### Linked Features
- `app/src/androidTest/assets/features/home_screen.feature`

### Linked Designs
- `docs/designs/home-design.md`
- `docs/designs/Home.html`
- `docs/design-overall.md` §4.1

### Android & Testing Implementation Details
- **Android**: Aggregate dashboard metrics using explicit Room SQL queries (e.g., `ORDER BY lastPlayedAt DESC LIMIT 1`). Utilize Compose `Card` and column structures for the widget layouts.
- **Testing**: Dashboard test mocking database repository layers. Espresso assertions ensuring mock widget content correctly renders conditionally.

