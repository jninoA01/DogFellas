package com.dogfellas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dogfellas.app.model.AnimalReportado
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.viewmodel.AdopcionesViewModel

// ============================================================
// COLORES DOGFELLAS
// ============================================================

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val BrownLight = Color(0xFF8D6E63)
private val Cream = Color(0xFFF0DDB0)
private val Beige = Color(0xFFD4A96A)
private val WhiteCream = Color(0xFFF8F3EE)

// ============================================================
// PANTALLA DE ADOPCIONES
// ============================================================

@Composable
fun AdopcionesScreen(
    navController: NavHostController,
    viewModel: AdopcionesViewModel = viewModel()
) {

    val animales by viewModel.animales.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    // ========================================================
    // CARGAR ANIMALES
    // ========================================================

    LaunchedEffect(Unit) {
        viewModel.cargarAnimales()
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
                        BrownLight
                    )
                )
            )
    ) {

        Scaffold(
            containerColor = Color.Transparent,

            topBar = {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 8.dp,
                            vertical = 8.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // =================================================
                    // VOLVER
                    // =================================================

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Volver",

                            tint =
                                Cream
                        )
                    }

                    // =================================================
                    // TÍTULO
                    // =================================================

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "Animales en adopción",

                            color =
                                Cream,

                            fontSize =
                                21.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Encuentra un nuevo compañero",

                            color =
                                Cream.copy(
                                    alpha = 0.72f
                                ),

                            fontSize =
                                11.sp
                        )
                    }

                    // =================================================
                    // ACTUALIZAR
                    // =================================================

                    Card(
                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Brown.copy(
                                        alpha = 0.75f
                                    )
                            )
                    ) {

                        IconButton(
                            onClick = {
                                viewModel.cargarAnimales()
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Refresh,

                                contentDescription =
                                    "Actualizar",

                                tint =
                                    Cream
                            )
                        }
                    }
                }
            }

        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        horizontal = 16.dp
                    )
            ) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                // =================================================
                // TARJETA DE PRESENTACIÓN
                // =================================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(24.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Cream
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation =
                                5.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        // =============================================
                        // ICONO
                        // =============================================

                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(
                                    RoundedCornerShape(
                                        18.dp
                                    )
                                )
                                .background(
                                    Beige.copy(
                                        alpha = 0.5f
                                    )
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Favorite,

                                contentDescription =
                                    null,

                                tint =
                                    BrownDark,

                                modifier =
                                    Modifier.size(31.dp)
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.width(13.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Una familia los está esperando",

                                color =
                                    BrownDark,

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "Estos animales están listos para encontrar un hogar responsable y lleno de cariño.",

                                color =
                                    Brown,

                                fontSize =
                                    12.sp,

                                lineHeight =
                                    18.sp
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                // =================================================
                // CONTADOR
                // =================================================

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "Disponibles para adopción",

                            color =
                                Cream,

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Conoce cada historia y encuentra tu compañero.",

                            color =
                                Cream.copy(
                                    alpha = 0.72f
                                ),

                            fontSize =
                                11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    14.dp
                                )
                            )
                            .background(
                                Beige
                            )
                            .padding(
                                horizontal = 13.dp,
                                vertical = 8.dp
                            )
                    ) {

                        Text(
                            text =
                                "${animales.size}",

                            color =
                                BrownDark,

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                // =================================================
                // CONTENIDO
                // =================================================

                when {

                    // =================================================
                    // CARGANDO
                    // =================================================

                    cargando -> {

                        Box(
                            modifier =
                                Modifier.fillMaxSize(),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Card(
                                shape =
                                    RoundedCornerShape(
                                        22.dp
                                    ),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            BrownDark.copy(
                                                alpha =
                                                    0.55f
                                            )
                                    )
                            ) {

                                Column(
                                    modifier =
                                        Modifier.padding(
                                            28.dp
                                        ),

                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    CircularProgressIndicator(
                                        color =
                                            Cream
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                14.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            "Buscando animales...",

                                        color =
                                            Cream,

                                        fontSize =
                                            14.sp,

                                        fontWeight =
                                            FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // =================================================
                    // ERROR
                    // =================================================

                    mensaje != null -> {

                        Box(
                            modifier =
                                Modifier.fillMaxSize(),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Card(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(
                                        22.dp
                                    ),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            Cream
                                    )
                            ) {

                                Column(
                                    modifier =
                                        Modifier.padding(
                                            24.dp
                                        ),

                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Pets,

                                        contentDescription =
                                            null,

                                        tint =
                                            BrownDark,

                                        modifier =
                                            Modifier.size(
                                                50.dp
                                            )
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                12.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            "No pudimos cargar los animales",

                                        color =
                                            BrownDark,

                                        fontSize =
                                            17.sp,

                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                7.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            mensaje ?: "",

                                        color =
                                            Brown,

                                        fontSize =
                                            13.sp
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                16.dp
                                            )
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.cargarAnimales()
                                        },

                                        shape =
                                            RoundedCornerShape(
                                                15.dp
                                            ),

                                        colors =
                                            ButtonDefaults.buttonColors(
                                                containerColor =
                                                    BrownDark,

                                                contentColor =
                                                    Cream
                                            )
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Default.Refresh,

                                            contentDescription =
                                                null
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.width(7.dp)
                                        )

                                        Text(
                                            text =
                                                "Intentar nuevamente",

                                            fontWeight =
                                                FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // =================================================
                    // SIN ANIMALES
                    // =================================================

                    animales.isEmpty() -> {

                        Box(
                            modifier =
                                Modifier.fillMaxSize(),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Card(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(
                                        22.dp
                                    ),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            Cream
                                    )
                            ) {

                                Column(
                                    modifier =
                                        Modifier.padding(
                                            28.dp
                                        ),

                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    22.dp
                                                )
                                            )
                                            .background(
                                                Beige.copy(
                                                    alpha =
                                                        0.4f
                                                )
                                            ),

                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Default.Pets,

                                            contentDescription =
                                                null,

                                            tint =
                                                BrownDark,

                                            modifier =
                                                Modifier.size(
                                                    40.dp
                                                )
                                        )
                                    }

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                15.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            "No hay animales en adopción",

                                        color =
                                            BrownDark,

                                        fontSize =
                                            18.sp,

                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                7.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            "Cuando un animal esté listo para ser adoptado aparecerá aquí.",

                                        color =
                                            Brown,

                                        fontSize =
                                            13.sp,

                                        lineHeight =
                                            19.sp
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                17.dp
                                            )
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.cargarAnimales()
                                        },

                                        shape =
                                            RoundedCornerShape(
                                                15.dp
                                            ),

                                        colors =
                                            ButtonDefaults.buttonColors(
                                                containerColor =
                                                    BrownDark,

                                                contentColor =
                                                    Cream
                                            )
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Default.Refresh,

                                            contentDescription =
                                                null
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.width(
                                                    7.dp
                                                )
                                        )

                                        Text(
                                            text =
                                                "Actualizar",

                                            fontWeight =
                                                FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // =================================================
                    // LISTA
                    // =================================================

                    else -> {

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize(),

                            verticalArrangement =
                                Arrangement.spacedBy(
                                    14.dp
                                )
                        ) {

                            items(
                                items =
                                    animales,

                                key = {
                                    it.id
                                }
                            ) { animal ->

                                AnimalAdopcionCard(

                                    animal =
                                        animal,

                                    onVerDetalle = {

                                        if (
                                            animal.id.isNotBlank()
                                        ) {

                                            navController.navigate(
                                                Screen.DetalleAdopcion
                                                    .createRoute(
                                                        animal.id
                                                    )
                                            )
                                        }
                                    }
                                )
                            }

                            item {

                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            20.dp
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// TARJETA DEL ANIMAL
// ============================================================

@Composable
private fun AnimalAdopcionCard(
    animal: AnimalReportado,
    onVerDetalle: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    WhiteCream
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    5.dp
            )
    ) {

        Column {

            // =================================================
            // FOTO
            // =================================================

            Box(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                if (
                    animal.fotoUri.isNotBlank()
                ) {

                    AsyncImage(

                        model =
                            animal.fotoUri,

                        contentDescription =
                            "Foto del animal",

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 24.dp,
                                    topEnd = 24.dp
                                )
                            ),

                        contentScale =
                            ContentScale.Crop
                    )

                } else {

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(
                                Beige.copy(
                                    alpha = 0.35f
                                )
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Pets,

                            contentDescription =
                                null,

                            tint =
                                BrownMedium,

                            modifier =
                                Modifier.size(
                                    75.dp
                                )
                        )
                    }
                }

                // =================================================
                // ETIQUETA
                // =================================================

                Box(
                    modifier = Modifier
                        .padding(
                            14.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                20.dp
                            )
                        )
                        .background(
                            BrownDark.copy(
                                alpha = 0.88f
                            )
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                        .align(
                            Alignment.TopEnd
                        )
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Favorite,

                            contentDescription =
                                null,

                            tint =
                                Cream,

                            modifier =
                                Modifier.size(
                                    14.dp
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.width(
                                    5.dp
                                )
                        )

                        Text(
                            text =
                                "EN ADOPCIÓN",

                            color =
                                Cream,

                            fontSize =
                                10.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }

            // =================================================
            // INFORMACIÓN
            // =================================================

            Column(
                modifier =
                    Modifier.padding(
                        17.dp
                    )
            ) {

                // =================================================
                // ESPECIE + CONDICIÓN
                // =================================================

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                animal.especie
                                    .ifBlank {
                                        "Animal"
                                    },

                            color =
                                BrownDark,

                            fontSize =
                                21.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    4.dp
                                )
                        )

                        Text(
                            text =
                                animal.condicion
                                    .ifBlank {
                                        "Condición no especificada"
                                    },

                            color =
                                BrownMedium,

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    14.dp
                                )
                            )
                            .background(
                                Beige.copy(
                                    alpha =
                                        0.4f
                                )
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            )
                    ) {

                        Text(
                            text =
                                "Adopción",

                            color =
                                BrownDark,

                            fontSize =
                                11.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                // =================================================
                // DESCRIPCIÓN
                // =================================================

                if (
                    animal.descripcion.isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                13.dp
                            )
                    )

                    Text(
                        text =
                            animal.descripcion,

                        color =
                            Brown,

                        fontSize =
                            14.sp,

                        lineHeight =
                            20.sp,

                        maxLines =
                            4
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )

                // =================================================
                // CTA
                // =================================================

                Button(

                    onClick =
                        onVerDetalle,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            49.dp
                        ),

                    shape =
                        RoundedCornerShape(
                            15.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                BrownDark,

                            contentColor =
                                Cream
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.VolunteerActivism,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                20.dp
                            )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )

                    Text(
                        text =
                            "Conocer y adoptar",

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            14.sp
                    )
                }
            }
        }
    }
}