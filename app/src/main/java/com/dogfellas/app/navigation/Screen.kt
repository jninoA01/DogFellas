package com.dogfellas.app.navigation

sealed class Screen(val route: String) {

    // 🔐 AUTENTICACIÓN
    object Login : Screen("login")

    object Register : Screen("register")

    object ForgotPassword : Screen("forgot_password")


    // 🏠 PRINCIPAL
    object Home : Screen("home")


    // 🐾 ANIMALES EN SITUACIÓN DE CALLE
    object ReportarAnimal : Screen("reportar_animal")

    object AnimalesCercanos : Screen("animales_cercanos")


    // 📋 DETALLE DEL REPORTE
    object DetalleAnimal :
        Screen("detalle_animal/{reporteId}") {

        fun createRoute(reporteId: String): String {
            return "detalle_animal/$reporteId"
        }
    }


    // ❤️ ADOPCIÓN
    object Adopciones : Screen("adopciones")


    // 📋 DETALLE DE ANIMAL EN ADOPCIÓN
    object DetalleAdopcion :
        Screen("detalle_adopcion/{reporteId}") {

        fun createRoute(reporteId: String): String {
            return "detalle_adopcion/$reporteId"
        }
    }


    // 📝 SOLICITUD DE ADOPCIÓN
    object SolicitudAdopcion :
        Screen("solicitud_adopcion") {

        fun createRoute(reporteId: String): String {
            return "solicitud_adopcion?reporteId=$reporteId"
        }
    }


    // 🏥 ATENCIÓN MÉDICA
    object AtencionMedica :
        Screen("atencion_medica")

    // 🐾 ATENCIÓN MÉDICA DE UN ANIMAL REPORTADO
    object AtencionMedicaAnimal :
        Screen("atencion_medica_animal/{reporteId}") {

        fun createRoute(reporteId: String): String {
            return "atencion_medica_animal/$reporteId"
        }
    }

    object AtencionMedicaMascotas :
        Screen("atencion_medica_mascotas")

    object Veterinarios :
        Screen("veterinarios")

    object Emergencias :
        Screen("emergencias")

    object Medicamentos :
        Screen("medicamentos")

    object ConsejosSaludables :
        Screen("consejos_saludables")


    // 🏠 CUIDADO TEMPORAL
    object CuidadoTemporal :
        Screen("cuidado_temporal")


    // 🙋 PARTICIPACIÓN
    object Voluntariado :
        Screen("voluntariado")


    // 🐶 MASCOTAS DEL USUARIO
    object Mascotas :
        Screen("mascotas")


    // 🔎 REPORTES ANTERIORES
    object PerdiMascota :
        Screen("perdi_mascota")

    object EncontreMascota :
        Screen("encontre_mascota")
}