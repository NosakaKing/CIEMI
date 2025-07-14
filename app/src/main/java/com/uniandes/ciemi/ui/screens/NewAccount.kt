package com.uniandes.ciemi.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.NewAccountViewModel

@Composable
fun NewAccountScreen(viewModel: NewAccountViewModel = viewModel()
) {
    val context = LocalContext.current
    val message by viewModel.message
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Personales", "Negocio")

    LaunchedEffect(true) {
        viewModel.loadCategories(context)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> DatosPersonales(viewModel)
            1 -> DatosNegocio(viewModel, context)
        }
    }
}

@Composable
fun DatosPersonales(viewModel: NewAccountViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Datos Personales", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.identificacion.value,
            onValueChange = { viewModel.identificacion.value = it },
            label = { Text("Identificación") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.userName.value,
            onValueChange = { viewModel.userName.value = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.nombre.value,
            onValueChange = { viewModel.nombre.value = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.apellido.value,
            onValueChange = { viewModel.apellido.value = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.confirmPassword.value,
            onValueChange = { viewModel.confirmPassword.value = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.telefono.value,
            onValueChange = { viewModel.telefono.value = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.ciudadOrigen.value,
            onValueChange = { viewModel.ciudadOrigen.value = it },
            label = { Text("Ciudad de Origen") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)

fun DatosNegocio(viewModel: NewAccountViewModel, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Datos del Negocio", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.nombreNegocio.value,
            onValueChange = { viewModel.nombreNegocio.value = it },
            label = { Text("Nombre del negocio") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.direccionNegocio.value,
            onValueChange = { viewModel.direccionNegocio.value = it },
            label = { Text("Dirección del negocio") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.telefonoNegocio.value,
            onValueChange = { viewModel.telefonoNegocio.value = it },
            label = { Text("Teléfono del negocio") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = viewModel.descripcion.value,
            onValueChange = { viewModel.descripcion.value = it },
            label = { Text("Descripción del negocio") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }
        val categorias = viewModel.categorias
        val selectedCategoria = viewModel.categorias.find { it.id == viewModel.categoriaSeleccionadaId.value }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedCategoria?.nombre ?: "Seleccione una categoría",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría del negocio") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.nombre) },
                        onClick = {
                            viewModel.categoriaSeleccionadaId.value = categoria.id
                            expanded = false
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.guardarPersona(context)},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0054A3)
            )
        ) {
            Text(text = "Ingresar")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
