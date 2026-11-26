package com.bustanquran.shared.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bustanquran.core.data.database.model.ChildProfileEntity
import com.bustanquran.core.data.repository.ContentRepository
import com.bustanquran.core.ui.theme.BustanTheme
import com.bustanquran.feature.games.BalonHurufScreen
import com.bustanquran.feature.games.GameListScreen
import com.bustanquran.feature.games.RewardScreen
import com.bustanquran.feature.games.SambungAyatScreen
import com.bustanquran.feature.games.SusunAyatScreen
import com.bustanquran.feature.hafalan.HafalanScreen
import com.bustanquran.feature.hafalan.HafalanStep
import com.bustanquran.feature.islamic.HijaiyahLessonScreen
import com.bustanquran.feature.islamic.TajwidLessonScreen
import com.bustanquran.feature.onboarding.LoadingScreen
import com.bustanquran.feature.onboarding.MainContent
import com.bustanquran.feature.onboarding.OnboardingContent
import com.bustanquran.feature.onboarding.ParentGateContent
import com.bustanquran.feature.quran.AyahDisplay
import com.bustanquran.feature.quran.HafalanStatus
import com.bustanquran.feature.quran.MushafPageContent
import com.bustanquran.feature.quran.MushafPageViewModel
import com.bustanquran.feature.quran.MushafSettingsContent
import com.bustanquran.feature.quran.MushafSurahBlock
import com.bustanquran.feature.quran.QuranLibraryContent
import com.bustanquran.feature.quran.ReciterInfo
import com.bustanquran.feature.quran.ReciterPickerContent
import com.bustanquran.feature.quran.SurahListItem
import com.bustanquran.feature.quran.SurahPlayerFullScreen
import com.bustanquran.feature.quran.SurahPlayerViewModel
import com.bustanquran.feature.quran.WordDetailContent
import com.bustanquran.feature.quran.WordDetailUi
import com.bustanquran.feature.quran.home.HomeContent
import com.bustanquran.feature.quran.home.HomeUiState
import kotlinx.coroutines.flow.first
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Debug-only harness that renders one P-18 screen at a time so the design can
 * be compared screen by screen (`adb shell am start -n
 * <package>/com.bustanquran.shared.debug.DesignPreviewActivity --es screen home`).
 *
 * It never ships: the whole file lives in `src/debug`. Real Arabic text still
 * comes from `content.db`; only the design's sample non-Qur'an copy (names,
 * stars, point values) is supplied here.
 */
class DesignPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Full-bleed like the design files: no status or navigation bar, so a
        // 390×844 screenshot is exactly the artboard.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        val screen = intent.getStringExtra(EXTRA_SCREEN).orEmpty().ifBlank { "loading" }
        setContent {
            BustanTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    PreviewScreen(screen)
                }
            }
        }
    }

    companion object {
        const val EXTRA_SCREEN = "screen"
    }
}

@Composable
private fun PreviewScreen(screen: String) {
    when (screen) {
        "loading" -> LoadingScreen(onLoaded = {})
        "onboarding" -> OnboardingContent(
            selectedLanguage = "en",
            onSelectLanguage = {},
            onContinue = {},
        )
        "parentgate" -> ParentGateContent(
            questionText = "Seven plus five equals?",
            options = listOf(10, 12, 14),
            failedCount = 0,
            onAnswer = {},
            onCancelled = {},
        )
        "main" -> MainContent(
            profiles = SampleChildren,
            onProfileSelected = {},
            onAddChildClicked = {},
            onParentDashboardClicked = {},
        )
        "home" -> HomeContent(
            state = HomeUiState(
                nickname = "Maryam",
                stage = "5-6",
                stars = 128,
                secondsToday = 180,
                dailyGoalMinutes = 5,
            ),
        )
        "surah" -> SurahPreview()
        "games" -> GameListScreen()
        "balon" -> BalonHurufScreen()
        "susun" -> SusunAyatScreen()
        "sambung" -> SambungAyatScreen()
        "hijaiyah" -> HijaiyahLessonScreen()
        "tajwid" -> TajwidLessonScreen()
        "reward" -> RewardScreen(nickname = "Yusuf", stars = 3)
        "hafalan" -> HafalanPreview()
        "library" -> QuranLibraryPreview()
        "mushaf" -> MushafPreview()
        "mushafsettings" -> MushafSettingsContent()
        "reciter" -> ReciterPickerContent(
            reciters = listOf(
                ReciterInfo("child_boy", "Child voice · boy", ""),
                ReciterInfo("child_girl", "Child voice · girl", ""),
                ReciterInfo("adult_1", "[Adult reciter 1]", ""),
                ReciterInfo("adult_2", "[Adult reciter 2]", ""),
            ),
            selectedId = "child_boy",
        )
        "worddetail" -> WordDetailPreview()
        else -> LoadingScreen(onLoaded = {})
    }
}

private val SampleChildren = listOf(
    ChildProfileEntity(id = "1", familyId = "f", nickname = "Maryam", stage = "3-4", avatar = "camel"),
    ChildProfileEntity(id = "2", familyId = "f", nickname = "Adam", stage = "5-6", avatar = "bear"),
    ChildProfileEntity(id = "3", familyId = "f", nickname = "Yusuf", stage = "7-10", avatar = "bird"),
)
/** Uses the real view model, so the ayah text is whatever `content.db` holds. */
@Composable
private fun SurahPreview() {
    val viewModel: SurahPlayerViewModel = koinViewModel()
    LaunchedEffect(Unit) { viewModel.load(106) }
    val state by viewModel.uiState.collectAsState()
    SurahPlayerFullScreen(
        surahName = state.surahName,
        surahNumber = state.surahNumber,
        ayahCount = state.ayahCount,
        revelation = state.revelation,
        surahNameAr = SURAH_NAME_AR,
        ayahs = state.ayahs,
        playerState = state.playerState.copy(currentAyah = 2, targetRepeats = 3, currentRepeat = 1),
    )
}

/** Loads ayah 2 of Quraysh straight from the repository, no view model needed. */
@Composable
private fun HafalanPreview() {
    val repository: ContentRepository = PreviewGraph.content
    var arabic by remember { mutableStateOf("") }
    var previous by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val ayat = repository.getAyahs(106).first()
        val translations = repository.getTranslation(106, "en").first()
        arabic = ayat.firstOrNull { it.ayahNo == 2 }?.textUthmani.orEmpty()
        previous = ayat.firstOrNull { it.ayahNo == 1 }?.textUthmani.orEmpty()
        next = ayat.firstOrNull { it.ayahNo == 3 }?.textUthmani.orEmpty()
        translation = translations.firstOrNull { it.ayahNo == 2 }?.textKids.orEmpty()
    }
    HafalanScreen(
        surahName = "Al-Ikhlas",
        ayahNumber = 2,
        totalAyahs = 4,
        arabicText = arabic,
        previousAyah = previous,
        nextAyah = next,
        translation = translation,
        currentStep = HafalanStep.REPEAT,
        listenCount = 3,
        repeatCount = 1,
        repeatTarget = 3,
    )
}

/** Surah rows come from `content.db`; statuses stay hidden until progress is wired. */
@Composable
private fun QuranLibraryPreview() {
    val repository: ContentRepository = PreviewGraph.content
    var surahs by remember { mutableStateOf<List<SurahListItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        surahs = repository.getSurahList(juz = 30).first()
            .sortedByDescending { it.id }
            .map { surah ->
                SurahListItem(
                    number = surah.id,
                    nameAr = surah.nameAr,
                    nameEn = surah.nameTr,
                    ayahCount = surah.ayahCount,
                    hafalanStatus = HafalanStatus.BELUM,
                )
            }
    }
    QuranLibraryContent(surahs = surahs)
}

/** Same view model as the app, so the page falls back exactly as the app does. */
@Composable
private fun MushafPreview() {
    val viewModel: MushafPageViewModel = koinViewModel()
    LaunchedEffect(Unit) { viewModel.load() }
    val blocks by viewModel.blocks.collectAsState()
    MushafPageContent(blocks = blocks)
}

@Composable
private fun WordDetailPreview() {
    val repository: ContentRepository = PreviewGraph.content
    var word by remember { mutableStateOf<WordDetailUi?>(null) }
    LaunchedEffect(Unit) {
        val surah = repository.getSurahById(113).first()
        val tapped = repository.getWords(113, 1).first().firstOrNull()
        if (tapped != null) {
            word = WordDetailUi(
                text = tapped.text,
                translit = tapped.translit,
                surahName = surah?.nameTr.orEmpty(),
                ayahNo = 1,
            )
        }
    }
    WordDetailContent(word = word)
}


private object PreviewGraph : KoinComponent {
    val content: ContentRepository by inject()
}

/** `قريش` — the surah name in the design header, not Qur'an text. */
private const val SURAH_NAME_AR = "قريش"

