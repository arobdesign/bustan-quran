package com.bustanquran.core.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanTextStyles

/**
 * Debug-only review screen for P-18 STEP 1.
 *
 * It renders every design-system component on one scrollable page so the look
 * can be compared against the rendered PNGs in `design/png` without navigating
 * the app. It is launched only from the debug build (see
 * `DesignCatalogActivity`), so it never reaches a release build and its labels
 * are not user-facing copy.
 */
@Composable
fun DesignCatalogScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanSand),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { CatalogHeaderSection() }
        item { CatalogColorsSection() }
        item { CatalogTypographySection() }
        item { CatalogButtonSection() }
        item { CatalogChipSection() }
        item { CatalogControlSection() }
        item { CatalogRowSection() }
        item { CatalogProgressSection() }
        item { CatalogStarSection() }
        item { CatalogArtSection() }
        item { CatalogCardSection() }
        item { CatalogNavigationSection() }
    }
}

/** Section wrapper: eyebrow label, then the component(s). */
@Composable
internal fun CatalogSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = title, style = BustanTextStyles.eyebrow, color = BustanMuted)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}

/** A colour swatch with its token name and hex value, for eyeballing. */
@Composable
internal fun Swatch(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(28.dp)
                .background(color, com.bustanquran.core.ui.theme.BustanShapeTile)
                .border(
                    width = 1.dp,
                    color = com.bustanquran.core.ui.theme.BustanBorder,
                    shape = com.bustanquran.core.ui.theme.BustanShapeTile,
                ),
        )
        Text(text = name, style = BustanTextStyles.labelMedium)
    }
}
