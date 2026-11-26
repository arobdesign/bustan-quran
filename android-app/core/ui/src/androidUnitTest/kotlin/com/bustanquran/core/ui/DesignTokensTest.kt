package com.bustanquran.core.ui

import androidx.compose.ui.graphics.Color
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanDivider
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanParentBg
import com.bustanquran.core.ui.theme.BustanProgressTrack
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanSegmentedTrack
import com.bustanquran.core.ui.theme.BustanSky
import com.bustanquran.core.ui.theme.BustanSwitchOff
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanWhite
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Locks the design tokens to the table in `design/README.md`.
 *
 * The colours are written here as hex literals on purpose: if somebody edits a
 * token, this test is the thing that has to be changed too, which keeps the
 * palette and the design document from drifting apart.
 */
class DesignTokensTest {

    @Test
    fun `core palette matches the design document`() {
        assertEquals(Color(0xFFFBF6EC), BustanSand)
        assertEquals(Color(0xFFF4F1EA), BustanParentBg)
        assertEquals(Color(0xFF23302C), BustanInk)
        assertEquals(Color(0xFF5B6661), BustanMuted)
        assertEquals(Color(0xFF0F6B5C), BustanTeal)
        assertEquals(Color(0xFF0A4F44), BustanTealDark)
        assertEquals(Color(0xFFDCEFE9), BustanMint)
        assertEquals(Color(0xFFF2B33D), BustanSaffron)
        assertEquals(Color(0xFFFCE7B8), BustanButter)
        assertEquals(Color(0xFFE0734F), BustanCoral)
        assertEquals(Color(0xFFCFE7F3), BustanSky)
        assertEquals(Color(0xFFFFFFFF), BustanWhite)
    }

    @Test
    fun `lines and component fills match the design sources`() {
        assertEquals(Color(0xFFE3D8C1), BustanBorder)
        assertEquals(Color(0xFFEADFC8), BustanBorderSoft)
        assertEquals(Color(0xFFEEE8DA), BustanDivider)
        assertEquals(Color(0xFFE8EEEC), BustanProgressTrack)
        assertEquals(Color(0xFFEFE7D6), BustanSegmentedTrack)
        assertEquals(Color(0xFFCFC7B5), BustanSwitchOff)
    }
}
