# P-13 — Unduhan & keadaan sistem

Build DownloadManager and system states (OfflineState, Loading, AudioMissing).
- Fetch /v1/content/manifest; download per-juz-per-reciter zip with WorkManager (Wi-Fi only by default, resumable, progress notification), verify sha256, unzip into audio/{reciterId}/.
- Show storage used/free; delete per juz; warn when free space < 300 MB.
- Connectivity observer; friendly child-safe messages, never technical errors on child screens.
