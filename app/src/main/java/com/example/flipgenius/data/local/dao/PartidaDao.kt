package com.example.flipgenius.data.local.dao

import androidx.room.*
import com.example.flipgenius.data.local.entities.Partida

@Dao
interface PartidaDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(partida: Partida): Long
    
    @Query("SELECT * FROM partida WHERE usuarioId = :usuarioId ORDER BY dataJogo DESC")
    suspend fun getPartidasPorUsuario(usuarioId: Long): List<Partida>
    
    @Query("SELECT * FROM partida ORDER BY pontuacao DESC, tentativas ASC LIMIT :limit")
    suspend fun getRanking(limit: Int = 10): List<Partida>
    
    @Query("SELECT * FROM partida WHERE id = :id")
    suspend fun getPartidaPorId(id: Long): Partida?
}


