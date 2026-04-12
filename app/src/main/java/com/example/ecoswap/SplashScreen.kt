package com.example.ecoswap

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(navController: NavController) {

    var startAnimation by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }

    // Animasi scale logo
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1200)
        showText = true
        delay(1800)

        // Pindah ke role screen
        navController.navigate("role") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFCF8F8), Color(0xFFFCF8F8))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.ecoswap), // nanti kamu ganti
                contentDescription = null,
                modifier = Modifier
                    .size(350.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = scale
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showText,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(1000)
                        )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tukar sampahmu hari ini.\nDapatkan poin, tukar jadi barang.",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}