# P-06 — Daftar surat, mushaf, ketuk kata

Build :feature:quran screens QuranLibrary, MushafPage, WordDetail (bottom sheet), MushafSettings (bottom sheet) per the artboards.
Requirements QRN-01..04.
- Mushaf page renders ayahs from content.db with the licensed Uthmani font, ayah end markers, surah headers and basmalah; justified RTL text; optional tajwid colouring from tajwid_mark ranges (AnnotatedString spans).
- Tapping a word opens WordDetail with word audio (if available), meaning, transliteration within 200 ms from local data.
- Settings: 3 text sizes, tajwid colours, transliteration, meaning language, night mode; persisted per profile.
