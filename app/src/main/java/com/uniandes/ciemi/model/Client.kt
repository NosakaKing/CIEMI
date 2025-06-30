package com.uniandes.ciemi.model


data class Client(
    val id: Int,
    val identificacion: String,
    val nombres: String,
    val primerApellido: String,
    val segundoApellido: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val direccion: String
)
