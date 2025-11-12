package com.example.flipgenius.data.repository

import com.example.flipgenius.data.local.dao.PartidaDao
import com.example.flipgenius.data.local.entities.Partida
import kotlinx.coroutines.flow.Flow

class PartidaRepository(private val partidaDao: PartidaDao) {

    suspend fun salvarPartida(partida: Partida): Result<Unit> {
        return try {
            partidaDao.insert(partida)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllPartidas(): Flow<List<Partida>> {
        return partidaDao.getAllPartidas()
    }

    fun getPartidasByUsuario(usuarioId: Long): Flow<List<Partida>> {
        return partidaDao.getPartidasByUsuario(usuarioId)
    }

    fun getPartidasByTema(tema: String): Flow<List<Partida>> {
        return partidaDao.getPartidasByTema(tema)
    }

    suspend fun limparHistorico(): Result<Unit> {
        return try {
            partidaDao.deleteAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarPartida(id: Long): Result<Unit> {
        return try {
            partidaDao.delete(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
