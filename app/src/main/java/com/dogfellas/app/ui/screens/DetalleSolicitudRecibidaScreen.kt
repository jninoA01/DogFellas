package com.dogfellas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dogfellas.app.ui.viewmodel.DetalleSolicitudRecibidaViewModel

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val Cream = Color(0xFFF0DDB0)
private val WhiteCream = Color(0xFFF8F3EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSolicitudRecibidaScreen(
    navController: NavController,
    solicitudId: String,
    viewModel: DetalleSolicitudRecibidaViewModel = viewModel()
) {
    val solicitud by viewModel.solicitud.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val procesando by viewModel.procesando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val adopcionFinalizada by viewModel.adopcionFinalizada.collectAsState()

    var mostrarConfirmacion by remember { mutableStateOf(false) }

    LaunchedEffect(solicitudId) {
        viewModel.cargarSolicitud(solicitudId)
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Finalizar adopción") },
            text = {
                Text(
                    "¿Confirmas que el animal fue entregado y deseas finalizar " +
                            "el proceso de adopción?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacion = false
                        viewModel.finalizarAdopcion()
                    }
                ) {
                    Text("Sí, finalizar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = WhiteCream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle de solicitud",
                        color = BrownDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = BrownDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteCream
                )
            )
        }
    ) { paddingValues ->

        when {
            cargando -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp),
                        color = Brown
                    )
                }
            }

            solicitud == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        mensaje ?: "No se encontró la solicitud.",
                        color = BrownDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }

            else -> {
                val s = solicitud!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Cream)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = null,
                                    tint = Brown
                                )
                                Text(
                                    s.nombreCompleto,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = BrownDark
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Estado: ${s.estado}",
                                color = Brown,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    SeccionSolicitud("Información personal") {
                        CampoSolicitud("Documento", "${s.tipoDocumento} ${s.numeroDocumento}")
                        CampoSolicitud("Fecha de nacimiento", s.fechaNacimiento)
                        CampoSolicitud("Teléfono", s.telefono)
                        CampoSolicitud("Correo", s.correo)
                        CampoSolicitud("Ciudad", s.ciudad)
                        CampoSolicitud("Dirección", s.direccion)
                    }

                    SeccionSolicitud("Vivienda y hogar") {
                        CampoSolicitud("Tipo de vivienda", s.tipoVivienda)
                        CampoSolicitud("Descripción", s.vivienda)
                        CampoSolicitud("Autorización del propietario", s.autorizacionPropietario)
                        CampoSolicitud("¿Con quién vive?", s.conQuienVive)
                        CampoSolicitud("Personas en el hogar", s.personasHogar)
                        CampoSolicitud("Acuerdo familiar", s.acuerdoFamilia)
                    }

                    SeccionSolicitud("Experiencia con animales") {
                        CampoSolicitud("¿Ha tenido mascotas?", s.haTenidoMascotas)
                        CampoSolicitud("Experiencia", s.experienciaAnimales)
                        CampoSolicitud("Mascotas actuales", s.mascotasActuales)
                        CampoSolicitud("Cantidad", s.cantidadMascotas)
                        CampoSolicitud("Mascotas vacunadas", s.mascotasVacunadas)
                        CampoSolicitud("Mascotas esterilizadas", s.mascotasEsterilizadas)
                        CampoSolicitud(
                            "Situación de mascotas anteriores",
                            s.situacionMascotasAnteriores
                        )
                    }

                    SeccionSolicitud("Plan de cuidado") {
                        CampoSolicitud("Lugar del animal", s.lugarAnimal)
                        CampoSolicitud("Tiempo que estará solo", s.tiempoSolo)
                        CampoSolicitud("Responsable de alimentación", s.responsableAlimentacion)
                        CampoSolicitud("Responsable de gastos", s.responsableGastos)
                        CampoSolicitud("Capacidad veterinaria", s.capacidadVeterinaria)
                    }

                    SeccionSolicitud("Motivación y compromiso") {
                        CampoSolicitud("Motivo de adopción", s.motivo)
                        CampoSolicitud("¿Por qué este animal?", s.porQueAnimal)
                        CampoSolicitud("Expectativas", s.expectativas)
                        CampoSolicitud(
                            "Compromiso de adopción",
                            if (s.compromisoAdopcion) "Sí" else "No"
                        )
                    }

                    if (!mensaje.isNullOrBlank()) {
                        Text(
                            text = mensaje!!,
                            color = BrownMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (adopcionFinalizada) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Cream
                            )
                        ) {
                            Text(
                                "¡Adopción finalizada! El animal quedó registrado como adoptado.",
                                modifier = Modifier.padding(16.dp),
                                color = BrownDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        when (s.estado.uppercase()) {
                            "PENDIENTE" -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        enabled = !procesando,
                                        onClick = { viewModel.rechazarSolicitud() }
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null
                                        )
                                        Text("Rechazar")
                                    }

                                    Button(
                                        modifier = Modifier.weight(1f),
                                        enabled = !procesando,
                                        onClick = { viewModel.aceptarSolicitud() },
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                            containerColor = Brown
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null
                                        )
                                        Text("Aceptar")
                                    }
                                }
                            }

                            "ACEPTADA" -> {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !procesando,
                                    onClick = { mostrarConfirmacion = true },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = Brown
                                    )
                                ) {
                                    Text("Finalizar adopción")
                                }
                            }
                        }
                    }

                    if (procesando) {
                        CircularProgressIndicator(color = Brown)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SeccionSolicitud(
    titulo: String,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                color = Brown,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Divider(color = Cream)

            contenido()
        }
    }
}

@Composable
private fun CampoSolicitud(
    etiqueta: String,
    valor: String
) {
    if (valor.isNotBlank()) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                etiqueta,
                color = BrownMedium,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                valor,
                color = BrownDark,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}