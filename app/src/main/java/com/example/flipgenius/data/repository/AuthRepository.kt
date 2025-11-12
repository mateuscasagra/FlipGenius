package com.example.flipgenius.data.repository

import com.example.flipgenius.data.local.dao.UsuarioDao
import com.example.flipgenius.data.local.entities.Usuario
import com.example.flipgenius.ui.utils.PasswordUtils

class AuthRepository(private val usuarioDao: UsuarioDao) {

    suspend fun cadastrarUsuario(nome: String, senha: String): Result<Usuario> {
        return try {
            val usuarioExiste = usuarioDao.getUsuarioByNome(nome)
            if (usuarioExiste != null) {
                Result.failure(Exception("Usuário já existe"))
            } else {
                val senhaHash = PasswordUtils.hashPassword(senha)
                val novoUsuario = Usuario(
                    nome = nome,
                    senhaHash = senhaHash
                )
                usuarioDao.insert(novoUsuario)
                val usuarioCriado = usuarioDao.getUsuarioByNome(nome)
                Result.success(usuarioCriado!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(nome: String, senha: String): Result<Usuario> {
        return try {
            val senhaHash = PasswordUtils.hashPassword(senha)
            val usuario = usuarioDao.login(nome, senhaHash)
            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception("Usuário ou senha inválidos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun atualizarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            usuarioDao.update(usuario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarUsuario(id: Long): Result<Unit> {
        return try {
            usuarioDao.delete(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
