package com.example.flipgenius.data.repository

import android.content.Context
import com.example.flipgenius.data.local.AppDatabase
import com.example.flipgenius.data.local.dao.PartidaDao
import com.example.flipgenius.data.local.entities.Partida
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class PartidaRepository private constructor(
    private val firestore: FirebaseFirestore,
    private val partidaDao: PartidaDao
) {
    private val collection = firestore.collection("partidas_classico")

    suspend fun salvarPartida(partida: Partida): Result<Unit> {
        return try {
            val data = partida.toMap()
            collection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private val remoteFlowAll: Flow<List<Partida>> = callbackFlow {
        val reg = collection
            .orderBy("tentativas", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(emptyList()); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.data?.toPartida() } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    fun getAllPartidas(): Flow<List<Partida>> = remoteFlowAll.flatMapLatest { list ->
        if (list.isNotEmpty()) flowOf(list) else partidaDao.getAllPartidas()
    }

    suspend fun limparHistorico(): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val docs = collection.get().await()
            docs.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Partida.toMap(): Map<String, Any> = mapOf(
        "usuarioId" to usuarioId,
        "tema" to tema,
        "tentativas" to tentativas,
        "dataPartida" to dataPartida
    )

    private fun Map<String, Any>.toPartida(): Partida? {
        val usuarioId = (this["usuarioId"] as? Number)?.toLong() ?: 0L
        val tema = this["tema"] as? String ?: return null
        val tentativas = (this["tentativas"] as? Number)?.toInt() ?: return null
        val dataPartida = (this["dataPartida"] as? Number)?.toLong() ?: System.currentTimeMillis()
        return Partida(
            id = 0L,
            usuarioId = usuarioId,
            tema = tema,
            tentativas = tentativas,
            dataPartida = dataPartida
        )
    }

    companion object {
        fun create(context: Context): PartidaRepository {
            val db = AppDatabase.getInstance(context)
            return PartidaRepository(FirebaseFirestore.getInstance(), db.partidaDao())
        }
    }
}
