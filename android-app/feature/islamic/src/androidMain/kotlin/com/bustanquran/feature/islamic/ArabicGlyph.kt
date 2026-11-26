package com.bustanquran.feature.islamic

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer

/**
 * Amiri carries very tall vertical metrics, so a `Text` box cannot be centred on
 * the ink: the glyph hangs under its own line box and a short container clips it.
 * These helpers measure the glyph once and place its first baseline instead,
 * which centres the ink no matter which font the style names.
 */
internal fun DrawScope.drawCentredGlyph(layout: TextLayoutResult, baselineY: Float) {
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            x = (size.width - layout.size.width) / 2f,
            y = baselineY - layout.firstBaseline,
        ),
    )
}

/** How much of the font size drops below the middle of the ink (≈ x-height / 2). */
internal const val GlyphBaselineShare = 0.32f

/** A single Arabic glyph centred in its own canvas, standing on an invisible baseline. */
@Composable
internal fun CentredGlyph(
    glyph: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val measurer = rememberTextMeasurer()
    val paintStyle = remember(style, color) {
        if (color == Color.Unspecified) style else style.copy(color = color)
    }
    val layout = remember(glyph, paintStyle, measurer) {
        measurer.measure(AnnotatedString(glyph), paintStyle)
    }
    val fontSizePx = with(LocalDensity.current) { paintStyle.fontSize.toPx() }
    Canvas(modifier) {
        drawCentredGlyph(layout, size.height / 2f + fontSizePx * GlyphBaselineShare)
    }
}
