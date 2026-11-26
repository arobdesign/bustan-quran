package com.bustanquran.feature.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.BustanSegmentedControl
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanSegmentedTrack
import com.bustanquran.core.ui.theme.BustanShapeCardSmall
import com.bustanquran.core.ui.theme.BustanShapeSmall
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Artboard 19 · `GameList.dc.html` — three age tabs over a two-column grid of
 * numbered game cards. Every number, name and goal is the design's copy.
 */
@Composable
fun GameListScreen(
    onGameSelected: (String) -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    var stageIndex by remember { mutableStateOf(0) }
    val stage = GameStages[stageIndex]
    val stageLabels = GameStages.map { stringResource(it.labelRes) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanSand)
            .padding(start = 18.dp, end = 18.dp, top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BackButton(onClick = onBackClicked, shadowColor = BustanBorderSoft)
            Text(
                text = stringResource(R.string.game_title),
                style = BustanTextStyles.titleLarge,
                color = BustanInk,
            )
        }

        BustanSegmentedControl(
            options = GameStages,
            selected = stage,
            onSelect = { selected -> stageIndex = GameStages.indexOf(selected) },
            label = { stageLabels[GameStages.indexOf(it)] },
            trackColor = BustanSegmentedTrack,
            selectedContainerColor = BustanTeal,
            selectedContentColor = BustanWhite,
            contentColor = BustanInk,
        )

        Text(
            text = stringResource(stage.taglineRes),
            style = BustanTextStyles.caption,
            color = BustanMuted,
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(stage.games, key = { it.number }) { game ->
                GameCard(
                    game = game,
                    tint = stage.tint,
                    onClick = { onGameSelected(game.route) },
                )
            }
        }
    }
}
@Composable
private fun GameCard(
    game: GameEntry,
    tint: Color,
    onClick: () -> Unit,
    locked: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (locked) Modifier.alpha(0.6f) else Modifier)
            .bustanShadow(color = BustanBorderSoft, shape = BustanShapeCardSmall)
            .clip(BustanShapeCardSmall)
            .background(BustanWhite)
            .clickable(enabled = !locked, onClick = onClick)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(BustanShapeSmall)
                .background(if (locked) BustanSegmentedTrack else tint),
            contentAlignment = Alignment.Center,
        ) {
            if (locked) {
                Icon(
                    imageVector = BustanIcons.Lock,
                    contentDescription = stringResource(R.string.game_locked),
                    tint = BustanMuted,
                    modifier = Modifier.size(22.dp),
                )
            } else {
                Text(
                    text = game.number.toString(),
                    style = BustanTextStyles.titleMedium,
                    color = BustanInk,
                )
            }
        }
        Text(
            text = stringResource(game.nameRes),
            style = BustanTextStyles.labelMediumStrong,
            color = BustanInk,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(game.goalRes),
            style = BustanTextStyles.micro,
            color = BustanMuted,
        )
    }
}

/** Flat route list, in design order, for call sites that do not group by age. */
val ALL_GAMES: List<GameEntry> = GameStages.flatMap { it.games }

