package com.bustanquran.core.ui

import androidx.compose.ui.graphics.Color
import com.bustanquran.core.ui.theme.QpkInk
import com.bustanquran.core.ui.theme.QpkMint
import com.bustanquran.core.ui.theme.QpkSand
import com.bustanquran.core.ui.theme.QpkSaffron
import com.bustanquran.core.ui.theme.QpkTeal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ThemeTokensTest {

    @Test
    fun testCoreColorTokensDefined() {
        assertNotNull(QpkSand)
        assertNotNull(QpkInk)
        assertNotNull(QpkTeal)
        assertNotNull(QpkMint)
        assertNotNull(QpkSaffron)

        // Verify hex colors match CLAUDE.md specification exactly
        assertEquals(Color(0xFFFBF6EC), QpkSand)
        assertEquals(Color(0xFF23302C), QpkInk)
        assertEquals(Color(0xFF0F6B5C), QpkTeal)
        assertEquals(Color(0xFFDCEFE9), QpkMint)
        assertEquals(Color(0xFFF2B33D), QpkSaffron)
    }
}
