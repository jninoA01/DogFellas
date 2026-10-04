package com.dogfellas.app.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.components.DogFellasHeader
import com.dogfellas.app.ui.viewmodel.AnimalReportadoViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.GeoPoint
import java.io.File

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportarAnimalScreen(
    navController: NavHostController,
    viewModel: AnimalReportadoViewModel = viewModel()
) {

    val context = androidx.compose.ui.platform.LocalContext.current

    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val reporteExitoso by viewModel.reporteExitoso.collectAsState()

    // ============================================================
    // ESTADOS
    // ============================================================

    var especie by remember { mutableStateOf("") }
    var condicion by remember { mutableStateOf("") }
    var urgencia by remember { mutableStateOf("Media") }
    var descripcion by remember { mutableStateOf("") }

    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var fotoCamaraUri by remember { mutableStateOf<Uri?>(null) }

    var ubicacion by remember { mutableStateOf<GeoPoint?>(null) }

    var cargandoUbicacion by remember {
        mutableStateOf(false)
    }

    var especieExpanded by remember {
        mutableStateOf(false)
    }

    var condicionExpanded by remember {
        mutableStateOf(false)
    }

    // ============================================================
    // OPCIONES
    // ============================================================

    val especies = listOf(
        "Perro",
        "Gato",
        "Otro"
    )

    val condiciones = listOf(
        "Aparentemente sano",
        "Herido",
        "Enfermo",
        "Maltratado",
        "Abandonado",
        "No se puede determinar"
    )

    // ============================================================
    // GALERÍA
    // ============================================================

    val selectorGaleria =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                fotoUri = uri
            }
        }

    // ============================================================
    // CREAR URI PARA CÁMARA
    // ============================================================

    fun crearUriParaCamara(): Uri {

        val archivo = File.createTempFile(
            "animal_",
            ".jpg",
            context.cacheDir
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }

    // ============================================================
    // CÁMARA
    // ============================================================

    val camaraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { exitoso ->

            if (exitoso && fotoCamaraUri != null) {

                fotoUri = fotoCamaraUri

            } else {

                Toast.makeText(
                    context,
                    "No se pudo tomar la fotografía.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // ============================================================
    // PERMISO DE CÁMARA
    // ============================================================

    val permisoCamara =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { permitido ->

            if (permitido) {

                try {

                    val uri = crearUriParaCamara()

                    fotoCamaraUri = uri

                    camaraLauncher.launch(uri)

                } catch (e: Exception) {

                    Toast.makeText(
                        context,
                        "Error al abrir la cámara: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } else {

                Toast.makeText(
                    context,
                    "Debes permitir el acceso a la cámara.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // ============================================================
    // UBICACIÓN
    // ============================================================

    val permisosUbicacion =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->

            val permitido =
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (permitido) {

                cargandoUbicacion = true

                val locationClient =
                    LocationServices.getFusedLocationProviderClient(
                        context
                    )

                locationClient
                    .getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    )
                    .addOnSuccessListener { location ->

                        if (location != null) {

                            ubicacion = GeoPoint(
                                location.latitude,
                                location.longitude
                            )

                        } else {

                            Toast.makeText(
                                context,
                                "No se pudo obtener la ubicación.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        cargandoUbicacion = false
                    }
                    .addOnFailureListener { error ->

                        cargandoUbicacion = false

                        Toast.makeText(
                            context,
                            "Error de ubicación: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }

    // ============================================================
    // REPORTE EXITOSO
    // ============================================================

    LaunchedEffect(reporteExitoso) {

        if (reporteExitoso) {

            kotlinx.coroutines.delay(1500)

            viewModel.limpiarEstado()

            navController.navigate(
                Screen.AnimalesCercanos.route
            ) {
                popUpTo(
                    Screen.ReportarAnimal.route
                ) {
                    inclusive = true
                }
            }
        }
    }

    // ============================================================
    // PALETA
    // ============================================================

    val brownDark = Color(0xFF2D1B12)
    val brown = Color(0xFF4E342E)
    val brownMedium = Color(0xFF6D4C41)
    val brownLight = Color(0xFF8D6E63)

    val cream = Color(0xFFF0DDB0)
    val beige = Color(0xFFD4A96A)
    val whiteCream = Color(0xFFF8F3EE)

    val transparentCream = Color(0x18F0DDB0)
    val softCream = Color(0x22F0DDB0)

    // ============================================================
    // PANTALLA
    // ============================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        brownDark,
                        brown,
                        brownMedium,
                        brownLight
                    )
                )
            )
    ) {

        // ========================================================
        // HEADER
        // ========================================================

        DogFellasHeader(
            showBackButton = true,
            onHomeClick = {

                navController.navigate(
                    Screen.Home.route
                ) {
                    popUpTo(
                        Screen.Home.route
                    ) {
                        inclusive = false
                    }
                }
            }
        )

        // ========================================================
        // CONTENIDO
        // ========================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ====================================================
            // ENCABEZADO PRINCIPAL
            // ====================================================

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0x44F0DDB0),
                                    Color(0x22D4A96A)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Pets,
                        contentDescription = null,
                        tint = cream,
                        modifier = Modifier.size(35.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Reportar animal",
                        color = cream,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Ayuda a un animal que necesita atención.",
                        color = beige,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ====================================================
            // TARJETA INFORMATIVA
            // ====================================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x20F0DDB0)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0x22F0DDB0)
                )
            ) {

                Row(
                    modifier = Modifier.padding(17.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(
                                RoundedCornerShape(14.dp)
                            )
                            .background(
                                Color(0x33F0DDB0)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.Pets,
                            contentDescription = null,
                            tint = cream,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Tu reporte puede ayudar",
                            color = cream,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "La información que proporciones ayudará a identificar la situación del animal y facilitar su atención.",
                            color = Color(0xFFEADBC8),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(26.dp)
            )

            // ====================================================
            // FOTOGRAFÍA
            // ====================================================

            SectionTitle(
                icon = Icons.Rounded.AddAPhoto,
                title = "Fotografía",
                subtitle = "Una imagen ayuda a identificar al animal."
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (fotoUri != null) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(23.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = transparentCream
                    )
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {

                        AsyncImage(
                            model = fotoUri,
                            contentDescription = "Fotografía del animal",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(
                                    RoundedCornerShape(23.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )

                        // Capa visual

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xCC2D1B12)
                                        )
                                    )
                                )
                        )

                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(11.dp)
                                .size(43.dp)
                                .clickable {
                                    fotoUri = null
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xDD2D1B12)
                        ) {

                            Box(
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "Eliminar fotografía",
                                    tint = Color.White,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 13.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = cream,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(7.dp)
                            )

                            Text(
                                text = "Fotografía seleccionada",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {

                    // ============================================
                    // CÁMARA
                    // ============================================

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .clickable {

                                val tienePermiso =
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED

                                if (tienePermiso) {

                                    try {

                                        val uri =
                                            crearUriParaCamara()

                                        fotoCamaraUri = uri

                                        camaraLauncher.launch(uri)

                                    } catch (e: Exception) {

                                        Toast.makeText(
                                            context,
                                            "No se pudo abrir la cámara: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                } else {

                                    permisoCamara.launch(
                                        Manifest.permission.CAMERA
                                    )
                                }
                            },
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = transparentCream
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0x25F0DDB0)
                        )
                    ) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(
                                        RoundedCornerShape(17.dp)
                                    )
                                    .background(
                                        softCream
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Rounded.AddAPhoto,
                                    contentDescription = null,
                                    tint = cream,
                                    modifier = Modifier.size(29.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(9.dp)
                            )

                            Text(
                                text = "Tomar foto",
                                color = cream,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(3.dp)
                            )

                            Text(
                                text = "Usar cámara",
                                color = beige,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // ============================================
                    // GALERÍA
                    // ============================================

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .clickable {

                                selectorGaleria.launch(
                                    "image/*"
                                )
                            },
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = transparentCream
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0x25F0DDB0)
                        )
                    ) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(
                                        RoundedCornerShape(17.dp)
                                    )
                                    .background(
                                        softCream
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Rounded.Pets,
                                    contentDescription = null,
                                    tint = cream,
                                    modifier = Modifier.size(29.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(9.dp)
                            )

                            Text(
                                text = "Galería",
                                color = cream,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(3.dp)
                            )

                            Text(
                                text = "Elegir imagen",
                                color = beige,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                Text(
                    text = "Puedes tomar una foto ahora o seleccionar una existente.",
                    color = beige,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(
                        horizontal = 4.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // ====================================================
            // INFORMACIÓN
            // ====================================================

            SectionTitle(
                icon = Icons.Rounded.Pets,
                title = "Información del animal",
                subtitle = "Cuéntanos lo que puedas observar."
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ====================================================
            // ESPECIE
            // ====================================================

            Text(
                text = "Especie",
                color = cream,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            ExposedDropdownMenuBox(
                expanded = especieExpanded,
                onExpandedChange = {
                    especieExpanded = !especieExpanded
                }
            ) {

                OutlinedTextField(
                    value = especie,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = "Selecciona una especie"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Rounded.Pets,
                            contentDescription = null,
                            tint = beige
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(19.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = whiteCream,
                        unfocusedTextColor = whiteCream,
                        focusedBorderColor = cream,
                        unfocusedBorderColor = Color(0x668D6E63),
                        focusedLabelColor = cream,
                        unfocusedLabelColor = beige,
                        cursorColor = cream
                    )
                )

                ExposedDropdownMenu(
                    expanded = especieExpanded,
                    onDismissRequest = {
                        especieExpanded = false
                    }
                ) {

                    especies.forEach { opcion ->

                        DropdownMenuItem(
                            text = {
                                Text(opcion)
                            },
                            onClick = {

                                especie = opcion
                                especieExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(19.dp)
            )

            // ====================================================
            // CONDICIÓN
            // ====================================================

            Text(
                text = "Estado del animal",
                color = cream,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            ExposedDropdownMenuBox(
                expanded = condicionExpanded,
                onExpandedChange = {
                    condicionExpanded = !condicionExpanded
                }
            ) {

                OutlinedTextField(
                    value = condicion,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = "¿Cómo se encuentra?"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = beige
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(19.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = whiteCream,
                        unfocusedTextColor = whiteCream,
                        focusedBorderColor = cream,
                        unfocusedBorderColor = Color(0x668D6E63),
                        focusedLabelColor = cream,
                        unfocusedLabelColor = beige,
                        cursorColor = cream
                    )
                )

                ExposedDropdownMenu(
                    expanded = condicionExpanded,
                    onDismissRequest = {
                        condicionExpanded = false
                    }
                ) {

                    condiciones.forEach { opcion ->

                        DropdownMenuItem(
                            text = {
                                Text(opcion)
                            },
                            onClick = {

                                condicion = opcion
                                condicionExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            // ====================================================
            // URGENCIA
            // ====================================================

            Text(
                text = "Nivel de urgencia",
                color = cream,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Selecciona qué tan urgente consideras la situación.",
                color = beige,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(11.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                listOf(
                    "Baja",
                    "Media",
                    "Alta"
                ).forEach { nivel ->

                    val seleccionado =
                        urgencia == nivel

                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            urgencia = nivel
                        },
                        modifier = Modifier.weight(1f),
                        label = {

                            Text(
                                text = nivel,
                                fontSize = 13.sp,
                                fontWeight =
                                    if (seleccionado) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }
                            )
                        },
                        leadingIcon =
                            if (seleccionado) {

                                {
                                    Icon(
                                        imageVector =
                                            Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        modifier =
                                            Modifier.size(17.dp)
                                    )
                                }

                            } else {
                                null
                            },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                containerColor =
                                    Color(0x18F0DDB0),
                                labelColor =
                                    Color(0xFFEADBC8),
                                selectedContainerColor =
                                    cream,
                                selectedLabelColor =
                                    brownDark
                            ),
                        border =
                            FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = seleccionado,
                                borderColor = Color(0x338D6E63),
                                selectedBorderColor = cream
                            )
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            // ====================================================
            // DESCRIPCIÓN
            // ====================================================

            Text(
                text = "¿Qué observaste?",
                color = cream,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Cuéntanos dónde lo viste y qué estaba sucediendo.",
                color = beige,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = {

                    Text(
                        text = "Ejemplo: Lo encontré cerca del parque, parece estar herido de una pata...",
                        color = Color(0x99EADBC8),
                        lineHeight = 18.sp
                    )
                },
                maxLines = 5,
                shape = RoundedCornerShape(19.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = whiteCream,
                    unfocusedTextColor = whiteCream,
                    focusedBorderColor = cream,
                    unfocusedBorderColor = Color(0x668D6E63),
                    focusedLabelColor = cream,
                    unfocusedLabelColor = beige,
                    cursorColor = cream
                )
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // ====================================================
            // UBICACIÓN
            // ====================================================

            SectionTitle(
                icon = Icons.Rounded.LocationOn,
                title = "Ubicación",
                subtitle = "Indica dónde se encuentra el animal."
            )

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = transparentCream
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0x25F0DDB0)
                )
            ) {

                Column(
                    modifier = Modifier.padding(17.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(49.dp)
                                .clip(
                                    RoundedCornerShape(15.dp)
                                )
                                .background(
                                    softCream
                                ),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = cream,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    if (ubicacion != null) {
                                        "Ubicación registrada"
                                    } else {
                                        "Ubicación pendiente"
                                    },
                                color = cream,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    if (ubicacion != null) {
                                        "La ubicación se asociará al reporte."
                                    } else {
                                        "Necesitamos tu ubicación para localizarlo."
                                    },
                                color = beige,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    Button(
                        onClick = {

                            val tienePermiso =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED

                            if (tienePermiso) {

                                cargandoUbicacion = true

                                val locationClient =
                                    LocationServices
                                        .getFusedLocationProviderClient(
                                            context
                                        )

                                locationClient
                                    .getCurrentLocation(
                                        Priority.PRIORITY_HIGH_ACCURACY,
                                        null
                                    )
                                    .addOnSuccessListener { location ->

                                        if (location != null) {

                                            ubicacion = GeoPoint(
                                                location.latitude,
                                                location.longitude
                                            )

                                        } else {

                                            Toast.makeText(
                                                context,
                                                "No se pudo obtener la ubicación.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        cargandoUbicacion = false
                                    }
                                    .addOnFailureListener { error ->

                                        cargandoUbicacion = false

                                        Toast.makeText(
                                            context,
                                            "Error de ubicación: ${error.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                            } else {

                                permisosUbicacion.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x33F0DDB0),
                            contentColor = cream
                        )
                    ) {

                        if (cargandoUbicacion) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = cream,
                                strokeWidth = 2.dp
                            )

                            Spacer(
                                modifier = Modifier.width(9.dp)
                            )

                            Text(
                                "Obteniendo ubicación..."
                            )

                        } else {

                            Icon(
                                imageVector =
                                    Icons.Rounded.LocationOn,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text =
                                    if (ubicacion != null) {
                                        "Actualizar ubicación"
                                    } else {
                                        "Usar mi ubicación"
                                    },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (ubicacion != null) {

                        Spacer(
                            modifier = Modifier.height(11.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(13.dp),
                            color = Color(0x1CF0DDB0)
                        ) {

                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 10.dp
                                ),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = cream,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(7.dp)
                                )

                                Text(
                                    text =
                                        "Ubicación lista para el reporte",
                                    color = cream,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            // ====================================================
            // BOTÓN REPORTAR
            // ====================================================

            Button(
                onClick = {

                    viewModel.guardarReporte(
                        especie = especie,
                        condicion = condicion,
                        urgencia = urgencia,
                        descripcion = descripcion,
                        fotoUri =
                            fotoUri?.toString() ?: "",
                        ubicacion = ubicacion,
                        context = context
                    )
                },
                enabled = !guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(19.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cream,
                    contentColor = brownDark,
                    disabledContainerColor =
                        Color(0x88F0DDB0)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp,
                    pressedElevation = 1.dp
                )
            ) {

                if (guardando) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = brownDark,
                        strokeWidth = 2.5.dp
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text = "ENVIANDO REPORTE...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                } else {

                    Icon(
                        imageVector = Icons.Rounded.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(9.dp)
                    )

                    Text(
                        text = "REPORTAR ANIMAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            // ====================================================
            // MENSAJE
            // ====================================================

            mensaje?.let { texto ->

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(19.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (reporteExitoso) {
                                Color(0xAA2D1B12)
                            } else {
                                Color(0xAA2D1B12)
                            }
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(39.dp)
                                .clip(
                                    RoundedCornerShape(12.dp)
                                )
                                .background(
                                    Color(0x25F0DDB0)
                                ),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    if (reporteExitoso) {
                                        Icons.Rounded.CheckCircle
                                    } else {
                                        Icons.Rounded.Warning
                                    },
                                contentDescription = null,
                                tint = cream,
                                modifier = Modifier.size(23.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(11.dp)
                        )

                        Text(
                            text = texto,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(38.dp)
            )
        }
    }
}


// ================================================================
// TÍTULO DE SECCIÓN
// ================================================================

@Composable
private fun SectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(
                    RoundedCornerShape(14.dp)
                )
                .background(
                    Color(0x22F0DDB0)
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFF0DDB0),
                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(11.dp)
        )

        Column {

            Text(
                text = title,
                color = Color(0xFFF0DDB0),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                color = Color(0xFFD4A96A),
                fontSize = 11.sp
            )
        }
    }
}