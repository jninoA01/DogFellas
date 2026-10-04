package com.dogfellas.app.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.SolicitudAdopcion
import com.dogfellas.app.repository.SolicitudAdopcionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SolicitudAdopcionViewModel : ViewModel() {

    private val repository = SolicitudAdopcionRepository()
    private val auth = FirebaseAuth.getInstance()

    // =========================================================
    // ESTADOS
    // =========================================================

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> =
        _guardando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    private val _solicitudCreada =
        MutableStateFlow(false)
    val solicitudCreada: StateFlow<Boolean> =
        _solicitudCreada.asStateFlow()

    private val _solicitudExistente =
        MutableStateFlow<SolicitudAdopcion?>(null)
    val solicitudExistente:
            StateFlow<SolicitudAdopcion?> =
        _solicitudExistente.asStateFlow()

    private val _consultandoSolicitud =
        MutableStateFlow(false)
    val consultandoSolicitud: StateFlow<Boolean> =
        _consultandoSolicitud.asStateFlow()


    // =========================================================
    // CONSULTAR SOLICITUD EXISTENTE
    // =========================================================

    fun cargarSolicitudExistente(
        reporteId: String
    ) {

        val usuario = auth.currentUser

        if (usuario == null) {
            _solicitudExistente.value = null
            return
        }

        if (reporteId.isBlank()) {
            _solicitudExistente.value = null
            return
        }

        viewModelScope.launch {

            _consultandoSolicitud.value = true
            _mensaje.value = null

            repository.obtenerSolicitudExistente(
                reporteId = reporteId,
                usuarioId = usuario.uid
            )
                .onSuccess { solicitud ->

                    _solicitudExistente.value =
                        solicitud
                }
                .onFailure { error ->

                    _mensaje.value =
                        error.message
                            ?: "No fue posible consultar tu solicitud."
                }

            _consultandoSolicitud.value = false
        }
    }


    // =========================================================
    // ENVIAR SOLICITUD
    // =========================================================

    fun enviarSolicitud(
        reporteId: String,
        nombreCompleto: String,
        tipoDocumento: String,
        numeroDocumento: String,
        fechaNacimiento: String,
        telefono: String,
        correo: String,
        ciudad: String,
        direccion: String,
        tipoVivienda: String,
        vivienda: String,
        autorizacionPropietario: String,
        conQuienVive: String,
        personasHogar: String,
        acuerdoFamilia: String,
        haTenidoMascotas: String,
        experienciaAnimales: String,
        mascotasActuales: String,
        cantidadMascotas: String,
        mascotasVacunadas: String,
        mascotasEsterilizadas: String,
        situacionMascotasAnteriores: String,
        lugarAnimal: String,
        tiempoSolo: String,
        responsableAlimentacion: String,
        responsableGastos: String,
        capacidadVeterinaria: String,
        motivo: String,
        porQueAnimal: String,
        expectativas: String,
        compromisoAdopcion: Boolean
    ) {

        // =====================================================
        // USUARIO
        // =====================================================

        val usuario = auth.currentUser

        if (usuario == null) {
            _mensaje.value =
                "Debes iniciar sesión para enviar una solicitud."
            return
        }

        // =====================================================
        // REPORTE
        // =====================================================

        if (reporteId.isBlank()) {
            _mensaje.value =
                "No se encontró el animal seleccionado."
            return
        }

        // =====================================================
        // VALIDACIONES
        // =====================================================

        if (nombreCompleto.trim().isBlank()) {
            _mensaje.value =
                "Ingresa tu nombre completo."
            return
        }

        if (tipoDocumento.isBlank()) {
            _mensaje.value =
                "Selecciona el tipo de documento."
            return
        }

        if (numeroDocumento.isBlank()) {
            _mensaje.value =
                "Ingresa tu número de documento."
            return
        }

        if (!numeroDocumento.all { it.isDigit() }) {
            _mensaje.value =
                "El número de documento solo debe contener números."
            return
        }

        if (numeroDocumento.length < 5) {
            _mensaje.value =
                "El número de documento no es válido."
            return
        }

        // =====================================================
        // FECHA DE NACIMIENTO
        // =====================================================

        val errorFecha =
            validarFechaNacimiento(fechaNacimiento)

        if (errorFecha != null) {
            _mensaje.value = errorFecha
            return
        }

        // =====================================================
        // TELÉFONO
        // =====================================================

        if (telefono.isBlank()) {
            _mensaje.value =
                "Ingresa tu número de teléfono."
            return
        }

        if (!telefono.all { it.isDigit() }) {
            _mensaje.value =
                "El teléfono solo debe contener números."
            return
        }

        if (telefono.length < 7) {
            _mensaje.value =
                "El número de teléfono no es válido."
            return
        }

        // =====================================================
        // CORREO
        // =====================================================

        if (correo.isBlank()) {
            _mensaje.value =
                "Ingresa tu correo electrónico."
            return
        }

        if (!correoValido(correo)) {
            _mensaje.value =
                "Ingresa un correo electrónico válido."
            return
        }

        // =====================================================
        // VIVIENDA
        // =====================================================

        if (ciudad.trim().isBlank()) {
            _mensaje.value =
                "Ingresa la ciudad donde vives."
            return
        }

        if (direccion.trim().isBlank()) {
            _mensaje.value =
                "Ingresa tu dirección."
            return
        }

        if (tipoVivienda.isBlank()) {
            _mensaje.value =
                "Selecciona el tipo de vivienda."
            return
        }

        if (vivienda.isBlank()) {
            _mensaje.value =
                "Indica si la vivienda es propia, arrendada u otra."
            return
        }

        // =====================================================
        // AUTORIZACIÓN DEL PROPIETARIO
        // =====================================================

        if (vivienda.equals(
                "Arrendada",
                ignoreCase = true
            )
        ) {

            if (autorizacionPropietario.isBlank()) {
                _mensaje.value =
                    "Indica si tienes autorización del propietario."
                return
            }

            if (!autorizacionPropietario.equals(
                    "Sí",
                    ignoreCase = true
                )
            ) {
                _mensaje.value =
                    "Necesitas autorización del propietario para adoptar."
                return
            }
        }

        // =====================================================
        // HOGAR
        // =====================================================

        if (conQuienVive.isBlank()) {
            _mensaje.value =
                "Indica con quién vives."
            return
        }

        if (personasHogar.isBlank()) {
            _mensaje.value =
                "Indica cuántas personas viven en el hogar."
            return
        }

        val cantidadPersonas =
            personasHogar.toIntOrNull()

        if (
            cantidadPersonas == null ||
            cantidadPersonas <= 0
        ) {
            _mensaje.value =
                "La cantidad de personas debe ser mayor a 0."
            return
        }

        if (acuerdoFamilia.isBlank()) {
            _mensaje.value =
                "Indica si todos están de acuerdo con la adopción."
            return
        }

        if (!acuerdoFamilia.equals(
                "Sí",
                ignoreCase = true
            )
        ) {
            _mensaje.value =
                "Es necesario que la familia esté de acuerdo con la adopción."
            return
        }

        // =====================================================
        // EXPERIENCIA CON MASCOTAS
        // =====================================================

        if (haTenidoMascotas.isBlank()) {
            _mensaje.value =
                "Indica si has tenido mascotas anteriormente."
            return
        }

        if (
            haTenidoMascotas.equals(
                "Sí",
                ignoreCase = true
            ) &&
            experienciaAnimales.trim().isBlank()
        ) {
            _mensaje.value =
                "Cuéntanos brevemente tu experiencia con animales."
            return
        }

        // =====================================================
        // MASCOTAS ACTUALES
        // =====================================================

        if (mascotasActuales.isBlank()) {
            _mensaje.value =
                "Indica si actualmente tienes mascotas."
            return
        }

        if (
            mascotasActuales.equals(
                "Sí",
                ignoreCase = true
            )
        ) {

            if (cantidadMascotas.isBlank()) {
                _mensaje.value =
                    "Indica cuántas mascotas tienes actualmente."
                return
            }

            val cantidad =
                cantidadMascotas.toIntOrNull()

            if (
                cantidad == null ||
                cantidad <= 0
            ) {
                _mensaje.value =
                    "La cantidad de mascotas debe ser mayor a 0."
                return
            }

            if (mascotasVacunadas.isBlank()) {
                _mensaje.value =
                    "Indica si tus mascotas están vacunadas."
                return
            }

            if (mascotasEsterilizadas.isBlank()) {
                _mensaje.value =
                    "Indica si tus mascotas están esterilizadas."
                return
            }
        }

        if (
            haTenidoMascotas.equals(
                "Sí",
                ignoreCase = true
            ) &&
            situacionMascotasAnteriores
                .trim()
                .isBlank()
        ) {
            _mensaje.value =
                "Indica qué ocurrió con tus mascotas anteriores."
            return
        }

        // =====================================================
        // CUIDADO DEL ANIMAL
        // =====================================================

        if (lugarAnimal.trim().isBlank()) {
            _mensaje.value =
                "Indica dónde permanecerá principalmente el animal."
            return
        }

        if (tiempoSolo.trim().isBlank()) {
            _mensaje.value =
                "Indica cuánto tiempo permanecerá solo el animal."
            return
        }

        if (responsableAlimentacion.trim().isBlank()) {
            _mensaje.value =
                "Indica quién será responsable de la alimentación."
            return
        }

        if (responsableGastos.trim().isBlank()) {
            _mensaje.value =
                "Indica quién será responsable de los gastos."
            return
        }

        // =====================================================
        // ATENCIÓN VETERINARIA
        // =====================================================

        if (capacidadVeterinaria.isBlank()) {
            _mensaje.value =
                "Indica si puedes cubrir atención veterinaria."
            return
        }

        if (!capacidadVeterinaria.equals(
                "Sí",
                ignoreCase = true
            )
        ) {
            _mensaje.value =
                "Para adoptar debes contar con capacidad para cubrir atención veterinaria."
            return
        }

        // =====================================================
        // MOTIVACIÓN
        // =====================================================

        if (motivo.trim().isBlank()) {
            _mensaje.value =
                "Cuéntanos cuál es tu motivo para adoptar."
            return
        }

        if (porQueAnimal.trim().isBlank()) {
            _mensaje.value =
                "Cuéntanos por qué quieres adoptar este animal."
            return
        }

        if (expectativas.trim().isBlank()) {
            _mensaje.value =
                "Cuéntanos cuáles son tus expectativas."
            return
        }

        // =====================================================
        // COMPROMISO
        // =====================================================

        if (!compromisoAdopcion) {
            _mensaje.value =
                "Debes aceptar el compromiso de adopción responsable."
            return
        }

        // =====================================================
        // CORRUTINA FIREBASE
        // =====================================================

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null
            _solicitudCreada.value = false

            // =================================================
            // COMPROBAR DUPLICADO
            // =================================================

            val solicitudActual =
                repository.obtenerSolicitudExistente(
                    reporteId = reporteId,
                    usuarioId = usuario.uid
                )

            var puedeCrear = true

            solicitudActual
                .onSuccess { solicitud ->

                    if (solicitud != null) {

                        _solicitudExistente.value =
                            solicitud

                        _mensaje.value =
                            "Ya presentaste una solicitud para este animal."

                        puedeCrear = false
                    }
                }
                .onFailure { error ->

                    _mensaje.value =
                        error.message
                            ?: "No fue posible comprobar si ya existe una solicitud."

                    puedeCrear = false
                }

            if (!puedeCrear) {
                _guardando.value = false
                return@launch
            }

            // =================================================
            // CREAR SOLICITUD
            // =================================================

            val solicitud =
                SolicitudAdopcion(

                    reporteId =
                        reporteId,

                    usuarioId =
                        usuario.uid,

                    nombreCompleto =
                        nombreCompleto.trim(),

                    tipoDocumento =
                        tipoDocumento.trim(),

                    numeroDocumento =
                        numeroDocumento.trim(),

                    fechaNacimiento =
                        fechaNacimiento.trim(),

                    telefono =
                        telefono.trim(),

                    correo =
                        correo.trim(),

                    ciudad =
                        ciudad.trim(),

                    direccion =
                        direccion.trim(),

                    tipoVivienda =
                        tipoVivienda.trim(),

                    vivienda =
                        vivienda.trim(),

                    autorizacionPropietario =
                        autorizacionPropietario.trim(),

                    conQuienVive =
                        conQuienVive.trim(),

                    personasHogar =
                        personasHogar.trim(),

                    acuerdoFamilia =
                        acuerdoFamilia.trim(),

                    haTenidoMascotas =
                        haTenidoMascotas.trim(),

                    experienciaAnimales =
                        experienciaAnimales.trim(),

                    mascotasActuales =
                        mascotasActuales.trim(),

                    cantidadMascotas =
                        cantidadMascotas.trim(),

                    mascotasVacunadas =
                        mascotasVacunadas.trim(),

                    mascotasEsterilizadas =
                        mascotasEsterilizadas.trim(),

                    situacionMascotasAnteriores =
                        situacionMascotasAnteriores.trim(),

                    lugarAnimal =
                        lugarAnimal.trim(),

                    tiempoSolo =
                        tiempoSolo.trim(),

                    responsableAlimentacion =
                        responsableAlimentacion.trim(),

                    responsableGastos =
                        responsableGastos.trim(),

                    capacidadVeterinaria =
                        capacidadVeterinaria.trim(),

                    motivo =
                        motivo.trim(),

                    porQueAnimal =
                        porQueAnimal.trim(),

                    expectativas =
                        expectativas.trim(),

                    compromisoAdopcion =
                        compromisoAdopcion,

                    estado =
                        "PENDIENTE",

                    fechaSolicitud =
                        System.currentTimeMillis()
                )

            // =================================================
            // GUARDAR EN FIREBASE
            // =================================================

            repository.crearSolicitud(solicitud)

                .onSuccess {

                    _solicitudCreada.value =
                        true

                    _solicitudExistente.value =
                        solicitud
                }

                .onFailure { error ->

                    _mensaje.value =
                        error.message
                            ?: "No fue posible registrar la solicitud."
                }

            _guardando.value = false
        }
    }


    // =========================================================
    // VALIDAR FECHA DE NACIMIENTO
    // =========================================================

    private fun validarFechaNacimiento(
        fecha: String
    ): String? {

        if (fecha.isBlank()) {
            return "Selecciona tu fecha de nacimiento."
        }

        return try {

            val formato =
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

            formato.isLenient = false

            val fechaNacimiento =
                formato.parse(fecha)
                    ?: return "La fecha de nacimiento no es válida."

            val hoy =
                Calendar.getInstance()

            // No permitir fechas futuras
            if (fechaNacimiento.after(hoy.time)) {
                return "La fecha de nacimiento no puede ser futura."
            }

            val nacimiento =
                Calendar.getInstance()

            nacimiento.time =
                fechaNacimiento

            var edad =
                hoy.get(Calendar.YEAR) -
                        nacimiento.get(Calendar.YEAR)

            val yaCumplio =
                hoy.get(Calendar.MONTH) >
                        nacimiento.get(Calendar.MONTH) ||
                        (
                                hoy.get(Calendar.MONTH) ==
                                        nacimiento.get(Calendar.MONTH) &&
                                        hoy.get(Calendar.DAY_OF_MONTH) >=
                                        nacimiento.get(Calendar.DAY_OF_MONTH)
                                )

            if (!yaCumplio) {
                edad--
            }

            if (edad < 18) {
                return "Debes ser mayor de edad para solicitar la adopción."
            }

            null

        } catch (e: Exception) {

            "La fecha de nacimiento no es válida."
        }
    }


    // =========================================================
    // VALIDAR CORREO
    // =========================================================

    private fun correoValido(
        correo: String
    ): Boolean {

        return Patterns.EMAIL_ADDRESS
            .matcher(correo.trim())
            .matches()
    }


    // =========================================================
    // LIMPIAR MENSAJE
    // =========================================================

    fun limpiarMensaje() {
        _mensaje.value = null
    }


    // =========================================================
    // LIMPIAR ESTADO DE SOLICITUD CREADA
    // =========================================================

    fun limpiarSolicitudCreada() {
        _solicitudCreada.value = false
    }
}