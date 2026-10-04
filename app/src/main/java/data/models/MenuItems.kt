package com.dogfellas.app.model

import androidx.compose.ui.graphics.Color
import com.dogfellas.app.navigation.Screen
import com.dogfellas.app.ui.theme.*

data class MenuItem(
    val icon: String,
    val iconBg: Color,
    val title: String,
    val subtitle: String,
    val destination: String? = null,
    val modalIcon: String = "🚧"
) {
    val isUnderConstruction: Boolean
        get() = destination == null
}


// ═══════════════════════════════════════════════════════════════
// 🏠 HOME PRINCIPAL
// ═══════════════════════════════════════════════════════════════

val homeMenuItems = listOf(

    // 🔴 PRIORIDAD 1
    MenuItem(
        icon = "🐾",
        iconBg = IconOrange,
        title = "Reportar animal",
        subtitle = "Ayuda a un animal en situación de calle",
        destination = Screen.ReportarAnimal.route
    ),

    // 🔴 PRIORIDAD 2
    MenuItem(
        icon = "📍",
        iconBg = IconTeal,
        title = "Animales cercanos",
        subtitle = "Encuentra animales que necesitan ayuda",
        destination = Screen.AnimalesCercanos.route
    ),

    // 🔴 PRIORIDAD 3
    MenuItem(
        icon = "❤️",
        iconBg = IconRed,
        title = "Animales en adopción",
        subtitle = "Encuentra un animal que busca hogar",
        destination = Screen.Adopciones.route
    ),

    // 🔴 PRIORIDAD 4
    MenuItem(
        icon = "🏥",
        iconBg = IconGreen,
        title = "Atención médica",
        subtitle = "Apoya la recuperación de animales",
        destination = Screen.AtencionMedica.route
    ),

    // 🟠 PRIORIDAD 5
    MenuItem(
        icon = "🏠",
        iconBg = IconBlue,
        title = "Cuidado temporal",
        subtitle = "Ofrece un hogar temporal",
        destination = Screen.CuidadoTemporal.route
    ),

    // 🟠 PRIORIDAD 6
    MenuItem(
        icon = "🙋",
        iconBg = IconPurple,
        title = "Voluntariado",
        subtitle = "Participa y ayuda a rescatar animales",
        destination = Screen.Voluntariado.route
    ),

    // 🟡 FUNCIÓN SECUNDARIA
    MenuItem(
        icon = "🐶",
        iconBg = IconTeal,
        title = "Mis mascotas",
        subtitle = "Gestiona los animales que tienes a tu cargo",
        destination = Screen.Mascotas.route
    )
)


// ═══════════════════════════════════════════════════════════════
// 🏥 ATENCIÓN MÉDICA
// ═══════════════════════════════════════════════════════════════

val atencionMedicaItems = listOf(

    MenuItem(
        icon = "🏥",
        iconBg = IconGreen,
        title = "Veterinarios",
        subtitle = "Encuentra clínicas veterinarias cercanas",
        destination = Screen.Veterinarios.route
    ),

    MenuItem(
        icon = "🚨",
        iconBg = IconRed,
        title = "Emergencias",
        subtitle = "Consulta qué hacer ante una emergencia",
        destination = Screen.Emergencias.route
    ),

    MenuItem(
        icon = "💊",
        iconBg = IconBlue,
        title = "Medicamentos",
        subtitle = "Consulta información de medicamentos",
        destination = Screen.Medicamentos.route
    ),

    MenuItem(
        icon = "📰",
        iconBg = IconTeal,
        title = "Consejos saludables",
        subtitle = "Información para el cuidado animal",
        destination = Screen.ConsejosSaludables.route
    )
)


// ═══════════════════════════════════════════════════════════════
// 🩺 ATENCIÓN VETERINARIA
// ═══════════════════════════════════════════════════════════════

val atencionMedicaMascotasItems = listOf(

    MenuItem(
        icon = "📅",
        iconBg = IconGreen,
        title = "Citas veterinarias",
        subtitle = "Gestiona la atención veterinaria",
        destination = null,
        modalIcon = "📅"
    ),

    MenuItem(
        icon = "📋",
        iconBg = IconBlue,
        title = "Historial médico",
        subtitle = "Consulta el historial de atención",
        destination = null,
        modalIcon = "📋"
    ),

    MenuItem(
        icon = "💉",
        iconBg = IconOrange,
        title = "Vacunación",
        subtitle = "Control de vacunas y prevención",
        destination = null,
        modalIcon = "💉"
    )
)


// ═══════════════════════════════════════════════════════════════
// 🔎 MASCOTA PERDIDA
// ═══════════════════════════════════════════════════════════════

val perdiMascotaItems = listOf(

    MenuItem(
        icon = "📣",
        iconBg = IconOrange,
        title = "Reportar mascota perdida",
        subtitle = "Publica un reporte de búsqueda",
        destination = null,
        modalIcon = "📣"
    ),

    MenuItem(
        icon = "🗺️",
        iconBg = IconBlue,
        title = "Ver reportes cercanos",
        subtitle = "Consulta mascotas perdidas en tu zona",
        destination = null,
        modalIcon = "🗺️"
    ),

    MenuItem(
        icon = "🔔",
        iconBg = IconYellow,
        title = "Alertas de búsqueda",
        subtitle = "Recibe notificaciones de nuevos reportes",
        destination = null,
        modalIcon = "🔔"
    )
)


// ═══════════════════════════════════════════════════════════════
// 📍 ENCONTRÉ UNA MASCOTA
// ═══════════════════════════════════════════════════════════════

val encontreMascotaItems = listOf(

    MenuItem(
        icon = "📍",
        iconBg = IconTeal,
        title = "Reportar mascota encontrada",
        subtitle = "Registra una mascota que encontraste",
        destination = null,
        modalIcon = "📍"
    ),

    MenuItem(
        icon = "🔎",
        iconBg = IconBlue,
        title = "Identificar mascota",
        subtitle = "Ayuda a encontrar a su familia",
        destination = null,
        modalIcon = "🔎"
    )
)


// ═══════════════════════════════════════════════════════════════
// 🙋 VOLUNTARIADO
// ═══════════════════════════════════════════════════════════════

val voluntariadoItems = listOf(

    MenuItem(
        icon = "📝",
        iconBg = IconPurple,
        title = "Registrarme como voluntario",
        subtitle = "Únete a la red de ayuda DogFellas",
        destination = null,
        modalIcon = "📝"
    ),

    MenuItem(
        icon = "🚑",
        iconBg = IconRed,
        title = "Apoyar rescates",
        subtitle = "Ayuda en situaciones que requieren atención",
        destination = null,
        modalIcon = "🚑"
    ),

    MenuItem(
        icon = "🏠",
        iconBg = IconBlue,
        title = "Ofrecer cuidado temporal",
        subtitle = "Brinda un espacio mientras se encuentra un hogar",
        destination = Screen.CuidadoTemporal.route
    ),

    MenuItem(
        icon = "📢",
        iconBg = IconOrange,
        title = "Difundir adopciones",
        subtitle = "Ayuda a encontrar hogares responsables",
        destination = Screen.Adopciones.route
    )
)