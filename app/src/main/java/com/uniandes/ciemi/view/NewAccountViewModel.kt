package com.uniandes.ciemi.view

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.data.repository.CategoryRepository
import com.uniandes.ciemi.model.Category
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject

class NewAccountViewModel : ViewModel() {
    val categorias = mutableStateListOf<Category>()
    var categoriaSeleccionadaId = mutableStateOf(0)
    var identificacion = mutableStateOf("")
    var userName = mutableStateOf("")
    var nombre = mutableStateOf("")
    var apellido = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    var telefono = mutableStateOf("")
    var ciudadOrigen = mutableStateOf("")
    var nombreNegocio = mutableStateOf("")
    var direccionNegocio = mutableStateOf("")
    var telefonoNegocio = mutableStateOf("")
    var descripcion = mutableStateOf("")
    var categoriaNegocio = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun loadCategories(context: Context) {
        CategoryRepository.loadCategories(
            context = context,
            negocioId = 0,
            tipo = "NEGOCIO",
            onSuccess = {
                categorias.clear()
                categorias.addAll(it)
            },
            onError = {
                println("Error al cargar categorías: $it")
            }
        )
    }

    fun guardarPersona(context: Context) {
        val url = "${Constants.BASE_URL}/Account/register"
        val datos = JSONObject()

        try {
            datos.put("identificacion", identificacion.value)
            datos.put("userName", userName.value)
            datos.put("nombre", nombre.value)
            datos.put("apellido", apellido.value)
            datos.put("email", email.value)
            datos.put("password", password.value)
            datos.put("confirmPassword", confirmPassword.value)
            datos.put("telefono", telefono.value)
            datos.put("ciudadOrigen", ciudadOrigen.value)
            datos.put("nombreNegocio", nombreNegocio.value)
            datos.put("direccionNegocio", direccionNegocio.value)
            datos.put("telefonoNegocio", telefonoNegocio.value)
            datos.put("descripcion", descripcion.value)
            datos.put("categoriaNegocio", categoriaSeleccionadaId.value)

            Log.d("JSON_ENVIADO", datos.toString(4))

        } catch (e: JSONException) {
            message.value = "Error al crear JSON: ${e.message}"
            return
        }

        val rq = Volley.newRequestQueue(context)
        val js = JsonObjectRequest(
            Request.Method.POST, url, datos,
            { response ->
                try {
                    val obj = JSONObject(response.toString())
                    if(obj.getBoolean("succeeded")) {
                        message.value = obj.optString("message")
                        clearFields()
                    } else {
                        message.value = obj.optString("Message", "Error en el registro")
                    }
                } catch (e: JSONException) {
                    message.value = "Error al procesar respuesta: ${e.message}"
                }
            },
            { error ->
                try {
                    val statusCode = error.networkResponse?.statusCode
                    val responseBody = error.networkResponse?.data?.let { String(it) }

                    if (responseBody != null) {
                        val errorJson = JSONObject(responseBody)
                        val errorMessage = errorJson.optString("Message", "Error desconocido")
                        message.value = "[$statusCode] $errorMessage"
                    } else {
                        message.value = "[$statusCode] Error sin cuerpo de respuesta"
                    }
                } catch (e: Exception) {
                    message.value = "Error al leer la respuesta de error: ${e.message}"
                }
            }
        )
        js.retryPolicy = com.android.volley.DefaultRetryPolicy(
            150000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        rq.add(js)
    }

    fun clearMessage() {
        message.value = null
    }

    fun clearFields() {
        identificacion.value = ""
        userName.value = ""
        nombre.value = ""
        apellido.value = ""
        email.value = ""
        password.value = ""
        confirmPassword.value = ""
        telefono.value = ""
        ciudadOrigen.value = ""
        nombreNegocio.value = ""
        direccionNegocio.value = ""
        telefonoNegocio.value = ""
        descripcion.value = ""
        categoriaNegocio.value = ""

    }
}
