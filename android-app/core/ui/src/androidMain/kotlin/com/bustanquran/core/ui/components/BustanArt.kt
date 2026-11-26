package com.bustanquran.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanBlueArtMid
import com.bustanquran.core.ui.theme.BustanCamelArt
import com.bustanquran.core.ui.theme.BustanCamelArtEdge
import com.bustanquran.core.ui.theme.BustanCamelFur
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanGreenArt
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * The design's illustrations, rebuilt as [ImageVector]s. Each path is copied
 * verbatim from the inline `<svg>` in `design/source`, because P-18 forbids
 * swapping the artwork for emoji or a different illustration.
 */
object BustanArt {

    /** 72×72 app mark: teal rounded square, sand book, saffron dot. */
    val AppLogo: ImageVector = artVector(
        name = "BustanLogo",
        viewport = 72f,
        paths = listOf(
            ArtPath(
                data = "M22 0h28a22 22 0 0 1 22 22v28a22 22 0 0 1-22 22H22A22 22 0 0 1 0 72V22A22 22 0 0 1 22 0z",
                fill = BustanTeal,
            ),
            ArtPath(data = "M18 46L36 40L54 46L54 26L36 20L18 26Z M36 20V40", stroke = BustanSand, strokeWidth = 3f),
            ArtPath(data = circlePath(cx = 52f, cy = 17f, radius = 5f), fill = BustanSaffron),
        ),
    )

    /**
     * Same 72×72 mark on a sand tile, as drawn in `Loading.dc.html`: the
     * background and the book swap colours compared with [AppLogo].
     */
    val AppLogoOnSand: ImageVector = artVector(
        name = "BustanLogoOnSand",
        viewport = 72f,
        paths = listOf(
            ArtPath(
                data = "M22 0h28a22 22 0 0 1 22 22v28a22 22 0 0 1-22 22H22A22 22 0 0 1 0 72V22A22 22 0 0 1 22 0z",
                fill = BustanSand,
            ),
            ArtPath(data = "M18 46L36 40L54 46L54 26L36 20L18 26Z M36 20V40", stroke = BustanTeal, strokeWidth = 3f),
            ArtPath(data = circlePath(cx = 52f, cy = 17f, radius = 5f), fill = BustanSaffron),
        ),
    )

    /**
     * The camel illustration on the reward card (150×130), copied from
     * `Reward.dc.html`. Distinct from [CamelAvatar], which is the round
     * profile head.
     */
    val CamelScene: ImageVector = artVector(
        name = "BustanCamelScene",
        viewportWidth = 150f,
        viewportHeight = 130f,
        paths = listOf(
            ArtPath(
                data = "M30 70Q40 30 62 52Q74 22 92 52Q100 62 112 54L118 30Q122 20 132 24L136 34L126 38L120 70" +
                    "Q118 84 104 86L104 124L94 124L94 88L58 88L58 124L48 124L48 86Q30 84 30 70Z",
                fill = BustanCamelFur,
            ),
            ArtPath(data = circlePath(cx = 129f, cy = 29f, radius = 2f), fill = BustanInk),
        ),
    )

    /** Hud-hud the guide bird (onboarding, reward, empty states). */
    val HudHud: ImageVector = artVector(
        name = "BustanHudHud",
        viewport = 84f,
        paths = listOf(
            ArtPath(data = ellipsePath(cx = 42f, cy = 52f, rx = 26f, ry = 24f), fill = BustanCamelArt),
            ArtPath(data = "M30 26L26 10L36 22L40 6L44 22L54 10L50 26Z", fill = BustanCoral),
            ArtPath(
                data = circlePath(cx = 34f, cy = 46f, radius = 6f) + circlePath(cx = 50f, cy = 46f, radius = 6f),
                fill = BustanWhite,
            ),
            ArtPath(
                data = circlePath(cx = 34f, cy = 46f, radius = 3f) + circlePath(cx = 50f, cy = 46f, radius = 3f),
                fill = BustanInk,
            ),
            ArtPath(data = "M42 52L60 56L42 58Z", fill = BustanInk),
        ),
    )

    /** Camel avatar, first child profile. */
    val CamelAvatar: ImageVector = artVector(
        name = "BustanAvatarCamel",
        viewport = 56f,
        paths = listOf(
            ArtPath(
                data = ellipsePath(cx = 28f, cy = 34f, rx = 16f, ry = 13f) +
                    ellipsePath(cx = 16f, cy = 20f, rx = 5f, ry = 7f) +
                    ellipsePath(cx = 40f, cy = 20f, rx = 5f, ry = 7f),
                fill = BustanCamelArt,
            ),
            ArtPath(
                data = circlePath(cx = 22f, cy = 31f, radius = 2.5f) +
                    circlePath(cx = 34f, cy = 31f, radius = 2.5f),
                fill = BustanInk,
            ),
            ArtPath(data = ellipsePath(cx = 28f, cy = 40f, rx = 5f, ry = 3f), fill = BustanCamelArtEdge),
        ),
    )

    /** Bear avatar, second child profile. */
    val BearAvatar: ImageVector = artVector(
        name = "BustanAvatarBear",
        viewport = 56f,
        paths = listOf(
            ArtPath(data = circlePath(cx = 28f, cy = 30f, radius = 16f), fill = BustanGreenArt),
            ArtPath(data = "M18 14L24 20M38 14L32 20", stroke = BustanGreenArt, strokeWidth = 4f),
            ArtPath(
                data = circlePath(cx = 22f, cy = 28f, radius = 5f) +
                    circlePath(cx = 34f, cy = 28f, radius = 5f),
                fill = BustanWhite,
            ),
            ArtPath(
                data = circlePath(cx = 22f, cy = 28f, radius = 2.5f) +
                    circlePath(cx = 34f, cy = 28f, radius = 2.5f),
                fill = BustanInk,
            ),
            ArtPath(data = "M26 36L28 39L30 36Z", fill = BustanSaffron),
        ),
    )

    /** Bird avatar, third child profile. */
    val BirdAvatar: ImageVector = artVector(
        name = "BustanAvatarBird",
        viewport = 56f,
        paths = listOf(
            ArtPath(data = ellipsePath(cx = 28f, cy = 32f, rx = 15f, ry = 16f), fill = BustanBlueArtMid),
            ArtPath(data = "M20 14Q28 4 36 14", stroke = BustanCoral, strokeWidth = 5f),
            ArtPath(
                data = circlePath(cx = 23f, cy = 28f, radius = 2.5f) +
                    circlePath(cx = 33f, cy = 28f, radius = 2.5f),
                fill = BustanInk,
            ),
            ArtPath(data = "M25 35L28 38L31 35Z", fill = BustanSaffron),
        ),
    )
}

private data class ArtPath(
    val data: String,
    val fill: Color? = null,
    val stroke: Color? = null,
    val strokeWidth: Float = 0f,
)

private fun artVector(
    name: String,
    viewport: Float = 24f,
    viewportWidth: Float = viewport,
    viewportHeight: Float = viewport,
    paths: List<ArtPath>,
): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = viewportWidth.dp,
        defaultHeight = viewportHeight.dp,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
    )
    paths.forEach { art ->
        builder.addPath(
            pathData = PathParser().parsePathString(art.data).toNodes(),
            fill = art.fill?.let(::SolidColor),
            stroke = art.stroke?.let(::SolidColor),
            strokeLineWidth = art.strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
    return builder.build()
}

private fun circlePath(cx: Float, cy: Float, radius: Float): String =
    "M${cx - radius} $cy" +
        "a$radius $radius 0 1 0 ${radius * 2} 0" +
        "a$radius $radius 0 1 0 ${-radius * 2} 0z"

private fun ellipsePath(cx: Float, cy: Float, rx: Float, ry: Float): String =
    "M${cx - rx} $cy" +
        "a$rx $ry 0 1 0 ${rx * 2} 0" +
        "a$rx $ry 0 1 0 ${-rx * 2} 0z"

/** Hud-hud, drawn at the size the design gives it. */
@Composable
fun HudHudMascot(
    modifier: Modifier = Modifier,
    size: Dp = 84.dp,
) {
    Icon(
        imageVector = BustanArt.HudHud,
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier.size(size),
    )
}

@Composable
fun BustanAppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
) {
    Icon(
        imageVector = BustanArt.AppLogo,
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier.size(size),
    )
}

/** The three child avatars shown on the profile screen. */
enum class AnimalAvatar { Camel, Bear, Bird }

@Composable
fun AnimalAvatarImage(
    avatar: AnimalAvatar,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    Icon(
        imageVector = avatar.vector,
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier.size(size),
    )
}

private val AnimalAvatar.vector: ImageVector
    get() = when (this) {
        AnimalAvatar.Camel -> BustanArt.CamelAvatar
        AnimalAvatar.Bear -> BustanArt.BearAvatar
        AnimalAvatar.Bird -> BustanArt.BirdAvatar
    }
