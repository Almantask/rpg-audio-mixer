# Credits ("Behind the Screen") — Screen Design

**Design References:**
- [`docs/designs/Credits.html`](../../docs/designs/Credits.html)
- [`docs/designs/Credits.png`](../../docs/designs/Credits.png)

---

## Purpose

The Credits screen provides info about the app, its creators, and external links. It doubles as the only "Settings"-adjacent screen in the app. Reached by tapping the ⚙️ gear icon on any screen.

---

## Layout

```
┌─────────────────────────────────────┐
│  ← Behind the Screen           [⚙️]  │
├─────────────────────────────────────┤
│                                     │
│         [App logo / wordmark]       │
│         Arcanum Audio               │
│         Version 1.0.0               │
│                                     │
│  [ SYNC PURCHASES & FREE TRACKS ]   │
│  (Available once per day)           │
│                                     │
│  [ 🗑️ RESTORE RECENT DELETES ]      │
│                                     │
│  ─── CREDITS ─────────────────────  │
│  Developer name / studio name       │
│  Role description                   │
│                                     │
│  ─── LINKS ────────────────────────  │
│  📄  Documentation                  │
│  💬  Discord community              │
│  ✉️   Contact / support email        │
│                                     │
│  Made with ❤️ for GMs everywhere     │
│                                     │
├─────────────────────────────────────┤
│  🏰 HOME  📖 CAMPAIGNS  🖼 SCENES  🎵 LIBRARY │
└─────────────────────────────────────┘
```

---

## Components

### Top Bar
- Back arrow → returns to whichever screen the user came from
- Screen title "Behind the Screen"
- ⚙️ gear icon (present but navigates to self — no-op or scrolls to top)

### App Identity Block
- App logo / wordmark centred
- App name: **Arcanum Audio** in large gold typography
- App version number (e.g. "Version 1.0.0")

### Actions Row
- **SYNC PURCHASES & FREE TRACKS**
  - Downloads all purchased and free (demo) tracks missing from the device.
  - Can only be used once per day. Greyed out (disabled) for 24 hours after a successful sync.
- **RESTORE RECENT DELETES**
  - Navigates to the Trash screen to restore soft-deleted categories or scenes.

### Credits Section
- Developer or studio name with role descriptions (e.g. "Design & Development — [Name]")
- Additional contributors listed as needed

### Links Section
Each link is a tappable row that opens the relevant URL or email in the appropriate OS handler:

| Link | Behaviour |
|---|---|
| Documentation | Opens docs URL in browser |
| Discord community | Opens Discord invite link in browser or Discord app |
| Contact / support email | Opens email client with pre-filled address |

### Tagline
Friendly tagline at the bottom — "Made with ❤️ for GMs everywhere" or similar.

### Bottom Navigation Bar
- The tab that was previously active remains active (Credits is a modal-style overlay in the navigation hierarchy, not a new tab)

---

## Interactions & Behaviour

| Interaction | Result |
|---|---|
| Tap back arrow | Return to previous screen |
| Tap SYNC PURCHASES | Initiates sync. Becomes greyed out for 24h on success |
| Tap RESTORE DELETES | Navigate to Trash screen |
| Tap Documentation link | Open in device browser |
| Tap Discord link | Open Discord (app or browser) |
| Tap email link | Open device email client |
| Tap bottom nav tab | Switch to that section (back stack cleared to tab root) |

---

## States

### Normal
Full content visible. No loading or empty states needed.

---

## Navigation

| Destination | Trigger |
|---|---|
| Previous screen | Back arrow |
| Trash screen | RESTORE RECENT DELETES |
| External browser | Documentation or Discord link |
| Email client | Contact/email link |
