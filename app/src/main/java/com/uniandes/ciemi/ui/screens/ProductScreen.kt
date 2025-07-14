package com.uniandes.ciemi.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.BusinessSelectViewModel
import com.uniandes.ciemi.view.ProductViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductViewModel = viewModel(),
    businessSelect: BusinessSelectViewModel = viewModel()
) {
    val context = LocalContext.current
    val negocioSeleccionado = businessSelect.negocioSeleccionado.value
    if (negocioSeleccionado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seleccione un negocio para ver los clientes")
        }
        return
    }

    var showDialog by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val products = viewModel.products
    val message by viewModel.message

    LaunchedEffect(negocioSeleccionado) {
        viewModel.loadProducts(context, negocioSeleccionado.id)
        viewModel.loadParams(context)
        viewModel.loadCategories(context, negocioSeleccionado.id)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Lista de Productos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.clearFields()
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0054A3)
            )
        ) {
            Text("Agregar Producto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar por Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.loadProducts(
                            context,
                            negocioId = negocioSeleccionado.id,
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
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Codigo: ${product.codigo}")
                        Text("Nombre: ${product.nombre}")
                        Text("Categoria: ${product.nombreCategoria}")
                        Text("Estado: ${product.estado}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo Producto") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = viewModel.codigo.value,
                        onValueChange = { viewModel.codigo.value = it },
                        label = { Text("Codigo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.nombre.value,
                        onValueChange = { viewModel.nombre.value = it },
                        label = { Text("Nombres") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.descripcion.value,
                        onValueChange = { viewModel.descripcion.value = it },
                        label = { Text("Descripcion") },
                        modifier = Modifier.fillMaxWidth()
                    )

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
                        value = viewModel.precioCompra.value,
                        onValueChange = { viewModel.precioCompra.value = it },
                        label = { Text("Precio de Compra") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.precioVenta.value,
                        onValueChange = { viewModel.precioVenta.value = it },
                        label = { Text("Precio de Venta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.cantidad.value,
                        onValueChange = { viewModel.cantidad.value = it },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    var expandedParams by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expandedParams,
                        onExpandedChange = { expandedParams = !expandedParams }
                    ) {
                        val selectedParam = viewModel.params.find { it.valor == viewModel.paramsSeleccionadoId.value }
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedParam?.nombre ?: "Seleccione el IVA",
                            onValueChange = {},
                            label = { Text("IVA") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedParams) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedParams,
                            onDismissRequest = { expandedParams = false }
                        ) {
                            viewModel.params.forEach { param ->
                                DropdownMenuItem(
                                    text = { Text(param.valor) },
                                    onClick = {
                                        viewModel.paramsSeleccionadoId.value = param.valor
                                        expandedParams = false
                                    }
                                )
                            }
                        }
                    }
                    DateInputField("Fecha de Ingreso", viewModel.fechaIngreso)
                    DateInputField("Fecha de Elaboración", viewModel.fechaElaboracion)
                    DateInputField("Fecha de Caducidad", viewModel.fechaCaducidad)
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.guardarProducto(context, negocioId = negocioSeleccionado.id.toString())
                    showDialog = false
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0054A3)
                    )) {
                    Text("Guardar Producto")
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

@Composable
fun DateInputField(label: String, dateValue: MutableState<String>) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dateValue.value = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedTextField(
        readOnly = true,
        value = dateValue.value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { datePicker.show() }
    )
}
