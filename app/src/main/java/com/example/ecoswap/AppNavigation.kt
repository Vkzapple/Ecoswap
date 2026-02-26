package com.example.ecoswap

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar =
        currentRoute?.startsWith("home") == true ||
                currentRoute == "ecoswap" ||
                currentRoute == "scan" ||
                currentRoute == "history" ||
                currentRoute == "profile"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(navController, currentRoute ?: "")
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "splash", // 🔥 UBAH DI SINI
            modifier = Modifier.padding(padding)
        ) {

            // ================= AUTH FLOW =================

            composable("splash") {
                SplashScreen(navController)
            }

            composable("role") {
                RoleScreen(navController)
            }

            composable("login/{role}") { backStackEntry ->
                val role = backStackEntry.arguments?.getString("role") ?: "siswa"
                LoginScreen(navController, role)
            }

            composable("register") {
                RegisterScreen(navController)
            }

            // ================= MAIN FLOW =================

            composable("home/{name}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                HomeScreen(
                    userName = name,
                    activePoint = 16500,
                    totalIn = 26600,
                    totalOut = 10100
                )
            }

            composable("ecoswap") {}
            composable("scan") {}
            composable("history") {}
            composable("profile") {}
        }
    }
}