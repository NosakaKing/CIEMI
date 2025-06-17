package com.uniandes.ciemi.view
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    private fun validate(): Boolean {
        if (email.value.isBlank()) {
            message.value = "El email está vacío"
            return false
        }
        if (password.value.isBlank()) {
            message.value = "La contraseña está vacía"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            message.value = "El email no es válido"
            return false
        }

        return true
    }

    fun login() {
        if (!validate()) {

            return
        }
        message.value = "Inicio de sesión correcto"
    }

    fun clearMessage() {
        message.value = null
    }
}
