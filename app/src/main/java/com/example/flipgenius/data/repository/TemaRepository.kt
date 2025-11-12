package com.example.flipgenius.data.repository

/**
 * Repositório simples para fornecer emojis por tema.
 * Futuramente pode ser substituído por uma fonte remota (Firestore).
 */
class TemaRepository {
    fun getEmojis(themeName: String): List<String> {
        return when (themeName.lowercase()) {
            "animais" -> listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊")
            "frutas" -> listOf("🍎", "🍌", "🍇", "🍓", "🍍", "🍑")
            "esportes" -> listOf("⚽️", "🏀", "🏈", "⚾️", "🎾", "🏓")
            "comidas" -> listOf("🍔", "🍟", "🍕", "🌭", "🥗", "🍣")
            else -> listOf("🙂", "😀", "😅", "😉", "😎", "🤩")
        }
    }
}