package com.uniandes.ciemi.ui.screens

import android.widget.Toast
import com.uniandes.ciemi.view.LoginViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.uniandes.ciemi.R

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val message by viewModel.message
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
            if (it.contains("Autentificación exitosa", true)) {
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoImage()

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Inicio de Sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.login(context)},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0054A3)
            )

        ) {
            Text(text = "Ingresar")
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Registrarse")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = { navController.navigate("reset") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "¿Olvidaste tu contraseña?")
        }

    }
}

@Composable
fun LogoImage() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo CIEMI",
        modifier = Modifier.size(120.dp),
        contentScale = ContentScale.Fit
    )
}
