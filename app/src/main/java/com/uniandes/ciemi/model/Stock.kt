package com.uniandes.ciemi.model


data class Stock (
    val id: Int,
    val cantidad: Int,
    val producto: Product,
    val precioCompra: Double,
    val precioVenta: Double,
    val fechaIngreso: String? = null,
    val fechaCaducidad: String? = null,
    val fechaElaboracion: String? = null
)

