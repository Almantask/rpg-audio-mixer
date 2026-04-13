# Arcanum Audio 🎲

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Almantask_rpg-audio-mixer&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Almantask_rpg-audio-mixer)
[![Unit Test Coverage](https://sonarcloud.io/api/project_badges/measure?project=Almantask_rpg-audio-mixer&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Almantask_rpg-audio-mixer)
![Mutation Score](https://img.shields.io/badge/mutations-3%25%20%2869%2F2247%29-green)
[![Acceptance Tests](https://img.shields.io/badge/Acceptance%20Tests-12%20passed-success)](app/src/androidTest/assets/features/)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Almantask_rpg-audio-mixer&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Almantask_rpg-audio-mixer)
[![Current Iteration](https://img.shields.io/badge/Iteration-1%20Completed-blue)](plans/plan.md)
[![Views](https://komarev.com/ghpvc/?username=Almantask&repo=rpg-audio-mixer&color=green&style=flat-square)](https://github.com/Almantask/rpg-audio-mixer)

**Arcanum Audio** is a premium RPG ambience and sound-mixing application designed specifically for Dungeon Masters and tabletop storytellers. It enables GMs to bring their worlds to life through a sophisticated audio engine capable of layering loopable soundscapes with low-latency soundboard effects.

## Core Features
- **Layered Soundscapes:** Compose complex atmospheres by mixing multiple loopable audio categories (Weather, Interior, Monsters, etc.).
- **Dynamic Intensity:** Seamlessly transition between three levels of intensity (I, II, III) for any soundscape category with automatic 2-second crossfades.
- **Low-Latency Soundboard:** Trigger one-shot sound effects (FX) that overlap perfectly with background ambience.
- **Smart Mixing:** Real-time control over Master Atmosphere and per-category MIX volumes using a cubic power curve for natural-sounding fades.
- **Campaign Management:** Organize your sessions into Campaigns and Sessions with full support for soft-deletion and recovery via the "Vault of Echoes."
- **Home Dashboard:** Quickly resume your last session or scene with one tap from a personalized landing page.

## Technical Foundation
- **Modern Android Stack:** Built with 100% Kotlin and Jetpack Compose.
- **Audio Engine:** Powered by Media3 (ExoPlayer) for soundscapes and SoundPool for high-performance FX triggers.
- **Architecture:** Clean Architecture with MVVM, Hilt for Dependency Injection, and Coroutines/Flow for reactive state.
- **Persistence:** Local storage managed by Room with automated cleanup and soft-delete recovery logic.
- **QA Rigor:** Comprehensive BDD coverage using Cucumber and Espresso, verified on real audio hardware in CI.

---

## Contributing
Curious how to contribute? Refer to the [implementation plan](plans/plan.md) - the next non-completed iteration is the best place to start.

---

## Developer Guide

The Arcanum Audio repository uses an AI-augmented team of specialized "Agents" (or Skills) to enforce architecture, quality, and Behavior-Driven Development (BDD). Whether you are developing manually or orchestrating the AI Agents, here is how to work with the project.

### Running Locally

Requirements: 
- JDK 17
- Android Studio Ladybug or later
- KVM / Hardware acceleration enabled (for local emulator testing)

```bash
# Build the application
./gradlew assembleDebug

# Run static analysis (Detekt)
./gradlew detekt

# Run all Unit Tests with Coverage
./gradlew testDebugUnitTest createDebugUnitTestCoverageReport
```

### Testing (BDD Automation)

We use Cucumber for Android to power our acceptance tests. The tests run against the real production stack (Room, Hilt, Compose) with only external dependencies faked.

```bash
# Run all Acceptance Tests
./gradlew connectedDebugAndroidTest

# Run a specific Acceptance Test (Feature)
# Replace [feature_name] with a file from app/src/androidTest/assets/features/
./gradlew connectedDebugAndroidTest -PcucumberFeatures="features/[feature_name].feature"
```
**Note:** Make sure an Android Emulator is running and unlocked before executing connected tests.

### Agentic Development Workflow

Arcanum uses a 4-step orchestration process implemented by distinct AI "personas." You can utilize these personas via either **GitHub Copilot CLI/Agents** or the **Gemini Assistant**.

The available roles are located in:
- `.github/agents/` (for GitHub Copilot)
- `.agents/skills/` (for Gemini Assistant)

#### Available Agents

1. **`product-owner`**: Gatekeeper of User Experience. Defines Acceptance Criteria (AC) and feature boundaries.
2. **`product-designer`**: Shapes UX flows, material components, and screen layouts.
3. **`android-developer`**: Implements production code via strict **Red → Green → Refactor** TDD.
4. **`qa-tester`**: Generates Gherkin `.feature` specs and implement Compose/Espresso steps.
5. **`android-code-reviewer`**: Audits production architecture, performance, and Android deprecations.
6. **`qa-code-reviewer`**: Audits test architecture, BDD semantics, and coverage gaps.
7. **`audio-specialist`**: Dedicated engineer for ExoPlayer/SoundPool engine tuning.
8. **`devops-engineer`**: Owns Gradle config, CI/CD, and release engineering.

#### Orchestrating via Gemini (IDE Extension)

**The Preferred Way (Automated Workflow)**
Do not manually direct agents unless absolutely necessary. Instead, use the built-in orchestration workflow by utilizing the Gemini CLI slash command. This will automatically execute the 5-phase sequence: Implementation → Validation → Review Council → Fixes → Historian.

> `/feature-delivery Implement the new Master Volume slider defined in our project plan`

- Phase 1 & 2: `@qa-tester` `@android-developer` implement the feature and update tests.
- Phase 3: `@android-code-reviewer` `@qa-code-reviewer` `@audio-specialist` `@product-owner` review the code and provide final sign-off.
- Phase 4: `@qa-tester` `@android-developer` fix issues found in phase 3.
- Phase 5: `@project-historian` update the project learnings if any.

---

## Assets & Branding (Not Open Source)

The Apache License 2.0 applies **only** to the source code contained in this repository.

The following are **not** licensed under Apache 2.0 and are **not open source**:

- Audio files and sound packs
- Preset mixes and curated content
- Icons, logos, artwork, and UI graphics
- Application name, branding, and store listing assets

These materials are proprietary and may not be redistributed, resold, or included in derivative works without explicit permission. Forks and derivative works must supply their own audio assets and branding.
