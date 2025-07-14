package com.uniandes.ciemi.data.repository

import android.content.Context
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.model.Category
import com.uniandes.ciemi.utils.Constants

object CategoryRepository {

    fun loadCategories(
        context: Context,
        negocioId: Int,
        tipo: String,
        onSuccess: (List<Category>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${Constants.BASE_URL}/Categoria/select-categorias?tipo=$tipo&negocioId=$negocioId"

        val rq = Volley.newRequestQueue(context)

        val js = object : JsonArrayRequest(Method.GET, url, null,
            { response ->
                val categories = mutableListOf<Category>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    categories.add(
                        Category(
                            id = obj.getInt("id"),
                            nombre = obj.getString("nombre"),
                        )
                    )
                }
                onSuccess(categories)
            },
            { error ->
                onError(error.message ?: "Unknown error")
            }
        ) {
        }

        rq.add(js)
    }
}
