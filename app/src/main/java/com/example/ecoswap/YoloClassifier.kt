package com.example.ecoswap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

data class Detection(
    val label: String,
    val confidence: Float,
    val boundingBox: RectF,     // values 0.0–1.0, relative to image size
    val category: String,
    val pointsPerKg: Int
)

class YoloDetector(context: Context) {

    // ── CONFIGURE THESE FOR YOUR MODEL ───────────────────────────────────────
    private val modelFileName       = "best32.tflite"   // file in assets/
    private val inputSize           = 640             // must match model export imgsz
    private val confidenceThreshold = 0.35f                 // min score to show detection
    private val iouThreshold        = 0.45f                 // NMS overlap threshold
    private val maxDetections       = 10                    // max boxes on screen

    // Order MUST match metadata.yaml "names" section exactly
    private val labelMap = listOf(
        "battery",
        "biodegradable",
        "cardboard",
        "metal",
        "paper",
        "plastic-bottles"
    )

    private val categoryMap = mapOf(
        "plastic-bottles"  to Pair("Non-Organik Plastik", 1300),
        "biodegradable"    to Pair("Organik Buah dan Tumbuhan",             500),
        "cardboard"        to Pair("Non-Organik Kardus",         1000),
        "battery"          to Pair("B3 / Berbahaya",      2000),
        "metal"            to Pair("Non-Organik Metal", 2000),
        "paper"            to Pair("Organik Kertas", 1000)
    )
    // ─────────────────────────────────────────────────────────────────────────

    private val interpreter: Interpreter
    private val numClasses = labelMap.size

    init {
        val afd   = context.assets.openFd(modelFileName)
        val model = FileInputStream(afd.fileDescriptor).channel.map(
            FileChannel.MapMode.READ_ONLY,
            afd.startOffset,
            afd.declaredLength
        )
        interpreter = Interpreter(model)

        android.util.Log.d("YOLO_SHAPE",
            "Input:  ${interpreter.getInputTensor(0).shape().toList()}")
        android.util.Log.d("YOLO_SHAPE",
            "Output: ${interpreter.getOutputTensor(0).shape().toList()}")
    }

    fun detect(bitmap: Bitmap): List<Detection> {
        val resized     = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val inputBuffer = bitmapToByteBuffer(resized)


        val numAnchors = 8400  // standard for 640x640 YOLOv8
        val output     = Array(1) { Array(4 + numClasses) { FloatArray(numAnchors) } }
        interpreter.run(inputBuffer, output)

        val detections = mutableListOf<Detection>()

        for (anchor in 0 until numAnchors) {
            // Box coords are center-x, center-y, width, height (normalized 0–1)
            val cx = output[0][0][anchor]
            val cy = output[0][1][anchor]
            val w  = output[0][2][anchor]
            val h  = output[0][3][anchor]

            // Find best class for this anchor
            var bestClass = -1
            var bestScore = 0f
            for (c in 0 until numClasses) {
                val score = output[0][4 + c][anchor]
                if (score > bestScore) {
                    bestScore = score
                    bestClass = c
                }
            }

            if (bestScore < confidenceThreshold || bestClass == -1) continue

            // Convert cx,cy,w,h → left,top,right,bottom
            val left   = (cx - w / 2f).coerceIn(0f, 1f)
            val top    = (cy - h / 2f).coerceIn(0f, 1f)
            val right  = (cx + w / 2f).coerceIn(0f, 1f)
            val bottom = (cy + h / 2f).coerceIn(0f, 1f)

            val label              = labelMap[bestClass]
            val (category, points) = categoryMap[label] ?: Pair("Unknown", 0)

            detections.add(Detection(
                label       = label,
                confidence  = bestScore,
                boundingBox = RectF(left, top, right, bottom),
                category    = category,
                pointsPerKg = points
            ))
        }

        return applyNMS(detections).take(maxDetections)
    }

    // Non-Maximum Suppression — removes duplicate overlapping boxes
    private fun applyNMS(detections: List<Detection>): List<Detection> {
        val sorted  = detections.sortedByDescending { it.confidence }.toMutableList()
        val results = mutableListOf<Detection>()

        while (sorted.isNotEmpty()) {
            val best = sorted.removeAt(0)
            results.add(best)
            sorted.removeAll { iou(best.boundingBox, it.boundingBox) > iouThreshold }
        }
        return results
    }

    private fun iou(a: RectF, b: RectF): Float {
        val interLeft   = maxOf(a.left,   b.left)
        val interTop    = maxOf(a.top,    b.top)
        val interRight  = minOf(a.right,  b.right)
        val interBottom = minOf(a.bottom, b.bottom)
        val interArea   = maxOf(0f, interRight - interLeft) * maxOf(0f, interBottom - interTop)
        val unionArea   = (a.width() * a.height()) + (b.width() * b.height()) - interArea
        return if (unionArea <= 0f) 0f else interArea / unionArea
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        buffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
        for (pixel in pixels) {
            // ImageNet normalization — required for YOLOv8
            val r = ((pixel shr 16) and 0xFF) / 255f
            val g = ((pixel shr 8)  and 0xFF) / 255f
            val b = ((pixel)        and 0xFF) / 255f
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }
        return buffer
    }

    fun close() = interpreter.close()
}