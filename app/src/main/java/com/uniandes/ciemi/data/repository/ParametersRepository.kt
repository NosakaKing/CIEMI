package com.uniandes.ciemi.data.repository

import android.content.Context
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.uniandes.ciemi.model.Parameters
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException

object ParametersRepository {

    fun loadParameters(
        context: Context,
        onSuccess: (List<Parameters>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${Constants.BASE_URL}/Parametros"


        val rq = Volley.newRequestQueue(context)
        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        val parametersList = mutableListOf<Parameters>()
                        val dataArray = response.getJSONArray("data")

                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            parametersList.add(
                                Parameters(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre"),
                                    valor = obj.getString("valor")
                                )
                            )
                        }
                        onSuccess(parametersList)
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
