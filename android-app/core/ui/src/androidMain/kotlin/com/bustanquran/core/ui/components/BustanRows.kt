package com.bustanquran.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanDivider
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanShapeCardSmall
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles

/**
 * The settings/statistics rows of `Settings.dc.html` and
 * `ParentDashboard.dc.html`: white group card, radius 18, 46-50 dp rows,
 * `1px #EEE8DA` dividers, label 14/700 and value 14/800 in teal.
 */
@Composable
fun BustanSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = BustanTextStyles.eyebrow,
        color = BustanMuted,
        modifier = modifier,
    )
}

@Composable
fun BustanGroupCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    BustanCard(
        modifier = modifier.fillMaxWidth(),
        shape = BustanShapeCardSmall,
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
fun BustanDivider(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BustanDivider),
    )
}

/** Label on the left, teal value on the right, optional "›". */
@Composable
fun BustanValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = true,
    showChevron: Boolean = false,
    minHeight: Dp = 46.dp,
    valueColor: Color = BustanTeal,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        var row = Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
        if (onClick != null) {
            row = row.clickable(role = Role.Button, onClick = onClick)
        }
        Row(
            modifier = row,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = BustanTextStyles.labelMedium, color = BustanInk)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, style = BustanTextStyles.labelMediumStrong, color = valueColor)
                if (showChevron) {
                    Icon(
                        imageVector = BustanIcons.ChevronRight,
                        contentDescription = null,
                        tint = valueColor,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(14.dp),
                    )
                }
            }
        }
        if (showDivider) {
            BustanDivider()
        }
    }
}

/** Label + hint on the left, [BustanSwitch] on the right. */
@Composable
fun BustanToggleRow(
    label: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = BustanTextStyles.labelMedium, color = BustanInk)
                Text(text = hint, style = BustanTextStyles.captionSmallSoft, color = BustanMuted)
            }
            BustanSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
        if (showDivider) {
            BustanDivider()
        }
    }
}

/** Stat card from the parent dashboard: big Baloo number over a 12/700 label. */
@Composable
fun BustanStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = BustanInk,
) {
    BustanCard(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(12.dp),
    ) {
        Text(text = value, style = BustanTextStyles.numericLarge, color = valueColor)
        Text(text = label, style = BustanTextStyles.captionSmall, color = BustanMuted)
    }
}
