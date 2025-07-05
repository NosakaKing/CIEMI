package com.uniandes.ciemi.model


data class Product(
    val id: Int,
    val categoriaId: Int,
    val nombreCategoria: String,
    val precioVenta: Double,
    val negocioId: Int,
    val nombreNegocio: String,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val estado: String,
    val iva: Double,
    val rutaImagen: String
)
