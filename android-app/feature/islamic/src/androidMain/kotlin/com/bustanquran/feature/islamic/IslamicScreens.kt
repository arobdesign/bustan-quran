package com.bustanquran.feature.islamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.QpkSand
import com.bustanquran.core.ui.theme.QpkTypography

@Composable
fun DoaHarianScreen() {
    IslamicPlaceholder("Daily Du'a (Doa Harian)")
}

@Composable
fun KisahNabiScreen() {
    IslamicPlaceholder("Stories of the Prophets (Kisah Nabi - v1.1)")
}

@Composable
fun ModeGuruScreen() {
    IslamicPlaceholder("Basic Teacher Mode (v2)")
}

@Composable
fun AsmaulHusnaScreen() {
    IslamicPlaceholder("Asmaul Husna 99 Names (v2)")
}

@Composable
fun WudhuGuideScreen() {
    IslamicPlaceholder("Wudhu Guide (v2)")
}

@Composable
fun SalatGuideScreen() {
    IslamicPlaceholder("Salat Guide (v2)")
}

@Composable
fun RukunIslamScreen() {
    IslamicPlaceholder("Rukun Islam & Iman (v2)")
}

@Composable
fun RamadanModeScreen() {
    IslamicPlaceholder("Ramadan Mode (v2)")
}

@Composable
private fun IslamicPlaceholder(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(QpkSand).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = QpkTypography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Islamic Content Module", style = QpkTypography.bodyMedium)
        }
    }
}
