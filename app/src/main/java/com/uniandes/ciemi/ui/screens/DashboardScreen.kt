package com.uniandes.ciemi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uniandes.ciemi.utils.Constants
import com.uniandes.ciemi.view.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    businessSelectViewModel: BusinessSelectViewModel = viewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current

    val userName = viewModel.userName.value
    val role = viewModel.role.value
    val currentSection = viewModel.currentSection.value

    LaunchedEffect(Unit) {
        viewModel.loadUserData(context)
        viewModel.loadNegocios(context)
    }

    val negocioSeleccionado = viewModel.negocioSeleccionado.value

    val estadoNegocio = negocioSeleccionado?.estado ?: ""
    val puedeVerTodo = estadoNegocio != "Pendiente" && estadoNegocio != "Rechazado"

    LaunchedEffect(negocioSeleccionado) {
        if (negocioSeleccionado != null) {
            businessSelectViewModel.setNegocio(negocioSeleccionado)
        }
    }

    val seccionesDisponibles = if (!puedeVerTodo) {
        listOf(DashboardSection.HOME)
    } else {
        DashboardSection.values().toList()
    }

    val topBarTitle = when (currentSection) {
        DashboardSection.HOME -> "Inicio"
        DashboardSection.USERS -> "Usuarios"
        DashboardSection.CATEGORY -> "Categorías"
        DashboardSection.SELLER -> "Vendedores"
        DashboardSection.CLIENT -> "Clientes"
        DashboardSection.PRODUCT -> "Productos"
        DashboardSection.STOCK -> "Stock"
        DashboardSection.SALES -> "Ventas"
        DashboardSection.SALESCONTROL -> "Lista de Ventas"
        DashboardSection.BUSINESS -> "Negocios"
        DashboardSection.SETTINGS -> "Configuración"
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    NegocioDropdown(viewModel)

                    seccionesDisponibles.forEach { section ->
                        val label = when (section) {
                            DashboardSection.HOME -> "Inicio"
                            DashboardSection.USERS -> "Gestión de usuarios"
                            DashboardSection.CATEGORY -> "Gestión de categorías"
                            DashboardSection.SELLER -> "Gestión de vendedores"
                            DashboardSection.CLIENT -> "Gestión de clientes"
                            DashboardSection.PRODUCT -> "Gestión de productos"
                            DashboardSection.STOCK -> "Gestión de stock"
                            DashboardSection.SALES -> "Gestión de ventas"
                            DashboardSection.SALESCONTROL -> "Lista de ventas"
                            DashboardSection.BUSINESS -> "Gestión de negocios"
                            DashboardSection.SETTINGS -> "Configuración"
                        }

                        if (section != DashboardSection.USERS || role == "Admin") {
                            NavigationDrawerItem(
                                label = { Text(label) },
                                selected = currentSection == section,
                                onClick = { viewModel.currentSection.value = section }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Bienvenido, $userName",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Rol: $role",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            Constants.logout(context)
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0054A3)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(topBarTitle) })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(12.dp)
            ) {
                if (!puedeVerTodo) {
                    OnHoldScreen()
                } else {
                    when (currentSection) {
                        DashboardSection.HOME -> HomeScreen()
                        DashboardSection.USERS -> UserScreen()
                        DashboardSection.CATEGORY -> CategoryScreen()
                        DashboardSection.SELLER -> SellerScreen()
                        DashboardSection.CLIENT -> ClientScreen()
                        DashboardSection.PRODUCT -> ProductScreen()
                        DashboardSection.STOCK -> StockScreen()
                        DashboardSection.SALES -> SalesScreen()
                        DashboardSection.SALESCONTROL -> SalesTableScreen()
                        DashboardSection.BUSINESS -> BusinessScreen()
                        DashboardSection.SETTINGS -> Text("Configuración de la cuenta.")
                    }
                }
            }
        }
    }
}
