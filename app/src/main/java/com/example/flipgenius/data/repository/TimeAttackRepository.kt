package com.example.flipgenius.data.repository

import com.example.flipgenius.data.local.entities.PartidaTimeAttack
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TimeAttackRepository private constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("partidasTimeAttack")

    val allPartidasFlow: Flow<List<PartidaTimeAttack>> = callbackFlow {
        val reg = collection
            .orderBy("pontuacao", Query.Direction.DESCENDING)
            .orderBy("dataPartida", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { it.data?.toTimeAttack() } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun insertPartida(partida: PartidaTimeAttack) {
        collection.add(partida.toMap()).await()
    }

    suspend fun deleteAllPartidas(){
        val batch = firestore.batch()
        val docs = collection.get().await()
        docs.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    private fun PartidaTimeAttack.toMap(): Map<String, Any> = mapOf(
        "nomeJogador" to nomeJogador,
        "pontuacao" to pontuacao,
        "temaNome" to temaNome,
        "dataPartida" to dataPartida
    )

    private fun Map<String, Any>.toTimeAttack(): PartidaTimeAttack? {
        val nomeJogador = this["nomeJogador"] as? String ?: return null
        val pontuacao = (this["pontuacao"] as? Number)?.toInt() ?: return null
        val temaNome = this["temaNome"] as? String ?: "padrao"
        val dataPartida = (this["dataPartida"] as? Number)?.toLong() ?: System.currentTimeMillis()
        return PartidaTimeAttack(
            id = 0,
            nomeJogador = nomeJogador,
            pontuacao = pontuacao,
            temaNome = temaNome,
            dataPartida = dataPartida
        )
    }

    companion object {
        fun create(): TimeAttackRepository = TimeAttackRepository(FirebaseFirestore.getInstance())
    }
}
