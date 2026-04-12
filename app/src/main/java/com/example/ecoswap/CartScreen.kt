package com.example.ecoswap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CartDemoItem(
    val id: String,
    val name: String,
    val category: String,
    val pointPerKg: Int,
    val emoji: String,
    var selected: Boolean = false
)

@Composable
fun CartScreen() {
    val green = Color(0xFF1FAA59)

    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartDemoItem("1", "Botol Kaca", "Non-Organik Kaca", 1500, "🍾", false),
                CartDemoItem("2", "Ember Plastik", "Non-Organik Plastik", 1400, "🪣", false),
            )
        )
    }
    var showDeleteDialog by remember { mutableStateOf<CartDemoItem?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val selectedItems = cartItems.filter { it.selected }
    val totalTypes = selectedItems.size

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F6FA))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ─── HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(green, Color(0xFF38D67A))))
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    "Tong Sampah",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            if (cartItems.isEmpty()) {
                // ─── EMPTY STATE
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🔍", fontSize = 64.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Tong sampah masih kosong", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(
                        "Silahkan untuk menambahkan sampah di sini",
                        color = Color.Gray, textAlign = TextAlign.Center, fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                }
            } else {
                // ─── SELECT ALL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedItems.size == cartItems.size,
                            onCheckedChange = { checked ->
                                cartItems = cartItems.map { it.copy(selected = checked) }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = green)
                        )
                        Text("Pilih Semua")
                    }
                    if (selectedItems.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                cartItems = cartItems.filter { !it.selected }
                            }
                        ) {
                            Text("Hapus", color = Color.Red)
                        }
                    }
                }

                // ─── CART LIST
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onCheckedChange = { checked ->
                                cartItems = cartItems.map {
                                    if (it.id == item.id) it.copy(selected = checked) else it
                                }
                            },
                            onDelete = { showDeleteDialog = item },
                            green = green
                        )
                    }
                }

                // ─── BOTTOM BAR
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Sampah", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                "$totalTypes Jenis Sampah",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { if (selectedItems.isNotEmpty()) showSuccessDialog = true },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = green),
                            enabled = selectedItems.isNotEmpty()
                        ) {
                            Text("Setorkan")
                        }
                    }
                }
            }
        }

        // ─── DELETE DIALOG
        showDeleteDialog?.let { item ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                text = { Text("Apakah Anda yakin untuk menghapus?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            cartItems = cartItems.filter { it.id != item.id }
                            showDeleteDialog = null
                        }
                    ) { Text("Ya", color = green) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Tidak")
                    }
                }
            )
        }

        // ─── SUCCESS DIALOG
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("✅ Berhasil!", textAlign = TextAlign.Center) },
                text = {
                    Text(
                        "Sampah berhasil disetorkan ke mitra!\nEcoPoin akan segera diperbarui.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            cartItems = cartItems.filter { !it.selected }
                            showSuccessDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = green)
                    ) { Text("Oke") }
                }
            )
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartDemoItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    green: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.selected,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = green)
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(green.copy(0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 28.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold)
                Text(item.category, color = Color.Gray, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("♻️", fontSize = 11.sp)
                    Text(
                        " ${item.pointPerKg} Poin / Kg",
                        color = green,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red.copy(0.7f))
                }
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Detail", fontSize = 11.sp)
                }
            }
        }
    }
}