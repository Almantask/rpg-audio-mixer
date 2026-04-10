---
description: An orchestration workflow that sequentially hands a feature specification through the PO, QA, Developer, and finally back to the PO for sign-off.
---

# Feature Delivery Orchestration

This workflow is designed to simulate a 3-agent agile development team right here in the IDE.

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
   - Propose to run the command `$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"; .\gradlew connectedAndroidTest -PcucumberFeatures="features/[target].feature"`
   - Evaluate the output. 
   - **Important:** If tasks fail, return to the Dev Persona to fix the implementation. Only proceed if tests pass. If it fails 3 times, pause and ask the User for human intervention.

4. **Adopt `product-owner` Persona:**
   - Review the final code changes.
   - Give the Final Approval if they match the original design documents.
   - Present a summary walkthrough artifact.
