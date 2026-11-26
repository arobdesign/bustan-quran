package com.bustanquran.feature.islamic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.*
import kotlin.math.hypot

/**
 * Pure Kotlin stroke coverage evaluator for Hijaiyah tracing (P-11).
 * Accepts when >= 70% of guide waypoints are visited within tolerance.
 */
object TracingEvaluator {
    const val ACCEPTANCE_THRESHOLD = 0.70f

    fun computeCoverage(
        guidePoints: List<Offset>,
        drawnPoints: List<Offset>,
        tolerancePx: Float = 48f
    ): Float {
        if (guidePoints.isEmpty()) return 1f
        if (drawnPoints.isEmpty()) return 0f
        val covered = guidePoints.count { g ->
            drawnPoints.any { d -> hypot((g.x - d.x).toDouble(), (g.y - d.y).toDouble()) <= tolerancePx }
        }
        return covered.toFloat() / guidePoints.size
    }

    fun isAccepted(guidePoints: List<Offset>, drawnPoints: List<Offset>, tolerancePx: Float = 48f): Boolean =
        computeCoverage(guidePoints, drawnPoints, tolerancePx) >= ACCEPTANCE_THRESHOLD
}

data class HijaiyahItem(
    val id: Int,
    val letterName: String,
    val isolated: String,
    val initial: String,
    val medial: String,
    val finalForm: String,
    val exampleWord: String
)

@Composable
fun HijaiyahTracingCard(
    item: HijaiyahItem? = null,
    onCompleted: () -> Unit = {}
) {
    val drawnPoints = remember { mutableStateListOf<Offset>() }
    var coverage by remember { mutableFloatStateOf(0f) }
    var accepted by remember { mutableStateOf(false) }

    // Guide waypoints (normalized in 300x300 box)
    val guidePoints = remember {
        listOf(
            Offset(150f, 50f), Offset(150f, 100f), Offset(150f, 150f),
            Offset(150f, 200f), Offset(150f, 250f)
        )
    }

    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (item != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FormChip("Tunggal", item.isolated)
                FormChip("Awal", item.initial)
                FormChip("Tengah", item.medial)
                FormChip("Akhir", item.finalForm)
            }
            Spacer(Modifier.height(12.dp))
        }

        Box(
            Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            Canvas(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            drawnPoints.add(change.position)
                            coverage = TracingEvaluator.computeCoverage(guidePoints, drawnPoints)
                            if (coverage >= TracingEvaluator.ACCEPTANCE_THRESHOLD && !accepted) {
                                accepted = true
                                onCompleted()
                            }
                        }
                    }
            ) {
                // Guide dashed stroke
                val guidePath = Path().apply {
                    guidePoints.forEachIndexed { idx, pt ->
                        if (idx == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                    }
                }
                drawPath(guidePath, color = Color(0xFFE0E0E0), style = Stroke(width = 24f, cap = StrokeCap.Round))

                // Start dot (green) + direction arrow
                drawCircle(color = QpkTeal, radius = 14f, center = guidePoints.first())

                // User stroke
                if (drawnPoints.size > 1) {
                    val userPath = Path().apply {
                        moveTo(drawnPoints.first().x, drawnPoints.first().y)
                        drawnPoints.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(userPath, color = QpkCoral, style = Stroke(width = 18f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = if (accepted) "Bagus sekali! (${(coverage * 100).toInt()}%)" else "Ikuti titik hijau (${(coverage * 100).toInt()}%)",
            style = QpkTypography.bodyMedium,
            color = if (accepted) QpkTeal else QpkInk,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { drawnPoints.clear(); coverage = 0f; accepted = false }) {
            Text("Ulangi")
        }
    }
}

@Composable
private fun FormChip(label: String, formText: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = QpkTypography.labelSmall, color = Color.Gray)
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(formText, style = QpkTypography.titleLarge, color = QpkInk)
        }
    }
}
