package com.dogfellas.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.ui.viewmodel.MascotasViewModel
import data.models.Mascota

// ── PALETA PREMIUM ──────────────────────────────────────────────────
private val DarkBg      = Color(0xFF1B0F08)
private val Cafe        = Color(0xFF6B3F1F)
private val CafeLight   = Color(0xFFD7B899)
private val Cream       = Color(0xFFF5E6CA)
private val GlassWhite  = Color(0x1AFFFFFF)
private val GlassBorder = Color(0x33FFFFFF)
private val Verde       = Color(0xFF81C784)
private val Rojo        = Color(0xFFE57373)

private fun especieEmoji(especie: String) = when (especie.lowercase()) {
    "gato" -> "🐱"; "conejo" -> "🐰"; "ave" -> "🐦"
    "pez"  -> "🐟"; "reptil" -> "🦎"; else  -> "🐶"
}

@Composable
fun MascotasScreen(
    navController: NavHostController,
    viewModel: MascotasViewModel = viewModel()
) {
    var mascotaEditando    by remember { mutableStateOf<Mascota?>(null) }
    var mostrarFormulario  by remember { mutableStateOf(false) }
    var mascotaAEliminar   by remember { mutableStateOf<Mascota?>(null) }
    var busqueda           by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.cargarMascotas() }

    val mascotasFiltradas by remember {
        derivedStateOf {
            if (busqueda.isBlank()) viewModel.mascotas.toList()
            else viewModel.mascotas.filter {
                it.nombre.contains(busqueda, ignoreCase = true) ||
                        it.tipo.contains(busqueda, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Box(
            modifier = Modifier.size(450.dp).offset(x = 200.dp, y = (-150).dp)
                .blur(160.dp).background(Cafe.copy(0.35f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { mostrarFormulario = true },
                    containerColor = CafeLight,
                    contentColor = DarkBg,
                    shape = RoundedCornerShape(20.dp),
                    icon = { Icon(Icons.Rounded.Add, null) },
                    text = { Text("Nueva Mascota", fontWeight = FontWeight.ExtraBold) }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {

                // HEADER
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Mi Manada", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Cream)
                            Text("${viewModel.mascotas.size} integrantes", color = Cream.copy(0.5f))
                        }
                        IconButton(
                            onClick = { navController.navigate("home") },
                            modifier = Modifier.clip(CircleShape).background(GlassWhite)
                        ) {
                            Icon(Icons.Rounded.Home, null, tint = Cream)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        placeholder = { Text("Buscar integrante...", color = Cream.copy(0.4f)) },
                        leadingIcon = { Icon(Icons.Rounded.Search, null, tint = Cream.copy(0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(100.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GlassWhite,
                            unfocusedContainerColor = GlassWhite,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GlassBorder,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

                if (mascotasFiltradas.isEmpty() && !viewModel.cargando) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay resultados 🐾", color = Cream.copy(0.4f))
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(mascotasFiltradas, key = { it.id }) { mascota ->
                            TarjetaMascotaPremium(
                                mascota = mascota,
                                onEditar = { mascotaEditando = mascota },
                                onEliminar = { mascotaAEliminar = mascota }
                            )
                        }
                        item { Spacer(Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        MascotaDialog(
            titulo = "Nueva Mascota",
            mascotaInicial = null,
            onGuardar = { viewModel.agregarMascota(it) { mostrarFormulario = false } },
            onCancelar = { mostrarFormulario = false }
        )
    }

    mascotaEditando?.let { m ->
        MascotaDialog(
            titulo = "Editar Mascota",
            mascotaInicial = m,
            onGuardar = { viewModel.actualizarMascota(it) { mascotaEditando = null } },
            onCancelar = { mascotaEditando = null }
        )
    }

    mascotaAEliminar?.let { m ->
        AlertDialog(
            onDismissRequest = { mascotaAEliminar = null },
            containerColor = Color(0xFF2D1B12),
            title = { Text("Confirmar eliminación", color = Cream) },
            text = { Text("¿Deseas eliminar a ${m.nombre} de tu manada?", color = Cream.copy(0.7f)) },
            confirmButton = {
                Button(onClick = { viewModel.eliminarMascota(m.id); mascotaAEliminar = null }, colors = ButtonDefaults.buttonColors(containerColor = Rojo)) {
                    Text("Eliminar")
                }
            },
            dismissButton = { TextButton(onClick = { mascotaAEliminar = null }) { Text("Cancelar", color = Cream) } }
        )
    }
}

@Composable
fun TarjetaMascotaPremium(mascota: Mascota, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
            .background(GlassWhite).border(1.dp, GlassBorder, RoundedCornerShape(30.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(60.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(CafeLight, Cafe))),
                    contentAlignment = Alignment.Center
                ) { Text(especieEmoji(mascota.tipo), fontSize = 30.sp) }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(mascota.nombre, color = Cream, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(mascota.raza, color = Cream.copy(0.6f), fontSize = 14.sp)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (mascota.vacunado) Verde.copy(0.15f) else Rojo.copy(0.15f)
                ) {
                    Text(
                        if (mascota.vacunado) "VACUNADO" else "PENDIENTE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Black,
                        color = if (mascota.vacunado) Verde else Rojo
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChipPremium("${mascota.edad} años")
                InfoChipPremium(mascota.sexo)
                InfoChipPremium("${mascota.peso} kg")
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlassWhite)
                ) {
                    Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(16.dp), tint = Cream)
                    Spacer(Modifier.width(8.dp)); Text("Editar", color = Cream)
                }
                Button(
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Rojo.copy(0.12f))
                ) {
                    Icon(Icons.Rounded.Delete, null, modifier = Modifier.size(16.dp), tint = Rojo)
                    Spacer(Modifier.width(8.dp)); Text("Borrar", color = Rojo)
                }
            }
        }
    }
}

@Composable
fun InfoChipPremium(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp), color = Color.White.copy(0.05f),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, color = Cream.copy(0.8f))
    }
}

@Composable
fun MascotaDialog(
    titulo: String,
    mascotaInicial: Mascota?,
    onGuardar: (Mascota) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(mascotaInicial?.nombre ?: "") }
    var tipo by remember { mutableStateOf(mascotaInicial?.tipo ?: "") }
    var raza by remember { mutableStateOf(mascotaInicial?.raza ?: "") }
    var edad by remember { mutableStateOf(mascotaInicial?.edad?.toString() ?: "") }
    var sexo by remember { mutableStateOf(mascotaInicial?.sexo ?: "Macho") }
    var color by remember { mutableStateOf(mascotaInicial?.color ?: "") }
    var peso by remember { mutableStateOf(mascotaInicial?.peso?.toString() ?: "") }
    var vacunado by remember { mutableStateOf(mascotaInicial?.vacunado ?: false) }
    var descripcion by remember { mutableStateOf(mascotaInicial?.descripcion ?: "") }

    // VALIDACIÓN: Todos los campos obligatorios deben estar llenos
    val esFormularioValido = nombre.isNotBlank() &&
            tipo.isNotBlank() &&
            raza.isNotBlank() &&
            edad.isNotBlank() &&
            peso.isNotBlank()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = CafeLight,
        unfocusedLabelColor = Cream.copy(0.4f),
        focusedBorderColor = CafeLight,
        unfocusedBorderColor = GlassBorder
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = Color(0xFF2D1B12),
        title = { Text(titulo, color = Cream, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {

                // Nombre
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre de la mascota *") },
                    modifier = Modifier.fillMaxWidth(), colors = textFieldColors
                )

                // Sexo
                Column {
                    Text("Sexo", color = Cream.copy(0.6f), fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth().height(42.dp).padding(top = 4.dp)) {
                        listOf("Macho", "Hembra").forEach { opcion ->
                            val selected = sexo == opcion
                            Box(
                                modifier = Modifier.weight(1f).fillMaxHeight()
                                    .background(if (selected) CafeLight else GlassWhite,
                                        if(opcion == "Macho") RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                                        else RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                                    .clickable { sexo = opcion }.border(1.dp, GlassBorder),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(opcion, color = if (selected) DarkBg else Cream, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Especie y Raza
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Especie *") }, modifier = Modifier.weight(1f), colors = textFieldColors, placeholder = { Text("Ej: Perro", fontSize = 12.sp) })
                    OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
                }

                // Edad y Peso
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = edad, onValueChange = { if(it.all { c -> c.isDigit() }) edad = it }, label = { Text("Edad *") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = textFieldColors)
                    OutlinedTextField(value = peso, onValueChange = { peso = it }, label = { Text("Peso (kg) *") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), colors = textFieldColors)
                }

                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Notas o descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2, colors = textFieldColors)

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { vacunado = !vacunado }) {
                    Checkbox(checked = vacunado, onCheckedChange = { vacunado = it }, colors = CheckboxDefaults.colors(checkedColor = Verde, uncheckedColor = Cream.copy(0.4f)))
                    Text("¿Tiene esquema de vacunación al día?", color = Cream, fontSize = 14.sp)
                }

                if (!esFormularioValido) {
                    Text("* Los campos con asterisco son obligatorios", color = Rojo.copy(0.8f), fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = esFormularioValido,
                onClick = {
                    onGuardar(Mascota(
                        id = mascotaInicial?.id ?: "",
                        nombre = nombre, tipo = tipo, raza = raza,
                        edad = edad.toIntOrNull() ?: 0, sexo = sexo,
                        color = color, peso = peso.toDoubleOrNull() ?: 0.0,
                        vacunado = vacunado, descripcion = descripcion
                    ))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CafeLight,
                    disabledContainerColor = GlassWhite
                )
            ) {
                Text("Confirmar", color = if(esFormularioValido) DarkBg else Cream.copy(0.3f), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar", color = Cream) } }
    )
}