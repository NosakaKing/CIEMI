package com.uniandes.ciemi.model


data class InventoryClasification(
    val productoNombre: String,
    val totalVendido: Double,
    val acumulado: Double,
    val porcentajeAcumulado: Double,
    val clasificacionABC: String,
)
