package com.bustanquran.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanMuted

/**
 * Star rating from `Reward.dc.html`: the design draws the middle star larger
 * (64 / 88 / 64 dp) and aligns the row to the bottom edge.
 *
 * The star is the design's own path (saffron fill, `#C98A12` outline), so the
 * icon is drawn un-tinted when earned.
 */
@Composable
fun BustanStarRating(
    stars: Int,
    modifier: Modifier = Modifier,
    maxStars: Int = 3,
    starSize: Dp = 32.dp,
    emphasizeCenter: Boolean = false,
) {
    val clamped = stars.coerceIn(0, maxStars)
    val middleIndex = maxStars / 2
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(maxStars) { index ->
            val earned = index < clamped
            val isEmphasized = emphasizeCenter && maxStars % 2 == 1 && index == middleIndex
            Icon(
                imageVector = BustanIcons.Star,
                contentDescription = null,
                tint = if (earned) Color.Unspecified else BustanBorder,
                modifier = Modifier.size(if (isEmphasized) starSize * 1.375f else starSize),
            )
        }
    }
}

/** Empty-state star row: saffron at 35 % like the "not earned yet" designs. */
@Composable
fun BustanEmptyStars(
    modifier: Modifier = Modifier,
    maxStars: Int = 3,
    starSize: Dp = 32.dp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(maxStars) {
            Icon(
                imageVector = BustanIcons.Star,
                contentDescription = null,
                tint = BustanMuted.copy(alpha = 0.35f),
                modifier = Modifier.size(starSize),
            )
        }
    }
}
