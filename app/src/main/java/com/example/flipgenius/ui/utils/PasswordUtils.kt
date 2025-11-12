package com.example.flipgenius.ui.utils

import java.security.MessageDigest

object PasswordUtils {

    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verificarSenha(senha: String, senhaHash: String): Boolean {
        return hashPassword(senha) == senhaHash
    }
}
