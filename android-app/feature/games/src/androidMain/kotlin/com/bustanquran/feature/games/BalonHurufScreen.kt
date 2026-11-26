package com.bustanquran.feature.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.HudHudMascot
import com.bustanquran.core.ui.theme.BustanBlueArtMid
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanGreenArt
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanInkGhost
import com.bustanquran.core.ui.theme.BustanLilacArtEdge
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanSkyLight
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Artboard 03 · `BalonHuruf.dc.html` — five balloons on a sky background, the
 * guide bird at the bottom and a 64 dp speaker that replays the letter.
 *
 * Letter glyphs come from `game_content` (content.db); the placeholders below
 * are the design's own five letters and are only used until the database is
 * bound, exactly like the previous structural placeholders.
 */

private val BalloonLetters = listOf("ب", "ت", "ث", "ن", "ي")

private data class BalloonSlot(
    val x: Dp,
    val y: Dp,
    val width: Dp,
    val height: Dp,
    val stringHeight: Dp,
    val fill: Color,
    val contentColor: Color,
    val ring: Boolean = false,
)

private val BalloonSlots = listOf(
    BalloonSlot(30.dp, 110.dp, 96.dp, 116.dp, 70.dp, BustanCoral, BustanWhite),
    BalloonSlot(148.dp, 30.dp, 104.dp, 126.dp, 80.dp, BustanSaffron, BustanInk, ring = true),
    BalloonSlot(268.dp, 150.dp, 96.dp, 116.dp, 70.dp, BustanBlueArtMid, BustanWhite),
    BalloonSlot(90.dp, 290.dp, 96.dp, 116.dp, 60.dp, BustanGreenArt, BustanWhite),
    BalloonSlot(216.dp, 330.dp, 96.dp, 116.dp, 60.dp, BustanLilacArtEdge, BustanWhite),
)

@Composable
fun BalonHurufScreen(
    questions: List<GameQuestion> = emptyList(),
    onFinished: () -> Unit = {},
    onSpeakLetter: (String) -> Unit = {}
) {
    val activeQuestions = remember(questions) {
        questions.ifEmpty {
            BalloonLetters.mapIndexed { index, letter ->
                GameQuestion(
                    id = "q${index + 1}",
                    prompt = letter,
                    promptArabic = letter,
                    correctAnswer = letter,
                    distractors = BalloonLetters.filter { it != letter }.take(4),
                    hintText = "",
                )
            }
        }
    }
    var state by remember {
        mutableStateOf(
            GameUiState(
                gameId = "balon_huruf",
                gameTitle = "Letter Balloons",
                level = 1,
                questions = activeQuestions,
                startTimeMs = System.currentTimeMillis(),
            )
        )
    }
    val dispatch: (GameEvent) -> Unit = { event -> state = reduceGameEvent(state, event) }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    val question = state.currentQuestion

    Column(modifier = Modifier.fillMaxSize().background(BustanSkyLight)) {
        BalloonHeader(round = state.round, total = state.total, level = state.level, onBack = onFinished)

        if (question != null) {
            PromptCard(
                letterName = question.prompt.ifBlank { question.correctAnswer },
                letter = question.promptArabic,
                onSpeak = { onSpeakLetter(question.correctAnswer) },
            )
        }
        BalloonField(
            options = question?.let { q -> listOf(q.correctAnswer) + q.distractors } ?: emptyList(),
            enabled = state.lastAnswerCorrect != true,
            modifier = Modifier.weight(1f),
            onPick = { letter -> dispatch(GameEvent.AnswerSelected(letter)) },
        )

        GuideBubble(
            letterName = question?.prompt?.ifBlank { question.correctAnswer }.orEmpty(),
            hint = question?.takeIf { state.showHint }?.hintText.orEmpty(),
        )

    }
}

/** Back tile, round dots and level pill, `padding: 48px 20px 8px`. */
@Composable
private fun BalloonHeader(round: Int, total: Int, level: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // The design's tile is 48 dp with radius 16 and no shadow.
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BustanWhite)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BustanIcons.ChevronLeft,
                contentDescription = stringResource(com.bustanquran.core.ui.R.string.back),
                tint = BustanInk,
                modifier = Modifier.size(24.dp),
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val dots = total.coerceIn(1, 5)
            repeat(dots) { index ->
                val done = index < round - 1
                val current = index == round - 1
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                done -> BustanTeal
                                current -> BustanSaffron
                                else -> BustanWhite
                            },
                        )
                        .then(if (current) Modifier.border(3.dp, BustanWhite, CircleShape) else Modifier),
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(BustanShapePill)
                .background(BustanWhite)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.balon_level, level),
                style = BustanTextStyles.label,
                color = BustanInk,
            )
        }
    }
}

/** White 28 dp card: 64 dp speaker, instruction, 60 dp letter tile. */
@Composable
private fun PromptCard(letterName: String, letter: String, onSpeak: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(BustanWhite)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(BustanTeal)
                .clickable(onClick = onSpeak),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BustanIcons.Speaker,
                contentDescription = stringResource(R.string.balon_speaker_desc),
                tint = BustanWhite,
                modifier = Modifier.size(32.dp),
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.balon_prompt, letterName),
                style = BustanTextStyles.titleMedium,
                color = BustanInk,
            )
            Text(
                text = stringResource(R.string.balon_listen_hint),
                style = BustanTextStyles.caption,
                color = BustanMuted,
            )
        }
        Box(
            modifier = Modifier
                .size(60.dp)
                // No `clip`: the Amiri glyph's ink box is taller than the tile
                // and would otherwise be cut off, unlike the design's CSS box.
                .background(BustanButter, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = letter,
                // 32 sp instead of the design's 40: the Amiri ink box is ~1.4×
                // the font size, and the 60 dp tile clips the overflow of the
                // card. 32 sp keeps the glyph whole inside the tile.
                style = BustanTextStyles.quranBody.copy(fontSize = 32.sp),
                color = BustanInk,
                modifier = Modifier.offset(y = (-6).dp),
            )
        }
    }
}

/**
 * The balloon area. Positions are the design's absolute coordinates inside a
 * 390×500 canvas, scaled to the available width so tablets keep the layout.
 */
@Composable
private fun BalloonField(
    options: List<String>,
    enabled: Boolean,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth().clipToBounds()) {
        val scale = maxWidth / 390.dp
        Box(
            modifier = Modifier
                .width(390.dp)
                .height(500.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                },
        ) {
            // Two soft clouds, `opacity: .8`.
            Box(
                modifier = Modifier
                    .offset(x = 40.dp, y = 40.dp)
                    .size(width = 90.dp, height = 34.dp)
                    .clip(BustanShapePill)
                    .background(BustanWhite.copy(alpha = 0.8f)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-30).dp, y = 120.dp)
                    .size(width = 70.dp, height = 26.dp)
                    .clip(BustanShapePill)
                    .background(BustanWhite.copy(alpha = 0.8f)),
            )
            // The design puts the right answer on the second balloon ("Ba").
            val ordered = if (options.size > 1) {
                listOf(options[1], options[0]) + options.drop(2)
            } else {
                options
            }
            ordered.take(BalloonSlots.size).forEachIndexed { index, letter ->
                Balloon(
                    slot = BalloonSlots[index],
                    letter = letter,
                    enabled = enabled,
                    onClick = { onPick(letter) },
                )
            }
        }
    }
}

@Composable
private fun BoxScope.Balloon(
    slot: BalloonSlot,
    letter: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(
        topStartPercent = 50,
        topEndPercent = 50,
        bottomEndPercent = 48,
        bottomStartPercent = 48,
    )
    Column(
        modifier = Modifier
            .offset(x = slot.x, y = slot.y)
            .width(slot.width)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(width = slot.width, height = slot.height)
                .clip(shape)
                .background(slot.fill)
                .then(if (slot.ring) Modifier.border(6.dp, BustanWhite, shape) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = letter,
                style = BustanTextStyles.quranBody.copy(
                    fontSize = if (slot.ring) 58.sp else 52.sp,
                    lineHeight = if (slot.ring) 90.sp else 82.sp,
                ),
                color = slot.contentColor,
            )
        }
        BalloonString(height = slot.stringHeight)
    }
}

/** The thin curved string under each balloon (`stroke: #7D8A85`). */
@Composable
private fun BalloonString(height: Dp) {
    Canvas(modifier = Modifier.width(20.dp).height(height)) {
        val path = Path().apply {
            moveTo(10f, 0f)
            quadraticTo(2f, size.height * 0.28f, 10f, size.height * 0.5f)
            quadraticTo(18f, size.height * 0.75f, 10f, size.height)
        }
        drawPath(
            path = path,
            color = BustanInkGhost,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

/** Hud-hud plus the white guidance bubble (`radius 20 20 20 4`). */
@Composable
private fun GuideBubble(letterName: String, hint: String) {
    val message = hint.ifBlank {
        if (letterName.isBlank()) {
            stringResource(R.string.balon_listen_hint)
        } else {
            stringResource(R.string.balon_mascot_hint, letterName)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HudHudMascot(size = 84.dp)
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 30.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 4.dp))
                .background(BustanWhite)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(
                text = message,
                style = BustanTextStyles.labelLarge,
                color = BustanInk,
            )
        }
    }
}


