
package com.bustanquran.feature.hafalan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.theme.*

// Hafalan 3-step (HFZ-01..03)
enum class HafalanStep { LISTEN, REPEAT, ON_MY_OWN }
enum class FadeLevel { FULL, SOME_HIDDEN, FIRST_LETTERS, BLANK }

// HafalanScreen lives in `HafalanDesign.kt` (artboard 05, P-18 STEP 2A).

// HafalanMap grid (HFZ-05) ── shared state types. The grid itself is drawn in
// `HafalanMapDesign.kt` (artboard 16, P-18), not here.
data class SurahMapItem(val number: Int, val nameEn: String, val status: HafalanStatus)
enum class HafalanStatus { HAFAL, BELAJAR, BELUM, PERLU_DIULANG }

@Composable
fun VoiceCheckScreen() {
    Box(Modifier.fillMaxSize().background(QpkSand), contentAlignment = Alignment.Center) {
        Text("Voice Check (v2 — behind feature flag)", color = Color.Gray)
    }
}
