package com.bustanquran.feature.hafalan

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.theme.BustanAmberDeepText
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanCoralDeepText
import com.bustanquran.core.ui.theme.BustanCoralRing
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanSandTint
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDeep
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Artboard 16 · `HafalanMap.dc.html` — "My hafalan".
 *
 * Three blocks: the back tile plus title, the teal review box with its queue
 * rows, and the white card holding the 7-column grid for juz 'Amma with its
 * legend. Surah numbers come from `content.db` (juz 30 runs 78–114); the
 * per-child [HafalanStatus] is supplied by the caller, because progress lives in
 * `user.db` rather than in the content fixture.
 */

/** One row of the "Today's review box"; [label] is a whole, translated task. */
data class ReviewTask(val label: String, val done: Boolean = false)

private val MapCardShape = RoundedCornerShape(22.dp)
private val MapTaskShape = RoundedCornerShape(14.dp)
private val MapCellShape = RoundedCornerShape(10.dp)
private val MapSwatchShape = RoundedCornerShape(4.dp)

/** The 37 cells of juz 'Amma, newest surah first, all "not yet". */
val JuzAmmaCells: List<SurahMapItem> = (114 downTo 78).map { number ->
    SurahMapItem(number = number, nameEn = "", status = HafalanStatus.BELUM)
}

@Composable
fun HafalanMapScreen(
    surahs: List<SurahMapItem> = JuzAmmaCells,
    reviewTasks: List<ReviewTask> = emptyList(),
    onReviewTaskClicked: (Int) -> Unit = {},
    onSurahSelected: (Int) -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    HafalanMapContent(
        surahs = surahs,
        reviewTasks = reviewTasks,
        onReviewTaskClicked = onReviewTaskClicked,
        onSurahSelected = onSurahSelected,
        onBackClicked = onBackClicked,
    )
}

@Composable
fun HafalanMapContent(
    surahs: List<SurahMapItem>,
    reviewTasks: List<ReviewTask> = emptyList(),
    onReviewTaskClicked: (Int) -> Unit = {},
    onSurahSelected: (Int) -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanSand)
            .verticalScroll(rememberScrollState())
            .padding(start = 18.dp, end = 18.dp, top = 48.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        MapHeader(onBackClicked = onBackClicked)
        if (reviewTasks.isNotEmpty()) {
            ReviewBox(tasks = reviewTasks, onTaskClicked = onReviewTaskClicked)
        }
        SurahGridCard(surahs = surahs, onSurahSelected = onSurahSelected)
    }
}

@Composable
private fun MapHeader(onBackClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BackButton(onClick = onBackClicked)
        Text(
            text = stringResource(R.string.hafalan_map_title),
            style = BustanTextStyles.titleLarge,
            color = BustanInk,
        )
    }
}

@Composable
private fun ReviewBox(tasks: List<ReviewTask>, onTaskClicked: (Int) -> Unit) {
    val doneCount = tasks.count { it.done }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MapCardShape)
            .background(BustanTeal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.hafalan_map_review_box),
                style = BustanTextStyles.titleSmall.copy(fontSize = 19.sp, lineHeight = 22.sp),
                color = BustanWhite,
            )
            Box(
                modifier = Modifier
                    .clip(BustanShapePill)
                    .background(BustanTealDeep)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(R.string.hafalan_map_review_count, doneCount, tasks.size),
                    style = BustanTextStyles.captionSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = BustanWhite,
                )
            }
        }
        tasks.forEachIndexed { index, task ->
            if (task.done) {
                DoneTaskRow(task = task)
            } else {
                PendingTaskRow(task = task, onClick = { onTaskClicked(index) })
            }
        }
    }
}

@Composable
private fun DoneTaskRow(task: ReviewTask) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MapTaskShape)
            .background(BustanTealDeep)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .defaultMinSize(minHeight = 44.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = BustanIcons.Check,
            contentDescription = stringResource(R.string.hafalan_map_task_done),
            tint = BustanSaffron,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = task.label,
            style = BustanTextStyles.labelMedium,
            color = BustanWhite,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PendingTaskRow(task: ReviewTask, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MapTaskShape)
            .background(BustanWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .defaultMinSize(minHeight = 44.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.5.dp, BustanTeal, CircleShape),
        )
        Text(
            text = task.label,
            style = BustanTextStyles.labelMediumStrong,
            color = BustanInk,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.hafalan_map_start),
            style = BustanTextStyles.captionSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = BustanTeal,
        )
    }
}

@Composable
private fun SurahGridCard(surahs: List<SurahMapItem>, onSurahSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MapCardShape)
            .background(BustanWhite)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = stringResource(R.string.hafalan_map_juz_title, surahs.size),
                style = BustanTextStyles.labelLarge,
                color = BustanInk,
            )
            Text(
                text = stringResource(R.string.hafalan_map_juz_range, JuzFirstSurah, JuzLastSurah),
                style = BustanTextStyles.captionSmall,
                color = BustanMuted,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            surahs.chunked(GridColumns).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { surah ->
                        MapCell(
                            surah = surah,
                            onClick = { onSurahSelected(surah.number) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(GridColumns - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        MapLegend()
    }
}

@Composable
private fun MapCell(surah: SurahMapItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val spec = cellSpec(surah.status)
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(MapCellShape)
            .background(spec.container)
            .then(if (spec.ring) Modifier.border(2.dp, BustanCoral, MapCellShape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = surah.number.toString(),
            style = BustanTextStyles.captionSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = spec.content,
        )
    }
}

/** The four fills of the design's status chain: memorised, review, learning, none. */
private data class CellSpec(val container: Color, val content: Color, val ring: Boolean = false)

private fun cellSpec(status: HafalanStatus): CellSpec = when (status) {
    HafalanStatus.HAFAL -> CellSpec(BustanTeal, BustanWhite)
    HafalanStatus.PERLU_DIULANG -> CellSpec(BustanCoralRing, BustanCoralDeepText, ring = true)
    HafalanStatus.BELAJAR -> CellSpec(BustanButter, BustanAmberDeepText)
    HafalanStatus.BELUM -> CellSpec(BustanSandTint, BustanMuted)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MapLegend() {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        LegendItem(
            label = stringResource(R.string.hafalan_map_legend_memorised),
            container = BustanTeal,
        )
        LegendItem(
            label = stringResource(R.string.hafalan_map_legend_review),
            container = BustanCoralRing,
            ring = true,
        )
        LegendItem(
            label = stringResource(R.string.hafalan_map_legend_learning),
            container = BustanButter,
        )
        LegendItem(
            label = stringResource(R.string.hafalan_map_legend_not_yet),
            container = BustanSandTint,
        )
    }
}

@Composable
private fun LegendItem(label: String, container: Color, ring: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(MapSwatchShape)
                .background(container)
                .then(if (ring) Modifier.border(2.dp, BustanCoral, MapSwatchShape) else Modifier),
        )
        Text(text = label, style = BustanTextStyles.captionSmall, color = BustanMuted)
    }
}

/** The design lays the map out as seven columns of equal width. */
private const val GridColumns = 7

/** Juz 'Amma spans surah 78 (An-Naba) to surah 114 (An-Nas). */
private const val JuzFirstSurah = 78
private const val JuzLastSurah = 114
