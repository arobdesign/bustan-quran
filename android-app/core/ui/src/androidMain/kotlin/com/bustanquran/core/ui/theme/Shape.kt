package com.bustanquran.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Corner radii taken from the design sources. The design uses a small fixed
 * set of radii, so each token is named after the element that uses it.
 */

/** Number tile (36 dp) and colour swatches. */
val BustanShapeTile = RoundedCornerShape(10.dp)

/** Nested illustration inside a card, chip row. */
val BustanShapeSmall = RoundedCornerShape(12.dp)

/** Back button tile, search field, settings gear tile. */
val BustanShapeControl = RoundedCornerShape(14.dp)

/** Language tile, list card, sheet body, map search field. */
val BustanShapeField = RoundedCornerShape(16.dp)

/** Settings group card, game card, icon square, level pill. */
val BustanShapeCardSmall = RoundedCornerShape(18.dp)

/** Goal card, dashboard card. */
val BustanShapeCard = RoundedCornerShape(20.dp)

/** Dark lock tile. */
val BustanShapeCardLarge = RoundedCornerShape(22.dp)

/** Profile card, dialog card, photo card, bottom-nav top corners. */
val BustanShapeProfile = RoundedCornerShape(24.dp)

/** Bottom sheet top corners. */
val BustanShapeSheet = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

/** Island tile: CSS `border-radius: 50% 50% 46% 46%`. */
val BustanShapeIsland = RoundedCornerShape(
    topStartPercent = 50,
    topEndPercent = 50,
    bottomEndPercent = 46,
    bottomStartPercent = 46,
)

/** Every button, badge, chip, progress bar and segmented control. */
val BustanShapePill = RoundedCornerShape(percent = 50)

val BustanShapes = Shapes(
    extraSmall = BustanShapeTile,
    small = BustanShapeSmall,
    medium = BustanShapeField,
    large = BustanShapeCard,
    extraLarge = BustanShapeProfile,
)

// Legacy aliases ------------------------------------------------------------
val QpkShapes = BustanShapes
val QpkPillShape = BustanShapePill
val QpkCardShapeMedium = BustanShapeField
val QpkCardShapeLarge = BustanShapeProfile
