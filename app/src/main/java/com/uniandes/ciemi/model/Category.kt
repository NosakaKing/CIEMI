package com.uniandes.ciemi.model

data class Category(
    val id: Int,
    val nombre: String,
    var descripcion: String? = null,
    val tipo: String? = null,
    val negocioId: Long? = null,
)


