package com.uniandes.ciemi.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.BusinessSelectViewModel
import com.uniandes.ciemi.view.CategoryViewModel

@Composable
fun CategoryScreen(viewModel: CategoryViewModel = viewModel(),
        businessViewModel: BusinessSelectViewModel = viewModel()
) {
    val context = LocalContext.current
    val negocioSeleccionado = businessViewModel.negocioSeleccionado.value

    if (negocioSeleccionado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seleccione un negocio para ver los clientes")
        }
        return
    }
    var showDialog by remember { mutableStateOf(false) }
    val categories = viewModel.categories
    val message by viewModel.message

    LaunchedEffect(negocioSeleccionado) {
        viewModel.loadCategories(context, negocioSeleccionado.id)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(viewModel.categoriaGuardada.value) {
        if (viewModel.categoriaGuardada.value) {
            viewModel.loadCategories(context, negocioSeleccionado.id)
            viewModel.resetCategoriaGuardada()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Lista de Categorías", style = MaterialTheme.typography.titleMedium)
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
            Text("Agregar Categoría")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(categories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${category.nombre}")
                        Text("Descripción: ${category.descripcion}")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva Categoría") },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.nombre.value,
                        onValueChange = { viewModel.nombre.value = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.descripcion.value,
                        onValueChange = { viewModel.descripcion.value = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveCategory(context, negocioId = negocioSeleccionado.id)
                    showDialog = false
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0054A3)
                    )
                    ) {
                    Text("Guardar Categoría")
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
fun CategoryScreenPreview() {
    CategoryScreen(viewModel = CategoryViewModel())
}


