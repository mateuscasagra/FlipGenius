package com.example.flipgenius.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config_jogador")
data class ConfigJogador(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nomeUsuario: String,
    val senhaHash: String,
    val senhaSalt: String,
    val temaPreferido: String = "padrao",
    val dataCriacao: Long = System.currentTimeMillis(),
    val dataAtualizacao: Long = System.currentTimeMillis()
)
