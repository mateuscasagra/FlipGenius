package com.example.flipgenius.data.repository

import android.util.Log
import com.example.flipgenius.data.local.dao.TimeAttackDao
import com.example.flipgenius.data.local.entities.PartidaTimeAttack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TimeAttackRepository(private val timeAttackDao: TimeAttackDao) {

    val allPartidasFlow: Flow<List<PartidaTimeAttack>> = timeAttackDao.getAllFlow()

    suspend fun insertPartida(partida: PartidaTimeAttack) {
        withContext(Dispatchers.IO) {
            try {
                timeAttackDao.inserir(partida)
            } catch (e: Exception) {
                Log.e("TimeAttackRepo", "Erro ao inserir partida", e)
            }
        }
    }

    suspend fun deleteAllPartidas(){
        withContext(Dispatchers.IO) {
            try {
                timeAttackDao.deleteAll()
            } catch (e: Exception) {
                Log.e("TimeAttackRepo", "Erro ao deletar histórico", e)
            }
        }
    }
}