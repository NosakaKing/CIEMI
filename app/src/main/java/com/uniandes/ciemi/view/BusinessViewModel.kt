package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.data.repository.CategoryRepository
import com.uniandes.ciemi.model.Business
import com.uniandes.ciemi.model.Category
import com.uniandes.ciemi.model.Client
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject

class BusinessViewModel : ViewModel() {

    val business = mutableStateListOf<Business>()
    val categorias = mutableStateListOf<Category>()
    var businessId = mutableIntStateOf(0)
    var nombre = mutableStateOf("")
    var categoriaSeleccionadaId = mutableIntStateOf(0)
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

    fun loadCategories(context: Context, negocioId: Int = 67) {
        CategoryRepository.loadCategories(
            context = context,
            negocioId = negocioId,
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


    fun saveOrUpdateBusiness(context: Context, esActualizar: Boolean, businessId: Int = 0) {
        val url = if (esActualizar) {
            "${Constants.BASE_URL}/Negocio/actualizar"
        } else {
            "${Constants.BASE_URL}/Negocio/crear"
        }

        val datos = JSONObject().apply {
            if (esActualizar) put("id", businessId)
            put("emprendedorId", Constants.getUserId(context))
            put("categoriaId", categoriaSeleccionadaId.value.toString())
            put("nombre", nombre.value)
            put("direccion", direccion.value)
            put("telefono", telefono.value)
            put("estado", estado.value.uppercase())
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
                val responseData = error.networkResponse?.data
                if (responseData != null) {
                    try {
                        val jsonString = String(responseData, Charsets.UTF_8)

                        val json = JSONObject(jsonString)

                        val succeeded = json.optBoolean("Succeeded", false)
                        val dataId = json.optInt("Data", 0)

                        if (succeeded && dataId != 0) {
                            message.value = if (esActualizar) "Negocio actualizado exitosamente" else "Negocio agregado exitosamente"
                            loadBusiness(context)
                            clearFields()
                        } else {
                            message.value = if (esActualizar) "Negocio actualizado exitosamente" else "Negocio agregado exitosamente"
                            loadBusiness(context)
                        }
                    } catch (ex: Exception) {
                        message.value = "Error al interpretar respuesta del servidor: ${ex.message}"
                    }
                } else {
                    message.value = error.message ?: "Error desconocido"
                }
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
