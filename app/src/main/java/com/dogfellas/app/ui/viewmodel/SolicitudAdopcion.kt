package com.dogfellas.app.model

data class SolicitudAdopcion(
    val id: String = "",

    // Animal
    val reporteId: String = "",

    // Usuario
    val usuarioId: String = "",

    // Datos personales
    val nombreCompleto: String = "",
    val tipoDocumento: String = "",
    val numeroDocumento: String = "",
    val fechaNacimiento: String = "",
    val telefono: String = "",
    val correo: String = "",

    // Residencia
    val ciudad: String = "",
    val direccion: String = "",
    val tipoVivienda: String = "",
    val vivienda: String = "",
    val autorizacionPropietario: String = "",

    // Composición del hogar
    val conQuienVive: String = "",
    val personasHogar: String = "",
    val acuerdoFamilia: String = "",

    // Experiencia con animales
    val haTenidoMascotas: String = "",
    val experienciaAnimales: String = "",
    val mascotasActuales: String = "",
    val cantidadMascotas: String = "",
    val mascotasVacunadas: String = "",
    val mascotasEsterilizadas: String = "",
    val situacionMascotasAnteriores: String = "",

    // Condiciones para el animal
    val lugarAnimal: String = "",
    val tiempoSolo: String = "",
    val responsableAlimentacion: String = "",
    val responsableGastos: String = "",
    val capacidadVeterinaria: String = "",

    // Motivación
    val motivo: String = "",
    val porQueAnimal: String = "",
    val expectativas: String = "",

    // Compromiso
    val compromisoAdopcion: Boolean = false,

    // Estado del proceso
    val estado: String = "PENDIENTE",

    val fechaSolicitud: Long = 0L
)