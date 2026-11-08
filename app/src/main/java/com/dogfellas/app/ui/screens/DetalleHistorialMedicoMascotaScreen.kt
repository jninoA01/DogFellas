
package com.dogfellas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.ui.viewmodel.HistorialMedicoMascotaViewModel
import data.models.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DetalleBg = Color(0xFF1B0F08)
private val DetalleCafe = Color(0xFF6B3F1F)
private val DetalleCafeLight = Color(0xFFD7B899)
private val DetalleCream = Color(0xFFF5E6CA)
private val DetalleGlass = Color(0xFF2A1B12)

private data class CampoFormulario(
    val clave: String,
    val etiqueta: String
)

private fun tituloCategoria(tipo: String): String {
    return when (tipo) {
        "historial_medico" -> "Consultas e historial"
        "citas" -> "Citas veterinarias"
        "vacunaciones" -> "Vacunaciones"
        "medicamentos" -> "Medicamentos"
        "desparasitaciones" -> "Desparasitaciones"
        "emergencias" -> "Emergencias"
        else -> "Historial médico"
    }
}

private fun camposCategoria(tipo: String): List<CampoFormulario> {
    return when (tipo) {
        "historial_medico" -> listOf(
            CampoFormulario("veterinario", "Veterinario"),
            CampoFormulario("tipo", "Tipo de consulta"),
            CampoFormulario("diagnostico", "Diagnóstico"),
            CampoFormulario("tratamiento", "Tratamiento"),
            CampoFormulario("observaciones", "Observaciones")
        )

        "citas" -> listOf(
            CampoFormulario("veterinario", "Veterinario"),
            CampoFormulario("direccionVeterinaria", "Dirección de la veterinaria"),
            CampoFormulario("motivo", "Motivo de la cita"),
            CampoFormulario("estado", "Estado (PENDIENTE, REALIZADA o CANCELADA)"),
            CampoFormulario("observaciones", "Observaciones")
        )

        "vacunaciones" -> listOf(
            CampoFormulario("vacuna", "Nombre de la vacuna"),
            CampoFormulario("veterinario", "Veterinario"),
            CampoFormulario("observaciones", "Observaciones")
        )

        "medicamentos" -> listOf(
            CampoFormulario("nombre", "Nombre del medicamento"),
            CampoFormulario("dosis", "Dosis"),
            CampoFormulario("frecuencia", "Frecuencia"),
            CampoFormulario("veterinario", "Veterinario"),
            CampoFormulario("observaciones", "Observaciones")
        )

        "desparasitaciones" -> listOf(
            CampoFormulario("producto", "Producto utilizado"),
            CampoFormulario("veterinario", "Veterinario"),
            CampoFormulario("observaciones", "Observaciones")
        )

        "emergencias" -> listOf(
            CampoFormulario("motivo", "Motivo de la emergencia"),
            CampoFormulario("atencionRealizada", "Atención realizada"),
            CampoFormulario("veterinario", "Veterinario"),
            CampoFormulario("resultado", "Resultado"),
            CampoFormulario("observaciones", "Observaciones")
        )

        else -> emptyList()
    }
}

private fun formatearFecha(valor: Any?): String {
    val timestamp = when (valor) {
        is Long -> valor
        is Int -> valor.toLong()
        is Double -> valor.toLong()
        else -> return ""
    }

    return try {
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}

private fun obtenerLong(
    registro: Map<String, Any>,
    clave: String,
    predeterminado: Long = System.currentTimeMillis()
): Long {
    return when (val valor = registro[clave]) {
        is Long -> valor
        is Int -> valor.toLong()
        is Double -> valor.toLong()
        else -> predeterminado
    }
}

private fun obtenerLongOpcional(
    registro: Map<String, Any>,
    clave: String
): Long? {
    return when (val valor = registro[clave]) {
        is Long -> valor
        is Int -> valor.toLong()
        is Double -> valor.toLong()
        else -> null
    }
}

private fun construirModelo(
    tipo: String,
    valores: Map<String, String>,
    registroOriginal: Map<String, Any>?,
    fechaPrincipal: Long,
    fechaSecundaria: Long?
): Any {

    val id = registroOriginal?.get("id")?.toString() ?: ""

    val fechaCreacion = registroOriginal?.let {
        obtenerLong(it, "fechaCreacion")
    } ?: System.currentTimeMillis()

    return when (tipo) {

        "historial_medico" -> HistorialMedicoMascota(
            id = id,
            veterinario = valores["veterinario"].orEmpty(),
            fecha = fechaPrincipal,
            tipo = valores["tipo"].orEmpty(),
            diagnostico = valores["diagnostico"].orEmpty(),
            tratamiento = valores["tratamiento"].orEmpty(),
            observaciones = valores["observaciones"].orEmpty(),
            fechaCreacion = fechaCreacion
        )

        "citas" -> CitaVeterinariaMascota(
            id = id,
            veterinario = valores["veterinario"].orEmpty(),
            direccionVeterinaria = valores["direccionVeterinaria"].orEmpty(),
            fecha = fechaPrincipal,
            motivo = valores["motivo"].orEmpty(),
            estado = valores["estado"].orEmpty().uppercase(Locale.ROOT),
            observaciones = valores["observaciones"].orEmpty(),
            fechaCreacion = fechaCreacion
        )

        "vacunaciones" -> VacunacionMascota(
            id = id,
            vacuna = valores["vacuna"].orEmpty(),
            fecha = fechaPrincipal,
            proximaDosis = fechaSecundaria,
            veterinario = valores["veterinario"].orEmpty(),
            observaciones = valores["observaciones"].orEmpty(),
            fechaCreacion = fechaCreacion
        )

        "medicamentos" -> MedicamentoMascota(
            id = id,
            nombre = valores["nombre"].orEmpty(),
            dosis = valores["dosis"].orEmpty(),
            frecuencia = valores["frecuencia"].orEmpty(),
            fechaInicio = fechaPrincipal,
            fechaFin = fechaSecundaria,
            veterinario = valores["veterinario"].orEmpty(),
            observaciones = valores["observaciones"].orEmpty(),
            activo = if (fechaSecundaria != null) {
                fechaSecundaria >= fechaPrincipal
            } else {
                registroOriginal?.get("activo") as? Boolean ?: true
            },
            fechaCreacion = fechaCreacion
        )

        "desparasitaciones" -> DesparasitacionMascota(
            id = id,
            producto = valores["producto"].orEmpty(),
            fecha = fechaPrincipal,
            proximaFecha = fechaSecundaria,
            veterinario = valores["veterinario"].orEmpty(),
            observaciones = valores["observaciones"].orEmpty(),
            fechaCreacion = fechaCreacion
        )

        "emergencias" -> EmergenciaMascota(
            id = id,
            fecha = fechaPrincipal,
            motivo = valores["motivo"].orEmpty(),
            atencionRealizada = valores["atencionRealizada"].orEmpty(),
            veterinario = valores["veterinario"].orEmpty(),
            resultado = valores["resultado"].orEmpty(),
            observaciones = valores["observaciones"].orEmpty(),
            fechaCreacion = fechaCreacion
        )

        else -> throw IllegalArgumentException(
            "Categoría médica no válida."
        )
    }
}

private fun camposObligatorios(tipo: String): List<String> {
    return when (tipo) {
        "historial_medico" -> listOf("diagnostico")
        "citas" -> listOf("motivo")
        "vacunaciones" -> listOf("vacuna")
        "medicamentos" -> listOf("nombre", "dosis", "frecuencia")
        "desparasitaciones" -> listOf("producto")
        "emergencias" -> listOf("motivo")
        else -> emptyList()
    }
}

private fun obtenerEtiquetaFechaSecundaria(tipo: String): String {
    return when (tipo) {
        "vacunaciones" -> "Fecha de próxima dosis"
        "medicamentos" -> "Fecha de finalización"
        "desparasitaciones" -> "Próxima desparasitación"
        else -> "Fecha adicional"
    }
}

private fun necesitaFechaSecundaria(tipo: String): Boolean {
    return tipo in listOf(
        "vacunaciones",
        "medicamentos",
        "desparasitaciones"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleHistorialMedicoMascotaScreen(
    navController: NavHostController,
    mascotaId: String,
    tipoRegistro: String,
    historialViewModel: HistorialMedicoMascotaViewModel = viewModel()
) {
    val registros by historialViewModel.registros.collectAsState()
    val cargando by historialViewModel.cargando.collectAsState()
    val mensaje by historialViewModel.mensaje.collectAsState()

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var registroAEliminar by remember {
        mutableStateOf<String?>(null)
    }

    var registroAEditar by remember {
        mutableStateOf<Map<String, Any>?>(null)
    }

    var errorFormulario by remember {
        mutableStateOf<String?>(null)
    }

    var fechaPrincipal by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    var fechaSecundaria by remember {
        mutableStateOf<Long?>(null)
    }

    var mostrarSelectorFecha by remember {
        mutableStateOf(false)
    }

    var selectorFechaSecundaria by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val campos = remember(tipoRegistro) {
        camposCategoria(tipoRegistro)
    }

    val valores = remember(tipoRegistro) {
        mutableStateMapOf<String, String>()
    }

    LaunchedEffect(mascotaId, tipoRegistro) {
        historialViewModel.cargarRegistros(
            mascotaId,
            tipoRegistro
        )
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            historialViewModel.limpiarMensaje()
        }
    }

    val titulo = tituloCategoria(tipoRegistro)

    Scaffold(
        containerColor = DetalleBg,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DetalleCafe,
                    contentColor = DetalleCream
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        titulo,
                        color = DetalleCream,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Text(
                            "Volver",
                            color = DetalleCafeLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DetalleBg
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    valores.clear()
                    registroAEditar = null
                    errorFormulario = null

                    fechaPrincipal = System.currentTimeMillis()
                    fechaSecundaria = null

                    mostrarFormulario = true
                },
                containerColor = DetalleCafe,
                contentColor = DetalleCream
            ) {
                Text(
                    "+",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DetalleBg)
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                "Registros médicos",
                color = DetalleCream,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "Consulta y administra la información médica de tu mascota.",
                color = DetalleCafeLight
            )

            Spacer(modifier = Modifier.height(18.dp))

            when {
                cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = DetalleCafeLight
                        )
                    }
                }

                registros.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Aún no hay registros",
                                color = DetalleCream,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Presiona el botón + para agregar el primero.",
                                color = DetalleCafeLight
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(
                            items = registros,
                            key = {
                                it["id"]?.toString()
                                    ?: it.hashCode().toString()
                            }
                        ) { registro ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = DetalleGlass
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {

                                    val fecha = formatearFecha(
                                        registro["fecha"]
                                            ?: registro["fechaInicio"]
                                    )

                                    if (fecha.isNotBlank()) {
                                        Text(
                                            "Fecha: $fecha",
                                            color = DetalleCafeLight,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    registro.forEach { (clave, valor) ->

                                        if (
                                            clave !in listOf(
                                                "id",
                                                "fechaCreacion",
                                                "fecha",
                                                "fechaInicio"
                                            ) &&
                                            valor.toString().isNotBlank()
                                        ) {
                                            val etiqueta = when (clave) {
                                                "veterinario" -> "Veterinario"
                                                "tipo" -> "Tipo"
                                                "diagnostico" -> "Diagnóstico"
                                                "tratamiento" -> "Tratamiento"
                                                "observaciones" -> "Observaciones"
                                                "motivo" -> "Motivo"
                                                "estado" -> "Estado"
                                                "vacuna" -> "Vacuna"
                                                "nombre" -> "Medicamento"
                                                "dosis" -> "Dosis"
                                                "frecuencia" -> "Frecuencia"
                                                "producto" -> "Producto"
                                                "atencionRealizada" -> "Atención realizada"
                                                "resultado" -> "Resultado"
                                                "direccionVeterinaria" -> "Dirección"
                                                "activo" -> "Activo"
                                                "proximaDosis" -> "Próxima dosis"
                                                "proximaFecha" -> "Próxima fecha"
                                                "fechaFin" -> "Fecha de finalización"
                                                else -> clave
                                            }

                                            val contenido = when (clave) {
                                                "proximaDosis",
                                                "proximaFecha",
                                                "fechaFin" -> formatearFecha(valor)

                                                "activo" -> if (valor == true) {
                                                    "Sí"
                                                } else {
                                                    "No"
                                                }

                                                else -> valor.toString()
                                            }

                                            if (contenido.isNotBlank()) {
                                                Text(
                                                    "$etiqueta: $contenido",
                                                    color = DetalleCream,
                                                    modifier = Modifier.padding(
                                                        vertical = 3.dp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TextButton(
                                            onClick = {
                                                valores.clear()

                                                campos.forEach { campo ->
                                                    valores[campo.clave] =
                                                        registro[campo.clave]
                                                            ?.toString()
                                                            ?: ""
                                                }

                                                fechaPrincipal = when (tipoRegistro) {
                                                    "medicamentos" -> obtenerLong(
                                                        registro,
                                                        "fechaInicio"
                                                    )
                                                    else -> obtenerLong(
                                                        registro,
                                                        "fecha"
                                                    )
                                                }

                                                fechaSecundaria = when (tipoRegistro) {
                                                    "vacunaciones" -> obtenerLongOpcional(
                                                        registro,
                                                        "proximaDosis"
                                                    )
                                                    "medicamentos" -> obtenerLongOpcional(
                                                        registro,
                                                        "fechaFin"
                                                    )
                                                    "desparasitaciones" -> obtenerLongOpcional(
                                                        registro,
                                                        "proximaFecha"
                                                    )
                                                    else -> null
                                                }

                                                registroAEditar = registro
                                                errorFormulario = null
                                                mostrarFormulario = true
                                            }
                                        ) {
                                            Text(
                                                "Editar",
                                                color = DetalleCafeLight
                                            )
                                        }

                                        TextButton(
                                            onClick = {
                                                registroAEliminar =
                                                    registro["id"]?.toString()
                                            },
                                            enabled = !registro["id"]
                                                ?.toString()
                                                .isNullOrBlank()
                                        ) {
                                            Text(
                                                "Eliminar",
                                                color = Color(0xFFE57373)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // FORMULARIO PARA CREAR Y EDITAR

    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = {
                mostrarFormulario = false
                registroAEditar = null
                errorFormulario = null
                mostrarSelectorFecha = false
            },
            containerColor = DetalleGlass,
            title = {
                Text(
                    if (registroAEditar == null) {
                        "Nuevo registro"
                    } else {
                        "Editar registro"
                    },
                    color = DetalleCream,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(campos) { campo ->

                            OutlinedTextField(
                                value = valores[campo.clave] ?: "",
                                onValueChange = {
                                    valores[campo.clave] = it
                                    errorFormulario = null
                                },
                                label = {
                                    Text(campo.etiqueta)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = DetalleCream,
                                    unfocusedTextColor = DetalleCream,
                                    focusedBorderColor = DetalleCafeLight,
                                    unfocusedBorderColor = DetalleCafeLight,
                                    focusedLabelColor = DetalleCafeLight,
                                    unfocusedLabelColor = DetalleCafeLight,
                                    cursorColor = DetalleCream
                                )
                            )
                        }

                        item {
                            Text(
                                "Fecha principal",
                                color = DetalleCream,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedButton(
                                onClick = {
                                    datePickerState.selectedDateMillis =
                                        fechaPrincipal
                                    selectorFechaSecundaria = false
                                    mostrarSelectorFecha = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = DetalleCafeLight
                                )
                            ) {
                                Text(
                                    formatearFecha(fechaPrincipal)
                                )
                            }
                        }

                        if (necesitaFechaSecundaria(tipoRegistro)) {
                            item {
                                Text(
                                    obtenerEtiquetaFechaSecundaria(tipoRegistro),
                                    color = DetalleCream,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis =
                                            fechaSecundaria
                                                ?: System.currentTimeMillis()

                                        selectorFechaSecundaria = true
                                        mostrarSelectorFecha = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = DetalleCafeLight
                                    )
                                ) {
                                    Text(
                                        fechaSecundaria?.let {
                                            formatearFecha(it)
                                        } ?: "Seleccionar fecha"
                                    )
                                }

                                if (fechaSecundaria != null) {
                                    TextButton(
                                        onClick = {
                                            fechaSecundaria = null
                                        }
                                    ) {
                                        Text(
                                            "Quitar fecha",
                                            color = Color(0xFFE57373)
                                        )
                                    }
                                }
                            }
                        }

                        if (tipoRegistro == "medicamentos") {
                            item {
                                Text(
                                    "Si no seleccionas una fecha de finalización, el medicamento quedará sin fecha de fin.",
                                    color = DetalleCafeLight,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        if (errorFormulario != null) {
                            item {
                                Text(
                                    errorFormulario!!,
                                    color = Color(0xFFE57373),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !cargando,
                    onClick = {

                        val camposRequeridos =
                            camposObligatorios(tipoRegistro)

                        val faltantes = camposRequeridos.filter {
                            valores[it].isNullOrBlank()
                        }

                        if (faltantes.isNotEmpty()) {
                            errorFormulario =
                                "Completa los campos obligatorios: " +
                                campos.filter {
                                    it.clave in faltantes
                                }.joinToString(", ") {
                                    it.etiqueta
                                }
                        } else {
                            try {
                                val datos = construirModelo(
                                    tipoRegistro,
                                    valores.toMap(),
                                    registroAEditar,
                                    fechaPrincipal,
                                    fechaSecundaria
                                )

                                val registroExistente = registroAEditar

                                if (registroExistente != null) {
                                    val id = registroExistente["id"]
                                        ?.toString()

                                    if (id.isNullOrBlank()) {
                                        errorFormulario =
                                            "No se encontró el identificador del registro."
                                    } else {
                                        historialViewModel.actualizarRegistro(
                                            mascotaId,
                                            tipoRegistro,
                                            id,
                                            datos
                                        )

                                        mostrarFormulario = false
                                        registroAEditar = null
                                    }
                                } else {
                                    historialViewModel.guardarRegistro(
                                        mascotaId,
                                        tipoRegistro,
                                        datos
                                    )

                                    mostrarFormulario = false
                                }

                            } catch (e: Exception) {
                                errorFormulario =
                                    e.message ?: "No se pudo procesar el registro."
                            }
                        }
                    }
                ) {
                    Text(
                        if (cargando) "Guardando..." else "Guardar",
                        color = DetalleCafeLight
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarFormulario = false
                        registroAEditar = null
                        errorFormulario = null
                    }
                ) {
                    Text(
                        "Cancelar",
                        color = DetalleCafeLight
                    )
                }
            }
        )
    }

    // SELECTOR DE FECHAS

    if (mostrarSelectorFecha) {
        DatePickerDialog(
            onDismissRequest = {
                mostrarSelectorFecha = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            if (selectorFechaSecundaria) {
                                fechaSecundaria = it
                            } else {
                                fechaPrincipal = it
                            }
                        }

                        mostrarSelectorFecha = false
                    }
                ) {
                    Text(
                        "Aceptar",
                        color = DetalleCafeLight
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarSelectorFecha = false
                    }
                ) {
                    Text(
                        "Cancelar",
                        color = DetalleCafeLight
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = DetalleGlass
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = DetalleGlass,
                    titleContentColor = DetalleCream,
                    headlineContentColor = DetalleCream,
                    weekdayContentColor = DetalleCafeLight,
                    subheadContentColor = DetalleCafeLight,
                    yearContentColor = DetalleCream,
                    currentYearContentColor = DetalleCafeLight,
                    selectedYearContentColor = DetalleBg,
                    selectedYearContainerColor = DetalleCafeLight,
                    dayContentColor = DetalleCream,
                    selectedDayContentColor = DetalleBg,
                    selectedDayContainerColor = DetalleCafeLight,
                    todayContentColor = DetalleCafeLight,
                    todayDateBorderColor = DetalleCafeLight
                )
            )
        }
    }

    // CONFIRMACIÓN DE ELIMINACIÓN

    registroAEliminar?.let { id ->

        AlertDialog(
            onDismissRequest = {
                registroAEliminar = null
            },
            containerColor = DetalleGlass,
            title = {
                Text(
                    "Eliminar registro",
                    color = DetalleCream
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar este registro médico?",
                    color = DetalleCream
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        historialViewModel.eliminarRegistro(
                            mascotaId,
                            tipoRegistro,
                            id
                        )
                        registroAEliminar = null
                    }
                ) {
                    Text(
                        "Eliminar",
                        color = Color(0xFFE57373)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        registroAEliminar = null
                    }
                ) {
                    Text(
                        "Cancelar",
                        color = DetalleCafeLight
                    )
                }
            }
        )
    }
}
