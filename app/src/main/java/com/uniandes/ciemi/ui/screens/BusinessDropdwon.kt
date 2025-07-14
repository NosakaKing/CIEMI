package com.uniandes.ciemi.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.uniandes.ciemi.view.DashboardViewModel

@Composable
fun NegocioDropdown(viewModel: DashboardViewModel) {
    val negocios = viewModel.negocios
    val negocioSeleccionado = viewModel.negocioSeleccionado.value
    var expanded by remember { mutableStateOf(false) }

    if (negocios.isEmpty()) {
        Text("No hay negocios disponibles", modifier = Modifier.padding(16.dp))
    } else {
        Box(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0054A3)
            )) {
                Text(negocioSeleccionado?.nombre ?: "Seleccionar negocio")
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                negocios.forEach { negocio ->
                    DropdownMenuItem(
                        text = { Text(negocio.nombre) },
                        onClick = {
                            viewModel.setNegocio(negocio)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
