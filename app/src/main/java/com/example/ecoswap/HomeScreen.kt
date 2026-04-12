package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    userName: String,
    activePoint: Int = 16500,
    totalIn: Int = 26600,
    totalOut: Int = 10100
) {
    val scrollState = rememberScrollState()
    val green = Color(0xFF1FAA59)
    val greenLight = Color(0xFF38D67A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
            .verticalScroll(scrollState)
    ) {
        // ─── HEADER ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(green, greenLight)))
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.toString() ?: "U",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            "Selamat Datang!",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                        Text(
                            userName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = Color.White
                    )
                }
            }
        }

        // ─── ECOPOIN CARD ────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-20).dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF0D7A3E), green))
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("♻️", fontSize = 16.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "EcoPoin",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Poin Aktif", color = Color.White.copy(0.75f), fontSize = 12.sp)
                    Text(
                        "$activePoint Poin",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Divider(color = Color.White.copy(0.25f))

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("↓", color = Color.White.copy(0.7f), fontSize = 12.sp)
                                Spacer(Modifier.width(4.dp))
                                Text("Total Poin Masuk", color = Color.White.copy(0.7f), fontSize = 11.sp)
                            }
                            Text(
                                "$totalIn Poin",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("↑", color = Color.White.copy(0.7f), fontSize = 12.sp)
                                Spacer(Modifier.width(4.dp))
                                Text("Total Poin Keluar", color = Color.White.copy(0.7f), fontSize = 11.sp)
                            }
                            Text(
                                "$totalOut Poin",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // ─── XP PROGRESS ─────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-10).dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "80 XP lagi menjadi penyelamat negara!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = 0.7f,
                        modifier = Modifier.fillMaxWidth(),
                        color = green,
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // ─── ARTIKEL TERBARU ─────────────────────────────────────────────────
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Artikel Terbaru",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            TextButton(onClick = {}) {
                Text("Lihat semua", color = green, fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArticleCard(
                tag = "Blog & Artikel",
                title = "DaurUang: Solusi Tukar\nSampah Jadi Berkah",
                date = "25 Juli 2023"
            )
            ArticleCard(
                tag = "Edukasi",
                title = "Cara Memilah Sampah\nyang Benar di Rumah",
                date = "10 Agustus 2023"
            )
        }

        // ─── KOMENTAR PENGGUNA ────────────────────────────────────────────────
        Spacer(Modifier.height(16.dp))

        Text(
            "Komentar Pengguna",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserCommentCard("TriBudi***t", "Aplikasi yang bagus dan sangat positif")
            UserCommentCard("Salsael***g", "Sangat membantu")
            UserCommentCard("Rina***s", "Mudah digunakan, keren!")
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ArticleCard(tag: String, title: String, date: String) {
    Card(
        modifier = Modifier.width(240.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1FAA59).copy(0.5f), Color(0xFF38D67A).copy(0.3f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("♻️", fontSize = 40.sp)
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Surface(
                    color = Color(0xFF1FAA59).copy(0.12f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        tag,
                        color = Color(0xFF1FAA59),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text(date, color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun UserCommentCard(user: String, comment: String) {
    Card(
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 16.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text(user, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(6.dp))
            Text(comment, fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}