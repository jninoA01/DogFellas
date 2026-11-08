
package com.dogfellas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dogfellas.app.navigation.Screen

private val HistorialBg = Color(0xFF1B0F08)
private val HistorialCafe = Color(0xFF6B3F1F)
private val HistorialCafeLight = Color(0xFFD7B899)
private val HistorialCream = Color(0xFFF5E6CA)
private val HistorialGlass = Color(0xFF2A1B12)

private data class CategoriaMedica(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val tipoRegistro: String
)

private val categoriasMedicas = listOf(
    CategoriaMedica(
        "Consultas e historial",
        "Diagnósticos y tratamientos",
        Icons.Rounded.MedicalServices,
        "historial_medico"
    ),
    CategoriaMedica(
        "Citas veterinarias",
        "Próximas y anteriores citas",
        Icons.Rounded.CalendarMonth,
        "citas"
    ),
    CategoriaMedica(
        "Vacunaciones",
        "Vacunas y próximas dosis",
        Icons.Rounded.Vaccines,
        "vacunaciones"
    ),
    CategoriaMedica(
        "Medicamentos",
        "Tratamientos y medicamentos",
        Icons.Rounded.Medication,
        "medicamentos"
    ),
    CategoriaMedica(
        "Desparasitaciones",
        "Control y próximas fechas",
        Icons.Rounded.HealthAndSafety,
        "desparasitaciones"
    ),
    CategoriaMedica(
        "Emergencias",
        "Atenciones de urgencia",
        Icons.Rounded.Warning,
        "emergencias"
    )
)

@Composable
fun HistorialMedicoMascotaScreen(
    navController: NavHostController,
    mascotaId: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HistorialBg)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Volver",
                    tint = HistorialCream
                )
            }

            Column {
                Text(
                    text = "Historial médico",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = HistorialCream
                )

                Text(
                    text = "Consulta la información de salud de tu mascota",
                    style = MaterialTheme.typography.bodySmall,
                    color = HistorialCafeLight
                )
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = HistorialCafe
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Pets,
                    contentDescription = null,
                    tint = HistorialCream,
                    modifier = Modifier.size(32.dp)
                )

                Column(
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = "Registro de salud",
                        fontWeight = FontWeight.SemiBold,
                        color = HistorialCream
                    )

                    Text(
                        text = "Mantén organizada la atención veterinaria",
                        color = HistorialCafeLight,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        // RECORDATORIOS MÉDICOS

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(
                        Screen.RecordatoriosMedicosMascota.createRoute(
                            mascotaId
                        )
                    )
                },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = HistorialCafe
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsActive,
                    contentDescription = null,
                    tint = HistorialCream,
                    modifier = Modifier.size(32.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = "Recordatorios médicos",
                        color = HistorialCream,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Consulta citas, vacunas y próximos cuidados",
                        color = HistorialCafeLight,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            text = "Categorías médicas",
            color = HistorialCream,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.padding(vertical = 6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categoriasMedicas) { categoria ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(
                                Screen.DetalleHistorialMedicoMascota.createRoute(
                                    mascotaId,
                                    categoria.tipoRegistro
                                )
                            )
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = HistorialGlass
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = categoria.icono,
                            contentDescription = null,
                            tint = HistorialCafeLight,
                            modifier = Modifier.size(30.dp)
                        )

                        Text(
                            text = categoria.titulo,
                            color = HistorialCream,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = categoria.descripcion,
                            color = HistorialCafeLight,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
