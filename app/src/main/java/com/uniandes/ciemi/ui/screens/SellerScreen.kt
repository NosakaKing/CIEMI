package com.uniandes.ciemi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.BusinessSelectViewModel
import com.uniandes.ciemi.view.SellerViewModel

@Composable
fun SellerScreen(viewModel: SellerViewModel = viewModel(),
                 businessViewModel: BusinessSelectViewModel = viewModel()) {
    val context = LocalContext.current
    val negocioSeleccionado = businessViewModel.negocioSeleccionado.value

    if (negocioSeleccionado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seleccione un negocio para ver los clientes")
        }
        return
    }
    var showDialog by remember { mutableStateOf(false) }
    val sellers = viewModel.sellers
    val message by viewModel.message

    LaunchedEffect(negocioSeleccionado) {
        viewModel.loadSeller(context, negocioSeleccionado.id)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(viewModel.sellerGuardado.value) {
        if (viewModel.sellerGuardado.value) {
            viewModel.loadSeller(context, negocioSeleccionado.id)
            viewModel.resetSellerGuardado()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Lista de Vendedores", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.clearFields()
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nuevo Vendedor")
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(sellers) { seller ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${seller.nombres} ${seller.apellido}")
                        Text("Email: ${seller.email}")
                        Text("Identificación: ${seller.identificacion}")
                        Text("Ciudad: ${seller.ciudad}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                }
            }
        }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo Vendedor") },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.identificacion.value,
                        onValueChange = { viewModel.identificacion.value = it },
                        label = { Text("Identificación") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.nombre.value,
                        onValueChange = { viewModel.nombre.value = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                        )
                    OutlinedTextField(
                        value = viewModel.apellido.value,
                        onValueChange = { viewModel.apellido.value = it },
                        label = { Text("Apellido") },
                    )
                    OutlinedTextField(
                        value = viewModel.email.value,
                        onValueChange = { viewModel.email.value = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.telefono.value,
                        onValueChange = { viewModel.telefono.value = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.ciudad.value,
                        onValueChange = { viewModel.ciudad.value = it },
                        label = { Text("Ciudad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.userName.value,
                        onValueChange = { viewModel.userName.value = it },
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.password.value,
                        onValueChange = { viewModel.password.value = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.confirmPassword.value,
                        onValueChange = { viewModel.confirmPassword.value = it },
                        label = { Text("Confirmar Contraseña") },
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveSeller(context, negocioSeleccionado.id)
                    showDialog = false
                }) {
                    Text("Guardar Vendedor")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
