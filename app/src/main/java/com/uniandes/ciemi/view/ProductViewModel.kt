package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.model.Client
import com.uniandes.ciemi.model.Product
import com.uniandes.ciemi.utils.Constants

class ProductViewModel : ViewModel() {

    val products = mutableStateListOf<Product>()
    var productId = mutableIntStateOf(0)
    var categoriaId = mutableStateOf("")
    var estado = mutableStateOf("")
    var precioCompra = mutableDoubleStateOf(0.0)
    var precioVenta = mutableDoubleStateOf(0.0)
    var cantidad = mutableIntStateOf(0)
    var descripcion = mutableStateOf("")
    var nombre = mutableStateOf("")
    var codigo = mutableStateOf("")
    var negocioId = mutableIntStateOf(0)
    var fechaIngreso = mutableStateOf("")
    var fechaCaducidad = mutableStateOf("")
    var fechaElaboracion = mutableStateOf("")
    var imagen = mutableStateOf("")
    var message = mutableStateOf<String?>(null)


    fun setClientToEdit(client: Client) {
        productId.intValue = client.id

    }


    fun loadProducts(
        context: Context,
        search: String = "",
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/Producto/listarProductos?" +
                "NombreProducto=$search&" +
                "NegocioId=67&pageNumber=${pageNumber}&pageSize=${pageSize}"

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
                                id = obj.getInt("id"),
                                categoriaId = obj.getInt("categoriaId"),
                                nombreCategoria = obj.getString("nombreCategoria"),
                                precioVenta = obj.getDouble("precioVenta"),
                                negocioId = obj.getInt("negocioId"),
                                nombreNegocio = obj.getString("nombreNegocio"),
                                codigo = obj.getString("codigo"),
                                nombre = obj.getString("nombre"),
                                descripcion = obj.getString("descripcion"),
                                estado = obj.getString("estado"),
                                iva = obj.getDouble("iva"),
                                rutaImagen = obj.getString("rutaImagen")
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

    fun clearMessage() {
        message.value = null
    }

    fun clearFields() {

    }

}
