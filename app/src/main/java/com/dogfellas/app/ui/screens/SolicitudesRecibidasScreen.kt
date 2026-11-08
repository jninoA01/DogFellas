package com.dogfellas.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.model.SolicitudAdopcion
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.viewmodel.SolicitudesRecibidasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val Cream = Color(0xFFF0DDB0)
private val Beige = Color(0xFFD4A96A)
private val WhiteCream = Color(0xFFF8F3EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesRecibidasScreen(
    navController: NavHostController,
    reporteId: String,
    viewModel: SolicitudesRecibidasViewModel = viewModel()
) {
    val solicitudes by viewModel.solicitudes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(reporteId) {
        viewModel.cargarSolicitudesRecibidas(reporteId)
    }

    Scaffold(
        containerColor = WhiteCream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Solicitudes recibidas",
                        color = Cream,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Cream
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrownDark
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Text(
                text = "Personas interesadas en adoptar",
                style = MaterialTheme.typography.titleMedium,
                color = BrownDark,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Revisa cada solicitud y conoce a quienes quieren darle un hogar al animal.",
                style = MaterialTheme.typography.bodyMedium,
                color = BrownMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            when {
                cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Brown)
                    }
                }

                mensaje != null && solicitudes.isEmpty() -> {
                    EstadoSolicitudes(
                        icono = Icons.Default.Inbox,
                        titulo = "No se pudieron cargar las solicitudes",
                        descripcion = mensaje ?: "Intenta nuevamente.",
                        mostrarBoton = true,
                        onReintentar = {
                            viewModel.cargarSolicitudesRecibidas(reporteId)
                        }
                    )
                }

                solicitudes.isEmpty() -> {
                    EstadoSolicitudes(
                        icono = Icons.Default.Inbox,
                        titulo = "Todavía no hay solicitudes",
                        descripcion = "Cuando alguien solicite adoptar este animal, aparecerá aquí."
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = solicitudes,
                            key = { it.id }
                        ) { solicitud ->

                            SolicitudRecibidaCard(
                                solicitud = solicitud,
                                onClick = {
                                    navController.navigate(
                                        Screen.DetalleSolicitudRecibida
                                            .createRoute(solicitud.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SolicitudRecibidaCard(
    solicitud: SolicitudAdopcion,
    onClick: () -> Unit
) {
    val estado = solicitud.estado.trim().uppercase()

    val colorEstado = when (estado) {
        "ACEPTADA" -> Color(0xFF2E7D32)
        "RECHAZADA" -> Color(0xFFC62828)
        else -> Color(0xFF9A6700)
    }

    val textoEstado = when (estado) {
        "ACEPTADA" -> "Aceptada"
        "RECHAZADA" -> "Rechazada"
        else -> "Pendiente"
    }

    val iconoEstado = when (estado) {
        "ACEPTADA" -> Icons.Default.CheckCircle
        "RECHAZADA" -> Icons.Default.Cancel
        else -> Icons.Default.HourglassTop
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    color = Cream
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Brown
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = solicitud.nombreCompleto.ifBlank {
                            "Solicitante"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = BrownDark,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Solicitud de adopción",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrownMedium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = colorEstado.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = iconoEstado,
                            contentDescription = null,
                            tint = colorEstado,
                            modifier = Modifier.size(14.dp)
                        )

                        Text(
                            text = textoEstado,
                            color = colorEstado,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = "Ciudad: ${solicitud.ciudad.ifBlank { "No indicada" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = BrownMedium,
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = "Fecha: ${formatearFechaSolicitud(solicitud.fechaSolicitud)}",
                style = MaterialTheme.typography.bodySmall,
                color = BrownMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver solicitud",
                    color = Brown,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun EstadoSolicitudes(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    descripcion: String,
    mostrarBoton: Boolean = false,
    onReintentar: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = BrownLight,
                modifier = Modifier.size(58.dp)
            )

            Text(
                text = titulo,
                color = BrownDark,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = descripcion,
                color = BrownMedium,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (mostrarBoton) {
                Button(
                    onClick = onReintentar,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                    Text(
                        text = "Reintentar",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

private val BrownLight = Color(0xFF8D6E63)

private fun formatearFechaSolicitud(fecha: Long): String {
    if (fecha <= 0L) return "Fecha no disponible"

    return SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    ).format(Date(fecha))
}