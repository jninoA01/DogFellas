package com.dogfellas.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.ui.viewmodel.AtencionMedicaViewModel
import data.models.Medicamento

// ── MISMO ADN VISUAL QUE EMERGENCIAS ─────────────────────────────────
private val DarkBg      = Color(0xFF080D0D) // Fondo ultra oscuro
private val MedBlue     = Color(0xFF00D2FF) // El "AlertRed" pero en Cian
private val Cream       = Color(0xFFF5E6CA) // El color crema de tus textos
private val GlassWhite  = Color(0x12FFFFFF)
private val GlassBorder = Color(0x26FFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentosScreen(
    navController: NavHostController,
    viewModel: AtencionMedicaViewModel = viewModel()
) {
    var medicamentoDetalle by remember { mutableStateOf<Medicamento?>(null) }

    // Sincronización al iniciar
    LaunchedEffect(Unit) {
        viewModel.cargarMedicamentos()
    }

    val filtrados = viewModel.medicamentosFiltrados()

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // --- EL AURA (Igual a Emergencias pero Cian) ---
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = 150.dp, y = (-100).dp)
                .blur(120.dp)
                .background(MedBlue.copy(0.12f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("GUÍA FARMACÉUTICA", color = Cream, fontWeight = FontWeight.Black, fontSize = 16.sp) },
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

                // --- CHIPS DE FILTRO (Estilo Premium) ---
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

                if (viewModel.cargandoMedicamentos) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedBlue)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filtrados) { med ->
                            // --- MISMA TARJETA QUE EMERGENCIAS ---
                            TarjetaMedicamentoPremium(med) { medicamentoDetalle = med }
                        }
                    }
                }
            }
        }

        // --- DIÁLOGO (Misma estructura que Emergencias) ---
        medicamentoDetalle?.let { med ->
            DetalleMedicamentoDialog(med) { medicamentoDetalle = null }
        }
    }
}

@Composable
fun TarjetaMedicamentoPremium(medicamento: Medicamento, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = GlassWhite,
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MedBlue.copy(0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(medicamento.icono, fontSize = 24.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(medicamento.nombre, color = Cream, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(medicamento.categoria.uppercase(), color = MedBlue, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }

            Icon(Icons.Rounded.ChevronRight, null, tint = Cream.copy(0.3f))
        }
    }
}

@Composable
fun DetalleMedicamentoDialog(med: Medicamento, onCerrar: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color(0xFF0F1A1A), // Fondo oscuro
        shape = RoundedCornerShape(28.dp),
        confirmButton = {
            Button(
                onClick = onCerrar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MedBlue)
            ) { Text("ENTENDIDO", fontWeight = FontWeight.Bold, color = DarkBg) }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(med.icono, fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Text(med.nombre, color = Cream, fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("PARA QUÉ SIRVE", color = MedBlue, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    Text(med.descripcion, color = Color.White.copy(0.9f), fontSize = 14.sp)
                }

                Column(modifier = Modifier.background(Color.Black.copy(0.2f), RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Text("DOSIS RECOMENDADA", color = Cream, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(med.dosis, color = Color.White, fontSize = 14.sp)
                }

                // --- BOTÓN WHATSAPP (Misma estética que el bloque de veterinario en emergencias) ---
                OutlinedButton(
                    onClick = {
                        val numero = "573000000000" // Tu número real aquí
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$numero&text=Hola! Consulto por: ${med.nombre}")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MedBlue.copy(0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Chat, null, tint = MedBlue, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("CONSULTAR DISPONIBILIDAD", color = MedBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    )
}