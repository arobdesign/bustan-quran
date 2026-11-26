# P-08 — Hafalan, rekaman, murojaah

Build :feature:hafalan: Hafalan (3 steps Listen x3 -> Repeat x3 -> On my own), HafalanMap, VoiceCheck (v2, behind a feature flag).
Requirements HFZ-01..05.
- Fade levels 0-3 (full, some words hidden, first letters only, blank) chosen from successful attempts.
- Recording with MediaRecorder AAC 32 kbps mono to app-internal storage; compare playback with the reciter; auto-delete after 90 days unless kept.
- ReviewScheduler: simplified SM-2 on ayah_progress (ease, intervalDays, dueAt). Daily review box = 3-5 due items. Unit-test the scheduler.
- HafalanMap grid of Juz 'Amma surahs (114 -> 78) with 4 status colours.
