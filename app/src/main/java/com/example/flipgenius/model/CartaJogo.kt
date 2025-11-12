package com.example.flipgenius.model

data class CartaJogo(
    val id: Int,
    val conteudo: String,
    val virada: Boolean = false,
    val encontrada: Boolean = false
)

