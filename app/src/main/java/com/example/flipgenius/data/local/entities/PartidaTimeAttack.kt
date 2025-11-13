package com.example.flipgenius.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.flipgenius.data.local.entities.Usuario

@Entity(
    tableName = "partidas_time_attack",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PartidaTimeAttack(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val usuarioId: Long,

    @ColumnInfo(name = "nome_jogador")
    val nomeJogador: String,

    @ColumnInfo(name = "pontuacao")
    val pontuacao: Int,

    @ColumnInfo(name = "tema_nome")
    val temaNome: String,

    @ColumnInfo(name = "data_partida")
    val dataPartida: Long
)