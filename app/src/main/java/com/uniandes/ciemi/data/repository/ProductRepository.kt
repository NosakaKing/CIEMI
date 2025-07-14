package com.uniandes.ciemi.data.repository

import android.content.Context
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.uniandes.ciemi.model.Parameters
import com.uniandes.ciemi.model.Product
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException

object ProductRepository {

    fun loadProduct(
        context: Context,
        negocioId: Int,
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${Constants.BASE_URL}/Producto/selectProductos?NegocioId=$negocioId"


        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        val productList = mutableListOf<Product>()
                        val dataArray = response.getJSONArray("data")

                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            productList.add(
                                Product(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre"),
                                )
                            )
                        }
                        onSuccess(productList)
                    } else {
                        onError(response.optString("message", "Error desconocido"))
                    }
                } catch (e: JSONException) {
                    onError(e.message ?: "Error al procesar la respuesta")
                }
            },
            { error ->
                onError(error.message ?: "Error de red")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return Constants.getAuthHeaders(context)
            }
        }

        rq.add(js)
    }
}
