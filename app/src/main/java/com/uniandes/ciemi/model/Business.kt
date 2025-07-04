package com.uniandes.ciemi.model


data class Business(

    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val estado: String,
    val descripcion: String,
    val categoriaId: Int
)
