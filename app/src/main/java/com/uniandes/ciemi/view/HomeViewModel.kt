package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.model.EarningCategory
import com.uniandes.ciemi.model.EarningClient
import com.uniandes.ciemi.model.EarningProduct
import com.uniandes.ciemi.model.InventoryClasification
import com.uniandes.ciemi.model.InventoryMes
import com.uniandes.ciemi.model.InventoryStatus
import com.uniandes.ciemi.model.SalesMes
import com.uniandes.ciemi.model.SellerMes
import com.uniandes.ciemi.utils.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel: ViewModel() {
    var message = mutableStateOf<String?>(null)
    val earningProducts = mutableStateListOf<EarningProduct>()
    val earningClients = mutableStateListOf<EarningClient>()
    val earningCategories = mutableStateListOf<EarningCategory>()
    val inventoryStatus = mutableStateListOf<InventoryStatus>()
    val inventoryClasification = mutableStateListOf<InventoryClasification>()
    val inventoryMes = mutableStateListOf<InventoryMes>()
    val salesyMes = mutableStateListOf<SalesMes>()
    val sellerMes = mutableStateListOf<SellerMes>()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val fechaHoy: String = sdf.format(Date())

    private inline fun <reified T> fetchData(
        context: Context,
        url: String,
        crossinline parseItem: (item: org.json.JSONObject) -> T,
        targetList: MutableList<T>
    ) {
        val rq = Volley.newRequestQueue(context)

        val js = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                if (response.getBoolean("succeeded")) {
                    val array = response.getJSONArray("data")
                    targetList.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        targetList.add(parseItem(obj))
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


    private inline fun <reified T> fetchDataArray(
        context: Context,
        url: String,
        crossinline parseItem: (item: org.json.JSONObject) -> T,
        targetList: MutableList<T>
    ) {
        val rq = Volley.newRequestQueue(context)

        val js = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                targetList.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    targetList.add(parseItem(obj))
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





    fun loadEarningProducts(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = "2025-01-01",
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/GananciasStats/gananciasPorProductos?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        fetchData(
            context = context,
            url = url,
            targetList = earningProducts,
            parseItem = { obj ->
                EarningProduct(
                    nombre = obj.getString("nombre"),
                    cantidadVendida = obj.getInt("cantidadVendida"),
                    costoTotal = obj.getDouble("costoTotal"),
                    gananciaTotal = obj.getDouble("gananciaTotal")
                )
            }
        )
    }

    fun loadEarningClients(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = "2025-01-01",
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/GananciasStats/MejoresClientes?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        fetchData(
            context = context,
            url = url,
            targetList = earningClients,
            parseItem = { obj ->
                EarningClient(
                    identificacion = obj.getString("identificacion"),
                    nombreCliente = obj.getString("nombreCliente"),
                    apellidoCliente = obj.getString("apellidoCliente"),
                    totalCompras = obj.getDouble("totalCompras"),
                    cantidadCompras = obj.getInt("cantidadCompras"),
                    promedioCompraMensual = obj.getDouble("promedioCompraMensual"),
                    totalGanancias = obj.getDouble("totalGanancias")
                )
            }
        )
    }


    fun loadCategory(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = "2025-01-01",
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/GananciasStats/gananciasPorCategoria?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        fetchData(
            context = context,
            url = url,
            targetList = earningCategories,
            parseItem = { obj ->
                EarningCategory(
                    categoria = obj.getString("categoria"),
                    cantidadVendida = obj.getInt("cantidadVendida"),
                    costoTotal = obj.getDouble("costoTotal"),
                    ventas = obj.getDouble("ventas"),
                    ganancias = obj.getDouble("ganancias")
                )
            }
        )
    }



    fun loadStatus(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = "2025-01-01",
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/InventarioStats/EstadoInventario?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        fetchData(
            context = context,
            url = url,
            targetList = inventoryStatus,
            parseItem = { obj ->
                InventoryStatus(
                    productoNombre = obj.getString("productoNombre"),
                    existencias = obj.getDouble("existencias"),
                    venta = obj.getDouble("venta"),
                    mesesInvt = obj.getDouble("mesesInvt"),
                    promedio12 = obj.getDouble("promedio12"),
                    stockMin = obj.getDouble("stockMin"),
                    stockMax = obj.getDouble("stockMax"),
                    estatusInvt = obj.getString("estatusInvt")
                )
            }
        )
    }


    fun loadClasification(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = "2025-01-01",
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/InventarioStats/Clasificacion?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        fetchData(
            context = context,
            url = url,
            targetList = inventoryClasification,
            parseItem = { obj ->
                InventoryClasification(
                    productoNombre = obj.getString("productoNombre"),
                    acumulado = obj.getDouble("acumulado"),
                    porcentajeAcumulado = obj.getDouble("porcentajeAcumulado"),
                    totalVendido = obj.getDouble("totalVendido"),
                    clasificacionABC = obj.getString("clasificacionABC"),
                )
            }
        )
    }


    fun loadMes(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = "2025-01-01",
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/InventarioStats/AnalisisPorMes?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        fetchData(
            context = context,
            url = url,
            targetList = inventoryMes,
            parseItem = { obj ->
                InventoryMes(
                    mes = obj.getInt("mes"),
                    costoInvInicial = obj.getDouble("costoInvInicial"),
                    costoInvFinal = obj.getDouble("costoInvFinal"),
                    totalVentas = obj.getDouble("totalVentas")
                )
            }
        )
    }


    fun loadSalesMes(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = "2025-01-01",
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/VentasStats/VentasMensuales?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        fetchData(
            context = context,
            url = url,
            targetList = salesyMes,
            parseItem = { obj ->
                SalesMes(
                    mes = obj.getInt("mes"),
                    anio = obj.getInt("anio"),
                    totalVentas = obj.getDouble("totalVentas")
                )
            }
        )
    }


    fun loadSellerMes(
        context: Context,
        negocioId: Int,
        fechaInicio: String? = "2025-01-01",
        fechaFin: String? = fechaHoy,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ) {
        val url = "${Constants.BASE_URL}/VentasStats/VentasPorVendedor?" +
                "FechaInicio=$fechaInicio&" +
                "FechaFin=$fechaFin&" +
                "NegocioId=${negocioId}&pageNumber=${pageNumber}&pageSize=${pageSize}"

        fetchDataArray(
            context = context,
            url = url,
            targetList = sellerMes,
            parseItem = { obj ->
                SellerMes(
                    nombreVendedor = obj.getString("nombreVendedor"),
                    apellidoVendedor = obj.getString("apellidoVendedor"),
                    userName = obj.getString("userName"),
                    totalVentas = obj.getDouble("totalVentas")
                )
            }
        )
    }




    fun clearMessage() {
        message.value = null
    }

}