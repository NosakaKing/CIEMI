package com.uniandes.ciemi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    LaunchedEffect(negocioSeleccionado) {
        viewModel.loadSales(context, negocioSeleccionado.id)
    }
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

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
                    }
                }
            }
        }
    }
}
