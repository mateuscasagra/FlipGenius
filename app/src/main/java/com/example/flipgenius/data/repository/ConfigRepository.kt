package com.example.flipgenius.data.repository

import com.example.flipgenius.data.local.entities.ConfigJogador
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Repositório para ConfigJogador usando Firebase Firestore.
 * - CRUD completo via Firestore
 * - Erros específicos de conexão tratados
 * - Listener em tempo real via snapshot listener
 */
class ConfigRepository private constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("configJogador")

    suspend fun obterPorNome(nome: String): ConfigJogador? = withContext(Dispatchers.IO) {
        try {
            val doc = collection.document(nome).get().await()
            if (!doc.exists()) return@withContext null
            doc.toConfig()
        } catch (e: FirebaseNetworkException) {
            throw e
        } catch (e: FirebaseFirestoreException) {
            throw e
        }
    }

    suspend fun criarOuObter(nome: String, senha: String): ConfigJogador = withContext(Dispatchers.IO) {
        validarNome(nome)
        validarSenha(nova = senha)
        val existente = obterPorNome(nome)
        if (existente != null) return@withContext existente
        val salt = gerarSalt()
        val hash = hashPassword(senha, salt)
        val now = System.currentTimeMillis()
        val novo = ConfigJogador(
            nomeUsuario = nome,
            senhaHash = hash,
            senhaSalt = salt,
            temaPreferido = "padrao",
            dataCriacao = now,
            dataAtualizacao = now
        )
        collection.document(nome).set(novo.toMap()).await()
        novo
    }

    suspend fun validarLogin(nome: String, senha: String): Boolean = withContext(Dispatchers.IO) {
        val cfg = obterPorNome(nome) ?: return@withContext false
        val candidate = hashPassword(senha, cfg.senhaSalt)
        candidate == cfg.senhaHash
    }

    suspend fun atualizarNome(antigoNome: String, novoNome: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            validarNome(novoNome)
            val existenteNovo = obterPorNome(novoNome)
            if (existenteNovo != null) return@withContext Result.failure(IllegalStateException("Nome de usuário já existe"))
            val atual = obterPorNome(antigoNome) ?: return@withContext Result.failure(IllegalArgumentException("Usuário não encontrado"))
            val atualizado = atual.copy(
                nomeUsuario = novoNome,
                dataAtualizacao = System.currentTimeMillis()
            )
            // Firestore não renomeia doc; copiar novo e apagar antigo
            collection.document(novoNome).set(atualizado.toMap()).await()
            collection.document(antigoNome).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun trocarSenha(nome: String, senhaAtual: String, novaSenha: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            validarSenha(nova = novaSenha)
            val config = obterPorNome(nome) ?: return@withContext Result.failure(IllegalArgumentException("Usuário não encontrado"))
            val atualHash = hashPassword(senhaAtual, config.senhaSalt)
            if (atualHash != config.senhaHash) {
                Result.failure(IllegalArgumentException("Senha atual incorreta"))
            } else {
                val novoSalt = gerarSalt()
                val novoHash = hashPassword(novaSenha, novoSalt)
                val atualizado = config.copy(
                    senhaHash = novoHash,
                    senhaSalt = novoSalt,
                    dataAtualizacao = System.currentTimeMillis()
                )
                collection.document(nome).set(atualizado.toMap()).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun atualizarTema(nome: String, tema: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (tema.isBlank()) return@withContext Result.failure(IllegalArgumentException("Tema inválido"))
            val config = obterPorNome(nome) ?: return@withContext Result.failure(IllegalArgumentException("Usuário não encontrado"))
            collection.document(nome)
                .update(mapOf("temaPreferido" to tema, "dataAtualizacao" to System.currentTimeMillis()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarConta(nome: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            collection.document(nome).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observarConfig(nome: String): Flow<ConfigJogador?> = callbackFlow {
        val reg = collection.document(nome).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toConfig())
            } else {
                trySend(null)
            }
        }
        awaitClose { reg.remove() }
    }

    private fun validarNome(nome: String) {
        require(nome.isNotBlank()) { "Nome inválido" }
        require(nome.length in 3..24) { "Nome deve ter entre 3 e 24 caracteres" }
    }

    private fun validarSenha(nova: String) {
        require(nova.length >= 6) { "Senha deve ter ao menos 6 caracteres" }
    }

    private fun gerarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt.toByteArray())
        val digest = md.digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(digest)
    }

    private fun Map<String, Any>.toConfig(): ConfigJogador = ConfigJogador(
        nomeUsuario = this["nomeUsuario"] as String,
        senhaHash = this["senhaHash"] as String,
        senhaSalt = this["senhaSalt"] as String,
        temaPreferido = (this["temaPreferido"] as? String) ?: "padrao",
        dataCriacao = (this["dataCriacao"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        dataAtualizacao = (this["dataAtualizacao"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.toConfig(): ConfigJogador =
        (data ?: emptyMap()).toConfig()

    private fun ConfigJogador.toMap(): Map<String, Any> = mapOf(
        "nomeUsuario" to nomeUsuario,
        "senhaHash" to senhaHash,
        "senhaSalt" to senhaSalt,
        "temaPreferido" to temaPreferido,
        "dataCriacao" to dataCriacao,
        "dataAtualizacao" to dataAtualizacao
    )

    companion object {
        fun create(): ConfigRepository {
            return ConfigRepository(FirebaseFirestore.getInstance())
        }
    }
}