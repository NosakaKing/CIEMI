package com.uniandes.ciemi.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.utils.Constants
import com.uniandes.ciemi.view.BusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(viewModel: BusinessViewModel = viewModel()) {
    val context = LocalContext.current
    var search by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val role = viewModel.role.value
    val business = viewModel.business
    val message by viewModel.message

    LaunchedEffect(true) {
        viewModel.loadBusiness(context)
        viewModel.loadUserData(context)
        viewModel.loadCategories(context)
        viewModel.loadEntrepreneur(context)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Mis negocios", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.clearFields()
                viewModel.businessId.value = 0
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0054A3)
                    )
        ) {
            Text("Nuevo Negocio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.loadBusiness(
                            context,
                            search = search,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0054A3)
                    )
                ) {
                    Text("Buscar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(business) { busines ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${busines.nombre}")
                        Text("Dirección: ${busines.direccion}")
                        Text("Teléfono: ${busines.telefono}")
                        Text("Estado: ${busines.estado}")
                        Spacer(modifier = Modifier.height(8.dp))
                        if(role == "Admin") {
                            Button(onClick = {
                                viewModel.aproveeBusiness(context, busines.id, true)
                            }) {
                                Text("Aprobar Negocio")
                            }
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
                Text(if (viewModel.businessId.value == 0) "Nuevo Negocio" else "Editar Negocio")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.nombre.value,
                        onValueChange = { viewModel.nombre.value = it },
                        label = { Text("Nombres") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.direccion.value,
                        onValueChange = { viewModel.direccion.value = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth()
                        )
                    OutlinedTextField(
                        value = viewModel.telefono.value,
                        onValueChange = { viewModel.telefono.value = it },
                        label = { Text("Telefono") },
                    )
                    if(role == "Admin") {
                        var expanded by remember { mutableStateOf(false) }
                        val estados = listOf("Activo", "Inactivo")


                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = viewModel.estado.value,
                                onValueChange = { },
                                label = { Text("Estado") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                estados.forEach { estado ->
                                    DropdownMenuItem(
                                        text = { Text(estado) },
                                        onClick = {
                                            viewModel.estado.value = estado
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    var expandedEmprendedor by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedEmprendedor,
                        onExpandedChange = { expandedEmprendedor = !expandedEmprendedor }
                    ) {
                        val selectedEmprendedor = viewModel.emprendedores.find { it.id == viewModel.empresaSeleccionadaId.value }
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedEmprendedor?.userName ?: "Seleccione un emprendedor",
                            onValueChange = {},
                            label = { Text("Emprendedor") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedEmprendedor) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEmprendedor,
                            onDismissRequest = { expandedEmprendedor = false }
                        ) {
                            viewModel.emprendedores.forEach { emprendedor ->
                                DropdownMenuItem(
                                    text = { Text(emprendedor.userName) },
                                    onClick = {
                                        viewModel.empresaSeleccionadaId.value = emprendedor.id
                                        expandedEmprendedor = false
                                    }
                                )
                            }
                        }
                    }

                    var expandedCategoria by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = !expandedCategoria }
                    ) {
                        val selectedCategoria = viewModel.categorias.find { it.id == viewModel.categoriaSeleccionadaId.value }
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedCategoria?.nombre ?: "Seleccione una categoría",
                            onValueChange = {},
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategoria) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false }
                        ) {
                            viewModel.categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nombre) },
                                    onClick = {
                                        viewModel.categoriaSeleccionadaId.value = categoria.id
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = viewModel.descripcion.value,
                        onValueChange = { viewModel.descripcion.value = it },
                        label = { Text("Descripción") },
                    )

                }
            },
            confirmButton = {
                Button(onClick = {
                    val esActualizar = viewModel.businessId.value != 0
                    viewModel.saveOrUpdateBusiness(
                        context,
                        esActualizar = esActualizar,
                        businessId = viewModel.businessId.value
                    )
                    showDialog = false
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0054A3)
                    )
                ) {
                    Text(if (viewModel.businessId.value == 0) "Guardar Negocio" else "Actualizar Negocio")
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
