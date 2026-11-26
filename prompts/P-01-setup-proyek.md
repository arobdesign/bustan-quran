# P-01 — Setup proyek

Using the master context, create the Android project skeleton:
- Gradle version catalog, the modules listed in ARCHITECTURE, Hilt wiring, Navigation-Compose host with placeholder routes for every screen name in the PRD screen map (Main, Home, QuranLibrary, MushafPage, MurottalQuraisy -> SurahPlayer, Hafalan, GameList, ParentGate, ParentDashboard, Settings, Paywall, ...).
- QpkTheme in :core:ui with the design tokens, typography (Baloo 2, Nunito, Baloo Bhaijaan 2 via downloadable fonts), and reusable components: QpkPrimaryButton, QpkPillButton, QpkCard, BackButton, StarRating, ProgressPill, BottomNav (Map, Qur'an, My cards).
- GitHub Actions CI: build, ktlint, detekt, unit tests.
- strings.xml for en, ar, fr, de with supportsRtl=true.
Output the file tree first, then the files.
