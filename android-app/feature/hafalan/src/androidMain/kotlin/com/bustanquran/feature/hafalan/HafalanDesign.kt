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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanBorderWarm
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanCoralRing
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanInkFaint
import com.bustanquran.core.ui.theme.BustanInkGhost
import com.bustanquran.core.ui.theme.BustanMintSoft
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanSandDeep
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDeep
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Artboard 05 · `Hafalan.dc.html` — listen, repeat, then read on your own.
 *
 * The teal header carries the three step tiles; the sand sheet shows the ayah
 * before, the ayah being memorised (one word highlighted) and the ayah after
 * it. Arabic arrives from `content.db` through [arabicText], [previousAyah] and
 * [nextAyah]; nothing is typed here.
 */
@Composable
fun HafalanScreen(
    surahName: String = "",
    ayahNumber: Int = 1,
    totalAyahs: Int = 1,
    arabicText: String = "",
    previousAyah: String = "",
    nextAyah: String = "",
    translation: String = "",
    highlightWord: String = "",
    currentStep: HafalanStep = HafalanStep.LISTEN,
    listenCount: Int = 0,
    repeatCount: Int = 0,
    repeatTarget: Int = 3,
    speed: String = "0.75",
    onListen: () -> Unit = {},
    onRecord: () -> Unit = {},
    onCheck: () -> Unit = {},
    onPrevAyah: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().background(BustanTeal)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 48.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BustanTealDeep)
                    .clickable(onClick = onBackClicked),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = BustanIcons.ChevronLeft,
                    contentDescription = stringResource(com.bustanquran.core.ui.R.string.back),
                    tint = BustanWhite,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.hafalan_memorise, surahName),
                    style = BustanTextStyles.titleSmall.copy(fontSize = 20.sp, lineHeight = 22.sp),
                    color = BustanWhite,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.hafalan_ayah_of, ayahNumber, totalAyahs),
                    style = BustanTextStyles.captionSmall,
                    color = BustanMintSoft,
                )
            }
            Spacer(modifier = Modifier.width(48.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StepTile(
                title = stringResource(R.string.hafalan_step_listen),
                subtitle = stringResource(R.string.hafalan_step_progress, listenCount, repeatTarget),
                state = stepState(currentStep, 0),
                modifier = Modifier.weight(1f),
            )
            StepTile(
                title = stringResource(R.string.hafalan_step_repeat),
                subtitle = stringResource(R.string.hafalan_step_progress, repeatCount, repeatTarget),
                state = stepState(currentStep, 1),
                modifier = Modifier.weight(1f),
            )
            StepTile(
                title = stringResource(R.string.hafalan_step_own),
                subtitle = if (currentStep == HafalanStep.ON_MY_OWN) {
                    stringResource(R.string.hafalan_step_progress, repeatCount, repeatTarget)
                } else {
                    stringResource(R.string.hafalan_step_locked)
                },
                state = stepState(currentStep, 2),
                modifier = Modifier.weight(1f),
            )
        }

        AyahSheet(
            arabicText = arabicText,
            previousAyah = previousAyah,
            nextAyah = nextAyah,
            translation = translation,
            highlightWord = highlightWord,
            ayahNumber = ayahNumber,
            speed = speed,
            repeatTarget = repeatTarget,
            onListen = onListen,
            onRecord = onRecord,
            onPrevAyah = onPrevAyah,
            modifier = Modifier.weight(1f),
        )
    }
}

/** 0 = done, 1 = current, 2 = locked, matching the design's three fills. */
private fun stepState(currentStep: HafalanStep, index: Int): Int = when {
    index < currentStep.ordinal -> 0
    index == currentStep.ordinal -> 1
    else -> 2
}

@Composable
private fun StepTile(title: String, subtitle: String, state: Int, modifier: Modifier = Modifier) {
    val container = when (state) {
        0 -> BustanSaffron
        1 -> BustanWhite
        else -> BustanTealDeep
    }
    val titleColor = if (state == 2) BustanMintSoft else BustanInk
    val subtitleColor = when (state) {
        0 -> BustanInk
        1 -> BustanMuted
        else -> BustanMintSoft
    }
    var surface = modifier
    if (state == 1) {
        surface = surface.border(3.dp, BustanSaffron, RoundedCornerShape(14.dp))
    }
    Box(
        modifier = surface
            .clip(RoundedCornerShape(14.dp))
            .background(container)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = BustanTextStyles.captionSmall, color = titleColor)
            Text(text = subtitle, style = BustanTextStyles.micro, color = subtitleColor)
        }
    }
}
/** Sand sheet: previous ayah, the ayah card, next ayah, chips and controls. */
@Composable
private fun AyahSheet(
    arabicText: String,
    previousAyah: String,
    nextAyah: String,
    translation: String,
    highlightWord: String,
    ayahNumber: Int,
    speed: String,
    repeatTarget: Int,
    onListen: () -> Unit,
    onRecord: () -> Unit,
    onPrevAyah: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(BustanSand)
            .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (previousAyah.isNotBlank()) {
            Text(
                text = previousAyah,
                style = BustanTextStyles.quranBody.copy(fontSize = 24.sp, lineHeight = 44.sp),
                color = BustanInkGhost,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .bustanShadow(color = BustanBorderSoft, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(BustanWhite)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = highlightedAyah(arabicText, highlightWord, ayahNumber),
                style = BustanTextStyles.quranBody.copy(fontSize = 40.sp, lineHeight = 76.sp),
                color = BustanInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (nextAyah.isNotBlank()) {
            Text(
                text = nextAyah,
                style = BustanTextStyles.quranBody.copy(fontSize = 24.sp, lineHeight = 44.sp),
                color = BustanInkFaint,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (translation.isNotBlank()) {
            Text(
                text = translation,
                style = BustanTextStyles.labelMedium,
                color = BustanMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SettingChip(text = stringResource(R.string.hafalan_reciter))
            SettingChip(text = stringResource(R.string.hafalan_speed, speed))
            SettingChip(text = stringResource(R.string.hafalan_repeat, repeatTarget))
        }
        AyahControls(
            ayahNumber = ayahNumber,
            onListen = onListen,
            onRecord = onRecord,
            onPrevAyah = onPrevAyah,
        )
    }
}
/** 52 dp skip, the 88 dp coral record circle and the 52 dp play circle. */
@Composable
private fun AyahControls(
    ayahNumber: Int,
    onListen: () -> Unit,
    onRecord: () -> Unit,
    onPrevAyah: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundIconButton(
            icon = BustanIcons.SkipBack,
            description = stringResource(R.string.hafalan_prev_ayah),
            size = 52.dp,
            containerColor = BustanSandDeep,
            contentColor = BustanInk,
            onClick = onPrevAyah,
        )
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(BustanCoral)
                .border(6.dp, BustanCoralRing, CircleShape)
                .clickable(onClick = onRecord),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BustanIcons.Mic,
                contentDescription = stringResource(R.string.hafalan_record),
                tint = BustanWhite,
                modifier = Modifier.size(36.dp),
            )
        }
        RoundIconButton(
            icon = BustanIcons.Play,
            description = stringResource(R.string.hafalan_play),
            size = 52.dp,
            containerColor = BustanTeal,
            contentColor = BustanWhite,
            onClick = onListen,
        )
    }

    Text(
        text = stringResource(R.string.hafalan_prompt, ayahNumber),
        style = BustanTextStyles.labelMediumStrong,
        color = BustanInk,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

/** Highlights one word on butter and appends the teal ayah marker. */
private fun highlightedAyah(arabic: String, word: String, ayahNumber: Int) =
    buildAnnotatedString {
        val source = arabic.ifBlank { "…" }
        val index = if (word.isBlank()) -1 else source.indexOf(word)
        if (index >= 0) {
            append(source.substring(0, index))
            withStyle(SpanStyle(background = BustanButter)) { append(word) }
            append(source.substring(index + word.length))
        } else {
            append(source)
        }
        append("  ")
        withStyle(SpanStyle(color = BustanTeal)) { append(ayahMarker(ayahNumber)) }
    }

/** `2` → `﴿٢﴾`, the design's marker; a numeral only. */
private fun ayahMarker(ayahNumber: Int): String {
    val digits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val number = ayahNumber.toString().map { digits[it.digitToInt()] }.joinToString("")
    return "﴿$number﴾"
}

@Composable
private fun SettingChip(text: String) {
    Box(
        modifier = Modifier
            .clip(BustanShapePill)
            .background(BustanWhite)
            .border(2.dp, BustanBorderWarm, BustanShapePill)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = BustanTextStyles.caption, color = BustanInk, maxLines = 1)
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    size: androidx.compose.ui.unit.Dp,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = contentColor,
            modifier = Modifier.size(24.dp),
        )
    }
}



