---
description: An orchestration workflow that sequentially hands a feature specification through the PO, QA, Developer, Code Reviewers, and finally back to the PO for sign-off.
---

# Feature Delivery Orchestration

This workflow is designed to simulate an agile development team right here in the IDE.

**Instructions for Antigravity (the AI Assistant):**
Follow these steps in strict sequence. Do NOT skip any steps. If a step involves a command, remember it will require the User's approval.

## Phase 1: Parallel Implementation (Dev & QA)
Both `android-developer` and `qa-tester` personas activate and work alongside each other on the given requirements.
- **QA Tester:** Writes the BDD `.feature` file based on requirements and drafts the Espresso step definitions. *Does NOT run tests yet.*
- **Android Developer:** Implements the production code following TDD (Red -> Green -> Refactor) for unit tests, setting up domain, ViewModels, and UI.

## Phase 2: QA Validation
Once Dev implementation is complete:
- **QA Tester** runs the acceptance test suite against the finished Dev code: `.\.agents\skills\qa-tester\scripts\run_acceptance_tests.ps1 -FeaturePath "features/[target].feature"`.
- If tests fail, hand execution back to **Android Developer** to fix the implementation. Do not proceed until tests are green.

## Phase 3: The Review Council
Once tests pass, the following reviews MUST be executed automatically in sequence:
1. **Peer Review:** `android-code-reviewer` checks production code for architecture/smells, and `qa-code-reviewer` checks the test codebase.
2. **Audio Review:** `audio-specialist` verifies that any media logic avoids latency traps and handles ExoPlayer/SoundPool correctly.
3. **PO Review:** `product-owner` performs a final review of the completed feature against the original Acceptance Criteria to give final sign-off.

## Phase 4: Project Historian (Documentation)
Once PO sign-off is achieved:
- Activate the `project-historian` persona.
- The Historian reviews the entire context of what was just built.
- The Historian updates `app/Learnings.md` and any other project documentation (like architectural decisions or new idioms discovered) to preserve institutional memory.

