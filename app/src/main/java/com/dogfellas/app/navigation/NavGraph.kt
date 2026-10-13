package com.dogfellas.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.dogfellas.app.ui.screen.DetalleAdopcionScreen
import com.dogfellas.app.ui.screens.*
import com.dogfellas.app.ui.viewmodel.AuthViewModel

@Composable
fun DogFellasNavGraph(
    navController: NavHostController
) {

    val authViewModel: AuthViewModel = viewModel()

    val startDestination =
        if (authViewModel.isUserLoggedIn()) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // =====================================================
        // 🔐 AUTENTICACIÓN
        // =====================================================

        composable(Screen.Login.route) {

            LoginScreen(
                onLoginExitoso = {

                    navController.navigate(
                        Screen.Home.route
                    ) {
                        popUpTo(
                            Screen.Login.route
                        ) {
                            inclusive = true
                        }
                    }
                },

                onGoToRegister = {

                    navController.navigate(
                        Screen.Register.route
                    )
                },

                onForgotPassword = {

                    navController.navigate(
                        Screen.ForgotPassword.route
                    )
                }
            )
        }


        composable(Screen.Register.route) {

            RegisterScreen(
                onRegisterSuccess = {

                    navController.navigate(
                        Screen.Home.route
                    ) {
                        popUpTo(
                            Screen.Login.route
                        ) {
                            inclusive = true
                        }
                    }
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }


        composable(Screen.ForgotPassword.route) {

            ForgotPasswordScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // 🏠 HOME
        // =====================================================

        composable(Screen.Home.route) {

            HomeScreen(
                navController = navController
            )
        }


        // =====================================================
        // 🐾 ANIMALES EN SITUACIÓN DE CALLE
        // =====================================================

        // -----------------------------------------------------
        // Reportar animal
        // -----------------------------------------------------

        composable(Screen.ReportarAnimal.route) {

            ReportarAnimalScreen(
                navController = navController
            )
        }


        // -----------------------------------------------------
        // Animales cercanos
        // -----------------------------------------------------

        composable(Screen.AnimalesCercanos.route) {

            AnimalesCercanosScreen(
                navController = navController
            )
        }


        // -----------------------------------------------------
        // Detalle de animal reportado
        // -----------------------------------------------------

        composable(
            route = Screen.DetalleAnimal.route,

            arguments = listOf(
                navArgument("reporteId") {
                    type = NavType.StringType
                }
            )

        ) { backStackEntry ->

            val reporteId =
                backStackEntry.arguments
                    ?.getString("reporteId")
                    ?: ""

            DetalleAnimalScreen(
                navController = navController,
                reporteId = reporteId
            )
        }


        // =====================================================
        // ❤️ ANIMALES EN ADOPCIÓN
        // =====================================================

        // -----------------------------------------------------
        // Lista de animales en adopción
        // -----------------------------------------------------

        composable(Screen.Adopciones.route) {

            AdopcionesScreen(
                navController = navController
            )
        }


        // -----------------------------------------------------
        // Detalle de animal en adopción
        // -----------------------------------------------------

        composable(
            route = Screen.DetalleAdopcion.route,

            arguments = listOf(
                navArgument("reporteId") {
                    type = NavType.StringType
                }
            )

        ) { backStackEntry ->

            val reporteId =
                backStackEntry.arguments
                    ?.getString("reporteId")
                    ?: ""

            DetalleAdopcionScreen(
                navController = navController,
                reporteId = reporteId
            )
        }


        // -----------------------------------------------------
        // Solicitud de adopción
        // -----------------------------------------------------

        composable(
            route = "${Screen.SolicitudAdopcion.route}?reporteId={reporteId}",

            arguments = listOf(
                navArgument("reporteId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )

        ) { backStackEntry ->

            val reporteId =
                backStackEntry.arguments
                    ?.getString("reporteId")
                    ?: ""

            SolicitudAdopcionScreen(
                navController = navController,
                reporteId = reporteId
            )
        }


        // =====================================================
        // 🏥 ATENCIÓN MÉDICA
        // =====================================================

        // -----------------------------------------------------
        // Atención médica general
        // -----------------------------------------------------

        composable(Screen.AtencionMedica.route) {

            AtencionMedicaScreen(
                navController = navController
            )
        }


        // -----------------------------------------------------
        // 🐾 Atención médica de animal reportado
        // -----------------------------------------------------

        composable(
            route = Screen.AtencionMedicaAnimal.route,

            arguments = listOf(
                navArgument("reporteId") {
                    type = NavType.StringType
                }
            )

        ) { backStackEntry ->

            val reporteId =
                backStackEntry.arguments
                    ?.getString("reporteId")
                    ?: ""

            AtencionMedicaAnimalScreen(
                navController = navController,
                reporteId = reporteId
            )
        }


        composable(Screen.AtencionMedicaMascotas.route) {

            AtencionMedicaMascotasScreen(
                navController = navController
            )
        }


        composable(Screen.Veterinarios.route) {

            VeterinariosScreen(
                navController = navController
            )
        }


        composable(Screen.Emergencias.route) {

            EmergenciasScreen(
                navController = navController
            )
        }


        composable(Screen.Medicamentos.route) {

            MedicamentosScreen(
                navController = navController
            )
        }


        composable(Screen.ConsejosSaludables.route) {

            ConsejosSaludablesScreen(
                navController = navController
            )
        }


        // =====================================================
        // 🙋 VOLUNTARIADO
        // =====================================================

        composable(Screen.Voluntariado.route) {

            VoluntariadoScreen(
                navController = navController
            )
        }


        // =====================================================
        // 🐶 MIS MASCOTAS
        // =====================================================

        composable(Screen.Mascotas.route) {

            MascotasScreen(
                navController = navController
            )
        }


        // =====================================================
        // 🔎 FUNCIONES ANTERIORES
        // =====================================================

        composable(Screen.PerdiMascota.route) {

            PerdiMascotaScreen(
                navController = navController
            )
        }


        composable(Screen.EncontreMascota.route) {

            EncontreMascotaScreen(
                navController = navController
            )
        }

        // =====================================================
        // 🧪 PRUEBA FASTAPI
        // =====================================================

        composable(Screen.AuthApiTest.route) {

            AuthApiTestScreen()
        }
    }
}
