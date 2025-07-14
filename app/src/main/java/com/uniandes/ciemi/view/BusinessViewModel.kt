package com.uniandes.ciemi.view

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.data.repository.CategoryRepository
import com.uniandes.ciemi.data.repository.EntrepreneurRepository
import com.uniandes.ciemi.model.Business
import com.uniandes.ciemi.model.Category
import com.uniandes.ciemi.model.Entrepreneur
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject

class BusinessViewModel : ViewModel() {

    val business = mutableStateListOf<Business>()
    val categorias = mutableStateListOf<Category>()
    val emprendedores = mutableStateListOf<Entrepreneur>()
    var businessId = mutableIntStateOf(0)
    var role = mutableStateOf("")
    var nombre = mutableStateOf("")
    var categoriaSeleccionadaId = mutableIntStateOf(0)
    var empresaSeleccionadaId = mutableStateOf("")
    var direccion = mutableStateOf("")
    var telefono = mutableStateOf("")
    var estado = mutableStateOf("")
    var descripcion = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun loadUserData(context: Context) {
        role.value = Constants.getUserRole(context)
    }

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
        val isAdmin = Constants.getUserRole(context) == "Admin"
        val baseUrl = "${Constants.BASE_URL}/Negocio/listar"

        val url = if (isAdmin) {
            "$baseUrl?Nombre=$search"
        } else {
            "$baseUrl?EmprendedorId=$emprendedorId&Nombre=$search"
        }
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

    fun loadEntrepreneur(context: Context) {
        EntrepreneurRepository.loadEntrepreneur(
            context = context,
            onSuccess = {
                emprendedores.clear()
                emprendedores.addAll(it)
                Log.d("Emprendedores", emprendedores.toString())

            },
            onError = {
                println("Error al cargar: $it")
                Log.e("EntrepreneurError", it)
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
            if(role.value == "Admin") {
                put("emprendedorId", empresaSeleccionadaId.value)
            }
            put("descripcion", descripcion.value)
        }

        val metodoHttp = if (esActualizar) Request.Method.PUT else Request.Method.POST

        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(
            metodoHttp, url, datos,
            { response ->
                try {
                    val obj = JSONObject(response.toString())
                    if(obj.getBoolean("succeeded")) {
                        message.value = obj.optString("message")
                        loadBusiness(context)
                        clearFields()
                    } else {
                        message.value = obj.getString("message")
                    }
                } catch (e: JSONException) {
                    message.value = e.message
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
        {
            override fun getHeaders(): MutableMap<String, String> {
                return Constants.getAuthHeaders(context)
            }
        }

        js.retryPolicy = com.android.volley.DefaultRetryPolicy(
            150000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        rq.add(js)
    }


    fun aproveeBusiness(context: Context, businessId: Int, aprobado: Boolean) {
        val url = "${Constants.BASE_URL}/Negocio/aprobar?negocioId=$businessId&aprobado=$aprobado"
        val rq = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
               message.value = response
                loadBusiness(context)
            },
            Response.ErrorListener { error ->
                message.value = error.message
            }
        ) {}

        stringRequest.retryPolicy = com.android.volley.DefaultRetryPolicy(
            150000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        rq.add(stringRequest)
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
