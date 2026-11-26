# tools/scripts

## `fetch_quran_content.py`

Mengisi `core/data/src/androidMain/assets/content.db` dengan konten asli dari
**eQuran.id API v2** — endpoint yang sama dipakai aplikasi `Apps SakuSantri`
(`/surat`, `/surat/{n}`).

| Kolom `content.db`        | Sumber API            |
| ------------------------- | --------------------- |
| `ayah.text_uthmani`       | `teksArab`            |
| `ayah.text_simple`        | `teksLatin`           |
| `translation.text_kids`   | `teksIndonesia` (`lang = 'id'`) |
| `meta.audio_template`     | pola URL `audio["05"]` (MP3 per ayat, Misyari Rasyid Al-Afasi) |

Cara pakai:

```bash
python3 tools/scripts/fetch_quran_content.py            # tulis ulang content.db
python3 tools/scripts/fetch_quran_content.py --check     # hanya validasi
python3 tools/scripts/fetch_quran_content.py --refresh   # abaikan cache HTTP
```

Catatan:

- Skema tabel **tidak** diubah; Room tetap versi 1 sehingga tidak perlu migrasi.
  Skrip hanya mengganti baris untuk surah yang diimpor.
- Surah yang diimpor = Al-Fatihah + seluruh juz 30 (78–114), yaitu yang dapat
  dijangkau alur anak (`QuranLibraryViewModel` memfilter `juz_start = 30`).
- Nomor halaman mushaf (`ayah.page`) diambil dari peta 604 halaman Madani di
  dalam skrip, karena API tidak menyediakannya.
- Cache HTTP ditulis ke `tools/scripts/.cache/` saat dijalankan; aman dihapus.
- Setelah mengimpor, jalankan `./gradlew :app:assembleDebug` agar aset baru ikut
  masuk APK. Aplikasi yang sudah terpasang perlu di-uninstall dulu — Room hanya
  menyalin `createFromAsset` saat database pertama kali dibuat.
