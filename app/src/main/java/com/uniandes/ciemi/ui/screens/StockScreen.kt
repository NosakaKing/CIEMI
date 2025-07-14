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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.BusinessSelectViewModel
import com.uniandes.ciemi.view.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    viewModel: StockViewModel = viewModel(),
    businessSelect: BusinessSelectViewModel = viewModel()
) {
    val context = LocalContext.current
    val negocioSeleccionado = businessSelect.negocioSeleccionado.value
    if (negocioSeleccionado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seleccione un negocio para ver el stock")
        }
        return
    }
    val stocks = viewModel.stocks
    var showDialog by remember { mutableStateOf(false) }
    val message by viewModel.message

    LaunchedEffect(negocioSeleccionado) {
        viewModel.loadStock(context, negocioSeleccionado.id)
        viewModel.loadProduct(context, negocioSeleccionado.id)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(viewModel.stockGuardado.value) {
        if (viewModel.stockGuardado.value) {
            viewModel.loadStock(context, negocioSeleccionado.id)
            viewModel.resetStockGuardado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize().padding(16.dp)

    ) {
        Text(text = "Lista de Stock", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.clearFields()
                viewModel.stockId.value = 0
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0054A3)
            )
        ) {
            Text("Agregar Stock")
        }
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                var expandedParams by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedParams,
                    onExpandedChange = { expandedParams = !expandedParams }
                ) {
                    val selectedParam = viewModel.products.find { it.id == viewModel.productoSeleccionadoId.value }
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedParam?.nombre ?: "Seleccione el producto",
                        onValueChange = {},
                        label = { Text("Producto") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedParams) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedParams,
                        onDismissRequest = { expandedParams = false }
                    ) {
                        viewModel.products.forEach { param ->
                            DropdownMenuItem(
                                text = { Text(param.nombre) },
                                onClick = {
                                    viewModel.productoSeleccionadoId.value = param.id
                                    expandedParams = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.loadStock(
                            context,
                            negocioId = negocioSeleccionado.id,
                            search = viewModel.productoSeleccionadoId.value
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
            items(stocks) { stock ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${stock.producto.nombre}")
                        Text("Descripción: ${stock.producto.descripcion}")
                        Text("Estado: ${stock.producto.estado}")
                        Text("Cantidad: ${stock.cantidad}")
                        Text("Precio de Compra: ${stock.precioCompra}")
                        Text("Precio de Venta: ${stock.precioVenta}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.setStockToEdit(stock)
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
                Text(if (viewModel.stockId.value == 0) "Nuevo Stock" else "Editar Stock")
            },
            text = {
                Column {
                    var expandedProduct by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedProduct,
                        onExpandedChange = { expandedProduct = !expandedProduct }
                    ) {
                        val selectedProduct =
                            viewModel.products.find { it.id == viewModel.productoSeleccionado.value }
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedProduct?.nombre ?: "Seleccione el producto",
                            onValueChange = {},
                            label = { Text("Producto") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expandedProduct
                                )
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedProduct,
                            onDismissRequest = { expandedProduct = false }
                        ) {
                            viewModel.products.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text(product.nombre) },
                                    onClick = {
                                        viewModel.productoSeleccionado.value = product.id
                                        expandedProduct = false
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
                    DateInputField("Fecha de Ingreso", viewModel.fechaIngreso)
                    DateInputField("Fecha de Elaboración", viewModel.fechaElaboracion)
                    DateInputField("Fecha de Caducidad", viewModel.fechaCaducidad)

                    }
            },
            confirmButton = {
                Button(onClick = {
                    val esActualizar = viewModel.stockId.value != 0
                    viewModel.saveOrUpdateStock(
                        context,
                        esActualizar = esActualizar,
                        stockId = viewModel.stockId.value,
                        negocioId = negocioSeleccionado.id
                    )
                    showDialog = false
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0054A3)
                    )) {
                    Text(if (viewModel.stockId.value == 0) "Guardar Stock" else "Actualizar Stock")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false}) {
                    Text("Cancelar")
                }
            }
        )
    }
}


