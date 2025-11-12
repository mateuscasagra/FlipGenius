package com.example.flipgenius.data.repository

import com.example.flipgenius.data.local.dao.PartidaDao
import com.example.flipgenius.data.local.entities.Partida

class PartidaRepository(
    private val partidaDao: PartidaDao
) {
    suspend fun salvarPartida(partida: Partida): Long {
        return partidaDao.insert(partida)
    }
    
    suspend fun getPartidasPorUsuario(usuarioId: Long): List<Partida> {
        return partidaDao.getPartidasPorUsuario(usuarioId)
    }
    
    suspend fun getRanking(limit: Int = 10): List<Partida> {
        return partidaDao.getRanking(limit)
    }
}


