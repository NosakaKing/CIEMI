package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.model.Business
import com.uniandes.ciemi.model.Client
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject

class BusinessViewModel : ViewModel() {

    val business = mutableStateListOf<Business>()
    var businessId = mutableIntStateOf(0)
    var nombre = mutableStateOf("")
    var direccion = mutableStateOf("")
    var telefono = mutableStateOf("")
    var estado = mutableStateOf("")
    var descripcion = mutableStateOf("")
    var message = mutableStateOf<String?>(null)



    fun setBusinessEdit(business: Business) {
        businessId.value = business.id
        nombre.value = business.nombre
        direccion.value = business.direccion
        telefono.value = business.telefono
        estado.value = business.estado
        descripcion.value = business.descripcion
    }


    fun loadBusiness(
        context: Context,
        search: String = "",
    ) {
        val emprendedorId = Constants.getUserId(context)
        val url = "${Constants.BASE_URL}/Negocio/listar?EmprendedorId=$emprendedorId&Nombre=$search"
        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                if (response.getBoolean("succeeded")) {
                    val array = response.getJSONArray("data")
                    business.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        business.add(
                            Business(
                                id = obj.getInt("id"),
                                nombre = obj.getString("nombre"),
                                direccion = obj.getString("direccion"),
                                telefono = obj.getString("telefono"),
                                estado = obj.getString("estado"),
                                descripcion = obj.getString("descripcion"),
                                categoriaId = obj.getInt("categoriaId")
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


    fun saveOrUpdateBusiness(context: Context, esActualizar: Boolean, businessId: Int = 0) {
        val url = if (esActualizar) {
            "${Constants.BASE_URL}/Negocio/actualizar"
        } else {
            "${Constants.BASE_URL}/Negocio/crear"
        }


        val datos = JSONObject().apply {
            if (esActualizar) put("id", businessId)
            put("emprendedorId", "")
            put("categoriaId", "")
            put("nombre", nombre.value)
            put("direccion", direccion.value)
            put("telefono", telefono.value)
            put("estado", estado.value)
            put("descripcion", descripcion.value)

        }

        val metodoHttp = if (esActualizar) Request.Method.PUT else Request.Method.POST

        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(
            metodoHttp, url, datos,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        message.value = if (esActualizar) "Negocio actualizado exitosamente" else "Negocio agregado exitosamente"
                        loadBusiness(context)
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
        businessId.intValue = 0
        nombre.value = ""
        direccion.value = ""
        telefono.value = ""
        estado.value = ""
        descripcion.value = ""
    }
}
