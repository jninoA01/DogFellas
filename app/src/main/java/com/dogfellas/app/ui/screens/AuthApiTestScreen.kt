package com.dogfellas.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.dogfellas.app.data.api.AuthApiTestViewModel

@Composable
fun AuthApiTestScreen(
    viewModel: AuthApiTestViewModel = viewModel()
) {

    val resultado by viewModel.resultado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Prueba de conexión con FastAPI"
        )

        Button(
            onClick = {
                viewModel.probar()
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Probar conexión")
        }

        Text(
            text = resultado,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
