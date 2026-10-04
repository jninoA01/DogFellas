package com.dogfellas.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Surface
import com.dogfellas.app.navigation.DogFellasNavGraph
import com.dogfellas.app.ui.theme.DogFellasTheme
import com.google.android.libraries.places.api.Places


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🔥 INICIALIZAR GOOGLE PLACES AQUÍ 🔥
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                "AIzaSyBLwewMx6UCGOsZIr_Asrnhc14S7ztYLU8"
            )
        }
        setContent {
            DogFellasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    DogFellasNavGraph(navController = navController)
                }
            }
        }
    }
}