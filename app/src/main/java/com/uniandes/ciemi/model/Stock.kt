package com.uniandes.ciemi.model


data class Stock (
    val id: Int,
    var cantidad: Int,
    var cantidadElegida: String,
    val producto: Product,
    val precioCompra: Double,
    val precioVenta: Double,
    val fechaIngreso: String? = null,
    val fechaCaducidad: String? = null,
    val fechaElaboracion: String? = null
)

