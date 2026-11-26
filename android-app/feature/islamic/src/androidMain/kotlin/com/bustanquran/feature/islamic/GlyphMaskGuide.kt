package com.bustanquran.feature.islamic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Turns the letter itself into the path the child has to trace.
 *
 * The artboards draw a dashed guide under the letters, but "trace the letter"
 * needs waypoints on the glyph's own ink — a line along the baseline accepts a
 * straight swipe under an alif and rejects a correct stroke. So the glyph is
 * rasterised once, sampled on a coarse grid, and its contour becomes the guide.
 */
internal object GlyphMaskGuide {

    /** Grid resolution used to sample the rasterised glyph. */
    const val GRID = 24

    /** Enough waypoints to need real coverage, few enough to stay forgiving. */
    const val MAX_POINTS = 24

    private const val INK_ALPHA = 0.25f

    /**
     * Guide waypoints for [glyph], normalised into its own layout box (0..1 on
     * both axes), so the caller can scale them onto any canvas size.
     */
    fun guide(
        measurer: TextMeasurer,
        glyph: String,
        style: TextStyle,
        density: Density,
        grid: Int = GRID,
        maxPoints: Int = MAX_POINTS,
    ): List<Offset> {
        if (glyph.isBlank()) return emptyList()
        val layout = measurer.measure(AnnotatedString(glyph), style)
        // Amiri lets ink spill outside the line box, so rasterise with a margin;
        // waypoints are mapped back into the layout box afterwards, which keeps
        // the caller's "topLeft + unit * layoutSize" maths exact.
        val width = layout.size.width.coerceAtLeast(1)
        val height = layout.size.height.coerceAtLeast(1)
        val margin = (maxOf(width, height) * MARGIN).toInt().coerceAtLeast(2)
        val bitmapWidth = width + margin * 2
        val bitmapHeight = height + margin * 2
        val bitmap = ImageBitmap(bitmapWidth, bitmapHeight)
        CanvasDrawScope().draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = Canvas(bitmap),
            size = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat()),
        ) {
            drawText(layout, topLeft = Offset(margin.toFloat(), margin.toFloat()))
        }
        val pixels = bitmap.toPixelMap()
        val mask = BooleanArray(grid * grid)
        for (row in 0 until grid) {
            for (column in 0 until grid) {
                val x0 = column * bitmapWidth / grid
                val y0 = row * bitmapHeight / grid
                val x1 = ((column + 1) * bitmapWidth / grid).coerceAtMost(bitmapWidth)
                val y1 = ((row + 1) * bitmapHeight / grid).coerceAtMost(bitmapHeight)
                // A cell narrower than a pixel cannot be read, so step one pixel.
                val safeX1 = x1.coerceAtLeast(x0 + 1).coerceAtMost(bitmapWidth)
                val safeY1 = y1.coerceAtLeast(y0 + 1).coerceAtMost(bitmapHeight)
                mask[row * grid + column] = hasInk(pixels, x0, safeX1, y0, safeY1)
            }
        }
        val contour = contour(mask, grid, grid, maxPoints).map { point ->
            Offset(
                x = (point.x * bitmapWidth - margin) / width,
                y = (point.y * bitmapHeight - margin) / height,
            )
        }
        // A glyph too thin to leave a contour still needs somewhere to trace.
        return contour.ifEmpty { centreLine() }
    }

    /** Margin added around the glyph so overflow ink is not clipped away. */
    private const val MARGIN = 0.08f

    /** Vertical centre line, used only when the mask came back empty. */
    private fun centreLine(): List<Offset> =
        listOf(Offset(0.5f, 0.25f), Offset(0.5f, 0.5f), Offset(0.5f, 0.75f))

    private fun hasInk(
        pixels: androidx.compose.ui.graphics.PixelMap,
        x0: Int,
        x1: Int,
        y0: Int,
        y1: Int,
    ): Boolean {
        for (y in y0 until y1) {
            for (x in x0 until x1) {
                if (pixels[x, y].alpha > INK_ALPHA) return true
            }
        }
        return false
    }

    /**
     * Waypoints spread around the mask's contour, normalised to 0..1. Falls back
     * to the whole ink when the glyph is thinner than one cell (a hairline alif
     * at low resolution has no interior to outline).
     */
    fun contour(
        mask: BooleanArray,
        columns: Int,
        rows: Int,
        maxPoints: Int = MAX_POINTS,
    ): List<Offset> {
        require(columns > 0 && rows > 0) { "grid must be positive" }
        require(mask.size == columns * rows) { "mask must match the grid" }
        if (maxPoints <= 0) return emptyList()

        fun ink(column: Int, row: Int): Boolean =
            column in 0 until columns && row in 0 until rows && mask[row * columns + column]

        val outline = ArrayList<Int>()
        val all = ArrayList<Int>()
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                if (!ink(column, row)) continue
                val index = row * columns + column
                all += index
                val edge = !ink(column - 1, row) || !ink(column + 1, row) ||
                    !ink(column, row - 1) || !ink(column, row + 1)
                if (edge) outline += index
            }
        }
        val pool = if (outline.size >= 6) outline else all
        if (pool.isEmpty()) return emptyList()

        // The pool is in row-major order, so a stride walks right round the
        // letter instead of bunching on its first rows.
        val count = minOf(maxPoints, pool.size)
        val step = pool.size.toFloat() / count
        return List(count) { i ->
            val index = pool[(i * step).toInt().coerceAtMost(pool.lastIndex)]
            Offset(
                x = (index % columns + 0.5f) / columns,
                y = (index / columns + 0.5f) / rows,
            )
        }
    }
}
