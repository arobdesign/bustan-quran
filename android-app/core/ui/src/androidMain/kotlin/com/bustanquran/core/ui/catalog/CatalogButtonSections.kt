package com.bustanquran.core.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.components.BustanCardBadge
import com.bustanquran.core.ui.components.BustanFilterChip
import com.bustanquran.core.ui.components.BustanOutlineButton
import com.bustanquran.core.ui.components.BustanPillLink
import com.bustanquran.core.ui.components.BustanPrimaryButton
import com.bustanquran.core.ui.components.BustanSecondaryButton
import com.bustanquran.core.ui.components.BustanSegmentedControl
import com.bustanquran.core.ui.components.BustanStatusBadge
import com.bustanquran.core.ui.components.BustanSwitch
import com.bustanquran.core.ui.theme.BustanAmberText
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTextStyles

@Composable
internal fun CatalogButtonSection() {
    CatalogSection(title = "BUTTONS · 56 / 52 / 48 / 40 dp pills") {
        BustanPrimaryButton(text = "Get started", onClick = {})
        BustanPrimaryButton(text = "Next ayah", onClick = {}, isLargeTouchTarget = true)
        BustanSecondaryButton(text = "Back to the map", onClick = {})
        BustanOutlineButton(text = "For parents", onClick = {})
        BustanOutlineButton(text = "Play recording", onClick = {}, accent = true)
        BustanPillLink(text = "Full weekly report ›", onClick = {})
    }
}

@Composable
internal fun CatalogChipSection() {
    var tab by remember { mutableStateOf(0) }
    val tabs = listOf("Juz 'Amma", "All 30 juz", "Downloaded")
    CatalogSection(title = "CHIPS · BADGES") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tabs.forEachIndexed { index, label ->
                BustanFilterChip(
                    label = label,
                    selected = tab == index,
                    onClick = { tab = index },
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BustanStatusBadge(
                text = "Memorised",
                containerColor = BustanMint,
                contentColor = BustanTealDark,
            )
            BustanStatusBadge(
                text = "Learning",
                containerColor = BustanButter,
                contentColor = BustanAmberText,
            )
            BustanCardBadge(text = "New card")
        }
    }
}

@Composable
internal fun CatalogControlSection() {
    val modes = listOf("Loop per ayah", "Whole surah")
    var mode by remember { mutableStateOf(modes.first()) }
    var soundsOn by remember { mutableStateOf(true) }
    CatalogSection(title = "CONTROLS · segmented + switch") {
        BustanSegmentedControl(
            options = modes,
            selected = mode,
            onSelect = { mode = it },
            label = { it },
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            BustanSwitch(checked = soundsOn, onCheckedChange = { soundsOn = it })
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (soundsOn) "on" else "off",
                style = BustanTextStyles.caption,
            )
            Spacer(modifier = Modifier.width(16.dp))
            BustanSwitch(checked = false, onCheckedChange = {})
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "off", style = BustanTextStyles.caption)
        }
    }
}
