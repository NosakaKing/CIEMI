package com.uniandes.ciemi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.ProductViewModel

@Composable
fun ProductScreen(viewModel: ProductViewModel = viewModel()) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val products = viewModel.products
    val message by viewModel.message

    LaunchedEffect(true) {
        viewModel.loadProducts(context)
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
            modifier = Modifier.fillMaxWidth()
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
                            search = search,
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
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Codigo: ${product.codigo}")
                        Text("Nombre: ${product.nombre}")
                        Text("Precio Venta: ${product.precioVenta}")
                        Text("Categoria: ${product.nombreCategoria}")
                        Text("Estado: ${product.estado}")
                        Spacer(modifier = Modifier.height(8.dp))

                    }
                }
            }
        }
    }


}
