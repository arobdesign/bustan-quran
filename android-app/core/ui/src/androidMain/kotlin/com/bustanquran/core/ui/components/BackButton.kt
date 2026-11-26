package com.bustanquran.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.R
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Back control from the design: a 44 dp white tile, radius 14, with the
 * design's chevron (`M15 5l-7 7 7 7`, 2.6 stroke). `GameList` adds the flat
 * `0 2px 0` shadow, the parent screens do not, hence the optional
 * [shadowColor].
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.back),
    tint: Color = BustanInk,
    isLargeTouchTarget: Boolean = false,
    size: Dp = if (isLargeTouchTarget) 64.dp else 44.dp,
    containerColor: Color = BustanWhite,
    shadowColor: Color? = null,
) {
    BustanIconTile(
        icon = BustanIcons.ChevronLeft,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        size = size,
        iconSize = 22.dp,
        containerColor = containerColor,
        contentColor = tint,
        shadowColor = shadowColor,
    )
}
