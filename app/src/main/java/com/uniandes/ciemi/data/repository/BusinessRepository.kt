package com.uniandes.ciemi.data.repository

import android.content.Context
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.uniandes.ciemi.model.SelectBusiness
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException

object BusinessRepository {

    fun loadBusiness(
        context: Context,
        onSuccess: (List<SelectBusiness>) -> Unit,
        onError: (String) -> Unit
    ) {
        val role = Constants.getUserRole(context)
        val userId = Constants.getUserId(context)
        val url = "${Constants.BASE_URL}/Negocio/${if (role == "Admin") "selectNegociosAdmin" else "selectNegocios?EmprendedorId=$userId"}"

        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        val businessList = mutableListOf<SelectBusiness>()
                        val dataArray = response.getJSONArray("data")

                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            businessList.add(
                                SelectBusiness(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre"),
                                    estado = obj.getString("estado")
                                )
                            )
                        }
                        onSuccess(businessList)
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
