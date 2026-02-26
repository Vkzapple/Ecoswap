package com.example.ecoswap

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color

@Composable
fun MainBottomBar(
    navController: NavController,
    currentRoute: String?
) {

    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Beranda") }
        )

        NavigationBarItem(
            selected = currentRoute == "ecoswap",
            onClick = { navController.navigate("ecoswap") },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("EcoSwap") }
        )

        NavigationBarItem(
            selected = currentRoute == "scan",
            onClick = { navController.navigate("scan") },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
            label = { Text("Scan") }
        )

        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = { navController.navigate("history") },
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text("Riwayat") }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profil") }
        )
    }
}