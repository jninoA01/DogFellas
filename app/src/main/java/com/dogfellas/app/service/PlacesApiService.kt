package com.dogfellas.app.service

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ── DTOs ──────────────────────────────────────────────────────────────────
data class NearbySearchResponse(
    val results: List<PlaceResult> = emptyList(),
    val status: String = ""
)

data class PlaceResult(
    val name: String?,
    val vicinity: String?,
    val geometry: Geometry?,
    val rating: Double?,
    @SerializedName("opening_hours") val openingHours: OpeningHours?,
    @SerializedName("formatted_phone_number") val phoneNumber: String?,
    @SerializedName("place_id") val placeId: String?
)

data class Geometry(val location: LatLngDto?)
data class LatLngDto(val lat: Double?, val lng: Double?)
data class OpeningHours(@SerializedName("open_now") val openNow: Boolean?)

// ── Retrofit service ──────────────────────────────────────────────────────
interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun buscarVeterinarias(
        @Query("location")  location: String,   // "lat,lng"
        @Query("radius")    radius: Int = 5000, // metros
        @Query("type")      type: String = "veterinary_care",
        @Query("key")       apiKey: String
    ): NearbySearchResponse
}

object PlacesRetrofit {
    val api: PlacesApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlacesApiService::class.java)
    }
}