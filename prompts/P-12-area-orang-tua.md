# P-12 — Area orang tua

Build :feature:parent: ParentDashboard, WeeklyReport, ChildRecordings, Settings, EmptyProgress, Istirahat (screen-time break) per artboards.
Requirements PAR-01..04, SET-01..03.
- All data from local Room queries (works offline).
- WeeklyReport: minutes per day bar chart (Compose Canvas), highlights, one tip; generated every Monday 06:00 local via WorkManager.
- ScreenTimeGuard: counts foreground learning time per profile per day; when the limit is reached navigate to Istirahat; +10 minutes only through ParentGate.
- Export (JSON) and delete-all with confirmation (ACC-05).
