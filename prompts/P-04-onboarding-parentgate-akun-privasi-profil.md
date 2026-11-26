# P-04 — Onboarding, ParentGate, akun, privasi, profil

Build :feature:onboarding to match design artboards Loading, Onboarding, ParentGate, SignIn, ForgotPassword, PrivacyConsent, AddChild, Main.
Requirements: ONB-01..03, ACC-01..05, PRF-01..03.
- ParentGate: question written in words ("Seven plus five"), 3 numeric options, new random question after a wrong answer; reusable composable + navigation guard used by all parent routes.
- Auth via Supabase (email + Google). Local-only mode allowed (ACC-03).
- PrivacyConsent stores choices with timestamp and policy version; Continue disabled until guardian checkbox.
- AddChild: nickname only, stage (3-4, 5-6, 7-10), animal avatar; enforce profile limit by plan.
Include Compose UI tests proving no parent route is reachable without passing the gate.
