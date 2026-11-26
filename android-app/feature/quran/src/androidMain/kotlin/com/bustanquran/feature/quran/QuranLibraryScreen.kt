package com.bustanquran.feature.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.data.repository.ContentRepository
import com.bustanquran.core.ui.components.BottomNav
import com.bustanquran.core.ui.components.BottomNavDestination
import com.bustanquran.core.ui.components.BustanFilterChip
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.BustanStatusBadge
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffronDeep
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.compose.viewmodel.koinViewModel

// ─── Models ─────────────────────────────────────────────────────────────────

/**
 * One row of artboard 15. [nameAr] and [ayahCount] come from `content.db`
 * (`surah.name_ar`, `surah.ayah_count`); nothing here is typed by hand.
 */
data class SurahListItem(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val ayahCount: Int,
    val hafalanStatus: HafalanStatus,
)

enum class HafalanStatus { HAFAL, BELAJAR, BELUM, PERLU_DIULANG }

/** One ayah of the mushaf page (artboard 36). Text is always from `content.db`. */
data class AyahDisplay(
    val ayahNo: Int,
    val arabicText: String,
    val translation: String,
    val isActive: Boolean = false,
)

data class ReciterInfo(val id: String, val nameEn: String, val nameAr: String)

private val SurahNumberTileShape = RoundedCornerShape(10.dp)
private val SurahRowShape = RoundedCornerShape(16.dp)
private val SearchShape = RoundedCornerShape(14.dp)

// ─── 15 · Surah list ────────────────────────────────────────────────────────

class QuranLibraryViewModel(
    private val contentRepository: ContentRepository,
) : ViewModel() {

    val surahs: StateFlow<List<SurahListItem>> = contentRepository.getSurahList(juz = 30)
        .map { entities ->
            entities
                .sortedByDescending { it.id }
                .map { surah ->
                    SurahListItem(
                        number = surah.id,
                        nameAr = surah.nameAr,
                        nameEn = surah.nameTr,
                        ayahCount = surah.ayahCount,
                        // Per-child progress lives outside content.db; until it
                        // is wired the badge stays hidden rather than invented.
                        hafalanStatus = HafalanStatus.BELUM,
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun QuranLibraryScreen(
    onSurahSelected: (Int) -> Unit = {},
    onBackClicked: () -> Unit = {},
    onHafalanMapClicked: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
    viewModel: QuranLibraryViewModel = koinViewModel(),
) {
    val surahs by viewModel.surahs.collectAsState()
    QuranLibraryContent(
        surahs = surahs,
        onSurahSelected = onSurahSelected,
        onHafalanMapClicked = onHafalanMapClicked,
        onNavigateToRoute = onNavigateToRoute,
    )
}

@Composable
fun QuranLibraryContent(
    surahs: List<SurahListItem>,
    onSurahSelected: (Int) -> Unit = {},
    onHafalanMapClicked: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
) {
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableIntStateOf(0) }

    val visible = remember(surahs, query) {
        surahs.filter { surah ->
            query.isBlank() ||
                surah.nameEn.contains(query, ignoreCase = true) ||
                surah.nameAr.contains(query) ||
                surah.number.toString().contains(query)
        }
    }

    Column(Modifier.fillMaxSize().background(BustanSand)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 48.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.library_title),
                    style = BustanTextStyles.headlineMedium,
                    color = BustanInk,
                )
                Text(
                    text = stringResource(R.string.library_hafalan_map),
                    style = BustanTextStyles.label,
                    color = BustanWhite,
                    modifier = Modifier
                        .clip(BustanShapePill)
                        .background(BustanTeal)
                        .clickable(role = Role.Button, onClick = onHafalanMapClicked)
                        .defaultMinSize(minHeight = 40.dp)
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                )
            }

            // `box-shadow: 0 2px 0 #EADFC8` on a 14 dp white field.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SearchShape)
                    .bustanShadow(color = BustanBorderSoft, shape = SearchShape)
                    .background(BustanWhite)
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = BustanIcons.Search,
                    contentDescription = null,
                    tint = BustanMuted,
                    modifier = Modifier.size(18.dp),
                )
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = BustanTextStyles.labelLarge.copy(color = BustanInk),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.library_search_hint),
                                    style = BustanTextStyles.labelLarge,
                                    color = BustanMuted,
                                )
                            }
                            inner()
                        }
                    },
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val tabs = listOf(
                    R.string.library_tab_juz_amma,
                    R.string.library_tab_all_juz,
                    R.string.library_tab_downloaded,
                )
                tabs.forEachIndexed { index, labelRes ->
                    BustanFilterChip(
                        label = stringResource(labelRes),
                        selected = tab == index,
                        onClick = { tab = index },
                        selectedContainerColor = BustanInk,
                        selectedContentColor = BustanWhite,
                        containerColor = BustanWhite,
                        contentColor = BustanInk,
                    )
                }
            }
        }

        if (visible.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.library_empty),
                    style = BustanTextStyles.labelMedium,
                    color = BustanMuted,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(visible, key = { it.number }) { surah ->
                    SurahRow(surah = surah, onClick = { onSurahSelected(surah.number) })
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        BottomNav(
            currentRoute = BottomNavDestination.QURAN.route,
            onNavigateToRoute = onNavigateToRoute,
        )
    }
}

@Composable
private fun SurahRow(surah: SurahListItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SurahRowShape)
            .background(BustanWhite)
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(SurahNumberTileShape)
                .background(BustanMint),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = surah.number.toString(), style = BustanTextStyles.label, color = BustanTealDark)
        }

        Column(Modifier.weight(1f)) {
            Text(text = surah.nameEn, style = BustanTextStyles.labelLarge, color = BustanInk, maxLines = 1)
            Text(
                text = stringResource(R.string.library_ayat_count, surah.ayahCount),
                style = BustanTextStyles.captionSmallSoft,
                color = BustanMuted,
            )
        }

        when (surah.hafalanStatus) {
            HafalanStatus.HAFAL -> BustanStatusBadge(
                text = stringResource(R.string.library_memorised),
                containerColor = BustanMint,
                contentColor = BustanTealDark,
            )

            HafalanStatus.BELAJAR, HafalanStatus.PERLU_DIULANG -> BustanStatusBadge(
                text = stringResource(R.string.library_learning),
                containerColor = BustanButter,
                contentColor = BustanSaffronDeep,
            )

            HafalanStatus.BELUM -> Unit
        }

        Text(
            text = surah.nameAr,
            style = BustanTextStyles.quranSmall,
            color = BustanTeal,
            maxLines = 1,
            modifier = Modifier.defaultMinSize(minWidth = 64.dp),
            textAlign = TextAlign.End,
        )
    }
}

