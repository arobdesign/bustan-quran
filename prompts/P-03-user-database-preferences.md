# P-03 — User database & preferences

Implement user.db with Room in :core:data: family, child_profile, ayah_progress, session_log, recording, reward, screen_time, download, purchase (PRD section 9). Every syncable table has updatedAt and deleted columns.
Add Proto DataStore for family settings and per-profile settings (appLang, meaningLang, reciterId, translit, tajwidColors, music, dailyLimitMin, backupRecordings, analyticsOptIn).
Write migrations as explicit Migration objects and tests with MigrationTestHelper.
