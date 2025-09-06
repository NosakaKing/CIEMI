package com.uniandes.ciemi.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ciemi.utils.Constants
import com.uniandes.ciemi.view.BusinessSelectViewModel
import com.uniandes.ciemi.view.HomeViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Pie
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = HomeViewModel(),
    businessSelect: BusinessSelectViewModel = viewModel()
) {
    val context = LocalContext.current
    val message by viewModel.message
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ganancias", "Inventario", "Ventas")
    val fechaInicio = viewModel.fechaInicio
    val fechaFin = viewModel.fechaFin


    val negocioSeleccionado = businessSelect.negocioSeleccionado.value
    if (negocioSeleccionado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seleccione un negocio para ver las ventas")
        }
        return
    }

    LaunchedEffect(selectedTabIndex, negocioSeleccionado) { negocioSeleccionado.let { when (selectedTabIndex) { 0 -> { viewModel.loadEarningProducts(context, it.id)
        viewModel.loadEarningClients(context, it.id)
        viewModel.loadCategory(context, it.id) } 1 -> { viewModel.loadStatus(context, it.id)
        viewModel.loadClasification(context,it.id)
        viewModel.loadMes(context, it.id) } 2 -> { viewModel.loadSalesMes(context, it.id)
        viewModel.loadSellerMes(context, it.id) } } } }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            DatePickerField("Fecha inicio", fechaInicio.value) { viewModel.fechaInicio.value = it }
            DatePickerField("Fecha fin", fechaFin.value) { viewModel.fechaFin.value = it }
        }

        Button(
            onClick = {
                negocioSeleccionado.let {
                    when (selectedTabIndex) {
                        0 -> {
                            viewModel.loadEarningProducts(context, it.id, fechaInicio.value, fechaFin.value)
                            viewModel.loadEarningClients(context, it.id, fechaInicio.value, fechaFin.value)
                            viewModel.loadCategory(context, it.id, fechaInicio.value, fechaFin.value)
                        }
                        1 -> {
                            viewModel.loadStatus(context, it.id, fechaInicio.value, fechaFin.value)
                            viewModel.loadClasification(context, it.id, fechaInicio.value, fechaFin.value)
                            viewModel.loadMes(context, it.id, fechaInicio.value, fechaFin.value)
                        }
                        2 -> {
                            viewModel.loadSalesMes(context, it.id, fechaInicio.value, fechaFin.value)
                            viewModel.loadSellerMes(context, it.id, fechaInicio.value, fechaFin.value)
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Filtrar")
        }


        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTabIndex) {
            0 -> DatosGanancias(viewModel)
            1 -> DatosInventario(viewModel)
            2 -> DatosVentas(viewModel)
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DatePickerField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        val parsed = sdf.parse(date)
        parsed?.let { calendar.time = it }
    } catch (_: Exception) {}

    OutlinedTextField(
        value = formatDisplayDate(date),
        onValueChange = {},
        modifier = Modifier
            .padding(4.dp),
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                        onDateSelected(selectedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
            }
        }
    )
}

fun formatDisplayDate(date: String): String {
    return try {
        val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfOut = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val parsed = sdfIn.parse(date)
        parsed?.let { sdfOut.format(it) } ?: date
    } catch (_: Exception) {
        date
    }
}

@Composable
fun DatosGanancias(viewModel: HomeViewModel) {
    val earningProducts = viewModel.earningProducts
    val earningClients = viewModel.earningClients
    val earningCategories = viewModel.earningCategories

    Column(modifier = Modifier.fillMaxSize().padding(top = 4.dp).verticalScroll(rememberScrollState())) {
        if (earningProducts.isNotEmpty()) {
            Text("Ganancias por producto")
            val productBarsData = remember(earningProducts) {
                listOf(
                    Bars(
                        label = "Ganancias",
                        values = earningProducts.mapIndexed { index, product ->
                            Bars.Data(
                                label = product.nombre,
                                value = product.gananciaTotal,
                                color = SolidColor(randomColor(index))
                            )
                        }
                    )
                )
            }
            ColumnChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(vertical = 16.dp),
                data = productBarsData,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                Text("No hay datos de ganancias por producto.")
            }
        }

        if (earningClients.isNotEmpty()) {
            Text("Ganancias por cliente")
            val clientBarsData = remember(earningClients) {
                listOf(
                    Bars(
                        label = "Ganancias",
                        values = earningClients.mapIndexed { index, client ->
                            Bars.Data(
                                label = client.nombreCliente,
                                value = client.totalGanancias,
                                color = SolidColor(randomColor(index))
                            )
                        }
                    )
                )
            }
            ColumnChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(vertical = 16.dp),
                data = clientBarsData,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                Text("No hay datos de ganancias por cliente.")
            }
        }

        if (earningCategories.isNotEmpty()) {
            Text("Ganancias por Categoría")
            var selectedIndex by remember { mutableIntStateOf(-1) }
            val pieData = earningCategories.mapIndexed { index, category ->
                Pie(
                    label = category.categoria,
                    data = category.ganancias,
                    color = randomColor(index),
                    selectedColor = randomColor(index),
                    selected = index == selectedIndex
                )
            }
            PieChart(
                modifier = Modifier.size(200.dp),
                data = pieData,
                onPieClick = { clickedPie ->
                    selectedIndex = pieData.indexOf(clickedPie)
                },
                selectedScale = 1.2f,
                scaleAnimEnterSpec = spring<Float>(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(300),
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                style = Pie.Style.Fill
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay datos de ganancias por categoría.")
            }
        }
    }
}

@Composable
fun DatosInventario(viewModel: HomeViewModel) {
    val status = viewModel.inventoryStatus
    val clasification = viewModel.inventoryClasification
    val mensual = viewModel.inventoryMes

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Text("Estado del Inventario", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp)) }

        items(status) { statusItem ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Producto: ${statusItem.productoNombre}")
                    Text("Existencias: ${statusItem.existencias}")
                    Text("Ventas: ${statusItem.venta}")
                    Text("Stock Mínimo: ${statusItem.stockMin}")
                    Text("Stock Máximo: ${statusItem.stockMax}")
                    Text("Estado: ${statusItem.estatusInvt}")
                }
            }
        }

        item { Text("Clasificación ABC", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp)) }

        items(clasification) { clasificationItem ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Producto: ${clasificationItem.productoNombre}")
                    Text("Total Vendido: ${clasificationItem.totalVendido}")
                    Text("Acumulado: ${clasificationItem.acumulado}")
                    Text("Porcentaje: ${clasificationItem.porcentajeAcumulado}")
                    Text("Clasificación: ${clasificationItem.clasificacionABC}")
                }
            }
        }

        item { Text("Inventario Por Mes", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp)) }

        items(mensual) { mensual ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mes: ${Constants.nombreDelMes(mensual.mes)}")
                    Text("Total Vendido: ${mensual.totalVentas}")
                    Text("Inv Inicial: ${mensual.costoInvInicial}")
                    Text("Inv Final: ${mensual.costoInvFinal}")
                }
            }
        }
    }
}

@Composable
fun DatosVentas(viewModel: HomeViewModel) {
    val salesMensual = viewModel.salesyMes
    val sellerMensual = viewModel.sellerMes

    Column(modifier = Modifier.fillMaxSize().padding(top = 4.dp).verticalScroll(rememberScrollState())) {
        if (salesMensual.isNotEmpty()) {
            Text("Ventas Por Mes")
            val productBarsData = remember(salesMensual) {
                listOf(
                    Bars(
                        label = "Ganancias",
                        values = salesMensual.mapIndexed { index, sales ->
                            Bars.Data(
                                label = Constants.nombreDelMes(sales.mes),
                                value = sales.totalVentas,
                                color = SolidColor(randomColor(index))
                            )
                        }
                    )
                )
            }
            ColumnChart(modifier = Modifier.fillMaxWidth().height(250.dp).padding(vertical = 16.dp), data = productBarsData,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) { Text("No hay datos de ventas.") }
        }

        if (sellerMensual.isNotEmpty()) {
            Text("Vendedor con más ventas")
            val productBarsData = remember(sellerMensual) {
                listOf(
                    Bars(
                        label = "Ventas",
                        values = sellerMensual.mapIndexed { index, sales ->
                            Bars.Data(
                                label = sales.nombreVendedor + " " + sales.apellidoVendedor,
                                value = sales.totalVentas,
                                color = SolidColor(randomColor(index))
                            )
                        }
                    )
                )
            }
            ColumnChart(modifier = Modifier.fillMaxWidth().height(250.dp).padding(vertical = 16.dp), data = productBarsData,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) { Text("No hay datos de ventas.") }
        }
    }
}

fun randomColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF3F51B5),
        Color(0xFFF44336),
        Color(0xFF4CAF50),
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFF2196F3),
    )
    return colors[index % colors.size]
}
