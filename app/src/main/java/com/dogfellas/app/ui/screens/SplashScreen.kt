package com.dogfellas.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfellas.app.ui.theme.*

@Composable
fun SplashScreen(onStart: () -> Unit) {

    // ✨ Animación del logo
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D1B12),
                        Color(0xFF4E342E),
                        Color(0xFF6D4C41),
                        Color(0xFF8D6E63)
                    )
                )
            )
    ) {

        // ✨ Glow superior
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .blur(120.dp)
                .background(
                    Color(0x55D7B899),
                    CircleShape
                )
        )

        // 🌆 Fondo decorativo inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0xFF1A120D)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 🐕 Card principal del logo
            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(240.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x33FFFFFF),
                                Color(0x11FFFFFF)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        Color(0x55FFFFFF),
                        RoundedCornerShape(36.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFFD7B899),
                                        Color(0xFF8B5A30)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Pets,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(58.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "DOGFELLAS",
                        color = Cream,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Encuentra clínicas,\nmascotas y tu manada.",
                        color = Tan,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(42.dp))

            // 🏷️ Nombre App
            Text(
                text = "DogFellas",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 52.sp,
                color = Cream,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "LOCALIZA A TU MANADA",
                fontSize = 13.sp,
                color = Color(0xFFD7B899),
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            // 🚀 Botón premium
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD7B899),
                    contentColor = Color(0xFF2D1B12)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .border(
                        2.dp,
                        Color(0xFFFFE0B2),
                        RoundedCornerShape(100.dp)
                    )
            ) {

                Text(
                    text = "COMENZAR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Versión Beta • Bogotá, Colombia",
                color = Color(0x99FFFFFF),
                fontSize = 12.sp
            )
        }
    }
}