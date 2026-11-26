# P-02 — Content database

Implement content.db as a read-only prepackaged SQLite asset in :core:data.
Tables: surah, ayah, word, translation, word_meaning, letter, tajwid_rule, tajwid_mark, dua, story, game_content, reciter, source, meta (see PRD section 9 for columns).
1. Write a Kotlin/JVM CLI script (tools/content-import) that imports Tanzil Uthmani text (translations are NOT bundled; they are fetched at runtime via the Quran Foundation Content Sync API into a separate translation cache (default editions: en = The Clear Quran by Dr. Mustafa Khattab, fr = Muhammad Hamidullah, de = Frank Bubenheim & Nadim Elyas; look up their resource IDs in the QF translations list, never hard-code text), re-synced at least every 7 days, never committed to the repo), computes a sha256 checksum per ayah, and fails if any ayah differs from the source file.
2. Room entities + DAOs (read-only) and a ContentRepository with: getSurahList(juz), getAyahs(surahId), getWords(surahId, ayahNo), getTranslation(surahId, lang), getGameItems(gameId, stage).
3. A version check: on startup compare meta.version with /v1/content/manifest and download a newer content.db in the background (verify sha256 before swapping).
Do NOT invent any Arabic text; use 3 dummy rows marked [PLACEHOLDER] in tests only.
