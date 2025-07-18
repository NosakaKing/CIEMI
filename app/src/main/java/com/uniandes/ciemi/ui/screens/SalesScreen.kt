package com.uniandes.ciemi.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.BusinessSelectViewModel
import com.uniandes.ciemi.view.SalesViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: SalesViewModel = viewModel(),
    businessSelect: BusinessSelectViewModel = viewModel()
) {
    val context = LocalContext.current
    val message by viewModel.message
    var showDialog by remember { mutableStateOf(false) }
    var clienteExpandido by remember { mutableStateOf(true) }

    val negocioSeleccionado = businessSelect.negocioSeleccionado.value
    if (negocioSeleccionado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seleccione un negocio para ver los clientes")
        }
        return
    }

    var search by remember { mutableStateOf("") }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(text = "Datos de la Factura", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar por identificación") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.loadClienteById(context, clienteId = search)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0054A3))
        ) {
            Text("Buscar Cliente")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { clienteExpandido = !clienteExpandido }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Datos del Cliente", style = MaterialTheme.typography.titleMedium)
                    Icon(
                        imageVector = if (clienteExpandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir/Colapsar"
                    )
                }

                AnimatedVisibility(visible = clienteExpandido) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.identificacion.value,
                            onValueChange = { viewModel.identificacion.value = it },
                            label = { Text("Identificación") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = viewModel.nombres.value,
                            onValueChange = { viewModel.nombres.value = it },
                            label = { Text("Nombres") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = viewModel.primerApellido.value,
                            onValueChange = { viewModel.primerApellido.value = it },
                            label = { Text("Primer Apellido") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = viewModel.segundoApellido.value,
                            onValueChange = { viewModel.segundoApellido.value = it },
                            label = { Text("Segundo Apellido") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = viewModel.correo.value,
                            onValueChange = { viewModel.correo.value = it },
                            label = { Text("Correo") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Seleccionar Productos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0054A3))
        ) {
            Text("Agregar Producto")
        }

        if (viewModel.productosSeleccionados.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Productos Seleccionados", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(viewModel.productosSeleccionados) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Nombre: ${producto.producto.nombre}")
                                Text("Cantidad: ${producto.cantidadElegida}")
                                Text("Precio: ${producto.precioVenta}")
                                Text("Total: ${producto.cantidadElegida.toInt() * producto.precioVenta}")
                            }
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                onClick = {
                                    viewModel.productosSeleccionados.remove(producto)
                                    Toast.makeText(
                                        context,
                                        "Producto eliminado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            ) {
                                Text("Eliminar", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.createSale(context, negocioSeleccionado.id)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0054A3))
        ) {
            Text("Registrar Venta")
        }

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Lista de Productos") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {
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
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProduct)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.loadStock(
                                context,
                                negocioId = negocioSeleccionado.id,
                                search = viewModel.productoSeleccionado.value
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0054A3))
                    ) {
                        Text("Buscar")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    viewModel.stocks.forEach { stock ->
                        var cantidadElegida by remember { mutableStateOf("1") }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Nombre: ${stock.producto.nombre}")
                                Text("Estado: ${stock.producto.estado}")
                                Text("Stock Disponible: ${stock.cantidad}")
                                Text("Precio de Compra: ${stock.precioCompra}")
                                Text("Precio de Venta: ${stock.precioVenta}")
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = cantidadElegida,
                                    onValueChange = {
                                        if (it.all(Char::isDigit)) {
                                            cantidadElegida = it
                                        }
                                    },
                                    label = { Text("Cantidad a agregar") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val cantidadInt = cantidadElegida.toIntOrNull() ?: 0
                                        if (cantidadInt <= 0 || cantidadInt > stock.cantidad) {
                                            Toast.makeText(
                                                context,
                                                "Cantidad no válida",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Button
                                        }

                                        if (!viewModel.productosSeleccionados.any { it.id == stock.id }) {
                                            val stockConCantidad = stock.copy(
                                                cantidadElegida = cantidadInt.toString()
                                            )
                                            viewModel.productosSeleccionados.add(stockConCantidad)
                                            Toast.makeText(
                                                context,
                                                "Producto agregado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "El producto ya ha sido agregado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Agregar")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0054A3))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}

