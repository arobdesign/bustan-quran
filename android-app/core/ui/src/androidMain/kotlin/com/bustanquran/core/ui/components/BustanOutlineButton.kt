package com.bustanquran.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanShapeProfile
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * 2 dp outline pill. The default is the 48 dp "For parents" button (ink on
 * white); `accent = true` gives the 40 dp teal variant used inside cards
 * ("Play recording", "Add to today").
 */
@Composable
fun BustanOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Boolean = false,
    minHeight: Dp = if (accent) 40.dp else 48.dp,
    contentColor: Color = if (accent) BustanTeal else BustanInk,
    containerColor: Color = BustanWhite,
    borderColor: Color = contentColor,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .clip(BustanShapePill)
            .background(containerColor)
            .border(width = 2.dp, color = borderColor, shape = BustanShapePill)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .alpha(if (enabled) 1f else 0.45f)
            .padding(
                if (accent) {
                    PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                } else {
                    PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = if (accent) BustanTextStyles.label else BustanTextStyles.labelLarge,
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}

/** Text-only action ("Full weekly report ›") with a 44 dp touch target. */
@Composable
fun BustanPillLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = BustanInk,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 44.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = BustanTextStyles.labelMediumStrong,
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Profile tile: white, radius 24, flat `0 2px 0` shadow, clickable as one
 * surface.
 */
@Composable
fun BustanShadowedClickable(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shadowColor: Color = BustanBorderSoft,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(BustanShapeProfile)
            .bustanShadow(color = shadowColor, shape = BustanShapeProfile)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        content()
    }
}
