package com.bustanquran.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.bustanquran.core.data.repository.ActiveProfile
import com.bustanquran.feature.games.LearningSessionRecorder
import com.bustanquran.feature.onboarding.AddChildScreen
import com.bustanquran.feature.onboarding.ForgotPasswordScreen
import com.bustanquran.feature.onboarding.LoadingScreen
import com.bustanquran.feature.onboarding.MainScreen
import com.bustanquran.feature.onboarding.OnboardingScreen
import com.bustanquran.feature.onboarding.OnboardingViewModel
import com.bustanquran.feature.onboarding.ParentGateScreen
import com.bustanquran.feature.onboarding.PrivacyConsentScreen
import com.bustanquran.feature.onboarding.SignInScreen
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bustanquran.feature.billing.PaywallScreen
import com.bustanquran.feature.billing.PurchaseSuccessScreen
import com.bustanquran.feature.billing.RestorePurchaseScreen
import com.bustanquran.feature.games.BalonHurufScreen
import com.bustanquran.feature.games.CertificateScreen
import com.bustanquran.feature.games.GAyatHilangScreen
import com.bustanquran.feature.games.GBayanganScreen
import com.bustanquran.feature.games.GBintangScreen
import com.bustanquran.feature.games.GKebunHurufScreen
import com.bustanquran.feature.games.GKeluargaScreen
import com.bustanquran.feature.games.GKeretaSurahScreen
import com.bustanquran.feature.games.GKuisKisahScreen
import com.bustanquran.feature.games.GLariHijaiyahScreen
import com.bustanquran.feature.games.GPasarHurufScreen
import com.bustanquran.feature.games.GSambungHurufScreen
import com.bustanquran.feature.games.GSusunArtiScreen
import com.bustanquran.feature.games.GTebakSurahScreen
import com.bustanquran.feature.games.GUrutkanAyatScreen
import com.bustanquran.feature.games.GameListScreen
import com.bustanquran.feature.games.IkanHarakatScreen
import com.bustanquran.feature.games.KoleksiKartuScreen
import com.bustanquran.feature.games.MemoriKataScreen
import com.bustanquran.feature.games.RewardScreen
import com.bustanquran.feature.games.SambungAyatScreen
import com.bustanquran.feature.games.SusunAyatScreen
import com.bustanquran.feature.hafalan.HafalanMapScreen
import com.bustanquran.feature.hafalan.HafalanScreen
import com.bustanquran.feature.hafalan.VoiceCheckScreen
import com.bustanquran.feature.islamic.AsmaulHusnaScreen
import com.bustanquran.feature.islamic.DoaHarianScreen
import com.bustanquran.feature.islamic.HijaiyahLessonScreen
import com.bustanquran.feature.islamic.KisahNabiScreen
import com.bustanquran.feature.islamic.ModeGuruScreen
import com.bustanquran.feature.islamic.RamadanModeScreen
import com.bustanquran.feature.islamic.RukunIslamScreen
import com.bustanquran.feature.islamic.SalatGuideScreen
import com.bustanquran.feature.islamic.TajwidLessonScreen
import com.bustanquran.feature.islamic.WudhuGuideScreen
import com.bustanquran.feature.parent.AudioMissingSheet
import com.bustanquran.feature.parent.ChildRecordingsScreen
import com.bustanquran.feature.parent.DownloadManagerScreen
import com.bustanquran.feature.parent.EmptyProgressScreen
import com.bustanquran.feature.parent.IstirahatScreen
import com.bustanquran.feature.parent.OfflineStateScreen
import com.bustanquran.feature.parent.ParentDashboardScreen
import com.bustanquran.feature.parent.SettingsScreen
import com.bustanquran.feature.parent.WeeklyReportScreen
import com.bustanquran.feature.quran.home.HomeScreen
import com.bustanquran.feature.quran.home.HomeViewModel
import com.bustanquran.feature.quran.MushafPageScreen
import com.bustanquran.feature.quran.MushafSettingsSheet
import com.bustanquran.feature.quran.QuranLibraryScreen
import com.bustanquran.feature.quran.ReciterPickerSheet
import com.bustanquran.feature.quran.SurahPlayerFullScreen
import com.bustanquran.feature.quran.SurahPlayerViewModel
import com.bustanquran.feature.quran.WordDetailSheet
import com.bustanquran.feature.quran.AudioMissingSheet as QuranAudioMissingSheet

/**
 * Saved-state key carrying which parent screen the gate should unlock. Set on
 * the caller's entry right before navigating to `PARENT_GATE`.
 */
private const val KEY_PARENT_GATE_NEXT = "parentGateNext"

/**
 * Sentinel value for [KEY_PARENT_GATE_NEXT]: after the gate passes, grant the
 * child +10 minutes of learning time and return to the adventure map instead of
 * opening a parent screen.
 */
private const val PARENT_GATE_NEXT_EXTRA_TIME = "__grant_extra_screen_time__"

/**
 * Surah the adventure map's "Murottal Quraisy" tile opens; the surah library
 * always navigates with an explicit id instead.
 */
private const val DEFAULT_SURAH_ID = 106

/**
 * Records a finished round and then runs [onRecorded]. Progress, stars and
 * badges are written by [LearningSessionRecorder]; if no child is selected the
 * recorder is a no-op.
 */
@Composable
private fun rememberRoundFinisher(onRecorded: () -> Unit = {}): (String, Int, Int, Int) -> Unit {
    val recorder: LearningSessionRecorder = koinInject()
    val scope = rememberCoroutineScope()
    return { refId, correct, total, durationSec ->
        scope.launch {
            recorder.recordGameCompleted(
                gameRefId = refId,
                correct = correct,
                total = total,
                durationSec = durationSec
            )
            onRecorded()
        }
    }
}

@Composable
fun BustanQuranNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = BustanQuranDestinations.MAIN
) {
    val activeProfile: ActiveProfile = koinInject()
    // Host-scoped so the rest screen can grant extra time on the same instance
    // that is tracking the child's foreground minutes.
    val homeViewModel: HomeViewModel = koinViewModel()
    val navScope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Onboarding & Profile selection
        composable(BustanQuranDestinations.MAIN) {
            val viewModel: OnboardingViewModel = koinViewModel()
            MainScreen(
                viewModel = viewModel,
                onProfileSelected = { profileId ->
                    activeProfile.select(profileId)
                    navController.navigate(BustanQuranDestinations.HOME)
                },
                onAddChildClicked = {
                    navController.currentBackStackEntry?.savedStateHandle
                        ?.set(KEY_PARENT_GATE_NEXT, BustanQuranDestinations.ADD_CHILD)
                    navController.navigate(BustanQuranDestinations.PARENT_GATE)
                },
                onParentDashboardClicked = {
                    navController.currentBackStackEntry?.savedStateHandle
                        ?.set(KEY_PARENT_GATE_NEXT, BustanQuranDestinations.PARENT_DASHBOARD)
                    navController.navigate(BustanQuranDestinations.PARENT_GATE)
                }
            )
        }
        composable(BustanQuranDestinations.ONBOARDING) {
            val viewModel: OnboardingViewModel = koinViewModel()
            OnboardingScreen(
                viewModel = viewModel,
                onContinue = { navController.navigate(BustanQuranDestinations.PRIVACY_CONSENT) }
            )
        }
        composable(BustanQuranDestinations.PARENT_GATE) {
            val viewModel: OnboardingViewModel = koinViewModel()
            val next = navController.previousBackStackEntry?.savedStateHandle
                ?.get<String>(KEY_PARENT_GATE_NEXT)
                ?: BustanQuranDestinations.PARENT_DASHBOARD
            ParentGateScreen(
                viewModel = viewModel,
                onPassed = {
                    if (next == PARENT_GATE_NEXT_EXTRA_TIME) {
                        // Grant first, then leave the rest screen: the map reads the
                        // new allowance on resume and stays unlocked.
                        navScope.launch {
                            homeViewModel.grantExtraScreenTimeMinutes()
                            navController.popBackStack(
                                BustanQuranDestinations.ISTIRAHAT,
                                inclusive = true
                            )
                        }
                    } else {
                        navController.navigate(next) { popUpTo(BustanQuranDestinations.MAIN) }
                    }
                },
                onCancelled = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.ADD_CHILD) {
            val viewModel: OnboardingViewModel = koinViewModel()
            AddChildScreen(
                viewModel = viewModel,
                onChildAdded = { childId ->
                    activeProfile.select(childId)
                    navController.navigate(BustanQuranDestinations.MAIN) {
                        popUpTo(BustanQuranDestinations.MAIN) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.SIGN_IN) {
            val viewModel: OnboardingViewModel = koinViewModel()
            SignInScreen(
                viewModel = viewModel,
                onSignedIn = { navController.navigate(BustanQuranDestinations.MAIN) },
                onForgotPassword = { navController.navigate(BustanQuranDestinations.FORGOT_PASSWORD) },
                onLocalModeSelected = { navController.navigate(BustanQuranDestinations.MAIN) }
            )
        }
        composable(BustanQuranDestinations.FORGOT_PASSWORD) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(BustanQuranDestinations.PRIVACY_CONSENT) {
            val viewModel: OnboardingViewModel = koinViewModel()
            PrivacyConsentScreen(
                viewModel = viewModel,
                onContinue = { navController.navigate(BustanQuranDestinations.SIGN_IN) }
            )
        }
        composable(BustanQuranDestinations.LOADING) {
            LoadingScreen(onLoaded = { navController.navigate(BustanQuranDestinations.ONBOARDING) })
        }

        // Home (Adventure Map)
        composable(BustanQuranDestinations.HOME) {
            val profileId by activeProfile.profileId.collectAsState()
            HomeScreen(
                viewModel = homeViewModel,
                profileId = profileId,
                onNavigateToSurah = {
                    navController.navigate(BustanQuranDestinations.surahPlayer(DEFAULT_SURAH_ID))
                },
                onNavigateToCards = { navController.navigate(BustanQuranDestinations.KOLEKSI_KARTU) },
                onNavigateToIsland = { island ->
                    when (island) {
                        "quran_library" -> navController.navigate(BustanQuranDestinations.QURAN_LIBRARY)
                        "game_list" -> navController.navigate(BustanQuranDestinations.GAME_LIST)
                        "hijaiyah_lesson" -> navController.navigate(BustanQuranDestinations.HIJAIYAH_LESSON)
                        "doa_harian" -> navController.navigate(BustanQuranDestinations.DOA_HARIAN)
                    }
                },
                onTimeLimitReached = { navController.navigate(BustanQuranDestinations.ISTIRAHAT) }
            )
        }

        // Qur'an and Player
        composable(BustanQuranDestinations.QURAN_LIBRARY) {
            QuranLibraryScreen(
                onSurahSelected = { surahId ->
                    navController.navigate(BustanQuranDestinations.surahPlayer(surahId))
                },
                onBackClicked = { navController.popBackStack() },
                onHafalanMapClicked = { navController.navigate(BustanQuranDestinations.HAFALAN_MAP) },
            )
        }
        composable(BustanQuranDestinations.MUSHAF_PAGE) {
            MushafPageScreen(
                onSettingsClicked = { navController.navigate(BustanQuranDestinations.MUSHAF_SETTINGS) },
                onBackClicked = { navController.popBackStack() },
                onReciterClicked = { navController.navigate(BustanQuranDestinations.RECITER_PICKER) },
                onMemoriseClicked = { navController.navigate(BustanQuranDestinations.HAFALAN) }
            )
        }
        composable(
            route = BustanQuranDestinations.SURAH_PLAYER_ROUTE,
            arguments = listOf(
                navArgument(BustanQuranDestinations.ARG_SURAH_ID) { type = NavType.IntType }
            ),
        ) { backStackEntry ->
            val surahId = backStackEntry.arguments
                ?.getInt(BustanQuranDestinations.ARG_SURAH_ID)
                ?: DEFAULT_SURAH_ID
            val playerViewModel: SurahPlayerViewModel = koinViewModel()
            LaunchedEffect(surahId) { playerViewModel.load(surahId) }
            val playerUiState by playerViewModel.uiState.collectAsState()
            SurahPlayerFullScreen(
                surahName = playerUiState.surahName,
                surahNumber = playerUiState.surahNumber,
                ayahCount = playerUiState.ayahCount,
                revelation = playerUiState.revelation,
                surahNameAr = playerUiState.surahNameAr,
                ayahs = playerUiState.ayahs,
                playerState = playerUiState.playerState,
                onPlay = playerViewModel::onPlay,
                onPause = playerViewModel::onPause,
                onPrev = playerViewModel::onPrev,
                onNext = playerViewModel::onNext,
                onAyahTap = playerViewModel::onAyahTap,
                onLoopModeChange = playerViewModel::onLoopModeChange,
                onRepeatChange = playerViewModel::onRepeatChange,
                onSpeedChange = playerViewModel::onSpeedChange,
                onReciterClicked = { navController.navigate(BustanQuranDestinations.RECITER_PICKER) },
                onBackClicked = { navController.popBackStack() }
            )
            if (playerUiState.audioError != null) {
                QuranAudioMissingSheet(
                    surahName = playerUiState.surahName,
                    onDownload = playerViewModel::onRetryAudio,
                    onDismiss = playerViewModel::onAudioErrorDismiss,
                )
            }
        }
        composable(BustanQuranDestinations.WORD_DETAIL) { WordDetailSheet() }
        composable(BustanQuranDestinations.MUSHAF_SETTINGS) { MushafSettingsSheet() }
        composable(BustanQuranDestinations.RECITER_PICKER) { ReciterPickerSheet() }

        // Hafalan & Review
        composable(BustanQuranDestinations.HAFALAN) {
            HafalanScreen(
                surahName = "Surah 106",
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.HAFALAN_MAP) { HafalanMapScreen() }
        composable(BustanQuranDestinations.VOICE_CHECK) { VoiceCheckScreen() }

        // Games & Rewards
        composable(BustanQuranDestinations.GAME_LIST) {
            GameListScreen(
                onGameSelected = { gameRoute -> navController.navigate(gameRoute) },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.BALON_HURUF) {
            BalonHurufScreen(onFinished = { navController.navigate(BustanQuranDestinations.REWARD) })
        }
        composable(BustanQuranDestinations.SUSUN_AYAT) {
            SusunAyatScreen(
                onFinished = { navController.navigate(BustanQuranDestinations.REWARD) },
                onBackClicked = { navController.popBackStack() },
            )
        }
        composable(BustanQuranDestinations.IKAN_HARAKAT) {
            val finishRound = rememberRoundFinisher()
            IkanHarakatScreen(
                onRoundFinished = { correct, total, durationSec ->
                    finishRound("game_ikan_harakat", correct, total, durationSec)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.MEMORI_KATA) {
            val finishRound = rememberRoundFinisher()
            MemoriKataScreen(
                onRoundFinished = { correct, total, durationSec ->
                    finishRound("game_memori_kata", correct, total, durationSec)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.SAMBUNG_AYAT) {
            SambungAyatScreen(
                onNext = { navController.navigate(BustanQuranDestinations.REWARD) },
                onBackClicked = { navController.popBackStack() },
            )
        }
        composable(BustanQuranDestinations.REWARD) {
            RewardScreen(onContinue = { navController.popBackStack() })
        }
        composable(BustanQuranDestinations.KOLEKSI_KARTU) { KoleksiKartuScreen() }
        composable(BustanQuranDestinations.CERTIFICATE) { CertificateScreen() }

        // 13 Additional Games
        composable(BustanQuranDestinations.G_KEBUN_HURUF) {
            val finishRound = rememberRoundFinisher()
            GKebunHurufScreen(
                onRoundFinished = { correct, total, durationSec ->
                    finishRound("game_kebun_huruf", correct, total, durationSec)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.G_BAYANGAN) {
            val finishRound = rememberRoundFinisher()
            GBayanganScreen(
                onRoundFinished = { correct, total, durationSec ->
                    finishRound("game_bayangan", correct, total, durationSec)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.G_KERETA_SURAH) { GKeretaSurahScreen() }
        composable(BustanQuranDestinations.G_BINTANG) { GBintangScreen() }
        composable(BustanQuranDestinations.G_SAMBUNG_HURUF) { GSambungHurufScreen() }
        composable(BustanQuranDestinations.G_LARI_HIJAIYAH) {
            val finishRound = rememberRoundFinisher()
            GLariHijaiyahScreen(
                onRoundFinished = { correct, total, durationSec ->
                    finishRound("game_lari_hijaiyah", correct, total, durationSec)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.G_AYAT_HILANG) { GAyatHilangScreen() }
        composable(BustanQuranDestinations.G_URUTKAN_AYAT) { GUrutkanAyatScreen() }
        composable(BustanQuranDestinations.G_PASAR_HURUF) {
            val finishRound = rememberRoundFinisher()
            GPasarHurufScreen(
                onRoundFinished = { correct, total, durationSec ->
                    finishRound("game_pasar_huruf", correct, total, durationSec)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.G_TEBAK_SURAH) { GTebakSurahScreen() }
        composable(BustanQuranDestinations.G_SUSUN_ARTI) { GSusunArtiScreen() }
        composable(BustanQuranDestinations.G_KUIS_KISAH) { GKuisKisahScreen() }
        composable(BustanQuranDestinations.G_KELUARGA) { GKeluargaScreen() }

        // Parent Area
        composable(BustanQuranDestinations.PARENT_DASHBOARD) {
            ParentDashboardScreen(
                onNavigateToSettings = { navController.navigate(BustanQuranDestinations.SETTINGS) },
                onNavigateToWeeklyReport = { navController.navigate(BustanQuranDestinations.WEEKLY_REPORT) },
                onNavigateToDownloads = { navController.navigate(BustanQuranDestinations.DOWNLOAD_MANAGER) },
                onNavigateToRecordings = { navController.navigate(BustanQuranDestinations.CHILD_RECORDINGS) },
                onAddChild = { navController.navigate(BustanQuranDestinations.ADD_CHILD) },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.WEEKLY_REPORT) { WeeklyReportScreen() }
        composable(BustanQuranDestinations.CHILD_RECORDINGS) {
            ChildRecordingsScreen(onBackClicked = { navController.popBackStack() })
        }
        composable(BustanQuranDestinations.SETTINGS) {
            SettingsScreen(
                onBackClicked = { navController.popBackStack() },
                onDataDeleted = {
                    navController.navigate(BustanQuranDestinations.ONBOARDING) {
                        // Nothing is left to go back to once the device is wiped.
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
            )
        }
        composable(BustanQuranDestinations.EMPTY_PROGRESS) { EmptyProgressScreen() }
        composable(BustanQuranDestinations.ISTIRAHAT) {
            IstirahatScreen(
                onParentUnlock = {
                    navController.currentBackStackEntry?.savedStateHandle
                        ?.set(KEY_PARENT_GATE_NEXT, PARENT_GATE_NEXT_EXTRA_TIME)
                    navController.navigate(BustanQuranDestinations.PARENT_GATE)
                }
            )
        }
        composable(BustanQuranDestinations.DOWNLOAD_MANAGER) { DownloadManagerScreen() }
        composable(BustanQuranDestinations.OFFLINE_STATE) { OfflineStateScreen() }
        composable(BustanQuranDestinations.AUDIO_MISSING) { AudioMissingSheet() }

        // Billing
        composable(BustanQuranDestinations.PAYWALL) {
            PaywallScreen(
                onPurchaseSuccess = { navController.navigate(BustanQuranDestinations.PURCHASE_SUCCESS) },
                onRestoreClicked = { navController.navigate(BustanQuranDestinations.RESTORE_PURCHASE) },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(BustanQuranDestinations.PURCHASE_SUCCESS) { PurchaseSuccessScreen() }
        composable(BustanQuranDestinations.RESTORE_PURCHASE) { RestorePurchaseScreen() }

        // Islamic & Extras
        composable(BustanQuranDestinations.HIJAIYAH_LESSON) {
            HijaiyahLessonScreen(
                onBackClicked = { navController.popBackStack() },
            )
        }
        composable(BustanQuranDestinations.TAJWID_LESSON) {
            TajwidLessonScreen(
                onBackClicked = { navController.popBackStack() },
            )
        }
        composable(BustanQuranDestinations.DOA_HARIAN) { DoaHarianScreen() }
        composable(BustanQuranDestinations.KISAH_NABI) { KisahNabiScreen() }
        composable(BustanQuranDestinations.MODE_GURU) { ModeGuruScreen() }
        composable(BustanQuranDestinations.ASMAUL_HUSNA) { AsmaulHusnaScreen() }
        composable(BustanQuranDestinations.WUDHU_GUIDE) { WudhuGuideScreen() }
        composable(BustanQuranDestinations.SALAT_GUIDE) { SalatGuideScreen() }
        composable(BustanQuranDestinations.RUKUN_ISLAM) { RukunIslamScreen() }
        composable(BustanQuranDestinations.RAMADAN_MODE) { RamadanModeScreen() }
    }
}
