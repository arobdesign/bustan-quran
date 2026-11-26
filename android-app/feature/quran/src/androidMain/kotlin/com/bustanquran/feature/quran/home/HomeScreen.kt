package com.bustanquran.feature.quran.home

import android.app.Activity
import android.content.ContextWrapper
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.bustanquran.core.ui.components.AnimalAvatar
import com.bustanquran.core.ui.components.AnimalAvatarImage
import com.bustanquran.core.ui.components.BustanIconTile
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.BustanProgressBar
import com.bustanquran.core.ui.components.IslandLabel
import com.bustanquran.core.ui.components.IslandNextBadge
import com.bustanquran.core.ui.components.IslandTile
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoralSoft
import com.bustanquran.core.ui.theme.BustanCoralSoftEdge
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanLilacArt
import com.bustanquran.core.ui.theme.BustanLilacArtEdge
import com.bustanquran.core.ui.theme.BustanMintArt
import com.bustanquran.core.ui.theme.BustanMintArtEdge
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSaffronDeep
import com.bustanquran.core.ui.theme.BustanSaffronFill
import com.bustanquran.core.ui.theme.BustanSky
import com.bustanquran.core.ui.theme.BustanSkyDeep
import com.bustanquran.core.ui.theme.BustanShapeField
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite
import com.bustanquran.feature.quran.R

/** Which illustration sits inside an island (design 02). */
private enum class IslandArt { Letters, Book, Dua, Locked }
private data class AdventureIsland(
    val titleRes: Int,
    val fill: Color,
    val edge: Color,
    val width: Dp,
    val height: Dp,
    val columnWidth: Dp,
    val x: Dp,
    val y: Dp,
    val route: String,
    val art: IslandArt,
    val minimumStage: Int = 0,
    val isNext: Boolean = false,
    val locked: Boolean = false,
)

private val MapViewportWidth = 390.dp
private val MapViewportHeight = 560.dp

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun HomeScreen(
    viewModel: HomeViewModel,
    profileId: String?,
    onNavigateToSurah: () -> Unit,
    onNavigateToIsland: (String) -> Unit,
    onNavigateToCards: () -> Unit,
    onTimeLimitReached: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = remember(context) {
        var currentContext = context
        while (currentContext is ContextWrapper && currentContext !is LifecycleOwner) {
            currentContext = currentContext.baseContext
        }
        currentContext as? LifecycleOwner
    }
    val speech = remember { TextToSpeech(context) {} }
    DisposableEffect(speech) { onDispose { speech.shutdown() } }
    DisposableEffect(lifecycleOwner, viewModel) {
        if (lifecycleOwner == null) return@DisposableEffect onDispose { viewModel.setHomeVisible(false) }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.setHomeVisible(true)
                Lifecycle.Event.ON_PAUSE -> viewModel.setHomeVisible(false)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) viewModel.setHomeVisible(true)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.setHomeVisible(false)
        }
    }
    val expanded = (context as? Activity)?.let {
        calculateWindowSizeClass(it).widthSizeClass == WindowWidthSizeClass.Expanded
    } ?: (LocalConfiguration.current.screenWidthDp >= 840)
    LaunchedEffect(profileId) { viewModel.loadProfile(profileId) }
    // Latch so returning from the rest screen (where the parent may have granted
    // extra time) does not immediately bounce the child straight back to it.
    var restScreenShown by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(state.isTimeLimitReached) {
        if (state.isTimeLimitReached) {
            if (!restScreenShown) {
                restScreenShown = true
                onTimeLimitReached()
            }
        } else {
            restScreenShown = false
        }
    }

    val speakIsland: (String, Boolean) -> Unit = { label, locked ->
        if (state.stage == "3-4") {
            speech.speak(label, TextToSpeech.QUEUE_FLUSH, null, "island_label")
            if (locked) {
                speech.speak(
                    context.getString(R.string.home_lock_explanation),
                    TextToSpeech.QUEUE_ADD,
                    null,
                    "island_lock_explanation",
                )
            }
        }
    }

    val content: @Composable (Modifier) -> Unit = { modifier ->
        HomeContent(
            state = state,
            modifier = modifier,
            onIslandSelected = { route, locked, label ->
                speakIsland(label, locked)
                if (!locked) onNavigateToIsland(route)
            },
            onNavigateToSurah = onNavigateToSurah,
            onNavigateToCards = onNavigateToCards,
        )
    }

    if (expanded) {
        Box(modifier = Modifier.fillMaxSize().background(BustanSky), contentAlignment = Alignment.TopCenter) {
            Box(modifier = Modifier.width(520.dp).fillMaxSize()) {
                content(Modifier.fillMaxSize())
            }
        }
    } else {
        content(Modifier.fillMaxSize())
    }
}

/**
 * Stateless map body, so the debug catalog can render it with sample data.
 * `onIslandSelected` receives the route, whether the island is locked and the
 * localised label (used for the spoken hint on the 3-4 stage).
 */
@Composable
fun HomeContent(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onIslandSelected: (route: String, locked: Boolean, label: String) -> Unit = { _, _, _ -> },
    onNavigateToSurah: () -> Unit = {},
    onNavigateToCards: () -> Unit = {},
) {
    val stageIndex = when (state.stage) {
        "3-4" -> 0
        "7-10" -> 2
        else -> 1
    }
    val islands = listOf(
        AdventureIsland(
            R.string.home_island_letters, BustanSaffronFill, BustanSaffronDeep,
            130.dp, 96.dp, 150.dp, 22.dp, 380.dp, "hijaiyah_lesson", IslandArt.Letters,
        ),
        AdventureIsland(
            R.string.home_island_surah, BustanMintArt, BustanMintArtEdge,
            140.dp, 104.dp, 160.dp, 210.dp, 270.dp, "quran_library", IslandArt.Book, isNext = true,
        ),
        AdventureIsland(
            R.string.home_island_dua, BustanCoralSoft, BustanCoralSoftEdge,
            124.dp, 92.dp, 150.dp, 30.dp, 150.dp, "doa_harian", IslandArt.Dua, minimumStage = 1,
        ),
        AdventureIsland(
            R.string.home_island_stories, BustanLilacArt, BustanLilacArtEdge,
            120.dp, 88.dp, 150.dp, 220.dp, 40.dp, "game_list", IslandArt.Locked, minimumStage = 1,
        ),
    ).map { it.copy(locked = stageIndex < it.minimumStage) }

    Column(modifier.background(BustanSky)) {
        HomeHeader(state = state)
        DailyGoalCard(state = state)
        AdventureMap(
            islands = islands,
            modifier = Modifier.weight(1f),
            onIslandClick = { island, label ->
                onIslandSelected(island.route, island.locked, label)
            },
        )
        // The bottom bar is provided once by the app shell's Scaffold; drawing it
        // here as well produced two stacked bars on device.
    }
}

/** `padding: 48px 20px 12px`, avatar disc, greeting and the star pill. */
@Composable
private fun HomeHeader(state: HomeUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BustanButter)
                    .border(3.dp, BustanWhite, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AnimalAvatarImage(avatar = AnimalAvatar.Camel, size = 34.dp)
            }
            Column {
                Text(
                    text = stringResource(R.string.home_greeting_salam),
                    style = BustanTextStyles.caption,
                    color = BustanSkyDeep,
                )
                Text(
                    text = state.nickname.ifBlank { stringResource(R.string.home_friend) },
                    style = BustanTextStyles.titleMedium,
                    color = BustanInk,
                )
            }
        }
        Row(
            modifier = Modifier
                .clip(BustanShapePill)
                .background(BustanWhite)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = BustanIcons.Star,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = state.stars.toString(),
                style = BustanTextStyles.buttonSmall,
                color = BustanInk,
            )
        }
    }
}

/** White goal card: teal tile, title, 10 dp pill bar, caption. */
@Composable
private fun DailyGoalCard(state: HomeUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp)
            .clip(BustanShapeField)
            .background(BustanWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BustanIconTile(
            icon = BustanIcons.QuranLines,
            contentDescription = null,
            size = 52.dp,
            shape = BustanShapeField,
            iconSize = 28.dp,
            containerColor = BustanTeal,
            contentColor = BustanWhite,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.home_goal_title),
                style = BustanTextStyles.labelMediumStrong,
                color = BustanInk,
            )
            BustanProgressBar(
                progress = state.secondsToday.toFloat() / (state.dailyGoalMinutes * 60).coerceAtLeast(1),
                height = 10.dp,
            )
            Text(
                text = stringResource(
                    R.string.home_goal_progress,
                    // The daily goal is a target, not the hard limit (the rest
                    // screen uses a longer allowance), so cap the label instead
                    // of printing nonsense like "30 of 15 minutes".
                    (state.secondsToday / 60).coerceAtMost(state.dailyGoalMinutes),
                    state.dailyGoalMinutes,
                ),
                style = BustanTextStyles.captionSmall,
                color = BustanMuted,
            )
        }
    }
}
/** Dashed trail plus four islands at their design coordinates. */
@Composable
private fun AdventureMap(
    islands: List<AdventureIsland>,
    onIslandClick: (AdventureIsland, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        // Fit the 390×560 design board into whatever height the shell leaves for
        // the map, so the lowest island is never clipped under the bottom bar.
        val scale = minOf(maxWidth / MapViewportWidth, maxHeight / MapViewportHeight)
        Box(
            modifier = Modifier
                .width(MapViewportWidth)
                .height(MapViewportHeight)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val trail = Path().apply {
                    moveTo(95f, 470f)
                    cubicTo(160f, 430f, 250f, 450f, 290f, 380f)
                    // `S 200 280, 120 250`: the second handle mirrors the first.
                    cubicTo(330f, 310f, 200f, 280f, 120f, 250f)
                    // `S 160 120, 280 100`
                    cubicTo(40f, 220f, 160f, 120f, 280f, 100f)
                }
                drawPath(
                    path = trail,
                    color = BustanWhite,
                    style = Stroke(
                        width = 6.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 14.dp.toPx())),
                    ),
                )
            }
            islands.forEach { island ->
                val label = stringResource(island.titleRes)
                IslandButton(
                    island = island,
                    label = label,
                    onClick = { onIslandClick(island, label) },
                )
            }
        }
    }
}

@Composable
private fun IslandButton(island: AdventureIsland, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .offset(x = island.x, y = island.y)
            .width(island.columnWidth)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box {
            IslandTile(
                fillColor = island.fill,
                edgeColor = island.edge,
                width = island.width,
                height = island.height,
                locked = island.locked,
            ) {
                when (island.art) {
                    IslandArt.Letters -> Text(
                        // The design's decorative hijaiyah sample, not Qur'an text.
                        text = "ا ب ت",
                        style = BustanTextStyles.quranIsland,
                        color = BustanTeal,
                    )
                    IslandArt.Book -> Icon(
                        imageVector = BustanIcons.Book,
                        contentDescription = null,
                        tint = BustanTeal,
                        modifier = Modifier.size(width = 56.dp, height = 44.dp),
                    )
                    IslandArt.Dua -> Icon(
                        imageVector = BustanIcons.Mic,
                        contentDescription = null,
                        tint = BustanWhite,
                        modifier = Modifier.size(44.dp),
                    )
                    IslandArt.Locked -> Icon(
                        imageVector = BustanIcons.Lock,
                        contentDescription = stringResource(R.string.home_locked),
                        tint = BustanWhite,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
            if (island.isNext && !island.locked) {
                IslandNextBadge(
                    text = stringResource(R.string.home_next_badge),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = (-10).dp),
                )
            }
        }
        IslandLabel(text = label)
    }
}



