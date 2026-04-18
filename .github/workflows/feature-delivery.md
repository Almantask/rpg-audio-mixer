---
description: An orchestration workflow that sequentially hands a feature specification through the PO, QA, Developer, Code Reviewers, and finally back to the PO for sign-off.
---

# Feature Delivery Workflow

This workflow is designed to simulate an agile development team right here in the IDE.

**Instructions for Antigravity (the AI Assistant):**
Follow these steps in strict sequence. Do NOT skip any steps. If a step involves a command, remember it will require the User's approval.

## Phase 1: Implementation
Both `@qa-tester` and `@android-developer` personas activate and work alongside each other on the given requirements.
- **QA Tester:** Writes the BDD `.feature` file based on requirements and drafts the Espresso step definitions. *Does NOT run tests yet.*
- **Android Developer:** Implements the production code following TDD (Red -> Green -> Refactor) for unit tests, setting up domain, ViewModels, and UI.

## Phase 2: Validation
Once Dev implementation is complete:
- **QA Tester** runs the acceptance test suite against the finished Dev code: `.\.agents\skills\qa-tester\scripts\run_acceptance_tests.ps1 -FeaturePath "features/[target].feature"`.
- If tests fail, hand execution back to **Android Developer** to fix the implementation. Do not proceed until tests are green.

## Phase 3: The Review Council
Once tests pass, the following reviews MUST be executed automatically in sequence:
1. **Peer Review:** `@android-reviewer` checks production code for architecture/smells, and `@qa-reviewer` checks the test codebase.
2. **Technical Excellence Review:** `@principal-engineer` audits the technical architecture and provides feedback or asks strategic questions in `/feedback/iteration [x].md`.
3. **Quality & Behavior Review:** `@principal-qa` audits the feature files and test robustness, providing feedback in `/feedback/feature [name].md`.
4. **Audio Review:** `@audio-specialist` verifies that any media logic avoids latency traps and handles ExoPlayer/SoundPool correctly.
5. **PO & Outcome Review:** `@product-owner` and `@principal-po` perform a final review against Acceptance Criteria and strategic outcomes. `@principal-po` provides feedback in `/feedback/feature [name].md`.

## Phase 4: Post-Review Fixes
If any issues were identified during the Review Council (Phase 3):
- **Android Developer** and **QA Tester** collaborate to address the review feedback from ALL reviewers.
- **Human Decisions:** ALL agents must check the `/feedback/` directory for any human decisions made on Principal questions and adjust the implementation accordingly.
- Re-run validation (Phase 2) and obtain final sign-off from the Review Council (Phase 3).

## Phase 5: Project Historian (Documentation)
Once PO sign-off is achieved:
- Activate the `@project-historian` persona.
- The Historian reviews the entire context of what was just built.
- The Historian updates `app/Learnings.md` and any other project documentation (like architectural decisions or new idioms discovered) to preserve institutional memory.
