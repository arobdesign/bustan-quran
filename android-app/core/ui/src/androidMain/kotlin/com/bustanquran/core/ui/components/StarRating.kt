package com.bustanquran.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Legacy entry point kept for the screens written before P-18; the design's
 * star rating lives in [BustanStarRating].
 */
@Composable
fun StarRating(
    stars: Int,
    modifier: Modifier = Modifier,
    maxStars: Int = 3,
    starSize: Dp = 32.dp,
) {
    BustanStarRating(
        stars = stars,
        modifier = modifier,
        maxStars = maxStars,
        starSize = starSize,
    )
}
