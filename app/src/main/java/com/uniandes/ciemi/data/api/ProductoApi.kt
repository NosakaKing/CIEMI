// data/api/ProductoApi.kt
package com.uniandes.ciemi.data.api

import com.uniandes.ciemi.model.ApiResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProductoApi {
    @Multipart
    @POST("Producto/crear")
    fun crearProducto(
        @Part("codigo") codigo: RequestBody,
        @Part("nombre") nombre: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("categoriaId") categoriaId: RequestBody,
        @Part("negocioId") negocioId: RequestBody,
        @Part("estado") estado: RequestBody,
        @Part("precioCompra") precioCompra: RequestBody,
        @Part("precioVenta") precioVenta: RequestBody,
        @Part("iva") iva: RequestBody,
        @Part("cantidad") cantidad: RequestBody,
        @Part("fechaIngreso") fechaIngreso: RequestBody,
        @Part("fechaElaboracion") fechaElaboracion: RequestBody,
        @Part("fechaCaducidad") fechaCaducidad: RequestBody
    ): Call<ApiResponse<Int>>
}
