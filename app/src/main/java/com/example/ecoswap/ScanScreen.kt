package com.example.ecoswap

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecoswap.data.FirebaseRepository

// ─────────────────────────────────────────────────────────────────────────────
//  SCAN SCREEN
//  NOTE FOR AI DEVELOPER:
//  - Ganti `CameraPreviewPlaceholder` dengan integrasi CameraX sesungguhnya.
//  - Setelah YOLO inference, panggil `FirebaseRepository().saveScanResult(...)`
//  - Lihat README.md section "AI Integration Bridge" untuk detail lengkap.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ScanScreen(navController: NavController) {
    var scanState by remember { mutableStateOf<ScanState>(ScanState.Idle) }
    var flashOn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── CAMERA PREVIEW AREA ──────────────────────────────────────────────
        // TODO (AI Developer): Ganti Box ini dengan AndroidView + CameraX Preview
        // Contoh:
        //   val previewView = remember { PreviewView(context) }
        //   AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        //   LaunchedEffect(Unit) { startCamera(context, previewView, lifecycleOwner) }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder gelap saat kamera belum terhubung
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📷", fontSize = 64.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Arahkan kamera ke sampah",
                    color = Color.White.copy(0.6f),
                    fontSize = 14.sp
                )
            }
        }

        // ── SCAN OVERLAY ─────────────────────────────────────────────────────
        when (scanState) {
            ScanState.Idle -> IdleOverlay(
                flashOn = flashOn,
                onFlashToggle = { flashOn = !flashOn },
                onClose = { navController.popBackStack() },
                onScanDemo = { scanState = ScanState.Scanning }
            )
            ScanState.Scanning -> ScanningOverlay()
            is ScanState.Result -> ResultOverlay(
                result = scanState as ScanState.Result,
                onAddToCart = {
                    // TODO: integrasikan dengan FirebaseRepository().saveScanResult(...)
                    scanState = ScanState.Idle
                },
                onRetry = { scanState = ScanState.Idle }
            )
            ScanState.Unrecognized -> UnrecognizedOverlay(onRetry = { scanState = ScanState.Idle })
            ScanState.LowConfidence -> LowConfidenceOverlay(onRetry = { scanState = ScanState.Idle })
        }

        // ── DEMO: simulasi hasil scan (untuk testing tanpa AI) ───────────────
        // Hapus block ini setelah AI developer mengintegrasikan YOLO
        if (scanState == ScanState.Scanning) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                scanState = ScanState.Result(
                    wasteLabel = "Botol Kaca",
                    wasteCategory = "Non-Organik Kaca",
                    confidence = 0.87f,
                    pointPerKg = 1500
                )
            }
        }
    }
}

// ─── STATE ───────────────────────────────────────────────────────────────────

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    object Unrecognized : ScanState()
    object LowConfidence : ScanState()
    data class Result(
        val wasteLabel: String,
        val wasteCategory: String,
        val confidence: Float,
        val pointPerKg: Int
    ) : ScanState()
}

// ─── OVERLAYS ────────────────────────────────────────────────────────────────

@Composable
private fun IdleOverlay(
    flashOn: Boolean,
    onFlashToggle: () -> Unit,
    onClose: () -> Unit,
    onScanDemo: () -> Unit
) {
    val green = Color(0xFF1FAA59)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
            }
            Text("Scan Sampah", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = onFlashToggle) {
                Icon(
                    if (flashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (flashOn) Color.Yellow else Color.White
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Scan frame
        ScanFrame()

        Spacer(Modifier.height(20.dp))

        Text(
            "Arahkan ke sampah dan tekan tombol scan",
            color = Color.White.copy(0.8f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        // Capture button
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onScanDemo) {
                    Text("♻️", fontSize = 28.sp)
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ScanFrame() {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "scanLine"
    )

    Box(
        modifier = Modifier
            .size(260.dp)
    ) {
        // Corner brackets
        val cornerColor = Color(0xFF1FAA59)
        val cornerSize = 40.dp
        val strokeWidth = 4.dp

        // Top-left
        Box(
            Modifier.align(Alignment.TopStart)
                .size(cornerSize)
                .border(width = strokeWidth, color = cornerColor, shape = RoundedCornerShape(topStart = 12.dp))
        )
        // Top-right
        Box(
            Modifier.align(Alignment.TopEnd)
                .size(cornerSize)
                .border(width = strokeWidth, color = cornerColor, shape = RoundedCornerShape(topEnd = 12.dp))
        )
        // Bottom-left
        Box(
            Modifier.align(Alignment.BottomStart)
                .size(cornerSize)
                .border(width = strokeWidth, color = cornerColor, shape = RoundedCornerShape(bottomStart = 12.dp))
        )
        // Bottom-right
        Box(
            Modifier.align(Alignment.BottomEnd)
                .size(cornerSize)
                .border(width = strokeWidth, color = cornerColor, shape = RoundedCornerShape(bottomEnd = 12.dp))
        )

        // Scan line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = (260 * scanLineY).dp)
                .background(Color(0xFF1FAA59).copy(0.7f))
        )
    }
}

@Composable
private fun ScanningOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color(0xFF1FAA59))
                Spacer(Modifier.height(16.dp))
                Text("Menganalisis sampah...", fontWeight = FontWeight.Medium)
                Text("Mohon tunggu sebentar", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ResultOverlay(
    result: ScanState.Result,
    onAddToCart: () -> Unit,
    onRetry: () -> Unit
) {
    val green = Color(0xFF1FAA59)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Category chip
                Surface(
                    color = green.copy(0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        result.wasteCategory,
                        color = green,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    result.wasteLabel,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("♻️", fontSize = 16.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${result.pointPerKg} Poin / Kg",
                        color = green,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Confidence bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Akurasi:", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = result.confidence,
                        modifier = Modifier.weight(1f),
                        color = green
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${(result.confidence * 100).toInt()}%",
                        color = green,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = green)
                ) {
                    Text("Tambahkan ke Tong", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Scan Ulang")
                }
            }
        }
    }
}

@Composable
private fun UnrecognizedOverlay(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🔍", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Sampah Tidak Dikenali", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Coba foto ulang dengan pencahayaan lebih baik atau dari sudut berbeda",
                    color = Color.Gray, textAlign = TextAlign.Center, fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50)) {
                    Text("Coba Lagi")
                }
            }
        }
    }
}

@Composable
private fun LowConfidenceOverlay(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⚠️", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Foto Kurang Jelas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Kepercayaan diri AI terlalu rendah. Mohon foto ulang sampah dengan lebih jelas.",
                    color = Color.Gray, textAlign = TextAlign.Center, fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50)) {
                    Text("Foto Ulang")
                }
            }
        }
    }
}