package com.bustanquran.feature.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.data.repository.ContentRepository
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.AmiriQuranFontFamily
import com.bustanquran.core.ui.theme.BalooFontFamily
import com.bustanquran.core.ui.theme.BustanBlueArt
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoralRing
import com.bustanquran.core.ui.theme.BustanDivider
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanInkScrim
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMushafBg
import com.bustanquran.core.ui.theme.BustanMushafGold
import com.bustanquran.core.ui.theme.BustanMushafPage
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanShapeCard
import com.bustanquran.core.ui.theme.BustanShapeCardSmall
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanShapeSheet
import com.bustanquran.core.ui.theme.BustanSwitchOff
import com.bustanquran.core.ui.theme.BustanTajwidGhunnah
import com.bustanquran.core.ui.theme.BustanTajwidIkhfa
import com.bustanquran.core.ui.theme.BustanTajwidMad
import com.bustanquran.core.ui.theme.BustanTajwidQalqalah
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/** One surah as it appears on a mushaf page: a gold pill plus its ayat. */
data class MushafSurahBlock(
    val surahId: Int,
    val nameAr: String,
    val nameEn: String = "",
    val ayahs: List<AyahDisplay>,
)

private val MushafPageShape = RoundedCornerShape(14.dp)
private val SettingsSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val SettingsSizeShape = RoundedCornerShape(14.dp)
private val ReciterRowShape = RoundedCornerShape(18.dp)

private val ArabicIndicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

private fun Int.toArabicIndic(): String =
    toString().map { if (it in '0'..'9') ArabicIndicDigits[it - '0'] else it }.joinToString("")


// ─── 36 · Mushaf page ───────────────────────────────────────────────────────

/**
 * Reads the surahs that share a mushaf page straight from `content.db`. Page
 * 604 of the Madani mushaf holds Al-Ikhlas (112) and Al-Falaq (113), which is
 * what the artboard shows.
 */
class MushafPageViewModel(
    private val contentRepository: ContentRepository,
) : ViewModel() {

    private val _blocks = MutableStateFlow<List<MushafSurahBlock>>(emptyList())
    val blocks: StateFlow<List<MushafSurahBlock>> = _blocks.asStateFlow()

    private var loaded = false

    fun load(surahIds: List<Int> = listOf(112, 113)) {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            val page = surahIds.mapNotNull { blockFor(it) }.filter { it.ayahs.isNotEmpty() }
            _blocks.value = if (page.isNotEmpty()) {
                page
            } else {
                // The bundled content.db is a fixture and only carries ayat for
                // a few surahs, so fall back to the first surah that has text
                // instead of showing an empty page.
                val fallback = contentRepository.getSurahList().first()
                    .firstOrNull { surah -> contentRepository.getAyahs(surah.id).first().isNotEmpty() }
                listOfNotNull(fallback?.let { blockFor(it.id) })
            }
        }
    }

    private suspend fun blockFor(surahId: Int): MushafSurahBlock? {
        val surah = contentRepository.getSurahById(surahId).first() ?: return null
        val ayat = contentRepository.getAyahs(surahId).first()
        val translations = contentRepository.getTranslation(surahId, "id").first()
            .ifEmpty { contentRepository.getTranslation(surahId, "en").first() }

        return MushafSurahBlock(
            surahId = surahId,
            nameAr = surah.nameAr,
            nameEn = surah.nameTr,
            ayahs = ayat.map { ayah ->
                AyahDisplay(
                    ayahNo = ayah.ayahNo,
                    arabicText = ayah.textUthmani,
                    translation = translations.firstOrNull { it.ayahNo == ayah.ayahNo }?.textKids.orEmpty(),
                )
            },
        )
    }
}

@Composable
fun MushafPageScreen(
    surahName: String = "",
    ayahs: List<AyahDisplay> = emptyList(),
    blocks: List<MushafSurahBlock> = emptyList(),
    juz: Int = 30,
    page: Int = 604,
    onWordTap: (wordIndex: Int, ayahNo: Int) -> Unit = { _, _ -> },
    onSettingsClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    onPlayClicked: () -> Unit = {},
    onMemoriseClicked: () -> Unit = {},
    onReciterClicked: () -> Unit = {},
    viewModel: MushafPageViewModel = koinViewModel(),
) {
    androidx.compose.runtime.LaunchedEffect(Unit) { viewModel.load() }
    val loadedBlocks by viewModel.blocks.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    val pageBlocks = when {
        blocks.isNotEmpty() -> blocks
        loadedBlocks.isNotEmpty() -> loadedBlocks
        ayahs.isNotEmpty() -> listOf(MushafSurahBlock(0, surahName, surahName, ayahs))
        else -> emptyList()
    }

    MushafPageContent(
        blocks = pageBlocks,
        juz = juz,
        page = page,
        onWordTap = onWordTap,
        onSettingsClicked = {
            onSettingsClicked()
            showSettings = true
        },
        onBackClicked = onBackClicked,
        onPlayClicked = onPlayClicked,
        onMemoriseClicked = onMemoriseClicked,
        onReciterClicked = onReciterClicked,
    )

    if (showSettings) {
        MushafSettingsSheet(onDismiss = { showSettings = false })
    }
}

@Composable
fun MushafPageContent(
    blocks: List<MushafSurahBlock>,
    juz: Int = 30,
    page: Int = 604,
    onWordTap: (wordIndex: Int, ayahNo: Int) -> Unit = { _, _ -> },
    onSettingsClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    onPlayClicked: () -> Unit = {},
    onMemoriseClicked: () -> Unit = {},
    onReciterClicked: () -> Unit = {},
) {
    var selectedWord by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Column(Modifier.fillMaxSize().background(BustanMushafBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 44.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BackButton(
                onClick = onBackClicked,
                size = 44.dp,
                containerColor = BustanWhite,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.mushaf_juz_page, juz, page),
                    style = BustanTextStyles.labelMediumStrong,
                    color = BustanInk,
                )
                val subtitle = blocks.map { it.nameEn }.filter { it.isNotBlank() }.joinToString(" · ")
                if (subtitle.isNotEmpty()) {
                    Text(text = subtitle, style = BustanTextStyles.captionSmall, color = BustanMuted)
                }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BustanWhite)
                    .clickable(role = Role.Button, onClick = onSettingsClicked),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Aa", style = BustanTextStyles.buttonSmall, color = BustanInk)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .clip(MushafPageShape)
                .border(2.dp, BustanMushafGold, MushafPageShape)
                .background(BustanMushafPage)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (blocks.isEmpty()) {
                Text(
                    text = stringResource(R.string.mushaf_empty),
                    style = BustanTextStyles.labelMedium,
                    color = BustanMuted,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
            blocks.forEachIndexed { index, block ->
                MushafSurahHeading(block = block, topSpacing = if (index == 0) 0.dp else 12.dp)
                MushafParagraph(
                    ayahs = block.ayahs,
                    selectedWord = selectedWord,
                    onWordTap = { word, ayahNo ->
                        selectedWord = ayahNo to word
                        onWordTap(word, ayahNo)
                    },
                )
            }
        }

        Text(
            text = stringResource(R.string.mushaf_hint),
            style = BustanTextStyles.captionSmall,
            color = BustanMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(BustanWhite)
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            MushafPillButton(
                text = stringResource(R.string.mushaf_child_voice),
                onClick = onReciterClicked,
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BustanTeal)
                    .clickable(role = Role.Button, onClick = onPlayClicked),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = BustanIcons.Play,
                    contentDescription = stringResource(R.string.mushaf_play_page),
                    tint = BustanWhite,
                    modifier = Modifier.size(24.dp),
                )
            }
            MushafPillButton(
                text = stringResource(R.string.mushaf_memorise),
                onClick = onMemoriseClicked,
            )
        }
    }
}


/** Gold-outlined surah-name pill, `padding: 0 24px`, 1.5 dp rule. */
@Composable
private fun MushafSurahHeading(block: MushafSurahBlock, topSpacing: Dp) {
    if (block.nameAr.isBlank()) return
    Box(
        modifier = Modifier
            .padding(top = topSpacing)
            .clip(BustanShapePill)
            .border(1.5.dp, BustanMushafGold, BustanShapePill)
            .padding(horizontal = 24.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = block.nameAr,
            style = BustanTextStyles.quranSmall,
            color = BustanTeal,
            maxLines = 1,
        )
    }
}

private data class MushafToken(
    val ayahNo: Int,
    val wordIndex: Int,
    val text: String,
    val isMarker: Boolean = false,
)

/**
 * The justified mushaf paragraph: every word is tappable, and the ayah number
 * sits in an ornate bracket. All words of the block share one flow so the text
 * wraps like a paragraph instead of one ayah per line. Word text is never typed
 * here — it comes from `content.db` through [MushafSurahBlock].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MushafParagraph(
    ayahs: List<AyahDisplay>,
    selectedWord: Pair<Int, Int>?,
    onWordTap: (word: Int, ayahNo: Int) -> Unit,
) {
    val wordStyle = BustanTextStyles.quranBody.copy(fontSize = 22.sp, lineHeight = 44.sp)
    val tokens = remember(ayahs) {
        ayahs.flatMap { ayah ->
            val words = ayah.arabicText.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            words.mapIndexed { index, word -> MushafToken(ayah.ayahNo, index, word) } +
                MushafToken(ayah.ayahNo, -1, "﴿${ayah.ayahNo.toArabicIndic()}﴾", isMarker = true)
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            tokens.forEach { token ->
                if (token.isMarker) {
                    Text(
                        text = token.text,
                        style = wordStyle,
                        color = BustanTeal,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                } else {
                    val highlighted = selectedWord == token.ayahNo to token.wordIndex
                    Text(
                        text = token.text,
                        style = wordStyle,
                        color = BustanInk,
                        modifier = Modifier
                            .padding(horizontal = 1.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .then(
                                if (highlighted) Modifier.background(BustanButter)
                                else Modifier
                            )
                            .clickable { onWordTap(token.wordIndex, token.ayahNo) }
                            .padding(horizontal = 1.dp),
                    )
                }
            }
        }
    }
}

/** 44 dp pill with a 2 dp border, used for “Child voice” and “Memorise”. */
@Composable
private fun MushafPillButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(BustanShapePill)
            .border(2.dp, BustanBorder, BustanShapePill)
            .clickable(role = Role.Button, onClick = onClick)
            .defaultMinSize(minHeight = 44.dp)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = BustanTextStyles.captionSmall, color = BustanInk, maxLines = 1)
    }
}


// ─── 38 · Reading settings ──────────────────────────────────────────────────

@Composable
fun MushafSettingsSheet(
    onDismiss: () -> Unit = {},
    onDone: () -> Unit = onDismiss,
) {
    MushafSettingsContent(
        onDone = {
            onDismiss()
            onDone()
        },
    )
}

@Composable
fun MushafSettingsContent(
    onDone: () -> Unit = {},
    initialSizeIndex: Int = 1,
    initialTajwid: Boolean = true,
) {
    var sizeIndex by remember { mutableIntStateOf(initialSizeIndex) }
    var tajwid by remember { mutableStateOf(initialTajwid) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanInk.copy(alpha = 0.45f))
            .clickable(onClick = onDone),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SettingsSheetShape)
                .background(BustanWhite)
                .clickable(enabled = false) {}
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 44.dp, height = 5.dp)
                    .clip(BustanShapePill)
                    .background(BustanBorder),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = BustanTextStyles.titleMedium,
                    color = BustanInk,
                )
                Text(
                    text = stringResource(R.string.settings_done),
                    style = BustanTextStyles.labelLarge,
                    color = BustanTeal,
                    modifier = Modifier
                        .clickable(role = Role.Button, onClick = onDone)
                        .defaultMinSize(minHeight = 44.dp)
                        .padding(vertical = 12.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.settings_text_size),
                    style = BustanTextStyles.labelMediumStrong,
                    color = BustanInk,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val sizes = listOf(20.sp, 28.sp, 36.sp)
                    sizes.forEachIndexed { index, fontSize ->
                        val selected = sizeIndex == index
                        TextSizeSample(
                            fontSize = fontSize,
                            selected = selected,
                            onClick = { sizeIndex = index },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }



            Row(
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.settings_tajwid),
                    style = BustanTextStyles.labelMediumStrong,
                    color = BustanInk,
                )
                TajwidSwitch(checked = tajwid, onCheckedChange = { tajwid = !tajwid })
            }

            if (tajwid) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TajwidLegend(stringResource(R.string.tajwid_ghunnah), BustanTajwidGhunnah, Modifier.weight(1f))
                        TajwidLegend(stringResource(R.string.tajwid_qalqalah), BustanTajwidQalqalah, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TajwidLegend(stringResource(R.string.tajwid_mad), BustanTajwidMad, Modifier.weight(1f))
                        TajwidLegend(stringResource(R.string.tajwid_ikhfa), BustanTajwidIkhfa, Modifier.weight(1f))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(BustanShapeCardSmall)
                    .background(BustanSand)
                    .padding(horizontal = 14.dp, vertical = 2.dp),
            ) {
                SettingsValueRow(stringResource(R.string.settings_transliteration), stringResource(R.string.settings_on))
                Box(Modifier.fillMaxWidth().height(1.dp).background(BustanDivider))
                SettingsValueRow(stringResource(R.string.settings_meaning), stringResource(R.string.settings_english))
                Box(Modifier.fillMaxWidth().height(1.dp).background(BustanDivider))
                SettingsValueRow(stringResource(R.string.settings_night), stringResource(R.string.settings_auto))
            }
        }
    }
}

@Composable
private fun SettingsValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = BustanTextStyles.labelMedium, color = BustanInk)
        Text(text = value, style = BustanTextStyles.labelMediumStrong, color = BustanTeal)
    }
}

@Composable
private fun TajwidLegend(label: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(14.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Text(text = label, style = BustanTextStyles.caption, color = BustanInk, maxLines = 1)
    }
}


/** 56 dp tile, 3 dp teal rule when selected, 2 dp sand rule otherwise. */
@Composable
private fun TextSizeSample(
    fontSize: TextUnit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .clip(SettingsSizeShape)
            .background(if (selected) BustanMint else BustanWhite)
            .border(
                width = if (selected) 3.dp else 2.dp,
                color = if (selected) BustanTeal else BustanBorder,
                shape = SettingsSizeShape,
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // Font-size sample only; ayah words always come from content.db.
        Text(
            text = "قُلْ",
            fontFamily = AmiriQuranFontFamily,
            fontSize = fontSize,
            color = BustanInk,
        )
    }
}

/** 52×32 track with a 24 dp knob, 4 dp from the edge. */
@Composable
private fun TajwidSwitch(checked: Boolean, onCheckedChange: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 32.dp)
            .clip(BustanShapePill)
            .background(if (checked) BustanTeal else BustanSwitchOff)
            .clickable(role = Role.Switch, onClick = onCheckedChange)
            .padding(4.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(BustanWhite))
    }
}


// ─── 39 · Choose a reciter ──────────────────────────────────────────────────

@Composable
fun ReciterPickerSheet(
    reciters: List<ReciterInfo> = emptyList(),
    selectedId: String = "",
    onSelect: (ReciterInfo) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val defaultReciters = listOf(
        ReciterInfo("child_boy", stringResource(R.string.reciter_child_boy), ""),
        ReciterInfo("child_girl", stringResource(R.string.reciter_child_girl), ""),
        ReciterInfo("adult_1", stringResource(R.string.reciter_adult_1), ""),
        ReciterInfo("adult_2", stringResource(R.string.reciter_adult_2), ""),
    )
    ReciterPickerContent(
        reciters = reciters.ifEmpty { defaultReciters },
        selectedId = selectedId.ifEmpty { defaultReciters.first().id },
        onSelect = onSelect,
        onBackClicked = onDismiss,
    )
}

@Composable
fun ReciterPickerContent(
    reciters: List<ReciterInfo> = emptyList(),
    selectedId: String = "",
    onSelect: (ReciterInfo) -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    val avatars = listOf(BustanBlueArt, BustanCoralRing, BustanMint, BustanButter)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanSand)
            .padding(start = 18.dp, end = 18.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(
                onClick = onBackClicked,
                size = 44.dp,
                containerColor = BustanWhite,
                shadowColor = BustanBorderSoft,
            )
            Text(
                text = stringResource(R.string.reciter_title),
                style = BustanTextStyles.titleLarge,
                color = BustanInk,
            )
        }

        Text(
            text = stringResource(R.string.reciter_subtitle),
            style = BustanTextStyles.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = BustanMuted,
        )

        reciters.forEachIndexed { index, reciter ->
            val selected = reciter.id == selectedId
            val note = if (reciter.id.startsWith("child")) {
                stringResource(R.string.reciter_note_child)
            } else {
                stringResource(R.string.reciter_note_adult)
            }
            ReciterRow(
                reciter = reciter,
                note = note,
                avatarColor = avatars[index % avatars.size],
                selected = selected,
                onSelect = { onSelect(reciter) },
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(R.string.reciter_footnote),
            style = BustanTextStyles.captionSmallSoft,
            color = BustanMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ReciterRow(
    reciter: ReciterInfo,
    note: String,
    avatarColor: Color,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val rowShape: Shape = ReciterRowShape
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .then(
                if (selected) Modifier.border(3.dp, BustanTeal, rowShape)
                else Modifier.bustanShadow(color = BustanBorderSoft, shape = rowShape)
            )
            .background(BustanWhite)
            .clickable(role = Role.RadioButton, onClick = onSelect)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(avatarColor))
        Column(Modifier.weight(1f)) {
            Text(
                text = reciter.nameEn,
                style = BustanTextStyles.labelLarge,
                color = BustanInk,
                maxLines = 1,
            )
            Text(text = note, style = BustanTextStyles.captionSmallSoft, color = BustanMuted, maxLines = 2)
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(2.dp, BustanTeal, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BustanIcons.Play,
                contentDescription = stringResource(R.string.reciter_sample),
                tint = BustanTeal,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}


// ─── 37 · Word detail ───────────────────────────────────────────────────────

/**
 * What the sheet needs to show for one tapped word. [text] and [translit] come
 * from the `word` table; [meaning] is null because `content.db` has no
 * dictionary yet, and the design's meaning card stays hidden until it does.
 */
data class WordDetailUi(
    val text: String,
    val translit: String,
    val surahName: String,
    val ayahNo: Int,
    val meaning: String? = null,
)

@Composable
fun WordDetailSheet(
    word: WordDetailUi? = null,
    onPlayWord: () -> Unit = {},
    onPlayAyah: () -> Unit = {},
    onAddToWords: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    WordDetailContent(
        word = word,
        onPlayWord = onPlayWord,
        onPlayAyah = onPlayAyah,
        onAddToWords = onAddToWords,
        onDismiss = onDismiss,
    )
}

@Composable
fun WordDetailContent(
    word: WordDetailUi?,
    onPlayWord: () -> Unit = {},
    onPlayAyah: () -> Unit = {},
    onAddToWords: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanMushafBg)
            .clickable(onClick = onDismiss),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(SettingsSheetShape)
                .background(BustanWhite)
                .clickable(enabled = false) {}
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 44.dp, height = 5.dp)
                    .clip(BustanShapePill)
                    .background(BustanBorder),
            )

            if (word == null) {
                Text(
                    text = stringResource(R.string.word_empty),
                    style = BustanTextStyles.labelMedium,
                    color = BustanMuted,
                )
                return@Column
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = word.text,
                    style = BustanTextStyles.quranBody.copy(fontSize = 44.sp, lineHeight = 70.sp),
                    color = BustanTeal,
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(BustanTeal)
                        .clickable(role = Role.Button, onClick = onPlayWord),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = BustanIcons.Speaker,
                        contentDescription = stringResource(R.string.word_hear),
                        tint = BustanWhite,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WordFact(
                    label = stringResource(R.string.word_say),
                    value = word.translit,
                    modifier = Modifier.weight(1f),
                )
                word.meaning?.let { meaning ->
                    WordFact(
                        label = stringResource(R.string.word_meaning),
                        value = meaning,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(BustanShapeCardSmall)
                    .background(BustanMint)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Text(
                    text = stringResource(R.string.word_context, word.surahName, word.ayahNo),
                    style = BustanTextStyles.labelMedium,
                    color = BustanTealDark,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WordActionButton(
                    text = stringResource(R.string.word_play_ayah),
                    filled = true,
                    onClick = onPlayAyah,
                    modifier = Modifier.weight(1f),
                )
                WordActionButton(
                    text = stringResource(R.string.word_add),
                    filled = false,
                    onClick = onAddToWords,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WordFact(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BustanSand)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(text = label, style = BustanTextStyles.captionSmall, color = BustanMuted)
        Text(text = value, style = BustanTextStyles.bodyMedium, color = BustanInk, maxLines = 2)
    }
}

@Composable
private fun WordActionButton(
    text: String,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(BustanShapePill)
            .background(if (filled) BustanTeal else BustanWhite)
            .border(2.dp, BustanTeal, BustanShapePill)
            .clickable(role = Role.Button, onClick = onClick)
            .defaultMinSize(minHeight = 52.dp)
            .padding(horizontal = 12.dp, vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = BustanTextStyles.labelLarge,
            color = if (filled) BustanWhite else BustanTeal,
            maxLines = 1,
        )
    }
}

