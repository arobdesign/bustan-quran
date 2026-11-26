package com.bustanquran.core.ui.components

import androidx.compose.ui.graphics.vector.ImageVector
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSaffronStroke

/**
 * Glyph-for-glyph copies of the icons the design draws inline. Nothing here
 * comes from `Icons.Default.*`: P-18 forbids replacing the design's own icon
 * set with Material ones, emoji or different art.
 *
 * Every vector is stroked in black and meant to be tinted at the call site via
 * `Icon(tint = ...)`; [Star] is the one exception because the design fills it
 * with saffron and outlines it in `#C98A12`.
 */
object BustanIcons {

    /** Map tab: folded map with two creases. */
    val Map: ImageVector = buildIcon(
        name = "BustanMap",
        paths = listOf("M3 7l6-3 6 3 6-3v13l-6 3-6-3-6 3z", "M9 4v13M15 7v13"),
    )

    /** Qur'an tab. */
    val Quran: ImageVector = buildIcon(
        name = "BustanQuran",
        paths = listOf("M4 19V5a2 2 0 0 1 2-2h12v16H6a2 2 0 0 0-2 2"),
    )

    /** Qur'an with text lines, used inside the goal card. */
    val QuranLines: ImageVector = buildIcon(
        name = "BustanQuranLines",
        paths = listOf(
            "M4 19V5a2 2 0 0 1 2-2h12v16H6a2 2 0 0 0-2 2",
            "M9 8h6M9 12h4",
        ),
    )

    /** My cards tab: a card with a star. */
    val Cards: ImageVector = buildIcon(
        name = "BustanCards",
        paths = listOf(
            "M7 3h10a3 3 0 0 1 3 3v12a3 3 0 0 1-3 3H7a3 3 0 0 1-3-3V6a3 3 0 0 1 3-3z",
            "M12 8l1.5 3 3 .4-2.2 2 .6 3-2.9-1.6-2.9 1.6.6-3-2.2-2 3-.4z",
        ),
    )

    /** Filled saffron star with the design's `#C98A12` outline. */
    val Star: ImageVector = buildIcon(
        name = "BustanStar",
        paths = listOf("M12 2.5l2.9 6 6.6.8-4.9 4.5 1.3 6.5L12 17l-5.9 3.3 1.3-6.5L2.5 9.3l6.6-.8z"),
        strokeWidth = 1f,
        filled = true,
        fillColor = BustanSaffron,
        strokeColor = BustanSaffronStroke,
    )

    /** Lock: locked islands, parent gate, privacy rows. */
    val Lock: ImageVector = buildIcon(
        name = "BustanLock",
        paths = listOf(
            "M7 11h10a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2z",
            "M8 11V8a4 4 0 0 1 8 0v3",
        ),
        strokeWidth = 2.4f,
    )

    /** Back chevron: `M15 5l-7 7 7 7`. */
    val ChevronLeft: ImageVector = buildIcon(
        name = "BustanChevronLeft",
        paths = listOf("M15 5l-7 7 7 7"),
        strokeWidth = 2.6f,
    )

    /** Forward chevron for "›" affordances in list rows. */
    val ChevronRight: ImageVector = buildIcon(
        name = "BustanChevronRight",
        paths = listOf("M9 5l7 7-7 7"),
        strokeWidth = 2.4f,
    )

    /** Tick: a finished review task in the hafalan map (`M5 12l5 5 9-10`). */
    val Check: ImageVector = buildIcon(
        name = "BustanCheck",
        paths = listOf("M5 12l5 5 9-10"),
        strokeWidth = 2.8f,
    )

    /** Search field magnifier. */
    val Search: ImageVector = buildIcon(
        name = "BustanSearch",
        paths = listOf("M11 4a7 7 0 1 0 0 14 7 7 0 0 0 0-14z", "M20 20l-4-4"),
        strokeWidth = 2.4f,
    )

    /** Microphone: du'a island, recording and voice-check screens. */
    val Mic: ImageVector = buildIcon(
        name = "BustanMic",
        paths = listOf(
            "M12 3a4 4 0 0 0-4 4v4a4 4 0 0 0 8 0V7a4 4 0 0 0-1.2-2.8",
            "M5 11a7 7 0 0 0 14 0M12 18v3",
        ),
    )

    /** Open book: Surah Island, mushaf rows. */
    val Book: ImageVector = buildIcon(
        name = "BustanBook",
        paths = listOf("M4 36L28 30L52 36L52 8L28 2L4 8Z", "M28 2V30"),
        viewportWidth = 56f,
        viewportHeight = 44f,
        strokeWidth = 2.5f,
    )

    /** Filled play triangle for "Play recording" pills. */
    val Play: ImageVector = buildIcon(
        name = "BustanPlay",
        paths = listOf("M8 5v14l11-7z"),
        filled = true,
        strokeWidth = 0f,
    )

    /** Download arrow for the audio download screens. */
    val Download: ImageVector = buildIcon(
        name = "BustanDownload",
        paths = listOf("M12 3v12M7 11l5 5 5-5M4 21h16"),
    )

    /** Settings gear (parent dashboard tile). */
    val Gear: ImageVector = buildIcon(
        name = "BustanGear",
        paths = listOf(
            "M12 9a3 3 0 1 0 0 6 3 3 0 0 0 0-6z",
            "M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 " +
                "1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 " +
                "1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 " +
                "1.5-1.1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3H9a1.7 1.7 0 0 " +
                "0 1-1.5V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 " +
                "1.7 0 0 0-.3 1.8V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z",
        ),
        strokeWidth = 2f,
    )

    /** Dashed "add child" plus, 48 dp viewport. */
    val Plus: ImageVector = buildIcon(
        name = "BustanPlus",
        paths = listOf("M46 24a22 22 0 1 1-44 0 22 22 0 0 1 44 0z", "M24 15v18M15 24h18"),
        viewportWidth = 48f,
        viewportHeight = 48f,
        strokeWidth = 2.5f,
    )

    /** Speaker with two waves: "hear the letter again" in `BalonHuruf`. */
    val Speaker: ImageVector = buildIcon(
        name = "BustanSpeaker",
        paths = listOf("M4 9v6h4l5 4V5L8 9z", "M16.5 8.5a5 5 0 0 1 0 7M19 6a8.5 8.5 0 0 1 0 12"),
    )

    /** Pause bars for the player circle button. */
    val Pause: ImageVector = buildIcon(
        name = "BustanPause",
        paths = listOf("M6 5h4v14H6z", "M14 5h4v14h-4z"),
        strokeWidth = 0f,
        filled = true,
    )

    /** "Previous ayah": triangle plus bar (`M18 6l-8 6 8 6zM6 6v12`). */
    val SkipBack: ImageVector = buildIcon(
        name = "BustanSkipBack",
        paths = listOf("M18 6l-8 6 8 6zM6 6v12"),
        strokeWidth = 2.4f,
    )

    /** "Next ayah": bar plus triangle (`M6 6l8 6-8 6zM18 6v12`). */
    val SkipForward: ImageVector = buildIcon(
        name = "BustanSkipForward",
        paths = listOf("M6 6l8 6-8 6zM18 6v12"),
        strokeWidth = 2.4f,
    )
}
