package com.dogfellas.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dogfellas.app.ui.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*

private val BrownDark = Color(0xFF2D1B12)
private val BrownMid = Color(0xFF6D4C41)
private val BrownLight = Color(0xFFD7B899)
private val Cream = Color(0xFFF8F3EE)

@Composable
fun VeterinariosScreen(
    navController: NavHostController,
    mapsViewModel: MapsViewModel = viewModel()
) {

    val veterinarias by mapsViewModel.veterinarias.collectAsState()
    val loading by mapsViewModel.loading.collectAsState()
    val userLocation by mapsViewModel.userLocation.collectAsState()

    val cameraPositionState =
        rememberCameraPositionState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                mapsViewModel.cargarVeterinarias()
            }
        }

    LaunchedEffect(Unit) {

        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(
                userLocation,
                13f
            )
        )

        if (
            ContextCompat.checkSelfPermission(
                navController.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            mapsViewModel.cargarVeterinarias()

        } else {

            permissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 🌍 MAPA REAL
        GoogleMap(
            modifier = Modifier.fillMaxSize(),

            cameraPositionState = cameraPositionState,

            properties = MapProperties(
                isMyLocationEnabled = true
            ),

            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {

            veterinarias.forEach { veterinaria ->

                Marker(
                    state = MarkerState(
                        position = veterinaria.posicion
                    ),

                    title = veterinaria.nombre,

                    snippet = veterinaria.direccion
                )
            }
        }

        // 🌑 OVERLAY SUPERIOR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            BrownDark.copy(alpha = 0.90f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 🔝 HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 48.dp,
                    start = 20.dp,
                    end = 20.dp
                )
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },

                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0x66FFFFFF)
                    )
                ) {

                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = Cream
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(
                        text = "Veterinarias",

                        color = Cream,

                        fontWeight = FontWeight.Bold,

                        fontSize = 28.sp
                    )

                    Text(
                        text = "Encuentra ayuda cerca de ti",

                        color = BrownLight,

                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 🐕 CARD
            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        BrownDark.copy(alpha = 0.82f)
                ),

                shape = RoundedCornerShape(26.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        BrownLight,
                                        BrownMid
                                    )
                                ),
                                CircleShape
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            Icons.Rounded.LocalHospital,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "${veterinarias.size} veterinarias encontradas",

                            color = Cream,

                            fontWeight = FontWeight.Bold,

                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Mapa inteligente DogFellas",

                            color = BrownLight,

                            fontSize = 13.sp
                        )
                    }

                    Icon(
                        Icons.Rounded.Pets,
                        contentDescription = null,
                        tint = BrownLight
                    )
                }
            }
        }

        // 📍 BOTÓN UBICACIÓN
        FloatingActionButton(
            onClick = {

                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(
                        userLocation,
                        15f
                    )
                )
            },

            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),

            containerColor = BrownDark,

            contentColor = Cream
        ) {

            Icon(
                Icons.Rounded.MyLocation,
                contentDescription = null
            )
        }

        // ⏳ LOADING
        if (loading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.35f)
                    ),

                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = BrownLight
                )
            }
        }
    }
}