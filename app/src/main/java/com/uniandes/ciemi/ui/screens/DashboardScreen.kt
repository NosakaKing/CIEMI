package com.uniandes.ciemi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uniandes.ciemi.view.DashboardSection
import com.uniandes.ciemi.view.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val context = LocalContext.current

    LaunchedEffect (Unit) {
        viewModel.loadUserData(context)
    }
    val userName = viewModel.userName.value
    val role = viewModel.role.value
    val currentSection = viewModel.currentSection.value
    val topBarTitle = when (currentSection) {
        DashboardSection.HOME     -> "Inicio"
        DashboardSection.USERS    -> "Usuarios"
        DashboardSection.CLIENT   -> "Clientes"
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
                    Text(
                        text = "CIEMI",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    NavigationDrawerItem(
                        label = { Text("Inicio") },
                        selected = currentSection == DashboardSection.HOME,
                        onClick = { viewModel.currentSection.value = DashboardSection.HOME }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestión de usuarios") },
                        selected = currentSection == DashboardSection.USERS,
                        onClick = { viewModel.currentSection.value = DashboardSection.USERS }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestión de clientes") },
                        selected = currentSection == DashboardSection.CLIENT,
                        onClick = { viewModel.currentSection.value = DashboardSection.CLIENT }
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
                    DashboardSection.CLIENT -> ClientScreen()
                    DashboardSection.SETTINGS -> Text("Configuración de la cuenta.")
                }
            }
        }
    }
}
