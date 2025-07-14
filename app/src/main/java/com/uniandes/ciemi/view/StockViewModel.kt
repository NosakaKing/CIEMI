package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.data.repository.ProductRepository
import com.uniandes.ciemi.model.Product
import com.uniandes.ciemi.model.Stock
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject

class StockViewModel : ViewModel() {

    val stocks = mutableStateListOf<Stock>()
    val products = mutableStateListOf<Product>()
    var productoSeleccionadoId = mutableIntStateOf(0)
    var productoSeleccionado = mutableIntStateOf(0)
    var stockId = mutableIntStateOf(0)
    var stockGuardado = mutableStateOf(false)
    var precioCompra = mutableStateOf("")
    var cantidad = mutableStateOf("")
    var precioVenta = mutableStateOf("")
    var fechaIngreso = mutableStateOf("")
    var fechaCaducidad = mutableStateOf("")
    var fechaElaboracion = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun loadStock(
        context: Context,
        negocioId: Int,
        search: Int? = null,
                pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val searchQuery = search?.toString() ?: ""
        val url = "${Constants.BASE_URL}/Stock/listar?" +
                "ProductoId=$searchQuery&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                if (response.getBoolean("succeeded")) {
                    val array = response.getJSONArray("data")
                    stocks.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val productoJson = obj.getJSONObject("producto")
                        val producto = productoJson.toProduct()
                        stocks.add(
                            Stock(
                                id = obj.getInt("id"),
                                cantidad = obj.getInt("cantidad"),
                                precioCompra = obj.getDouble("precioCompra"),
                                precioVenta = obj.getDouble("precioVenta"),
                                fechaIngreso = obj.getString("fechaIngreso"),
                                fechaCaducidad = obj.getString("fechaCaducidad"),
                                fechaElaboracion = obj.getString("fechaElaboracion"),
                                producto = producto
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

    fun setStockToEdit(stock: Stock) {
        stockId.value = stock.id
        precioCompra.value = stock.precioCompra.toString()
        cantidad.value = stock.cantidad.toString()
        precioVenta.value = stock.precioVenta.toString()
        fechaIngreso.value = stock.fechaIngreso.toString()
        fechaCaducidad.value = stock.fechaCaducidad.toString()
        fechaElaboracion.value = stock.fechaElaboracion.toString()
    }

    fun loadProduct(context: Context, negocioId: Int) {
        ProductRepository.loadProduct(
            context = context,
            negocioId = negocioId,
            onSuccess = {
                products.clear()
                products.addAll(it)
            },
            onError = {
                println("Error al cargar productos: $it")
            }
        )
    }

    fun JSONObject.toProduct(): Product {
        return Product(
            id = getInt("id"),
            categoriaId = getInt("categoriaId"),
            codigo = getString("codigo"),
            nombre = getString("nombre"),
            descripcion = getString("descripcion"),
            estado = getString("estado"),
            iva = getDouble("iva"),
            rutaImagen = getString("rutaImagen")
        )
    }

    fun saveOrUpdateStock(context: Context, esActualizar: Boolean, stockId: Int = 0, negocioId: Int){
        val url = if (esActualizar) {
            "${Constants.BASE_URL}/Stock/actualizar"
        } else {
            "${Constants.BASE_URL}/Stock/crear"
        }

        val datos = JSONObject().apply {
            if (esActualizar) put("id", stockId)
            put("cantidad", cantidad.value)
            put("productoId", productoSeleccionado.value)
            put("precioCompra", precioCompra.value)
            put("precioVenta", precioVenta.value)
            put("negocioId", negocioId)
            put("fechaIngreso", fechaIngreso.value)
            put("fechaCaducidad", fechaCaducidad.value)
            put("fechaElaboracion", fechaElaboracion.value)
        }
        val metodoHttp = if (esActualizar) Request.Method.PUT else Request.Method.POST
        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(
            metodoHttp, url, datos,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        message.value = if (esActualizar) "Stock actualizado exitosamente" else "Stock agregado exitosamente"
                        clearFields()
                        stockGuardado.value = true
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

    fun resetStockGuardado() {
        stockGuardado.value = false
    }

    fun clearFields() {
        productoSeleccionado.value = 0
        precioCompra.value = ""
        cantidad.value = ""
        precioVenta.value = ""
        fechaIngreso.value = ""
        fechaCaducidad.value = ""
        fechaElaboracion.value = ""
        stockId.value = 0

    }

}
