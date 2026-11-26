package com.bustanquran.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Legacy primary button, now the design's 56 dp teal pill
 * (see [BustanPrimaryButton]). `isLargeTouchTarget` gives 64 dp for ages 3–4.
 */
@Composable
fun QpkPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLargeTouchTarget: Boolean = false,
    containerColor: Color = BustanTeal,
    contentColor: Color = BustanWhite,
) {
    BustanPrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLargeTouchTarget = isLargeTouchTarget,
        containerColor = containerColor,
        contentColor = contentColor,
    )
}
