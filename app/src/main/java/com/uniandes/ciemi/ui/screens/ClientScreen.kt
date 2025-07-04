package com.uniandes.ciemi.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.ClientViewModel

@Composable
fun ClientScreen(viewModel: ClientViewModel = viewModel()) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var filtroIdentificacion by remember { mutableStateOf("") }
    var filtroPrimerApellido by remember { mutableStateOf("") }
    val clients = viewModel.clients
    val message by viewModel.message

    LaunchedEffect(true) {
        viewModel.loadClients(context)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Lista de Clientes", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.clearFields()
                viewModel.clienteId.value = 0
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Cliente")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {

                OutlinedTextField(
                    value = filtroIdentificacion,
                    onValueChange = { filtroIdentificacion = it },
                    label = { Text("Buscar por Identificación") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = filtroPrimerApellido,
                    onValueChange = { filtroPrimerApellido = it },
                    label = { Text("Buscar por Primer Apellido") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.loadClients(
                            context,
                            identificacionBusqueda = filtroIdentificacion,
                            primerApellidoBusqueda = filtroPrimerApellido
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Buscar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(clients) { client ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${client.nombres} ${client.primerApellido} ${client.segundoApellido}")
                        Text("Email: ${client.email}")
                        Text("Identificacióm: ${client.identificacion}")
                        Text("Ciudad: ${client.ciudad}")
                        Text("Dirección: ${client.direccion}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.setClientToEdit(client)
                            showDialog = true
                        }) {
                            Text("Editar")
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(if (viewModel.clienteId.value == 0) "Nuevo Cliente" else "Editar Cliente")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.nombres.value,
                        onValueChange = { viewModel.nombres.value = it },
                        label = { Text("Nombres") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.identificacion.value,
                        onValueChange = { viewModel.identificacion.value = it },
                        label = { Text("Identificación") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.primerApellido.value,
                        onValueChange = { viewModel.primerApellido.value = it },
                        label = { Text("Primer Apellido") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.segundoApellido.value,
                        onValueChange = { viewModel.segundoApellido.value = it },
                        label = { Text("Segundo Apellido") },
                        modifier = Modifier.fillMaxWidth()
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
                        value = viewModel.direccion.value,
                        onValueChange = { viewModel.direccion.value = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val esActualizar = viewModel.clienteId.value != 0
                    viewModel.saveOrUpdateClient(
                        context,
                        esActualizar = esActualizar,
                        clienteId = viewModel.clienteId.value
                    )
                    showDialog = false
                }) {
                    Text(if (viewModel.clienteId.value == 0) "Guardar Cliente" else "Actualizar Cliente")
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showSystemUi = true)
@Composable
fun ClientScreenPreview() {
    ClientScreen(viewModel = ClientViewModel())
}
