package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EcoSwapScreen() {
    val green = Color(0xFF1FAA59)
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0=Katalog, 1=Reward
    var selectedCategory by remember { mutableStateOf("Semua") }

    val categories = listOf("Semua", "Elektronik", "Kaca", "Kertas", "Plastik")

    val demoWasteItems = listOf(
        DemoWasteItem("Botol Atom", "Non-Organik Plastik", 2000, "🧴"),
        DemoWasteItem("Botol Kaca", "Non-Organik Kaca", 1500, "🍾"),
        DemoWasteItem("Botol Plastik", "Non-Organik Plastik", 1300, "🥤"),
        DemoWasteItem("Tutup Botol", "Non-Organik Plastik", 2000, "🔵"),
        DemoWasteItem("Ember Plastik", "Non-Organik Plastik", 1400, "🪣"),
        DemoWasteItem("Kardus", "Non-Organik Kertas", 800, "📦"),
    )

    val demoRewards = listOf(
        DemoRewardItem("GoPay", 10100, "💳"),
        DemoRewardItem("OVO", 20100, "💳"),
        DemoRewardItem("Pulsa 10rb", 15000, "📱"),
        DemoRewardItem("Alat Tulis", 5000, "✏️"),
    )

    val filtered = if (searchQuery.isBlank()) demoWasteItems
    else demoWasteItems.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {
        // ─── HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(green, Color(0xFF38D67A))))
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    "Katalog Penukaran",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(12.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Benih...", color = Color.White.copy(0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = Color.White.copy(0.8f))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(0.5f),
                        focusedBorderColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(0.15f),
                        focusedContainerColor = Color.White.copy(0.2f),
                        cursorColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(50)
                )
            }
        }

        // ─── TABS
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = green
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Katalog Sampah", modifier = Modifier.padding(vertical = 12.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Reward", modifier = Modifier.padding(vertical = 12.dp))
            }
        }

        if (selectedTab == 0) {
            // ─── CATEGORY CHIPS
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = green,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // ─── RESULTS
            if (filtered.isEmpty()) {
                EmptySearchResult()
            } else {
                Text(
                    "${filtered.size} ditemukan",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // 2-column grid via rows
                    items(filtered.chunked(2)) { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            rowItems.forEach { item ->
                                WasteItemCard(item, green, modifier = Modifier.weight(1f))
                            }
                            if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // ─── REWARDS ──────────────────────────────────────────────────────
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(demoRewards) { reward ->
                    RewardCard(reward, green)
                }
            }
        }
    }
}

@Composable
private fun WasteItemCard(item: DemoWasteItem, green: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable {},
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(green.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 44.sp)
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(item.category, color = Color.Gray, fontSize = 10.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("♻️", fontSize = 11.sp)
                    Text(
                        " ${item.pointPerKg} Poin / Kg",
                        color = green,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardCard(item: DemoRewardItem, green: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(green.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 28.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${item.pointCost} Poin", color = green, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = {},
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = green)
            ) {
                Text("Tukar", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun EmptySearchResult() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔍", fontSize = 64.sp)
        Spacer(Modifier.height(12.dp))
        Text("Pencarian tidak ditemukan", fontWeight = FontWeight.SemiBold)
        Text("Silakan masukkan kata kunci yang lain", color = Color.Gray, fontSize = 13.sp)
    }
}

// Demo data classes
data class DemoWasteItem(val name: String, val category: String, val pointPerKg: Int, val emoji: String)
data class DemoRewardItem(val name: String, val pointCost: Int, val emoji: String)