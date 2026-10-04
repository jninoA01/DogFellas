package com.dogfellas.app.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.ui.viewmodel.SolicitudAdopcionViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val BrownLight = Color(0xFF8D6E63)
private val Cream = Color(0xFFF0DDB0)
private val Beige = Color(0xFFD4A96A)
private val WhiteCream = Color(0xFFF8F3EE)

@Composable
fun SolicitudAdopcionScreen(
    navController: NavHostController,
    reporteId: String,
    viewModel: SolicitudAdopcionViewModel = viewModel()
) {
    val context = LocalContext.current

    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val solicitudCreada by viewModel.solicitudCreada.collectAsState()

    // ---------------------------------------------------------
    // DATOS DEL SOLICITANTE
    // ---------------------------------------------------------

    var nombreCompleto by remember { mutableStateOf("") }
    var tipoDocumento by remember { mutableStateOf("") }
    var numeroDocumento by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    // ---------------------------------------------------------
    // RESIDENCIA
    // ---------------------------------------------------------

    var ciudad by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var tipoVivienda by remember { mutableStateOf("") }
    var vivienda by remember { mutableStateOf("") }
    var autorizacionPropietario by remember { mutableStateOf("") }

    // ---------------------------------------------------------
    // HOGAR
    // ---------------------------------------------------------

    var conQuienVive by remember { mutableStateOf("") }
    var personasHogar by remember { mutableStateOf("") }
    var acuerdoFamilia by remember { mutableStateOf("") }

    // ---------------------------------------------------------
    // EXPERIENCIA CON ANIMALES
    // ---------------------------------------------------------

    var haTenidoMascotas by remember { mutableStateOf("") }
    var experienciaAnimales by remember { mutableStateOf("") }
    var mascotasActuales by remember { mutableStateOf("") }
    var cantidadMascotas by remember { mutableStateOf("") }
    var mascotasVacunadas by remember { mutableStateOf("") }
    var mascotasEsterilizadas by remember { mutableStateOf("") }
    var situacionMascotasAnteriores by remember { mutableStateOf("") }

    // ---------------------------------------------------------
    // CONDICIONES PARA EL ANIMAL
    // ---------------------------------------------------------

    var lugarAnimal by remember { mutableStateOf("") }
    var tiempoSolo by remember { mutableStateOf("") }
    var responsableAlimentacion by remember { mutableStateOf("") }
    var responsableGastos by remember { mutableStateOf("") }
    var capacidadVeterinaria by remember { mutableStateOf("") }

    // ---------------------------------------------------------
    // MOTIVACIÓN
    // ---------------------------------------------------------

    var motivo by remember { mutableStateOf("") }
    var porQueAnimal by remember { mutableStateOf("") }
    var expectativas by remember { mutableStateOf("") }

    // ---------------------------------------------------------
    // COMPROMISO
    // ---------------------------------------------------------

    var compromisoAdopcion by remember { mutableStateOf(false) }

    // ---------------------------------------------------------
    // DESPUÉS DE GUARDAR
    // ---------------------------------------------------------

    LaunchedEffect(solicitudCreada) {
        if (solicitudCreada) {
            delay(1500)
            viewModel.limpiarSolicitudCreada()
            navController.popBackStack()
        }
    }

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

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // =================================================
            // HEADER
            // =================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        if (!guardando) {
                            navController.popBackStack()
                        }
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Volver",
                        tint = WhiteCream
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Solicitud de adopción",
                        color = WhiteCream,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Conozcamos un poco sobre ti",
                        color = Cream,
                        fontSize = 13.sp
                    )
                }
            }

            // =================================================
            // CONTENIDO
            // =================================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(horizontal = 20.dp)
            ) {

                FormularioHeader()

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // =================================================
                // 1. DATOS DEL SOLICITANTE
                // =================================================

                SeccionFormulario(
                    numero = "1",
                    titulo = "Datos del solicitante"
                ) {

                    Campo(
                        titulo = "Nombre completo *",
                        valor = nombreCompleto,
                        onValorChange = {
                            nombreCompleto =
                                filtrarTextoNombre(it)
                        }
                    )

                    Selector(
                        titulo = "Tipo de documento *",
                        opciones = listOf(
                            "Cédula de ciudadanía",
                            "Cédula de extranjería",
                            "Otro"
                        ),
                        seleccion = tipoDocumento
                    ) {
                        tipoDocumento = it
                    }

                    Campo(
                        titulo = "Número de documento *",
                        valor = numeroDocumento,
                        onValorChange = {
                            numeroDocumento =
                                filtrarNumeros(it)
                        },
                        keyboardType =
                            KeyboardType.Number,
                        maxLength = 15
                    )

                    CampoFecha(
                        titulo = "Fecha de nacimiento *",
                        fecha = fechaNacimiento,
                        onClick = {

                            val calendario =
                                Calendar.getInstance()

                            DatePickerDialog(
                                context,
                                { _: DatePicker,
                                  year: Int,
                                  month: Int,
                                  day: Int ->

                                    val seleccion =
                                        Calendar.getInstance()

                                    seleccion.set(
                                        year,
                                        month,
                                        day
                                    )

                                    val formato =
                                        SimpleDateFormat(
                                            "dd/MM/yyyy",
                                            Locale.getDefault()
                                        )

                                    fechaNacimiento =
                                        formato.format(
                                            seleccion.time
                                        )
                                },
                                calendario.get(
                                    Calendar.YEAR
                                ),
                                calendario.get(
                                    Calendar.MONTH
                                ),
                                calendario.get(
                                    Calendar.DAY_OF_MONTH
                                )
                            ).apply {

                                // No permite fechas futuras
                                datePicker.maxDate =
                                    System.currentTimeMillis()

                            }.show()
                        }
                    )

                    Campo(
                        titulo = "Teléfono *",
                        valor = telefono,
                        onValorChange = {
                            telefono =
                                filtrarNumeros(it)
                        },
                        keyboardType =
                            KeyboardType.Phone,
                        maxLength = 15
                    )

                    Campo(
                        titulo = "Correo electrónico *",
                        valor = correo,
                        onValorChange = {
                            correo = it.trimStart()
                        },
                        keyboardType =
                            KeyboardType.Email
                    )
                }

                // =================================================
                // 2. RESIDENCIA
                // =================================================

                SeccionFormulario(
                    numero = "2",
                    titulo = "Información de residencia"
                ) {

                    Campo(
                        titulo = "Ciudad *",
                        valor = ciudad,
                        onValorChange = {
                            ciudad = it
                        }
                    )

                    Campo(
                        titulo = "Dirección *",
                        valor = direccion,
                        onValorChange = {
                            direccion = it
                        }
                    )

                    Selector(
                        titulo = "Tipo de vivienda *",
                        opciones = listOf(
                            "Casa",
                            "Apartamento",
                            "Finca",
                            "Otro"
                        ),
                        seleccion = tipoVivienda
                    ) {
                        tipoVivienda = it
                    }

                    Selector(
                        titulo = "La vivienda es *",
                        opciones = listOf(
                            "Propia",
                            "Arrendada",
                            "Familiar",
                            "Otra"
                        ),
                        seleccion = vivienda
                    ) {

                        vivienda = it

                        if (it != "Arrendada") {
                            autorizacionPropietario = ""
                        }
                    }

                    if (vivienda == "Arrendada") {

                        Selector(
                            titulo =
                                "¿Tiene autorización del propietario? *",
                            opciones = listOf(
                                "Sí",
                                "No"
                            ),
                            seleccion =
                                autorizacionPropietario
                        ) {
                            autorizacionPropietario = it
                        }
                    }
                }

                // =================================================
                // 3. HOGAR
                // =================================================

                SeccionFormulario(
                    numero = "3",
                    titulo = "Información del hogar"
                ) {

                    Campo(
                        titulo = "¿Con quién vive? *",
                        valor = conQuienVive,
                        onValorChange = {
                            conQuienVive = it
                        },
                        singleLine = false,
                        minLines = 2
                    )

                    Campo(
                        titulo =
                            "Número de personas en el hogar *",
                        valor = personasHogar,
                        onValorChange = {
                            personasHogar =
                                filtrarEnteroPositivo(it)
                        },
                        keyboardType =
                            KeyboardType.Number,
                        maxLength = 2
                    )

                    Selector(
                        titulo =
                            "¿Todos están de acuerdo con la adopción? *",
                        opciones = listOf(
                            "Sí",
                            "No"
                        ),
                        seleccion = acuerdoFamilia
                    ) {
                        acuerdoFamilia = it
                    }
                }

                // =================================================
                // 4. EXPERIENCIA
                // =================================================

                SeccionFormulario(
                    numero = "4",
                    titulo = "Experiencia con animales"
                ) {

                    Selector(
                        titulo =
                            "¿Ha tenido mascotas anteriormente? *",
                        opciones = listOf(
                            "Sí",
                            "No"
                        ),
                        seleccion = haTenidoMascotas
                    ) {
                        haTenidoMascotas = it
                    }

                    Campo(
                        titulo =
                            "Cuéntenos sobre su experiencia",
                        valor =
                            experienciaAnimales,
                        onValorChange = {
                            experienciaAnimales = it
                        },
                        singleLine = false,
                        minLines = 3
                    )

                    Selector(
                        titulo =
                            "¿Tiene mascotas actualmente? *",
                        opciones = listOf(
                            "Sí",
                            "No"
                        ),
                        seleccion = mascotasActuales
                    ) {

                        mascotasActuales = it

                        if (it == "No") {

                            cantidadMascotas = ""
                            mascotasVacunadas = ""
                            mascotasEsterilizadas = ""
                        }
                    }

                    // -----------------------------------------
                    // Solo aparece si tiene mascotas
                    // -----------------------------------------

                    if (mascotasActuales == "Sí") {

                        Campo(
                            titulo =
                                "Cantidad de mascotas actuales *",
                            valor =
                                cantidadMascotas,
                            onValorChange = {
                                cantidadMascotas =
                                    filtrarEnteroPositivo(
                                        it
                                    )
                            },
                            keyboardType =
                                KeyboardType.Number,
                            maxLength = 2
                        )

                        Selector(
                            titulo =
                                "¿Están vacunadas? *",
                            opciones = listOf(
                                "Sí",
                                "No",
                                "No aplica"
                            ),
                            seleccion =
                                mascotasVacunadas
                        ) {
                            mascotasVacunadas = it
                        }

                        Selector(
                            titulo =
                                "¿Están esterilizadas? *",
                            opciones = listOf(
                                "Sí",
                                "No",
                                "No aplica"
                            ),
                            seleccion =
                                mascotasEsterilizadas
                        ) {
                            mascotasEsterilizadas = it
                        }
                    }

                    Campo(
                        titulo =
                            "Situación de mascotas anteriores",
                        valor =
                            situacionMascotasAnteriores,
                        onValorChange = {
                            situacionMascotasAnteriores =
                                it
                        },
                        singleLine = false,
                        minLines = 3
                    )
                }

                // =================================================
                // 5. CONDICIONES PARA EL ANIMAL
                // =================================================

                SeccionFormulario(
                    numero = "5",
                    titulo = "Condiciones para el animal"
                ) {

                    Selector(
                        titulo =
                            "¿Dónde permanecerá principalmente?",
                        opciones = listOf(
                            "Dentro de la vivienda",
                            "Patio o espacio exterior",
                            "Finca",
                            "Otro"
                        ),
                        seleccion = lugarAnimal
                    ) {
                        lugarAnimal = it
                    }

                    Selector(
                        titulo =
                            "¿Cuánto tiempo permanecerá solo?",
                        opciones = listOf(
                            "Menos de 2 horas",
                            "2 a 4 horas",
                            "4 a 8 horas",
                            "Más de 8 horas"
                        ),
                        seleccion = tiempoSolo
                    ) {
                        tiempoSolo = it
                    }

                    Campo(
                        titulo =
                            "¿Quién será responsable de alimentarlo?",
                        valor =
                            responsableAlimentacion,
                        onValorChange = {
                            responsableAlimentacion =
                                it
                        }
                    )

                    Campo(
                        titulo =
                            "¿Quién asumirá los gastos?",
                        valor =
                            responsableGastos,
                        onValorChange = {
                            responsableGastos = it
                        }
                    )

                    Selector(
                        titulo =
                            "¿Puede asumir atención veterinaria?",
                        opciones = listOf(
                            "Sí",
                            "No"
                        ),
                        seleccion =
                            capacidadVeterinaria
                    ) {
                        capacidadVeterinaria = it
                    }
                }

                // =================================================
                // 6. MOTIVACIÓN
                // =================================================

                SeccionFormulario(
                    numero = "6",
                    titulo = "Motivación"
                ) {

                    Campo(
                        titulo =
                            "¿Cuál es su principal motivo para adoptar? *",
                        valor = motivo,
                        onValorChange = {
                            motivo = it
                        },
                        singleLine = false,
                        minLines = 3
                    )

                    Campo(
                        titulo =
                            "¿Por qué eligió este animal? *",
                        valor = porQueAnimal,
                        onValorChange = {
                            porQueAnimal = it
                        },
                        singleLine = false,
                        minLines = 3
                    )

                    Campo(
                        titulo =
                            "¿Qué expectativas tiene de la adopción? *",
                        valor = expectativas,
                        onValorChange = {
                            expectativas = it
                        },
                        singleLine = false,
                        minLines = 3
                    )
                }

                // =================================================
                // 7. COMPROMISO
                // =================================================

                SeccionFormulario(
                    numero = "7",
                    titulo = "Compromiso de adopción"
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.Top
                    ) {

                        Checkbox(
                            checked =
                                compromisoAdopcion,
                            onCheckedChange = {
                                compromisoAdopcion =
                                    it
                            },
                            colors =
                                CheckboxDefaults.colors(
                                    checkedColor =
                                        Beige
                                )
                        )

                        Text(
                            text =
                                "Declaro que la información "
                                        + "suministrada es verdadera "
                                        + "y me comprometo a brindar "
                                        + "al animal alimentación, "
                                        + "atención veterinaria, "
                                        + "protección, afecto y "
                                        + "condiciones adecuadas "
                                        + "de vida.",
                            color = WhiteCream,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            modifier =
                                Modifier.padding(
                                    top = 12.dp
                                )
                        )
                    }
                }

                // =================================================
                // MENSAJE
                // =================================================

                if (!mensaje.isNullOrBlank()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(14.dp)
                            )
                            .background(
                                Color(0x332D1B12)
                            )
                            .padding(14.dp)
                    ) {

                        Text(
                            text = mensaje ?: "",
                            color = Color(0xFFFFCDD2),
                            fontSize = 14.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // =================================================
                // BOTÓN ENVIAR
                // =================================================

                Button(
                    onClick = {

                        viewModel.enviarSolicitud(

                            reporteId =
                                reporteId,

                            nombreCompleto =
                                nombreCompleto,

                            tipoDocumento =
                                tipoDocumento,

                            numeroDocumento =
                                numeroDocumento,

                            fechaNacimiento =
                                fechaNacimiento,

                            telefono =
                                telefono,

                            correo =
                                correo,

                            ciudad =
                                ciudad,

                            direccion =
                                direccion,

                            tipoVivienda =
                                tipoVivienda,

                            vivienda =
                                vivienda,

                            autorizacionPropietario =
                                autorizacionPropietario,

                            conQuienVive =
                                conQuienVive,

                            personasHogar =
                                personasHogar,

                            acuerdoFamilia =
                                acuerdoFamilia,

                            haTenidoMascotas =
                                haTenidoMascotas,

                            experienciaAnimales =
                                experienciaAnimales,

                            mascotasActuales =
                                mascotasActuales,

                            cantidadMascotas =
                                cantidadMascotas,

                            mascotasVacunadas =
                                mascotasVacunadas,

                            mascotasEsterilizadas =
                                mascotasEsterilizadas,

                            situacionMascotasAnteriores =
                                situacionMascotasAnteriores,

                            lugarAnimal =
                                lugarAnimal,

                            tiempoSolo =
                                tiempoSolo,

                            responsableAlimentacion =
                                responsableAlimentacion,

                            responsableGastos =
                                responsableGastos,

                            capacidadVeterinaria =
                                capacidadVeterinaria,

                            motivo =
                                motivo,

                            porQueAnimal =
                                porQueAnimal,

                            expectativas =
                                expectativas,

                            compromisoAdopcion =
                                compromisoAdopcion
                        )
                    },
                    enabled =
                        !guardando &&
                                !solicitudCreada,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape =
                        RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Beige,
                            contentColor =
                                BrownDark
                        )
                ) {

                    when {

                        guardando -> {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(24.dp),
                                strokeWidth = 3.dp,
                                color =
                                    BrownDark
                            )
                        }

                        solicitudCreada -> {

                            Icon(
                                imageVector =
                                    Icons.Rounded.CheckCircle,
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text(
                                text =
                                    "Solicitud registrada",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }

                        else -> {

                            Text(
                                text =
                                    "ENVIAR SOLICITUD",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(35.dp)
                )
            }
        }
    }
}

// =============================================================
// HEADER DEL FORMULARIO
// =============================================================

@Composable
private fun FormularioHeader() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(26.dp)
            )
            .background(
                Color(0x22FFFFFF)
            )
            .padding(22.dp)
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Rounded.Pets,
                contentDescription =
                    null,
                tint = Beige,
                modifier =
                    Modifier.size(48.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(16.dp)
            )

            Column {

                Text(
                    text =
                        "Adopción responsable",
                    color =
                        WhiteCream,
                    fontSize = 21.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        "La información será revisada por "
                                + "el responsable del proceso "
                                + "de adopción.",
                    color =
                        Cream,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

// =============================================================
// SECCIÓN
// =============================================================

@Composable
private fun SeccionFormulario(
    numero: String,
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp)
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(Beige),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = numero,
                    color = BrownDark,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )

            Text(
                text = titulo,
                color = WhiteCream,
                fontSize = 19.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Column(
            verticalArrangement =
                Arrangement.spacedBy(10.dp),
            content = contenido
        )
    }
}

// =============================================================
// CAMPO DE TEXTO
// =============================================================

@Composable
private fun Campo(
    titulo: String,
    valor: String,
    onValorChange: (String) -> Unit,
    keyboardType: KeyboardType =
        KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLength: Int? = null
) {

    OutlinedTextField(
        value = valor,

        onValueChange = { nuevo ->

            val resultado =
                if (maxLength != null) {
                    nuevo.take(maxLength)
                } else {
                    nuevo
                }

            onValorChange(resultado)
        },

        label = {
            Text(titulo)
        },

        modifier =
            Modifier.fillMaxWidth(),

        singleLine =
            singleLine,

        minLines =
            minLines,

        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    keyboardType
            ),

        colors =
            OutlinedTextFieldDefaults.colors(

                focusedBorderColor =
                    Beige,

                unfocusedBorderColor =
                    Color(0x88FFFFFF),

                focusedLabelColor =
                    Beige,

                unfocusedLabelColor =
                    Cream,

                focusedTextColor =
                    WhiteCream,

                unfocusedTextColor =
                    WhiteCream,

                cursorColor =
                    Beige
            ),

        shape =
            RoundedCornerShape(16.dp)
    )
}

// =============================================================
// FECHA
// =============================================================

@Composable
private fun CampoFecha(
    titulo: String,
    fecha: String,
    onClick: () -> Unit
) {

    OutlinedTextField(

        value = fecha,

        onValueChange = {},

        readOnly = true,

        label = {
            Text(titulo)
        },

        trailingIcon = {

            IconButton(
                onClick = onClick
            ) {

                Icon(
                    imageVector =
                        Icons.Rounded.CalendarMonth,
                    contentDescription =
                        "Seleccionar fecha",
                    tint = Beige
                )
            }
        },

        modifier =
            Modifier.fillMaxWidth(),

        colors =
            OutlinedTextFieldDefaults.colors(

                focusedBorderColor =
                    Beige,

                unfocusedBorderColor =
                    Color(0x88FFFFFF),

                focusedLabelColor =
                    Beige,

                unfocusedLabelColor =
                    Cream,

                focusedTextColor =
                    WhiteCream,

                unfocusedTextColor =
                    WhiteCream
            ),

        shape =
            RoundedCornerShape(16.dp)
    )
}

// =============================================================
// SELECTOR
// =============================================================

@Composable
private fun Selector(
    titulo: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit
) {

    var expandido by remember {
        mutableStateOf(false)
    }

    Box(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(

            value =
                seleccion,

            onValueChange = {},

            readOnly = true,

            label = {
                Text(titulo)
            },

            trailingIcon = {

                TextButton(
                    onClick = {
                        expandido =
                            !expandido
                    }
                ) {

                    Text(
                        text = "▼",
                        color = Beige
                    )
                }
            },

            modifier =
                Modifier.fillMaxWidth(),

            colors =
                OutlinedTextFieldDefaults.colors(

                    focusedBorderColor =
                        Beige,

                    unfocusedBorderColor =
                        Color(0x88FFFFFF),

                    focusedLabelColor =
                        Beige,

                    unfocusedLabelColor =
                        Cream,

                    focusedTextColor =
                        WhiteCream,

                    unfocusedTextColor =
                        WhiteCream
                ),

            shape =
                RoundedCornerShape(16.dp)
        )

        DropdownMenu(

            expanded =
                expandido,

            onDismissRequest = {
                expandido = false
            }
        ) {

            opciones.forEach { opcion ->

                DropdownMenuItem(

                    text = {
                        Text(opcion)
                    },

                    onClick = {

                        onSeleccion(opcion)

                        expandido =
                            false
                    }
                )
            }
        }
    }
}

// =============================================================
// FILTROS
// =============================================================

private fun filtrarNumeros(
    texto: String
): String {

    return texto.filter {
        it.isDigit()
    }
}

private fun filtrarEnteroPositivo(
    texto: String
): String {

    val limpio =
        texto.filter {
            it.isDigit()
        }

    if (limpio.isBlank()) {
        return ""
    }

    return try {

        val numero =
            limpio.toInt()

        if (numero > 0) {
            numero.toString()
        } else {
            ""
        }

    } catch (
        e: NumberFormatException
    ) {

        ""
    }
}

private fun filtrarTextoNombre(
    texto: String
): String {

    return texto.filter { caracter ->

        caracter.isLetter() ||
                caracter.isWhitespace() ||
                caracter == '.' ||
                caracter == '-'
    }
}