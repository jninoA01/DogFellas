package com.dogfellas.app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class CrearSolicitudAdopcionRequest(

    val reporte_id: Int,

    val nombre_completo: String,
    val tipo_documento: String,
    val numero_documento: String,
    val fecha_nacimiento: String,
    val telefono: String,
    val correo: String,

    val ciudad: String,
    val direccion: String,
    val tipo_vivienda: String,
    val vivienda: String,
    val autorizacion_propietario: String,

    val con_quien_vive: String,
    val personas_hogar: String,
    val acuerdo_familia: String,

    val ha_tenido_mascotas: String,
    val experiencia_animales: String,
    val mascotas_actuales: String,
    val cantidad_mascotas: String,
    val mascotas_vacunadas: String,
    val mascotas_esterilizadas: String,
    val situacion_mascotas_anteriores: String,

    val lugar_animal: String,
    val tiempo_solo: String,
    val responsable_alimentacion: String,
    val responsable_gastos: String,
    val capacidad_veterinaria: String,

    val motivo: String,
    val por_que_animal: String,
    val expectativas: String,

    val compromiso_adopcion: Boolean,

    val estado: String = "PENDIENTE"
)

data class SolicitudAdopcionApiResponse(

    val id: Int,

    val reporte_id: Int,
    val usuario_id: Int,

    val nombre_completo: String,
    val tipo_documento: String,
    val numero_documento: String,
    val fecha_nacimiento: String,
    val telefono: String,
    val correo: String,

    val ciudad: String,
    val direccion: String,
    val tipo_vivienda: String,
    val vivienda: String,
    val autorizacion_propietario: String,

    val con_quien_vive: String,
    val personas_hogar: String,
    val acuerdo_familia: String,

    val ha_tenido_mascotas: String,
    val experiencia_animales: String,
    val mascotas_actuales: String,
    val cantidad_mascotas: String,
    val mascotas_vacunadas: String,
    val mascotas_esterilizadas: String,
    val situacion_mascotas_anteriores: String,

    val lugar_animal: String,
    val tiempo_solo: String,
    val responsable_alimentacion: String,
    val responsable_gastos: String,
    val capacidad_veterinaria: String,

    val motivo: String,
    val por_que_animal: String,
    val expectativas: String,

    val compromiso_adopcion: Boolean,

    val estado: String,

    val fecha_solicitud: String
)

interface SolicitudAdopcionApi {

    @POST("solicitudes-adopcion/")
    suspend fun crearSolicitud(
        @Header("Authorization")
        authorization: String,
        @Body
        datos: CrearSolicitudAdopcionRequest
    ): Response<SolicitudAdopcionApiResponse>

    @GET("solicitudes-adopcion/animal/{reporte_id}")
    suspend fun obtenerSolicitudExistente(
        @Header("Authorization")
        authorization: String,
        @Path("reporte_id")
        reporteId: Int
    ): Response<SolicitudAdopcionApiResponse?>
}
