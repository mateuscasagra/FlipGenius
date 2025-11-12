package com.example.flipgenius.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partida")
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long,
    val temaId: String = "",
    val temaNome: String = "",
    val pontuacao: Int = 0,
    val tentativas: Int = 0,
    val dataJogo: Long = System.currentTimeMillis()
)


