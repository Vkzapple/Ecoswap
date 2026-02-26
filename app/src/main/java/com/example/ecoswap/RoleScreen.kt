package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

@Composable
fun RoleScreen(navController: NavController) {

    Box(modifier = Modifier.fillMaxSize()) {

        // Background (sudah ada overlay ijo)
        Image(
            painter = painterResource(id = R.drawable.rolebg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Green overlay biar readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA1FAA59))
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Selamat Datang",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("login/siswa") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Masuk sebagai Siswa")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("login/mitra") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Masuk sebagai Mitra")
            }
        }
    }
}