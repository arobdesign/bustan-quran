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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Chips and badges from the design.
 *
 * - filter chip (`QuranLibrary`, `ParentDashboard` child tabs): pill, 40 dp,
 *   Nunito 13–14/800, ink background when selected, white when not.
 * - status badge (`Memorised`, `Learning`, `New card`): pill, 3×8 dp padding,
 *   Nunito 11–13/800 with a soft tinted background.
 */
@Composable
fun BustanFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 40.dp,
    selectedContainerColor: Color = BustanInk,
    selectedContentColor: Color = BustanWhite,
    containerColor: Color = BustanWhite,
    contentColor: Color = BustanInk,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .clip(BustanShapePill)
            .background(if (selected) selectedContainerColor else containerColor)
            .clickable(role = Role.Tab, onClick = onClick)
            .semantics { this.selected = selected }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = BustanTextStyles.label,
            color = if (selected) selectedContentColor else contentColor,
        )
    }
}

/** Selected/learning badge, e.g. "Memorised" in `QuranLibrary`. */
@Composable
fun BustanStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = BustanMint,
    contentColor: Color = BustanTealDark,
    borderColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
) {
    BustanBadge(
        text = text,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = borderColor,
        contentPadding = contentPadding,
    )
}

/** The softer, larger badge used on cards ("New card", tier labels). */
@Composable
fun BustanCardBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = BustanMint,
    contentColor: Color = BustanTealDark,
) {
    BustanBadge(
        text = text,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = null,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        large = true,
    )
}

@Composable
private fun BustanBadge(
    text: String,
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color?,
    contentPadding: PaddingValues,
    large: Boolean = false,
) {
    var surface = modifier.clip(BustanShapePill).background(containerColor)
    if (borderColor != null) {
        surface = surface.border(width = 1.dp, color = borderColor, shape = BustanShapePill)
    }
    Box(
        modifier = surface.padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = if (large) BustanTextStyles.caption else BustanTextStyles.micro,
            color = contentColor,
        )
    }
}
