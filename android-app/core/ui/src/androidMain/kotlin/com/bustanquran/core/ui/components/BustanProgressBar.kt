package com.bustanquran.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanProgressTrack
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles

/** One colour band of a stacked progress bar. */
data class BustanProgressSegment(
    val fraction: Float,
    val color: Color,
)

/**
 * Pill progress bar from the design: 10 dp track on the map goal card, 12 dp
 * on the parent dashboard. No animation library is involved, the fraction is
 * rendered as-is.
 */
@Composable
fun BustanProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    trackColor: Color = BustanProgressTrack,
    fillColor: Color = BustanTeal,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(BustanShapePill)
            .background(trackColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(BustanShapePill)
                .background(fillColor),
        )
    }
}

/**
 * Two-tone bar ("Memorised" teal + "In progress" saffron) used by the parent
 * dashboard. Segments are laid out left to right, like the design's flex row.
 */
@Composable
fun BustanStackedProgressBar(
    segments: List<BustanProgressSegment>,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    trackColor: Color = BustanProgressTrack,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(BustanShapePill)
            .background(trackColor),
    ) {
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            segments.filter { it.fraction > 0f }.forEach { segment ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(segment.fraction.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(segment.color),
                )
            }
        }
    }
}

/** Legend entry: a 10 dp rounded swatch followed by 12/700 muted text. */
@Composable
fun BustanLegendDot(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color),
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = BustanTextStyles.captionSmall, color = BustanMuted)
    }
}
