package com.bustanquran.feature.islamic

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TracingEvaluatorTest {
    private val guide = listOf(
        Offset(100f, 50f),
        Offset(100f, 100f),
        Offset(100f, 150f),
        Offset(100f, 200f)
    )

    @Test
    fun `empty drawn points is not accepted`() {
        assertFalse(TracingEvaluator.isAccepted(guide, emptyList()))
    }

    @Test
    fun `covering 75 percent of guide points is accepted`() {
        val drawn = listOf(Offset(102f, 52f), Offset(98f, 101f), Offset(101f, 149f))
        assertTrue(TracingEvaluator.isAccepted(guide, drawn))
    }

    @Test
    fun `covering 50 percent is rejected`() {
        val drawn = listOf(Offset(100f, 50f), Offset(100f, 100f))
        assertFalse(TracingEvaluator.isAccepted(guide, drawn))
    }
}
