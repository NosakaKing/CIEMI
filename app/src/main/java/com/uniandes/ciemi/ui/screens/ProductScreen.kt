package com.uniandes.ciemi.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.uniandes.ciemi.view.ProductViewModel
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(viewModel: ProductViewModel = viewModel()) {
    val context = LocalContext.current
    val products = viewModel.products
    val categorias = viewModel.categorias
    val message by viewModel.message
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.loadCategorias(context)
        viewModel.loadProducts(context)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lista de Productos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.clearFields()
            viewModel.loadCategorias(context)
            showDialog = true
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Agregar Producto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Código: ${product.codigo}")
                        Text("Nombre: ${product.nombre}")
                        Text("Descripción: ${product.descripcion}")
                        Text("Categoría: ${product.nombreCategoria}")
                        Text("Negocio: ${product.nombreNegocio}")
                        Text("Estado: ${product.estado}")
                        Text("IVA: ${product.iva}")

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.setProductToEdit(product)
                            viewModel.loadCategorias(context)
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
        val launcher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
            viewModel.imagenUri.value = uri
        }
        val imagenUri by viewModel.imagenUri

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (viewModel.productoId.value == 0) "Nuevo Producto" else "Editar Producto") },
            text = {
                Column {
                    OutlinedTextField(viewModel.codigo.value, { viewModel.codigo.value = it }, label = { Text("Código") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(viewModel.nombre.value, { viewModel.nombre.value = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(viewModel.descripcion.value, { viewModel.descripcion.value = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(viewModel.estado.value, { viewModel.estado.value = it }, label = { Text("Estado") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(viewModel.iva.value.toString(), { viewModel.iva.value = it.toDoubleOrNull() ?: 0.0 }, label = { Text("IVA") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                    // Selector de categoría
                    var expanded by remember { mutableStateOf(false) }
                    var selectedCategoriaNombre by remember {
                        mutableStateOf(categorias.find { it.id == viewModel.categoriaId.value }?.nombre ?: "Seleccionar categoría")
                    }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = selectedCategoriaNombre,
                            onValueChange = {},
                            label = { Text("Categoría") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            categorias.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat.nombre) }, onClick = {
                                    viewModel.categoriaId.value = cat.id
                                    selectedCategoriaNombre = cat.nombre
                                    expanded = false
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Imagen seleccionada:", style = MaterialTheme.typography.bodyMedium)
                    if (imagenUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imagenUri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.size(120.dp).clickable { launcher.launch("image/*") }
                        )
                    } else {
                        Box(modifier = Modifier.size(120.dp).clickable { launcher.launch("image/*") }, contentAlignment = Alignment.Center) {
                            Text("Seleccionar Imagen")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (viewModel.productoId.value == 0) viewModel.saveProduct(context)
                    else viewModel.updateProduct(context)
                    showDialog = false
                }) {
                    Text(if (viewModel.productoId.value == 0) "Guardar" else "Actualizar")
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
fun ProductScreenPreview() {
    ProductScreen(viewModel = ProductViewModel())
}


