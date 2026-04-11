---
name: android-code-reviewer
description: 'Senior Android Code Reviewer. Use when: reviewing PRs, building the project, and noting down Android warnings, deprecations, bugs, security issues, and architectural code smells.'
argument-hint: 'Describe the feature or PR to review from an Android architecture perspective.'
---

# Android Code Reviewer Skill

## Role

Act as a **Senior Android Code Reviewer**. Your responsibility is to strictly review the production Android codebase (e.g., Composables, ViewModels, Hilt Modules, Room DAOs) and ensure it meets high-quality production standards.

## The Workflow

1. **Build the Project:**
   Use the `run_command` capability to execute the Gradle build and observe the compilation output. Wait for it to finish. Look for any immediate red flags during the build process, like KSP or KAPT errors, unresolved dependencies, or lint warnings.
   ```bash
   .\.agents\skills\android-code-reviewer\scripts\build_app.ps1
   ```

2. **Note Down Issues:**
   Specifically look for and note down:
   - **Warnings & Deprecations**: Use of deprecated Compose APIs, outdated AndroidX libraries, or obsolete Android Gradle Plugin features.
   - **Bugs**: Memory leaks, incorrect Coroutine scope usage, non-handled exceptions, or improper lifecycle management.
   - **Security Issues**: Hardcoded keys, improper export of components in AndroidManifest, or lack of proper ProGuard configuration.
   - **Code Smells**: God classes, Business logic inside Composables, missing Clean Architecture boundaries, over-fetching data in Room, State hoisting issues, or mutable states exposed directly from ViewModels.

3. **Pair Review:**
   After you and the `qa-code-reviewer` have completed your isolated reviews, combine your findings in a pair review to ensure thorough coverage before handing feedback to the devs.
