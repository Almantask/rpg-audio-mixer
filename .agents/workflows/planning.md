---
description: A workflow for planning a new feature or increment, starting from requirements to finalized designs and feature specs.
---

# Feature Planning Workflow

This workflow ensures that every new feature is thoroughly designed, documented, and reviewed before a single line of production code is written.

## Phase 1: Strategic Discovery
- **Trigger:** High-level feature request or problem statement.
- **Product Owner (@product-owner):**
    - Defines high-level business goals and prioritizes value-first.
    - Drafts primary behavior examples in cooperation with the `@qa-tester`.
- **Principal Product Owner (@principal-po):**
    - Reviews the strategic alignment and outcome focus (per *Inspired*, *Build Trap*, etc.).

## Phase 2: Design & UI Translation
- **Product Designer (UX) (@product-designer):**
    - Translates PO goals into `x-design.md` files.
    - Builds or updates **HTML prototypes** in `docs/designs/`.
    - Ensures each "Scene" in the design points to its corresponding HTML file.
    - Defines empty, loading, success, and error states in the design specs.

## Phase 3: Behavioral Specification
- **QA Tester (@qa-tester):**
    - Translates the Designer's specs into Gherkin `.feature` files with concrete examples.
    - Places feature files in `app/src/androidTest/assets/features/`.
- **Principal QA (@principal-qa):**
    - Reviews `.feature` files for ambiguity and asks the human clarifying questions about behavior.
    - Suggests test optimizations.

## Phase 4: Implementation Strategy
- **Android Developer (@android-developer):**
    - Drafts an extensive implementation plan summary in `/plans/summary.md`.
    - Creates detailed iteration plans in `/plans/iteration-x.md`.
    - **Links and References:** All plans MUST reference the relevant `.feature` files, Scenes (pointing to HTML), and `.html` prototypes.
- **Audio Specialist (@audio-specialist):** (If applicable)
    - Provides specific audio strategy recommendations to be included in the implementation plan.
- **Principal Engineer (@principal-engineer):**
    - Reviews the iteration plans and asks the human strategic questions with exactly 2-3 options in `/feedback/iteration [x].md`.
- **Entrepreneur (@entrepreneur):**
    - Reviews the plans and asks the human strategic business/market viability questions in `/feedback/request [name].md`.

## Phase 5: Human Feedback Loop
- **Trigger:** Human selects an option (ticks a checkbox) in the `/feedback/` directory.
- **The Team (@product-owner, @product-designer, @qa-tester, @android-developer):**
    - ALL agents must check the relevant `/feedback/` files for human decisions.
    - If a decision affects their area, they must update their artifacts (specs, designs, feature files, or plans) accordingly.
    - Implementation does NOT start until all feedback is resolved and artifacts are updated.

## Phase 6: Final Review & Baseline
- **Project Historian (@project-historian):**
    - Finalizes the baseline documentation in `plans/summary.md` and updates `README.md`.
    - Captures the strategic rationale for the chosen path.
