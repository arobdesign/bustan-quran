package com.bustanquran.feature.islamic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlyphMaskGuideTest {

    /** 7x7 mask with an ink column in the middle, like a bare alif. */
    private fun verticalBar(): BooleanArray {
        val grid = 7
        return BooleanArray(grid * grid) { index -> index % grid == 3 }
    }

    /** 7x7 mask with only the border inked, like a hollow rectangle. */
    private fun hollowRing(): BooleanArray {
        val grid = 7
        return BooleanArray(grid * grid) { index ->
            val column = index % grid
            val row = index / grid
            column == 0 || row == 0 || column == grid - 1 || row == grid - 1
        }
    }

    @Test
    fun `guide points stay on the ink of a bare letter`() {
        val points = GlyphMaskGuide.contour(verticalBar(), columns = 7, rows = 7)

        assertTrue(points.isNotEmpty())
        // The stroke sits in column 3 of 7, so every waypoint is inside that cell.
        assertTrue(points.all { it.x > 3f / 7f && it.x < 4f / 7f })
    }

    @Test
    fun `contour is spread down the whole letter not only its first rows`() {
        val points = GlyphMaskGuide.contour(verticalBar(), columns = 7, rows = 7)

        assertTrue(points.any { it.y < 0.3f })
        assertTrue(points.any { it.y > 0.7f })
    }

    @Test
    fun `hollow glyph is sampled on its border only`() {
        val points = GlyphMaskGuide.contour(hollowRing(), columns = 7, rows = 7)

        assertTrue(points.isNotEmpty())
        val inside = points.none { point ->
            point.x > 0.2f && point.x < 0.8f && point.y > 0.2f && point.y < 0.8f
        }
        assertTrue("waypoints must not fall inside the ring", inside)
    }

    @Test
    fun `never returns more points than asked for`() {
        val points = GlyphMaskGuide.contour(hollowRing(), columns = 7, rows = 7, maxPoints = 5)

        assertEquals(5, points.size)
    }

    @Test
    fun `an empty mask has no guide`() {
        val points = GlyphMaskGuide.contour(BooleanArray(49), columns = 7, rows = 7)

        assertTrue(points.isEmpty())
    }

    @Test
    fun `a baseline swipe no longer covers a vertical letter`() {
        val grid = 7
        val guide = GlyphMaskGuide.contour(verticalBar(), columns = grid, rows = grid)
        // Fake tolerance of one cell in normalised units, like 44 dp on the card.
        val tolerance = 1f / grid
        val swipe = List(20) { step -> androidx.compose.ui.geometry.Offset(step / 19f, 6.5f / grid) }

        val coverage = TracingEvaluator.computeCoverage(guide, swipe, tolerancePx = tolerance)

        assertTrue("a straight swipe under an alif must not count as tracing it", coverage < 0.7f)
    }
}
