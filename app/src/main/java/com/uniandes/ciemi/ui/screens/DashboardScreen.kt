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
import androidx.navigation.compose.rememberNavController
import com.uniandes.ciemi.utils.Constants
import com.uniandes.ciemi.view.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel(),
                    businessSelectViewModel: BusinessSelectViewModel = viewModel(),
                    navController: NavHostController
                    ,

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
    LaunchedEffect(negocioSeleccionado) {
        if (negocioSeleccionado != null) {
            businessSelectViewModel.setNegocio(negocioSeleccionado)
        }
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

                    NavigationDrawerItem(
                        label = { Text("Inicio") },
                        selected = currentSection == DashboardSection.HOME,
                        onClick = { viewModel.currentSection.value = DashboardSection.HOME }
                    )
                    if (role == "Admin") {
                        NavigationDrawerItem(
                            label = { Text("Gestión de usuarios") },
                            selected = currentSection == DashboardSection.USERS,
                            onClick = { viewModel.currentSection.value = DashboardSection.USERS }
                        )
                    }
                    NavigationDrawerItem(
                        label = { Text("Gestión de categorías") },
                        selected = currentSection == DashboardSection.CATEGORY,
                        onClick = { viewModel.currentSection.value = DashboardSection.CATEGORY }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestión de productos") },
                        selected = currentSection == DashboardSection.PRODUCT,
                        onClick = { viewModel.currentSection.value = DashboardSection.PRODUCT }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestión de stock") },
                        selected = currentSection == DashboardSection.STOCK,
                        onClick = { viewModel.currentSection.value = DashboardSection.STOCK }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestión de ventas") },
                        selected = currentSection == DashboardSection.SALES,
                        onClick = { viewModel.currentSection.value = DashboardSection.SALES }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestión de vendedores") },
                        selected = currentSection == DashboardSection.SELLER,
                        onClick = { viewModel.currentSection.value = DashboardSection.SELLER }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestión de clientes") },
                        selected = currentSection == DashboardSection.CLIENT,
                        onClick = { viewModel.currentSection.value = DashboardSection.CLIENT }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestión de negocios") },
                        selected = currentSection == DashboardSection.BUSINESS,
                        onClick = { viewModel.currentSection.value = DashboardSection.BUSINESS }
                    )
                    NavigationDrawerItem(
                        label = { Text("Configuración") },
                        selected = currentSection == DashboardSection.SETTINGS,
                        onClick = { viewModel.currentSection.value = DashboardSection.SETTINGS }
                    )
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
                when (currentSection) {
                    DashboardSection.HOME -> Text("Esta es la pantalla de inicio.")
                    DashboardSection.USERS -> UserScreen()
                    DashboardSection.CATEGORY -> CategoryScreen()
                    DashboardSection.SELLER -> SellerScreen()
                    DashboardSection.CLIENT -> ClientScreen()
                    DashboardSection.PRODUCT -> ProductScreen()
                    DashboardSection.STOCK -> StockScreen()
                    DashboardSection.SALES -> SalesScreen()
                    DashboardSection.BUSINESS -> BusinessScreen()
                    DashboardSection.SETTINGS -> Text("Configuración de la cuenta.")
                }
            }
        }
    }
}
