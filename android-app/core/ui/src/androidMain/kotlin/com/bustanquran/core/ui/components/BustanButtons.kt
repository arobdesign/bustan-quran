package com.bustanquran.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * The design's primary pill: 56 dp tall, radius 999, teal background, Baloo 2
 * 20/800 white label ("Get started", "Next ayah").
 *
 * `isLargeTouchTarget` raises the height to 64 dp for ages 3-4.
 */
@Composable
fun BustanPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLargeTouchTarget: Boolean = false,
    containerColor: Color = BustanTeal,
    contentColor: Color = BustanWhite,
) {
    BustanButtonSurface(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        minHeight = if (isLargeTouchTarget) 64.dp else 56.dp,
        containerColor = containerColor,
        contentColor = contentColor,
        textStyle = BustanTextStyles.buttonLarge,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    )
}

/**
 * The quiet white pill under a primary action: 52 dp tall, Nunito 16/800 ink
 * ("Back to the map", "I already have an account").
 */
@Composable
fun BustanSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLargeTouchTarget: Boolean = false,
    containerColor: Color = BustanWhite,
    contentColor: Color = BustanInk,
    shadowColor: Color? = null,
) {
    var surface = modifier
    if (shadowColor != null) {
        surface = surface.bustanShadow(color = shadowColor, shape = BustanShapePill)
    }
    BustanButtonSurface(
        text = text,
        onClick = onClick,
        modifier = surface,
        enabled = enabled,
        minHeight = if (isLargeTouchTarget) 64.dp else 52.dp,
        containerColor = containerColor,
        contentColor = contentColor,
        textStyle = BustanTextStyles.bodyMedium,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    )
}

@Composable
internal fun BustanButtonSurface(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    minHeight: Dp,
    containerColor: Color,
    contentColor: Color,
    textStyle: TextStyle,
    contentPadding: PaddingValues,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .clip(BustanShapePill)
            .background(containerColor)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .alpha(if (enabled) 1f else 0.45f)
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = textStyle,
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}
