package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONException
import com.uniandes.ciemi.model.Category
import com.uniandes.ciemi.utils.Constants

class CategoryViewModel : ViewModel() {

    val categories = mutableStateListOf<Category>()
    var categoriaId = mutableStateOf(0)
    var nombre = mutableStateOf("")
    var descripcion = mutableStateOf("")
    var tipo = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    private fun getToken(context: Context): String {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userDataString = sharedPref.getString("user_data", null) ?: throw Exception("Token no encontrado")
        val userData = JSONObject(userDataString)
        return userData.getString("jwToken")
    }

    private fun getAuthHeaders(token: String): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $token"
        headers["Content-Type"] = "application/json"
        return headers
    }

    fun loadCategories(context: Context, type: String = "PROOUCTO", pageNumber: Int = 1, pageSize: Int = 10) {
        val token = getToken(context)
        val url = "${Constants.BASE_URL}/Categoria?Tipo=${type}&NegocioId=3&pageNumber=$pageNumber&pageSize=$pageSize"

        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                if (response.getBoolean("succeeded")) {
                    val array = response.getJSONArray("data")
                    categories.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        categories.add(
                            Category(
                                id = obj.getInt("id"),
                                nombre = obj.getString("nombre"),
                                descripcion = obj.getString("descripcion"),
                                tipo = obj.getString("tipo"),
                                negocioId = obj.getLong("negocioId")
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
                return getAuthHeaders(token)
            }
        }

        rq.add(js)
    }

    fun saveCategory(context: Context) {
        val token = getToken(context)
        val url = "${Constants.BASE_URL}/Categoria/crear"

        val datos = JSONObject().apply {
            put("nombre", nombre.value)
            put("descripcion", descripcion.value)
            put("tipo", tipo.value)
            put("negocioId", 3)
        }

        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(
            Request.Method.POST, url, datos,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        message.value = "Categoría agregada exitosamente"
                        loadCategories(context)
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
                return getAuthHeaders(token)
            }
        }

        rq.add(js)
    }


    fun clearMessage() {
        message.value = null
    }

    fun clearFields() {
        nombre.value = ""
        descripcion.value = ""
        tipo.value = ""
        // negocioId no se limpia porque puede mantenerse constante (por sesión)
    }
}
