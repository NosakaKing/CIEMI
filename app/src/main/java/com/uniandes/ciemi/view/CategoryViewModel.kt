package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
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
    var nombre = mutableStateOf("")
    var descripcion = mutableStateOf("")
    var tipo = mutableStateOf("")
    var message = mutableStateOf<String?>(null)


    fun loadCategories(context: Context, pageNumber: Int = 1, pageSize: Int = 10) {
        val url = "${Constants.BASE_URL}/Categoria?Tipo=PRODUCTO&NegocioId=67&pageNumber=$pageNumber&pageSize=$pageSize"

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
                return Constants.getAuthHeaders(context)
            }
        }

        rq.add(js)
    }

    fun saveCategory(context: Context) {
        val url = "${Constants.BASE_URL}/Categoria"

        val datos = JSONObject().apply {
            put("nombre", nombre.value)
            put("descripcion", descripcion.value)
            put("tipo", tipo.value)
            put("negocioId", 67)
        }

        val metodoHttp = Request.Method.POST
        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(
            metodoHttp, url, datos,
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
                return Constants.getAuthHeaders(context)

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
    }
}