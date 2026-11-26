package com.bustanquran.feature.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanCoralWash
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanInkSoft
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMintSoft
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite
import org.koin.compose.viewmodel.koinViewModel

/**
 * Artboard 26 · `SambungAyat.dc.html` — "Continue the Ayah".
 *
 * The prompt card plays an ayah; three cards below offer the next one, and the
 * child taps the continuation. The prompt, the options and the correct index all
 * come from `content.db` via [AyatGameViewModel].
 */
private val SambungPromptShape = RoundedCornerShape(26.dp)
private val SambungOptionShape = RoundedCornerShape(18.dp)

@Composable
fun SambungAyatScreen(
    onNext: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    viewModel: AyatGameViewModel = koinViewModel(),
) {
    val data by viewModel.data.collectAsState()
    SambungAyatContent(data = data, onNext = onNext, onBackClicked = onBackClicked)
}

@Composable
fun SambungAyatContent(
    data: AyatGameData,
    onNext: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    val state = remember(data) { buildSambungAyatState(data.surahNameAr, data.ayahs) }
    var picked by remember(state) { mutableStateOf(-1) }
    val solved = picked >= 0 && picked == state.correctIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanSand)
            .padding(start = 18.dp, end = 18.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SambungHeader(
            state = state,
            surahNameTr = data.surahNameTr,
            onBackClicked = onBackClicked,
        )

        if (!state.hasOptions) {
            Text(
                text = stringResource(R.string.game_sambung_empty),
                style = BustanTextStyles.bodyMedium,
                color = BustanMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            return@Column
        }

        PromptCard(prompt = state.prompt)

        Text(
            text = stringResource(R.string.game_sambung_question),
            style = BustanTextStyles.labelLarge.copy(fontSize = 16.sp),
            color = BustanInk,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            state.options.forEachIndexed { index, option ->
                OptionCard(
                    text = option,
                    state = when {
                        solved && index == state.correctIndex -> OptionState.Correct
                        !solved && index == picked -> OptionState.Wrong
                        else -> OptionState.Idle
                    },
                    onClick = { if (!solved) picked = index },
                )
            }
        }

        Text(
            text = when {
                picked < 0 -> ""
                solved -> stringResource(R.string.game_sambung_correct, state.prompt.number + 1)
                else -> stringResource(R.string.game_sambung_wrong)
            },
            style = BustanTextStyles.labelMediumStrong,
            color = BustanInkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 20.dp),
        )

        Spacer(Modifier.weight(1f))

        if (solved) {
            NextButton(onClick = onNext)
        }
    }
}

@Composable
private fun SambungHeader(
    state: SambungAyatState,
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
                text = stringResource(R.string.game_sambung_title),
                style = BustanTextStyles.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = BustanInk,
            )
            Text(
                text = stringResource(R.string.game_sambung_subtitle, surahNameTr),
                style = BustanTextStyles.captionSmall,
                color = BustanMuted,
            )
        }
        Text(
            text = stringResource(R.string.game_sambung_stage, state.stage, state.stageTotal),
            style = BustanTextStyles.label,
            color = BustanInk,
            modifier = Modifier
                .clip(BustanShapePill)
                .background(BustanWhite)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

/** The deep-teal card that carries the ayah the child just heard. */
@Composable
private fun PromptCard(prompt: AyatSource) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SambungPromptShape)
            .background(BustanTeal)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.game_sambung_prompt_label),
            style = BustanTextStyles.label,
            color = BustanMintSoft,
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = prompt.text + " " + arabicAyahMarker(prompt.number),
                style = SambungPromptStyle,
                color = BustanWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Row(
            modifier = Modifier
                .clip(BustanShapePill)
                .background(BustanSaffron)
                .clickable {}
                .defaultMinSize(minHeight = 44.dp)
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = BustanIcons.Play,
                contentDescription = null,
                tint = BustanInk,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.game_sambung_play_again),
                style = BustanTextStyles.labelMediumStrong,
                color = BustanInk,
            )
        }
    }
}

/** The three answers: teal for the right one, coral for a wrong tap. */
private enum class OptionState { Idle, Correct, Wrong }

@Composable
private fun OptionCard(
    text: String,
    state: OptionState,
    onClick: () -> Unit,
) {
    val borderColor = when (state) {
        OptionState.Correct -> BustanTeal
        OptionState.Wrong -> BustanCoral
        OptionState.Idle -> BustanBorder
    }
    val borderWidth = if (state == OptionState.Idle) 2.dp else 3.dp
    val container = when (state) {
        OptionState.Correct -> BustanMint
        OptionState.Wrong -> BustanCoralWash
        OptionState.Idle -> BustanWhite
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (state == OptionState.Idle) {
                    Modifier.bustanShadow(color = BustanBorderSoft, shape = SambungOptionShape)
                } else {
                    Modifier
                },
            )
            .clip(SambungOptionShape)
            .background(container)
            .border(borderWidth, borderColor, SambungOptionShape)
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 72.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = text,
                style = SambungOptionStyle,
                color = BustanInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** The 56 dp "Next" pill that appears once the child has found the answer. */
@Composable
private fun NextButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(BustanShapePill)
            .background(BustanTeal)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.game_sambung_next),
            style = BustanTextStyles.buttonLarge,
            color = BustanWhite,
        )
    }
}

/** Amiri 32/1.8 for the prompt ayah inside the teal card. */
private val SambungPromptStyle =
    BustanTextStyles.quranBody.copy(fontSize = 32.sp, lineHeight = 58.sp)

/** Amiri 26 for the answer cards. */
private val SambungOptionStyle =
    BustanTextStyles.quranBody.copy(fontSize = 26.sp, lineHeight = 40.sp)

