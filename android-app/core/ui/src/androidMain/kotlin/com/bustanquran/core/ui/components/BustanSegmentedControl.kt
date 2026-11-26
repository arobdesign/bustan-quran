package com.bustanquran.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanSegmentedTrack
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Segmented control from `GameList.dc.html`: a `#EFE7D6` pill track with 4 dp
 * padding, and one 44 dp teal pill for the selected item (Nunito 13/800).
 *
 * Used for the age tabs on the games list, the loop mode on the murottal
 * player and the mushaf settings.
 */
@Composable
fun <T> BustanSegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    itemMinHeight: Dp = 44.dp,
    trackColor: Color = BustanSegmentedTrack,
    selectedContainerColor: Color = BustanTeal,
    selectedContentColor: Color = BustanWhite,
    contentColor: Color = BustanInk,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(BustanShapePill)
            .background(trackColor)
            .padding(4.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = itemMinHeight)
                    .clip(BustanShapePill)
                    .background(if (isSelected) selectedContainerColor else Color.Transparent)
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onSelect(option) },
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(option),
                    style = BustanTextStyles.label,
                    color = if (isSelected) selectedContentColor else contentColor,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
