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
    - Updates `x-design.md` and **HTML prototypes** in `docs/designs/`.
    - Ensures all Scenes remain correctly linked to their HTML prototypes.

## Phase 3: Specification Refinement
- **QA Tester (@qa-tester):**
    - Modifies existing `.feature` files to match the refinement.
- **Principal QA (@principal-qa):**
    - Reviews refined specs for ambiguity and consults with the human user.

## Phase 4: Plan Update
- **Android Developer (@android-developer):**
    - Updates `/plans/summary.md` and the relevant `/plans/iteration-x.md`.
    - Ensures updated plans correctly reference all changed feature files, scenes, and html files.
- **Audio Specialist (@audio-specialist):** (If applicable)
    - Updates recommendations in the implementation plan for any audio refinements.
- **Principal Engineer (@principal-engineer):**
    - Audits updated plans and provides 2-3 options for any new technical trade-offs to the human in `/feedback/iteration [x].md`.
- **Entrepreneur (@entrepreneur):**
    - Challenges the refinement for business impact and speed-to-market in `/feedback/request [name].md`.

## Phase 5: Human Feedback Loop
- **Trigger:** Human selects an option in the `/feedback/` directory.
- **The Team:**
    - ALL agents must review the human's choices in the `/feedback/` directory.
    - Update artifacts (designs, feature files, plans) to align with the chosen options.

## Phase 6: Historical Baseline
- **Project Historian (@project-historian):**
    - Updates the project history and architectural records in `app/Learnings.md`.
