package com.uniandes.ciemi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OnHoldScreen() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0) // Un color suave de fondo
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Información",
                    tint = Color(0xFFF57C00),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Acceso restringido",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFF57C00)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "Está en proceso de revisión por parte del administrador. Una vez aprobado, podrás acceder a todas las secciones del sistema.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
