package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.data.repository.ProductRepository
import com.uniandes.ciemi.model.Product
import com.uniandes.ciemi.model.Stock
import com.uniandes.ciemi.utils.Constants
import org.json.JSONArray
import org.json.JSONObject

class SalesViewModel: ViewModel() {
    val idCliente = mutableIntStateOf(0)
    val identificacion = mutableStateOf("")
    var productoSeleccionado = mutableIntStateOf(0)
    val productosSeleccionados = mutableStateListOf<Stock>()
    val nombres = mutableStateOf("")
    val products = mutableStateListOf<Product>()
    val primerApellido = mutableStateOf("")
    val segundoApellido = mutableStateOf("")
    val correo = mutableStateOf("")
    val telefono = mutableStateOf("")
    val direccion = mutableStateOf("")
    var message = mutableStateOf<String?>(null)
    val stocks = mutableStateListOf<Stock>()



    fun loadClienteById(
        context: Context,
        clienteId: String,
    ) {
        val url = "${Constants.BASE_URL}/Cliente/clienteByIdentificacion?identificacion=${clienteId}"
        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                if (response.getBoolean("succeeded")) {
                    message.value = "Cliente Encontrado"
                    val data = response.getJSONObject("data")
                    idCliente.intValue = data.getInt("id")
                    identificacion.value = data.getString("identificacion")
                    nombres.value = data.getString("nombres")
                    primerApellido.value = data.getString("primerApellido")
                    segundoApellido.value = data.getString("segundoApellido")
                    correo.value = data.getString("email")
                    telefono.value = data.getString("telefono")
                    direccion.value = data.getString("direccion")
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

    fun loadStock(
        context: Context,
        negocioId: Int,
        search: Int? = null,
        pageNumber: Int = 1,
        pageSize: Int = 5
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
                                cantidadElegida = obj.getString("cantidad"),
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


    fun createSale(context: Context, negocioId: Int) {
        if (idCliente.intValue == 0 || productosSeleccionados.isEmpty()) {
            message.value = "Debe seleccionar un cliente y al menos un producto"
            return
        }

        val url = "${Constants.BASE_URL}/Venta/crear"

        val detallesArray = JSONArray()
        productosSeleccionados.forEach { stock ->
            val detalle = JSONObject().apply {
                put("productoId", stock.producto.id)
                put("cantidad", stock.cantidadElegida.toIntOrNull() ?: 0)
                put("precio", stock.precioVenta)
                put("total", stock.cantidadElegida.toInt() * stock.precioVenta)
                put("stockId", stock.id)
            }
            detallesArray.put(detalle)
        }

        val datos = JSONObject().apply {
            put("negocioId", negocioId)
            put("clienteId", idCliente.intValue)
            put("fecha", java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()))
            put("detalles", detallesArray)
        }

        println(datos.toString(4))

        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(Method.POST, url, datos,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        message.value = "Venta registrada correctamente"
                        loadStock(context, negocioId)
                        cleanFields()
                        productosSeleccionados.clear()
                    } else {
                        message.value = response.getString("message")
                    }
                } catch (e: Exception) {
                    message.value = e.message ?: "Error inesperado"
                }
            },
            { error ->
                message.value = error.message ?: "Error en la red"
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

    fun cleanFields() {
        idCliente.intValue = 0
        identificacion.value = ""
        productosSeleccionados.clear()
        nombres.value = ""
        primerApellido.value = ""
    }


}