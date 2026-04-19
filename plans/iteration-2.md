## Iteration 2 — Persistence & Campaign CRUD (Foundation)

### Relies on
- Library UI & Audio Engine (Iteration 1)

### Goal
Stand up the Room database with support for Campaigns, Sessions, and soft-deletion.

### Build
**1. Room Database Setup** (`data/local/`)
- `AppDatabase.kt` — Room DB version 1.
- `AudioTrackEntity`, `CampaignEntity`, `SessionEntity` — include **`isDeleted`** flag.
  - *Details from previous plan:* `CampaignEntity` (`id`, `name`, `coverArtUri`, `lastPlayedAt`), `SessionEntity` (`id`, `campaignId`, `name`, `date`, `coverArtUri`).
- DAOs: `AudioTrackDao`, `CampaignDao`, `SessionDao`.

**2. Migration to Persistence**
- **Copy to Internal Storage**: Implement `FileStorageManager` to copy selected URIs to `filesDir/audio/`.
- Load tracks from `AudioTrackDao` instead of in-memory list.

**3. Campaigns & Sessions UI**
- **Campaigns Screen**: List of campaign cards, + NEW CAMPAIGN, **Swipe-to-Delete**. Photo picker for cover art via `rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia())` *(Restored from previous plan)*.
- **Sessions Screen**: List of sessions for a campaign, + NEW SESSION, **Swipe-to-Delete**.

### Docs to reference *(Restored from previous plan)*
- `docs/designs/campaigns-design.md`, `docs/designs/campaign-sessions-design.md`
- `docs/design-overall.md` §4.2, §8

