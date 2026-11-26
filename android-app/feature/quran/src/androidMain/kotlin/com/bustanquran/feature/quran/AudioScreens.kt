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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.audio.LoopMode
import com.bustanquran.core.audio.PlayerState
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.BustanSegmentedControl
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanBorderWarm
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanInkGhost
import com.bustanquran.core.ui.theme.BustanInkSoft
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMintSoft
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSaffronWash
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanSandDeep
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTealDeep
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Artboard 09 · `MurottalQuraisy.dc.html` — memorising one surah with the
 * reciter, looping per ayah or over the whole surah.
 *
 * Arabic text is never written here: it arrives through [AyahItem.arabicText]
 * from `content.db`.
 */

data class AyahItem(
    val ayahNo: Int,
    /** `text_uthmani` from `content.db`; empty until the surah is loaded. */
    val arabicText: String,
    /** Kid-friendly translation line under the ayah. */
    val translation: String = "",
    val isActive: Boolean = false,
)

private val RepeatOptions = listOf(1, 3, 5, 10, 0)
private val SpeedOptions = listOf(0.75f, 1.0f, 1.25f, 1.5f)
@Composable
fun SurahPlayerFullScreen(
    surahName: String = "",
    surahNumber: Int = 0,
    ayahCount: Int = 0,
    revelation: String = "",
    surahNameAr: String = "",
    ayahs: List<AyahItem> = emptyList(),
    playerState: PlayerState = PlayerState(),
    onPlay: () -> Unit = {},
    onPause: () -> Unit = {},
    onPrev: () -> Unit = {},
    onNext: () -> Unit = {},
    onAyahTap: (Int) -> Unit = {},
    onLoopModeChange: (LoopMode) -> Unit = {},
    onRepeatChange: (Int) -> Unit = {},
    onSpeedChange: (Float) -> Unit = {},
    onReciterClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    val totalAyahs = playerState.totalAyahs.takeIf { it > 0 } ?: ayahCount.takeIf { it > 0 } ?: ayahs.size
    val perAyah = playerState.loopMode == LoopMode.PER_AYAH
    val totalRepeats = if (playerState.targetRepeats == 0) "∞" else playerState.targetRepeats.toString()
    val perAyahLabel = stringResource(R.string.player_loop_per_ayah)
    val perSurahLabel = stringResource(R.string.player_loop_whole_surah)
    val repeatBadge = if (perAyah) {
        stringResource(R.string.player_repeat_badge, playerState.currentRepeat, totalRepeats)
    } else if (playerState.isPlaying) {
        stringResource(R.string.player_playing)
    } else {
        stringResource(R.string.player_ready)
    }

    Column(modifier = Modifier.fillMaxSize().background(BustanSand)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(BustanTeal)
                .padding(start = 16.dp, end = 16.dp, top = 44.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(84.dp), contentAlignment = Alignment.CenterStart) {
                    BustanIconTileForPlayer(onClick = onBackClicked)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.player_title, surahName),
                        style = BustanTextStyles.titleMedium.copy(fontSize = 21.sp, lineHeight = 23.sp),
                        color = BustanWhite,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(
                            R.string.player_surah_meta,
                            surahNumber,
                            totalAyahs,
                            revelation,
                        ),
                        style = BustanTextStyles.captionSmall,
                        color = BustanMintSoft,
                    )
                }
                Box(modifier = Modifier.width(84.dp), contentAlignment = Alignment.CenterEnd) {
                    if (surahNameAr.isNotBlank()) {
                        Text(
                            text = surahNameAr,
                            style = BustanTextStyles.quranSmall.copy(fontSize = 20.sp, lineHeight = 22.sp),
                            color = BustanWhite,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }

            BustanSegmentedControl(
                options = listOf(LoopMode.PER_AYAH, LoopMode.PER_SURAH),
                selected = playerState.loopMode,
                onSelect = onLoopModeChange,
                label = { if (it == LoopMode.PER_AYAH) perAyahLabel else perSurahLabel },
                trackColor = BustanTealDeep,
                selectedContainerColor = BustanSaffron,
                selectedContentColor = BustanInk,
                contentColor = BustanWhite,
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(ayahs, key = { it.ayahNo }) { ayah ->
                val isActive = ayah.ayahNo == playerState.currentAyah
                AyahCard(
                    ayah = ayah,
                    active = isActive,
                    badge = if (isActive) {
                        stringResource(R.string.player_ayah_badge, ayah.ayahNo, repeatBadge)
                    } else {
                        null
                    },
                    onClick = { onAyahTap(ayah.ayahNo) },
                )
            }
        }

        PlayerSheet(
            playerState = playerState,
            perAyah = perAyah,
            totalRepeats = totalRepeats,
            totalAyahs = totalAyahs,
            onPlay = onPlay,
            onPause = onPause,
            onPrev = onPrev,
            onNext = onNext,
            onRepeatChange = onRepeatChange,
            onSpeedChange = onSpeedChange,
            onReciterClicked = onReciterClicked,
        )
    }
}
/** Back tile on the teal header: 44 dp, `#0A5A4D`, radius 14. */
@Composable
private fun BustanIconTileForPlayer(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BustanTealDeep)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = BustanIcons.ChevronLeft,
            contentDescription = stringResource(com.bustanquran.core.ui.R.string.back),
            tint = BustanWhite,
            modifier = Modifier.size(22.dp),
        )
    }
}

/**
 * One ayah: the active card is saffron-washed with a 3 dp border and a repeat
 * badge; the rest are white with the flat `0 2px 0` shadow.
 */
@Composable
private fun AyahCard(
    ayah: AyahItem,
    active: Boolean,
    badge: String?,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    var surface = Modifier.fillMaxWidth()
    if (!active) {
        surface = surface.bustanShadow(color = BustanBorderSoft, shape = shape)
    }
    surface = surface
        .clip(shape)
        .background(if (active) BustanSaffronWash else BustanWhite)
    if (active) {
        surface = surface.border(3.dp, BustanSaffron, shape)
    }
    surface = surface
        .clickable(onClick = onClick)
        .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 10.dp)

    Column(modifier = surface, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (active && badge != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .clip(BustanShapePill)
                        .background(com.bustanquran.core.ui.theme.BustanButter)
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = badge,
                        style = BustanTextStyles.captionSmall,
                        color = com.bustanquran.core.ui.theme.BustanAmberText,
                    )
                }
                WaveformBars()
            }
        }
        LanguageAyahText(
            arabic = ayah.arabicText,
            ayahNo = ayah.ayahNo,
            active = active,
        )
        if (ayah.translation.isNotBlank()) {
            Text(
                text = ayah.translation,
                style = if (active) BustanTextStyles.caption else BustanTextStyles.captionSmall,
                color = if (active) BustanInkSoft else BustanMuted,
            )
        }
    }
}

/** Four teal bars, as drawn in the active ayah card of the design. */
@Composable
private fun WaveformBars() {
    Row(
        modifier = Modifier.height(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        listOf(8, 16, 11, 14).forEach { barHeight ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BustanTeal),
            )
        }
    }
}
/** Qur'an line from `content.db` plus the teal ayah marker `﴿n﴾`. */
@Composable
private fun LanguageAyahText(arabic: String, ayahNo: Int, active: Boolean) {
    val annotated = buildAnnotatedString {
        append(arabic.ifBlank { "…" })
        append("  ")
        withStyle(SpanStyle(color = BustanTeal)) { append(ayahMarker(ayahNo)) }
    }
    Text(
        text = annotated,
        style = if (active) {
            BustanTextStyles.quranBody.copy(fontSize = 27.sp, lineHeight = 50.sp)
        } else {
            BustanTextStyles.quranBody.copy(fontSize = 23.sp, lineHeight = 43.sp)
        },
        color = BustanInk,
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth(),
    )
}

/** `3` → `﴿٣﴾`. Only a numeral, never scripture. */
private fun ayahMarker(ayahNo: Int): String {
    val digits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val number = ayahNo.toString().map { digits[it.digitToInt()] }.joinToString("")
    return "﴿$number﴾"
}

/** Bottom control sheet: status, repeat target, prev/play/next and the pills. */
@Composable
private fun PlayerSheet(
    playerState: PlayerState,
    perAyah: Boolean,
    totalRepeats: String,
    totalAyahs: Int,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRepeatChange: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onReciterClicked: () -> Unit,
) {
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val statusTop = if (perAyah) {
        stringResource(R.string.player_status_ayah, playerState.currentAyah, totalAyahs)
    } else {
        stringResource(R.string.player_status_surah, playerState.currentAyah, totalAyahs)
    }
    val statusSub = if (perAyah) {
        stringResource(R.string.player_status_ayah_sub, playerState.currentRepeat, totalRepeats)
    } else {
        stringResource(R.string.player_status_surah_sub, playerState.currentRepeat, totalRepeats)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bustanShadow(color = BustanBorderSoft, shape = sheetShape, offsetY = (-2).dp)
            .clip(sheetShape)
            .background(BustanWhite)
            .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = statusTop,
                    style = BustanTextStyles.titleSmall.copy(fontSize = 17.sp, lineHeight = 20.sp),
                    color = BustanInk,
                )
                Text(
                    text = statusSub,
                    style = BustanTextStyles.caption,
                    color = BustanMuted,
                )
            }
            Row(
                modifier = Modifier
                    .clip(BustanShapePill)
                    .background(BustanMint)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = BustanIcons.Download,
                    contentDescription = null,
                    tint = BustanTealDark,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = stringResource(R.string.player_mp3_offline),
                    style = BustanTextStyles.captionSmall,
                    color = BustanTealDark,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = if (perAyah) {
                    stringResource(R.string.player_repeat_each_ayah)
                } else {
                    stringResource(R.string.player_repeat_surah)
                },
                style = BustanTextStyles.label,
                color = BustanInk,
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
            ) {
                RepeatOptions.forEach { repeats ->
                    RepeatChip(
                        label = if (repeats == 0) "∞" else "${repeats}×",
                        selected = playerState.targetRepeats == repeats,
                        onClick = { onRepeatChange(repeats) },
                    )
                }
            }
        }

        PlayerControlsRow(
            playerState = playerState,
            onPlay = onPlay,
            onPause = onPause,
            onPrev = onPrev,
            onNext = onNext,
            onSpeedChange = onSpeedChange,
            onReciterClicked = onReciterClicked,
        )
    }
}
/** Reciter pill, 48 dp skip circles, the 68 dp play button and the speed pill. */
@Composable
private fun PlayerControlsRow(
    playerState: PlayerState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onReciterClicked: () -> Unit,
) {
    val speedIndex = SpeedOptions.indexOf(playerState.speed).coerceAtLeast(0)
    val nextSpeed = SpeedOptions[(speedIndex + 1) % SpeedOptions.size]

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerPill(
            text = stringResource(R.string.player_reciter),
            onClick = onReciterClicked,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CircleControl(
                icon = BustanIcons.SkipBack,
                description = stringResource(R.string.player_prev_ayah),
                containerColor = BustanSandDeep,
                contentColor = BustanInk,
                size = 48.dp,
                onClick = onPrev,
            )
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(BustanTeal)
                    .border(5.dp, BustanMintSoft, CircleShape)
                    .clickable { if (playerState.isPlaying) onPause() else onPlay() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) BustanIcons.Pause else BustanIcons.Play,
                    contentDescription = stringResource(
                        if (playerState.isPlaying) R.string.player_pause else R.string.player_play,
                    ),
                    tint = BustanWhite,
                    modifier = Modifier.size(26.dp),
                )
            }
            CircleControl(
                icon = BustanIcons.SkipForward,
                description = stringResource(R.string.player_next_ayah),
                containerColor = BustanSandDeep,
                contentColor = BustanInk,
                size = 48.dp,
                onClick = onNext,
            )
        }
        PlayerPill(
            text = stringResource(R.string.player_speed, formatSpeed(playerState.speed)),
            onClick = { onSpeedChange(nextSpeed) },
        )
    }
}

private fun formatSpeed(speed: Float): String =
    if (speed == speed.toInt().toFloat()) {
        speed.toInt().toString()
    } else {
        speed.toString().trimEnd('0').trimEnd('.')
    }

@Composable
private fun RepeatChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 44.dp, minHeight = 40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) BustanTeal else BustanWhite)
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(2.dp, BustanBorderWarm, RoundedCornerShape(12.dp))
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = BustanTextStyles.labelMediumStrong,
            color = if (selected) BustanWhite else BustanInk,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun PlayerPill(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = 40.dp)
            .clip(BustanShapePill)
            .background(BustanWhite)
            .border(2.dp, BustanBorderWarm, BustanShapePill)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = BustanTextStyles.captionSmall,
            color = BustanInk,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun CircleControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    containerColor: Color,
    contentColor: Color,
    size: androidx.compose.ui.unit.Dp,
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
            modifier = Modifier.size(22.dp),
        )
    }
}
// ─── Audio missing sheet ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioMissingSheet(
    surahName: String = "",
    onDownload: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = BustanIcons.Download,
                contentDescription = null,
                tint = BustanTeal,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.audio_missing_title),
                style = BustanTextStyles.titleLarge,
                color = BustanInk,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.audio_missing_body, surahName),
                style = BustanTextStyles.labelMedium,
                color = BustanMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDownload,
                colors = ButtonDefaults.buttonColors(containerColor = BustanTeal),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.audio_missing_download))
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.audio_missing_later), color = BustanMuted)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}





