package com.uniandes.ciemi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.ResetAccountViewModel

@Composable
fun ResetScreen(
    viewModel: ResetAccountViewModel = viewModel()
) {
    val context = LocalContext.current
    val message by viewModel.message

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Restablecer Contraseña",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ingresa tu correo electrónico para restablecer tu contraseña.")
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Correo electrónico") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.resetPassword(context) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0054A3)
            )
        ) {
            Text(text = "Restablecer")
        }

    }
}
