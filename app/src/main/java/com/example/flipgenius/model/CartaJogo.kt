package com.example.flipgenius.model

data class CartaJogo(
    val id: Int,
    val emoji: String, // Emoji da carta
    val virada: Boolean = false,
    val encontrada: Boolean = false
)

