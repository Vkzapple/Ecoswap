package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    navController: NavController,
    role: String,
    viewModel: AuthViewModel = viewModel()
) {
    var nisOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val state = viewModel.uiState
    val green = Color(0xFF1FAA59)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(green, Color(0xFF38D67A))))
    ) {
        // ── HEADER ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 80.dp)
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text("untuk mengakses aplikasi!", color = Color.White.copy(0.8f))
        }

        // ── CARD ──────────────────────────────────────────────────────────────
        Card(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp).fillMaxWidth()
            ) {
                Spacer(Modifier.height(8.dp))

                // NIS / Email field
                OutlinedTextField(
                    value = nisOrEmail,
                    onValueChange = { nisOrEmail = it },
                    label = { Text(if (role == "siswa") "NIS" else "Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = green,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata Sandi") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Lupa Kata Sandi?",
                    modifier = Modifier.align(Alignment.End).clickable {},
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                // Error
                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(8.dp))

                // Login Button
                Button(
                    onClick = {
                        if (nisOrEmail.isNotBlank() && password.isNotBlank()) {
                            if (role == "siswa") {
                                viewModel.loginSiswa(nisOrEmail, password) { profile ->
                                    navController.navigate("home/${profile.name}") {
                                        popUpTo("login/$role") { inclusive = true }
                                    }
                                }
                            } else {
                                viewModel.loginMitra(nisOrEmail, password) { profile ->
                                    navController.navigate("home/${profile.name}") {
                                        popUpTo("login/$role") { inclusive = true }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else Text("Login", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f))
                    Text("  atau lanjut dengan  ", color = Color.Gray)
                    Divider(modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = {}, shape = RoundedCornerShape(12.dp)) { Text("Facebook") }
                    OutlinedButton(onClick = {}, shape = RoundedCornerShape(12.dp)) { Text("Google") }
                }

                if (role == "siswa") {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Belum memiliki akun? ")
                        Text(
                            "Daftar",
                            color = green,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate("register") }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}