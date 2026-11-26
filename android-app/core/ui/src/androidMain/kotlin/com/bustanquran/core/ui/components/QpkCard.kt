package com.bustanquran.core.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanShapeField
import com.bustanquran.core.ui.theme.BustanShapeProfile
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Legacy card entry point, now rendered by [BustanCard] with the design's
 * radii (16 dp for list cards, 24 dp for profile/dialog cards).
 */
@Composable
fun QpkCard(
    modifier: Modifier = Modifier,
    isLargeRadius: Boolean = false,
    backgroundColor: Color = BustanWhite,
    borderColor: Color = BustanBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    BustanCard(
        modifier = modifier,
        shape = if (isLargeRadius) BustanShapeProfile else BustanShapeField,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        borderWidth = borderWidth,
        contentPadding = PaddingValues(16.dp),
        content = content,
    )
}
