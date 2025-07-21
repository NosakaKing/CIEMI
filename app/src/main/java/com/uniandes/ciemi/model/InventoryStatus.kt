package com.uniandes.ciemi.model

data class InventoryStatus(
    val productoNombre: String,
    val existencias: Double,
    val venta: Double,
    val mesesInvt: Double,
    val promedio12: Double,
    val stockMin: Double,
    val stockMax: Double,
    val estatusInvt: String
)
