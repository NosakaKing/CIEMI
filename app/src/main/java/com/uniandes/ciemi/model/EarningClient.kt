package com.uniandes.ciemi.model


data class EarningClient(
    val identificacion: String,
    val nombreCliente: String,
    val apellidoCliente: String,
    val totalCompras: Double,
    val cantidadCompras: Int,
    val promedioCompraMensual: Double,
    val totalGanancias: Double
)
