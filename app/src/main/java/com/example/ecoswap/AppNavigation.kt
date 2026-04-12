package com.example.ecoswap

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = setOf("home/{name}", "ecoswap", "scan", "history", "profile/{name}")
    val showBottomBar = bottomBarRoutes.any { currentRoute?.startsWith(it.substringBefore("{")) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(navController, currentRoute ?: "")
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(padding)
        ) {

            // ── AUTH FLOW ─────────────────────────────────────────────────────
            composable("splash") {
                SplashScreen(navController)
            }

            composable("role") {
                RoleScreen(navController)
            }

            composable("login/{role}") { back ->
                val role = back.arguments?.getString("role") ?: "siswa"
                LoginScreen(navController, role)
            }

            composable("register") {
                RegisterScreen(navController)
            }

            // ── MAIN FLOW ─────────────────────────────────────────────────────
            composable("home/{name}") { back ->
                val name = back.arguments?.getString("name") ?: ""
                HomeScreen(userName = name)
            }

            composable("ecoswap") {
                EcoSwapScreen()
            }

            composable("scan") {
                ScanScreen(navController)
            }

            composable("history") {
                HistoryScreen()
            }

            composable("profile/{name}") { back ->
                val name = back.arguments?.getString("name") ?: ""
                ProfileScreen(navController, name)
            }

            // Fallback: profile tanpa nama
            composable("profile") {
                ProfileScreen(navController)
            }
        }
    }
}