package com.uniandes.ciemi.view

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.model.Category
import com.uniandes.ciemi.model.Product
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream

class ProductViewModel : ViewModel() {

    val products = mutableStateListOf<Product>()
    val categorias = mutableStateListOf<Category>()

    var productoId = mutableStateOf(0)
    var codigo = mutableStateOf("")
    var nombre = mutableStateOf("")
    var descripcion = mutableStateOf("")
    var estado = mutableStateOf("Activo")
    var iva = mutableStateOf(0.0)
    var categoriaId = mutableStateOf(2)
    var imagenUri = mutableStateOf<Uri?>(null)

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

    private fun encodeImageToBase64(context: Context, uri: Uri?): String {
        if (uri == null) return ""
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun loadCategorias(context: Context) {
        val token = getToken(context)
        val url = "${Constants.BASE_URL}/Categoria/select-categorias?tipo=PRODUCTO&NegocioId=3"

        val rq = Volley.newRequestQueue(context)
        val js = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                categorias.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    categorias.add(
                        Category(
                            id = obj.optInt("id"),
                            nombre = obj.optString("nombre")
                        )
                    )
                }
            },
            { error ->
                message.value = error.message
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = getAuthHeaders(token)
        }
        rq.add(js)

    }

    fun loadProducts(context: Context, nombreBusqueda: String = "", pageNumber: Int = 1, pageSize: Int = 10) {
        val token = getToken(context)
        val url = "${Constants.BASE_URL}/Producto/listarProductos?NegocioId=3&pageNumber=$pageNumber&pageSize=$pageSize"

        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                if (response.getBoolean("succeeded")) {
                    val array = response.getJSONArray("data")
                    products.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        products.add(
                            Product(
                                codigo = obj.optString("codigo"),
                                nombre = obj.optString("nombre"),
                                descripcion = obj.optString("descripcion"),
                                estado = obj.optString("estado"),
                                iva = obj.optDouble("iva"),
                                categoriaId = obj.optInt("categoriaId"),
                                nombreCategoria = obj.optString("nombreCategoria"),
                                negocioId = obj.optLong("negocioId"),
                                nombreNegocio = obj.optString("nombreNegocio"),
                                imagen = obj.optString("rutaImagen")
                            )
                        )
                    }
                } else {
                    message.value = response.optString("message")
                }
            },
            { error ->
                message.value = error.message
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = getAuthHeaders(token)
        }
        rq.add(js)
    }

    fun saveProduct(context: Context) {
        val token = getToken(context)
        val url = "${Constants.BASE_URL}/Producto/crear"

        val base64Image = encodeImageToBase64(context, imagenUri.value)

        val datos = JSONObject().apply {
            put("codigo", codigo.value)
            put("nombre", nombre.value)
            put("descripcion", descripcion.value)
            put("estado", estado.value)
            put("iva", iva.value)
            put("categoriaId", categoriaId.value)
            put("negocioId", 3)
            put("imagen", base64Image)
        }

        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(Method.POST, url, datos,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        message.value = "Producto agregado exitosamente"
                        clearFields()
                        loadProducts(context)
                    } else {
                        message.value = response.getString("message")
                    }
                } catch (e: JSONException) {
                    message.value = e.message
                }
            },
            { error ->
                message.value = error.message
            }) {
            override fun getHeaders(): MutableMap<String, String> = getAuthHeaders(token)
        }
        rq.add(js)
    }

    fun updateProduct(context: Context) {
        val token = getToken(context)
        val url = "${Constants.BASE_URL}/Producto/actualizar"

        val base64Image = encodeImageToBase64(context, imagenUri.value)

        val datos = JSONObject().apply {
            put("codigo", codigo.value)
            put("nombre", nombre.value)
            put("descripcion", descripcion.value)
            put("estado", estado.value)
            put("iva", iva.value)
            put("categoriaId", categoriaId.value)
            put("negocioId", 3)
            put("imagen", base64Image)
        }

        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(Method.PUT, url, datos,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        message.value = "Producto actualizado exitosamente"
                        clearFields()
                        loadProducts(context)
                    } else {
                        message.value = response.getString("message")
                    }
                } catch (e: JSONException) {
                    message.value = e.message
                }
            },
            { error ->
                message.value = error.message
            }) {
            override fun getHeaders(): MutableMap<String, String> = getAuthHeaders(token)
        }
        rq.add(js)
    }

    fun setProductToEdit(product: Product) {
        codigo.value = product.codigo
        nombre.value = product.nombre
        descripcion.value = product.descripcion
        estado.value = product.estado
        iva.value = product.iva
        categoriaId.value = product.categoriaId
        imagenUri.value = null
    }

    fun clearMessage() {
        message.value = null
    }

    fun clearFields() {
        productoId.value = 0
        codigo.value = ""
        nombre.value = ""
        descripcion.value = ""
        estado.value = "Activo"
        iva.value = 0.0
        categoriaId.value = 0
        imagenUri.value = null
    }
}
