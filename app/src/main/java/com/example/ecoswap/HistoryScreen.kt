package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DemoTransaction(
    val title: String,
    val subtitle: String,
    val point: Int,
    val isEarn: Boolean,
    val date: String,
    val emoji: String
)

@Composable
fun HistoryScreen() {
    val green = Color(0xFF1FAA59)
    var selectedTab by remember { mutableStateOf(0) }

    val allTransactions = listOf(
        DemoTransaction("Setor Botol Kaca", "Non-Organik Kaca", 1500, true, "25 Jul 2023", "🍾"),
        DemoTransaction("Tukar GoPay 10rb", "Penukaran Reward", 10100, false, "24 Jul 2023", "💳"),
        DemoTransaction("Setor Botol Plastik", "Non-Organik Plastik", 1300, true, "23 Jul 2023", "🥤"),
        DemoTransaction("Setor Kardus", "Non-Organik Kertas", 800, true, "20 Jul 2023", "📦"),
        DemoTransaction("Tukar Pulsa 10rb", "Penukaran Reward", 15000, false, "15 Jul 2023", "📱"),
    )

    val filtered = when (selectedTab) {
        1 -> allTransactions.filter { it.isEarn }
        2 -> allTransactions.filter { !it.isEarn }
        else -> allTransactions
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {
        // ─── HEADER ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(green, Color(0xFF38D67A))))
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                "Riwayat Transaksi",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        // ─── TABS ────────────────────────────────────────────────────────────
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = green
        ) {
            listOf("Semua", "Poin Masuk", "Poin Keluar").forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                ) {
                    Text(label, modifier = Modifier.padding(vertical = 12.dp), fontSize = 13.sp)
                }
            }
        }

        // ─── LIST ────────────────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("📋", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Belum ada transaksi", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { tx ->
                    TransactionCard(tx, green)
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(tx: DemoTransaction, green: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (tx.isEarn) green.copy(0.1f) else Color.Red.copy(0.08f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(tx.emoji, fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(tx.subtitle, color = Color.Gray, fontSize = 12.sp)
                Text(tx.date, color = Color.Gray, fontSize = 11.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (tx.isEarn) "+" else "-"}${tx.point}",
                    color = if (tx.isEarn) green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text("Poin", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}