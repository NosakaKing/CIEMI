package com.uniandes.ciemi.model

data class Product(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val estado: String,
    val iva: Double,
    val categoriaId: Int,
    val nombreCategoria : String,
    val nombreNegocio : String,
    val negocioId: Long,
    val imagen: String? = null

)
