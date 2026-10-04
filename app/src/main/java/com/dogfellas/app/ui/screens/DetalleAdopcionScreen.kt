package com.dogfellas.app.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.dogfellas.app.model.AnimalReportado
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.viewmodel.SolicitudAdopcionViewModel
import kotlinx.coroutines.tasks.await


// =============================================================
// COLORES DOGFELLAS
// =============================================================

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val BrownLight = Color(0xFF8D6E63)
private val Cream = Color(0xFFF0DDB0)
private val Beige = Color(0xFFD4A96A)
private val WhiteCream = Color(0xFFF8F3EE)

// Verde utilizado únicamente para indicar estados completados
private val SuccessGreen = Color(0xFF4E7A52)


// =============================================================
// PANTALLA PRINCIPAL
// =============================================================

@Composable
fun DetalleAdopcionScreen(
    navController: NavHostController,
    reporteId: String,
    solicitudViewModel: SolicitudAdopcionViewModel = viewModel()
) {

    var animal by remember {
        mutableStateOf<AnimalReportado?>(null)
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    val solicitudExistente by
    solicitudViewModel.solicitudExistente.collectAsState()

    val consultandoSolicitud by
    solicitudViewModel.consultandoSolicitud.collectAsState()


    Log.d(
        "DetalleAdopcion",
        "reporteId recibido: [$reporteId]"
    )


    // =========================================================
    // CARGAR ANIMAL
    // =========================================================

    LaunchedEffect(reporteId) {

        cargando = true
        error = null

        try {

            if (reporteId.isBlank()) {

                error = "No se recibió el identificador del animal."
                cargando = false

                return@LaunchedEffect
            }


            val documento =
                FirebaseFirestore
                    .getInstance()
                    .collection("animales_reportados")
                    .document(reporteId)
                    .get()
                    .await()


            if (documento.exists()) {

                animal =
                    documento
                        .toObject(AnimalReportado::class.java)
                        ?.copy(
                            id = documento.id
                        )


                // =================================================
                // COMPROBAR SI EL USUARIO YA TIENE SOLICITUD
                // =================================================

                solicitudViewModel
                    .cargarSolicitudExistente(reporteId)

            } else {

                error = "No se encontró el animal."
            }

        } catch (e: Exception) {

            Log.e(
                "DetalleAdopcion",
                "Error cargando animal",
                e
            )

            error =
                e.message
                    ?: "No fue posible cargar la información."

        } finally {

            cargando = false
        }
    }


    // =========================================================
    // CONTENEDOR GENERAL
    // =========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BrownDark,
                        Brown,
                        BrownMedium
                    )
                )
            )
    ) {

        Scaffold(
            containerColor = Color.Transparent
        ) { paddingValues ->

            when {

                // =================================================
                // LOADING
                // =================================================

                cargando -> {

                    LoadingDetalleAdopcion(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }


                // =================================================
                // ERROR
                // =================================================

                error != null -> {

                    ErrorDetalleAdopcion(
                        mensaje = error
                            ?: "Error desconocido",

                        onBack = {
                            navController.popBackStack()
                        },

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }


                // =================================================
                // CONTENIDO
                // =================================================

                animal != null -> {

                    DetalleContenido(
                        animal = animal!!,

                        solicitudExistente =
                            solicitudExistente,

                        consultandoSolicitud =
                            consultandoSolicitud,

                        onBack = {
                            navController.popBackStack()
                        },

                        onSolicitarAdopcion = {

                            navController.navigate(
                                Screen.SolicitudAdopcion
                                    .createRoute(
                                        animal!!.id
                                    )
                            )
                        },

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}


// =============================================================
// CONTENIDO PRINCIPAL
// =============================================================

@Composable
private fun DetalleContenido(
    animal: AnimalReportado,
    solicitudExistente:
    com.dogfellas.app.model.SolicitudAdopcion?,
    consultandoSolicitud: Boolean,
    onBack: () -> Unit,
    onSolicitarAdopcion: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .navigationBarsPadding()
        ) {


            // =====================================================
            // FOTO HERO
            // =====================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            ) {

                // -------------------------------------------------
                // FOTO
                // -------------------------------------------------

                if (animal.fotoUri.isNotBlank()) {

                    AsyncImage(
                        model = animal.fotoUri,

                        contentDescription =
                            "Foto de ${animal.especie}",

                        modifier = Modifier.fillMaxSize(),

                        contentScale =
                            ContentScale.Crop
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        BrownLight,
                                        Brown
                                    )
                                )
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Pets,

                            contentDescription =
                                "Sin fotografía",

                            tint = Cream,

                            modifier =
                                Modifier.size(90.dp)
                        )
                    }
                }


                // -------------------------------------------------
                // DEGRADADO
                // -------------------------------------------------

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    BrownDark.copy(
                                        alpha = 0.85f
                                    )
                                )
                            )
                        )
                )


                // -------------------------------------------------
                // BOTÓN VOLVER
                // -------------------------------------------------

                Surface(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(
                            start = 16.dp,
                            top = 8.dp
                        ),

                    shape = CircleShape,

                    color =
                        BrownDark.copy(
                            alpha = 0.75f
                        )
                ) {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Volver",

                            tint = Cream
                        )
                    }
                }


                // -------------------------------------------------
                // ESTADO EN ADOPCIÓN
                // -------------------------------------------------

                Surface(
                    modifier = Modifier
                        .align(
                            Alignment.BottomStart
                        )
                        .padding(
                            start = 20.dp,
                            bottom = 20.dp
                        ),

                    shape =
                        RoundedCornerShape(50.dp),

                    color = Beige
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Favorite,

                            contentDescription = null,

                            tint = BrownDark,

                            modifier =
                                Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text = "EN ADOPCIÓN",

                            color = BrownDark,

                            fontSize = 12.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }


            // =====================================================
            // CONTENIDO
            // =====================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        WhiteCream,
                        RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp
                        )
                    )
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 26.dp,
                        bottom = 130.dp
                    )
            ) {


                // =================================================
                // ESPECIE
                // =================================================

                Text(
                    text =
                        animal.especie.ifBlank {
                            "Animal"
                        },

                    color = BrownDark,

                    fontSize = 30.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(
                    text =
                        "Busca una familia donde pueda sentirse seguro y querido.",

                    color = BrownMedium,

                    fontSize = 14.sp,

                    lineHeight = 20.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )


                // =================================================
                // INFORMACIÓN RÁPIDA
                // =================================================

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    InfoCard(
                        modifier =
                            Modifier.weight(1f),

                        icon =
                            Icons.Default.Pets,

                        title =
                            "Especie",

                        value =
                            animal.especie.ifBlank {
                                "No indicada"
                            }
                    )


                    InfoCard(
                        modifier =
                            Modifier.weight(1f),

                        icon =
                            Icons.Default.Description,

                        title =
                            "Condición",

                        value =
                            animal.condicion.ifBlank {
                                "No indicada"
                            }
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )


                // =================================================
                // PROCESO
                // =================================================

                SectionTitle(
                    icon =
                        Icons.Default.Pets,

                    title =
                        "Proceso del animal"
                )


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                LifecycleCard()


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )


                // =================================================
                // DESCRIPCIÓN
                // =================================================

                SectionTitle(
                    icon =
                        Icons.Default.Description,

                    title =
                        "Sobre este animal"
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .animateContentSize(),

                    shape =
                        RoundedCornerShape(20.dp),

                    color = Color.White
                ) {

                    Text(
                        text =
                            animal.descripcion.ifBlank {
                                "No hay una descripción disponible para este animal."
                            },

                        modifier =
                            Modifier.padding(18.dp),

                        color = Brown,

                        fontSize = 15.sp,

                        lineHeight = 23.sp
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )


                // =================================================
                // MENSAJE
                // =================================================

                AdoptionMessageCard()


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )


                // =================================================
                // FECHA
                // =================================================

                if (animal.fechaReporte > 0L) {

                    InfoRow(
                        icon =
                            Icons.Default.CalendarToday,

                        title =
                            "Animal registrado",

                        value =
                            "Reporte disponible en DogFellas"
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(30.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(50.dp)
                )
            }
        }


        // =========================================================
        // CTA FIJO
        // =========================================================

        Surface(
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .fillMaxWidth(),

            color = WhiteCream,

            shadowElevation = 12.dp
        ) {

            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    )
            ) {

                AnimatedContent(
                    targetState =
                        consultandoSolicitud,

                    label =
                        "estadoSolicitud"
                ) { consultando ->


                    // =============================================
                    // CONSULTANDO SOLICITUD
                    // =============================================

                    if (consultando) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(24.dp),

                                color = Brown
                            )
                        }


                        // =============================================
                        // YA EXISTE SOLICITUD
                        // =============================================

                    } else if (
                        solicitudExistente != null
                    ) {

                        SolicitudPresentadaCard()


                        // =============================================
                        // NO EXISTE SOLICITUD
                        // =============================================

                    } else {

                        Button(
                            onClick =
                                onSolicitarAdopcion,

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),

                            shape =
                                RoundedCornerShape(18.dp),

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

                                contentDescription = null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(10.dp)
                            )

                            Text(
                                text =
                                    "Quiero adoptar",

                                fontSize =
                                    16.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


// =============================================================
// INFORMACIÓN RÁPIDA
// =============================================================

@Composable
private fun InfoCard(
    modifier: Modifier,
    icon:
    androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {

    Surface(
        modifier = modifier,

        shape =
            RoundedCornerShape(18.dp),

        color = Color.White
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Icon(
                imageVector = icon,

                contentDescription = null,

                tint = BrownMedium,

                modifier =
                    Modifier.size(22.dp)
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Text(
                text = title,

                color = BrownLight,

                fontSize = 12.sp
            )


            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )


            Text(
                text = value,

                color = BrownDark,

                fontSize = 14.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


// =============================================================
// TÍTULO DE SECCIÓN
// =============================================================

@Composable
private fun SectionTitle(
    icon:
    androidx.compose.ui.graphics.vector.ImageVector,

    title: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    Beige.copy(
                        alpha = 0.35f
                    )
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector = icon,

                contentDescription = null,

                tint = BrownDark,

                modifier =
                    Modifier.size(19.dp)
            )
        }


        Spacer(
            modifier =
                Modifier.width(10.dp)
        )


        Text(
            text = title,

            color = BrownDark,

            fontSize = 19.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}


// =============================================================
// CICLO DE VIDA
// =============================================================

@Composable
private fun LifecycleCard() {

    val estados =
        listOf(
            "Reportado",
            "En atención",
            "Rescatado",
            "En recuperación",
            "En adopción"
        )


    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        color = Color.White
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            estados.forEachIndexed {
                    index,
                    estado ->

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (
                                        estado ==
                                        "En adopción"
                                    ) {

                                        Beige

                                    } else {

                                        SuccessGreen
                                            .copy(
                                                alpha =
                                                    0.85f
                                            )
                                    }
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            if (
                                estado ==
                                "En adopción"
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Favorite,

                                    contentDescription =
                                        null,

                                    tint =
                                        BrownDark,

                                    modifier =
                                        Modifier.size(15.dp)
                                )

                            } else {

                                Icon(
                                    imageVector =
                                        Icons.Default.Check,

                                    contentDescription =
                                        null,

                                    tint =
                                        Color.White,

                                    modifier =
                                        Modifier.size(17.dp)
                                )
                            }
                        }


                        if (
                            index <
                            estados.lastIndex
                        ) {

                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(25.dp)
                                    .background(
                                        SuccessGreen
                                            .copy(
                                                alpha =
                                                    0.35f
                                            )
                                    )
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.width(14.dp)
                    )


                    Text(
                        text = estado,

                        color =
                            if (
                                estado ==
                                "En adopción"
                            ) {

                                BrownDark

                            } else {

                                BrownMedium
                            },

                        fontSize =
                            14.sp,

                        fontWeight =
                            if (
                                estado ==
                                "En adopción"
                            ) {

                                FontWeight.Bold

                            } else {

                                FontWeight.Normal
                            }
                    )
                }
            }
        }
    }
}


// =============================================================
// MENSAJE DE ADOPCIÓN
// =============================================================

@Composable
private fun AdoptionMessageCard() {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        color =
            Beige.copy(
                alpha = 0.28f
            )
    ) {

        Row(
            modifier =
                Modifier.padding(18.dp),

            verticalAlignment =
                Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Beige),

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
                        Modifier.size(22.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )


            Column {

                Text(
                    text =
                        "Una adopción cambia una vida",

                    color = BrownDark,

                    fontSize = 16.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(
                    text =
                        "Al adoptar estás brindando un hogar, seguridad y una nueva oportunidad.",

                    color = Brown,

                    fontSize = 13.sp,

                    lineHeight = 19.sp
                )
            }
        }
    }
}


// =============================================================
// INFORMACIÓN
// =============================================================

@Composable
private fun InfoRow(
    icon:
    androidx.compose.ui.graphics.vector.ImageVector,

    title: String,

    value: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,

            contentDescription = null,

            tint = BrownMedium,

            modifier =
                Modifier.size(20.dp)
        )


        Spacer(
            modifier =
                Modifier.width(10.dp)
        )


        Column {

            Text(
                text = title,

                color = BrownLight,

                fontSize = 12.sp
            )


            Text(
                text = value,

                color = Brown,

                fontSize = 13.sp
            )
        }
    }
}


// =============================================================
// SOLICITUD YA PRESENTADA
// =============================================================

@Composable
private fun SolicitudPresentadaCard() {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(20.dp),

        color =
            Beige.copy(
                alpha = 0.20f
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            // =================================================
            // ENCABEZADO
            // =================================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Beige),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Check,

                        contentDescription =
                            null,

                        tint = BrownDark,

                        modifier =
                            Modifier.size(22.dp)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Solicitud presentada",

                        color = BrownDark,

                        fontSize = 16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    Text(
                        text =
                            "Tu solicitud para este animal ya fue registrada.",

                        color = BrownMedium,

                        fontSize = 12.sp,

                        lineHeight = 17.sp
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // =================================================
            // ESTADO DE LA SOLICITUD
            // =================================================

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(12.dp),

                color = Color.White
            ) {

                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Description,

                        contentDescription =
                            null,

                        tint = BrownMedium,

                        modifier =
                            Modifier.size(18.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )


                    Column {

                        Text(
                            text =
                                "Estado de la solicitud",

                            color = BrownLight,

                            fontSize = 11.sp
                        )


                        Text(
                            text =
                                "Pendiente de revisión",

                            color = BrownDark,

                            fontSize = 13.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// =============================================================
// LOADING
// =============================================================

@Composable
private fun LoadingDetalleAdopcion(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier,

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                color = Beige
            )


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            Text(
                text =
                    "Cargando información...",

                color = Cream,

                fontSize = 14.sp
            )
        }
    }
}


// =============================================================
// ERROR
// =============================================================

@Composable
private fun ErrorDetalleAdopcion(
    mensaje: String,

    onBack: () -> Unit,

    modifier: Modifier = Modifier
) {

    Box(
        modifier =
            modifier.padding(24.dp),

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.Pets,

                contentDescription =
                    null,

                tint = Cream,

                modifier =
                    Modifier.size(70.dp)
            )


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            Text(
                text = mensaje,

                color = Cream,

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.Bold,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            Button(
                onClick = onBack,

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Beige,
                        contentColor = BrownDark
                    )
            ) {

                Text(
                    text = "Volver"
                )
            }
        }
    }
}