package com.dogfellas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.dogfellas.app.model.MenuItem
import com.dogfellas.app.model.atencionMedicaItems
import com.dogfellas.app.model.atencionMedicaMascotasItems
import com.dogfellas.app.model.encontreMascotaItems
import com.dogfellas.app.model.perdiMascotaItems
import com.dogfellas.app.model.voluntariadoItems
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.components.ConstructionModal
import com.dogfellas.app.ui.components.DogFellasFooter
import com.dogfellas.app.ui.components.DogFellasHeader
import com.dogfellas.app.ui.components.DogFellasMenuItem

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val BrownLight = Color(0xFFD7B899)

private val Cream = Color(0xFFF5E6CA)
private val Tan = Color(0xFFD7B899)

@Composable
private fun SubMenuScreen(
    navController: NavHostController,
    title: String,
    bannerIcon: String,
    bannerText: String,
    items: List<MenuItem>
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BrownDark,
                        Brown,
                        BrownMedium,
                        Color(0xFF8D6E63)
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
                .size(250.dp)
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

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ====================================================
            // HEADER
            // ====================================================

            DogFellasHeader(
                showBackButton = true,
                onHomeClick = {

                    navController.navigate(
                        Screen.Home.route
                    ) {
                        popUpTo(
                            Screen.Home.route
                        ) {
                            inclusive = false
                        }
                    }
                }
            )

            // ====================================================
            // CONTENIDO
            // ====================================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // =================================================
                // HERO
                // =================================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(32.dp)
                        )
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x33FFFFFF),
                                    Color(0x11FFFFFF)
                                )
                            )
                        )
                        .border(
                            1.5.dp,
                            Color(0x44FFFFFF),
                            RoundedCornerShape(32.dp)
                        )
                        .padding(22.dp)
                ) {

                    Column {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            // =============================
                            // ICONO
                            // =============================

                            Box(
                                modifier = Modifier
                                    .size(74.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                BrownLight,
                                                BrownMedium
                                            )
                                        )
                                    )
                                    .border(
                                        2.dp,
                                        Color(0x33FFFFFF),
                                        CircleShape
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text = bannerIcon,
                                    fontSize = 35.sp
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(17.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = title,
                                    color = Cream,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = bannerText,
                                    color = Tan,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        // =========================================
                        // DIVISOR
                        // =========================================

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    Color(0x22FFFFFF)
                                )
                        )

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                                    .background(
                                        Color(0x22F0DDB0)
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Rounded.Pets,
                                    contentDescription = null,
                                    tint = BrownLight,
                                    modifier =
                                        Modifier.size(18.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(9.dp)
                            )

                            Text(
                                text =
                                    "${items.size} opciones disponibles",
                                color = Cream,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // =================================================
                // OPCIONES
                // =================================================

                items.forEach { item ->

                    DogFellasMenuItem(
                        icon = item.icon,
                        iconBackground = item.iconBg,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = {

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
                    modifier = Modifier.height(15.dp)
                )
            }

            // ====================================================
            // FOOTER
            // ====================================================

            DogFellasFooter()
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


// ================================================================
// PANTALLAS
// ================================================================

@Composable
fun AtencionMedicaScreen(
    navController: NavHostController
) =
    SubMenuScreen(
        navController = navController,
        title = "Atención Médica",
        bannerIcon = "🏥",
        bannerText = "Cuidamos la salud de tu mascota",
        items = atencionMedicaItems
    )


@Composable
fun AtencionMedicaMascotasScreen(
    navController: NavHostController
) =
    SubMenuScreen(
        navController = navController,
        title = "Atención Veterinaria",
        bannerIcon = "🩺",
        bannerText = "Servicios médicos y veterinarios",
        items = atencionMedicaMascotasItems
    )


@Composable
fun PerdiMascotaScreen(
    navController: NavHostController
) =
    SubMenuScreen(
        navController = navController,
        title = "Perdí a mi mascota",
        bannerIcon = "🔍",
        bannerText = "Encuentra a tu mejor amigo",
        items = perdiMascotaItems
    )


@Composable
fun EncontreMascotaScreen(
    navController: NavHostController
) =
    SubMenuScreen(
        navController = navController,
        title = "Encontré una mascota",
        bannerIcon = "❤️",
        bannerText = "Ayuda a reunir familias",
        items = encontreMascotaItems
    )


@Composable
fun VoluntariadoScreen(
    navController: NavHostController
) =
    SubMenuScreen(
        navController = navController,
        title = "Voluntariado",
        bannerIcon = "🤝",
        bannerText = "Únete a la manada DogFellas",
        items = voluntariadoItems
    )