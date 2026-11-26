package com.bustanquran.feature.islamic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanCoralText
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanTraceInk
import com.bustanquran.core.ui.theme.BustanWhite
import org.koin.compose.viewmodel.koinViewModel

/**
 * Artboard 17 · `HijaiyahLesson.dc.html` — "Letter lesson".
 *
 * The card in the middle is the tracing canvas: the letter is printed as a
 * ghost plus a teal outline, and the child's finger draws over it. The four
 * tiles underneath swap the canvas between the letter's word shapes. Glyph,
 * name, shapes, makhraj note and example word all come from `content.db`.
 */
private val HijaiyahCardShape = RoundedCornerShape(28.dp)
private val HijaiyahTileShape = RoundedCornerShape(16.dp)
private val HijaiyahExampleShape = RoundedCornerShape(18.dp)
private val HijaiyahHearShape = RoundedCornerShape(18.dp)
/** Design: 230 px Amiri glyph printed on the tracing card. */
private val HijaiyahGlyphSize = 230.sp

private val HijaiyahGhostStyle = BustanTextStyles.quranBody.copy(
    fontSize = HijaiyahGlyphSize,
    lineHeight = HijaiyahGlyphSize,
    color = BustanTraceInk,
)
/** The 34 px Amiri glyph inside a word-shape tile. */
private val HijaiyahTileGlyphStyle = BustanTextStyles.quranSmall.copy(
    fontSize = 34.sp,
    lineHeight = 36.sp,
)

/** The 30 px Amiri example word. */
private val HijaiyahExampleWordStyle = BustanTextStyles.quranSmall.copy(
    fontSize = 30.sp,
    lineHeight = 34.sp,
)

/** Tile labels in design order; resolved per locale by [stringResource]. */
private val FormLabels = listOf(
    R.string.hijaiyah_form_isolated,
    R.string.hijaiyah_form_initial,
    R.string.hijaiyah_form_medial,
    R.string.hijaiyah_form_final,
)

@Composable
fun HijaiyahLessonScreen(
    onFinished: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    onHearLetter: () -> Unit = {},
    onRecordClicked: () -> Unit = {},
    viewModel: HijaiyahLessonViewModel = koinViewModel(),
) {
    val data by viewModel.data.collectAsState()
    HijaiyahLessonContent(
        data = data,
        onFinished = onFinished,
        onBackClicked = onBackClicked,
        onHearLetter = onHearLetter,
        onRecordClicked = onRecordClicked,
    )
}

/**
 * Stateless body: the caller supplies the letters, the screen keeps the lesson
 * index, the chosen shape and the traced ink.
 */
@Composable
fun HijaiyahLessonContent(
    data: HijaiyahLessonData,
    onFinished: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    onHearLetter: () -> Unit = {},
    onRecordClicked: () -> Unit = {},
) {
    var index by remember(data) { mutableStateOf(0) }
    var formIndex by remember(data, index) { mutableStateOf(0) }
    val letter = data.letters.getOrNull(index)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanSand)
            .padding(start = 18.dp, end = 18.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        HijaiyahHeader(
            position = index + 1,
            total = data.total,
            letterName = letter?.name.orEmpty(),
            onBackClicked = onBackClicked,
        )

        if (letter == null) {
            Text(
                text = stringResource(R.string.hijaiyah_empty),
                style = BustanTextStyles.bodyMedium,
                color = BustanMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            return@Column
        }

        TraceCard(
            glyph = letter.forms.tiles[formIndex],
            shape = HijaiyahCardShape,
            modifier = Modifier.fillMaxWidth().height(330.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.hijaiyah_forms_title, letter.name),
                style = BustanTextStyles.labelMediumStrong,
                color = BustanInk,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                letter.forms.tiles.forEachIndexed { tileIndex, shape ->
                    FormTile(
                        glyph = shape,
                        label = stringResource(FormLabels[tileIndex]),
                        selected = tileIndex == formIndex,
                        onClick = { formIndex = tileIndex },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        ExampleRow(
            letter = letter,
            onHearLetter = onHearLetter,
        )

        Spacer(Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HijaiyahPill(
                label = stringResource(R.string.hijaiyah_say),
                onClick = onRecordClicked,
                filled = false,
                modifier = Modifier.weight(1f),
            )
            HijaiyahPill(
                label = stringResource(R.string.hijaiyah_next),
                onClick = {
                    if (index + 1 < data.total) {
                        index += 1
                        formIndex = 0
                    } else {
                        onFinished()
                    }
                },
                filled = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HijaiyahHeader(
    position: Int,
    total: Int,
    letterName: String,
    onBackClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BackButton(
            onClick = onBackClicked,
            shadowColor = BustanBorderSoft,
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.hijaiyah_lesson_title, position, letterName),
                style = BustanTextStyles.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = BustanInk,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.hijaiyah_lesson_subtitle),
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
        ) {
            Text(
                text = stringResource(R.string.hijaiyah_lesson_counter, position, total),
                style = BustanTextStyles.label,
                color = BustanInk,
            )
        }
    }
}

/**
 * The tracing canvas: dashed baseline, ghost glyph, teal outline, the child's
 * ink and the yellow start dot, in that order. The dashed line only shows where
 * the letter stands; the ink is scored against the glyph's own contour, so the
 * child has to follow the shape rather than swipe along the baseline.
 */
@Composable
private fun TraceCard(
    glyph: String,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val strokes = remember(glyph) { mutableStateListOf<List<Offset>>() }
    var current by remember(glyph) { mutableStateOf<List<Offset>>(emptyList()) }
    var traced by remember(glyph) { mutableStateOf(false) }
    val measurer = rememberTextMeasurer()
    val ghost = remember(glyph, measurer) {
        measurer.measure(AnnotatedString(glyph), HijaiyahGhostStyle)
    }
    val outline = rememberOutlineStyle()
    val outlineLayout = remember(glyph, measurer, outline) {
        measurer.measure(AnnotatedString(glyph), outline)
    }
    val density = LocalDensity.current
    val glyphSizePx = with(density) { HijaiyahGlyphSize.toPx() }
    // Waypoints sit on the letter's own outline, normalised to the glyph box.
    val guideBox = remember(glyph, measurer, density) {
        GlyphMaskGuide.guide(
            measurer = measurer,
            glyph = glyph,
            style = HijaiyahGhostStyle,
            density = density,
        )
    }

    Box(
        modifier = modifier
            .bustanShadow(color = BustanBorderSoft, shape = shape, offsetY = 3.dp)
            .clip(shape)
            .background(BustanWhite),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val inset = 20.dp.toPx()
            // The letters stand on this line, so it is the glyph baseline too.
            val baselineY = size.height / 2f + glyphSizePx * GlyphBaselineShare
            drawLine(
                color = BustanBorderSoft,
                start = Offset(inset, baselineY),
                end = Offset(size.width - inset, baselineY),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(8.dp.toPx(), 6.dp.toPx()),
                ),
            )
            drawCentredGlyph(ghost, baselineY)
            drawCentredGlyph(outlineLayout, baselineY)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(glyph) {
                    val tolerance = 44.dp.toPx()
                    detectDragGestures(
                        onDragStart = { start -> current = listOf(start) },
                        onDragEnd = {
                            if (current.isNotEmpty()) strokes.add(current)
                            current = emptyList()
                            val baselineY = size.height / 2f + glyphSizePx * GlyphBaselineShare
                            // Same placement as drawCentredGlyph, so a waypoint
                            // scaled into the glyph box lands on the right ink.
                            val left = (size.width - ghost.size.width) / 2f
                            val top = baselineY - ghost.firstBaseline
                            val guide = guideBox.map { point ->
                                Offset(
                                    x = left + point.x * ghost.size.width,
                                    y = top + point.y * ghost.size.height,
                                )
                            }
                            if (TracingEvaluator.isAccepted(guide, strokes.flatten(), tolerance)) {
                                traced = true
                            }
                        },
                    ) { change, _ ->
                        change.consume()
                        current = current + change.position
                    }
                },
        ) {
            strokes.forEach { points -> drawInk(points) }
            drawInk(current)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 96.dp, top = 96.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(BustanSaffron),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.hijaiyah_trace_start),
                style = BustanTextStyles.labelMediumStrong,
                color = BustanInk,
            )
        }

        Text(
            text = stringResource(
                if (traced) R.string.hijaiyah_trace_done else R.string.hijaiyah_trace_hint,
            ),
            style = BustanTextStyles.caption,
            color = if (traced) BustanTeal else BustanMuted,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp),
        )
    }
}

@Composable
private fun rememberOutlineStyle(): TextStyle {
    val density = LocalDensity.current
    return remember(density) {
        HijaiyahGhostStyle.copy(
            color = BustanTeal,
            drawStyle = Stroke(width = with(density) { 3.dp.toPx() }),
        )
    }
}

/** One finger path, drawn thick and rounded like a crayon. */
private fun DrawScope.drawInk(points: List<Offset>) {
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }
    drawPath(
        path = path,
        color = BustanTeal,
        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}

/** One of the four word-shape tiles; the selected one turns mint. */
@Composable
private fun FormTile(
    glyph: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 76.dp)
            .clip(HijaiyahTileShape)
            .background(if (selected) BustanMint else BustanWhite)
            .border(
                width = if (selected) 3.dp else 2.dp,
                color = if (selected) BustanTeal else BustanBorder,
                shape = HijaiyahTileShape,
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = glyph,
            style = HijaiyahTileGlyphStyle,
            color = BustanInk,
        )
        Text(
            text = label,
            style = BustanTextStyles.micro,
            color = if (selected) BustanInk else BustanMuted,
        )
    }
}

/** Example word plus its makhraj note, and the separate teal "hear" tile beside it. */
@Composable
private fun ExampleRow(
    letter: HijaiyahLetter,
    onHearLetter: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .bustanShadow(color = BustanBorderSoft, shape = HijaiyahExampleShape)
                .clip(HijaiyahExampleShape)
                .background(BustanWhite)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = letter.exampleWord.ifBlank { letter.glyph },
                style = HijaiyahExampleWordStyle,
                color = BustanTeal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(R.string.hijaiyah_example_heading),
                    style = BustanTextStyles.labelMediumStrong,
                    color = BustanInk,
                )
                if (letter.makhrajNote.isNotBlank()) {
                    Text(
                        text = letter.makhrajNote,
                        style = BustanTextStyles.captionSoft,
                        color = BustanMuted,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .width(64.dp)
                .fillMaxHeight()
                .clip(HijaiyahExampleShape)
                .background(BustanTeal)
                .clickable(onClick = onHearLetter),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BustanIcons.Speaker,
                contentDescription = stringResource(R.string.hijaiyah_hear, letter.name),
                tint = BustanWhite,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

/** One of the two bottom actions: teal filled, or white with a warm edge. */
@Composable
private fun HijaiyahPill(
    label: String,
    onClick: () -> Unit,
    filled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .bustanShadow(
                color = if (filled) BustanTealDark else BustanBorderSoft,
                shape = HijaiyahHearShape,
                offsetY = 3.dp,
            )
            .clip(HijaiyahHearShape)
            .background(if (filled) BustanTeal else BustanWhite)
            .border(
                width = if (filled) 0.dp else 2.dp,
                color = BustanCoral,
                shape = HijaiyahHearShape,
            )
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 54.dp)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = BustanTextStyles.buttonMedium,
            color = if (filled) BustanWhite else BustanCoralText,
            textAlign = TextAlign.Center,
        )
    }
}

