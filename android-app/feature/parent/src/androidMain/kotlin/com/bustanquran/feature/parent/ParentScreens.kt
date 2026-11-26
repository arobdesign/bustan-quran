package com.bustanquran.feature.parent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.components.QpkPrimaryButton
import com.bustanquran.core.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentDashboardScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToWeeklyReport: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {},
    onNavigateToRecordings: () -> Unit = {},
    onAddChild: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    Column(Modifier.fillMaxSize().background(QpkParentBg)) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = QpkInk) }
            Text("Area Orang Tua", style = QpkTypography.headlineSmall, fontWeight = FontWeight.Bold, color = QpkInk)
        }
        Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Ringkasan Anak", style = QpkTypography.titleMedium, fontWeight = FontWeight.Bold, color = QpkInk)
                    Spacer(Modifier.height(4.dp))
                    Text("Semua data tersimpan lokal (berfungsi offline).", style = QpkTypography.bodySmall, color = Color.Gray)
                }
            }
            QpkPrimaryButton(text = "Tambah Anak", onClick = onAddChild)
            QpkPrimaryButton(text = "Laporan Mingguan", onClick = onNavigateToWeeklyReport)
            QpkPrimaryButton(text = "Pengaturan & Privasi", onClick = onNavigateToSettings)
            OutlinedButton(onClick = onNavigateToDownloads, Modifier.fillMaxWidth()) { Text("Manajer Unduhan Audio") }
            OutlinedButton(onClick = onNavigateToRecordings, Modifier.fillMaxWidth()) { Text("Rekaman Suara Anak") }
        }
    }
}

@Composable
fun WeeklyReportScreen(
    dailyMinutes: List<Int> = listOf(12, 18, 10, 22, 15, 25, 14),
    onBackClicked: () -> Unit = {}
) {
    val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val maxVal = (dailyMinutes.maxOrNull() ?: 1).coerceAtLeast(1)

    Column(Modifier.fillMaxSize().background(QpkParentBg).padding(16.dp)) {
        Row(Modifier.fillMaxWidth().statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = QpkInk) }
            Text("Laporan Mingguan", style = QpkTypography.headlineSmall, fontWeight = FontWeight.Bold, color = QpkInk)
        }
        Spacer(Modifier.height(16.dp))

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("Menit Belajar per Hari", style = QpkTypography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Canvas(Modifier.fillMaxWidth().height(160.dp)) {
                    val barW = size.width / (dailyMinutes.size * 2f)
                    dailyMinutes.forEachIndexed { idx, mins ->
                        val ratio = mins.toFloat() / maxVal
                        val barH = size.height * ratio
                        val x = idx * (size.width / dailyMinutes.size) + barW / 2f
                        drawRoundRect(
                            color = QpkTeal,
                            topLeft = Offset(x, size.height - barH),
                            size = Size(barW, barH),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    days.forEach { Text(it, style = QpkTypography.labelSmall, color = Color.Gray) }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = QpkMint.copy(0.3f))) {
            Column(Modifier.padding(16.dp)) {
                Text("💡 Tips Orang Tua", style = QpkTypography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Putar murottal 10 menit sebelum tidur membantu hafalan jangka panjang.", style = QpkTypography.bodyMedium)
            }
        }
    }
}

@Composable
fun ChildRecordingsScreen(onBackClicked: () -> Unit = {}) {
    Column(Modifier.fillMaxSize().background(QpkParentBg).padding(16.dp)) {
        Row(Modifier.fillMaxWidth().statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = QpkInk) }
            Text("Rekaman Suara Anak", style = QpkTypography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Rekaman disimpan lokal (AAC 32 kbps) dan otomatis dihapus setelah 90 hari kecuali ditandai Simpan.",
                style = QpkTypography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

/** Languages that actually ship translated resources (`res/values-*`). */
private data class LanguageOption(val code: String, val label: String)

private val SettingsLanguageOptions = listOf(
    LanguageOption("en", "English"),
    LanguageOption("ar", "العربية"),
    LanguageOption("fr", "Français"),
    LanguageOption("de", "Deutsch"),
)

@Composable
fun SettingsScreen(
    onBackClicked: () -> Unit = {},
    onDataDeleted: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(QpkParentBg).padding(16.dp)) {
        Row(Modifier.fillMaxWidth().statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = QpkInk) }
            Text("Pengaturan", style = QpkTypography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Text("Bahasa Aplikasi", style = QpkTypography.titleSmall, fontWeight = FontWeight.Bold, color = QpkInk)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SettingsLanguageOptions.forEach { option ->
                val selected = state.appLang == option.code
                Button(
                    onClick = { viewModel.setLanguage(option.code) },
                    enabled = !state.busy,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) QpkTeal else QpkParentBg,
                        contentColor = if (selected) Color.White else QpkInk
                    )
                ) {
                    Text(option.label, style = QpkTypography.labelMedium)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Unduh lewat Wi-Fi saja", Modifier.weight(1f), style = QpkTypography.bodyLarge)
            Switch(
                checked = state.wifiOnly,
                onCheckedChange = viewModel::setWifiOnly,
                enabled = !state.busy
            )
        }
        Spacer(Modifier.height(24.dp))
        QpkPrimaryButton(
            text = "Ekspor Data (JSON)",
            onClick = {
                viewModel.exportJson { json ->
                    if (json == null) {
                        Toast.makeText(context, "Ekspor gagal. Coba lagi.", Toast.LENGTH_SHORT).show()
                    } else {
                        shareJson(context, json)
                    }
                }
            },
            enabled = !state.busy
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { showDeleteConfirm = true },
            enabled = !state.busy,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = QpkCoral)
        ) {
            Text("Hapus Semua Data")
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Semua Data?") },
            text = { Text("Semua profil, progres hafalan, dan rekaman lokal maupun server akan dihapus permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteAllData {
                            Toast.makeText(context, "Semua data sudah dihapus.", Toast.LENGTH_SHORT).show()
                            onDataDeleted()
                        }
                    }
                ) {
                    Text("Ya, Hapus", color = QpkCoral)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
            }
        )
    }
}

/**
 * "Ekspor Data (JSON)" hands the document to any app the parent already trusts
 * (Drive, email, notes) instead of writing into a folder they cannot find.
 */
private fun shareJson(context: Context, json: String) {
    val send = Intent(Intent.ACTION_SEND)
        .setType("application/json")
        .putExtra(Intent.EXTRA_SUBJECT, "Bustan Qur'an — data ekspor")
        .putExtra(Intent.EXTRA_TEXT, json)
    val chooser = Intent.createChooser(send, "Ekspor Data (JSON)")
    if (context !is Activity) chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(chooser) }
        .onFailure { Toast.makeText(context, "Tidak ada aplikasi untuk membagikan data.", Toast.LENGTH_SHORT).show() }
}

@Composable
fun EmptyProgressScreen() {
    Box(Modifier.fillMaxSize().background(QpkParentBg).padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌱", style = QpkTypography.displayMedium)
            Spacer(Modifier.height(12.dp))
            Text("Belum Ada Progres", style = QpkTypography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Mulai sesi pertama anak untuk melihat perkembangan di sini.", textAlign = TextAlign.Center, color = Color.Gray)
        }
    }
}

/**
 * PAR-04 rest screen. [onParentUnlock] must lead to the parent gate; the +10
 * minutes are only granted once that gate passes, so the parameter is required
 * to keep the button from silently doing nothing.
 */
@Composable
fun IstirahatScreen(onParentUnlock: () -> Unit) {
    Box(Modifier.fillMaxSize().background(QpkMint).padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⏰", style = QpkTypography.displayLarge)
            Spacer(Modifier.height(16.dp))
            Text("Waktunya Istirahat", style = QpkTypography.headlineLarge, fontWeight = FontWeight.Bold, color = QpkInk)
            Spacer(Modifier.height(8.dp))
            Text("Batas waktu belajar harian telah tercapai.", textAlign = TextAlign.Center, color = QpkInk)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onParentUnlock) { Text("Tambah +10 Menit (Orang Tua)") }
        }
    }
}

@Composable
fun DownloadManagerScreen(
    freeStorageMb: Long = 1024L,
    packages: List<JuzAudioPackage> = listOf(
        JuzAudioPackage(30, "mishary_child", 42, "abc123", isDownloaded = true),
        JuzAudioPackage(29, "mishary_child", 48, "def456", isDownloaded = false)
    ),
    onTogglePackage: (JuzAudioPackage) -> Unit = {}
) {
    Column(Modifier.fillMaxSize().background(QpkParentBg).padding(16.dp)) {
        Text("Manajer Unduhan Audio", style = QpkTypography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (StorageHealthChecker.isLowStorage(freeStorageMb)) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = QpkCoral.copy(0.15f))) {
                Text("⚠️ Ruang penyimpanan tersisa $freeStorageMb MB (< 300 MB).", Modifier.padding(12.dp), color = QpkCoral)
            }
            Spacer(Modifier.height(8.dp))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(packages) { pkg ->
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Juz ${pkg.juzNumber}", style = QpkTypography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${pkg.reciterId} · ${pkg.sizeMb} MB", style = QpkTypography.bodySmall, color = Color.Gray)
                        }
                        Button(
                            onClick = { onTogglePackage(pkg) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (pkg.isDownloaded) QpkCoral else QpkTeal)
                        ) {
                            Text(if (pkg.isDownloaded) "Hapus" else "Unduh")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineStateScreen(onRetry: () -> Unit = {}) {
    Box(Modifier.fillMaxSize().background(QpkSand).padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📡", style = QpkTypography.displayMedium)
            Spacer(Modifier.height(12.dp))
            Text("Sedang Offline", style = QpkTypography.headlineSmall, fontWeight = FontWeight.Bold, color = QpkInk)
            Spacer(Modifier.height(8.dp))
            Text("Jangan khawatir, semua surat dan game yang sudah diunduh tetap bisa dimainkan!",
                textAlign = TextAlign.Center, color = Color.Gray)
            Spacer(Modifier.height(20.dp))
            QpkPrimaryButton(text = "Coba Lagi", onClick = onRetry)
        }
    }
}

@Composable
fun AudioMissingSheet() {
    OfflineStateScreen()
}
