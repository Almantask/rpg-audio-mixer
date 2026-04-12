# Arcanum Audio 🎲

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Almantask_rpg-audio-mixer&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Almantask_rpg-audio-mixer)
[![Unit Test Coverage](https://sonarcloud.io/api/project_badges/measure?project=Almantask_rpg-audio-mixer&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Almantask_rpg-audio-mixer)
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

## Assets & Branding (Not Open Source)

The Apache License 2.0 applies **only** to the source code contained in this repository.

The following are **not** licensed under Apache 2.0 and are **not open source**:

- Audio files and sound packs
- Preset mixes and curated content
- Icons, logos, artwork, and UI graphics
- Application name, branding, and store listing assets

These materials are proprietary and may not be redistributed, resold, or included in derivative works without explicit permission. Forks and derivative works must supply their own audio assets and branding.
