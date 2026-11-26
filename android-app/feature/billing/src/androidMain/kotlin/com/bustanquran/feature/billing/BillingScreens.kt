package com.bustanquran.feature.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.components.QpkPrimaryButton
import com.bustanquran.core.ui.theme.*

data class PlanDisplay(val product: BillingProduct, val title: String, val subtitle: String, val priceLabel: String)

@Composable
fun PaywallScreen(
    plans: List<PlanDisplay> = listOf(
        PlanDisplay(BillingProduct.JUZ_AMMA_LIFETIME, "Juz 'Amma Selamanya", "3 profil anak · Semua surat Juz 30 & game", "Sekali bayar"),
        PlanDisplay(BillingProduct.QURAN_30_LIFETIME, "Al-Qur'an 30 Juz", "6 profil anak · 30 Juz lengkap", "Sekali bayar"),
        PlanDisplay(BillingProduct.FAMILY_MONTHLY, "Keluarga Bulanan", "6 profil · Semua fitur & sync cloud", "Bulanan"),
        PlanDisplay(BillingProduct.FAMILY_YEARLY, "Keluarga Tahunan", "Hemat 40% · 6 profil anak", "Tahunan")
    ),
    onLaunchBilling: (BillingProduct) -> Unit = {},
    onPurchaseSuccess: () -> Unit = {},
    onRestoreClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    Column(Modifier.fillMaxSize().background(QpkParentBg).padding(16.dp)) {
        Row(Modifier.fillMaxWidth().statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = QpkInk) }
            Text("Pilih Paket Keluarga", style = QpkTypography.headlineSmall, fontWeight = FontWeight.Bold, color = QpkInk)
        }
        Spacer(Modifier.height(12.dp))
        plans.forEach { plan ->
            Card(
                Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(plan.title, style = QpkTypography.titleMedium, fontWeight = FontWeight.Bold, color = QpkInk)
                        Text(plan.subtitle, style = QpkTypography.bodySmall, color = Color.Gray)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onLaunchBilling(plan.product); onPurchaseSuccess() },
                        colors = ButtonDefaults.buttonColors(containerColor = QpkTeal)
                    ) { Text(plan.priceLabel) }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onRestoreClicked, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Pulihkan Pembelian Sebelumnya")
        }
    }
}

@Composable
fun PurchaseSuccessScreen(onContinue: () -> Unit = {}) {
    Box(Modifier.fillMaxSize().background(QpkParentBg).padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎉", style = QpkTypography.displayLarge)
            Spacer(Modifier.height(16.dp))
            Text("Alhamdulillah, Pembelian Berhasil!", style = QpkTypography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            QpkPrimaryButton(text = "Mulai Belajar", onClick = onContinue)
        }
    }
}

@Composable
fun RestorePurchaseScreen(onRestored: () -> Unit = {}) {
    Box(Modifier.fillMaxSize().background(QpkParentBg).padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Pulihkan Pembelian", style = QpkTypography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Memeriksa riwayat pembelian Google Play Anda...", textAlign = TextAlign.Center, color = Color.Gray)
            Spacer(Modifier.height(24.dp))
            QpkPrimaryButton(text = "Periksa Sekarang", onClick = onRestored)
        }
    }
}
