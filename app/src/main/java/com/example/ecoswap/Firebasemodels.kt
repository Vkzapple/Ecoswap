package com.example.ecoswap.data

import com.google.firebase.Timestamp

// ─────────────────────────────────────────────────────────────────────────────
//  FIREBASE DATA MODELS
//  NOTE FOR AZKA:
//  Semua model ini di-mirror ke Firestore collection dengan nama yang sama.
//  Field `scanResult` diisi sama AI pipeline setelah YOLO inference selesai.
//  Lihat README.md section "AI Integration Bridge" buat detail.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Firestore collection: "users"
 * Document ID: Firebase Auth UID
 */
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val nis: String = "",           // untuk siswa
    val email: String = "",         // untuk mitra
    val role: String = "siswa",     // "siswa" | "mitra"
    val ecoPoint: Int = 0,
    val totalPointIn: Int = 0,
    val totalPointOut: Int = 0,
    val xp: Int = 0,
    val level: String = "Pemula",
    val profileImageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * Firestore collection: "scan_results"
 * Document ID: auto-generated
 *
 * NOTE FOR AI DEVELOPER:
 * - Setelah YOLO inference di device, tulis ke collection ini.
 * - Field yang WAJIB diisi AI: wasteLabel, wasteCategory, confidence, pointsEarned
 * - Field status: "pending" → "recognized" | "unrecognized" | "low_confidence"
 */
data class ScanResult(
    val id: String = "",
    val userId: String = "",            // Firebase Auth UID
    val wasteLabel: String = "",        // nama sampah dari YOLO (e.g. "Botol Plastik")
    val wasteCategory: String = "",     // "organik" | "anorganik" | "b3"
    val confidence: Float = 0f,         // 0.0 - 1.0
    val status: String = "pending",     // "recognized" | "unrecognized" | "low_confidence"
    val pointsEarned: Int = 0,
    val imageUrl: String = "",
    val scannedAt: Timestamp = Timestamp.now()
)

/**
 * Firestore collection: "waste_catalog"
 * Document ID: auto-generated
 * Diisi oleh admin/mitra
 */
data class WasteItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",          // "Non-Organik Plastik" | "Non-Organik Kaca" | etc
    val pointPerKg: Int = 0,
    val imageUrl: String = "",
    val description: String = "",
    val isActive: Boolean = true
)

/**
 * Firestore collection: "rewards"
 * Document ID: auto-generated
 * Barang yang bisa ditukar dengan EcoPoin
 */
data class RewardItem(
    val id: String = "",
    val name: String = "",
    val pointCost: Int = 0,
    val stock: Int = 0,
    val imageUrl: String = "",
    val description: String = "",
    val category: String = ""
)

/**
 * Firestore collection: "transactions"
 * Document ID: auto-generated
 */
data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "",              // "earn" | "redeem"
    val pointAmount: Int = 0,
    val description: String = "",
    val relatedScanId: String = "",     // jika dari scan sampah
    val relatedRewardId: String = "",   // jika penukaran reward
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * Firestore collection: "cart_items"
 * Document ID: auto-generated
 * Tong sampah sementara sebelum disetorkan ke mitra
 */
data class CartItem(
    val id: String = "",
    val userId: String = "",
    val scanResultId: String = "",
    val wasteLabel: String = "",
    val wasteCategory: String = "",
    val estimatedPointPerKg: Int = 0,
    val imageUrl: String = "",
    val addedAt: Timestamp = Timestamp.now()
)