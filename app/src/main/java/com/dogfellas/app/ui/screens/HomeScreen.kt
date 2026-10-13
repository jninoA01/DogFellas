package com.dogfellas.app.ui.screens

import com.dogfellas.app.navigation.Screen
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dogfellas.app.model.homeMenuItems
import com.dogfellas.app.ui.components.ConstructionModal
import com.dogfellas.app.ui.components.DogFellasFooter
import com.dogfellas.app.ui.components.DogFellasMenuItem

@Composable
fun HomeScreen(
    navController: NavHostController
) {

    var showModal by remember {
        mutableStateOf(false)
    }

    var modalTitle by remember {
        mutableStateOf("")
    }

    var modalIcon by remember {
        mutableStateOf("🚧")
    }

    // ============================================================
    // PALETA DOGFELLAS
    // ============================================================

    val brownDark = Color(0xFF2D1B12)
    val brown = Color(0xFF4E342E)
    val brownMedium = Color(0xFF6D4C41)
    val brownLight = Color(0xFF8D6E63)

    val cream = Color(0xFFF5E6CA)
    val tan = Color(0xFFD7B899)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        brownDark,
                        brown,
                        brownMedium,
                        brownLight
                    )
                )
            )
    ) {

        // ========================================================
        // GLOW SUPERIOR
        // ========================================================

        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(
                    x = (-80).dp,
                    y = (-80).dp
                )
                .blur(120.dp)
                .background(
                    Color(0x55D7B899),
                    CircleShape
                )
        )

        // ========================================================
        // GLOW INFERIOR
        // ========================================================

        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(
                    x = 100.dp,
                    y = 100.dp
                )
                .blur(110.dp)
                .background(
                    Color(0x33D7B899),
                    CircleShape
                )
        )

        // ========================================================
        // DEGRADADO INFERIOR
        // ========================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x331A120D)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ====================================================
            // HEADER
            // ====================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 50.dp,
                        start = 22.dp,
                        end = 22.dp,
                        bottom = 12.dp
                    )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x55D7B899),
                                        Color(0x22D7B899)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Color(0x44FFFFFF),
                                RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.Pets,
                            contentDescription = null,
                            tint = tan,
                            modifier = Modifier.size(31.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column {

                        Text(
                            text = "DOGFELLAS",
                            color = cream,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = "Una red para ayudar",
                            color = tan,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // ====================================================
            // HERO
            // ====================================================

            Column(
                modifier = Modifier.padding(
                    horizontal = 22.dp,
                    vertical = 10.dp
                )
            ) {

                Text(
                    text = "Ayuda a un animal",
                    color = cream,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "que necesita una segunda oportunidad.",
                    color = tan,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // Línea decorativa

                Box(
                    modifier = Modifier
                        .width(65.dp)
                        .height(3.dp)
                        .clip(
                            RoundedCornerShape(100.dp)
                        )
                        .background(cream)
                )
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            // ====================================================
            // MENÚ
            // ====================================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 18.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                homeMenuItems.forEach { item ->

                    DogFellasMenuItem(
                        icon = item.icon,
                        iconBackground = item.iconBg,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = {

                            android.util.Log.d(
                                "DogFellasNav",
                                "CLICK -> title=${item.title}, destination=${item.destination}"
                            )

                            if (item.destination != null) {

                                navController.navigate(
                                    item.destination
                                )

                            } else {

                                modalTitle = item.title
                                modalIcon = item.modalIcon
                                showModal = true
                            }
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            // ====================================================
            // FOOTER
            // ====================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 5.dp,
                        bottom = 18.dp
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "DogFellas • Beta 2026",
                    color = Color(0x99FFFFFF),
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }

    // ============================================================
    // MODAL
    // ============================================================

    if (showModal) {

        ConstructionModal(
            featureName = modalTitle,
            featureIcon = modalIcon,
            onDismiss = {
                showModal = false
            }
        )
    }
}
