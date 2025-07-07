package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.model.Client
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject

class ClientViewModel : ViewModel() {

    val clients = mutableStateListOf<Client>()
    var clienteId = mutableIntStateOf(0)
    var identificacion = mutableStateOf("")
    var nombres = mutableStateOf("")
    var primerApellido = mutableStateOf("")
    var segundoApellido = mutableStateOf("")
    var telefono = mutableStateOf("")
    var email = mutableStateOf("")
    var ciudad = mutableStateOf("")
    var direccion = mutableStateOf("")
    var message = mutableStateOf<String?>(null)


    fun setClientToEdit(client: Client) {
        clienteId.value = client.id
        nombres.value = client.nombres
        identificacion.value = client.identificacion
        primerApellido.value = client.primerApellido
        segundoApellido.value = client.segundoApellido
        email.value = client.email
        telefono.value = client.telefono
        ciudad.value = client.ciudad
        direccion.value = client.direccion
    }


    fun loadClients(
        context: Context,
        negocioId: Int,
        identificacionBusqueda: String = "",
        primerApellidoBusqueda: String = "",
        pageNumber: Int = 1,
        pageSize: Int = 10,

    ) {
        val url = "${Constants.BASE_URL}/Cliente/listar?" +
                "Identificacion=${identificacionBusqueda}&" +
                "PrimerApellido=${primerApellidoBusqueda}&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                if (response.getBoolean("succeeded")) {
                    val array = response.getJSONArray("data")
                    clients.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        clients.add(
                            Client(
                                id = obj.getInt("id"),
                                identificacion = obj.getString("identificacion"),
                                nombres = obj.getString("nombres"),
                                primerApellido = obj.getString("primerApellido"),
                                segundoApellido = obj.getString("segundoApellido"),
                                email = obj.getString("email"),
                                telefono = obj.getString("telefono"),
                                ciudad = obj.getString("ciudad"),
                                direccion = obj.getString("direccion")
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


    fun saveOrUpdateClient(context: Context, esActualizar: Boolean, clienteId: Int = 0, negocioId: Int,) {
        val url = if (esActualizar) {
            "${Constants.BASE_URL}/Cliente/actualizar"
        } else {
            "${Constants.BASE_URL}/Cliente/crear"
        }

        val datos = JSONObject().apply {
            if (esActualizar) put("id", clienteId)
            put("nombres", nombres.value)
            put("identificacion", identificacion.value)
            put("primerApellido", primerApellido.value)
            put("segundoApellido", segundoApellido.value)
            put("email", email.value)
            put("telefono", telefono.value)
            put("ciudad", ciudad.value)
            put("direccion", direccion.value)
            put("negocioId", negocioId)
        }

        val metodoHttp = if (esActualizar) Request.Method.PUT else Request.Method.POST

        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(
            metodoHttp, url, datos,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        message.value = if (esActualizar) "Cliente actualizado exitosamente" else "Cliente agregado exitosamente"
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

    fun clearFields() {
        identificacion.value = ""
        nombres.value = ""
        primerApellido.value = ""
        segundoApellido.value = ""
        telefono.value = ""
        email.value = ""
        ciudad.value = ""
        direccion.value = ""
    }
}
