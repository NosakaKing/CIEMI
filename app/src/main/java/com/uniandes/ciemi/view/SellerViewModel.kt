package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.model.Seller
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject

class SellerViewModel : ViewModel() {

    val sellers = mutableStateListOf<Seller>()
    var sellerGuardado = mutableStateOf(false)
    var identificacion = mutableStateOf("")
    var nombre = mutableStateOf("")
    var apellido = mutableStateOf("")
    var telefono = mutableStateOf("")
    var email = mutableStateOf("")
    var ciudad = mutableStateOf("")
    var userName = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun loadSeller(
        context: Context,
        negocioId: Int,
    ) {
        val url = "${Constants.BASE_URL}/Account/listarVendedores?Identificacion=&Email=&NegocioId=${negocioId}&pageNumber=1&pageSize=10"
        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                if (response.getBoolean("succeeded")) {
                    val array = response.getJSONArray("data")
                    sellers.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        sellers.add(
                            Seller(
                                identificacion = obj.getString("identificacion"),
                                nombres = obj.getString("nombre"),
                                apellido = obj.getString("apellido"),
                                email = obj.getString("email"),
                                telefono = obj.getString("telefono"),
                                ciudad = obj.getString("ciudadOrigen"),
                                userName = obj.getString("userName"),
                            )
                        )
                    }
                } else {
                    println(response.getString("message"))
                }
            },
            { error ->
                println("Error: ${error.message}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return Constants.getAuthHeaders(context)
            }
        }

        rq.add(js)
    }

    fun saveSeller(context: Context,  negocioId: Int,) {
        val url = "${Constants.BASE_URL}/Account/registerSeller"
        val datos = JSONObject().apply {
            put("identificacion", identificacion.value)
            put("userName", userName.value)
            put("nombre", nombre.value)
            put("email", email.value)
            put("password", password.value)
            put("confirmPassword", confirmPassword.value)
            put("apellido", apellido.value)
            put("telefono", telefono.value)
            put("ciudadOrigen", ciudad.value)
            put("negocioId", negocioId)
        }

        val metodoHttp = Request.Method.POST
        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(
            metodoHttp, url, datos,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        message.value =
                          "Vendedor agregado exitosamente"
                        sellerGuardado.value = true
                        clearFields()
                    } else {
                        message.value = response.getString("message")
                    }
                } catch (e: JSONException) {
                    message.value = e.message
                }
            },
            { error ->
                message.value = error.message
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return Constants.getAuthHeaders(context)
            }
        }

        rq.add(js)
    }

    fun clearMessage() {
        message.value = null
    }

    fun resetSellerGuardado() {
        sellerGuardado.value = false
    }

    fun clearFields() {
        identificacion.value = ""
        nombre.value = ""
        apellido.value = ""
        telefono.value = ""
        email.value = ""
        ciudad.value = ""
        userName.value = ""
        password.value = ""
        confirmPassword.value = ""
    }
}
