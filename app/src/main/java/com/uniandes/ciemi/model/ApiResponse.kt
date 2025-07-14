package com.uniandes.ciemi.model

data class ApiResponse<T>(
    val succeeded: Boolean,
    val message: String?,
    val errors: List<String>?,
    val data: T
)
