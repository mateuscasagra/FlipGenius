package com.example.flipgenius.data.repository

import android.content.Context
import com.example.flipgenius.data.local.AppDatabase
import com.example.flipgenius.data.local.dao.ConfigJogadorDao
import com.example.flipgenius.data.local.entities.ConfigJogador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Repositório para ConfigJogador, com validações simples e hashing de senha.
 */
class ConfigRepository private constructor(
    private val dao: ConfigJogadorDao
) {

    suspend fun obterPorId(id: Long): ConfigJogador? = withContext(Dispatchers.IO) { dao.getById(id) }

    suspend fun obterPorNome(nome: String): ConfigJogador? = withContext(Dispatchers.IO) { dao.getByNome(nome) }

    suspend fun criarOuObter(nome: String, senha: String): ConfigJogador = withContext(Dispatchers.IO) {
        val existente = dao.getByNome(nome)
        if (existente != null) return@withContext existente
        validarNome(nome)
        validarSenha(nova = senha)
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
        val id = dao.insert(novo)
        dao.getById(id)!!
    }

    suspend fun atualizarNome(id: Long, novoNome: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            validarNome(novoNome)
            val config = dao.getById(id) ?: return@withContext Result.failure(IllegalArgumentException("Usuário não encontrado"))
            if (dao.getByNome(novoNome) != null && config.nomeUsuario != novoNome) {
                Result.failure(IllegalStateException("Nome de usuário já existe"))
            } else {
                dao.update(config.copy(nomeUsuario = novoNome, dataAtualizacao = System.currentTimeMillis()))
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun trocarSenha(id: Long, senhaAtual: String, novaSenha: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            validarSenha(nova = novaSenha)
            val config = dao.getById(id) ?: return@withContext Result.failure(IllegalArgumentException("Usuário não encontrado"))
            val atualHash = hashPassword(senhaAtual, config.senhaSalt)
            if (atualHash != config.senhaHash) {
                Result.failure(IllegalArgumentException("Senha atual incorreta"))
            } else {
                val novoSalt = gerarSalt()
                val novoHash = hashPassword(novaSenha, novoSalt)
                dao.update(
                    config.copy(
                        senhaHash = novoHash,
                        senhaSalt = novoSalt,
                        dataAtualizacao = System.currentTimeMillis()
                    )
                )
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun atualizarTema(id: Long, tema: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (tema.isBlank()) return@withContext Result.failure(IllegalArgumentException("Tema inválido"))
            val config = dao.getById(id) ?: return@withContext Result.failure(IllegalArgumentException("Usuário não encontrado"))
            dao.update(config.copy(temaPreferido = tema, dataAtualizacao = System.currentTimeMillis()))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarConta(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val config = dao.getById(id) ?: return@withContext Result.failure(IllegalArgumentException("Usuário não encontrado"))
            dao.delete(config)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    companion object {
        fun create(context: Context): ConfigRepository {
            val db = AppDatabase.getInstance(context)
            return ConfigRepository(db.configJogadorDao())
        }
    }
}