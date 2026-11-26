# P-15 — Sinkronisasi & lokalisasi FR/DE

1. Implement SyncWorker and POST /v1/sync per PRD sections 9-10: push rows changed since lastSync, pull server changes, conflict rules (stars/memorisation status = max, settings = latest updatedAt), batches of 500, only when a parent account exists. Recordings upload only if backupRecordings = true, to a private per-family bucket.
2. Complete fr and de strings and voice-instruction keys; run screenshot tests for every screen in en, ar, fr, de.
