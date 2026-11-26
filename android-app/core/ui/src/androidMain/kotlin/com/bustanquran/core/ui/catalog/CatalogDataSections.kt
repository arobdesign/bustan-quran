package com.bustanquran.core.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.components.BustanEmptyStars
import com.bustanquran.core.ui.components.BustanGroupCard
import com.bustanquran.core.ui.components.BustanLegendDot
import com.bustanquran.core.ui.components.BustanProgressBar
import com.bustanquran.core.ui.components.BustanProgressSegment
import com.bustanquran.core.ui.components.BustanSectionLabel
import com.bustanquran.core.ui.components.BustanStackedProgressBar
import com.bustanquran.core.ui.components.BustanStarRating
import com.bustanquran.core.ui.components.BustanToggleRow
import com.bustanquran.core.ui.components.BustanValueRow
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles

@Composable
internal fun CatalogRowSection() {
    var tajwid by remember { mutableStateOf(true) }
    CatalogSection(title = "ROWS · settings groups") {
        BustanSectionLabel(text = "LANGUAGE & QUR'AN")
        BustanGroupCard {
            BustanValueRow(label = "App language", value = "English")
            BustanValueRow(label = "Reciter", value = "Child voice")
            BustanValueRow(
                label = "Plan",
                value = "Free · Upgrade",
                showChevron = true,
                showDivider = false,
            )
        }
        BustanToggleRow(
            label = "Colour-coded tajwid",
            hint = "Colours for each tajwid rule",
            checked = tajwid,
            onCheckedChange = { tajwid = it },
            showDivider = false,
        )
    }
}

@Composable
internal fun CatalogProgressSection() {
    CatalogSection(title = "PROGRESS · 10 dp and 12 dp") {
        Text(
            text = "3 of 5 minutes",
            style = BustanTextStyles.captionSmall,
            color = BustanMuted,
        )
        BustanProgressBar(progress = 0.6f)
        BustanStackedProgressBar(
            segments = listOf(
                BustanProgressSegment(fraction = 0.24f, color = BustanTeal),
                BustanProgressSegment(fraction = 0.06f, color = BustanSaffron),
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            BustanLegendDot(color = BustanTeal, label = "Memorised")
            BustanLegendDot(color = BustanSaffron, label = "In progress")
        }
    }
}

@Composable
internal fun CatalogStarSection() {
    CatalogSection(title = "STARS · design 64 / 88 / 64") {
        BustanStarRating(stars = 3, starSize = 32.dp, emphasizeCenter = true)
        BustanStarRating(stars = 2, starSize = 32.dp)
        BustanEmptyStars(starSize = 24.dp)
    }
}
