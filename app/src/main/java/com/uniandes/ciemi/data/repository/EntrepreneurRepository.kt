package com.uniandes.ciemi.data.repository

import android.content.Context
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.uniandes.ciemi.model.Entrepreneur
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException

object EntrepreneurRepository {

    fun loadEntrepreneur(
        context: Context,
        onSuccess: (List<Entrepreneur>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${Constants.BASE_URL}/Account/AllEmprendedores"

        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                try {
                    if (response.getBoolean("succeeded")) {
                        val EntrepreneurList = mutableListOf<Entrepreneur>()
                        val dataArray = response.getJSONArray("data")

                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            EntrepreneurList.add(
                                Entrepreneur(
                                    id = obj.getString("id"),
                                    userName = obj.getString("userName"),
                                )
                            )
                        }
                        onSuccess(EntrepreneurList)
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
