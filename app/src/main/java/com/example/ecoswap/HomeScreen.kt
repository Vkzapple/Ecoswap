package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
@Composable
fun HomeScreen(
    userName: String,
    activePoint: Int,
    totalIn: Int,
    totalOut: Int
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1FAA59), Color(0xFF38D67A))
                    )
                )
                .padding(24.dp)
        ) {

            Column {
                Text(
                    "Selamat Datang!",
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    userName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ECOPOINT CARD
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-60).dp),
            shape = RoundedCornerShape(20.dp)
        ) {

            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1FAA59), Color(0xFF38D67A))
                        )
                    )
                    .padding(20.dp)
            ) {

                Column {

                    Text(
                        "EcoPoin",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Poin Aktif",
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Text(
                        "$activePoint Poin",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = Color.White.copy(alpha = 0.3f))

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column {
                            Text("Total Poin Masuk", color = Color.White.copy(0.7f))
                            Text("$totalIn Poin", color = Color.White)
                        }

                        Column {
                            Text("Total Poin Keluar", color = Color.White.copy(0.7f))
                            Text("$totalOut Poin", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // PROGRESS CARD
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("80 XP lagi menjadi penyelamat negara!")

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = 0.7f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}