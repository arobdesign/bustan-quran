package com.bustanquran.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint

/**
 * Legacy pill badge, now rendered with the design's badge metrics
 * (see [BustanStatusBadge]).
 */
@Composable
fun ProgressPill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BustanMint,
    contentColor: Color = BustanInk,
    borderColor: Color = BustanBorder,
) {
    BustanStatusBadge(
        text = text,
        modifier = modifier,
        containerColor = backgroundColor,
        contentColor = contentColor,
        borderColor = borderColor,
    )
}
