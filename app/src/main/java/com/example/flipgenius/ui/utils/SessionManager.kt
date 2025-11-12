package com.example.flipgenius.ui.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "FlipGeniusSession",
        Context.MODE_PRIVATE
    )

    fun salvarUsuario(usuarioId: Long, nomeUsuario: String, isAdmin: Boolean) {
        prefs.edit().apply {
            putLong("usuarioId", usuarioId)
            putString("nomeUsuario", nomeUsuario)
            putBoolean("isAdmin", isAdmin)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }

    fun getUsuarioId(): Long {
        return prefs.getLong("usuarioId", 0)
    }

    fun getNomeUsuario(): String {
        return prefs.getString("nomeUsuario", "") ?: ""
    }

    fun isAdmin(): Boolean {
        return prefs.getBoolean("isAdmin", false)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("isLoggedIn", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
