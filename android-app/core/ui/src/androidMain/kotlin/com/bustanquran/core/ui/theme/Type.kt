package com.bustanquran.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.R

/**
 * The three UI families plus the Qur'an placeholder font, bundled from Google
 * Fonts (OFL, see `core/ui/licenses/`).
 *
 * `core/ui` does not apply the Compose Multiplatform resources plugin, so the
 * fonts are Android font resources for now. When P-17 resumes and the theme
 * moves to `commonMain`, only these four `FontFamily` values change to
 * `Font(Res.font.*)` — no screen touches them directly.
 */
val BalooFontFamily = FontFamily(
    Font(R.font.baloo2_bold, FontWeight.Bold),
    Font(R.font.baloo2_extrabold, FontWeight.ExtraBold),
)

val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
)

val BalooBhaijaanFontFamily = FontFamily(
    Font(R.font.baloo_bhaijaan2_regular, FontWeight.Normal),
    Font(R.font.baloo_bhaijaan2_bold, FontWeight.Bold),
)

/** Placeholder until the licensed mushaf font is added (P-18 rule). */
val AmiriQuranFontFamily = FontFamily(
    Font(R.font.amiri_quran, FontWeight.Normal),
)

/**
 * Named text styles, one entry per typographic role in the design files.
 * Colours are deliberately absent: the caller supplies them through
 * `color = ...` so the same style works on sand, teal and white surfaces.
 */
object BustanTextStyles {

    /** Reward heading: Baloo 2 36/800, line-height 1.1. */
    val displayHero = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
    )

    /** Onboarding heading: Baloo 2 32/800, line-height 1.1. */
    val displayLarge = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 36.sp,
    )

    /** App title on the profile screen: Baloo 2 30/800. */
    val displayMedium = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 33.sp,
    )

    /** Parent-gate heading: Baloo 2 28/800, line-height 1.15. */
    val headlineLarge = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
    )

    /** "My Qur'an": Baloo 2 26/800. */
    val headlineMedium = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 29.sp,
    )

    /** Stat numbers on the parent dashboard: Baloo 2 26/800, line-height 1.1. */
    val numericLarge = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 28.sp,
    )

    /** Screen titles: Baloo 2 24/800, line-height 1.15. */
    val titleLarge = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
    )

    /** Greeting name: Baloo 2 22/800, line-height 1.1. */
    val titleMedium = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 24.sp,
    )

    /** Card titles, "The Camel": Baloo 2 20/700. */
    val titleSmall = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 23.sp,
    )

    /** Primary button label: Baloo 2 20/800 (min-height 56 dp). */
    val buttonLarge = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
    )

    /** Island label, secondary Baloo button: 16/800. */
    val buttonMedium = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
    )

    /** Count pill on the map ("128"): Baloo 2 18/800. */
    val buttonSmall = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        lineHeight = 21.sp,
    )

    /** Onboarding subtitle: Nunito 18/700. */
    val bodyLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    )

    /** Reward subtitle: Nunito 17/700. */
    val body = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 23.sp,
    )

    /** List titles, language tiles: Nunito 16/800. */
    val bodyMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    )

    /** "For parents" pill, card section titles: Nunito 15/800. */
    val labelLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    )

    /** Settings row labels: Nunito 14/700. */
    val labelMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 19.sp,
    )

    /** Settings row values, chips: Nunito 14/800. */
    val labelMediumStrong = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        lineHeight = 19.sp,
    )

    /** Nav labels and tags: Nunito 13/800. */
    val label = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    /** Tier badge text, captions: Nunito 13/700. */
    val caption = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    /** Footnotes: Nunito 13/600. */
    val captionSoft = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    /** Section eyebrow ("LANGUAGE & QUR'AN"): Nunito 12/800 + 0.06em tracking. */
    val eyebrow = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.72.sp,
    )

    /** Progress captions: Nunito 12/700. */
    val captionSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    /** Toggle hints: Nunito 12/600. */
    val captionSmallSoft = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    /** Status badges: Nunito 11/800. */
    val micro = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    )

    /** Qur'an text: Amiri Quran, a placeholder for the licensed mushaf font. */
    val quranBody = TextStyle(
        fontFamily = AmiriQuranFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 44.sp,
        // CSS line boxes are used in the design files; Android's extra font
        // padding would push the tall Amiri glyphs out of their tile.
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    )

    val quranSmall = TextStyle(
        fontFamily = AmiriQuranFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 34.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    )

    val quranIsland = TextStyle(
        fontFamily = AmiriQuranFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp,
        lineHeight = 40.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    )

    /** Arabic UI (non-Qur'an) copy: Baloo Bhaijaan 2 18/700. */
    val arabicUi = TextStyle(
        fontFamily = BalooBhaijaanFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    )
}

/** Material 3 slot mapping so `MaterialTheme.typography` also looks right. */
val QpkTypography = Typography(
    displayLarge = BustanTextStyles.displayHero,
    displayMedium = BustanTextStyles.displayLarge,
    displaySmall = BustanTextStyles.displayMedium,
    headlineLarge = BustanTextStyles.headlineLarge,
    headlineMedium = BustanTextStyles.headlineMedium,
    headlineSmall = BustanTextStyles.titleLarge,
    titleLarge = BustanTextStyles.titleMedium,
    titleMedium = BustanTextStyles.titleSmall,
    titleSmall = BustanTextStyles.bodyMedium,
    bodyLarge = BustanTextStyles.bodyLarge,
    bodyMedium = BustanTextStyles.body,
    bodySmall = BustanTextStyles.caption,
    labelLarge = BustanTextStyles.labelLarge,
    labelMedium = BustanTextStyles.caption,
    labelSmall = BustanTextStyles.micro,
)

/** Also exposed under the design-system name. */
val BustanTypography = QpkTypography
