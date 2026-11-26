# P-14 — Pembelian

Implement :feature:billing with Play Billing 7+ and the Paywall, PurchaseSuccess, RestorePurchase artboards.
Products: juz_amma_lifetime (INAPP), quran_30_lifetime (INAPP), family_monthly and family_yearly (SUBS).
Flow per PRD section 10: show ProductDetails prices, launchBillingFlow behind ParentGate, call POST /v1/purchases/verify, unlock only after server confirms, acknowledge within 3 days, cache entitlement encrypted for offline, re-check every 7 days, restore via queryPurchasesAsync.
Also write the Supabase Edge Function for /v1/purchases/verify using the Google Play Developer API (service account from env), and the RTDN webhook handler for refunds/cancellations.
