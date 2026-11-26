package com.bustanquran.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.R
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

enum class BottomNavDestination(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
) {
    MAP("home", R.string.nav_map, BustanIcons.Map),
    QURAN("quran_library", R.string.nav_quran, BustanIcons.Quran),
    MY_CARDS("koleksi_kartu", R.string.nav_cards, BustanIcons.Cards),
}

private val BottomNavShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

/**
 * Bottom navigation from the design: a white bar with 24 dp top corners,
 * `padding: 10px 12px 22px`, three items of at least 64×48 dp, a 26 dp icon
 * and a 12/800 label. Selected item is teal, the rest `#5B6661`.
 *
 * Material's `NavigationBar` is not used because its height, indicator pill and
 * type scale do not match the design.
 */
@Composable
fun BottomNav(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(BottomNavShape)
            .background(BustanWhite)
            .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 22.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomNavDestination.entries.forEach { destination ->
            val isSelected = currentRoute == destination.route
            val contentColor = if (isSelected) BustanTeal else BustanMuted
            Column(
                modifier = Modifier
                    .defaultMinSize(minWidth = 64.dp, minHeight = 48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onNavigateToRoute(destination.route) },
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(destination.title),
                    style = BustanTextStyles.captionSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor,
                )
            }
        }
    }
}
