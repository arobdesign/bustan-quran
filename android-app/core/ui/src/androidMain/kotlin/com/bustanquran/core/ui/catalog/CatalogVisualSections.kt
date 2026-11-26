package com.bustanquran.core.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.components.AnimalAvatar
import com.bustanquran.core.ui.components.AnimalAvatarImage
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BottomNav
import com.bustanquran.core.ui.components.BustanAppLogo
import com.bustanquran.core.ui.components.BustanCard
import com.bustanquran.core.ui.components.BustanCardBadge
import com.bustanquran.core.ui.components.HudHudMascot
import com.bustanquran.core.ui.components.IslandLabel
import com.bustanquran.core.ui.components.IslandNextBadge
import com.bustanquran.core.ui.components.IslandTile
import com.bustanquran.core.ui.theme.BustanAmberText
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoralSoft
import com.bustanquran.core.ui.theme.BustanCoralSoftEdge
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanLilacArt
import com.bustanquran.core.ui.theme.BustanLilacArtEdge
import com.bustanquran.core.ui.theme.BustanMintArt
import com.bustanquran.core.ui.theme.BustanMintArtEdge
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanShapeCardSmall
import com.bustanquran.core.ui.theme.BustanShapeProfile
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles

@Composable
internal fun CatalogArtSection() {
    CatalogSection(title = "ILLUSTRATION · vectors") {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BustanAppLogo(size = 52.dp)
            HudHudMascot(size = 52.dp)
            AnimalAvatarImage(avatar = AnimalAvatar.Camel, size = 44.dp)
            AnimalAvatarImage(avatar = AnimalAvatar.Bear, size = 44.dp)
            AnimalAvatarImage(avatar = AnimalAvatar.Bird, size = 44.dp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IslandTile(
                fillColor = BustanMintArt,
                edgeColor = BustanMintArtEdge,
                width = 130.dp,
                height = 96.dp,
            ) {
                IslandNextBadge(
                    text = "Next",
                    modifier = Modifier.padding(top = 6.dp, start = 76.dp),
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Text(text = "ا ب ت", style = BustanTextStyles.quranIsland, color = BustanTeal)
        }
        IslandLabel(text = "Surah Island")
        IslandTile(
            fillColor = BustanLilacArt,
            edgeColor = BustanLilacArtEdge,
            width = 120.dp,
            height = 88.dp,
            locked = true,
        ) {}
        IslandTile(
            fillColor = BustanCoralSoft,
            edgeColor = BustanCoralSoftEdge,
            width = 124.dp,
            height = 92.dp,
        ) {}
    }
}

@Composable
internal fun CatalogCardSection() {
    CatalogSection(title = "CARDS · flat shadow + profile") {
        BustanCard(
            modifier = Modifier.fillMaxWidth(),
            shape = BustanShapeCardSmall,
            shadowColor = BustanBorderSoft,
            contentPadding = PaddingValues(10.dp),
        ) {
            Text(
                text = "Letter Balloons",
                style = BustanTextStyles.labelMediumStrong,
                color = BustanInk,
            )
            Text(
                text = "Letter sounds",
                style = BustanTextStyles.captionSmall,
                color = BustanMuted,
            )
        }
        BustanCard(
            modifier = Modifier.fillMaxWidth(),
            shape = BustanShapeProfile,
            shadowColor = BustanBorderSoft,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimalAvatarImage(avatar = AnimalAvatar.Camel, size = 56.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Maryam", style = BustanTextStyles.titleSmall, color = BustanInk)
                Spacer(modifier = Modifier.height(8.dp))
                BustanCardBadge(
                    text = "Little Stars · 4",
                    containerColor = BustanButter,
                    contentColor = BustanAmberText,
                )
            }
        }
    }
}

@Composable
internal fun CatalogNavigationSection() {
    var route by remember { mutableStateOf("home") }
    CatalogSection(title = "NAVIGATION · 24 dp top bar + back tile") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BackButton(onClick = {})
            BackButton(onClick = {}, shadowColor = BustanBorderSoft)
        }
        BottomNav(currentRoute = route, onNavigateToRoute = { route = it })
        Text(
            text = "selected route: $route",
            style = BustanTextStyles.captionSmall,
            color = BustanMuted,
        )
    }
}
