package com.example.ecoswap.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

// ─────────────────────────────────────────────────────────────────────────────
//  FIREBASE REPOSITORY
//  Semua operasi Firestore ada di sini.
//  NOTE FOR AZKA:
//  Gunakan `FirebaseRepository.saveScanResult(...)` setelah inference selesai.
//  Lihat README.md untuk contoh penggunaan lengkap.
// ─────────────────────────────────────────────────────────────────────────────

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUid get() = auth.currentUser?.uid ?: ""

    // ─── AUTH ────────────────────────────────────────────────────────────────

    suspend fun registerSiswa(
        name: String,
        nis: String,
        password: String
    ): Result<UserProfile> = runCatching {
        // Buat email internal dari NIS (karena Firebase Auth butuh email format)
        val internalEmail = "siswa_${nis}@ecoswap.internal"
        val authResult = auth.createUserWithEmailAndPassword(internalEmail, password).await()
        val uid = authResult.user?.uid ?: error("UID null")

        val profile = UserProfile(
            uid = uid,
            name = name,
            nis = nis,
            email = internalEmail,
            role = "siswa"
        )
        db.collection("users").document(uid).set(profile).await()
        profile
    }

    suspend fun loginSiswa(nis: String, password: String): Result<UserProfile> = runCatching {
        val internalEmail = "siswa_${nis}@ecoswap.internal"
        val authResult = auth.signInWithEmailAndPassword(internalEmail, password).await()
        val uid = authResult.user?.uid ?: error("UID null")
        getUserProfile(uid) ?: error("Profil tidak ditemukan")
    }

    suspend fun loginMitra(email: String, password: String): Result<UserProfile> = runCatching {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: error("UID null")
        getUserProfile(uid) ?: error("Profil tidak ditemukan")
    }

    fun logout() = auth.signOut()

    // ─── USER PROFILE ────────────────────────────────────────────────────────

    suspend fun getUserProfile(uid: String): UserProfile? {
        return db.collection("users").document(uid).get().await()
            .toObject(UserProfile::class.java)
    }

    suspend fun getCurrentUserProfile(): UserProfile? = getUserProfile(currentUid)

    suspend fun updateEcoPoint(uid: String, delta: Int, isEarn: Boolean) {
        val ref = db.collection("users").document(uid)
        db.runTransaction { tx ->
            val snap = tx.get(ref)
            val current = snap.getLong("ecoPoint")?.toInt() ?: 0
            val totalIn = snap.getLong("totalPointIn")?.toInt() ?: 0
            val totalOut = snap.getLong("totalPointOut")?.toInt() ?: 0
            tx.update(ref, mapOf(
                "ecoPoint" to (current + if (isEarn) delta else -delta),
                "totalPointIn" to if (isEarn) totalIn + delta else totalIn,
                "totalPointOut" to if (!isEarn) totalOut + delta else totalOut
            ))
        }.await()
    }

    // ─── SCAN RESULTS ────────────────────────────────────────────────────────

    /**
     * NOTE FOR AI DEVELOPER:
     * Panggil fungsi ini setelah YOLO inference selesai.
     *
     * Contoh:
     * ```kotlin
     * val repo = FirebaseRepository()
     * val result = ScanResult(
     *     userId = FirebaseAuth.getInstance().currentUser!!.uid,
     *     wasteLabel = "Botol Plastik",
     *     wasteCategory = "anorganik",
     *     confidence = 0.87f,
     *     status = "recognized",
     *     pointsEarned = 1500,
     *     imageUrl = uploadedImageUrl
     * )
     * repo.saveScanResult(result)
     * ```
     */
    suspend fun saveScanResult(result: ScanResult): Result<String> = runCatching {
        val ref = db.collection("scan_results").document()
        val withId = result.copy(id = ref.id, userId = currentUid)
        ref.set(withId).await()

        // Tambah ke cart otomatis jika recognized
        if (result.status == "recognized") {
            addToCart(CartItem(
                userId = currentUid,
                scanResultId = ref.id,
                wasteLabel = result.wasteLabel,
                wasteCategory = result.wasteCategory,
                estimatedPointPerKg = result.pointsEarned,
                imageUrl = result.imageUrl
            ))
        }
        ref.id
    }

    suspend fun getMyScans(): List<ScanResult> {
        return db.collection("scan_results")
            .whereEqualTo("userId", currentUid)
            .orderBy("scannedAt", Query.Direction.DESCENDING)
            .limit(50)
            .get().await()
            .toObjects(ScanResult::class.java)
    }

    // ─── CART (TONG SAMPAH) ──────────────────────────────────────────────────

    suspend fun addToCart(item: CartItem): Result<Unit> = runCatching {
        val ref = db.collection("cart_items").document()
        ref.set(item.copy(id = ref.id, userId = currentUid)).await()
    }

    suspend fun getMyCart(): List<CartItem> {
        return db.collection("cart_items")
            .whereEqualTo("userId", currentUid)
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(CartItem::class.java)
    }

    suspend fun removeFromCart(cartItemId: String) {
        db.collection("cart_items").document(cartItemId).delete().await()
    }

    suspend fun submitCart(cartItems: List<CartItem>): Result<Unit> = runCatching {
        val batch = db.batch()
        var totalPoints = 0

        cartItems.forEach { item ->
            // Hapus dari cart
            batch.delete(db.collection("cart_items").document(item.id))
            // Catat transaksi
            val txRef = db.collection("transactions").document()
            batch.set(txRef, Transaction(
                id = txRef.id,
                userId = currentUid,
                type = "earn",
                pointAmount = item.estimatedPointPerKg,
                description = "Setor sampah: ${item.wasteLabel}",
                relatedScanId = item.scanResultId
            ))
            totalPoints += item.estimatedPointPerKg
        }
        batch.commit().await()
        updateEcoPoint(currentUid, totalPoints, isEarn = true)
    }

    // ─── WASTE CATALOG ───────────────────────────────────────────────────────

    suspend fun getWasteCatalog(): List<WasteItem> {
        return db.collection("waste_catalog")
            .whereEqualTo("isActive", true)
            .get().await()
            .toObjects(WasteItem::class.java)
    }

    suspend fun searchWaste(query: String): List<WasteItem> {
        // Firestore tidak support full-text search native
        // Solusi: load semua lalu filter di client (ok untuk data kecil)
        return getWasteCatalog().filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
    }

    // ─── REWARDS ─────────────────────────────────────────────────────────────

    suspend fun getRewards(): List<RewardItem> {
        return db.collection("rewards")
            .whereGreaterThan("stock", 0)
            .get().await()
            .toObjects(RewardItem::class.java)
    }

    suspend fun redeemReward(reward: RewardItem): Result<Unit> = runCatching {
        val userRef = db.collection("users").document(currentUid)
        val rewardRef = db.collection("rewards").document(reward.id)

        db.runTransaction { tx ->
            val userSnap = tx.get(userRef)
            val currentPoint = userSnap.getLong("ecoPoint")?.toInt() ?: 0
            val rewardSnap = tx.get(rewardRef)
            val stock = rewardSnap.getLong("stock")?.toInt() ?: 0

            if (currentPoint < reward.pointCost) error("Poin tidak cukup")
            if (stock <= 0) error("Stok habis")

            tx.update(userRef, "ecoPoint", currentPoint - reward.pointCost)
            tx.update(rewardRef, "stock", stock - 1)

            val txRef = db.collection("transactions").document()
            tx.set(txRef, Transaction(
                id = txRef.id,
                userId = currentUid,
                type = "redeem",
                pointAmount = reward.pointCost,
                description = "Tukar reward: ${reward.name}",
                relatedRewardId = reward.id
            ))
        }.await()

        // Update total point out
        updateEcoPoint(currentUid, reward.pointCost, isEarn = false)
    }

    // ─── TRANSACTIONS / HISTORY ──────────────────────────────────────────────

    suspend fun getMyTransactions(): List<Transaction> {
        return db.collection("transactions")
            .whereEqualTo("userId", currentUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .get().await()
            .toObjects(Transaction::class.java)
    }
}