# Implementation Plan (TDD, selected design subset)

## Goal
Implement full app behavior from design spec with a working Compose MVP and unit tests for core flows. Skip all Cucumber/BDD acceptance tests (keep existing scaffold but do not add new). Use TDD loops for each feature group.

## Checklist
1. Data model and repositories
   - [x] Campaign, Session, Scene, SoundscapeCategory, SoundEffect domain models
   - [x] In-memory repository implementations and interfaces
   - [x] Basic persistence contract stubs (not production, in-memory)
2. Navigation and global structure
   - [x] MainNavHost with tabs: HOME, CAMPAIGNS, SCENES, SOUNDSCAPES
   - [x] Gear icon to Credits screen from all pages
   - [x] Back arrow behaves as previous screen
3. Home screen
   - [x] Active campaign hero card with ENTER DOMAIN behavior
   - [x] Resume Journey card with last scene and ENTER behavior
   - [x] Top Atmosphere and Legendary Action from play stats
4. Campaigns screen
   - [x] Sorted list by most recent play
   - [x] RESUME buttons on campaign cards
   - [x] Empty state illustration mimic & “Scribe New Tale” action
   - [x] Cover art picker placeholder (UI only)
5. Sessions screen
   - [x] Sorted by date desc, ADD NEW SESSION button
   - [x] No FILTER button
6. Scenes screen (global)
   - [x] Scene list with open/tap and play actions
   - [x] Tags ability TODO (baseline: display + add custom)
   - [x] Add New Scene button and empty state
7. Active Scene (soundscapes + soundboard)
   - [x] Master Atmosphere slider and per-category mix sliders
   - [x] d20 random track button + play/pause state
   - [x] Category reorder (drag handles) placeholder UI
   - [x] Add new soundscape category flow (selection screen)
   - [x] Soundboard tab: master slider, 4-column grid, effect buttons re-trigger
   - [x] Add new effect flow
8. Audio library
   - [x] Soundscapes tab: lists categories + intensity counts + edit icon
   - [x] Soundscape Composer: layers + sliders + native file picker stub + save
   - [x] FX tab: import button stub + edit pencil UI + mini player bottom bar
9. Credits screen
   - [x] Info: version, support link, docs, Discord, email
10. UI polish
    - [x] Dark theme + brand coloring
    - [x] Animation placeholders (The Breath cards)
    - [x] Volume sliders instant snap and no animation

## TDD process
- For each feature, implement 1 unit test first (`app/src/test/java/...`) then app code.
- Rerun tests and ensure pass.
- Update this checklist with [x] when done.
