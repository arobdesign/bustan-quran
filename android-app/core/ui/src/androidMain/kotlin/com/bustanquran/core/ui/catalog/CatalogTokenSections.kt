package com.bustanquran.core.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.components.HudHudMascot
import com.bustanquran.core.ui.theme.BustanAmberText
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanParentBg
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanSky
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

@Composable
internal fun CatalogHeaderSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HudHudMascot(size = 72.dp)
        Text(text = "Design catalog", style = BustanTextStyles.displayMedium, color = BustanTeal)
        Text(
            text = "P-18 STEP 1 · tokens, type and shared components",
            style = BustanTextStyles.caption,
            color = BustanMuted,
        )
    }
}

@Composable
internal fun CatalogColorsSection() {
    CatalogSection(title = "COLOUR TOKENS · design/README.md") {
        listOf(
            "sand #FBF6EC" to BustanSand,
            "parent-bg #F4F1EA" to BustanParentBg,
            "ink #23302C" to BustanInk,
            "muted #5B6661" to BustanMuted,
            "teal #0F6B5C" to BustanTeal,
            "teal-dark #0A4F44" to BustanTealDark,
            "mint #DCEFE9" to BustanMint,
            "saffron #F2B33D" to BustanSaffron,
            "butter #FCE7B8" to BustanButter,
            "coral #E0734F" to BustanCoral,
            "sky #CFE7F3" to BustanSky,
            "border #E3D8C1" to BustanBorder,
            "border-soft #EADFC8" to BustanBorderSoft,
            "amber-text #8A5A10" to BustanAmberText,
            "white #FFFFFF" to BustanWhite,
        ).forEach { (label, color) -> Swatch(name = label, color = color) }
    }
}

@Composable
internal fun CatalogTypographySection() {
    CatalogSection(title = "TYPOGRAPHY · Baloo 2 / Nunito / Amiri Quran") {
        Text(text = "Learn the Qur'an through play", style = BustanTextStyles.displayHero, color = BustanInk)
        Text(text = "MashaAllah, Yusuf!", style = BustanTextStyles.displayLarge, color = BustanTeal)
        Text(text = "Bustan Qur'an", style = BustanTextStyles.displayMedium, color = BustanTeal)
        Text(text = "This week", style = BustanTextStyles.titleLarge, color = BustanInk)
        Text(text = "The Camel", style = BustanTextStyles.titleSmall, color = BustanInk)
        Text(text = "Letters, short surahs and memorisation.", style = BustanTextStyles.bodyLarge, color = BustanInk)
        Text(text = "For parents", style = BustanTextStyles.labelLarge, color = BustanInk)
        Text(text = "App language · English", style = BustanTextStyles.labelMedium, color = BustanMuted)
        Text(text = "Memorised", style = BustanTextStyles.micro, color = BustanTealDark)
        Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            style = BustanTextStyles.quranSmall,
            color = BustanTeal,
        )
    }
}
