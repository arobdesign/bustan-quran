# P-17 — Migrasi ke Kotlin Multiplatform (Android + iOS)

```text
Read CLAUDE.md, PRD-Bustan-Quran.md and PROGRESS.md first.

GOAL
Convert ./android-app into a Kotlin Multiplatform + Compose Multiplatform project that ships BOTH Android and iOS from one codebase. Reuse the existing code; do not rewrite from scratch. Android behaviour must not change.

WORK IN PHASES. After each phase: build Android, run all tests, report files changed + results, update PROGRESS.md, then STOP and wait for my "lanjut".

PHASE 1 - AUDIT (no code changes)
List every Android-only API/library per module (Hilt, Android resources, Media3/ExoPlayer, MediaRecorder, WorkManager, Play Billing, Context/file paths, Crashlytics, Android ViewModel/Navigation, WindowSizeClass) and the KMP replacement you propose. Wait for my approval.

PHASE 2 - PROJECT STRUCTURE
Create the KMP layout: shared modules with commonMain / androidMain / iosMain (keep the existing module boundaries: core:ui, core:data, core:audio, feature:*), an androidApp entry, and an iosApp Xcode project that hosts the Compose UI (ComposeUIViewController). Use the official KMP Gradle plugin and current stable versions.

PHASE 3 - SHARED LOGIC
Move pure-Kotlin domain code to commonMain with its unit tests to commonTest: PlayerController, ReviewScheduler, RewardEngine, SyncEngine, ScreenTimeGuard, TracingEvaluator, GameEngine/reducer, ParentGateChallengeGenerator, BillingEntitlements rules. All existing tests must still pass.

PHASE 4 - DATA
Room -> Room KMP with BundledSQLiteDriver; on iOS copy the prepackaged content.db from the app bundle on first launch and verify its SHA-256. Proto DataStore -> KMP DataStore with kotlinx.serialization. Supabase -> supabase-kt (multiplatform) + Ktor client. Never add Quran Foundation translations to the bundle (Content Sync only, per PRD section 13).

PHASE 5 - DI, RESOURCES, NAVIGATION, UI
Hilt -> Koin. strings.xml (en, ar, fr, de) -> Compose Multiplatform resources (composeResources/values, values-ar, values-fr, values-de); fonts as compose resources. Jetpack Navigation/ViewModel -> the JetBrains multiplatform versions; WindowSizeClass -> material3-adaptive. Move Compose screens to commonMain. Arabic must stay full RTL on iOS too.

PHASE 6 - PLATFORM CODE (expect/actual or interfaces + Koin)
- Audio player: Media3 ExoPlayer (Android) / AVQueuePlayer (iOS) with next-ayah preload (gap <= 150 ms), speed without pitch change, background audio + lock-screen controls.
- Recording: MediaRecorder / AVAudioRecorder (AAC, app-private storage, 90-day auto-delete).
- Background work: WorkManager / BGTaskScheduler (sync, downloads, weekly report).
- Billing: Play Billing (Android) / StoreKit 2 (iOS), same product IDs and entitlement model. Ask me before adding RevenueCat or any other SDK.
- Crash reporting: Firebase Crashlytics on both platforms, no child data.
- Connectivity, file paths, share sheet / PDF print for the certificate.

PHASE 7 - BACKEND + APPLE RULES
- Extend the Supabase Edge Functions: verify App Store transactions (App Store Server API) and handle App Store Server Notifications V2, next to the existing Google Play verification.
- Add Sign in with Apple next to Google sign-in.
- App Store Kids Category compliance: parental gate before purchases, settings and external links; no third-party analytics or ads; privacy nutrition labels. Write docs/IOS_RELEASE_CHECKLIST.md.

PHASE 8 - VERIFY
Android: build + all tests green. iOS: build and run on the iPhone and iPad simulators; walk the main flows (onboarding -> parent gate -> child profile -> map -> surah player with loop per ayah/surah -> game -> reward) in English and Arabic. Report anything that differs from the design canvas.

Start with PHASE 1 now.
```
