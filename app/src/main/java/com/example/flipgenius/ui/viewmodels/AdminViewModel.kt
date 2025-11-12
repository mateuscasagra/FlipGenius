package com.example.flipgenius.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdminUiState(
    val selectedTabIndex: Int = 0,
    val showAddTemaDialog: Boolean = false,
    val temasList: List<Pair<String, String>> = listOf(
        "Animais" to "🐶🐱🐭🐹",
        "Frutas" to "🍎🍌🍇🍓",
        "Esportes" to "⚽️🏀🏈⚾️"
    ),
    val accountList: List<String> = listOf(
        "marco@email.com",
        "joao@email.com",
        "rhillary@email.com"
    )
)


class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()


    fun onTabChange(index: Int) {
        _uiState.update { estadoAtual ->
            estadoAtual.copy(selectedTabIndex = index)
        }
    }

    fun onShowAddDialog() {
        _uiState.update { estadoAtual ->
            estadoAtual.copy(showAddTemaDialog = true)
        }
    }

    fun onDismissAddDialog() {
        _uiState.update { estadoAtual ->
            estadoAtual.copy(showAddTemaDialog = false)
        }
    }

    fun onAddTheme(nome: String, emojis: String) {
        // Chamar o Repositório aqui
        onDismissAddDialog()
    }

    fun onDeleteAccount(email: String) {
        // Lógica para apagar a conta (chama o Repositório)
    }

}