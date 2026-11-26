# P-07 — Pemutar audio + loop per ayat / per surat

Implement :core:audio and the SurahPlayer screen (artboards MurottalQuraisy, MurottalArabic, MurottalTablet, ReciterPicker, AudioMissing).
Requirements AUD-01..08 and PRD section 7 exactly.
- Audio files: audio/{reciterId}/{SSSAAA}.mp3, one file per ayah; resolve from bundled assets first, then downloaded files.
- PlayerController (pure Kotlin, unit-tested) holds PlayerState and implements onAyahCompleted() as specified for LoopMode.PER_AYAH and PER_SURAH with target 1/3/5/10/infinite (0).
- ExoPlayer wrapper preloads the next ayah so the gap is <= 150 ms; speed via PlaybackParameters(speed, pitch=1f); optional pause-after-ayah 0/2/4 s.
- UI: segmented control (Loop per ayah / Loop whole surah), repeat chips, status text "Ayah 2 of 4 - Repeat 1 of 3", tappable ayah cards that restart counting, highlighted active ayah, reciter chip, speed chip.
- If a file is missing show the AudioMissing sheet.
Write the unit tests from PRD section 16 (loop scenarios) first, then the implementation.
