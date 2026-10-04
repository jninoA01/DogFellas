package com.dogfellas.app.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.VeterinariaUI
import com.dogfellas.app.service.PlacesRetrofit
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    // ⚠️ Reemplaza con tu API key de Google Cloud Console
    // Asegúrate de activar "Places API" en la consola
    private val PLACES_API_KEY = "AIzaSyBLwewMx6UCGOsZIr_Asrnhc14S7ztYLU8"

    private val _loading       = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _veterinarias  = MutableStateFlow<List<VeterinariaUI>>(emptyList())
    val veterinarias: StateFlow<List<VeterinariaUI>> = _veterinarias

    private val _userLocation  = MutableStateFlow(LatLng(4.7110, -74.0721)) // Bogotá por defecto
    val userLocation: StateFlow<LatLng> = _userLocation

    private val _error         = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    @SuppressLint("MissingPermission")
    fun cargarVeterinarias() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                // 1. Obtener ubicación actual del usuario
                val location = fusedLocationClient.lastLocation.await()

                if (location == null) {
                    _error.value = "No se pudo obtener tu ubicación. Activa el GPS."
                    return@launch
                }

                val userLatLng = LatLng(location.latitude, location.longitude)
                _userLocation.value = userLatLng

                // 2. Llamar a Nearby Search REST API
                val response = PlacesRetrofit.api.buscarVeterinarias(
                    location = "${location.latitude},${location.longitude}",
                    radius   = 5000,       // 5 km de radio
                    apiKey   = PLACES_API_KEY
                )

                if (response.status != "OK" && response.status != "ZERO_RESULTS") {
                    _error.value = "Error de Places API: ${response.status}"
                    return@launch
                }

                // 3. Mapear resultados a VeterinariaUI
                val lista = response.results.mapNotNull { place ->
                    val lat = place.geometry?.location?.lat ?: return@mapNotNull null
                    val lng = place.geometry?.location?.lng ?: return@mapNotNull null

                    VeterinariaUI(
                        nombre    = place.name ?: "Veterinaria",
                        direccion = place.vicinity ?: "Dirección no disponible",
                        posicion  = LatLng(lat, lng),
                        rating    = place.rating ?: 0.0,
                        abierto   = place.openingHours?.openNow ?: false,
                        telefono  = place.phoneNumber,
                        fotoUrl   = null
                    )
                }

                _veterinarias.value = lista

            } catch (e: Exception) {
                // Bloque agregado para depuración detallada
                e.printStackTrace()
                android.util.Log.e("MAPS_ERROR", "Error completo: ${e.message}", e)
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}