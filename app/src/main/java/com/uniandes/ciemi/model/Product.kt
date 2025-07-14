package com.uniandes.ciemi.model


data class Product(
    val id: Int,
    val categoriaId: Int? = null,
    val nombreCategoria: String? = null,
    val nombreNegocio: String ? = null,
    val codigo: String? = null,
    val nombre: String,
    val descripcion: String? = null,
    val estado: String? = null,
    val iva: Double? = null,
    val rutaImagen: String? = null
)
