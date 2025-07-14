package com.uniandes.ciemi.view

import android.content.Context
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import retrofit2.Response as RetrofitResponse
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.data.repository.CategoryRepository
import com.uniandes.ciemi.data.repository.ParametersRepository
import com.uniandes.ciemi.model.ApiResponse
import com.uniandes.ciemi.model.Category
import com.uniandes.ciemi.model.Parameters
import com.uniandes.ciemi.model.Product
import com.uniandes.ciemi.utils.Constants

class ProductViewModel : ViewModel() {

    val products = mutableStateListOf<Product>()
    var categoriaSeleccionadaId = mutableIntStateOf(0)
    val params = mutableStateListOf<Parameters>()
    val categorias = mutableStateListOf<Category>()
    var paramsSeleccionadoId = mutableStateOf("")
    var productoGuardado = mutableStateOf(false)
    var precioCompra = mutableStateOf("")
    var precioVenta = mutableStateOf("")
    var estado = mutableStateOf("")
    var cantidad = mutableStateOf("")
    var descripcion = mutableStateOf("")
    var nombre = mutableStateOf("")
    var codigo = mutableStateOf("")
    var fechaIngreso = mutableStateOf("")
    var fechaCaducidad = mutableStateOf("")
    var fechaElaboracion = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun loadProducts(
        context: Context,
        negocioId: Int,
        search: String = "",
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/Producto/listarProductos?" +
                "NombreProducto=$search&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

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

    fun loadParams(context: Context) {
        ParametersRepository.loadParameters(
            context = context,
            onSuccess = {
                params.clear()
                params.addAll(it)
            },
            onError = {
                println("Error al cargar Parametros: $it")
            }
        )
    }

    fun loadCategories(context: Context, negocioId: Int) {
        CategoryRepository.loadCategories(
            context = context,
            negocioId = negocioId,
            tipo = "PRODUCTO",
            onSuccess = {
                categorias.clear()
                categorias.addAll(it)
            },
            onError = {
                println("Error al cargar categorías: $it")
            }
        )
    }

    fun guardarProducto(context: Context, negocioId: String) {
        Log.d("Producto", "Intentando guardar producto: nombre=${nombre.value}, codigo=${codigo.value}")
        val api = com.uniandes.ciemi.data.api.ApiClient.getProductoApi(context)
        val part = { value: String -> value.toRequestBody("text/plain".toMediaType()) }
        val iva = try {
            // Truncamos y convertimos a entero
            paramsSeleccionadoId.value.toDouble().toInt().toString()
        } catch (e: Exception) {
            Log.e("Producto", "IVA inválido: ${paramsSeleccionadoId.value}")
            "0"
        }


        val call = api.crearProducto(
            part(codigo.value),
            part(nombre.value),
            part(descripcion.value),
            part(categoriaSeleccionadaId.value.toString()),
            part(negocioId),
            part(estado.value.ifEmpty { "ACTIVO" }),
            part(precioCompra.value),
            part(precioVenta.value),
            part(iva),
                    part(cantidad.value),
            part(fechaIngreso.value.ifEmpty { "2025-07-12" }),
            part(fechaElaboracion.value.ifEmpty { "2025-07-12" }),
            part(fechaCaducidad.value.ifEmpty { "2025-08-12" })
        )

        call.enqueue(object : Callback<ApiResponse<Int>> {
            override fun onResponse(call: Call<ApiResponse<Int>>, response: RetrofitResponse<ApiResponse<Int>>) {
                if (response.isSuccessful && response.body()?.succeeded == true) {
                    productoGuardado.value = true
                    message.value = "Producto guardado exitosamente"
                    loadProducts(context, negocioId.toInt())
                } else {
                    message.value = response.body()?.message ?: response.errorBody()?.string()
                    Log.e("Producto", "Error al guardar producto: ${response.body()?.message ?: response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<Int>>, t: Throwable) {
                Log.e("Producto", "Fallo en la red: ${t.message}")
            }
        })
    }


    fun clearMessage() {
        message.value = null
    }


    fun clearFields() {

    }

}
