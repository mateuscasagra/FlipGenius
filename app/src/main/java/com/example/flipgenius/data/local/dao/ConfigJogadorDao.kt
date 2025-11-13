package com.example.flipgenius.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flipgenius.data.local.entities.ConfigJogador

@Dao
interface ConfigJogadorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: ConfigJogador): Long

    @Update
    suspend fun update(config: ConfigJogador)

    @Delete
    suspend fun delete(config: ConfigJogador)

    @Query("SELECT * FROM config_jogador WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ConfigJogador?

    @Query("SELECT * FROM config_jogador WHERE nomeUsuario = :nome LIMIT 1")
    suspend fun getByNomeUsuario(nome: String): ConfigJogador?
}
