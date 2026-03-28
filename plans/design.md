# Design Clarifications Checklist

A screen-by-screen and cross-cutting list of open questions to resolve before implementation.

---

## Navigation & Information Architecture

- [x] Bottom nav labels are inconsistent across screens (SESSIONS / CAMPAIGNS, TRACKS / LIBRARY, SCENES / ATMOSPHERES) — **Resolved:** 4 tabs: 🏰 HOME (castle icon), 📖 CAMPAIGNS (storybook icon), 🖼 SCENES (picture frame icon), 🎵 LIBRARY (music note icon)
- [x] Confirm the top-level navigation hierarchy — **Resolved:** Home → Campaigns → Sessions → Scenes → Active Scene
- [x] Clarify whether the global SCENES tab shows all scenes flat, or only scenes under the current session/campaign — **Resolved:** navigation is hierarchical (Home → Campaigns → Sessions → Scenes); scene transitions:
  - Scenes list / Session Scenes: tapping the card opens the Active Scene without starting playback; tapping ▶ opens AND starts playing (fresh start, ~2–3 s fade-in)
  - Home "Resume Journey" ENTER button: always fresh start with ~2–3 s fade-in
  - Home "ENTER DOMAIN" on active campaign: navigates to that campaign's Sessions list
  - Campaigns screen RESUME button: same as ENTER DOMAIN — navigates to Sessions list
  - Switching scenes: back to Scenes list → pick new scene → ~2–3 s crossfade between old and new
- [x] Confirm whether the back arrow in the top bar always goes to the previous screen, or sometimes to a specific parent — **Resolved:** back always goes to previous screen; switching scenes goes back → pick new scene → old scene crossfades out while new scene crossfades in simultaneously over ~2–3 seconds

---

## Data Model

- [x] Define the relationship between Campaigns, Sessions, and Scenes — can a Scene belong to multiple Sessions? — **Resolved:** Scenes are shared — the same Scene can be added to multiple Sessions; editing it updates it everywhere
- [x] Clarify whether Soundscape Categories (Weather, Interior, Monsters…) are system-defined, user-defined, or both — **Resolved:** fully user-defined; categories are created, named and managed through the Soundscape Category Composer
- [x] Define what a "Soundscape" is vs a "Track" vs a "Layer" — the naming varies across screens — **Resolved:** Layer and Soundscape are the same thing (a named composition of audio files); Category is a named group (e.g. Weather) that holds one active Soundscape at a time; Track is a general term for any single playable audio file
- [x] Clarify what "Progress: 65%" means on the Home Resume Journey card — **Resolved:** design mistake, remove it
- [x] Define what the 3 intensity levels (Level I / II / III) represent semantically — **Resolved:** they represent dramatic stakes (Level I = calm, Level III = tense/climactic); the DM switches between levels manually — no automatic triggering

---

## Home Screen

- [x] What determines which campaign appears as "Active" in the hero card? — **Resolved:** always the most recently played campaign, automatic — no manual control
- [x] What triggers the "Resume Journey" section — last scene played, or something else? — **Resolved:** the last scene opened in the currently active campaign
- [x] What do "Top Atmosphere" and "Legendary Action" sections show — most played globally, or within the active campaign? — **Resolved:** global all-time most played — Top Atmosphere = most played loopable track, Legendary Action = most played FX

---

## Campaigns & Sessions

- [x] What happens when there are no campaigns — empty state? — **Resolved:** illustration + "Scribe New Tale" prompt button, no full onboarding flow
- [x] How is cover art for a Campaign / Session set (AI-generated, user-uploaded, picked from library)? — **Resolved:** user picks an image from their phone's photo library
- [x] What does "FILTER" do on the Sessions screen — filter by what? — **Resolved:** remove the FILTER button; sessions are auto-sorted by date, most recent at the top

---

## Scenes List

- [x] Confirm where scenes live in the hierarchy — are they global or scoped to a session? — **Resolved:** Scenes are global; the SCENES tab shows all scenes ever created in one flat list
- [ ] What does the play button (▶) on a scene card do — starts playback immediately with defaults?
- [x] How are scene tags (COMBAT, INTENSE) assigned — user-defined, from a fixed list, or inferred? — **Resolved:** fixed list to pick from, plus option to add custom tags

---

## Active Scene — Soundscapes Tab

- [x] What does the refresh (↺) button on a soundscape category do — pick a random track, or cycle through? — **Resolved:** replace the ↺ icon with a d20 die icon; tapping it picks a random track from that category's composition and starts playing it
- [x] Can soundscape categories be reordered within a scene? — **Resolved:** yes, drag to reorder
- [x] How do Master Atmosphere and the per-category MIX slider interact (multiplicative, independent, capped)? — **Resolved:** multiplicative — final volume = Master × MIX (e.g. Master 50% × MIX 80% = 40% actual volume)
- [x] "ADD NEW SOUNDSCAPE" — does this add a new category or a new track inside an existing category? — **Resolved:** adds a new category to the scene via the simplified Soundscape Categories selection view

---

## Active Scene — Soundboard Tab

- [x] Can effect buttons be reordered or grouped? — **Resolved:** yes, drag to reorder; no grouping
- [x] Is there a per-effect volume control, or only the master slider? — **Resolved:** master slider only, no per-effect volume control
- [x] What happens if the same effect button is tapped while it is already playing (re-trigger, ignore, overlap)? — **Resolved:** re-trigger — a new instance starts from the beginning, overlapping with any already-playing instances

---

## Audio Library — Soundscapes

- [x] What is "The Archivist's Choice" — a curated preset section, user favourites, or something else? — **Resolved:** design mistake, remove it
- [x] The Soundscape Category Composer saves a "composition" — where is it stored (globally, per-scene, per-category)? — **Resolved:** saved globally to the category — updates everywhere that category is used
- [x] How is a layer added via "INVOKE NEW LAYER" — from the library, or by recording? — **Resolved:** opens the device's native file picker to select a local audio file
- [x] Is there a maximum number of layers per category composition? — **Resolved:** no limit
- [x] Navigation to this screen: reachable from LIBRARY tab; "ADD NEW SOUNDSCAPE" on Active Scene opens a **simplified Soundscape Categories selection view** (not the full Library) with a back button and multi-selection support — user picks one or more categories to add to the scene
- [x] Tapping the ✏️ edit icon on a category opens the **Soundscape Category Composer** for that category

---

## Audio Library — Sound Effects (FX)

- [x] What file formats are supported for "Import FX"?  — **Resolved:** "IMPORT FX" on the Library screen opens the device's native file picker; imported track is added to the global FX library
- [x] Does the mini player at the bottom persist while navigating between screens? — **Resolved:** Library screen only — navigating away stops playback and hides the mini player
- [x] What does the ⋮ (three-dot) menu on a track expose — add to scene, delete, rename, etc.? — **Resolved:** replace ⋮ with a ✏️ pencil icon; opens a track edit screen with: Name, Tags, Delete
- [x] Heart/favourite — does this affect any ranking or home screen recommendations? — **Resolved:** design mistake, remove the heart icon
- [x] Navigation to this screen: reachable from LIBRARY tab (Sound Effects tab); "ADD NEW EFFECT" on Active Scene Soundboard opens a **simplified FX selection view** (not the full Library) with a back button and multi-selection support — user picks one or more effects to add to the Soundboard

---

## Monetisation / Store — OUT OF SCOPE

- [x] There is no store / catalogue screen in the designs — is one needed for "Buy More" and "Buy Sounds"? — **Resolved:** out of scope, remove all "Buy More" / "Buy Sounds" buttons
- [x] Is purchasing handled in-app (Google Play Billing) or via an external web flow? — **Resolved:** out of scope

---

## Animation

- [x] Define the screen transition style — slide, fade, or shared element transitions between screens — **Resolved:** "The Breath" — subtle scale + fade, feels premium and organic:
  - **Forward (A → B):** A fades out + scales up (100% → 102%); B fades in + scales up (98% → 100%)
  - **Back (B → A):** B fades out + scales down (100% → 98%); A fades in + scales down (102% → 100%)
  - Speed: fast; feel: consistent across all screen transitions
- [x] Confirm whether scene cards use a shared element hero transition into the Active Scene screen — **Resolved:** no hero transition; The Breath handles all screen transitions uniformly
- [x] Specify how the Master / MIX sliders animate when their value changes programmatically (e.g. smooth tween vs instant snap) — **Resolved:** instant snap, no animation
- [x] Clarify what the play button (▶) on scene and track cards does visually on tap — ripple, scale, state transition to a stop icon? — **Resolved:** button glows/pulses while playing and switches to ⏸ (pause); tapping ⏸ stops playback and reverts to ▶
- [x] Define how soundscape category cards animate when playback starts/stops (e.g. waveform pulse, glow, progress bar fill) — **Resolved:** coloured glow / highlight border around the card while playing; removed when stopped
- [x] Specify the loading / skeleton state animation while audio assets are fetching — **Resolved:** simple spinner/loading indicator centred on screen
- [x] Confirm whether the bottom mini player (FX screen) has an entrance/exit animation when a track is first selected — **Resolved:** The Breath — scales + fades in (98% → 100%) on appearance; reverses on dismissal

---

## Missing / Undesigned Screens

- [x] Settings screen (gear icon is present on every screen but no design exists) — **Resolved:** the ⚙️ gear icon navigates to the Credits screen; no separate Settings screen needed
- [ ] Onboarding / profile setup flow
- [ ] Empty states for all major lists (no campaigns, no scenes, no tracks)
- [ ] Error states (network failure, audio load failure)
- [ ] Search results screen / state (search bar appears in several screens)
