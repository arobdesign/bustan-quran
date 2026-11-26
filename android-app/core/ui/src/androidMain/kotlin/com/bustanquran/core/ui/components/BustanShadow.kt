package com.bustanquran.core.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * The design uses flat "offset" shadows (`box-shadow: 0 2px 0 #EADFC8`, `0 6px 0
 * #E7B96A`), never blurred elevation shadows. This modifier draws the same
 * solid shape, translated down by [offsetY], behind the content.
 *
 * It must be applied *before* any `clip`/`background` in the modifier chain so
 * the shape is not clipped away.
 */
fun Modifier.bustanShadow(
    color: Color,
    shape: Shape,
    offsetY: Dp = 2.dp,
): Modifier = drawBehind {
    val path = shape.toPath(size = size, layoutDirection = layoutDirection, density = this)
    translate(left = 0f, top = offsetY.toPx()) {
        drawPath(path = path, color = color)
    }
}

/** Converts a [Shape] outline into a [Path] that can be drawn directly. */
internal fun Shape.toPath(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density,
): Path {
    val outline = createOutline(size = size, layoutDirection = layoutDirection, density = density)
    return when (outline) {
        is Outline.Rectangle -> Path().apply { addRect(outline.rect) }
        is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
        is Outline.Generic -> outline.path
    }
}
