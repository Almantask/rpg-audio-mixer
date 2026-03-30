# RPG Audio Mixer: Design & Specification Audit Review

This document outlines the inconsistencies, discrepancies, and missing elements identified during a comprehensive audit of the project's design mockups, `design-spec.md`, individual screen design docs, and the Cucumber `.feature` files.

## 1. Inconsistencies with "Removed" Features still in Mockups
Several features were agreed to be "removed" or "out of scope" in the `design.md` checklist, but they still appear prominently in the provided visual mockups:
*   **"The Archivist's Choice":** `manage_soundscape_categories.feature` and `design.md` explicitly state this is removed. However, the `AudioLibrary-Soundscape-Categories.png` mockup still displays a large banner for it at the bottom of the screen.
*   **"BUY MORE" Button / In-App Purchases:** `cannot_modify_bought_scenes.feature` notes that "The concept of purchased/locked scenes is out of scope." Yet, `AudioLibrary-FX.png` features a large "BUY MORE" button next to "IMPORT FX".
*   **Heart / Favourite Icons:** `design.md` indicates that Heart/Favourite icons were removed. However, `AudioLibrary-FX.png` contains heart icons on every single FX row.
*   **Three-Dot (`⋮`) Menus:** `manage_fx_library.feature` specifically says "I do not see a three-dot menu icon on the row" and specifies a pencil icon for editing. The `AudioLibrary-FX.png` mockup still shows three-dot menus instead of pencil icons for individual sound effects.
*   **FILTER button:** Mentioned as removed in `campaign-sessions-design.md`, but filtering UI is still ambiguously represented.

## 2. Empty States, Errors, and Undesigned Flows
As noted in the project checklist, substantial portions of the user journey currently lack visual design representation:
*   **Empty States:** Feature files perfectly describe empty states (e.g., "I see the empty state illustration and a 'Scribe New Tale' button"). We currently have no mockups for these empty states across Campaigns, Sessions, Scenes, or the Library.
*   **Error Handling:** Missing UI designs for network errors, failed audio loading, and invalid file imports (as described in `import_custom_sound.feature` where an error message "The selected file could not be read as audio" is mandated).
*   **Search/Filtering States:** `search_sounds.feature` describes advanced filtering capabilities (by scene, by ambient vs. soundboard type). The mockups only show basic dropdowns in the Audio Library FX screen and a text search bar in the global Scenes list. The UI for these advanced filters is missing.
*   **Onboarding / Profile Setup:** Noted as missing in `design.md`.

## Recommendations for Next Steps
1.  **Mockup Refresh:** Request updated Figma/PNG exports that remove the legacy elements (Buy buttons, Archivist's Choice, Hearts, Three-Dot menus) to match the feature files.
2.  **Design Missing States:** Prioritize the creation of the empty state illustrations and error toasts/dialogues, as these are critical for the minimum viable product's user experience.
