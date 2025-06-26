package com.uniandes.ciemi.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.ClientViewModel

@Composable
fun ClientScreen(viewModel: ClientViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Lista de Clientes",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Acción al hacer clic en el botón */ },
            modifier = Modifier.height(48.dp)
        ) {
            Text(text = "Agregar Cliente")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Tabla de clientes
        // ...

    }
}