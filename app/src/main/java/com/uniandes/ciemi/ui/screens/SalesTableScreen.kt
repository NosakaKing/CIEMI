package com.uniandes.ciemi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.view.BusinessSelectViewModel
import com.uniandes.ciemi.view.SalesTableViewModel

@Composable
fun SalesTableScreen(
    viewModel: SalesTableViewModel = viewModel(),
    businessSelect: BusinessSelectViewModel = viewModel()
) {
    val context = LocalContext.current
    val message by viewModel.message
    val sales = viewModel.sales

    val negocioSeleccionado = businessSelect.negocioSeleccionado.value
    if (negocioSeleccionado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seleccione un negocio para ver las ventas")
        }
        return
    }

    val fechaInicio = viewModel.fechaInicio
    val fechaFin = viewModel.fechaFin

    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 10

    LaunchedEffect(negocioSeleccionado, currentPage) {
        viewModel.loadSales(
            context,
            negocioSeleccionado.id,
            fechaInicio.value,
            fechaFin.value,
            pageNumber = currentPage,
            pageSize = pageSize
        )
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DatePickerField("Fecha inicio", fechaInicio.value) { viewModel.fechaInicio.value = it }
            DatePickerField("Fecha fin", fechaFin.value) { viewModel.fechaFin.value = it }
        }

        Button(
            onClick = {
                currentPage = 1
                negocioSeleccionado.let {
                    viewModel.loadSales(
                        context,
                        it.id,
                        fechaInicio.value,
                        fechaFin.value,
                        pageNumber = currentPage,
                        pageSize = pageSize
                    )
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Filtrar")
        }

        LazyColumn {
            items(sales) { sale ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cliente: ${sale.clienteNombres} ${sale.clientePrimerApellido}")
                        Text("Identificación: ${sale.clienteIdentificacion}")
                        Text("Fecha: ${sale.fecha}")
                        Text("Subtotal: ${sale.subtotal}")
                        Text("Total: ${sale.total}")
                        Button(
                            colors = ButtonDefaults.buttonColors(),
                            onClick = {
                                viewModel.downloadSalePDF(context, sale.id)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Eliminar",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            item {
                PaginationControls(
                    currentPage = currentPage,
                    pageSize = pageSize,
                    itemsLoaded = sales.size,
                    onPrevious = { if (currentPage > 1) currentPage-- },
                    onNext = { currentPage++ }
                )
            }
        }
    }
}
