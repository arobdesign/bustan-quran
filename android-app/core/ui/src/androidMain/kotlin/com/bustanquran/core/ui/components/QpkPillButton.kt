package com.bustanquran.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Legacy secondary button, now the design's 2 dp outline pill
 * (see [BustanOutlineButton]).
 */
@Composable
fun QpkPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLargeTouchTarget: Boolean = false,
    borderColor: Color = BustanTeal,
    contentColor: Color = BustanInk,
    backgroundColor: Color = BustanWhite,
) {
    BustanOutlineButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        minHeight = if (isLargeTouchTarget) 64.dp else 48.dp,
        contentColor = contentColor,
        containerColor = backgroundColor,
        borderColor = borderColor,
    )
}
