package com.bustanquran.feature.games

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanInkFaint
import com.bustanquran.core.ui.theme.BustanInkSoft
import com.bustanquran.core.ui.theme.BustanMushafGold
import com.bustanquran.core.ui.theme.BustanMushafPage
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffronGhost
import com.bustanquran.core.ui.theme.BustanSaffronStroke
import com.bustanquran.core.ui.theme.BustanSaffronWash
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite
import org.koin.compose.viewmodel.koinViewModel

/**
 * Artboard 25 · `SusunAyat.dc.html` — "Build the Ayah".
 *
 * A mushaf page shows the ayat already assembled; the current ayah keeps its
 * first words printed and leaves two dashed slots, and the tray of word chips
 * below supplies them. Every Arabic string on screen comes from `content.db`
 * through [AyatGameViewModel]; nothing is written in Kotlin.
 */
private val SusunPageShape = RoundedCornerShape(20.dp)
private val SusunInnerShape = RoundedCornerShape(14.dp)
private val SusunSlotShape = RoundedCornerShape(12.dp)
private val SusunGhostShape = RoundedCornerShape(10.dp)
private val SusunChipShape = RoundedCornerShape(16.dp)

@Composable
fun SusunAyatScreen(
    onFinished: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    viewModel: AyatGameViewModel = koinViewModel(),
) {
    val data by viewModel.data.collectAsState()
    SusunAyatContent(data = data, onFinished = onFinished, onBackClicked = onBackClicked)
}

/**
 * Stateless body: the caller supplies the ayat, the screen keeps only the words
 * the child has dropped into the slots.
 */
@Composable
fun SusunAyatContent(
    data: AyatGameData,
    onFinished: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    val state = remember(data) { buildSusunAyatState(data.surahNameAr, data.ayahs) }
    var placed by remember(state) { mutableStateOf(List<String?>(state.slots.size) { null }) }
    var message by remember(state) { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanSand)
            .padding(start = 18.dp, end = 18.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SusunHeader(
            state = state,
            surahNameTr = data.surahNameTr,
            onBackClicked = onBackClicked,
        )

        MushafPage(
            state = state,
            placed = placed,
            onSlotClicked = { index ->
                placed = placed.toMutableList().also { it[index] = null }
                message = null
            },
            modifier = Modifier.weight(1f),
        )

        if (state.hasTarget) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(
                        message ?: R.string.game_susun_instruction,
                        state.targetAyah,
                    ),
                    style = BustanTextStyles.labelMediumStrong,
                    color = if (message == R.string.game_susun_wrong) BustanInkSoft else BustanInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                WordTray(
                    options = state.options,
                    placed = placed,
                    onWordClicked = { word ->
                        val next = placed.indexOfFirst { it == null }
                        if (next >= 0) {
                            placed = placed.toMutableList().also { it[next] = word }
                            message = null
                        }
                    },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GamePill(
                        label = stringResource(R.string.game_susun_listen),
                        onClick = {},
                        filled = false,
                        icon = BustanIcons.Speaker,
                        modifier = Modifier.weight(1f),
                    )
                    GamePill(
                        label = stringResource(R.string.game_susun_check),
                        onClick = {
                            if (placed.all { it != null }) {
                                if (placed == state.answers) {
                                    message = R.string.game_susun_correct
                                    onFinished()
                                } else {
                                    message = R.string.game_susun_wrong
                                }
                            }
                        },
                        filled = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.game_susun_empty),
                style = BustanTextStyles.bodyMedium,
                color = BustanMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SusunHeader(
    state: SusunAyatState,
    surahNameTr: String,
    onBackClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BackButton(onClick = onBackClicked, size = 48.dp, shadowColor = BustanBorderSoft)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.game_susun_title),
                style = BustanTextStyles.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = BustanInk,
            )
            Text(
                text = stringResource(R.string.game_susun_subtitle, surahNameTr),
                style = BustanTextStyles.captionSmall,
                color = BustanMuted,
            )
        }
        Row(
            modifier = Modifier
                .bustanShadow(color = BustanBorderSoft, shape = BustanShapePill)
                .clip(BustanShapePill)
                .background(BustanWhite)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = BustanIcons.Star,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.game_susun_stage, state.stage, state.stageTotal),
                style = BustanTextStyles.labelLarge,
                color = BustanInk,
            )
        }
    }
}

/** The ayah being built: printed prefix, dashed word slots, then its number. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TargetAyahRow(
    state: SusunAyatState,
    placed: List<String?>,
    onSlotClicked: (Int) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SusunInnerShape)
            .background(BustanSaffronWash)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.fixedPrefix.isNotBlank()) {
            Text(
                text = state.fixedPrefix,
                style = SusunArabicStyle,
                color = BustanInk,
            )
        }
        state.slots.forEachIndexed { index, _ ->
            val word = placed.getOrNull(index)
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(46.dp)
                    .clip(SusunSlotShape)
                    .background(if (word == null) Color.Transparent else BustanWhite)
                    .dashedRoundBorder(BustanSaffronStroke, radius = 12.dp)
                    .clickable(enabled = word != null) { onSlotClicked(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = word ?: state.slots[index],
                    style = SusunSlotStyle,
                    color = if (word == null) BustanSaffronGhost else BustanInk,
                )
            }
        }
        Text(
            text = arabicAyahMarker(state.targetAyah),
            style = SusunMarkerStyle,
            color = BustanTeal,
        )
    }
}

/** The faded row of the ayah after this one: dashed boxes, no words yet. */
@Composable
private fun GhostAyahRow(widths: List<Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.45f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        widths.forEach { width ->
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .height(38.dp)
                    .clip(SusunGhostShape)
                    .dashedRoundBorder(BustanInkFaint, radius = 10.dp, width = 2.dp),
            )
        }
    }
}

/** The tray under the page: the missing words plus their distractors. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordTray(
    options: List<String>,
    placed: List<String?>,
    onWordClicked: (String) -> Unit,
) {
    val remaining = options.toMutableList()
    placed.filterNotNull().forEach { remaining.remove(it) }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        remaining.forEach { word ->
            Box(
                modifier = Modifier
                    .bustanShadow(color = BustanTeal, shape = SusunChipShape, offsetY = 4.dp)
                    .clip(SusunChipShape)
                    .background(BustanWhite)
                    .clickable { onWordClicked(word) }
                    .defaultMinSize(minHeight = 60.dp)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = word,
                    style = SusunChipStyle,
                    color = BustanInk,
                )
            }
        }
    }
}

@Composable
private fun MushafPage(
    state: SusunAyatState,
    placed: List<String?>,
    onSlotClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SusunPageShape)
            .background(BustanMushafPage)
            .border(3.dp, BustanTeal, SusunPageShape)
            .padding(6.dp),
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(SusunInnerShape)
                    .border(1.5.dp, BustanMushafGold, SusunInnerShape)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (state.surahNameAr.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(BustanShapePill)
                            .background(BustanTeal)
                            .padding(horizontal = 22.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = state.surahNameAr,
                            style = BustanTextStyles.quranSmall,
                            color = BustanWhite,
                        )
                    }
                }
                state.completed.forEach { ayah -> AyahLine(ayah) }
                if (state.hasTarget) {
                    TargetAyahRow(state = state, placed = placed, onSlotClicked = onSlotClicked)
                }
                if (state.nextAyahSlots.isNotEmpty()) {
                    GhostAyahRow(widths = state.nextAyahSlots)
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AyahLine(ayah: AyatSource) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                append(ayah.text)
                append(" ")
                withStyle(SpanStyle(color = BustanTeal)) {
                    append(arabicAyahMarker(ayah.number))
                }
            },
            style = SusunArabicStyle,
            color = BustanInk,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        if (ayah.translation.isNotBlank()) {
            Text(
                text = ayah.translation,
                style = BustanTextStyles.label,
                color = BustanMuted,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** The two 52 dp pills: "Listen" outlined, "Check" filled teal. */
@Composable
private fun GamePill(
    label: String,
    onClick: () -> Unit,
    filled: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val container = if (filled) BustanTeal else BustanWhite
    val content = if (filled) BustanWhite else BustanTeal
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .clip(BustanShapePill)
            .background(container)
            .then(
                if (filled) Modifier else Modifier.border(2.dp, BustanTeal, BustanShapePill),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(text = label, style = BustanTextStyles.labelLarge, color = content)
    }
}

/** Amiri 26/1.8 for the assembled ayat, as drawn in the design. */
private val SusunArabicStyle = BustanTextStyles.quranBody.copy(fontSize = 26.sp, lineHeight = 47.sp)

/** Amiri 24 for the words sitting in the slots. */
private val SusunSlotStyle = BustanTextStyles.quranBody.copy(fontSize = 24.sp, lineHeight = 30.sp)

/** Amiri 22 for the ayah number rosette. */
private val SusunMarkerStyle = BustanTextStyles.quranBody.copy(fontSize = 22.sp, lineHeight = 30.sp)

/** Amiri 28 for the word chips in the tray. */
private val SusunChipStyle = BustanTextStyles.quranBody.copy(fontSize = 28.sp, lineHeight = 34.sp)

/** A dashed rounded outline; Compose's `border` only draws solid strokes. */
private fun Modifier.dashedRoundBorder(
    color: Color,
    radius: Dp,
    width: Dp = 2.5.dp,
): Modifier = drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(x = radius.toPx(), y = radius.toPx()),
        style = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(8.dp.toPx(), 6.dp.toPx()),
                0f,
            ),
        ),
    )
}

