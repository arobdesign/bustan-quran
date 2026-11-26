# Bustan Qur'an — Play Store & Families Release Checklist (P-16)

## 1. Google Play Families & Target Audience
- [x] Target audience: Under 13 (3–10 years old).
- [x] **No Ads SDK**, no Advertising ID (`AD_ID` permission absent in merged manifest).
- [x] **No Social / Chat** features.
- [x] **ParentGate** (verbal math in EN/AR/FR/DE) guards all parent-only actions: `ParentDashboard`, `Settings`, `Paywall`, `AddChild`, external links.
- [x] **Offline-first**: all core learning, mushaf, Juz 'Amma audio, and 18 mini-games work offline.

## 2. Data Safety Form Answers
- **Child nickname & age stage**: Collected only locally or synced if parent explicitly signs in; never logged to Logcat or Crashlytics.
- **Voice recordings**: Stored locally in app-internal storage (AAC 32 kbps); auto-deleted after 90 days unless marked Keep; cloud backup **off by default** (`backupRecordings = false`).
- **Analytics**: Optional & **off by default** (`analyticsOptIn = false`); zero child PII.
- **Account deletion**: Export JSON & Delete All Data available in `SettingsScreen` (ACC-05, <= 30 days server + immediate local wipe).

## 3. Store Listing & Localization
- [x] `en`, `ar` (RTL), `fr`, `de` strings & voice-instruction keys complete.
- [x] Content rating: Everyone / PEGI 3 / USK 0.

## 4. App Size & Build Verification
- [x] R8 minification & resource shrinking enabled for release builds (`proguard-rules.pro`).
- [ ] Target AAB size: `<= 150 MB` (with bundled Juz 30 child audio).

## 5. Staged Rollout Plan
1. **10% rollout** — monitor Crashlytics crash-free rate (`>= 99.5%`) for 48 hours.
2. **50% rollout** — verify billing `POST /v1/purchases/verify` & RTDN webhook latency.
3. **100% rollout** — full release.
