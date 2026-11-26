package com.bustanquran.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanSwitchOff
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * The settings switch: 52x32 dp track, 24 dp knob inset by 4 dp, teal when on
 * and `#CFC7B5` when off (see `design/source/Settings.dc.html`).
 *
 * Material's `Switch` is not used because its track height, thumb size and
 * inset do not match the design.
 */
@Composable
fun BustanSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checkedTrackColor: Color = BustanTeal,
    uncheckedTrackColor: Color = BustanSwitchOff,
    thumbColor: Color = BustanWhite,
) {
    val width: Dp = 52.dp
    val height: Dp = 32.dp
    val knobSize: Dp = 24.dp
    val inset: Dp = 4.dp
    val knobOffset by animateDpAsState(
        targetValue = if (checked) width - knobSize - inset else inset,
        label = "BustanSwitchKnob",
    )
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(BustanShapePill)
            .background(if (checked) checkedTrackColor else uncheckedTrackColor)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(knobSize)
                .clip(CircleShape)
                .background(thumbColor),
        )
    }
}
