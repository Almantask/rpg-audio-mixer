# Iteration Plans Review Report

Date: 2026-04-30

## Scope

- Audited plans: current iteration plans under `plans/`, numbered sequentially from Iteration 0 through Iteration 11
- Audited feature files: 40 files under `app/src/androidTest/assets/features/`
- Audited design specs: 13 markdown files under `docs/designs/` plus their paired HTML prototypes
- Supplemental context: `docs/design-overall.md`

## Executive Summary

- High-risk findings: 10
- Medium-risk findings: 6
- Weakly scoped or mis-tagged feature files: 8
- Orphan design specs: 0
- Iteration 11 is correctly gated as a placeholder and does not need correction

## Main Patterns

1. Multiple user-visible behaviors are assigned to more than one iteration, especially across Iterations 6, 7, 8, and 9.
2. Several plans link full design specs that describe the final UX, while the plan text claims an earlier simplified slice. That forces implementers to guess which parts are actually in scope.
3. A few large user-facing behaviors exist in plans without matching acceptance tests: Scene Notes, campaign export/import, limiter behavior, and FX jitter/randomization.
4. Several feature files mix scenarios from multiple iterations but use only a single feature-level `@iter*` tag, which makes iteration-based QA execution unreliable.

## Alignment Matrix

| Iteration | Status | Summary |
| --- | --- | --- |
| 0 | Partial | Core shell is clear, but linked Home design also contains Iteration 5 dashboard behavior. |
| 1 | Broken | Plan says simple library playback, linked designs describe a much richer library, and there is no Iteration 1 BDD coverage. |
| 2 | Mostly aligned | Campaign and session CRUD are covered, but delete/restore hierarchy semantics remain partial. |
| 3 | Partial | Scene CRUD and composer coverage are good, but linked designs also contain later behaviors like clone/play and unresolved routing notes. |
| 4 | Broken ownership | FX library scope is linked here, but the main feature files are tagged `@iter5` and one feature exceeds the linked design scope. |
| 5 | Contradictory | Plan says stats are mocked until Iteration 6, but features and design expect real metrics and real playback behavior. |
| 6 | Overloaded | Engine, playback, reorder, and UI interactions are mixed together and also reused by Iteration 7. |
| 7 | Partial | Active Scene UI aligns at a high level, but add-to-scene behavior and future controls are contradictory. |
| 8 | Mostly aligned | Trash and Credits fit, but unrelated legacy `@iter5` files still drift into this slot by stale tagging. |
| 9 | Weakly scoped | Motion work is real, but linked artifacts are not isolated to motion/polish outcomes. |
| 10 | Incomplete | Several major planned behaviors have no acceptance coverage and one live-session safety rule contradicts its tests. |
| 11 | Correctly gated | Placeholder state is explicit and consistent with repository memory guidance. |

## Per-Iteration Review

### Iteration 0

Status: Partial

- Linked features are reasonable for app shell work: `can_launch.feature`, `bottom_navigation.feature`, and the `@iter0` scenarios in `view_credits.feature`.
- Linked design pairs are complete for Credits, but the linked Home design is the full dashboard spec, not just the shell/nav slice.
- Resolution recorded 2026-04-30: Iteration 0 includes a placeholder Home surface, but no real dashboard widgets or metrics.
- Correction needed: scope the Home design reference to placeholder-shell concerns in Iteration 0 and keep dashboard-specific behavior in later iterations.

### Iteration 1

Status: Broken

- The plan says this iteration is a simple library list plus preview/import using the real audio stack.
- The linked designs are the full Soundscapes and FX library designs, which include tabs, search, filters, mini-player, composer entry points, and richer editing flows.
- The plan explicitly says there are no `@iter1` feature files, so this foundational slice has no iteration-owned acceptance contract.
- Resolution recorded 2026-04-30: Iteration 1 begins the broader multi-tab library experience rather than a minimal preview-only slice.
- Correction needed: revise the plan and add Iteration 1 acceptance coverage for the broader library scope, or explicitly re-stage which parts of the full library experience still wait for later iterations.

### Iteration 2

Status: Mostly aligned

- Campaign and session CRUD are covered by `campaign_crud.feature`, `manage_campaigns.feature`, and `manage_sessions.feature`.
- Linked design pairs for Campaigns and Campaign Sessions are appropriate.
- Resolution recorded 2026-04-30: restoring a deleted campaign restores its valid child entities and valid Home or resume references automatically.
- Follow-up: document the full hierarchy recovery model earlier, even if the Trash UI still ships in Iteration 8.

### Iteration 3

Status: Partial

- Core scene creation, deletion, tagging, descriptions, session linking, and soundscape composition align well with the linked feature set.
- `manage_soundscape_categories.feature` and `compose_soundscape.feature` support the library/composer slice.
- Resolution recorded 2026-04-30: direct play from the Scenes list belongs to Iteration 7, while clone belongs to Iteration 9.
- Follow-up: keep both controls out of the Iteration 3 slice and scope `scenes-list-design.md` accordingly.
- Resolution recorded 2026-04-30: tapping a Soundscape Library category card body opens the composer.
- Follow-up: remove the routing TBD from `audio-library-soundscapes-design.md` so the design matches the feature contract.
- Resolution recorded 2026-04-30: Iteration 3 shows CRUD, edit, linking, and composition surfaces only; direct play and clone remain deferred.
- Correction needed: scope the design references to the Iteration 3 subset, and remove the unresolved routing note from the library design.

### Iteration 4

Status: Broken ownership

- The plan says this iteration owns the FX library, search, and mini-player.
- The three main linked features are `manage_fx_library.feature`, `search_sounds.feature`, and `preview_fx_track.feature`, but all three are tagged `@iter5` today.
- `search_sounds.feature` exceeds the linked FX design. The design supports FX-tab search/filter/sort, while the feature file describes cross-library filtering by category, type, intensity, and scene.
- Resolution recorded 2026-04-30: the core FX Library slice is owned by Iteration 4, not Iteration 5.
- Resolution recorded 2026-04-30: FX editing is exposed via a direct pencil icon.
- Follow-up: update the linked FX design so it no longer expects a three-dot overflow menu for edit.
- Follow-up: keep Iteration 4 focused on the core FX Library slice while moving broader cross-library discovery to a later iteration.
- Resolution recorded 2026-04-30: split `search_sounds.feature` so FX-only search stays in Iteration 4 and broader cross-library discovery moves to a later iteration.
- Correction needed: retag the FX-library slice to `@iter4`, split later-only discovery behavior out of `search_sounds.feature`, and resolve the FX edit affordance mismatch.

### Iteration 5

Status: Contradictory

- `home_screen.feature` and `home-design.md` describe final dashboard behavior: real active campaign, real last scene, global top metrics, and playback starting with cubic fade-in.
- The plan says the Home screen lands early and its stats are mocked until Iteration 6.
- That means the plan, design, and feature files do not describe the same acceptance target.
- Resolution recorded 2026-04-30: QA should validate placeholder cards with mocked values in Iteration 5.
- Resolution recorded 2026-04-30: Iteration 5 ships the Home dashboard shell with mocked placeholders; real metrics and playback-derived behavior move to Iteration 6.
- Correction needed: split Iteration 5 into a shell-only Home dashboard and move final metrics, playback history, and fade-driven behavior to Iteration 6 artifacts.

### Iteration 6

Status: Overloaded

- The plan is supposed to focus on advanced engine behavior, intensity support, playback statistics, and notification/background control.
- The linked feature set also includes reorder and direct UI behaviors that are later central to Iteration 7: `reorder_soundboard_effects.feature`, `reorder_soundscape_categories.feature`, and much of the visible Active Scene interaction model.
- Resolution recorded 2026-04-30: `system_audio_handling.feature` is primarily owned by Iteration 6.
- Follow-up: remove the defer-to-Iteration-9 ownership language and use later iterations only where they cross-reference already-owned behavior.
- Resolution recorded 2026-04-30: hard concurrency limits are engine guardrails, not primary user-facing product rules.
- Follow-up: if hitting a limit creates visible behavior, document the overflow behavior, but do not treat the numeric caps themselves as UX scope.
- Resolution recorded 2026-04-30: tapping pause on a soundboard effect stops all running instances of that effect.
- Follow-up: update the soundboard design wording so overlap-start and stop-all semantics are both explicit.
- Resolution recorded 2026-04-30: the first complete user-visible Active Scene behavior slice begins in Iteration 6.
- Follow-up: frame Iteration 7 as an expansion of the Active Scene surface rather than its first complete introduction.
- Resolution recorded 2026-04-30: `reorder_soundscape_categories.feature` is primarily owned by Iteration 7 as a user-visible Active Scene behavior.
- Correction needed: keep reorder ownership in Iteration 7, keep concurrency limits as implementation guardrails unless overflow behavior becomes user-visible, and make the `system_audio_handling` ownership explicit.

### Iteration 7

Status: Partial

- The high-level Active Scene UI scope is appropriate: assigned soundscapes and FX, live sliders, reorder, visual play state, and scene playback.
- The biggest blocker is the add-to-scene interaction model. `active-scene-soundscapes-design.md`, `active-scene-soundboard-design.md`, and `docs/design-overall.md` describe multi-select plus confirm behavior, while `add_soundscape_to_scene.feature`, `add_fx_to_soundboard.feature`, and `add-fx-or-soundscape-to-scene-design.md` describe immediate add with no confirm step.
- Resolution recorded 2026-04-30: split `play_scene.feature` by iteration ownership.
- Follow-up: keep basic scene open/play in Iteration 7, move scene-switch crossfade to Iteration 9, and move ducking behavior to Iteration 10.
- Resolution recorded 2026-04-30: Master Controls ship in Iteration 7.
- Follow-up: keep Session Lock and Scene Notes out of the Iteration 7 design scope, and hide any later-only controls entirely.
- Correction needed: decide the authoritative add-to-scene flow, split `play_scene.feature` by iteration ownership, and scope the Active Scene designs by iteration.

### Iteration 8

Status: Mostly aligned

- `trash_recovery.feature` and the `@iter8` Credits scenarios fit the Trash and Credits integration plan.
- Design pairs for Trash and Credits align to the intended screen work.
- The main issue is external noise: `manage_fx_library.feature`, `search_sounds.feature`, `preview_fx_track.feature`, and `ci_readiness.feature` still carry legacy `@iter5` tagging even though this slot now lives at Iteration 8 and does not own them.
- Follow-up: restore rules now assume full recovery of valid child entities and valid Home or resume shortcuts after campaign restoration.
- Correction needed: clean up the foreign legacy `@iter5` ownership and reflect the full-restore rule consistently.

### Iteration 9

Status: Weakly scoped

- `screen_transitions.feature` is the clearest Iteration 9 artifact and should remain the anchor for this iteration.
- The plan names Container Transform and Shared X-Axis, but the design/feature set also expects Shared Z-Axis and Shared Y-Axis behavior.
- The plan links `session_scenes.feature`, `preview_fx_track.feature`, and `tag_scene.feature`, but those files mainly test earlier core behavior rather than motion/polish outcomes.
- Resolution recorded 2026-04-30: Iteration 9 polish scope is limited to transitions and crossfades only.
- Resolution recorded 2026-04-30: transitions must become interactive within 300 ms.
- Correction needed: keep this iteration focused on motion/polish-specific scenarios and replace `within a short time` with the 300 ms responsiveness threshold.

### Iteration 10

Status: Incomplete

- `master_controls.feature`, `session_lock.feature`, and `scene_cloning.feature` match major parts of the plan.
- Resolution recorded 2026-04-30: remove Scene Notes from Iteration 10 for now.
- Resolution recorded 2026-04-30: remove campaign export/import from Iteration 10 for now.
- Resolution recorded 2026-04-30: remove limiter behavior from Iteration 10 for now.
- Resolution recorded 2026-04-30: remove FX jitter/randomization from Iteration 10 for now.
- Result: no remaining undocumented items stay in Iteration 10 without acceptance coverage.
- Resolution recorded 2026-04-30: remove Session Lock from product scope rather than trying to reconcile contradictory behavior.
- Separate requirement retained: OS-level lock-screen media controls, background playback, and phone-call interruption behavior still belong to `system_audio_handling.feature` and `docs/design-overall.md`, with primary ownership in Iteration 6.
- Resolution recorded 2026-04-30: external `Next` triggers d20 randomization only for the currently prominent category.
- Follow-up: align `play_random_track.feature`, `system_audio_handling.feature`, and the Active Scene design to that single rule.
- Decomposition recorded 2026-04-30: resolve Scene Notes, campaign export/import, limiter behavior, and FX jitter as separate scope decisions instead of one bundle.
- Follow-up challenge: the remaining work is cleanup, not product invention. Iteration 10 artifacts now need to match the retained scope only.
- Correction needed: remove Session Lock, Scene Notes, campaign export/import, limiter behavior, and FX jitter from Iteration 10 artifacts, and keep only the retained behaviors with matching acceptance coverage.

### Iteration 11

Status: Correctly gated

- The placeholder clearly states there are no linked features or designs yet.
- This matches repository memory guidance and should remain unchanged until the next feature bundle is authored.

## Orphan Inventory

### Weakly Scoped or Mis-Tagged Feature Files

- `app/src/androidTest/assets/features/manage_fx_library.feature`
  - Current tag: `@iter5`
   - Plan ownership: Iteration 4
   - Resolution recorded 2026-04-30: keep the direct pencil icon as the edit affordance.
    - Action: retag to Iteration 4 and align the linked design to the direct-edit control.

- `app/src/androidTest/assets/features/search_sounds.feature`
  - Current tag: `@iter5`
   - Plan ownership: Iteration 4
    - Resolution recorded 2026-04-30: split it so FX-only search remains in Iteration 4 and broader cross-library search moves later.
    - Action: retag the Iteration 4 slice and move broader discovery scenarios into a later-owned feature file or scenario set.

- `app/src/androidTest/assets/features/preview_fx_track.feature`
  - Current tag: `@iter5`
   - Plan ownership: Iteration 4
    - Resolution recorded 2026-04-30: split it so core preview behavior stays in Iteration 4 and animation polish moves to Iteration 9.
    - Action: isolate animation-specific scenarios into Iteration 9 and retag the core preview slice to Iteration 4.

- `app/src/androidTest/assets/features/reorder_soundscape_categories.feature`
  - No `@iter*` tag.
  - Referenced by Iterations 6 and 7.
   - Resolution recorded 2026-04-30: Iteration 7 is the single primary owner.
   - Action: add the Iteration 7 tag and remove competing ownership language from Iteration 6.

- `app/src/androidTest/assets/features/view_credits.feature`
   - Mixed scenario-level ownership across Iterations 0 and 8.
   - Resolution recorded 2026-04-30: keep a single file with scenario-level iteration tags.
   - Action: document that file-level filtering is unsafe and scenario-level filtering is required.

- `app/src/androidTest/assets/features/ci_readiness.feature`
  - Tagged `@iter5` but not linked from any iteration plan.
  - Scope overlaps Iteration 6 notification/background work and later system audio behavior.
   - Resolution recorded 2026-04-30: remove it from the iteration-owned feature inventory.
   - Action: drop iteration ownership and, if any checks are still needed, rewrite them outside the product iteration plan structure.

- `app/src/androidTest/assets/features/play_scene.feature`
  - Tagged only `@iter7`.
   - Resolution recorded 2026-04-30: split it by iteration.
   - Action: keep basic scene open/play in Iteration 7, move crossfade scenarios to Iteration 9, and move ducking scenarios to Iteration 10.

- `app/src/androidTest/assets/features/play_random_track.feature`
  - Tagged only `@iter7`.
   - Resolution recorded 2026-04-30: keep both local randomization and external `Next` behavior in Iteration 7.
   - Action: align the external-control scenario to the currently prominent category rule and remove duplicate ownership language elsewhere.

### Orphan Design Docs

- None found in `docs/designs/`.

## Scope Drift Table

| Artifact | Current placement | Better placement or fix | Why |
| --- | --- | --- | --- |
| `manage_fx_library.feature` | Tagged `@iter5`, linked from Iteration 4 | Move to Iteration 4 or split later-only scenarios | Current ownership is internally contradictory. |
| `search_sounds.feature` | Tagged `@iter5`, linked from Iteration 4 | Split FX-tab search into Iteration 4 and move broader cross-library search later | The feature file exceeds the linked design scope. |
| `preview_fx_track.feature` | Tagged `@iter5`, linked from Iteration 4 and Iteration 9 | Split it so preview behavior stays in 4 and animation scenarios move to 9 | Core preview and motion polish are different slices. |
| `play_scene.feature` | Tagged `@iter7`, reused by Iterations 9 and 10 | Split into Iteration 7 open/play, Iteration 9 crossfade, and Iteration 10 ducking | One file currently spans three iterations. |
| `play_random_track.feature` | Tagged `@iter7` | Keep both local d20 behavior and external `Next` behavior in Iteration 7, but align the rule and remove duplicate ownership elsewhere | The behavior contract is still shared with external-control artifacts and must stay consistent. |
| `reorder_soundscape_categories.feature` | Linked by Iterations 6 and 7 | Make Iteration 7 the sole owner and tag it accordingly | Current ownership obscures completion criteria. |
| `ci_readiness.feature` | Tagged `@iter5`, not linked anywhere | Remove it from iteration ownership entirely | CI readiness is currently orphaned and mixed-scope. |
| `home-design.md` | Linked by Iterations 0 and 5 | Scope the Iteration 0 reference to a placeholder Home surface only | The full design also contains later dashboard rules. |
| `scenes-list-design.md` | Linked by Iterations 3 and 10 | Keep direct play in Iteration 7, clone in Iteration 10, and keep both out of Iteration 3 | The design currently leaks future behavior into earlier planning. |
| `active-scene-soundscapes-design.md` and `active-scene-soundboard-design.md` | Linked by Iterations 7 and 10 | Keep Master Controls in Iteration 7, and keep Session Lock and Scene Notes out for now | The designs still need iteration-specific scoping. |

## Scenario Alignment Summary

| Iteration | Alignment result | Notes |
| --- | --- | --- |
| 0 | Partial | Acceptance coverage exists, but the linked Home design overstates scope. |
| 1 | Missing | No iteration-owned feature file coverage. |
| 2 | Good | CRUD coverage is strong; hierarchy restore rules are the main missing contract. |
| 3 | Partial | Core behavior is covered, but linked designs include later functionality and one routing TBD. |
| 4 | Partial | Coverage exists, but tags and ownership are wrong and one feature file overreaches. |
| 5 | Contradictory | Feature and design expect real data; plan says mocked data. |
| 6 | Partial | Engine behavior is covered, but ownership overlaps heavily with Iteration 7 and some rules live only in tests. |
| 7 | Partial | UI behavior exists, but add-to-scene and future-scope scenarios need splitting. |
| 8 | Good with noise | Owned features align, but foreign legacy `@iter5` files still pollute the slice. |
| 9 | Weak | Motion coverage exists, but linked supporting artifacts are not motion-specific. |
| 10 | Missing major coverage | Several planned features have no acceptance tests. |
| 11 | N/A | Placeholder by design. |

## Specification Completeness Challenges

These were the original ambiguity hotspots in the report. Resolutions are recorded inline where decisions have been made.

### Critical

1. What is the authoritative add-to-scene interaction?
   - Resolution recorded 2026-04-30: immediate single-tap add with no confirm is the authoritative contract.
   - Follow-up: align the plan, design, and feature artifacts to remove or defer the multi-select plus confirm flow.
   - This affects back-stack behavior, test steps, selection state, and duplicate handling.

2. What is the real Iteration 5 Home contract?
   - Resolution recorded 2026-04-30: Top Atmosphere and Legendary Action are mocked placeholders until Iteration 6.
   - Follow-up: move real playback history, real metrics, and real fade-in behavior to Iteration 6 artifacts only.

3. What exactly does Session Lock disable?
   - Resolution recorded 2026-04-30: remove Session Lock from product scope.
   - Separate requirement retained: playback should still behave correctly under OS lock-screen, notification, Bluetooth remote, and phone-call interruption scenarios.
   - Cleanup needed: remove Session Lock references from the plan, designs, and feature inventory so they do not conflict with system audio handling.

4. What does external `Next` do?
   - Resolution recorded 2026-04-30: randomize the currently prominent category only.
   - Follow-up: keep local category randomization separate from external control behavior in the feature files.
   - MediaSession behavior must be precise here.

5. What is the acceptance contract for Scene Notes, export/import, limiter behavior, and FX jitter?
   - Decomposition recorded 2026-04-30: resolve these four behaviors separately.
   - Resolution recorded 2026-04-30: remove Scene Notes from Iteration 10 for now.
   - Resolution recorded 2026-04-30: remove campaign export/import from Iteration 10 for now.
   - Resolution recorded 2026-04-30: remove limiter behavior from Iteration 10 for now.
   - Resolution recorded 2026-04-30: remove FX jitter/randomization from Iteration 10 for now.
   - Result: this bundled specification gap is closed by de-scoping all four items until they are properly specified.
   - These are in the plan, but not in matching feature files.
   - Without artifact support, they are not implementation-ready.

### Moderate

1. Are the soundscape/FX concurrency limits real product rules or engine guardrails?
   - Resolution recorded 2026-04-30: they are engine guardrails, not primary product rules.
   - Follow-up: keep only user-visible overflow behavior in the UX contract, if any.
   - Current limits only appear in feature files.

2. When an effect has multiple overlapping instances, does pause stop one instance or all instances?
   - Resolution recorded 2026-04-30: pause stops all running instances for that effect.
   - Tests currently expect all instances to stop.
   - Design wording is not explicit enough.

3. What happens to child entities and resume/home references when a trashed campaign is restored?
   - Resolution recorded 2026-04-30: restoring a campaign also restores its valid child entities and valid Home or resume references automatically.
   - Sessions are described as orphaned on delete.
   - Full restoration semantics are not completely documented.

4. In the Soundscape Library, what does tapping the category card body do?
   - Resolution recorded 2026-04-30: tapping the category card body opens the composer.
   - One design note still says routing is TBD.
   - The feature file already locks it to opening the composer.

5. Which parts of the full screen designs are visible early versus intentionally deferred?
   - Resolution recorded 2026-04-30: deferred controls should be hidden entirely in earlier iterations unless a plan explicitly introduces them.
   - This is especially important for the Home screen, Scenes list, and Active Scene tabs.

## Recommended Next Actions

1. Split mixed-scope feature files or add scenario-level iteration tags for `play_scene.feature`, `play_random_track.feature`, `preview_fx_track.feature`, and `view_credits.feature`.
2. Apply the add-to-scene decision across the plan, design, and feature artifacts: immediate single-tap add with no confirm.
3. Update the Iteration 5 and Iteration 6 artifacts so Home is mocked in Iteration 5 and real metrics begin in Iteration 6.
4. Fix Iteration 4 and the legacy `@iter5` ownership by retagging or splitting the FX library features.
5. Clean up Iteration 10 scope by removing Session Lock, Scene Notes, campaign export/import, limiter behavior, and FX jitter from the plan/design/feature set for now.
6. Scope full-screen design docs by iteration so early plans do not inherit future controls by accident, and hide deferred controls entirely unless a plan explicitly calls out a placeholder.