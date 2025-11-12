package com.example.flipgenius.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "partidas_time_attack")

data class PartidaTimeAttack(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nome_jogador")
    val nomeJogador: String,

    @ColumnInfo(name = "pontuacao")
    val pontuacao: Int,

    @ColumnInfo(name = "tema_nome")
    val temaNome: String,

    @ColumnInfo(name = "data_partida")
    val dataPartida: Long

)