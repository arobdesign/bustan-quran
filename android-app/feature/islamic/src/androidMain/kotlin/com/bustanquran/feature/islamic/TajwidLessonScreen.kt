package com.bustanquran.feature.islamic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.theme.BustanBlueArt
import com.bustanquran.core.ui.theme.BustanBlueArtDeep
import com.bustanquran.core.ui.theme.BustanBlueArtMid
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanCoralWash
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTajwidBg
import com.bustanquran.core.ui.theme.BustanTajwidEdge
import com.bustanquran.core.ui.theme.BustanTajwidInk
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite
import org.koin.compose.viewmodel.koinViewModel

/**
 * Artboard 18 · `TajwidLesson.dc.html` — "Qalqalah".
 *
 * Everything on the page is data: the rule's name, position, colour and
 * description come from `tajwid_rule`, the example ayah from `ayah`, and the
 * quick check's distractors from `letter`. The teaching copy lives in the
 * string resources, keyed by `tajwid_rule.description_key`.
 */
private val TajwidCardShape = RoundedCornerShape(22.dp)
private val TajwidLetterShape = RoundedCornerShape(18.dp)
private val TajwidOptionShape = RoundedCornerShape(16.dp)
private val TajwidBadgeShape = RoundedCornerShape(999.dp)
private val TajwidAyahStyle = BustanTextStyles.quranBody.copy(
    fontSize = 34.sp,
    lineHeight = 64.sp,
)
private val TajwidLetterGlyphStyle = BustanTextStyles.quranIsland
private val TajwidOptionGlyphStyle = BustanTextStyles.quranIsland.copy(fontSize = 30.sp)

/** The rule description: Nunito 15/600 in the design, one step lighter than a label. */
private val TajwidDescriptionStyle = BustanTextStyles.labelLarge.copy(
    fontWeight = FontWeight.SemiBold,
)

@Composable
fun TajwidLessonScreen(
    onBackClicked: () -> Unit = {},
    onHearExample: () -> Unit = {},
    viewModel: TajwidLessonViewModel = koinViewModel(),
) {
    val state by viewModel.data.collectAsState()
    TajwidLessonContent(
        state = state,
        onBackClicked = onBackClicked,
        onHearExample = onHearExample,
    )
}

/**
 * Stateless body: the caller supplies the rule, the screen keeps the tap on the
 * quick check. The design ends on the check card, so there is no footer action.
 */
@Composable
fun TajwidLessonContent(
    state: TajwidLessonState,
    onBackClicked: () -> Unit = {},
    onHearExample: () -> Unit = {},
) {
    var picked by remember(state.ruleName) { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanTajwidBg)
            .padding(start = 18.dp, end = 18.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BackButton(onClick = onBackClicked)
            if (!state.isEmpty) {
                Row(
                    modifier = Modifier
                        .clip(TajwidBadgeShape)
                        .background(BustanBlueArt)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = stringResource(
                            R.string.tajwid_lesson_badge,
                            state.rulePosition,
                            state.ruleTotal,
                        ),
                        style = BustanTextStyles.captionSmall,
                        color = BustanBlueArtDeep,
                    )
                }
            }
        }

        if (state.isEmpty) {
            Text(
                text = stringResource(R.string.tajwid_empty),
                style = BustanTextStyles.bodyMedium,
                color = BustanTajwidInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            return@Column
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = state.ruleName,
                style = BustanTextStyles.displayLarge,
                color = BustanInk,
            )
            Text(
                text = stringResource(ruleDescriptionRes(state.descriptionKey)),
                style = TajwidDescriptionStyle,
                color = BustanTajwidInk,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.letters.forEach { letter ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clip(TajwidLetterShape)
                        .background(BustanBlueArtMid),
                ) {
                    CentredGlyph(
                        glyph = letter,
                        style = TajwidLetterGlyphStyle,
                        color = BustanWhite,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        if (state.ruleName.equals(QalqalahRuleName, ignoreCase = true)) {
            Text(
                text = stringResource(R.string.tajwid_lesson_tip),
                style = BustanTextStyles.label,
                color = BustanBlueArtDeep,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(TajwidCardShape)
                .background(BustanWhite)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = if (state.hasExample) {
                    stringResource(
                        R.string.tajwid_example_label,
                        state.exampleSurahName,
                        state.exampleAyahNo,
                    )
                } else {
                    stringResource(R.string.tajwid_example_pending)
                },
                style = BustanTextStyles.label,
                color = BustanTajwidInk,
            )
            if (state.hasExample) {
                Text(
                    text = stopMarkedAyah(state.exampleAyah),
                    style = TajwidAyahStyle,
                    color = BustanInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(BustanShapePill)
                    .background(BustanBlueArtDeep)
                    .clickable(onClick = onHearExample)
                    .height(44.dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = BustanIcons.Speaker,
                    contentDescription = null,
                    tint = BustanWhite,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(R.string.tajwid_hear_bounce),
                    style = BustanTextStyles.labelMediumStrong,
                    color = BustanWhite,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(TajwidCardShape)
                .background(BustanWhite)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.tajwid_check_title),
                style = BustanTextStyles.labelMediumStrong,
                color = BustanInk,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.options.forEachIndexed { optionIndex, glyph ->
                    val isPicked = picked == optionIndex
                    val isRight = isPicked && optionIndex == state.correctIndex
                    val isWrong = isPicked && optionIndex != state.correctIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 60.dp)
                            .clip(TajwidOptionShape)
                            .background(
                                when {
                                    isRight -> BustanMint
                                    isWrong -> BustanCoralWash
                                    else -> BustanWhite
                                },
                            )
                            .border(
                                width = if (isRight || isWrong) 3.dp else 2.dp,
                                color = when {
                                    isRight -> BustanTeal
                                    isWrong -> BustanCoral
                                    else -> BustanTajwidEdge
                                },
                                shape = TajwidOptionShape,
                            )
                            .clickable { picked = optionIndex }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CentredGlyph(
                            glyph = glyph,
                            style = TajwidOptionGlyphStyle,
                            color = BustanInk,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                        )
                    }
                }
            }
            Text(
                text = when {
                    picked == null -> stringResource(R.string.tajwid_check_idle)
                    picked == state.correctIndex -> stringResource(
                        R.string.tajwid_check_right,
                        state.options.getOrNull(state.correctIndex).orEmpty(),
                    )
                    else -> stringResource(R.string.tajwid_check_wrong)
                },
                style = BustanTextStyles.caption,
                color = BustanTajwidInk,
                modifier = Modifier.defaultMinSize(minHeight = 18.dp),
            )
        }

        Spacer(Modifier.weight(1f))
    }
}

/**
 * Colours the stop mark — the last word of the example ayah — the way the design
 * highlights the letter that bounces.
 */
private fun stopMarkedAyah(text: String): AnnotatedString {
    val trimmed = text.trim()
    val splitAt = trimmed.lastIndexOf(' ') + 1
    if (splitAt <= 0) return AnnotatedString(trimmed)
    return buildAnnotatedString {
        append(trimmed.substring(0, splitAt))
        withStyle(
            SpanStyle(
                color = BustanBlueArtDeep,
                background = BustanBlueArt,
            ),
        ) {
            append(trimmed.substring(splitAt))
        }
    }
}

/**
 * `tajwid_rule.description_key` → the lesson copy for the active locale. The
 * keys live under `tajwid_desc_*` because `:feature:quran` already owns the bare
 * `tajwid_ghunnah` … names for the mushaf legend's rule labels.
 */
private fun ruleDescriptionRes(descriptionKey: String): Int = when (descriptionKey) {
    "tajwid_ghunnah" -> R.string.tajwid_desc_ghunnah
    "tajwid_idgham_bi" -> R.string.tajwid_desc_idgham_bi
    "tajwid_idgham_bila" -> R.string.tajwid_desc_idgham_bila
    "tajwid_iqlab" -> R.string.tajwid_desc_iqlab
    "tajwid_ikhfa" -> R.string.tajwid_desc_ikhfa
    "tajwid_izhar" -> R.string.tajwid_desc_izhar
    "tajwid_qalqalah" -> R.string.tajwid_desc_qalqalah
    "tajwid_madd" -> R.string.tajwid_desc_madd
    else -> R.string.tajwid_desc_generic
}

