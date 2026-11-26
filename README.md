# Bustan Qur'an (بستان القرآن) — Dokumen Develop

Aplikasi Android & iOS (satu kode Kotlin Multiplatform) offline-first untuk anak 3–10 tahun (Timur Tengah & Eropa): hijaiyah, hafalan Juz 'Amma dengan loop per ayat / per surat, 24 mini game, dan dashboard orang tua.

## Isi folder

| File | Isi |
| --- | --- |
| `PRD-Bustan-Quran.md` | PRD lengkap A–Z (19 bagian), format Markdown |
| `PRD-Bustan-Quran.docx` | PRD yang sama, format Word |
| `CLAUDE.md` | Master context (P-00) untuk AI coding. Letakkan di root repo Android |
| `prompts/` | Prompt develop P-00 s.d. P-17 (P-17 = migrasi ke Kotlin Multiplatform + iOS) + prompt desain |
| `assets/arsitektur.png` | Diagram arsitektur |

## Cara pakai prompt

1. Buat repo Android baru, salin `CLAUDE.md` ke root repo (atau `.cursorrules` untuk Cursor).
2. Jalankan prompt berurutan sesuai sprint (PRD bagian 17): P-01 → P-16, satu prompt per tugas.
3. Review dan uji hasil setiap prompt sebelum lanjut ke prompt berikutnya.

## Tautan online

- PRD (bisa diedit & dikomentari): https://claude.ai/artifact/5jo9rK1L8L3mXTthxnV4Qa
- Desain UI 71 layar: https://claude.ai/artifact/3Go3zN69CsHLcX8fqBoSXd
- Spesifikasi produk: https://claude.ai/artifact/GQtUvAtApU3i2NSayDW6S2

Catatan: file di folder ini adalah salinan per 25 Nov 2026 (versi Android & iOS). Bila PRD online diubah, ekspor ulang.
