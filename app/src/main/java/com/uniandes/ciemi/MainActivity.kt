package com.uniandes.ciemi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uniandes.ciemi.ui.screens.DashboardScreen
import com.uniandes.ciemi.ui.theme.CIEMITheme
import com.uniandes.ciemi.ui.screens.LoginScreen
import com.uniandes.ciemi.ui.screens.NewAccountScreen
import com.uniandes.ciemi.ui.screens.ResetScreen
import com.uniandes.ciemi.ui.screens.UserScreen
import com.uniandes.ciemi.view.DashboardViewModel
import com.uniandes.ciemi.view.LoginViewModel
import com.uniandes.ciemi.view.NewAccountViewModel
import com.uniandes.ciemi.view.ResetAccountViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CIEMITheme {
                val navController = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()
                val dashboardViewModel: DashboardViewModel = viewModel()
                val newAccountViewModel: NewAccountViewModel = viewModel()
                val resetAccountViewModel: ResetAccountViewModel = viewModel()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navController = navController, viewModel = loginViewModel)
                    }
                    composable("dashboard") {
                        DashboardScreen(
                            navController = navController,
                            viewModel = dashboardViewModel
                        )
                    }

                    composable("register") {
                        NewAccountScreen(viewModel = newAccountViewModel)
                    }
                    composable("reset") {
                        ResetScreen(viewModel = resetAccountViewModel)
                    }
                }
            }
        }

        }
    }
