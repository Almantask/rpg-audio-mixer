## Iteration 5 — Trash Screen & Credits Integration (Simple)

### Relies on
- `isDeleted` flag (Iteration 2)

### Goal
Implement the "Vault of Echoes" for restoring deleted items, and wire it to Credits.

### Build
- **Trash Screen**: List of items with `isDeleted = true` (Campaigns, Sessions, Scenes, Categories, FX), Restore button (gold), Permanent Delete button (red), Empty Vault button. Footer about 7-day auto-purge.
- **Credits Integration**: Ensure Credits screen (from Iteration 0) has the "VAULT OF ECHOES" button to navigate to Trash.

### Linked Features
- `app/src/androidTest/assets/features/trash_recovery.feature`
- `app/src/androidTest/assets/features/view_credits.feature` (scenarios tagged `@iter5`: VAULT OF ECHOES button & navigation)

### Linked Designs
- `docs/designs/trash-design.md`
- `docs/designs/Trash.html`
- `docs/designs/credits-design.md`
- `docs/designs/Credits.html`

### Android & Testing Implementation Details
- **Android**: Cross-entity querying logic in `VaultRepository` (resolving `isDeleted = 1`). Enqueue `WorkManager` jobs tracking timestamps for the 7-day automated database purge background tasks.
- **Testing**: Dao tests verifying `isDeleted` flip capabilities. UI instrumentation simulating item restoration in lists.

