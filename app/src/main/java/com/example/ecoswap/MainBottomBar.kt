package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// ─────────────────────────────────────────────────────────────────────────────
//  MAIN BOTTOM BAR
//  Tombol tengah (Scan) dibuat menonjol seperti tombol QRIS di e-wallet.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MainBottomBar(navController: NavController, currentRoute: String) {
    val green = Color(0xFF1FAA59)

    Box {
        NavigationBar(containerColor = Color.White) {
            // Beranda
            NavigationBarItem(
                selected = currentRoute.startsWith("home"),
                onClick = { navController.navigate("home/Dimas") },
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Beranda") },
                colors = NavigationBarItemDefaults.colors(indicatorColor = green.copy(0.15f))
            )

            // Tong Sampah
            NavigationBarItem(
                selected = currentRoute == "ecoswap",
                onClick = { navController.navigate("ecoswap") },
                icon = { Icon(Icons.Default.Delete, null) },
                label = { Text("EcoSwap") },
                colors = NavigationBarItemDefaults.colors(indicatorColor = green.copy(0.15f))
            )

            // Scan — placeholder spacer so center FAB fits
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { Spacer(Modifier.size(56.dp)) },
                label = { Text("Scan") },
                enabled = false
            )

            // Riwayat
            NavigationBarItem(
                selected = currentRoute == "history",
                onClick = { navController.navigate("history") },
                icon = { Icon(Icons.Default.History, null) },
                label = { Text("Riwayat") },
                colors = NavigationBarItemDefaults.colors(indicatorColor = green.copy(0.15f))
            )

            // Profil
            NavigationBarItem(
                selected = currentRoute.startsWith("profile"),
                onClick = { navController.navigate("profile") },
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("Profil") },
                colors = NavigationBarItemDefaults.colors(indicatorColor = green.copy(0.15f))
            )
        }

        // ── CENTER FAB ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp)
                .size(60.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(green)
                .clickableNoRipple { navController.navigate("scan") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = "Scan",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}