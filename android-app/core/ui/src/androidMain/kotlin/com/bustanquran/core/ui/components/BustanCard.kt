package com.bustanquran.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanShapeCard
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * The single card surface used by the design: white, rounded 16–28 dp, and
 * either flat (no shadow) or with the design's solid offset shadow.
 *
 * Examples from `design/source`:
 * - profile card: radius 24, `box-shadow: 0 2px 0 #EADFC8`, padding 20/12
 * - goal card: radius 20, no shadow, padding 14/16
 * - settings group: radius 18, no shadow, horizontal padding 16
 */
@Composable
fun BustanCard(
    modifier: Modifier = Modifier,
    shape: Shape = BustanShapeCard,
    backgroundColor: Color = BustanWhite,
    shadowColor: Color? = null,
    shadowOffset: Dp = 2.dp,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    var surface = modifier
    if (shadowColor != null) {
        surface = surface.bustanShadow(color = shadowColor, shape = shape, offsetY = shadowOffset)
    }
    surface = surface.clip(shape).background(backgroundColor)
    if (borderColor != null) {
        surface = surface.border(width = borderWidth, color = borderColor, shape = shape)
    }
    Column(modifier = surface.padding(contentPadding), content = content)
}

/** Convenience wrapper for the very common "white card with flat shadow". */
@Composable
fun BustanShadowedCard(
    modifier: Modifier = Modifier,
    shape: Shape = BustanShapeCard,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    backgroundColor: Color = BustanWhite,
    shadowColor: Color = BustanBorderSoft,
    content: @Composable ColumnScope.() -> Unit,
) = BustanCard(
    modifier = modifier,
    shape = shape,
    backgroundColor = backgroundColor,
    shadowColor = shadowColor,
    contentPadding = contentPadding,
    content = content,
)
