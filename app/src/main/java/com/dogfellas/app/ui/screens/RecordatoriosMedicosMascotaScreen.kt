
package com.dogfellas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dogfellas.app.ui.viewmodel.HistorialMedicoMascotaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val FondoRecordatorios = Color(0xFF1B0F08)
private val CafeRecordatorios = Color(0xFF6B3F1F)
private val CremaRecordatorios = Color(0xFFF5E6CA)
private val CafeClaroRecordatorios = Color(0xFFD7B899)

@Composable
fun RecordatoriosMedicosMascotaScreen(
    navController: NavController,
    mascotaId: String,
    viewModel: HistorialMedicoMascotaViewModel = viewModel()
) {
    val recordatorios by viewModel.recordatorios.collectAsState()
    val cargando by viewModel.cargandoRecordatorios.collectAsState()

    LaunchedEffect(mascotaId) {
        viewModel.cargarRecordatorios(mascotaId)
    }

    Scaffold(
        containerColor = FondoRecordatorios,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoRecordatorios)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = CremaRecordatorios
                    )
                }

                Text(
                    text = "Recordatorios médicos",
                    color = CremaRecordatorios,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
        ) {
            Text(
                text = "Cuidados y próximas atenciones",
                color = CafeClaroRecordatorios,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (cargando) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CafeClaroRecordatorios
                    )
                }
            } else if (recordatorios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = CafeClaroRecordatorios,
                            modifier = Modifier.size(52.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "No hay recordatorios registrados",
                            color = CremaRecordatorios,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Cuando registres citas, vacunas o tratamientos, aparecerán aquí.",
                            color = CafeClaroRecordatorios
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = recordatorios,
                        key = { "${it.categoria}_${it.id}" }
                    ) { recordatorio ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = CafeRecordatorios
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = CremaRecordatorios
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = recordatorio.titulo,
                                        color = CremaRecordatorios,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    val colorEstado = when (
                                        recordatorio.estado
                                    ) {
                                        "VENCIDO" -> Color(0xFFFF8A80)
                                        "ACTIVO" -> Color(0xFFA5D6A7)
                                        else -> Color(0xFFFFD180)
                                    }

                                    Text(
                                        text = recordatorio.estado,
                                        color = colorEstado,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = recordatorio.descripcion.ifBlank {
                                        "Sin descripción"
                                    },
                                    color = CremaRecordatorios
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val fecha = remember(recordatorio.fecha) {
                                    SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                    ).format(Date(recordatorio.fecha))
                                }

                                Text(
                                    text = "Fecha: $fecha",
                                    color = CafeClaroRecordatorios,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
