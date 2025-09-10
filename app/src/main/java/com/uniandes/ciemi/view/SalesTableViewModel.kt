package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.model.Sales
import com.uniandes.ciemi.utils.Constants
import com.uniandes.ciemi.utils.PdfUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalesTableViewModel  : ViewModel() {
    var message = mutableStateOf<String?>(null)
    val sales = mutableStateListOf<Sales>()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val fechaHoy: String = sdf.format(Date())
    val fechaInicio = mutableStateOf("2025-01-01")
    val fechaFin = mutableStateOf(fechaHoy)
    fun loadSales(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = fechaHoy,
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/Venta/listar?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->

                if (response.getBoolean("succeeded")) {
                    val array = response.getJSONArray("data")
                    sales.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        sales.add(
                            Sales(
                                id = obj.getInt("id"),
                                fecha = obj.getString("fecha"),
                                subtotal = obj.getString("subtotal"),
                                total = obj.getString("total"),
                                clienteNombres = obj.getString("clienteNombres"),
                                clientePrimerApellido = obj.getString("clientePrimerApellido"),
                                clienteIdentificacion = obj.getString("clienteIdentificacion"),
                                negocioNombre = obj.getString("negocioNombre")
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

    fun downloadSalePDF(context: Context, idSales: Int?) {
        if (idSales == null) return

        val url = "${Constants.BASE_URL}/Venta/GenerarNotaPDF?Ventaid=$idSales"
        val fileName = "venta_${idSales}.pdf"
        val headers = Constants.getAuthHeaders(context)

        PdfUtils.downloadAndOpenPDF(context, url, fileName, headers)
    }



    fun clearMessage() {
        message.value = null
    }
}