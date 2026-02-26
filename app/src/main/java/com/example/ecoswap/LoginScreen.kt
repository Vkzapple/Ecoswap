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
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    navController: NavController,
    role: String,
    viewModel: AuthViewModel = viewModel()
) {

    var nisOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1FAA59), Color(0xFF38D67A))
                )
            )
    ) {

        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 80.dp)
        ) {
            Text(
                "Login",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                "untuk mengakses aplikasi!",
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        // WHITE CARD
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nisOrEmail,
                    onValueChange = { nisOrEmail = it },
                    label = {
                        Text(
                            if (role == "siswa") "NIS"
                            else "Email Sekolah"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1FAA59),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata Sandi") },
                    singleLine = true,
                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {
                            Icon(
                                imageVector =
                                    if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else
                                        Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Lupa Kata Sandi?",
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { },
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // LOGIN BUTTON
                Button(
                    onClick = {
                        // dummy login
                        navController.navigate("home/Dimas")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1FAA59)
                    )
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DIVIDER
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "  atau lanjut dengan  ",
                        color = Color.Gray
                    )
                    Divider(
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SOCIAL BUTTONS
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Facebook")
                    }

                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Google")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // REGISTER
                if (role == "siswa") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Belum memiliki akun? ")
                        Text(
                            "Daftar",
                            color = Color(0xFF1FAA59),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("register")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}