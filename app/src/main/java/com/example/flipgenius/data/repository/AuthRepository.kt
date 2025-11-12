package com.example.flipgenius.data.repository

import com.example.flipgenius.data.local.dao.UsuarioDao
import com.example.flipgenius.data.local.entities.Usuario
import com.example.flipgenius.ui.utils.PasswordUtils

class AuthRepository(
    private val usuarioDao: UsuarioDao
) {
    suspend fun login(nome: String, senha: String): Usuario? {
        val senhaHash = PasswordUtils.hashPassword(senha)
        return usuarioDao.login(nome, senhaHash)
    }
    
    suspend fun registrar(nome: String, senha: String): Usuario {
        val senhaHash = PasswordUtils.hashPassword(senha)
        val usuario = Usuario(
            nome = nome,
            senhaHash = senhaHash,
            isAdmin = false
        )
        usuarioDao.insert(usuario)
        return usuarioDao.getUsuarioByNome(nome) ?: usuario
    }
    
    suspend fun getUsuarioPorId(id: Long): Usuario? {
        // Como não temos esse método no DAO, vamos buscar por nome
        // Na prática, você deveria adicionar esse método ao DAO
        return null
    }
}


