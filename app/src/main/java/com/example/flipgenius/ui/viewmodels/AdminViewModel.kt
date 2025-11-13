package com.example.flipgenius.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.data.remote.FirebaseService
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdminUiState(
    val selectedTabIndex: Int = 0,
    val showAddTemaDialog: Boolean = false,
    val temasList: List<Pair<String, String>> = emptyList(),
    val accountList: List<String> = emptyList()
)

class AdminViewModel : ViewModel() {

    private val service = FirebaseService()
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()

    init {
        carregarTemas()
    }

    fun onTabChange(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun onShowAddDialog() {
        _uiState.update { it.copy(showAddTemaDialog = true) }
    }

    fun onDismissAddDialog() {
        _uiState.update { it.copy(showAddTemaDialog = false) }
    }

    fun onAddTheme(nome: String, emojis: String) {
        if (nome.isBlank()) return
        val lista = emojis.toCharArray().map { it.toString() }.filter { it.isNotBlank() }
        viewModelScope.launch {
            try {
                service.adicionarTema(nome, lista)
                carregarTemas()
            } finally {
                onDismissAddDialog()
            }
        }
    }

    fun onEditTheme(nome: String, emojis: String) {
        val lista = emojis.toCharArray().map { it.toString() }.filter { it.isNotBlank() }
        viewModelScope.launch {
            try {
                service.editarTema(nome, nome, lista)
                carregarTemas()
            } catch (_: Exception) { }
        }
    }

    fun onDeleteTheme(nome: String) {
        viewModelScope.launch {
            try {
                service.deletarTema(nome)
                carregarTemas()
            } catch (_: Exception) { }
        }
    }

    private fun carregarTemas() {
        viewModelScope.launch {
            try {
                val temas = service.listarTemas().map { it.nome to it.emojis.joinToString("") }
                _uiState.update { it.copy(temasList = temas) }
            } catch (_: Exception) { }
        }
    }

    fun onDeleteAccount(email: String) { }
}
