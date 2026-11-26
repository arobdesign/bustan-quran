package com.bustanquran.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanShapeControl
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Square icon tile from the design. Three uses, all the same shape:
 *
 * - back button: 44 dp, radius 14, white, 22 dp chevron
 * - settings tile: 48 dp, radius 14, white, 22 dp gear
 * - goal card icon: 52 dp, radius 16, teal, 28 dp glyph
 */
@Composable
fun BustanIconTile(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    size: Dp = 44.dp,
    shape: Shape = BustanShapeControl,
    iconSize: Dp = 22.dp,
    containerColor: Color = BustanWhite,
    contentColor: Color = BustanInk,
    shadowColor: Color? = null,
) {
    var surface = modifier.size(size)
    if (shadowColor != null) {
        surface = surface.bustanShadow(color = shadowColor, shape = shape)
    }
    surface = surface.clip(shape).background(containerColor)
    if (onClick != null) {
        surface = surface.clickable(role = Role.Button, onClick = onClick)
    }
    Box(modifier = surface, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(iconSize),
        )
    }
}
