package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController, userName: String = "Dimas Ardiansyah") {
    val green = Color(0xFF1FAA59)
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
            .verticalScroll(rememberScrollState())
    ) {
        // ─── HEADER ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(green, Color(0xFF38D67A))))
                .padding(horizontal = 20.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        userName.firstOrNull()?.toString() ?: "U",
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

                Spacer(Modifier.height(4.dp))

                Surface(
                    color = Color.White.copy(0.2f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "🌱 Siswa Hijau",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // ─── STATS ───────────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-20).dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("16.500", "EcoPoin", "♻️")
                Divider(modifier = Modifier.width(1.dp).height(48.dp))
                StatItem("720", "XP", "⭐")
                Divider(modifier = Modifier.width(1.dp).height(48.dp))
                StatItem("Level 3", "Peringkat", "🏆")
            }
        }

        // ─── RANK CARD ───────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D7A3E))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⭐", fontSize = 32.sp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kota 720 XP", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("80 XP lagi naik level!", color = Color.White.copy(0.8f), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = 0.72f,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        trackColor = Color.White.copy(0.3f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── MENU ITEMS ──────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                ProfileMenuItem(Icons.Default.Person, "Edit Profil", green) {}
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(Icons.Default.LocationOn, "Tambah Alamat", green) {}
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(Icons.Default.Lock, "Ubah Kata Sandi", green) {}
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(Icons.Default.Notifications, "Notifikasi", green) {}
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(Icons.Default.Info, "Tentang Aplikasi", green) {}
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── LOGOUT ──────────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            ProfileMenuItem(Icons.Default.Logout, "Keluar", Color.Red) {
                showLogoutDialog = true
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // ─── LOGOUT DIALOG ────────────────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar?") },
            text = { Text("Apakah kamu yakin ingin keluar dari akun ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        navController.navigate("role") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Keluar") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun StatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(Modifier.clickableNoRipple(onClick))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 14.sp)
        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
    }
}