package com.dogfellas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Healing
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.model.AnimalReportado
import com.dogfellas.app.model.CitaVeterinariaAnimal
import com.dogfellas.app.model.DesparasitacionAnimal
import com.dogfellas.app.model.EmergenciaAnimal
import com.dogfellas.app.model.HistorialMedicoAnimal
import com.dogfellas.app.model.MedicamentoAnimal
import com.dogfellas.app.model.VacunacionAnimal
import com.dogfellas.app.ui.viewmodel.AtencionMedicaAnimalViewModel

// =============================================================
// PALETA DOGFELLAS
// =============================================================

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val BrownLight = Color(0xFF8D6E63)

private val Cream = Color(0xFFF0DDB0)
private val Beige = Color(0xFFD4A96A)
private val WhiteCream = Color(0xFFF8F3EE)

private val SoftBrown = Color(0xFFF3E9DF)
private val BorderBrown = Color(0xFFE5D6C9)

private val SuccessGreen = Color(0xFF4E6B50)
private val SuccessSoft = Color(0xFFE6EFE5)

private val WarningRed = Color(0xFF9B4A3C)
private val WarningSoft = Color(0xFFF8E5E0)

private enum class TipoRegistro {
    HISTORIAL,
    MEDICAMENTO,
    VACUNACION,
    DESPARASITACION,
    CITA,
    EMERGENCIA
}

// =============================================================
// PANTALLA PRINCIPAL
// =============================================================

@Composable
fun AtencionMedicaAnimalScreen(
    navController: NavHostController,
    reporteId: String,
    animal: AnimalReportado? = null,
    viewModel: AtencionMedicaAnimalViewModel = viewModel()
) {

    val historial by viewModel.historialMedico.collectAsState()
    val medicamentos by viewModel.medicamentos.collectAsState()
    val vacunaciones by viewModel.vacunaciones.collectAsState()
    val desparasitaciones by viewModel.desparasitaciones.collectAsState()
    val citas by viewModel.citas.collectAsState()
    val emergencias by viewModel.emergencias.collectAsState()

    val cargando by viewModel.cargando.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var dialogoActivo by remember {
        mutableStateOf<TipoRegistro?>(null)
    }

    LaunchedEffect(reporteId) {
        viewModel.cargarInformacionMedica(reporteId)
    }

    val estadoActual = animal?.estado ?: "RESCATADO"

    Scaffold(
        containerColor = WhiteCream,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            val esPantallaAmplia = maxWidth >= 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {

                // =================================================
                // HEADER
                // =================================================

                MedicalHeader(
                    animal = animal,
                    onBack = {
                        navController.popBackStack()
                    }
                )

                // =================================================
                // CONTENIDO RESPONSIVE
                // =================================================

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 900.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(
                            horizontal = if (esPantallaAmplia) {
                                28.dp
                            } else {
                                16.dp
                            }
                        )
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    // =================================================
                    // ESTADO
                    // =================================================

                    SectionHeader(
                        icon = Icons.Rounded.Favorite,
                        title = "Estado de recuperación",
                        subtitle = "Seguimiento del proceso del animal"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RecoveryCard(
                        estadoActual = estadoActual
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // =================================================
                    // RESUMEN
                    // =================================================

                    SectionHeader(
                        icon = Icons.Rounded.Description,
                        title = "Resumen médico",
                        subtitle = "Información registrada hasta el momento"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (esPantallaAmplia) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                value = historial.size.toString(),
                                label = "Atenciones",
                                icon = Icons.Rounded.Healing
                            )

                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                value = medicamentos.size.toString(),
                                label = "Medicamentos",
                                icon = Icons.Rounded.Medication
                            )

                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                value = vacunaciones.size.toString(),
                                label = "Vacunas",
                                icon = Icons.Rounded.Vaccines
                            )

                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                value = citas.size.toString(),
                                label = "Citas",
                                icon = Icons.Rounded.CalendarMonth
                            )
                        }

                    } else {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                value = historial.size.toString(),
                                label = "Atenciones",
                                icon = Icons.Rounded.Healing
                            )

                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                value = medicamentos.size.toString(),
                                label = "Medicamentos",
                                icon = Icons.Rounded.Medication
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                value = vacunaciones.size.toString(),
                                label = "Vacunas",
                                icon = Icons.Rounded.Vaccines
                            )

                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                value = citas.size.toString(),
                                label = "Citas",
                                icon = Icons.Rounded.CalendarMonth
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // =================================================
                    // ACCIONES
                    // =================================================

                    SectionHeader(
                        icon = Icons.Rounded.Healing,
                        title = "Atención médica",
                        subtitle = "Selecciona el tipo de registro que deseas agregar"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Acción principal
                    PrimaryMedicalAction(
                        onClick = {
                            dialogoActivo = TipoRegistro.HISTORIAL
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (esPantallaAmplia) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            MedicalActionCard(
                                modifier = Modifier.weight(1f),
                                title = "Medicamento",
                                description = "Tratamiento farmacológico",
                                icon = Icons.Rounded.Medication,
                                onClick = {
                                    dialogoActivo = TipoRegistro.MEDICAMENTO
                                }
                            )

                            MedicalActionCard(
                                modifier = Modifier.weight(1f),
                                title = "Vacunación",
                                description = "Vacunas aplicadas",
                                icon = Icons.Rounded.Vaccines,
                                onClick = {
                                    dialogoActivo = TipoRegistro.VACUNACION
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            MedicalActionCard(
                                modifier = Modifier.weight(1f),
                                title = "Desparasitación",
                                description = "Control antiparasitario",
                                icon = Icons.Rounded.Pets,
                                onClick = {
                                    dialogoActivo = TipoRegistro.DESPARASITACION
                                }
                            )

                            MedicalActionCard(
                                modifier = Modifier.weight(1f),
                                title = "Cita veterinaria",
                                description = "Programar revisión",
                                icon = Icons.Rounded.CalendarMonth,
                                onClick = {
                                    dialogoActivo = TipoRegistro.CITA
                                }
                            )
                        }

                    } else {

                        MedicalActionCard(
                            title = "Medicamento",
                            description = "Registrar tratamiento farmacológico",
                            icon = Icons.Rounded.Medication,
                            onClick = {
                                dialogoActivo = TipoRegistro.MEDICAMENTO
                            }
                        )

                        MedicalActionCard(
                            title = "Vacunación",
                            description = "Registrar vacunas aplicadas",
                            icon = Icons.Rounded.Vaccines,
                            onClick = {
                                dialogoActivo = TipoRegistro.VACUNACION
                            }
                        )

                        MedicalActionCard(
                            title = "Desparasitación",
                            description = "Registrar control antiparasitario",
                            icon = Icons.Rounded.Pets,
                            onClick = {
                                dialogoActivo = TipoRegistro.DESPARASITACION
                            }
                        )

                        MedicalActionCard(
                            title = "Cita veterinaria",
                            description = "Programar una próxima revisión",
                            icon = Icons.Rounded.CalendarMonth,
                            onClick = {
                                dialogoActivo = TipoRegistro.CITA
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Emergencia separada visualmente
                    EmergencyActionCard(
                        onClick = {
                            dialogoActivo = TipoRegistro.EMERGENCIA
                        }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // =================================================
                    // HISTORIAL
                    // =================================================

                    SectionHeader(
                        icon = Icons.Rounded.Description,
                        title = "Registros médicos",
                        subtitle = "Historial de atención del animal"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val tieneRegistros =
                        historial.isNotEmpty() ||
                                medicamentos.isNotEmpty() ||
                                vacunaciones.isNotEmpty() ||
                                desparasitaciones.isNotEmpty() ||
                                citas.isNotEmpty() ||
                                emergencias.isNotEmpty()

                    if (!tieneRegistros && !cargando) {

                        EmptyMedicalState(
                            onRegistrar = {
                                dialogoActivo = TipoRegistro.HISTORIAL
                            }
                        )

                    } else {

                        historial.forEach {
                            HistorialCard(it)
                        }

                        medicamentos.forEach {
                            MedicamentoCard(it)
                        }

                        vacunaciones.forEach {
                            VacunacionCard(it)
                        }

                        desparasitaciones.forEach {
                            DesparasitacionCard(it)
                        }

                        citas.forEach {
                            CitaCard(it)
                        }

                        emergencias.forEach {
                            EmergenciaCard(it)
                        }
                    }

                    // =================================================
                    // CARGANDO
                    // =================================================

                    if (cargando || guardando) {

                        Spacer(modifier = Modifier.height(16.dp))

                        LoadingCard(
                            guardando = guardando
                        )
                    }

                    // =================================================
                    // MENSAJE
                    // =================================================

                    mensaje?.let {

                        Spacer(modifier = Modifier.height(12.dp))

                        MessageCard(
                            message = it
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // =============================================================
    // DIÁLOGOS
    // =============================================================

    when (dialogoActivo) {

        TipoRegistro.HISTORIAL -> {

            HistorialDialog(
                reporteId = reporteId,
                onDismiss = {
                    dialogoActivo = null
                },
                onGuardar = {
                    viewModel.registrarHistorialMedico(it)
                    dialogoActivo = null
                }
            )
        }

        TipoRegistro.MEDICAMENTO -> {

            MedicamentoDialog(
                reporteId = reporteId,
                onDismiss = {
                    dialogoActivo = null
                },
                onGuardar = {
                    viewModel.registrarMedicamento(it)
                    dialogoActivo = null
                }
            )
        }

        TipoRegistro.VACUNACION -> {

            VacunacionDialog(
                reporteId = reporteId,
                onDismiss = {
                    dialogoActivo = null
                },
                onGuardar = {
                    viewModel.registrarVacunacion(it)
                    dialogoActivo = null
                }
            )
        }

        TipoRegistro.DESPARASITACION -> {

            DesparasitacionDialog(
                reporteId = reporteId,
                onDismiss = {
                    dialogoActivo = null
                },
                onGuardar = {
                    viewModel.registrarDesparasitacion(it)
                    dialogoActivo = null
                }
            )
        }

        TipoRegistro.CITA -> {

            CitaDialog(
                reporteId = reporteId,
                onDismiss = {
                    dialogoActivo = null
                },
                onGuardar = {
                    viewModel.registrarCita(it)
                    dialogoActivo = null
                }
            )
        }

        TipoRegistro.EMERGENCIA -> {

            EmergenciaDialog(
                reporteId = reporteId,
                onDismiss = {
                    dialogoActivo = null
                },
                onGuardar = {
                    viewModel.registrarEmergencia(it)
                    dialogoActivo = null
                }
            )
        }

        null -> Unit
    }
}

// =============================================================
// HEADER
// =============================================================

@Composable
private fun MedicalHeader(
    animal: AnimalReportado?,
    onBack: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BrownDark,
                        Brown,
                        BrownMedium
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 18.dp,
                    top = 8.dp,
                    bottom = 22.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "ATENCIÓN MÉDICA",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Ficha de recuperación",
                        color = Cream,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.13f)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Healing,
                        contentDescription = "Atención médica",
                        tint = Cream,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.12f)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(17.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Cream),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.Pets,
                            contentDescription = "Animal",
                            tint = BrownDark,
                            modifier = Modifier.size(35.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = animal?.especie
                                ?.takeIf { it.isNotBlank() }
                                ?.replaceFirstChar { it.uppercase() }
                                ?: "Animal rescatado",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = animal?.condicion
                                ?.takeIf { it.isNotBlank() }
                                ?: "Seguimiento médico",
                            color = Cream,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        EstadoChip(
                            estado = animal?.estado ?: "RESCATADO"
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// ENCABEZADO DE SECCIÓN
// =============================================================

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Cream),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Brown,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(11.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = BrownDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = BrownLight,
                fontSize = 12.sp
            )
        }
    }
}

// =============================================================
// ESTADO
// =============================================================

@Composable
private fun EstadoChip(
    estado: String
) {

    val texto = when (estado) {
        "REPORTADO" -> "Reportado"
        "EN_ATENCION" -> "En atención"
        "RESCATADO" -> "Rescatado"
        "EN_RECUPERACION" -> "En recuperación"
        "EN_ADOPCION" -> "En adopción"
        "ADOPTADO" -> "Adoptado"
        else -> estado.replace("_", " ")
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Cream)
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Brown)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = texto.uppercase(),
            color = BrownDark,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// =============================================================
// PROGRESO
// =============================================================

@Composable
private fun RecoveryCard(
    estadoActual: String
) {

    val estados = listOf(
        "REPORTADO",
        "RESCATADO",
        "EN_RECUPERACION",
        "EN_ADOPCION",
        "ADOPTADO"
    )

    val posicionActual = estados
        .indexOf(estadoActual)
        .coerceAtLeast(0)

    val nombreEstado = when (estadoActual) {
        "REPORTADO" -> "Animal reportado"
        "EN_ATENCION" -> "Animal en atención"
        "RESCATADO" -> "Animal rescatado"
        "EN_RECUPERACION" -> "En recuperación"
        "EN_ADOPCION" -> "Disponible para adopción"
        "ADOPTADO" -> "Adopción completada"
        else -> "Seguimiento activo"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SuccessSoft),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(11.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = nombreEstado,
                        color = BrownDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Estado actual del proceso",
                        color = BrownLight,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                estados.forEachIndexed { index, estado ->

                    val activo = index <= posicionActual
                    val ultimo = index == estados.lastIndex

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (activo) {
                                            Brown
                                        } else {
                                            Color(0xFFE4DCD5)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                if (activo) {

                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            if (!ultimo) {

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(3.dp)
                                        .background(
                                            if (index < posicionActual) {
                                                Brown
                                            } else {
                                                Color(0xFFE4DCD5)
                                            }
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = when (estado) {
                                "REPORTADO" -> "Reporte"
                                "RESCATADO" -> "Rescate"
                                "EN_RECUPERACION" -> "Recuperación"
                                "EN_ADOPCION" -> "Adopción"
                                else -> "Adoptado"
                            },
                            color = if (activo) {
                                BrownDark
                            } else {
                                BrownLight
                            },
                            fontSize = 8.sp,
                            fontWeight = if (activo) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// RESUMEN
// =============================================================

@Composable
private fun SummaryCard(
    modifier: Modifier,
    value: String,
    label: String,
    icon: ImageVector
) {

    Card(
        modifier = modifier.height(92.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(31.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(SoftBrown),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrownMedium,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = value,
                    color = BrownDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = label,
                color = BrownLight,
                fontSize = 11.sp
            )
        }
    }
}

// =============================================================
// ACCIÓN PRINCIPAL
// =============================================================

@Composable
private fun PrimaryMedicalAction(
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Brown
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Cream),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Rounded.Healing,
                    contentDescription = "Registrar atención",
                    tint = BrownDark,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Registrar atención",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Diagnóstico, evolución y tratamiento",
                    color = Cream,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "›",
                color = Cream,
                fontSize = 30.sp
            )
        }
    }
}

// =============================================================
// ACCIONES SECUNDARIAS
// =============================================================

@Composable
private fun MedicalActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(47.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SoftBrown),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = BrownMedium,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = BrownDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    color = BrownLight,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Text(
                text = "›",
                color = BrownMedium,
                fontSize = 27.sp
            )
        }
    }
}

// =============================================================
// EMERGENCIA
// =============================================================

@Composable
private fun EmergencyActionCard(
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = WarningSoft
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(47.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = "Emergencia",
                    tint = WarningRed,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Registrar emergencia",
                    color = BrownDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Atención veterinaria urgente",
                    color = WarningRed,
                    fontSize = 11.sp
                )
            }

            Text(
                text = "›",
                color = WarningRed,
                fontSize = 27.sp
            )
        }
    }
}

// =============================================================
// ESTADO VACÍO
// =============================================================

@Composable
private fun EmptyMedicalState(
    onRegistrar: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SoftBrown
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Cream),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    tint = Brown,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Aún no hay registros médicos",
                color = BrownDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Registra la primera atención para comenzar el seguimiento.",
                color = BrownLight,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onRegistrar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brown
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                Icon(
                    imageVector = Icons.Rounded.Healing,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text("Registrar atención")
            }
        }
    }
}

// =============================================================
// CARGANDO
// =============================================================

@Composable
private fun LoadingCard(
    guardando: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = SoftBrown
        )
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CircularProgressIndicator(
                modifier = Modifier.size(23.dp),
                color = Brown,
                strokeWidth = 2.5.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (guardando) {
                    "Guardando información..."
                } else {
                    "Cargando información..."
                },
                color = BrownDark,
                fontSize = 13.sp
            )
        }
    }
}

// =============================================================
// MENSAJE
// =============================================================

@Composable
private fun MessageCard(
    message: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = WarningSoft
        )
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = WarningRed,
                modifier = Modifier.size(21.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = message,
                color = BrownDark,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

// =============================================================
// TARJETAS DE REGISTROS
// =============================================================

@Composable
private fun RecordCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color = Brown,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(43.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Cream),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(11.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = title,
                        color = BrownDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = subtitle,
                        color = BrownLight,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun InfoLine(
    label: String,
    value: String
) {

    if (value.isNotBlank()) {

        Column(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {

            Text(
                text = label,
                color = BrownLight,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = value,
                color = BrownDark,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// =============================================================
// REGISTROS ESPECÍFICOS
// =============================================================

@Composable
private fun HistorialCard(
    registro: HistorialMedicoAnimal
) {

    RecordCard(
        title = "Evaluación médica",
        subtitle = registro.veterinario.ifBlank {
            "Atención veterinaria"
        },
        icon = Icons.Rounded.Healing
    ) {

        InfoLine("DIAGNÓSTICO", registro.diagnostico)
        InfoLine("TRATAMIENTO", registro.tratamiento)
        InfoLine("OBSERVACIONES", registro.observaciones)
    }
}

@Composable
private fun MedicamentoCard(
    registro: MedicamentoAnimal
) {

    RecordCard(
        title = registro.nombre.ifBlank {
            "Medicamento"
        },
        subtitle = registro.veterinario.ifBlank {
            "Tratamiento farmacológico"
        },
        icon = Icons.Rounded.Medication
    ) {

        InfoLine("DOSIS", registro.dosis)
        InfoLine("FRECUENCIA", registro.frecuencia)
        InfoLine("OBSERVACIONES", registro.observaciones)
    }
}

@Composable
private fun VacunacionCard(
    registro: VacunacionAnimal
) {

    RecordCard(
        title = registro.vacuna.ifBlank {
            "Vacunación"
        },
        subtitle = registro.veterinario.ifBlank {
            "Vacuna aplicada"
        },
        icon = Icons.Rounded.Vaccines
    ) {

        InfoLine("OBSERVACIONES", registro.observaciones)
    }
}

@Composable
private fun DesparasitacionCard(
    registro: DesparasitacionAnimal
) {

    RecordCard(
        title = registro.producto.ifBlank {
            "Desparasitación"
        },
        subtitle = registro.veterinario.ifBlank {
            "Control antiparasitario"
        },
        icon = Icons.Rounded.Pets
    ) {

        InfoLine("OBSERVACIONES", registro.observaciones)
    }
}

@Composable
private fun CitaCard(
    registro: CitaVeterinariaAnimal
) {

    RecordCard(
        title = "Cita veterinaria",
        subtitle = registro.veterinario.ifBlank {
            "Cita programada"
        },
        icon = Icons.Rounded.CalendarMonth
    ) {

        InfoLine("DIRECCIÓN", registro.direccionVeterinaria)
        InfoLine("MOTIVO", registro.motivo)
        InfoLine("ESTADO", registro.estado)
        InfoLine("OBSERVACIONES", registro.observaciones)
    }
}

@Composable
private fun EmergenciaCard(
    registro: EmergenciaAnimal
) {

    RecordCard(
        title = "Atención de emergencia",
        subtitle = registro.veterinario.ifBlank {
            "Atención de urgencia"
        },
        icon = Icons.Rounded.Warning,
        accent = WarningRed
    ) {

        InfoLine("MOTIVO", registro.motivo)
        InfoLine("ATENCIÓN REALIZADA", registro.atencionRealizada)
        InfoLine("RESULTADO", registro.resultado)
        InfoLine("OBSERVACIONES", registro.observaciones)
    }
}

// =============================================================
// CAMPO MÉDICO
// =============================================================

@Composable
private fun MedicalField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        singleLine = false,
        maxLines = 4
    )
}

// =============================================================
// DIÁLOGO BASE
// =============================================================

@Composable
private fun MedicalDialog(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    onGuardar: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        icon = {

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Cream),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Brown,
                    modifier = Modifier.size(27.dp)
                )
            }
        },

        title = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = title,
                    color = BrownDark,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = BrownLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                contenido()
            }
        },

        confirmButton = {

            Button(
                onClick = onGuardar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brown
                ),
                shape = RoundedCornerShape(13.dp)
            ) {

                Text(
                    text = "Guardar",
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "Cancelar",
                    color = BrownMedium
                )
            }
        },

        containerColor = WhiteCream,
        shape = RoundedCornerShape(25.dp)
    )
}

// =============================================================
// HISTORIAL
// =============================================================

@Composable
private fun HistorialDialog(
    reporteId: String,
    onDismiss: () -> Unit,
    onGuardar: (HistorialMedicoAnimal) -> Unit
) {

    var veterinario by remember { mutableStateOf("") }
    var diagnostico by remember { mutableStateOf("") }
    var tratamiento by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    MedicalDialog(
        title = "Registrar atención",
        subtitle = "Documenta la evolución del animal",
        icon = Icons.Rounded.Healing,
        onDismiss = onDismiss,
        onGuardar = {

            onGuardar(
                HistorialMedicoAnimal(
                    reporteId = reporteId,
                    usuarioId = "",
                    veterinario = veterinario,
                    fecha = System.currentTimeMillis(),
                    tipo = "EVALUACION",
                    diagnostico = diagnostico,
                    tratamiento = tratamiento,
                    observaciones = observaciones
                )
            )
        }
    ) {

        MedicalField(
            value = veterinario,
            onValueChange = { veterinario = it },
            label = "Veterinario"
        )

        MedicalField(
            value = diagnostico,
            onValueChange = { diagnostico = it },
            label = "Diagnóstico"
        )

        MedicalField(
            value = tratamiento,
            onValueChange = { tratamiento = it },
            label = "Tratamiento"
        )

        MedicalField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = "Observaciones"
        )
    }
}

// =============================================================
// MEDICAMENTO
// =============================================================

@Composable
private fun MedicamentoDialog(
    reporteId: String,
    onDismiss: () -> Unit,
    onGuardar: (MedicamentoAnimal) -> Unit
) {

    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }
    var veterinario by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    MedicalDialog(
        title = "Registrar medicamento",
        subtitle = "Agrega el tratamiento indicado",
        icon = Icons.Rounded.Medication,
        onDismiss = onDismiss,
        onGuardar = {

            onGuardar(
                MedicamentoAnimal(
                    reporteId = reporteId,
                    nombre = nombre,
                    dosis = dosis,
                    frecuencia = frecuencia,
                    fechaInicio = System.currentTimeMillis(),
                    fechaFin = 0L,
                    veterinario = veterinario,
                    observaciones = observaciones,
                    activo = true
                )
            )
        }
    ) {

        MedicalField(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre del medicamento"
        )

        MedicalField(
            value = dosis,
            onValueChange = { dosis = it },
            label = "Dosis"
        )

        MedicalField(
            value = frecuencia,
            onValueChange = { frecuencia = it },
            label = "Frecuencia"
        )

        MedicalField(
            value = veterinario,
            onValueChange = { veterinario = it },
            label = "Veterinario"
        )

        MedicalField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = "Observaciones"
        )
    }
}

// =============================================================
// VACUNACIÓN
// =============================================================

@Composable
private fun VacunacionDialog(
    reporteId: String,
    onDismiss: () -> Unit,
    onGuardar: (VacunacionAnimal) -> Unit
) {

    var vacuna by remember { mutableStateOf("") }
    var veterinario by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    MedicalDialog(
        title = "Registrar vacunación",
        subtitle = "Registra la vacuna aplicada",
        icon = Icons.Rounded.Vaccines,
        onDismiss = onDismiss,
        onGuardar = {

            onGuardar(
                VacunacionAnimal(
                    reporteId = reporteId,
                    vacuna = vacuna,
                    fecha = System.currentTimeMillis(),
                    proximaDosis = 0L,
                    veterinario = veterinario,
                    observaciones = observaciones
                )
            )
        }
    ) {

        MedicalField(
            value = vacuna,
            onValueChange = { vacuna = it },
            label = "Vacuna"
        )

        MedicalField(
            value = veterinario,
            onValueChange = { veterinario = it },
            label = "Veterinario"
        )

        MedicalField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = "Observaciones"
        )
    }
}

// =============================================================
// DESPARASITACIÓN
// =============================================================

@Composable
private fun DesparasitacionDialog(
    reporteId: String,
    onDismiss: () -> Unit,
    onGuardar: (DesparasitacionAnimal) -> Unit
) {

    var producto by remember { mutableStateOf("") }
    var veterinario by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    MedicalDialog(
        title = "Registrar desparasitación",
        subtitle = "Registra el control antiparasitario",
        icon = Icons.Rounded.Pets,
        onDismiss = onDismiss,
        onGuardar = {

            onGuardar(
                DesparasitacionAnimal(
                    reporteId = reporteId,
                    producto = producto,
                    fecha = System.currentTimeMillis(),
                    proximaFecha = 0L,
                    veterinario = veterinario,
                    observaciones = observaciones
                )
            )
        }
    ) {

        MedicalField(
            value = producto,
            onValueChange = { producto = it },
            label = "Producto"
        )

        MedicalField(
            value = veterinario,
            onValueChange = { veterinario = it },
            label = "Veterinario"
        )

        MedicalField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = "Observaciones"
        )
    }
}

// =============================================================
// CITA
// =============================================================

@Composable
private fun CitaDialog(
    reporteId: String,
    onDismiss: () -> Unit,
    onGuardar: (CitaVeterinariaAnimal) -> Unit
) {

    var veterinario by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    MedicalDialog(
        title = "Programar cita",
        subtitle = "Agenda una próxima revisión",
        icon = Icons.Rounded.CalendarMonth,
        onDismiss = onDismiss,
        onGuardar = {

            onGuardar(
                CitaVeterinariaAnimal(
                    reporteId = reporteId,
                    veterinario = veterinario,
                    direccionVeterinaria = direccion,
                    fecha = System.currentTimeMillis(),
                    motivo = motivo,
                    estado = "PROGRAMADA",
                    observaciones = observaciones
                )
            )
        }
    ) {

        MedicalField(
            value = veterinario,
            onValueChange = { veterinario = it },
            label = "Veterinario / clínica"
        )

        MedicalField(
            value = direccion,
            onValueChange = { direccion = it },
            label = "Dirección"
        )

        MedicalField(
            value = motivo,
            onValueChange = { motivo = it },
            label = "Motivo de la cita"
        )

        MedicalField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = "Observaciones"
        )
    }
}

// =============================================================
// EMERGENCIA
// =============================================================

@Composable
private fun EmergenciaDialog(
    reporteId: String,
    onDismiss: () -> Unit,
    onGuardar: (EmergenciaAnimal) -> Unit
) {

    var motivo by remember { mutableStateOf("") }
    var atencion by remember { mutableStateOf("") }
    var veterinario by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    MedicalDialog(
        title = "Registrar emergencia",
        subtitle = "Documenta la atención de urgencia",
        icon = Icons.Rounded.Warning,
        onDismiss = onDismiss,
        onGuardar = {

            onGuardar(
                EmergenciaAnimal(
                    reporteId = reporteId,
                    fecha = System.currentTimeMillis(),
                    motivo = motivo,
                    atencionRealizada = atencion,
                    veterinario = veterinario,
                    resultado = resultado,
                    observaciones = observaciones
                )
            )
        }
    ) {

        MedicalField(
            value = motivo,
            onValueChange = { motivo = it },
            label = "Motivo de la emergencia"
        )

        MedicalField(
            value = atencion,
            onValueChange = { atencion = it },
            label = "Atención realizada"
        )

        MedicalField(
            value = veterinario,
            onValueChange = { veterinario = it },
            label = "Veterinario"
        )

        MedicalField(
            value = resultado,
            onValueChange = { resultado = it },
            label = "Resultado"
        )

        MedicalField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = "Observaciones"
        )
    }
}