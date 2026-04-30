## Iteration 0 — Design System & App Shell (Foundation + Simple)

### Relies on
- Empty scaffold with placeholder bottom nav and nav host (already exists)
- Default Material theme (already exists — needs replacing)

### Goal
Establish the app's visual identity, navigation shell, and resource infrastructure. Build the simplest screens first.

### Build
**1. Resource Infrastructure Fix**
- Update `LocalTrackRepository` to resolve `file:///android_asset/audio/` URIs.

**2. Theme & Design Tokens** (`app/theme/`)
- `Color.kt` — black backgrounds, gold/amber (`#F2CA50`), purple/pink accents, surface/card tones, error reds (`#FFB4AB`)
- `Type.kt` — Newsreader (serif display) + Manrope (body), gold heading style
- `Theme.kt` — dark-only `MaterialTheme`, no dynamic color, custom `ColorScheme`
- `Shape.kt` — rounded corner tokens for cards, buttons *(Restored from previous plan)*
- **Motion Tokens**: Define standard durations and easings for the "Arcanum Motion System" (Container Transform, Shared X-Axis).

**3. Arcanum Components**
- **`ArcanumEmptyState`**: Large gold Material 3 icon + title + CTA button (per Design Spec Section 8).
- **`ArcanumTopBar`**: Reusable top bar with gold title and ⚙️ gear icon.
- **`MainBottomNavBar`**: 4 tabs (HOME, CAMPAIGNS, SCENES, LIBRARY) with gold selected state.
- **Error Overlay**: reusable `ErrorDialog` composable *(Restored from previous plan)*

**4. Screens (Simple)**
- **Credits Screen**: Static list of developer credits and links (reached via ⚙️). Base view, "Vault of Echoes" button will be added in Iteration 8.

**5. Navigation & DI Foundation**
- Update `MainNavDestination` enum: `HOME, CAMPAIGNS, SCENES, LIBRARY`.
- Wrap `MainNavHost` in `SharedTransitionLayout` (Compose 1.7+) for future motion support.
- Root `AppModule` providing `ApplicationContext`.

### Linked Features
- `app/src/androidTest/assets/features/can_launch.feature`
- `app/src/androidTest/assets/features/bottom_navigation.feature`
- `app/src/androidTest/assets/features/view_credits.feature` (scenarios tagged `@iter0`)

### Linked Designs
- `docs/designs/home-design.md`
- `docs/designs/Home.html`
- `docs/designs/credits-design.md`
- `docs/designs/Credits.html`
- `docs/design-overall.md` §1 (Branding), §2 (Navigation), §6 (Animation), §9 (Error Handling)

### Android & Testing Implementation Details
- **Android**: Setup Material 3 Compose theme overrides in `Theme.kt`, `Color.kt`, `Type.kt`. Implement `NavHost` with `bottomBar`. Implement `ArcanumEmptyState` and `ArcanumTopBar` components.
- **Testing**: Espresso UI tests asserting bottom navigation visibility. UI test asserting the structural presence of the empty Credits screen. Basic CI validation runs for ensuring `can_launch` passes on a fresh install.
