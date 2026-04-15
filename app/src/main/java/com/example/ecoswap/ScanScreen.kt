package com.example.ecoswap

import android.Manifest
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ecoswap.data.FirebaseRepository
import com.example.ecoswap.data.ScanResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(navController: NavController) {
    var scanState    by remember { mutableStateOf<ScanState>(ScanState.Idle) }
    var flashOn      by remember { mutableStateOf(false) }
    var detections   by remember { mutableStateOf<List<Detection>>(emptyList()) }

    // Holds the latest frame bitmap captured from the analyzer
    var latestBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope          = rememberCoroutineScope()
    val detector       = remember { YoloDetector(context) }
    val mainHandler    = remember { Handler(Looper.getMainLooper()) }
    var cameraRef      by remember { mutableStateOf<Camera?>(null) }

    val green = Color(0xFF1FAA59)

    LaunchedEffect(flashOn) { cameraRef?.cameraControl?.enableTorch(flashOn) }
    DisposableEffect(Unit) { onDispose { detector.close() } }

    // Called when user taps the scan button — runs detection on the last captured frame
    fun triggerScan() {
        val bmp = latestBitmap ?: return
        scanState = ScanState.Scanning
        val results = detector.detect(bmp)
        detections = results
        scanState = when {
            results.isEmpty() -> ScanState.Unrecognized
            results.first().confidence < 0.35f -> ScanState.LowConfidence
            else -> ScanState.Result(
                wasteLabel    = results.first().label,
                wasteCategory = results.first().category,
                confidence    = results.first().confidence,
                pointPerKg    = results.first().pointsPerKg
            )
        }
    }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    if (!cameraPermission.status.isGranted) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📷", fontSize = 48.sp)
                Spacer(Modifier.height(16.dp))
                Text("Izin kamera diperlukan", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { cameraPermission.launchPermissionRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = green)
                ) { Text("Izinkan Kamera") }
            }
        }
        return
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {

        // ── Camera Preview ────────────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                val previewView          = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val analyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build().also { analysis ->
                            analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                // Always keep the latest frame ready, but never run detection here
                                val bitmap = imageProxy.toBitmap()
                                mainHandler.post { latestBitmap = bitmap }
                                imageProxy.close()
                            }
                        }

                    runCatching {
                        cameraProvider.unbindAll()
                        cameraRef = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analyzer
                        )
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // ── Bounding Box Overlay (only shown after a scan produces results) ──
        Canvas(modifier = Modifier.fillMaxSize()) {
            detections.forEach { det ->
                val box    = det.boundingBox
                val left   = box.left   * size.width
                val top    = box.top    * size.height
                val right  = box.right  * size.width
                val bottom = box.bottom * size.height
                val w      = right - left
                val h      = bottom - top

                drawRect(
                    color   = Color(0xFF1FAA59),
                    topLeft = Offset(left, top),
                    size    = Size(w, h),
                    style   = Stroke(width = 3f)
                )

                val cornerLen = minOf(w, h) * 0.15f
                val c = Color(0xFF1FAA59)
                drawLine(c, Offset(left, top), Offset(left + cornerLen, top), 6f)
                drawLine(c, Offset(left, top), Offset(left, top + cornerLen), 6f)
                drawLine(c, Offset(right, top), Offset(right - cornerLen, top), 6f)
                drawLine(c, Offset(right, top), Offset(right, top + cornerLen), 6f)
                drawLine(c, Offset(left, bottom), Offset(left + cornerLen, bottom), 6f)
                drawLine(c, Offset(left, bottom), Offset(left, bottom - cornerLen), 6f)
                drawLine(c, Offset(right, bottom), Offset(right - cornerLen, bottom), 6f)
                drawLine(c, Offset(right, bottom), Offset(right, bottom - cornerLen), 6f)

                drawRect(
                    color   = Color(0xFF1FAA59),
                    topLeft = Offset(left, top - 36f),
                    size    = Size(w, 36f)
                )
            }
        }

        // ── Label text overlay ────────────────────────────────────────────────
        detections.forEach { det ->
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val pxWidth  = constraints.maxWidth.toFloat()
                val pxHeight = constraints.maxHeight.toFloat()
                val box      = det.boundingBox
                Box(
                    Modifier.offset(
                        x = (box.left * pxWidth).dp  / 3f,
                        y = ((box.top * pxHeight) - 36f).dp / 3f
                    )
                ) {
                    Text(
                        "${det.label} ${"%.0f".format(det.confidence * 100)}%",
                        color      = Color.White,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // ── Scan Overlays ─────────────────────────────────────────────────────
        when (scanState) {
            ScanState.Idle -> IdleOverlay(
                flashOn       = flashOn,
                onFlashToggle = { flashOn = !flashOn },
                onClose       = { navController.popBackStack() },
                onScanDemo    = { triggerScan() }          // ← button now triggers capture+detect
            )
            ScanState.Scanning    -> ScanningOverlay()
            is ScanState.Result   -> ResultOverlay(
                result      = scanState as ScanState.Result,
                onAddToCart = {
                    val r = scanState as ScanState.Result
                    scope.launch {
                        FirebaseRepository().saveScanResult(
                            ScanResult(
                                wasteLabel    = r.wasteLabel,
                                wasteCategory = r.wasteCategory,
                                confidence    = r.confidence,
                                status        = "recognized",
                                pointsEarned  = r.pointPerKg,
                                imageUrl      = ""
                            )
                        )
                            .onSuccess { android.util.Log.d("FIREBASE", "✅ Saved! ID: $it") }
                            .onFailure { android.util.Log.e("FIREBASE", "❌ Failed: ${it.message}") }
                    }
                    detections = emptyList()
                    scanState  = ScanState.Idle
                },
                onRetry = {
                    detections = emptyList()
                    scanState  = ScanState.Idle
                }
            )
            ScanState.Unrecognized  -> UnrecognizedOverlay(onRetry = {
                detections = emptyList(); scanState = ScanState.Idle })
            ScanState.LowConfidence -> LowConfidenceOverlay(onRetry = {
                detections = emptyList(); scanState = ScanState.Idle })
        }
    }
}

// ─── STATE ────────────────────────────────────────────────────────────────────

sealed class ScanState {
    object Idle            : ScanState()
    object Scanning        : ScanState()
    object Unrecognized    : ScanState()
    object LowConfidence   : ScanState()
    data class Result(
        val wasteLabel    : String,
        val wasteCategory : String,
        val confidence    : Float,
        val pointPerKg    : Int
    ) : ScanState()
}

// ─── OVERLAYS ─────────────────────────────────────────────────────────────────

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
        ScanFrame()
        Spacer(Modifier.height(20.dp))
        Text(
            "Arahkan ke sampah dan tekan tombol scan",
            color = Color.White.copy(0.8f), fontSize = 13.sp, textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onScanDemo) { Text("♻️", fontSize = 28.sp) }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ScanFrame() {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "scanLine"
    )
    Box(modifier = Modifier.size(260.dp)) {
        val cornerColor = Color(0xFF1FAA59)
        val cornerSize  = 40.dp
        val strokeWidth = 4.dp
        Box(Modifier.align(Alignment.TopStart).size(cornerSize)
            .border(strokeWidth, cornerColor, RoundedCornerShape(topStart = 12.dp)))
        Box(Modifier.align(Alignment.TopEnd).size(cornerSize)
            .border(strokeWidth, cornerColor, RoundedCornerShape(topEnd = 12.dp)))
        Box(Modifier.align(Alignment.BottomStart).size(cornerSize)
            .border(strokeWidth, cornerColor, RoundedCornerShape(bottomStart = 12.dp)))
        Box(Modifier.align(Alignment.BottomEnd).size(cornerSize)
            .border(strokeWidth, cornerColor, RoundedCornerShape(bottomEnd = 12.dp)))
        Box(
            modifier = Modifier
                .fillMaxWidth().height(2.dp)
                .offset(y = (260 * scanLineY).dp)
                .background(Color(0xFF1FAA59).copy(0.7f))
        )
    }
}

@Composable
private fun ScanningOverlay() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.5f)),
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
private fun ResultOverlay(result: ScanState.Result, onAddToCart: () -> Unit, onRetry: () -> Unit) {
    val green = Color(0xFF1FAA59)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Surface(color = green.copy(0.12f), shape = RoundedCornerShape(6.dp)) {
                    Text(result.wasteCategory, color = green, fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(result.wasteLabel, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("♻️", fontSize = 16.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("${result.pointPerKg} Poin / Kg", color = green,
                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Akurasi:", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    LinearProgressIndicator(progress = result.confidence,
                        modifier = Modifier.weight(1f), color = green)
                    Spacer(Modifier.width(8.dp))
                    Text("${(result.confidence * 100).toInt()}%", color = green,
                        fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(20.dp))
                Button(onClick = onAddToCart,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = green)
                ) { Text("Tambahkan ke Tong", fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50)
                ) { Text("Scan Ulang") }
            }
        }
    }
}

@Composable
private fun UnrecognizedOverlay(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔍", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Sampah Tidak Dikenali", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Coba foto ulang dengan pencahayaan lebih baik atau dari sudut berbeda",
                    color = Color.Gray, textAlign = TextAlign.Center, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50)) { Text("Coba Lagi") }
            }
        }
    }
}

@Composable
private fun LowConfidenceOverlay(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚠️", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Foto Kurang Jelas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Kepercayaan diri AI terlalu rendah. Mohon foto ulang sampah dengan lebih jelas.",
                    color = Color.Gray, textAlign = TextAlign.Center, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50)) { Text("Foto Ulang") }
            }
        }
    }
}