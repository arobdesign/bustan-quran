# P-18 — Implementasi desain UI sesuai canvas (prioritas sebelum lanjut P-17)

```text
PRIORITY CHANGE: pause P-17 (KMP migration) after the current phase. Before anything else, make the app UI match the design exactly. The current screens do not look like the design yet.

SOURCE OF TRUTH FOR THE LOOK
Read ./design/README.md first. Then use:
- ./design/png/NN-*.png  — rendered design of every screen at 2x (1 dp = 2 px). This is what the screen must look like.
- ./design/source/*.dc.html — the original design files. Every size, color, radius, spacing, font size and weight is in the inline `style` attributes as CSS px. Rule: 1 CSS px = 1 dp, font-size px = sp. Icons and illustrations are inline <svg>: convert them to Compose ImageVector / Canvas. Do NOT replace them with Material icons, emoji or different art.
- {{ }}, <sc-for>, <sc-if> and the <script> renderVals() block in the source files only provide sample data. Real data comes from the existing ViewModels.
Keep all existing logic, ViewModels, navigation and tests. This task changes the UI layer only.

STEP 0 - GAP REPORT (no code changes)
Open every PNG and compare it with the current Compose screen. Reply with a table: screen no. | screen | current state (matches / partly / old UI) | main differences. Then wait for my "OK".

STEP 1 - DESIGN SYSTEM FIRST
- Theme tokens with the exact hex values from design/README.md (sand, parent-bg, ink, muted, teal, teal-dark, mint, saffron, butter, coral, sky, borders).
- Bundle the fonts: Baloo 2, Nunito, Baloo Bhaijaan 2 (Google Fonts, OFL licence) as font resources. Amiri Quran only as a placeholder for Qur'an text until the licensed mushaf font is added.
- Typography scale and shapes taken from the design files (card radius 16-28 dp, pill buttons, the flat "0 2px 0" offset card shadow drawn as a solid offset shape, not an elevation shadow).
- Rebuild the shared components to match the design: primary/secondary pill buttons, cards, chips, segmented control, toggle switch, back button, bottom navigation, star rating, progress bars, badges, island tiles, mascot (Hud-hud) and animal avatars as ImageVectors.
- Add a debug-only "Design Catalog" screen that shows every component, for review.
STOP and show me screenshots of the catalog.

STEP 2 - SCREENS, IN PRIORITY BATCHES (screen numbers = design/png numbers)
A. Core child flow: 44, 10, 11, 01, 02, 19, 03, 09, 05, 06
B. Qur'an: 15, 36, 37, 38, 39, 16, 04, 22, 17, 18
C. Parents & account: 07, 33, 34, 14, 12, 13, 31, 32, 28, 29, 30, 40, 41, 42, 43, 26
D. Games: 20, 21, 45-57
E. Islamic content & rewards: 23, 24, 25, 35, 58-63, 27
F. Variants: Arabic RTL (08, 64-67), French/German (68-69), tablet layouts (70-71). These are NOT separate screens: the same screens must match these designs when the locale is ar/fr/de or the window is Expanded.

RULES FOR EVERY SCREEN
- Match layout, spacing, sizes, colors, fonts and copy of the design. English copy from the design goes into the string resources (en); keep ar/fr/de translations in sync.
- Touch targets as in the design (>= 64 dp for ages 3-4, >= 48 dp otherwise). Full RTL mirroring for Arabic.
- Never type Arabic Qur'an text yourself: Arabic text in the design is a placeholder; real text comes from content.db.
- Write composables using only APIs that also exist in Compose Multiplatform (no android.* imports inside screen composables; keep platform things behind small wrappers) so the later move to commonMain in P-17 is mechanical.

VERIFY EACH SCREEN
Add screenshot tests (Roborazzi) at 390x844 dp (phone) and 1194x834 dp (tablet). For each screen save a side-by-side image ./design/compare/NN-name.png (left: design PNG, right: app screenshot). List any remaining differences.

AFTER EACH BATCH
Build, run all tests, update PROGRESS.md (one row per batch: P-18A ... P-18F), show me the compare images, then STOP and wait for "lanjut".
When P-18F is done, resume P-17 at PHASE 2.

Start with STEP 0 now.
```
