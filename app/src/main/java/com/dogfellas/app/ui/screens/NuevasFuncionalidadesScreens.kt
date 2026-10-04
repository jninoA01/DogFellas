package com.dogfellas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.components.DogFellasHeader

@Composable
private fun NuevaFuncionalidadScreen(
    navController: NavHostController,
    titulo: String,
    icono: String,
    descripcion: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2D1B12),
                        Color(0xFF4E342E),
                        Color(0xFF6D4C41),
                        Color(0xFF8D6E63)
                    )
                )
            )
    ) {

        DogFellasHeader(
            showBackButton = true,
            onHomeClick = {
                navController.navigate(
                    Screen.Home.route
                ) {
                    popUpTo(Screen.Home.route) {
                        inclusive = false
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = icono,
                fontSize = 70.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = titulo,
                color = Color(0xFFF0DDB0),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = descripcion,
                color = Color(0xFFD4A96A),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Esta funcionalidad será desarrollada en la siguiente etapa.",
                color = Color(0xCCFFFFFF),
                fontSize = 14.sp
            )
        }
    }
}



// ═══════════════════════════════════════════════════════════════
// 🏠 CUIDADO TEMPORAL
// ═══════════════════════════════════════════════════════════════

@Composable
fun CuidadoTemporalScreen(
    navController: NavHostController
) {
    NuevaFuncionalidadScreen(
        navController = navController,
        titulo = "Cuidado temporal",
        icono = "🏠",
        descripcion = "Ofrece un espacio temporal para un animal mientras encuentra un hogar definitivo."
    )
}