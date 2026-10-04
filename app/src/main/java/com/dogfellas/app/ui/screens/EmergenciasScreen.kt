package com.dogfellas.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.ui.viewmodel.AtencionMedicaViewModel
import data.models.Emergencia

// ── COLORES PREMIUM ──────────────────────────────────────────────────
private val DarkBg      = Color(0xFF1B0F08)
private val AlertRed    = Color(0xFFE53935)
private val AlertOrange = Color(0xFFFF9800)
private val AlertGreen  = Color(0xFF4CAF50)
private val Cream       = Color(0xFFF5E6CA)
private val GlassWhite  = Color(0x12FFFFFF)
private val GlassBorder = Color(0x26FFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergenciasScreen(
    navController: NavHostController,
    viewModel: AtencionMedicaViewModel = viewModel()
) {
    var emergenciaDetalle by remember { mutableStateOf<Emergencia?>(null) }

    // Sincronizar con el repositorio al iniciar
    LaunchedEffect(viewModel.filtroEspecie) {
        viewModel.cargarEmergencias()
    }

    // Usamos la lista que el ViewModel ya filtró
    val filtradas = viewModel.emergenciasFiltradas()

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // Efecto visual de fondo
        Box(modifier = Modifier.size(400.dp).offset(x = (-100).dp, y = (-100).dp).blur(120.dp).background(AlertRed.copy(0.1f), CircleShape))

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("GUÍA DE SOCORRO", color = Cream, fontWeight = FontWeight.Black, fontSize = 16.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Cream)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {

                // Chips de Filtro (Especies que maneja tu repo)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "Todos" suele no funcionar con whereArrayContains,
                    // así que el ViewModel debe manejar la lógica de "Todos" cargando la colección completa
                    items(viewModel.especiesFiltro) { especie ->
                        val seleccionado = viewModel.filtroEspecie == especie
                        FilterChip(
                            selected = seleccionado,
                            onClick = { viewModel.filtroEspecie = especie },
                            label = { Text(especie, color = if(seleccionado) DarkBg else Cream) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Cream,
                                containerColor = GlassWhite
                            ),
                            border = FilterChipDefaults.filterChipBorder(borderColor = GlassBorder, enabled = true, selected = seleccionado)
                        )
                    }
                }

                if (viewModel.cargandoEmergencias) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AlertRed)
                    }
                } else if (filtradas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🐾", fontSize = 40.sp)
                            Text("No se encontraron emergencias", color = Cream.copy(0.5f))
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filtradas, key = { it.id }) { em ->
                            TarjetaEmergenciaPremium(em) { emergenciaDetalle = em }
                        }
                    }
                }
            }
        }

        emergenciaDetalle?.let { em ->
            DetalleEmergenciaDialog(em) { emergenciaDetalle = null }
        }
    }
}

@Composable
fun TarjetaEmergenciaPremium(emergencia: Emergencia, onClick: () -> Unit) {
    val colorNivel = when(emergencia.nivel.lowercase()) {
        "grave", "critico" -> AlertRed
        "moderado" -> AlertOrange
        else -> AlertGreen
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = GlassWhite,
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).background(colorNivel.copy(0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emergencia.icono, fontSize = 24.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(emergencia.titulo, color = Cream, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(emergencia.nivel.uppercase(), color = colorNivel, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }

            Icon(Icons.Rounded.ChevronRight, null, tint = Cream.copy(0.3f))
        }
    }
}

@Composable
fun DetalleEmergenciaDialog(emergencia: Emergencia, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color(0xFF2D1B12),
        shape = RoundedCornerShape(28.dp),
        confirmButton = {
            Button(
                onClick = onCerrar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
            ) { Text("ENTENDIDO", fontWeight = FontWeight.Bold) }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emergencia.icono, fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Text(emergencia.titulo, color = Cream, fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Column {
                    Text("SÍNTOMAS COMUNES", color = AlertOrange, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    Text(emergencia.sintomas.joinToString(" • "), color = Color.White.copy(0.9f), fontSize = 14.sp)
                }

                Column(modifier = Modifier.background(Color.Black.copy(0.2f), RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Text("PRIMEROS PASOS", color = Cream, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(emergencia.queHacer, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
                }

                Column(modifier = Modifier.border(1.dp, AlertRed.copy(0.5f), RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Text("🚨 CUÁNDO IR AL VETERINARIO", color = AlertRed, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(emergencia.cuandoIrAlVet, color = Cream, fontSize = 13.sp)
                }
            }
        }
    )
}