package com.uniandes.ciemi.model


data class Sales(
    val fecha: String,
    val subtotal: String,
    val total: String,
    val clientePrimerApellido: String,
    val clienteNombres: String,
    val clienteIdentificacion: String,
    val negocioNombre: String,
)
