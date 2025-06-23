package com.uniandes.ciemi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.uniandes.ciemi.ui.theme.CIEMITheme
import com.uniandes.ciemi.ui.screens.LoginScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CIEMITheme {
                LoginScreen()
            }
        }
    }
}
