package com.bustanquran.shared.navigation

/**
 * Route names matching the PRD screen map artboard names (PRD Section 4)
 */
object BustanQuranDestinations {
    // 1-10 Main Navigation & Core Child Flow
    const val MAIN = "main"                         // Artboard: Main (Pilih profil anak)
    const val HOME = "home"                         // Artboard: Home (Peta Petualangan)
    const val BALON_HURUF = "balon_huruf"           // Artboard: BalonHuruf
    const val SUSUN_AYAT = "susun_ayat"             // Artboard: SusunAyat
    const val HAFALAN = "hafalan"                   // Artboard: Hafalan (3 langkah)
    const val REWARD = "reward"                     // Artboard: Reward
    const val PARENT_DASHBOARD = "parent_dashboard" // Artboard: ParentDashboard
    const val SURAH_PLAYER = "surah_player"         // Artboard: MurottalQuraisy -> SurahPlayer
    const val ARG_SURAH_ID = "surahId"
    /** `surah_player/{surahId}` — the player must know which surah it opens. */
    const val SURAH_PLAYER_ROUTE = "$SURAH_PLAYER/{$ARG_SURAH_ID}"
    const val ONBOARDING = "onboarding"             // Artboard: Onboarding
    const val PARENT_GATE = "parent_gate"           // Artboard: ParentGate

    // 11-20 Onboarding, Settings & Quran
    const val ADD_CHILD = "add_child"               // Artboard: AddChild
    const val PAYWALL = "paywall"                   // Artboard: Paywall
    const val SETTINGS = "settings"                 // Artboard: Settings
    const val QURAN_LIBRARY = "quran_library"       // Artboard: QuranLibrary
    const val HAFALAN_MAP = "hafalan_map"           // Artboard: HafalanMap
    const val HIJAIYAH_LESSON = "hijaiyah_lesson"   // Artboard: HijaiyahLesson
    const val TAJWID_LESSON = "tajwid_lesson"       // Artboard: TajwidLesson (v1.1)
    const val GAME_LIST = "game_list"               // Artboard: GameList
    const val IKAN_HARAKAT = "ikan_harakat"         // Artboard: IkanHarakat
    const val MEMORI_KATA = "memori_kata"           // Artboard: MemoriKata

    // 21-30 Additional Features & Account
    const val SAMBUNG_AYAT = "sambung_ayat"         // Artboard: SambungAyat
    const val DOA_HARIAN = "doa_harian"             // Artboard: DoaHarian (v1.1)
    const val KISAH_NABI = "kisah_nabi"             // Artboard: KisahNabi (v1.1)
    const val KOLEKSI_KARTU = "koleksi_kartu"       // Artboard: KoleksiKartu
    const val ISTIRAHAT = "istirahat"               // Artboard: Istirahat
    const val MODE_GURU = "mode_guru"               // Artboard: ModeGuru (v2)
    const val SIGN_IN = "sign_in"                   // Artboard: SignIn
    const val FORGOT_PASSWORD = "forgot_password"   // Artboard: ForgotPassword
    const val PRIVACY_CONSENT = "privacy_consent"   // Artboard: PrivacyConsent
    const val PURCHASE_SUCCESS = "purchase_success" // Artboard: PurchaseSuccess

    // 31-44 System, Sheets, & Mushaf Details
    const val RESTORE_PURCHASE = "restore_purchase" // Artboard: RestorePurchase
    const val WEEKLY_REPORT = "weekly_report"       // Artboard: WeeklyReport
    const val CHILD_RECORDINGS = "child_recordings" // Artboard: ChildRecordings
    const val CERTIFICATE = "certificate"           // Artboard: Certificate (v1.1)
    const val MUSHAF_PAGE = "mushaf_page"           // Artboard: MushafPage
    const val WORD_DETAIL = "word_detail"           // Artboard: WordDetail
    const val MUSHAF_SETTINGS = "mushaf_settings"   // Artboard: MushafSettings
    const val RECITER_PICKER = "reciter_picker"     // Artboard: ReciterPicker
    const val DOWNLOAD_MANAGER = "download_manager" // Artboard: DownloadManager
    const val OFFLINE_STATE = "offline_state"       // Artboard: OfflineState
    const val AUDIO_MISSING = "audio_missing"       // Artboard: AudioMissing
    const val EMPTY_PROGRESS = "empty_progress"     // Artboard: EmptyProgress
    const val LOADING = "loading"                   // Artboard: Loading

    // 45-57 13 Additional Games
    const val G_KEBUN_HURUF = "g_kebun_huruf"       // Artboard: GKebunHuruf
    const val G_BAYANGAN = "g_bayangan"             // Artboard: GBayangan
    const val G_KERETA_SURAH = "g_kereta_surah"     // Artboard: GKeretaSurah
    const val G_BINTANG = "g_bintang"               // Artboard: GBintang
    const val G_SAMBUNG_HURUF = "g_sambung_huruf"   // Artboard: GSambungHuruf
    const val G_LARI_HIJAIYAH = "g_lari_hijaiyah"   // Artboard: GLariHijaiyah
    const val G_AYAT_HILANG = "g_ayat_hilang"       // Artboard: GAyatHilang
    const val G_URUTKAN_AYAT = "g_urutkan_ayat"     // Artboard: GUrutkanAyat
    const val G_PASAR_HURUF = "g_pasar_huruf"       // Artboard: GPasarHuruf
    const val G_TEBAK_SURAH = "g_tebak_surah"       // Artboard: GTebakSurah
    const val G_SUSUN_ARTI = "g_susun_arti"         // Artboard: GSusunArti
    const val G_KUIS_KISAH = "g_kuis_kisah"         // Artboard: GKuisKisah
    const val G_KELUARGA = "g_keluarga"             // Artboard: GKeluarga

    // 58-63 Advanced Islamic & Voice
    const val ASMAUL_HUSNA = "asmaul_husna"         // Artboard: AsmaulHusna (v2)
    const val WUDHU_GUIDE = "wudhu_guide"           // Artboard: WudhuGuide (v2)
    const val SALAT_GUIDE = "salat_guide"           // Artboard: SalatGuide (v2)
    const val RUKUN_ISLAM = "rukun_islam"           // Artboard: RukunIslam (v2)
    const val RAMADAN_MODE = "ramadan_mode"         // Artboard: RamadanMode (v2)
    const val VOICE_CHECK = "voice_check"           // Artboard: VoiceCheck (v2)

    /** Concrete route for the player, e.g. `surahPlayer(112)` -> `surah_player/112`. */
    fun surahPlayer(surahId: Int): String = "$SURAH_PLAYER/$surahId"
}
