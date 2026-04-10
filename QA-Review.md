# 10-Minute Manual QA Checklist: Arcanum Audio

This checklist covers the end-to-end functionality of the app across campaigns, scenes, audio management, and the active session engine based on the `design-overall.md` and iteration plan.

## 1. Shell & Data Configuration (~2 mins)
- [ ] **Theming & Shell:** Verify the app uses a dark theme with gold/amber accents. Ensure the bottom navigation transitions smoothly between Home, Campaigns, Scenes, and Library tabs.
- [ ] **Credits:** Tap the ⚙️ gear icon from any screen. Verify it opens the Credits screen.
- [ ] **Campaigns:** Go to the Campaigns tab. Check for the empty state illustration. Add a `+ NEW CAMPAIGN`.
- [ ] **Sessions:** Enter the campaign. Verify the empty state here. Add a `+ ADD NEW SESSION`.
- [ ] **FX Library:** Go to Library > Sound Effects tab. Import a sound or edit (✏️) an existing one. Try applying the predefined tags via the chips.
- [ ] **Soundscape Composer:** Go to Library > Soundscapes tab. Edit (✏️) a category. Ensure you can specify audio files, assign them to Intensity levels (I/II/III), and adjust their default mix sliders. Save the composition.

## 2. Scene Assembly (~2 mins)
- [ ] **Create Scene:** Navigate to the Scenes tab. Add a New Scene. Use the chip picker to add thematic tags.
- [ ] **Enter Active Scene:** Open the newly created scene. 
- [ ] **Add Soundscapes:** In the Soundscapes tab of the Active Scene, tap `+ ADD NEW SOUNDSCAPE`. Select categories you have tracks for (categories with 0 tracks should be omitted/disabled).
- [ ] **Add FX:** Switch to the Soundboard tab. Tap `+ ADD NEW EFFECT` and add your mapped FX from Step 1.

## 3. The Audio Engine & Mixing (~3 mins)
- [ ] **Master & Mix Volumes:** In the Soundscapes tab, adjust the Master volume slider and the per-category MIX sliders. Both should affect audio output. Sliders should announce their percentage to VoiceOver/TalkBack.
- [ ] **Rolled Playback:** Press the 🎲 (d20) button on a loaded category. It should pick a random track from the pool.
- [ ] **Intensity Switching:** Change the intensity level (I/II/III). Note that levels with no tracks should be greyed out. Change the intensity and press play/d20 to ensure tracks switch.
- [ ] **Visual Cues:** Verify that a playing category card features a smooth glowing highlight border.
- [ ] **Soundboard Overlap:** Switch to the Soundboard tab. Tap an FX. Then tap it repeatedly while playing. It should re-trigger, overlapping multiple instances of the sound simultaneously. 
- [ ] **Stop FX:** The button should pulse while playing and show a ⏸ icon. Tap ⏸ to cut it short.

## 4. Scene Switching & Home Dashboard (~2 mins)
- [ ] **Crossfading:** Go back to the Scenes tab. Tap the **▶ (Play)** button directly on a *different* scene's card. It should start with a smooth 2-3 second fade-in while replacing the previous audio.
- [ ] **Home Dashboard:** Navigate to the Home tab. 
- [ ] **Resume Journey:** Verify the middle card shows your last opened scene. Tapping 'ENTER' should auto-resume the scene with fade-in audio.
- [ ] **Stats Tracking:** Check the "Top Atmosphere" and "Legendary Action" cards. They should populate accurately based on the sounds you just triggered most during step 3.

## 5. Deletion & Recovery (~1 min)
- [ ] **Soft Delete:** Go to Campaigns (or Scenes) and delete an item (e.g. by using the swipe-to-delete gesture or edit dialog).
- [ ] **Trash Vault:** Tap the ⚙️ gear icon, then go to `RESTORE RECENT DELETES` (Vault of Echoes).
- [ ] **Restore & Verify:** Ensure your deleted item appears in the Vault with the appropriate 7-day warning. Tap the gold Restore button. Go back to where you deleted it and verify it has completely returned.

# Score: 