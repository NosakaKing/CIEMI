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

class ResetAccountViewModel : ViewModel() {
    var email = mutableStateOf("")
    var message = mutableStateOf<String?>(null)


    fun resetPassword(context: Context) {
        val url = "${Constants.BASE_URL}/Account/forgotPassword?correo=${email.value}"
        val datos = JSONObject()
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
        js.retryPolicy = DefaultRetryPolicy(
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
        email.value = ""

    }
}
