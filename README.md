# EcoSwap — Aplikasi Pilah Sampah Pintar

> Aplikasi Android (Kotlin + Jetpack Compose) untuk siswa dan mitra Adiwiyata.  
> Tukar sampah → dapat EcoPoin → tukar jadi reward.

---

## 📋 Daftar Isi

1. [Struktur Proyek](#struktur-proyek)
2. [Setup Firebase (Android Developer)](#setup-firebase)
3. [AI Integration Bridge](#ai-integration-bridge)
4. [Berbagi Akses Firebase ke AI Developer](#berbagi-akses-firebase)
5. [Alur Aplikasi](#alur-aplikasi)
6. [Screen yang Sudah Ada](#screen-yang-sudah-ada)
7. [Yang Belum Selesai / TODO](#todo)

---
### Struktur Firestore Collections:

```
firestore/
├── users/              {uid}      → UserProfile
├── scan_results/       {auto-id}  → ScanResult   ← AI tulis ke sini
├── cart_items/         {auto-id}  → CartItem
├── transactions/       {auto-id}  → Transaction
├── waste_catalog/      {auto-id}  → WasteItem     ← Admin isi manual
└── rewards/            {auto-id}  → RewardItem    ← Admin isi manual
```

---

## 🤖 AI Integration Bridge

> Bagian ini khusus untuk **AZKA** yang mengintegrasikan model YOLO.

### Cara Kerja:

```
[Kamera] → [YOLO Inference di device] → [FirebaseRepository.saveScanResult()] → [Firestore]
                                                        ↓
                                          [Otomatis tambah ke CartItem]
                                          [User bisa setorkan ke mitra]
```

### Yang Perlu AZKA Lakukan:

#### 1. Aktifkan CameraX di `build.gradle.kts`
```kotlin
// Uncomment baris ini di app/build.gradle.kts:
implementation("androidx.camera:camera-core:1.3.4")
implementation("androidx.camera:camera-camera2:1.3.4")
implementation("androidx.camera:camera-lifecycle:1.3.4")
implementation("androidx.camera:camera-view:1.3.4")
```

#### 2. Ganti placeholder di `ScanScreen.kt`
Cari komentar `// TODO (AI Developer):` di `ScanScreen.kt`:
```kotlin
// Ganti Box placeholder ini dengan CameraX Preview:
val previewView = remember { PreviewView(context) }
AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
LaunchedEffect(Unit) { startCamera(context, previewView, lifecycleOwner) }
```

#### 3. Setelah YOLO inference, panggil ini:
```kotlin
// Import
import com.example.ecoswap.data.FirebaseRepository
import com.example.ecoswap.data.ScanResult

// Di dalam coroutine scope setelah inference:
val repo = FirebaseRepository()
val result = ScanResult(
    wasteLabel   = "Botol Plastik",      // dari YOLO output label
    wasteCategory = "anorganik",          // mapping dari label
    confidence   = 0.87f,                // dari YOLO confidence score
    status       = when {
        confidence < 0.5f -> "low_confidence"
        wasteLabel.isEmpty() -> "unrecognized"
        else -> "recognized"
    },
    pointsEarned = lookupPointPerKg(wasteLabel),  // dari waste_catalog
    imageUrl     = uploadedImageUrl               // upload ke Firebase Storage dulu
)
val scanId = repo.saveScanResult(result).getOrThrow()
// Selesai! UI di ScanScreen akan update otomatis via state
```

#### 4. Mapping Label YOLO → Kategori & Poin
Referensi dari `waste_catalog` Firestore (isi manual oleh admin):

| Label YOLO | Kategori | Poin/Kg |
|---|---|---|
| botol_plastik | Non-Organik Plastik | 1300 |
| botol_kaca | Non-Organik Kaca | 1500 |
| botol_atom | Non-Organik Plastik | 2000 |
| tutup_botol | Non-Organik Plastik | 2000 |
| ember_plastik | Non-Organik Plastik | 1400 |
| kardus | Non-Organik Kertas | 800 |

#### 5. Upload Foto ke Firebase Storage
```kotlin
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

suspend fun uploadScanImage(imageBytes: ByteArray, userId: String): String {
    val ref = FirebaseStorage.getInstance()
        .reference
        .child("scan_images/$userId/${System.currentTimeMillis()}.jpg")
    ref.putBytes(imageBytes).await()
    return ref.downloadUrl.await().toString()
}
```

---

## 🗺️ Alur Aplikasi

```
Splash → Role Screen
              ├── Siswa → Login (NIS + password) → Home
              │               └── Register (NIS baru)
              └── Mitra → Login (Email + password) → Home

Home → Bottom Bar:
  ├── 🏠 Beranda     → HomeScreen (poin, artikel, komentar)
  ├── 🗑️ EcoSwap    → Katalog sampah + Reward
  ├── 📷 [SCAN]      → ScanScreen (AI inference) ← TOMBOL TENGAH
  ├── 📋 Riwayat     → HistoryScreen (transaksi)
  └── 👤 Profil      → ProfileScreen (logout, edit)
```

---

## 📱 Screen yang Sudah Ada

| Screen | Status | Catatan |
|---|---|---|
| SplashScreen | ✅ Selesai | Animasi scale + fade |
| RoleScreen | ✅ Selesai | Siswa / Mitra |
| LoginScreen | ✅ Selesai | Firebase Auth |
| RegisterScreen | ✅ Selesai | Firebase Auth |
| HomeScreen | ✅ Selesai | EcoPoin card, XP, artikel |
| EcoSwapScreen | ✅ Selesai | Katalog + Reward tabs |
| ScanScreen | ⚠️ Perlu AI | Placeholder kamera siap |
| CartScreen | ✅ Selesai | Tong sampah + setorkan |
| HistoryScreen | ✅ Selesai | Filter masuk/keluar |
| ProfileScreen | ✅ Selesai | Rank, menu, logout |

---

## ✅ TODO

### AI Developer:
- [ ] Integrasikan CameraX ke `ScanScreen.kt`
- [ ] Deploy model YOLO ke Android (TFLite / ONNX)
- [ ] Panggil `FirebaseRepository.saveScanResult()` setelah inference
- [ ] Fungsi upload foto scan ke Firebase Storage
- [ ] Buat label mapping YOLO → kategori sampah


