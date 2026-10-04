package com.dogfellas.app.ui.screens

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.model.EstadoAnimal
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.viewmodel.AnimalReporteUI
import com.dogfellas.app.ui.viewmodel.AnimalesCercanosViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

// ============================================================
// COLORES DOGFELLAS
// ============================================================

private val BrownDark = Color(0xFF2D1B12)
private val Brown = Color(0xFF4E342E)
private val BrownMedium = Color(0xFF6D4C41)
private val BrownLight = Color(0xFF8D6E63)
private val Cream = Color(0xFFF0DDB0)
private val Beige = Color(0xFFD4A96A)
private val WhiteCream = Color(0xFFF8F3EE)

// ============================================================
// TIPO DE ORDENAMIENTO
// ============================================================

private enum class OrdenReportes {
    CERCANIA,
    URGENCIA,
    FECHA
}

// ============================================================
// FILTRO DE ESTADO
// ============================================================

private enum class FiltroEstado {
    TODOS,
    REPORTADO,
    EN_ATENCION,
    RESCATADO,
    EN_RECUPERACION,
    EN_ADOPCION,
    ADOPTADO
}

// ============================================================
// PANTALLA PRINCIPAL
// ============================================================

@Composable
fun AnimalesCercanosScreen(
    navController: NavHostController,
    viewModel: AnimalesCercanosViewModel = viewModel()
) {

    val reportes by viewModel.reportes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val ubicacionUsuario by viewModel.ubicacionUsuario.collectAsState()

    val context = LocalContext.current

    var mostrarMapa by remember {
        mutableStateOf(true)
    }

    var reporteSeleccionado by remember {
        mutableStateOf<AnimalReporteUI?>(null)
    }

    var ordenSeleccionado by remember {
        mutableStateOf(OrdenReportes.CERCANIA)
    }

    var filtroEstado by remember {
        mutableStateOf(FiltroEstado.TODOS)
    }

    var mostrarFiltroEstado by remember {
        mutableStateOf(false)
    }

    // ========================================================
    // UBICACIÓN INICIAL
    // ========================================================

    val ubicacionInicial = remember(ubicacionUsuario) {

        ubicacionUsuario?.let { location ->

            LatLng(
                location.latitude,
                location.longitude
            )

        } ?: LatLng(
            4.7110,
            -74.0721
        )
    }

    // ========================================================
    // CÁMARA
    // ========================================================

    val cameraPositionState =
        rememberCameraPositionState {

            position =
                CameraPosition.fromLatLngZoom(
                    ubicacionInicial,
                    13f
                )
        }

    // ========================================================
    // CARGAR REPORTES
    // ========================================================

    LaunchedEffect(Unit) {
        viewModel.cargarReportes()
    }

    // ========================================================
    // ACTUALIZAR CÁMARA
    // ========================================================

    LaunchedEffect(ubicacionUsuario) {

        ubicacionUsuario?.let { location ->

            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ),
                    14f
                )
            )
        }
    }

    // ========================================================
    // FILTRAR REPORTES
    // ========================================================

    val reportesFiltrados = remember(
        reportes,
        filtroEstado
    ) {

        when (filtroEstado) {

            FiltroEstado.TODOS ->
                reportes

            FiltroEstado.REPORTADO ->
                reportes.filter {
                    it.animal.estado ==
                            EstadoAnimal.REPORTADO.valor
                }

            FiltroEstado.EN_ATENCION ->
                reportes.filter {
                    it.animal.estado ==
                            EstadoAnimal.EN_ATENCION.valor
                }

            FiltroEstado.RESCATADO ->
                reportes.filter {
                    it.animal.estado ==
                            EstadoAnimal.RESCATADO.valor
                }

            FiltroEstado.EN_RECUPERACION ->
                reportes.filter {
                    it.animal.estado ==
                            EstadoAnimal.EN_RECUPERACION.valor
                }

            FiltroEstado.EN_ADOPCION ->
                reportes.filter {
                    it.animal.estado ==
                            EstadoAnimal.EN_ADOPCION.valor
                }

            FiltroEstado.ADOPTADO ->
                reportes.filter {
                    it.animal.estado ==
                            EstadoAnimal.ADOPTADO.valor
                }
        }
    }

    // ========================================================
    // SCAFFOLD
    // ========================================================

    Scaffold(

        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrownDark)
                    .padding(
                        horizontal = 8.dp,
                        vertical = 10.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Volver",

                        tint =
                            Cream
                    )
                }

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Animales cerca de mí",

                        color =
                            Cream,

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            20.sp
                    )

                    Text(
                        text =
                            "Encuentra y ayuda a un animal",

                        color =
                            Cream.copy(alpha = 0.72f),

                        fontSize =
                            11.sp
                    )
                }

                Card(
                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Brown.copy(alpha = 0.75f)
                        )
                ) {

                    IconButton(
                        onClick = {
                            viewModel.cargarReportes()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Refresh,

                            contentDescription =
                                "Actualizar",

                            tint =
                                Cream
                        )
                    }
                }
            }
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
            ) {

                // =================================================
                // TARJETA PRINCIPAL
                // =================================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(22.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Cream
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation =
                                4.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(17.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(
                                    RoundedCornerShape(
                                        17.dp
                                    )
                                )
                                .background(
                                    Beige.copy(
                                        alpha = 0.48f
                                    )
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Pets,

                                contentDescription =
                                    null,

                                tint =
                                    BrownDark,

                                modifier =
                                    Modifier.size(31.dp)
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.width(13.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Ayuda a un animal",

                                fontSize =
                                    18.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    BrownDark
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    if (
                                        ubicacionUsuario != null
                                    ) {

                                        if (
                                            filtroEstado ==
                                            FiltroEstado.TODOS
                                        ) {

                                            "${reportesFiltrados.size} reportes encontrados"

                                        } else {

                                            "${reportesFiltrados.size} animales en este estado"
                                        }

                                    } else {

                                        "Buscando tu ubicación..."
                                    },

                                fontSize =
                                    13.sp,

                                color =
                                    Brown
                            )
                        }

                        if (ubicacionUsuario != null) {

                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            14.dp
                                        )
                                    )
                                    .background(
                                        BrownDark
                                    )
                                    .padding(
                                        horizontal = 11.dp,
                                        vertical = 8.dp
                                    )
                            ) {

                                Text(
                                    text =
                                        "${reportesFiltrados.size}",

                                    color =
                                        Cream,

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize =
                                        16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                // =================================================
                // FILTRO DE ESTADO
                // =================================================

                Text(
                    text =
                        "Filtrar por estado",

                    color =
                        Cream,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        15.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )

                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(

                        onClick = {
                            mostrarFiltroEstado =
                                true
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(15.dp),

                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor =
                                    Cream
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Pets,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(7.dp)
                        )

                        Text(
                            text =
                                obtenerNombreFiltroEstado(
                                    filtroEstado
                                ),

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }

                    DropdownMenu(

                        expanded =
                            mostrarFiltroEstado,

                        onDismissRequest = {
                            mostrarFiltroEstado =
                                false
                        }
                    ) {

                        FiltroEstadoItem(
                            texto =
                                "Todos los estados",

                            onClick = {
                                filtroEstado =
                                    FiltroEstado.TODOS

                                mostrarFiltroEstado =
                                    false
                            }
                        )

                        FiltroEstadoItem(
                            texto =
                                "Reportado",

                            onClick = {
                                filtroEstado =
                                    FiltroEstado.REPORTADO

                                mostrarFiltroEstado =
                                    false
                            }
                        )

                        FiltroEstadoItem(
                            texto =
                                "En atención",

                            onClick = {
                                filtroEstado =
                                    FiltroEstado.EN_ATENCION

                                mostrarFiltroEstado =
                                    false
                            }
                        )

                        FiltroEstadoItem(
                            texto =
                                "Rescatado",

                            onClick = {
                                filtroEstado =
                                    FiltroEstado.RESCATADO

                                mostrarFiltroEstado =
                                    false
                            }
                        )

                        FiltroEstadoItem(
                            texto =
                                "En recuperación",

                            onClick = {
                                filtroEstado =
                                    FiltroEstado.EN_RECUPERACION

                                mostrarFiltroEstado =
                                    false
                            }
                        )

                        FiltroEstadoItem(
                            texto =
                                "En adopción",

                            onClick = {
                                filtroEstado =
                                    FiltroEstado.EN_ADOPCION

                                mostrarFiltroEstado =
                                    false
                            }
                        )

                        FiltroEstadoItem(
                            texto =
                                "Adoptado",

                            onClick = {
                                filtroEstado =
                                    FiltroEstado.ADOPTADO

                                mostrarFiltroEstado =
                                    false
                            }
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                // =================================================
                // ORDENAR
                // =================================================

                Text(
                    text =
                        "Ordenar reportes",

                    color =
                        Cream,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        15.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        ),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    FilterChip(

                        selected =
                            ordenSeleccionado ==
                                    OrdenReportes.CERCANIA,

                        onClick = {

                            ordenSeleccionado =
                                OrdenReportes.CERCANIA

                            viewModel.ordenarPorCercania()
                        },

                        label = {
                            Text(
                                text =
                                    "Más cercanos",

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.LocationOn,

                                contentDescription =
                                    null,

                                modifier =
                                    Modifier.size(17.dp)
                            )
                        },

                        colors =
                            coloresChipDogFellas()
                    )

                    FilterChip(

                        selected =
                            ordenSeleccionado ==
                                    OrdenReportes.URGENCIA,

                        onClick = {

                            ordenSeleccionado =
                                OrdenReportes.URGENCIA

                            viewModel.ordenarPorUrgencia()
                        },

                        label = {
                            Text(
                                text =
                                    "Urgencia",

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Warning,

                                contentDescription =
                                    null,

                                modifier =
                                    Modifier.size(17.dp)
                            )
                        },

                        colors =
                            coloresChipDogFellas()
                    )

                    FilterChip(

                        selected =
                            ordenSeleccionado ==
                                    OrdenReportes.FECHA,

                        onClick = {

                            ordenSeleccionado =
                                OrdenReportes.FECHA

                            viewModel.ordenarPorFecha()
                        },

                        label = {
                            Text(
                                text =
                                    "Más recientes",

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        },

                        colors =
                            coloresChipDogFellas()
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(13.dp)
                )

                // =================================================
                // MAPA / LISTA
                // =================================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(17.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                BrownDark.copy(
                                    alpha = 0.35f
                                )
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp),

                        horizontalArrangement =
                            Arrangement.Center
                    ) {

                        FilterChip(

                            selected =
                                mostrarMapa,

                            onClick = {
                                mostrarMapa =
                                    true
                            },

                            label = {
                                Text(
                                    text =
                                        "Mapa",

                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            },

                            leadingIcon = {

                                Icon(
                                    imageVector =
                                        Icons.Default.Map,

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier.size(17.dp)
                                )
                            },

                            colors =
                                coloresChipDogFellas()
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        FilterChip(

                            selected =
                                !mostrarMapa,

                            onClick = {
                                mostrarMapa =
                                    false
                            },

                            label = {
                                Text(
                                    text =
                                        "Lista",

                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            },

                            leadingIcon = {

                                Icon(
                                    imageVector =
                                        Icons.Default.Pets,

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier.size(17.dp)
                                )
                            },

                            colors =
                                coloresChipDogFellas()
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                // =================================================
                // CONTENIDO
                // =================================================

                when {

                    cargando -> {

                        LoadingAnimales()
                    }

                    mensaje != null -> {

                        ErrorContenido(
                            mensaje =
                                mensaje
                                    ?: "Error desconocido",

                            onReintentar = {
                                viewModel.cargarReportes()
                            }
                        )
                    }

                    reportesFiltrados.isEmpty() -> {

                        EmptyContenido(
                            filtroEstado =
                                filtroEstado
                        )
                    }

                    mostrarMapa -> {

                        MapaAnimales(

                            reportes =
                                reportesFiltrados,

                            ubicacionUsuario =
                                ubicacionUsuario,

                            cameraPositionState =
                                cameraPositionState,

                            onReporteSeleccionado = {
                                reporteSeleccionado =
                                    it
                            }
                        )
                    }

                    else -> {

                        ListaAnimales(

                            reportes =
                                reportesFiltrados,

                            onVerDetalle = {
                                    reporte ->

                                navegarADetalle(
                                    navController,
                                    reporte.animal.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // ========================================================
    // REPORTE SELECCIONADO
    // ========================================================

    reporteSeleccionado?.let { reporte ->

        AnimalSeleccionadoCard(

            reporte =
                reporte,

            onCerrar = {
                reporteSeleccionado =
                    null
            },

            onVerDetalle = {

                navegarADetalle(
                    navController,
                    reporte.animal.id
                )
            },

            onComoLlegar = {

                abrirGoogleMaps(
                    context,
                    reporte
                )
            }
        )
    }
}

// ============================================================
// COLORES DE CHIPS
// ============================================================

@Composable
private fun coloresChipDogFellas() =
    FilterChipDefaults.filterChipColors(

        containerColor =
            BrownDark.copy(alpha = 0.45f),

        labelColor =
            Cream,

        iconColor =
            Cream,

        selectedContainerColor =
            Beige,

        selectedLabelColor =
            BrownDark,

        selectedLeadingIconColor =
            BrownDark
    )

// ============================================================
// ITEM DEL FILTRO
// ============================================================

@Composable
private fun FiltroEstadoItem(
    texto: String,
    onClick: () -> Unit
) {

    DropdownMenuItem(

        text = {

            Text(
                text =
                    texto,

                color =
                    BrownDark,

                fontWeight =
                    FontWeight.Medium
            )
        },

        onClick =
            onClick
    )
}

// ============================================================
// LOADING
// ============================================================

@Composable
private fun LoadingAnimales() {

    Box(

        modifier =
            Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Card(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(22.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        BrownDark.copy(
                            alpha = 0.55f
                        )
                )
        ) {

            Column(

                modifier =
                    Modifier.padding(28.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(
                    color =
                        Cream
                )

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Cargando animales cercanos...",

                    color =
                        Cream,

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }
}

// ============================================================
// MAPA
// ============================================================

@Composable
private fun MapaAnimales(
    reportes: List<AnimalReporteUI>,
    ubicacionUsuario: Location?,
    cameraPositionState: CameraPositionState,
    onReporteSeleccionado:
        (AnimalReporteUI) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(
                RoundedCornerShape(22.dp)
            )
    ) {

        GoogleMap(

            modifier =
                Modifier.fillMaxSize(),

            cameraPositionState =
                cameraPositionState,

            properties =
                MapProperties(
                    isMyLocationEnabled =
                        ubicacionUsuario != null
                ),

            uiSettings =
                MapUiSettings(

                    zoomControlsEnabled =
                        true,

                    myLocationButtonEnabled =
                        true,

                    mapToolbarEnabled =
                        true,

                    compassEnabled =
                        true
                )
        ) {

            reportes.forEach { reporte ->

                val geoPoint =
                    reporte.animal.ubicacion

                if (geoPoint != null) {

                    val posicion =
                        LatLng(
                            geoPoint.latitude,
                            geoPoint.longitude
                        )

                    Marker(

                        state =
                            rememberMarkerState(
                                position =
                                    posicion
                            ),

                        title =
                            "${reporte.animal.especie} - " +
                                    obtenerNombreEstado(
                                        reporte.animal.estado
                                    ),

                        snippet =
                            reporte.animal.condicion,

                        onClick = {

                            onReporteSeleccionado(
                                reporte
                            )

                            false
                        }
                    )
                }
            }
        }

        // ====================================================
        // MI UBICACIÓN
        // ====================================================

        if (ubicacionUsuario != null) {

            Card(

                modifier = Modifier
                    .align(
                        Alignment.TopEnd
                    )
                    .padding(12.dp),

                shape =
                    RoundedCornerShape(15.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Cream
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation =
                            5.dp
                    )
            ) {

                IconButton(

                    onClick = {

                        val posicion =
                            LatLng(
                                ubicacionUsuario.latitude,
                                ubicacionUsuario.longitude
                            )

                        cameraPositionState.move(

                            CameraUpdateFactory
                                .newLatLngZoom(
                                    posicion,
                                    15f
                                )
                        )
                    }
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.MyLocation,

                        contentDescription =
                            "Mi ubicación",

                        tint =
                            BrownDark
                    )
                }
            }
        }

        // ====================================================
        // CONTADOR
        // ====================================================

        Card(

            modifier = Modifier
                .align(
                    Alignment.TopStart
                )
                .padding(12.dp),

            shape =
                RoundedCornerShape(15.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Cream
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        5.dp
                )
        ) {

            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 13.dp,
                        vertical = 8.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Pets,

                    contentDescription =
                        null,

                    tint =
                        BrownDark,

                    modifier =
                        Modifier.size(17.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(

                    text =
                        "${reportes.size} reportes",

                    color =
                        BrownDark,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        12.sp
                )
            }
        }
    }
}

// ============================================================
// LISTA
// ============================================================

@Composable
private fun ListaAnimales(
    reportes: List<AnimalReporteUI>,
    onVerDetalle:
        (AnimalReporteUI) -> Unit
) {

    LazyColumn(

        modifier =
            Modifier.fillMaxSize(),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        items(

            items =
                reportes,

            key = {
                it.animal.id
            }

        ) { reporte ->

            AnimalCard(

                reporte =
                    reporte,

                onVerDetalle = {
                    onVerDetalle(
                        reporte
                    )
                }
            )
        }
    }
}

// ============================================================
// TARJETA DE ANIMAL
// ============================================================

@Composable
private fun AnimalCard(
    reporte: AnimalReporteUI,
    onVerDetalle: () -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(21.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Cream
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    4.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(
                            RoundedCornerShape(
                                15.dp
                            )
                        )
                        .background(
                            Beige.copy(
                                alpha =
                                    0.45f
                            )
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Pets,

                        contentDescription =
                            null,

                        tint =
                            BrownDark,

                        modifier =
                            Modifier.size(29.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(

                        text =
                            reporte.animal.especie
                                .ifBlank {
                                    "Animal"
                                },

                        fontSize =
                            18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            BrownDark
                    )

                    Text(

                        text =
                            obtenerNombreEstado(
                                reporte.animal.estado
                            ),

                        color =
                            BrownMedium,

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(

                        text =
                            reporte.animal.condicion
                                .ifBlank {
                                    "Condición no indicada"
                                },

                        color =
                            Brown,

                        fontSize =
                            13.sp
                    )
                }

                UrgenciaBadge(
                    urgencia =
                        reporte.animal.urgencia
                )
            }

            // =================================================
            // DISTANCIA
            // =================================================

            if (
                reporte.distanciaMetros != null
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    modifier =
                        Modifier
                            .clip(
                                RoundedCornerShape(
                                    10.dp
                                )
                            )
                            .background(
                                Beige.copy(
                                    alpha = 0.22f
                                )
                            )
                            .padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.LocationOn,

                        contentDescription =
                            null,

                        tint =
                            BrownMedium,

                        modifier =
                            Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(5.dp)
                    )

                    Text(

                        text =
                            formatearDistancia(
                                reporte.distanciaMetros
                            ),

                        color =
                            BrownMedium,

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize =
                            12.sp
                    )
                }
            }

            // =================================================
            // DESCRIPCIÓN
            // =================================================

            if (
                reporte.animal.descripcion
                    .isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(

                    text =
                        reporte.animal.descripcion,

                    color =
                        Brown,

                    fontSize =
                        14.sp,

                    maxLines =
                        3
                )
            }

            Spacer(
                modifier =
                    Modifier.height(13.dp)
            )

            Button(

                onClick =
                    onVerDetalle,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),

                shape =
                    RoundedCornerShape(15.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            BrownDark,

                        contentColor =
                            Cream
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Pets,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(19.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        "Ver detalle",

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}

// ============================================================
// TARJETA DEL REPORTE SELECCIONADO
// ============================================================

@Composable
private fun AnimalSeleccionadoCard(
    reporte: AnimalReporteUI,
    onCerrar: () -> Unit,
    onVerDetalle: () -> Unit,
    onComoLlegar: () -> Unit
) {

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        Card(

            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .fillMaxWidth()
                .padding(12.dp),

            shape =
                RoundedCornerShape(25.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Cream
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        14.dp
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(
                                RoundedCornerShape(
                                    16.dp
                                )
                            )
                            .background(
                                Beige.copy(
                                    alpha =
                                        0.45f
                                )
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Pets,

                            contentDescription =
                                null,

                            tint =
                                BrownDark,

                            modifier =
                                Modifier.size(30.dp)
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(

                            text =
                                reporte.animal.especie
                                    .ifBlank {
                                        "Animal"
                                    },

                            fontSize =
                                20.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                BrownDark
                        )

                        Text(

                            text =
                                obtenerNombreEstado(
                                    reporte.animal.estado
                                ),

                            color =
                                BrownMedium,

                            fontWeight =
                                FontWeight.Bold,

                            fontSize =
                                13.sp
                        )
                    }

                    IconButton(
                        onClick =
                            onCerrar
                    ) {

                        Text(

                            text =
                                "✕",

                            fontSize =
                                20.sp,

                            color =
                                BrownDark,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(

                    text =
                        reporte.animal.condicion
                            .ifBlank {
                                "Condición no indicada"
                            },

                    color =
                        Brown,

                    fontSize =
                        14.sp
                )

                if (
                    reporte.distanciaMetros != null
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Row(
                        modifier =
                            Modifier
                                .clip(
                                    RoundedCornerShape(
                                        10.dp
                                    )
                                )
                                .background(
                                    Beige.copy(
                                        alpha = 0.22f
                                    )
                                )
                                .padding(
                                    horizontal = 9.dp,
                                    vertical = 5.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.LocationOn,

                            contentDescription =
                                null,

                            tint =
                                BrownMedium,

                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(5.dp)
                        )

                        Text(

                            text =
                                formatearDistancia(
                                    reporte.distanciaMetros
                                ),

                            color =
                                BrownMedium,

                            fontWeight =
                                FontWeight.Bold,

                            fontSize =
                                13.sp
                        )
                    }
                }

                if (
                    reporte.animal.descripcion
                        .isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(

                        text =
                            reporte.animal.descripcion,

                        color =
                            Brown,

                        maxLines =
                            3,

                        fontSize =
                            14.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(

                        onClick =
                            onVerDetalle,

                        modifier =
                            Modifier.weight(1f),

                        shape =
                            RoundedCornerShape(15.dp),

                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor =
                                    BrownDark
                            )
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Pets,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(5.dp)
                        )

                        Text(
                            text =
                                "Detalle",

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Button(

                        onClick =
                            onComoLlegar,

                        modifier =
                            Modifier.weight(1f),

                        shape =
                            RoundedCornerShape(15.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    BrownDark,

                                contentColor =
                                    Cream
                            )
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Directions,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(5.dp)
                        )

                        Text(
                            text =
                                "Cómo llegar",

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// NOMBRE DEL ESTADO
// ============================================================

private fun obtenerNombreEstado(
    estado: String
): String {

    return when (estado) {

        EstadoAnimal.REPORTADO.valor ->
            EstadoAnimal.REPORTADO.nombre

        EstadoAnimal.EN_ATENCION.valor ->
            EstadoAnimal.EN_ATENCION.nombre

        EstadoAnimal.RESCATADO.valor ->
            EstadoAnimal.RESCATADO.nombre

        EstadoAnimal.EN_RECUPERACION.valor ->
            EstadoAnimal.EN_RECUPERACION.nombre

        EstadoAnimal.EN_ADOPCION.valor ->
            EstadoAnimal.EN_ADOPCION.nombre

        EstadoAnimal.ADOPTADO.valor ->
            EstadoAnimal.ADOPTADO.nombre

        else ->
            "Estado no determinado"
    }
}

// ============================================================
// NOMBRE DEL FILTRO
// ============================================================

private fun obtenerNombreFiltroEstado(
    filtro: FiltroEstado
): String {

    return when (filtro) {

        FiltroEstado.TODOS ->
            "Todos los estados"

        FiltroEstado.REPORTADO ->
            "Reportado"

        FiltroEstado.EN_ATENCION ->
            "En atención"

        FiltroEstado.RESCATADO ->
            "Rescatado"

        FiltroEstado.EN_RECUPERACION ->
            "En recuperación"

        FiltroEstado.EN_ADOPCION ->
            "En adopción"

        FiltroEstado.ADOPTADO ->
            "Adoptado"
    }
}

// ============================================================
// BADGE DE URGENCIA
// ============================================================

@Composable
private fun UrgenciaBadge(
    urgencia: String
) {

    val backgroundColor = when (urgencia) {

        "Alta" ->
            BrownDark

        "Media" ->
            BrownMedium

        "Baja" ->
            Beige

        else ->
            BrownLight
    }

    val textColor =
        if (urgencia == "Baja") {
            BrownDark
        } else {
            Cream
        }

    Card(

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    backgroundColor
            )
    ) {

        Text(

            text =
                urgencia.uppercase(),

            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                ),

            color =
                textColor,

            fontSize =
                10.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}

// ============================================================
// ERROR
// ============================================================

@Composable
private fun ErrorContenido(
    mensaje: String,
    onReintentar: () -> Unit
) {

    Box(

        modifier =
            Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Card(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(22.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Cream
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        5.dp
                )
        ) {

            Column(

                modifier =
                    Modifier.padding(25.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(
                            RoundedCornerShape(
                                18.dp
                            )
                        )
                        .background(
                            Beige.copy(
                                alpha = 0.4f
                            )
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Warning,

                        contentDescription =
                            null,

                        tint =
                            BrownDark,

                        modifier =
                            Modifier.size(34.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(13.dp)
                )

                Text(

                    text =
                        "No se pudieron cargar los reportes",

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        BrownDark,

                    fontSize =
                        17.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(

                    text =
                        mensaje,

                    color =
                        Brown,

                    fontSize =
                        13.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(17.dp)
                )

                Button(

                    onClick =
                        onReintentar,

                    shape =
                        RoundedCornerShape(15.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                BrownDark,

                            contentColor =
                                Cream
                        )
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Refresh,

                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            "Reintentar",

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ============================================================
// SIN RESULTADOS
// ============================================================

@Composable
private fun EmptyContenido(
    filtroEstado: FiltroEstado
) {

    Box(

        modifier =
            Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Card(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(22.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Cream
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        5.dp
                )
        ) {

            Column(

                modifier =
                    Modifier.padding(30.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(
                            RoundedCornerShape(
                                20.dp
                            )
                        )
                        .background(
                            Beige.copy(
                                alpha =
                                    0.4f
                            )
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Pets,

                        contentDescription =
                            null,

                        tint =
                            BrownDark,

                        modifier =
                            Modifier.size(39.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )

                Text(

                    text =
                        if (
                            filtroEstado ==
                            FiltroEstado.TODOS
                        ) {

                            "No hay reportes cercanos"

                        } else {

                            "No hay animales en este estado"
                        },

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        BrownDark
                )

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )

                Text(

                    text =
                        if (
                            filtroEstado ==
                            FiltroEstado.TODOS
                        ) {

                            "Por ahora no encontramos animales reportados."

                        } else {

                            "No encontramos animales con el estado seleccionado."
                        },

                    color =
                        Brown,

                    fontSize =
                        14.sp
                )
            }
        }
    }
}

// ============================================================
// FORMATEAR DISTANCIA
// ============================================================

private fun formatearDistancia(
    metros: Float?
): String {

    if (metros == null) {
        return "Distancia no disponible"
    }

    return if (metros < 1000) {

        "${metros.toInt()} m"

    } else {

        val kilometros =
            metros / 1000f

        if (kilometros < 10f) {

            String.format(
                "%.1f km",
                kilometros
            )

        } else {

            String.format(
                "%.0f km",
                kilometros
            )
        }
    }
}

// ============================================================
// GOOGLE MAPS - CÓMO LLEGAR
// ============================================================

private fun abrirGoogleMaps(
    context: Context,
    reporte: AnimalReporteUI
) {

    val ubicacion =
        reporte.animal.ubicacion
            ?: return

    val latitud =
        ubicacion.latitude

    val longitud =
        ubicacion.longitude

    val uri = Uri.parse(
        "google.navigation:q=$latitud,$longitud"
    )

    val intent = Intent(
        Intent.ACTION_VIEW,
        uri
    )

    intent.setPackage(
        "com.google.android.apps.maps"
    )

    try {

        context.startActivity(intent)

    } catch (e: Exception) {

        val webUri = Uri.parse(
            "https://www.google.com/maps/dir/" +
                    "?api=1" +
                    "&destination=$latitud,$longitud"
        )

        val webIntent =
            Intent(
                Intent.ACTION_VIEW,
                webUri
            )

        context.startActivity(
            webIntent
        )
    }
}

// ============================================================
// NAVEGAR AL DETALLE
// ============================================================

private fun navegarADetalle(
    navController: NavHostController,
    reporteId: String
) {

    if (reporteId.isBlank()) {
        return
    }

    val ruta =
        Screen.DetalleAnimal.route
            .replace(
                "{reporteId}",
                reporteId
            )

    navController.navigate(
        ruta
    )
}