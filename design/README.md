# Desain UI Bustan Qur'an — sumber kebenaran tampilan

Folder ini adalah salinan desain dari canvas https://claude.ai/artifact/3Go3zN69CsHLcX8fqBoSXd (25 Nov 2026).

- `png/` — gambar tiap layar, resolusi 2x (1 dp = 2 px). Ini acuan visual.
- `source/` — file desain asli (`*.dc.html`). Semua ukuran, warna, radius, jarak, dan font ada di atribut `style` sebagai px CSS. **1 px CSS = 1 dp.** Ikon dan ilustrasi ada sebagai `<svg>` inline: konversi ke ImageVector / Canvas, jangan diganti ikon lain.
- `{{ ... }}`, `<sc-for>`, `<sc-if>` di file sumber adalah template: data contohnya ada di blok `<script>` (`renderVals()`).
- `source/canvas.json` — daftar layar dan ukurannya.

## Token desain

| Token | Nilai | Dipakai untuk |
| --- | --- | --- |
| sand | #FBF6EC | latar layar anak |
| parent-bg | #F4F1EA | latar area orang tua |
| ink | #23302C | teks utama |
| muted | #5B6661 | teks sekunder |
| teal | #0F6B5C | warna utama, tombol utama |
| teal-dark | #0A4F44 | bayangan tombol, teks di mint |
| mint | #DCEFE9 | badge, latar lembut |
| saffron | #F2B33D | aksen, bintang, pilihan aktif |
| butter | #FCE7B8 | latar kuning lembut |
| coral | #E0734F | rekam, peringatan lembut |
| sky | #CFE7F3 | latar peta |
| border | #E3D8C1 / #EADFC8 | garis kartu, bayangan kartu |

Font: **Baloo 2** (judul, 700–800), **Nunito** (isi, 600–800), **Baloo Bhaijaan 2** (UI Arab), **Amiri Quran** (teks Al-Qur'an di desain; di aplikasi diganti font mushaf berlisensi). Radius umum: kartu 16–28 dp, tombol pil 999 dp. Target sentuh minimum 44–64 dp.

Semua warna yang muncul di file desain: #0A4F44, #0A5A4D, #0F6B5C, #13806E, #1E2A4A, #1E7A4C, #1F4E79, #1F6FB2, #23302C, #2D3A5C, #2E4A57, #2E8B57, #2F6696, #3B4A72, #3E4743, #3E7CB1, #3E8E7E, #4A5560, #4A5A86, #4F5A55, #5B6661, #5B8DB8, #5E4628, #5FAFCB, #6B4A3E, #6E4708, #6E5520, #6FB48F, #7A4A2A, #7D8A85, #8A5A10, #8C6A3F, #8C7A5B, #8C9AA8, #8E44AD, #8E8AA8, #8F3A1C, #8FB8DE, #8FCBE0, #9ED3B5, #A79FD1, #A9B8C4, #B4532F, #B88B52, #B8913F, #B8AE97, #C0392B, #C9883A, #C98A12, #C9A65A, #C9C3E6, #CDBF9F, #CDE7DF, #CFC7B5, #CFE7F3, #D3E4DC, #D6DEE8, #D7E8F5, #D89B4A, #D8DEF0, #D9C08A, #D9CDB2, #DCEFE9, #DDF0F8, #DE937A, #E0734F, #E2A54B, #E3D8C1, #E3F1F8, #E4E0F3, #E4F2EC, #E7B96A, #E8B48C, #E8EEEC, #E9ECF6, #EADFC8, #EDE4D0, #EEE8DA, #EEF3FA, #EFE6D2, #EFE7D6, #EFF6F3, #F1ECE0, #F2B33D, #F4B9A2, #F4F1EA, #F6D58E, #F6E3C8, #F7D3C5, #F7F0E1, #FBE3D9, #FBF0D5, #FBF6EC, #FCE7B8, #FDF1EC, #FFF4D6, #FFFDF7, #FFFFFF

## Daftar layar

| No | Layar | Gambar | Sumber | Ukuran (dp) |
| --- | --- | --- | --- | --- |
| 1 | 1 · Pilih profil anak | `png/01-Pilih-profil-anak.png` | `source/Main.dc.html` | 390×844 |
| 2 | 2 · Peta Petualangan | `png/02-Peta-Petualangan.png` | `source/Home.dc.html` | 390×844 |
| 3 | 3 · Game Balon Huruf (3–4 thn) | `png/03-Game-Balon-Huruf-3-4-thn.png` | `source/BalonHuruf.dc.html` | 390×844 |
| 4 | 4 · Game Susun Ayat (7–10 thn) | `png/04-Game-Susun-Ayat-7-10-thn.png` | `source/SusunAyat.dc.html` | 390×844 |
| 5 | 5 · Hafalan & audio qari | `png/05-Hafalan-audio-qari.png` | `source/Hafalan.dc.html` | 390×844 |
| 6 | 6 · Layar hadiah | `png/06-Layar-hadiah.png` | `source/Reward.dc.html` | 390×844 |
| 7 | 7 · Dashboard orang tua | `png/07-Dashboard-orang-tua.png` | `source/ParentDashboard.dc.html` | 390×844 |
| 8 | 8 · Beranda versi Arab (RTL) | `png/08-Beranda-versi-Arab-RTL.png` | `source/HomeArabic.dc.html` | 390×844 |
| 9 | 9 · Menghafal: Surah Quraisy (loop per ayat / per surat) | `png/09-Menghafal-Surah-Quraisy-loop-per-ayat-per-surat.png` | `source/MurottalQuraisy.dc.html` | 390×844 |
| 10 | 10 · Sambutan & pilih bahasa | `png/10-Sambutan-pilih-bahasa.png` | `source/Onboarding.dc.html` | 390×844 |
| 11 | 11 · Gerbang orang tua | `png/11-Gerbang-orang-tua.png` | `source/ParentGate.dc.html` | 390×844 |
| 12 | 12 · Tambah profil anak | `png/12-Tambah-profil-anak.png` | `source/AddChild.dc.html` | 390×844 |
| 13 | 13 · Paket & harga | `png/13-Paket-harga.png` | `source/Paywall.dc.html` | 390×844 |
| 14 | 14 · Pengaturan orang tua | `png/14-Pengaturan-orang-tua.png` | `source/Settings.dc.html` | 390×844 |
| 15 | 15 · Daftar surat (Juz 'Amma) | `png/15-Daftar-surat-Juz-Amma.png` | `source/QuranLibrary.dc.html` | 390×844 |
| 16 | 16 · Peta hafalan & kotak murojaah | `png/16-Peta-hafalan-kotak-murojaah.png` | `source/HafalanMap.dc.html` | 390×844 |
| 17 | 17 · Pelajaran hijaiyah & menulis | `png/17-Pelajaran-hijaiyah-menulis.png` | `source/HijaiyahLesson.dc.html` | 390×844 |
| 18 | 18 · Pelajaran tajwid (qalqalah) | `png/18-Pelajaran-tajwid-qalqalah.png` | `source/TajwidLesson.dc.html` | 390×844 |
| 19 | 19 · Daftar game per pulau | `png/19-Daftar-game-per-pulau.png` | `source/GameList.dc.html` | 390×844 |
| 20 | 20 · Game Ikan Harakat (3–4 thn) | `png/20-Game-Ikan-Harakat-3-4-thn.png` | `source/IkanHarakat.dc.html` | 390×844 |
| 21 | 21 · Game Memori Kata (5–6 thn) | `png/21-Game-Memori-Kata-5-6-thn.png` | `source/MemoriKata.dc.html` | 390×844 |
| 22 | 22 · Game Sambung Ayat (7–10 thn) | `png/22-Game-Sambung-Ayat-7-10-thn.png` | `source/SambungAyat.dc.html` | 390×844 |
| 23 | 23 · Doa harian | `png/23-Doa-harian.png` | `source/DoaHarian.dc.html` | 390×844 |
| 24 | 24 · Kisah nabi (audio-book) | `png/24-Kisah-nabi-audio-book.png` | `source/KisahNabi.dc.html` | 390×844 |
| 25 | 25 · Koleksi kartu & lencana | `png/25-Koleksi-kartu-lencana.png` | `source/KoleksiKartu.dc.html` | 390×844 |
| 26 | 26 · Waktunya istirahat | `png/26-Waktunya-istirahat.png` | `source/Istirahat.dc.html` | 390×844 |
| 27 | 27 · Mode guru / madrasah | `png/27-Mode-guru-madrasah.png` | `source/ModeGuru.dc.html` | 390×844 |
| 28 | 28 · Masuk / daftar akun orang tua | `png/28-Masuk-daftar-akun-orang-tua.png` | `source/SignIn.dc.html` | 390×844 |
| 29 | 29 · Lupa kata sandi | `png/29-Lupa-kata-sandi.png` | `source/ForgotPassword.dc.html` | 390×844 |
| 30 | 30 · Persetujuan privasi (GDPR) | `png/30-Persetujuan-privasi-GDPR.png` | `source/PrivacyConsent.dc.html` | 390×844 |
| 31 | 31 · Pembelian berhasil | `png/31-Pembelian-berhasil.png` | `source/PurchaseSuccess.dc.html` | 390×844 |
| 32 | 32 · Pulihkan pembelian | `png/32-Pulihkan-pembelian.png` | `source/RestorePurchase.dc.html` | 390×844 |
| 33 | 33 · Laporan mingguan | `png/33-Laporan-mingguan.png` | `source/WeeklyReport.dc.html` | 390×844 |
| 34 | 34 · Rekaman suara anak | `png/34-Rekaman-suara-anak.png` | `source/ChildRecordings.dc.html` | 390×844 |
| 35 | 35 · Sertifikat khatam | `png/35-Sertifikat-khatam.png` | `source/Certificate.dc.html` | 390×844 |
| 36 | 36 · Halaman mushaf | `png/36-Halaman-mushaf.png` | `source/MushafPage.dc.html` | 390×844 |
| 37 | 37 · Detail kata (ketuk kata) | `png/37-Detail-kata-ketuk-kata.png` | `source/WordDetail.dc.html` | 390×844 |
| 38 | 38 · Pengaturan mushaf & tajwid berwarna | `png/38-Pengaturan-mushaf-tajwid-berwarna.png` | `source/MushafSettings.dc.html` | 390×844 |
| 39 | 39 · Pilih qari | `png/39-Pilih-qari.png` | `source/ReciterPicker.dc.html` | 390×844 |
| 40 | 40 · Unduhan audio per juz | `png/40-Unduhan-audio-per-juz.png` | `source/DownloadManager.dc.html` | 390×844 |
| 41 | 41 · Keadaan offline | `png/41-Keadaan-offline.png` | `source/OfflineState.dc.html` | 390×844 |
| 42 | 42 · Audio belum diunduh | `png/42-Audio-belum-diunduh.png` | `source/AudioMissing.dc.html` | 390×844 |
| 43 | 43 · Belum ada progres | `png/43-Belum-ada-progres.png` | `source/EmptyProgress.dc.html` | 390×844 |
| 44 | 44 · Layar memuat | `png/44-Layar-memuat.png` | `source/Loading.dc.html` | 390×844 |
| 45 | 45 · Game 2 Kebun Huruf | `png/45-Game-2-Kebun-Huruf.png` | `source/GKebunHuruf.dc.html` | 390×844 |
| 46 | 46 · Game 4 Pasangkan Bayangan | `png/46-Game-4-Pasangkan-Bayangan.png` | `source/GBayangan.dc.html` | 390×844 |
| 47 | 47 · Game 5 Kereta Surah | `png/47-Game-5-Kereta-Surah.png` | `source/GKeretaSurah.dc.html` | 390×844 |
| 48 | 48 · Game 8 Bintang Malam | `png/48-Game-8-Bintang-Malam.png` | `source/GBintang.dc.html` | 390×844 |
| 49 | 49 · Game 9 Sambung Huruf | `png/49-Game-9-Sambung-Huruf.png` | `source/GSambungHuruf.dc.html` | 390×844 |
| 50 | 50 · Game 10 Lari Hijaiyah | `png/50-Game-10-Lari-Hijaiyah.png` | `source/GLariHijaiyah.dc.html` | 390×844 |
| 51 | 51 · Game 11 Ayat Hilang | `png/51-Game-11-Ayat-Hilang.png` | `source/GAyatHilang.dc.html` | 390×844 |
| 52 | 52 · Game 12 Urutkan Ayat | `png/52-Game-12-Urutkan-Ayat.png` | `source/GUrutkanAyat.dc.html` | 390×844 |
| 53 | 53 · Game 15 Pasar Huruf | `png/53-Game-15-Pasar-Huruf.png` | `source/GPasarHuruf.dc.html` | 390×844 |
| 54 | 54 · Game 16 Tebak Surah | `png/54-Game-16-Tebak-Surah.png` | `source/GTebakSurah.dc.html` | 390×844 |
| 55 | 55 · Game 18 Susun Arti | `png/55-Game-18-Susun-Arti.png` | `source/GSusunArti.dc.html` | 390×844 |
| 56 | 56 · Game 21 Kuis Kisah | `png/56-Game-21-Kuis-Kisah.png` | `source/GKuisKisah.dc.html` | 390×844 |
| 57 | 57 · Game 23 Tantangan Keluarga | `png/57-Game-23-Tantangan-Keluarga.png` | `source/GKeluarga.dc.html` | 390×844 |
| 58 | 58 · Asmaul Husna | `png/58-Asmaul-Husna.png` | `source/AsmaulHusna.dc.html` | 390×844 |
| 59 | 59 · Panduan wudhu | `png/59-Panduan-wudhu.png` | `source/WudhuGuide.dc.html` | 390×844 |
| 60 | 60 · Panduan salat | `png/60-Panduan-salat.png` | `source/SalatGuide.dc.html` | 390×844 |
| 61 | 61 · Rukun Islam & iman | `png/61-Rukun-Islam-iman.png` | `source/RukunIslam.dc.html` | 390×844 |
| 62 | 62 · Mode Ramadan | `png/62-Mode-Ramadan.png` | `source/RamadanMode.dc.html` | 390×844 |
| 63 | 63 · Hasil cek bacaan (suara) | `png/63-Hasil-cek-bacaan-suara.png` | `source/VoiceCheck.dc.html` | 390×844 |
| 64 | 64 · Pilih profil (Arab) | `png/64-Pilih-profil-Arab.png` | `source/MainArabic.dc.html` | 390×844 |
| 65 | 65 · Menghafal Quraisy (Arab) | `png/65-Menghafal-Quraisy-Arab.png` | `source/MurottalArabic.dc.html` | 390×844 |
| 66 | 66 · Susun Ayat (Arab) | `png/66-Susun-Ayat-Arab.png` | `source/SusunAyatArabic.dc.html` | 390×844 |
| 67 | 67 · Dashboard orang tua (Arab) | `png/67-Dashboard-orang-tua-Arab.png` | `source/ParentDashboardArabic.dc.html` | 390×844 |
| 68 | 68 · Peta Petualangan (Prancis) | `png/68-Peta-Petualangan-Prancis.png` | `source/HomeFrench.dc.html` | 390×844 |
| 69 | 69 · Peta Petualangan (Jerman) | `png/69-Peta-Petualangan-Jerman.png` | `source/HomeGerman.dc.html` | 390×844 |
| 70 | 70 · Peta Petualangan (tablet) | `png/70-Peta-Petualangan-tablet.png` | `source/HomeTablet.dc.html` | 1194×834 |
| 71 | 71 · Menghafal Quraisy (tablet) | `png/71-Menghafal-Quraisy-tablet.png` | `source/MurottalTablet.dc.html` | 1194×834 |
