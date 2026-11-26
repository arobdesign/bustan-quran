package com.bustanquran.feature.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.components.BustanPrimaryButton
import com.bustanquran.core.ui.components.BustanArt
import com.bustanquran.core.ui.components.BustanCardBadge
import com.bustanquran.core.ui.components.BustanSecondaryButton
import com.bustanquran.core.ui.components.BustanStarRating
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.BustanBlueArtMid
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffronDeep
import com.bustanquran.core.ui.theme.BustanSaffronFill
import com.bustanquran.core.ui.theme.BustanShapeProfile
import com.bustanquran.core.ui.theme.BustanSky
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Artboard 06 · `Reward.dc.html` — "MashaAllah, <name>!", three stars (the
 * middle one larger), the tilted animal card and the two pills.
 *
 * The name, achievement and card text are data from the caller; the design's
 * copy is used when a value is missing.
 */
@Composable
fun RewardScreen(
    stars: Int = 3,
    newCard: String? = null,
    onContinue: () -> Unit = {},
    nickname: String = "",
    achievement: String = "",
    cardProgress: String = "",
    onBackToMap: () -> Unit = {}
) {
    val title = if (nickname.isBlank()) {
        stringResource(R.string.reward_title_generic)
    } else {
        stringResource(R.string.reward_title, nickname)
    }
    val starDescription = stringResource(R.string.reward_stars_desc, stars.coerceIn(0, 3), 3)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanButter)
            .clipToBounds(),
    ) {
        // Confetti, `position: absolute` in the design.
        ConfettiSquare(
            modifier = Modifier.offset(x = 34.dp, y = 120.dp),
            color = BustanCoral,
            width = 12.dp,
            height = 12.dp,
            rotation = 20f,
        )
        ConfettiDot(
            modifier = Modifier.align(Alignment.TopEnd).offset(x = (-48).dp, y = 96.dp),
            color = BustanTeal,
            size = 10.dp,
        )
        ConfettiSquare(
            modifier = Modifier.align(Alignment.TopEnd).offset(x = (-70).dp, y = 230.dp),
            color = BustanBlueArtMid,
            width = 14.dp,
            height = 8.dp,
            rotation = -30f,
        )
        ConfettiDot(
            modifier = Modifier.offset(x = 60.dp, y = 300.dp),
            color = BustanTeal,
            size = 8.dp,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 72.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = title,
                style = BustanTextStyles.displayHero,
                color = BustanTeal,
                textAlign = TextAlign.Center,
            )
            Text(
                text = achievement.ifBlank { stringResource(R.string.reward_default_achievement) },
                style = BustanTextStyles.body,
                color = BustanInk,
                textAlign = TextAlign.Center,
            )
            BustanStarRating(
                stars = stars,
                starSize = 64.dp,
                emphasizeCenter = true,
                modifier = Modifier.semantics { contentDescription = starDescription },
            )

            RewardCard(
                cardName = newCard ?: stringResource(R.string.reward_card_name),
                progress = cardProgress.ifBlank { stringResource(R.string.reward_card_progress) },
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BustanPrimaryButton(
                    text = stringResource(R.string.reward_next_ayah),
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                )
                BustanSecondaryButton(
                    text = stringResource(R.string.reward_back_to_map),
                    onClick = onBackToMap,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
/** The tilted animal card: sky scene, camel, name, "New card" badge. */
@Composable
private fun RewardCard(cardName: String, progress: String) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .graphicsLayer {
                rotationZ = -3f
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
            .bustanShadow(color = BustanSaffronDeep, shape = BustanShapeProfile, offsetY = 6.dp)
            .clip(BustanShapeProfile)
            .background(BustanWhite)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BustanSky),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .background(BustanSaffronFill),
            )
            Icon(
                imageVector = BustanArt.CamelScene,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(width = 150.dp, height = 130.dp),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = cardName,
                style = BustanTextStyles.titleSmall,
                color = BustanInk,
            )
            BustanCardBadge(
                text = stringResource(R.string.reward_new_card),
                containerColor = BustanMint,
                contentColor = BustanTealDark,
            )
        }
        Text(
            text = progress,
            style = BustanTextStyles.caption,
            color = BustanMuted,
        )
    }
}

@Composable
private fun ConfettiSquare(
    modifier: Modifier,
    color: Color,
    width: Dp,
    height: Dp,
    rotation: Float,
) {
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .graphicsLayer { rotationZ = rotation }
            .clip(RoundedCornerShape(3.dp))
            .background(color),
    )
}

@Composable
private fun ConfettiDot(modifier: Modifier, color: Color, size: Dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
    )
}

