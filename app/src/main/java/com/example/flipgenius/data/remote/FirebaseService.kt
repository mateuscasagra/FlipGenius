package com.example.flipgenius.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.flipgenius.data.local.entities.Partida
import com.example.flipgenius.data.local.entities.PartidaTimeAttack

class FirebaseService(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val temasRef = firestore.collection("temas")
    private val partidasClassicoRef = firestore.collection("partidas_classico")
    private val partidasTimeAttackRef = firestore.collection("partidas_time_attack")
    private val contasRef = firestore.collection("configJogador")

    suspend fun listarTemas(): List<Tema> {
        val snap = temasRef.get().await()
        return snap.documents.map { d ->
            val id = d.id
            val nome = d.getString("nome") ?: id
            val emojis = (d.get("emojis") as? List<*>)?.map { it.toString() } ?: emptyList()
            Tema(id = id, nome = nome, emojis = emojis)
        }
    }

    suspend fun adicionarTema(nome: String, emojis: List<String>) {
        val key = nome.lowercase()
        val data = mapOf("nome" to key, "emojis" to emojis)
        temasRef.document(key).set(data).await()
    }

    suspend fun editarTema(idOuNome: String, nome: String, emojis: List<String>) {
        val key = (idOuNome.ifBlank { nome }).lowercase()
        val data = mapOf("nome" to nome.lowercase(), "emojis" to emojis)
        temasRef.document(key).set(data).await()
    }

    suspend fun deletarTema(idOuNome: String) {
        temasRef.document(idOuNome.lowercase()).delete().await()
    }

    suspend fun listarContas(): List<String> {
        val snap = contasRef.get().await()
        return snap.documents.mapNotNull { it.getString("nomeUsuario") }
    }

    suspend fun deletarConta(nome: String) {
        contasRef.document(nome).delete().await()
    }

    suspend fun adicionarPartidaClassico(partida: Partida) {
        val data = mapOf(
            "nomeJogador" to partida.usuarioId.toString(),
            "temaNome" to partida.tema,
            "tentativas" to partida.tentativas,
            "dataPartida" to partida.dataPartida
        )
        partidasClassicoRef.add(data).await()
    }

    suspend fun listarPartidasClassico(): List<Partida> {
        val snap = partidasClassicoRef.get().await()
        return snap.documents.mapNotNull { d ->
            val usuarioNome = d.getString("nomeJogador") ?: return@mapNotNull null
            val tema = d.getString("temaNome") ?: return@mapNotNull null
            val tentativas = (d.get("tentativas") as? Number)?.toInt() ?: return@mapNotNull null
            val data = (d.get("dataPartida") as? Number)?.toLong() ?: System.currentTimeMillis()
            Partida(
                usuarioId = 0L,
                tema = tema,
                tentativas = tentativas,
                dataPartida = data
            )
        }
    }

    suspend fun deletarTodasPartidasClassico() {
        val snap = partidasClassicoRef.get().await()
        for (d in snap.documents) partidasClassicoRef.document(d.id).delete().await()
    }

    suspend fun adicionarPartidaTimeAttack(p: PartidaTimeAttack) {
        val data = mapOf(
            "usuarioId" to p.usuarioId,
            "nomeJogador" to p.nomeJogador,
            "pontuacao" to p.pontuacao,
            "temaNome" to p.temaNome,
            "dataPartida" to p.dataPartida
        )
        partidasTimeAttackRef.add(data).await()
    }

    suspend fun listarPartidasTimeAttack(): List<PartidaTimeAttack> {
        val snap = partidasTimeAttackRef.get().await()
        return snap.documents.mapNotNull { d ->
            val usuarioId = (d.get("usuarioId") as? Number)?.toLong() ?: 0L
            val nome = d.getString("nomeJogador") ?: return@mapNotNull null
            val tema = d.getString("temaNome") ?: return@mapNotNull null
            val pont = (d.get("pontuacao") as? Number)?.toInt() ?: return@mapNotNull null
            val data = (d.get("dataPartida") as? Number)?.toLong() ?: System.currentTimeMillis()
            PartidaTimeAttack(
                usuarioId = usuarioId,
                nomeJogador = nome,
                pontuacao = pont,
                temaNome = tema,
                dataPartida = data
            )
        }
    }

    suspend fun deletarTodasPartidasTimeAttack() {
        val snap = partidasTimeAttackRef.get().await()
        for (d in snap.documents) partidasTimeAttackRef.document(d.id).delete().await()
    }
}
