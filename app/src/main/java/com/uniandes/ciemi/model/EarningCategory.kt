package com.uniandes.ciemi.model


data class EarningCategory(
    val categoria: String,
    val cantidadVendida: Int,
    val costoTotal: Double,
    val ventas: Double,
    val ganancias: Double
)
