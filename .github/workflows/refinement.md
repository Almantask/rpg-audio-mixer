---
description: A workflow for refining existing specs, designs, and implementation plans.
---

# Feature Refinement Workflow

This workflow focuses on improving, correcting, or extending existing features through design iterations and plan updates.

## Phase 1: Identifying Refinement Needs
- **Trigger:** UX feedback, bug report, or feature extension request.
- **Product Owner (@product-owner):**
    - Evaluates feedback and re-prioritizes feature value.
    - Defines change goals and behavior updates with the `@qa-tester`.

## Phase 2: Design & UI Update
- **Product Designer (UX) (@product-designer):**
    - Updates **`[scene]-design.md`** and **HTML prototypes** in `docs/designs/`.
    - Ensures all Scenes remain correctly linked to their HTML prototypes.

## Phase 3: Specification Refinement
- **QA Tester (@qa-tester):**
    - Modifies existing `.feature` files to match the refinement.
- **QA Reviewer (@qa-reviewer):**
    - Performs a peer review of the refined `.feature` files to ensure they follow project standards and maintain coverage integrity.
- **Principal QA (@principal-qa):**
    - Reviews refined specs for ambiguity and consults with the human user.

## Phase 4: Plan Update
- **Android Developer (@android-developer):**
    - Updates `/plans/summary.md` and the relevant `/plans/iteration-x.md`.
    - Ensures updated plans correctly reference all changed feature files, scenes, and html files.
- **Audio Specialist (@audio-specialist):** (If applicable)
    - Updates recommendations in the implementation plan for any audio refinements.
- **Android Reviewer (@android-reviewer):**
    - Performs a "Dev Review" of the refined implementation strategy to catch architectural regressions or complexity early before it moves to Principal review.

## Phase 5: Refined Strategy Review
- **Principal Engineer (@principal-engineer):**
    - Audits updated plans and provides 2-3 options for any new technical trade-offs to the human in `/feedback/iteration [x].md`.
- **Principal Product Owner (@principal-po):**
    - Challenges the refinement for business impact, market fit, and speed-to-market in `/feedback/request [name].md`.
- **Principal QA (@principal-qa):**
    - Reviews the refined plan for testability and behavior coverage.

## Phase 6: Human Feedback Loop
- **Trigger:** Human selects an option in the `/feedback/` directory.
- **The Team:**
    - ALL agents must review the human's choices in the `/feedback/` directory.

## Phase 7: Post-Review Fixes
- **Android Developer (@android-developer) & Audio Specialist (@audio-specialist):**
    - Address all technical and strategic feedback from the Reviewers and the Human user.
    - Update artifacts (designs, feature files, plans) to align with the final decisions.