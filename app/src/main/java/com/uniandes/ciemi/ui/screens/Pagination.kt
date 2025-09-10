package com.uniandes.ciemi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PaginationControls(
    currentPage: Int,
    pageSize: Int,
    itemsLoaded: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Anterior
        OutlinedButton(
            onClick = { if (currentPage > 1) onPrevious() },
            enabled = currentPage > 1
        ) {
            Text("⟵ Anterior")
        }

        // Indicador de página en el centro
        Text(
            text = "Página $currentPage",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Botón Siguiente (habilitado solo si itemsLoaded == pageSize)
        OutlinedButton(
            onClick = { if (itemsLoaded >= pageSize) onNext() },
            enabled = itemsLoaded >= pageSize
        ) {
            Text("Siguiente ⟶")
        }
    }
}
