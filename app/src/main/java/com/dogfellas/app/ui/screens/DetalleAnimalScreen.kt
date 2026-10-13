package com.dogfellas.app.ui.screens

import repository.AnimalReportadoRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dogfellas.app.model.AnimalReportado
import com.dogfellas.app.model.ComentarioAnimal
import com.dogfellas.app.model.HistorialEstadoAnimal
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.viewmodel.ComentariosAnimalViewModel
import com.dogfellas.app.ui.viewmodel.HistorialAnimalViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val BrownLight = Color(0xFF8D6E63)
private val Cream = Color(0xFFF0DDB0)
private val Beige = Color(0xFFD4A96A)

private val estadosAnimal = listOf(
    "REPORTADO",
    "EN_ATENCION",
    "RESCATADO",
    "EN_RECUPERACION",
    "EN_ADOPCION",
    "ADOPTADO"
)

@Composable
fun DetalleAnimalScreen(
    navController: NavHostController,
    reporteId: String
) {

    val animalRepository = remember {
        AnimalReportadoRepository()
    }

    var animal by remember {
        mutableStateOf<AnimalReportado?>(null)
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    // ============================================================
    // VIEWMODEL DE COMENTARIOS
    // ============================================================

    val comentariosViewModel =
        remember {
            ComentariosAnimalViewModel()
        }

    val comentarios by comentariosViewModel
        .comentarios
        .collectAsState()

    val cargandoComentarios by comentariosViewModel
        .cargando
        .collectAsState()

    val guardandoComentario by comentariosViewModel
        .guardando
        .collectAsState()

    val mensajeComentario by comentariosViewModel
        .mensaje
        .collectAsState()


    // ============================================================
    // VIEWMODEL DEL HISTORIAL
    // ============================================================

    val historialViewModel =
        remember {
            HistorialAnimalViewModel()
        }

    val historial by historialViewModel
        .historial
        .collectAsState()

    val cargandoHistorial by historialViewModel
        .cargando
        .collectAsState()

    val actualizandoEstado by historialViewModel
        .actualizando
        .collectAsState()

    val mensajeHistorial by historialViewModel
        .mensaje
        .collectAsState()

    val estadoActualizado by historialViewModel
        .actualizado
        .collectAsState()


    // ============================================================
    // CARGAR ANIMAL
    // ============================================================

  LaunchedEffect(reporteId) {
    cargando = true
    error = null

    animalRepository.obtenerAnimalPorId(reporteId)
        .onSuccess { resultado ->
            animal = resultado
        }
        .onFailure { excepcion ->
            error = excepcion.message
                ?: "No fue posible cargar el reporte."
        }

    cargando = false
}

    // ============================================================
    // CARGAR COMENTARIOS
    // ============================================================

    LaunchedEffect(reporteId) {

        comentariosViewModel
            .cargarComentarios(reporteId)
    }


    // ============================================================
    // INICIALIZAR HISTORIAL
    // ============================================================

    LaunchedEffect(animal?.id) {

        val animalActual = animal

        if (animalActual != null) {

            historialViewModel.inicializarHistorial(
                reporteId = animalActual.id,
                estadoActual = animalActual.estado,
                usuarioReporta = animalActual.usuarioReporta,
                fechaReporte = animalActual.fechaReporte
            )
        }
    }


    // ============================================================
    // CUANDO CAMBIA EL ESTADO
    // RECARGAMOS EL ANIMAL
    // ============================================================

    LaunchedEffect(estadoActualizado) {
    if (estadoActualizado) {
        animalRepository.obtenerAnimalPorId(reporteId)
            .onSuccess { resultado ->
                animal = resultado
                error = null
            }
            .onFailure { excepcion ->
                error = excepcion.message
                    ?: "No fue posible actualizar la pantalla."
            }

        historialViewModel.limpiarActualizado()
    }
}

    // ============================================================
    // PANTALLA
    // ============================================================

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

        when {

            cargando -> {

                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    ),
                    color = Cream
                )
            }

            error != null -> {

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🐾",
                        fontSize = 50.sp
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = error!!,
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Button(
                        onClick = {
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Cream,
                            contentColor = BrownDark
                        )
                    ) {

                        Text("Volver")
                    }
                }
            }

            animal != null -> {

                DetalleAnimalContenido(
                    animal = animal!!,
                    navController = navController,

                    // HISTORIAL
                    historial = historial,
                    cargandoHistorial = cargandoHistorial,
                    actualizandoEstado = actualizandoEstado,
                    mensajeHistorial = mensajeHistorial,

                    onCambiarEstado = { nuevoEstado, observacion ->

                        historialViewModel.cambiarEstado(
                            reporteId = reporteId,
                            nuevoEstado = nuevoEstado,
                            observacion = observacion
                        )
                    },

                    // COMENTARIOS
                    comentarios = comentarios,
                    cargandoComentarios = cargandoComentarios,
                    guardandoComentario = guardandoComentario,
                    mensajeComentario = mensajeComentario,

                    onAgregarComentario = { comentario ->

                        comentariosViewModel.agregarComentario(
                            reporteId = reporteId,
                            comentario = comentario
                        )
                    },

                    onLimpiarMensaje = {

                        comentariosViewModel
                            .limpiarMensaje()
                    }
                )
            }
        }
    }
}


@Composable
private fun DetalleAnimalContenido(
    animal: AnimalReportado,
    navController: NavHostController,

    // HISTORIAL
    historial: List<HistorialEstadoAnimal>,
    cargandoHistorial: Boolean,
    actualizandoEstado: Boolean,
    mensajeHistorial: String?,
    onCambiarEstado: (String, String) -> Unit,

    // COMENTARIOS
    comentarios: List<ComentarioAnimal>,
    cargandoComentarios: Boolean,
    guardandoComentario: Boolean,
    mensajeComentario: String?,
    onAgregarComentario: (String) -> Unit,
    onLimpiarMensaje: () -> Unit
) {

    var nuevoComentario by remember {
        mutableStateOf("")
    }

    // ============================================================
    // DIALOGO DE CAMBIO DE ESTADO
    // ============================================================

    var mostrarDialogoEstado by remember {
        mutableStateOf(false)
    }

    var observacionEstado by remember {
        mutableStateOf("")
    }

    // ============================================================
    // DIALOGO QUIERO AYUDAR
    // ============================================================

    var mostrarDialogoAyuda by remember {
        mutableStateOf(false)
    }


    // ============================================================
    // USUARIO ACTUAL
    // ============================================================

    val usuarioActual =
        FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid

    /*
     * Según las reglas actuales de Firebase solamente el usuario
     * que creó el reporte puede modificar su estado.
     */

    val puedeActualizarEstado =
        usuarioActual != null &&
                usuarioActual == animal.usuarioReporta


    // ============================================================
    // SIGUIENTE ESTADO
    // ============================================================

    val siguienteEstado =
        obtenerSiguienteEstado(animal.estado)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        // ═════════════════════════════════════
        // HEADER
        // ═════════════════════════════════════

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Cream
                )
            }

            Text(
                text = "Seguimiento del animal",
                color = Cream,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }


        // ═════════════════════════════════════
        // FOTO
        // ═════════════════════════════════════

        if (animal.fotoUri.isNotBlank()) {

            AsyncImage(
                model = animal.fotoUri,
                contentDescription =
                    "Foto del animal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp)
                    .clip(
                        RoundedCornerShape(22.dp)
                    ),
                contentScale =
                    ContentScale.Crop
            )

        } else {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp),
                shape =
                    RoundedCornerShape(22.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White.copy(
                                alpha = 0.12f
                            )
                    )
            ) {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "🐾",
                            fontSize = 55.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Sin fotografía",
                            color =
                                Color.White.copy(
                                    alpha = 0.8f
                                )
                        )
                    }
                }
            }
        }


        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // ═════════════════════════════════════
            // ESPECIE + URGENCIA
            // ═════════════════════════════════════

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = animal.especie,
                    modifier =
                        Modifier.weight(1f),
                    color = Cream,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                UrgenciaDetalle(
                    urgencia =
                        animal.urgencia
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            // ═════════════════════════════════════
            // ESTADO ACTUAL
            // ═════════════════════════════════════

            EstadoActualCard(
                estado = animal.estado
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            // ═════════════════════════════════════
            // PROGRESO
            // ═════════════════════════════════════

            ProgresoAnimal(
                estadoActual =
                    animal.estado
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // ═════════════════════════════════════
            // INFORMACIÓN
            // ═════════════════════════════════════

            InfoCard(
                titulo = "Condición",
                contenido = animal.condicion
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            InfoCard(
                titulo = "Descripción",
                contenido = animal.descripcion
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // ═════════════════════════════════════
            // UBICACIÓN
            // ═════════════════════════════════════

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(18.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = Cream
                    )
            ) {

                Row(
                    modifier =
                        Modifier.padding(16.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.LocationOn,
                        contentDescription =
                            null,
                        tint = Brown
                    )

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Column {

                        Text(
                            text = "Ubicación",
                            color = BrownMedium,
                            fontSize = 13.sp
                        )

                        Text(
                            text =
                                if (
                                    animal.ubicacion != null
                                )
                                    "Ubicación disponible"
                                else
                                    "Ubicación no disponible",
                            color = BrownDark,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // ═════════════════════════════════════
            // QUIERO AYUDAR
            // ═════════════════════════════════════

            Button(
                onClick = {
                    mostrarDialogoAyuda = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape =
                    RoundedCornerShape(18.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Beige,
                        contentColor = BrownDark
                    )
            ) {

                Text(
                    text =
                        "🐾  QUIERO AYUDAR",
                    fontSize = 17.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // ═════════════════════════════════════
            // GESTIÓN DEL ESTADO
            // ═════════════════════════════════════

            if (
                puedeActualizarEstado &&
                siguienteEstado != null
            ) {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(18.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Cream
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(
                            text =
                                "Actualizar proceso",
                            color = BrownDark,
                            fontSize = 18.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Puedes registrar el siguiente avance del animal.",
                            color = BrownMedium,
                            fontSize = 14.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Button(
                            onClick = {

                                observacionEstado =
                                    ""

                                mostrarDialogoEstado =
                                    true
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            enabled =
                                !actualizandoEstado,
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = Brown,
                                    contentColor = Cream
                                ),
                            shape =
                                RoundedCornerShape(14.dp)
                        ) {

                            if (actualizandoEstado) {

                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(
                                            22.dp
                                        ),
                                    color = Cream,
                                    strokeWidth = 2.dp
                                )

                            } else {

                                Icon(
                                    imageVector =
                                        Icons.Default.CheckCircle,
                                    contentDescription =
                                        null
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(8.dp)
                                )

                                Text(
                                    text =
                                        "AVANZAR A: ${
                                            nombreEstado(
                                                siguienteEstado
                                            )
                                        }",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }

                        if (
                            mensajeHistorial != null
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    mensajeHistorial,
                                color = Brown,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }


            // ═════════════════════════════════════
            // HISTORIAL REAL DE FIREBASE
            // ═════════════════════════════════════

            Text(
                text =
                    "📋 Historial del proceso",
                color = Cream,
                fontSize = 19.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            if (cargandoHistorial) {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = Cream,
                        modifier =
                            Modifier.size(28.dp)
                    )
                }

            } else if (historial.isEmpty()) {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(18.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White.copy(
                                    alpha = 0.12f
                                )
                        )
                ) {

                    Text(
                        text =
                            "Todavía no hay movimientos registrados.",
                        modifier =
                            Modifier.padding(18.dp),
                        color =
                            Color.White.copy(
                                alpha = 0.8f
                            ),
                        fontSize = 14.sp
                    )
                }

            } else {

                historial.forEach { registro ->

                    HistorialRealCard(
                        registro = registro
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            // ═════════════════════════════════════
            // COMENTARIOS
            // ═════════════════════════════════════

            Text(
                text =
                    "💬 Comentarios",
                color = Cream,
                fontSize = 20.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Comparte información que pueda ayudar " +
                            "en el seguimiento de este animal.",
                color =
                    Color.White.copy(
                        alpha = 0.8f
                    ),
                fontSize = 14.sp
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ═════════════════════════════════════
            // LISTA DE COMENTARIOS
            // ═════════════════════════════════════

            if (cargandoComentarios) {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = Cream,
                        modifier =
                            Modifier.size(28.dp)
                    )
                }

            } else if (comentarios.isEmpty()) {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(18.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White.copy(
                                    alpha = 0.12f
                                )
                        )
                ) {

                    Text(
                        text =
                            "Todavía no hay comentarios. " +
                                    "Sé el primero en aportar información.",
                        modifier =
                            Modifier.padding(18.dp),
                        color =
                            Color.White.copy(
                                alpha = 0.8f
                            ),
                        fontSize = 14.sp
                    )
                }

            } else {

                comentarios.forEach { comentario ->

                    ComentarioCard(
                        comentario = comentario
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )
                }
            }


            // ═════════════════════════════════════
            // ESCRIBIR COMENTARIO
            // ═════════════════════════════════════

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = nuevoComentario,
                onValueChange = {
                    nuevoComentario = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                placeholder = {

                    Text(
                        text =
                            "Escribe un comentario...",
                        color =
                            Color.White.copy(
                                alpha = 0.65f
                            )
                    )
                },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor =
                            Color.White,
                        unfocusedTextColor =
                            Color.White,
                        focusedBorderColor =
                            Cream,
                        unfocusedBorderColor =
                            Color.White.copy(
                                alpha = 0.5f
                            ),
                        cursorColor = Cream
                    ),
                shape =
                    RoundedCornerShape(18.dp),
                minLines = 3,
                enabled =
                    !guardandoComentario
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Button(
                onClick = {

                    onAgregarComentario(
                        nuevoComentario
                    )

                    nuevoComentario = ""

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled =
                    nuevoComentario
                        .trim()
                        .isNotEmpty() &&
                            !guardandoComentario,
                shape =
                    RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Beige,
                        contentColor = BrownDark
                    )
            ) {

                if (guardandoComentario) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(22.dp),
                        color = BrownDark,
                        strokeWidth = 2.dp
                    )

                } else {

                    Icon(
                        imageVector =
                            Icons.Default.Send,
                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text = "PUBLICAR",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            if (mensajeComentario != null) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                TextButton(
                    onClick =
                        onLimpiarMensaje
                ) {

                    Text(
                        text =
                            mensajeComentario,
                        color = Cream,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }
    }


    // ============================================================
    // DIALOGO QUIERO AYUDAR
    // ============================================================

    if (mostrarDialogoAyuda) {

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoAyuda = false
            },

            title = {

                Text(
                    text = "¿Cómo quieres ayudar?",
                    color = BrownDark,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {

                Column {

                    Text(
                        text =
                            "Estado actual: ${nombreEstado(animal.estado)}",
                        color = BrownMedium,
                        fontSize = 14.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    when (normalizarEstado(animal.estado)) {

                        // =================================================
                        // REPORTADO / EN ATENCION
                        // =================================================

                        "REPORTADO",
                        "EN_ATENCION" -> {

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.AtencionMedicaAnimal
                                            .createRoute(animal.id)
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Brown,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "🏥  Atención médica",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.Voluntariado.route
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = BrownMedium,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "🙋  Voluntariado",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }


                        // =================================================
                        // RESCATADO
                        // =================================================

                        "RESCATADO" -> {

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.Voluntariado.route
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = BrownMedium,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "🙋  Apoyar como voluntario",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.AtencionMedicaAnimal
                                            .createRoute(animal.id)
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Brown,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "🏥  Atención médica",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }


                        // =================================================
                        // EN RECUPERACION
                        // =================================================

                        "EN_RECUPERACION" -> {

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.AtencionMedicaAnimal
                                            .createRoute(animal.id)
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Brown,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "🏥  Atención médica",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.Voluntariado.route
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = BrownMedium,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "🙋  Voluntariado",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }


                        // =================================================
                        // EN ADOPCION
                        // =================================================

                        "EN_ADOPCION" -> {

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.Adopciones.route
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Brown,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "🏠  Ver adopciones",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.Voluntariado.route
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = BrownMedium,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "📢  Difundir adopción",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }


                        // =================================================
                        // ADOPTADO
                        // =================================================

                        "ADOPTADO" -> {

                            Text(
                                text =
                                    "❤️ Este animal ya fue adoptado.",
                                color =
                                    BrownMedium,
                                fontWeight =
                                    FontWeight.Bold,
                                fontSize =
                                    15.sp
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    "Puedes seguir ayudando a otros animales " +
                                            "que todavía necesitan apoyo.",
                                color =
                                    BrownMedium,
                                fontSize =
                                    14.sp
                            )
                        }


                        // =================================================
                        // OTROS ESTADOS
                        // =================================================

                        else -> {

                            Text(
                                text =
                                    "Puedes ayudar apoyando el proceso " +
                                            "de atención del animal.",
                                color =
                                    BrownMedium,
                                fontSize =
                                    14.sp
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )

                            Button(
                                onClick = {

                                    mostrarDialogoAyuda = false

                                    navController.navigate(
                                        Screen.Voluntariado.route
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = BrownMedium,
                                        contentColor = Cream
                                    ),

                                shape =
                                    RoundedCornerShape(14.dp)
                            ) {

                                Text(
                                    text =
                                        "🙋  Voluntariado",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        mostrarDialogoAyuda = false
                    }
                ) {

                    Text(
                        text = "CERRAR",
                        color = Brown,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }


    // ============================================================
    // DIALOGO PARA CAMBIAR ESTADO
    // ============================================================

    if (
        mostrarDialogoEstado &&
        siguienteEstado != null
    ) {

        AlertDialog(
            onDismissRequest = {

                if (!actualizandoEstado) {
                    mostrarDialogoEstado =
                        false
                }
            },

            title = {

                Text(
                    text =
                        "Actualizar estado",
                    color = BrownDark,
                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Column {

                    Text(
                        text =
                            "El animal pasará a:",
                        color = BrownMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            nombreEstado(
                                siguienteEstado
                            ),
                        color = BrownDark,
                        fontSize = 19.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    OutlinedTextField(
                        value =
                            observacionEstado,
                        onValueChange = {
                            observacionEstado = it
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        label = {
                            Text("Observación")
                        },
                        placeholder = {
                            Text(
                                "Ej. El animal fue trasladado a un hogar temporal."
                            )
                        },
                        minLines = 3,
                        maxLines = 5
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        onCambiarEstado(
                            siguienteEstado,
                            observacionEstado.trim()
                        )

                        mostrarDialogoEstado =
                            false
                    },

                    enabled =
                        !actualizandoEstado
                ) {

                    Text(
                        text = "CONFIRMAR",
                        color = Brown,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        mostrarDialogoEstado =
                            false
                    },

                    enabled =
                        !actualizandoEstado
                ) {

                    Text(
                        text = "CANCELAR",
                        color = BrownMedium
                    )
                }
            }
        )
    }
}


// ═════════════════════════════════════════════
// HISTORIAL REAL
// ═════════════════════════════════════════════

@Composable
private fun HistorialRealCard(
    registro: HistorialEstadoAnimal
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.12f
                    )
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CheckCircle,
                    contentDescription =
                        null,
                    tint = Beige,
                    modifier =
                        Modifier.size(24.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column {

                    Text(
                        text =
                            nombreEstado(
                                registro.estado
                            ),
                        color = Cream,
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
                            formatearFecha(
                                registro.fecha
                            ),
                        color =
                            Color.White.copy(
                                alpha = 0.65f
                            ),
                        fontSize = 11.sp
                    )
                }
            }

            if (
                registro.observacion.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text =
                        registro.observacion,
                    color =
                        Color.White.copy(
                            alpha = 0.85f
                        ),
                    fontSize = 14.sp
                )
            }
        }
    }
}


// ═════════════════════════════════════════════
// COMENTARIO
// ═════════════════════════════════════════════

@Composable
private fun ComentarioCard(
    comentario: ComentarioAnimal
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Cream
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    comentario.nombreUsuario.ifBlank {
                        "Usuario"
                    },
                color = BrownMedium,
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    comentario.comentario,
                color = BrownDark,
                fontSize = 15.sp
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    formatearFecha(
                        comentario.fecha
                    ),
                color = BrownLight,
                fontSize = 11.sp
            )
        }
    }
}


// ═════════════════════════════════════════════
// FECHA
// ═════════════════════════════════════════════

private fun formatearFecha(
    fecha: Long
): String {

    if (fecha == 0L) {
        return "Fecha no disponible"
    }

    return try {

        val formato =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            )

        formato.format(
            Date(fecha)
        )

    } catch (e: Exception) {

        "Fecha no disponible"
    }
}


// ═════════════════════════════════════════════
// ESTADO ACTUAL
// ═════════════════════════════════════════════

@Composable
private fun EstadoActualCard(
    estado: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Cream
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text = "Estado actual",
                color = BrownMedium,
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    nombreEstado(estado),
                color = BrownDark,
                fontSize = 21.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


// ═════════════════════════════════════════════
// PROGRESO
// ═════════════════════════════════════════════

@Composable
private fun ProgresoAnimal(
    estadoActual: String
) {

    val estadoNormalizado =
        normalizarEstado(estadoActual)

    val posicion =
        estadosAnimal
            .indexOf(estadoNormalizado)
            .coerceAtLeast(0)

    Column {

        Text(
            text =
                "Progreso del proceso",
            color = Cream,
            fontSize = 18.sp,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        estadosAnimal.forEachIndexed {
                index,
                estado ->

            val completado =
                index <= posicion

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(18.dp),
                    shape =
                        RoundedCornerShape(50),
                    color =
                        if (completado)
                            Beige
                        else
                            Color.White.copy(
                                alpha = 0.3f
                            )
                ) {}

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Text(
                    text =
                        nombreEstado(estado),
                    color =
                        if (completado)
                            Cream
                        else
                            Color.White.copy(
                                alpha = 0.55f
                            ),
                    fontSize = 14.sp,
                    fontWeight =
                        if (
                            estado ==
                            estadoNormalizado
                        )
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                )
            }

            if (
                index <
                estadosAnimal.lastIndex
            ) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )
            }
        }
    }
}


// ═════════════════════════════════════════════
// INFO CARD
// ═════════════════════════════════════════════

@Composable
private fun InfoCard(
    titulo: String,
    contenido: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Cream
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text = titulo,
                color = BrownMedium,
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    contenido.ifBlank {
                        "Sin información"
                    },
                color = BrownDark,
                fontSize = 16.sp
            )
        }
    }
}


// ═════════════════════════════════════════════
// URGENCIA
// ═════════════════════════════════════════════

@Composable
private fun UrgenciaDetalle(
    urgencia: String
) {

    Surface(
        shape =
            RoundedCornerShape(50.dp),
        color =
            when (urgencia.lowercase()) {

                "alta" ->
                    Color(0xFFE8B4A8)

                "media" ->
                    Color(0xFFE8D3A3)

                else ->
                    Color(0xFFC6D7C0)
            }
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            if (
                urgencia.equals(
                    "Alta",
                    true
                )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Warning,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(15.dp),
                    tint = BrownDark
                )

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )
            }

            Text(
                text = urgencia,
                color = BrownDark,
                fontWeight =
                    FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}


// ═════════════════════════════════════════════
// NORMALIZAR ESTADO
// ═════════════════════════════════════════════

private fun normalizarEstado(
    estado: String
): String {

    return estado
        .trim()
        .uppercase()
        .replace(
            " ",
            "_"
        )
}


// ═════════════════════════════════════════════
// NOMBRE DEL ESTADO
// ═════════════════════════════════════════════

private fun nombreEstado(
    estado: String
): String {

    return when (
        normalizarEstado(estado)
    ) {

        "REPORTADO" ->
            "Reportado"

        "EN_ATENCION" ->
            "En atención"

        "RESCATADO" ->
            "Rescatado"

        "EN_RECUPERACION" ->
            "En recuperación"

        "EN_ADOPCION" ->
            "En adopción"

        "ADOPTADO" ->
            "Adoptado"

        else ->
            estado
                .replace(
                    "_",
                    " "
                )
                .lowercase()
                .replaceFirstChar {
                    it.uppercase()
                }
    }
}


// ═════════════════════════════════════════════
// SIGUIENTE ESTADO
// ═════════════════════════════════════════════

private fun obtenerSiguienteEstado(
    estadoActual: String
): String? {

    val estadoNormalizado =
        normalizarEstado(
            estadoActual
        )

    val posicion =
        estadosAnimal.indexOf(
            estadoNormalizado
        )

    if (posicion == -1) {
        return null
    }

    if (
        posicion >=
        estadosAnimal.lastIndex
    ) {
        return null
    }

    return estadosAnimal[
        posicion + 1
    ]
}
