package com.example.flipgenius.model

data class CartaJogo(
    val id: Int,
    val numero: Int,
    val virada: Boolean = false,
    val encontrada: Boolean = false
)

