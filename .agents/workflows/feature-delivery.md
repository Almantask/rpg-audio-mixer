---
description: An orchestration workflow that sequentially hands a feature specification through the PO, QA, Developer, Code Reviewers, and finally back to the PO for sign-off.
---

# Feature Delivery Orchestration

This workflow is designed to simulate an agile development team right here in the IDE.

**Instructions for Antigravity (the AI Assistant):**
Follow these steps in strict sequence. Do NOT skip any steps. If a step involves a command, remember it will require the User's approval.

1. **Adopt `product-owner` Persona:**
   - Read the provided design spec `.html` or `.md`.
   - List the core Acceptance Criteria and business rules that must be met.
   - Hand off to QA.

2. **Parallel Implementation (`qa-tester` & `android-developer`):**
   - **QA:** Generate or modify the appropriate Cucumber `.feature` file based on the PO's criteria, and document test scenarios.
   - **Dev:** Do not wait for the tests. Immediately begin TDD based on the PO's requirements. Write the Domain models, unit tests, ViewModels, and Composables.

3. **Adopt `qa-tester` Persona (Validation):**
   - Write the Espresso test step definitions bridging the Dev's UI and the `.feature` file.
   - Propose to run the command `.\.agents\skills\qa-tester\scripts\run_acceptance_tests.ps1 -FeaturePath "features/[target].feature"`
   - Evaluate the output. 
   - **Important:** If tasks fail, return to the Dev Persona to fix the implementation. Only proceed if tests pass. If it fails 3 times, pause and ask the User for human intervention.

4. **Code Review Phase (`qa-code-reviewer` & `android-code-reviewer`):**
   - **QA Review:** Adopt `qa-code-reviewer` persona to build and evaluate the test codebase (e.g., Cucumber scenarios, Espresso definitions). Note any warnings, deprecations, bugs, security issues, or testing smells.
   - **Android Review:** Adopt `android-code-reviewer` persona to build and evaluate the production codebase. Note any architectural warnings, memory leaks, security issues, or code smells.
   - **Pair Review:** Combine both reviewer findings in a pair review. Determine if changes are needed. Hand back to Dev/QA if necessary. (Maximum 2 revisions allowed before pausing for human intervention.)

5. **Adopt `product-owner` Persona:**
   - Review the final code changes.
   - Give the Final Approval if they match the original design documents.
   - If the feature is rejected, hand back to the Developer. (Maximum 2 revisions allowed before pausing for human intervention.)
   - Present a summary walkthrough artifact after final approval.
