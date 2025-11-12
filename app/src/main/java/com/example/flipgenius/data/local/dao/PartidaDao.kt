package com.example.flipgenius.data.local.dao

import androidx.room.*
import com.example.flipgenius.data.local.entities.Partida
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(partida: Partida)

    @Query("SELECT * FROM partida ORDER BY tentativas ASC, dataPartida DESC")
    fun getAllPartidas(): Flow<List<Partida>>

    @Query("SELECT * FROM partida WHERE usuarioId = :usuarioId ORDER BY tentativas ASC")
    fun getPartidasByUsuario(usuarioId: Long): Flow<List<Partida>>

    @Query("SELECT * FROM partida WHERE tema = :tema ORDER BY tentativas ASC")
    fun getPartidasByTema(tema: String): Flow<List<Partida>>

    @Query("DELETE FROM partida")
    suspend fun deleteAll()

    @Query("DELETE FROM partida WHERE id = :id")
    suspend fun delete(id: Long)
}
