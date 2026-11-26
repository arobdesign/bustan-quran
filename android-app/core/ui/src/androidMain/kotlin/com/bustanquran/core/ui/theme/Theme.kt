package com.bustanquran.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Material 3 colour scheme built only from the design tokens. Screens should
 * still prefer the named tokens directly; this mapping exists so Material
 * components that read `MaterialTheme.colorScheme` stay on-palette.
 */
private val BustanLightColorScheme = lightColorScheme(
    primary = BustanTeal,
    onPrimary = BustanWhite,
    primaryContainer = BustanMint,
    onPrimaryContainer = BustanTealDark,
    secondary = BustanSaffron,
    onSecondary = BustanInk,
    secondaryContainer = BustanButter,
    onSecondaryContainer = BustanAmberText,
    tertiary = BustanCoral,
    onTertiary = BustanWhite,
    background = BustanSand,
    onBackground = BustanInk,
    surface = BustanWhite,
    onSurface = BustanInk,
    surfaceVariant = BustanSand,
    onSurfaceVariant = BustanMuted,
    outline = BustanBorder,
    outlineVariant = BustanDivider,
    error = BustanCoralText,
    onError = BustanWhite,
)

@Composable
fun BustanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BustanLightColorScheme,
        typography = QpkTypography,
        shapes = BustanShapes,
        content = content,
    )
}

/** Legacy name kept for the screens written before P-18. */
@Composable
fun QpkTheme(content: @Composable () -> Unit) = BustanTheme(content)
