package com.example.flipgenius.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TemaRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getEmojis(themeName: String): List<String> {
        return try {
            val doc = firestore.collection("temas").document(themeName.lowercase()).get().await()
            val list = doc.get("emojis") as? List<*>
            list?.mapNotNull { it?.toString() }?.takeIf { it.isNotEmpty() } ?: fallback(themeName)
        } catch (_: Exception) {
            fallback(themeName)
        }
    }

    private fun fallback(themeName: String): List<String> {
        return when (themeName.lowercase()) {
            "animais" -> listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊")
            "frutas" -> listOf("🍎", "🍌", "🍇", "🍓", "🍍", "🍑")
            "esportes" -> listOf("⚽️", "🏀", "🏈", "⚾️", "🎾", "🏓")
            "comidas" -> listOf("🍔", "🍟", "🍕", "🌭", "🥗", "🍣")
            else -> listOf("🙂", "😀", "😅", "😉", "😎", "🤩")
        }
    }
}