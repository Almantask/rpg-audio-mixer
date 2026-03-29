# RPG Audio Mixer — Implementation Plan (TDD, No BDD)

## Scope
Implement all features and screens described in the design specs, following strict TDD (unit tests only, no acceptance/feature tests). Prioritize user experience and maintainability. Adjust requirements if contradictions or impracticalities arise.

---

## Checklist

- [ ] 1. **Project Structure & Navigation**
    - Set up base navigation (bottom nav: Home, Campaigns, Scenes, Library)
    - Create navigation graph and screen stubs

- [ ] 2. **Home Screen**
    - Display active campaign summary
    - Show last scene and top atmosphere
    - Quick entry points to campaign/scene

- [ ] 3. **Campaigns**
    - List all campaigns (cover art, name, last played)
    - Add new campaign
    - Resume campaign

- [ ] 4. **Campaign Sessions**
    - List sessions for a campaign
    - Add new session
    - Show session details

- [ ] 5. **Session Scenes**
    - List scenes linked to a session
    - Import scene to session
    - Show scene details

- [ ] 6. **Scenes List (Global)**
    - List all scenes
    - Add new scene
    - Edit scene (updates globally)

- [ ] 7. **Audio Library**
    - Tabbed: Soundscapes / Sound Effects
    - List, add, edit, delete soundscapes and FX
    - Import audio files
    - Mini player for FX

- [ ] 8. **Soundscape Category Composer**
    - Edit soundscape categories (layers, intensity levels)
    - Add/remove layers, import audio
    - Save composition (applies globally)

- [ ] 9. **Credits / Settings**
    - Show app info, version, credits, links
    - Access from gear icon on any screen

- [ ] 10. **Core Data Models & Persistence**
    - Define Room entities, DAOs, repositories for Campaign, Session, Scene, SoundscapeCategory, FX
    - Implement ViewModels and state flows

- [ ] 11. **Unit Tests (TDD)**
    - Write failing unit tests for all business logic and ViewModels
    - Use AssertJ, MockK, Turbine as appropriate

- [ ] 12. **UI Implementation**
    - Build Compose screens for all features
    - Use Material 3 components per design
    - Ensure accessibility and usability

- [ ] 13. **Polish & Edge Cases**
    - Handle empty, loading, error states (done)
    - Validate input, handle file import errors (done)
    - Ensure offline support for audio

---

## Notes
- **No BDD/acceptance tests:** Skip all feature/gherkin/Cucumber tests.
- **TDD only:** Every production class/function must be driven by a failing unit test.
- **Adjust designs as needed** for best UX and technical feasibility.
- **Stop only when all checklist items are implemented and tested.**
