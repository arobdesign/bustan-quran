package com.bustanquran.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanShapeIsland
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Island from the adventure map: `border-radius: 50% 50% 46% 46%` with a
 * `border-bottom: 10px` band in a darker shade, which is how the design fakes
 * a 3D edge. Locked islands are drawn at 75 % opacity, exactly like the
 * `Story Island` in `Home.dc.html`.
 */
@Composable
fun IslandTile(
    fillColor: Color,
    edgeColor: Color,
    modifier: Modifier = Modifier,
    width: Dp = 130.dp,
    height: Dp = 96.dp,
    edgeHeight: Dp = 10.dp,
    locked: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    var surface = modifier
        .size(width = width, height = height)
        .alpha(if (locked) 0.75f else 1f)
        .drawBehind {
            val path = BustanShapeIsland.toPath(
                size = size,
                layoutDirection = layoutDirection,
                density = this,
            )
            drawPath(path = path, color = fillColor)
            val band = edgeHeight.toPx()
            clipPath(path) {
                drawRect(
                    color = edgeColor,
                    topLeft = Offset(x = 0f, y = size.height - band),
                    size = Size(width = size.width, height = band),
                )
            }
        }
    if (onClick != null) {
        surface = surface.clickable(role = Role.Button, onClick = onClick)
    }
    Box(modifier = surface, contentAlignment = Alignment.Center) {
        content()
    }
}

/** White pill label under an island ("Letter Island"). */
@Composable
fun IslandLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(BustanShapePill)
            .background(BustanWhite)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = BustanTextStyles.buttonMedium,
            textAlign = TextAlign.Center,
        )
    }
}

/** Coral "Next" badge that sits on the corner of the island ahead. */
@Composable
fun IslandNextBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 22.dp)
            .clip(BustanShapePill)
            .background(BustanCoral)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        Text(text = text, style = BustanTextStyles.captionSmall, color = BustanWhite)
    }
}
