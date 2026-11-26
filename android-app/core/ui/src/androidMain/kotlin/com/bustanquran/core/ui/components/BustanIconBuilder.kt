package com.bustanquran.core.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Builds an [ImageVector] from the raw `<path d="...">` strings of the design
 * sources, so every icon is the design's own geometry instead of a lookalike.
 *
 * Vectors built here are stroked in black and tinted at the call site through
 * `Icon(tint = ...)`, except when [filled] is requested.
 */
internal fun buildIcon(
    name: String,
    paths: List<String>,
    viewportWidth: Float = 24f,
    viewportHeight: Float = 24f,
    strokeWidth: Float = 2.2f,
    filled: Boolean = false,
    fillColor: Color = Color.Black,
    strokeColor: Color = Color.Black,
): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = viewportWidth.dp,
        defaultHeight = viewportHeight.dp,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
    )
    paths.forEach { data ->
        builder.addPath(
            pathData = PathParser().parsePathString(data).toNodes(),
            fill = if (filled) SolidColor(fillColor) else null,
            stroke = if (strokeWidth > 0f) SolidColor(strokeColor) else null,
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
    return builder.build()
}
