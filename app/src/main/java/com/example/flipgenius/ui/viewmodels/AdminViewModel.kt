package com.example.flipgenius.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.data.remote.FirebaseService
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.BreakIterator

data class AdminUiState(
    val selectedTabIndex: Int = 0,
    val showAddTemaDialog: Boolean = false,
    val temasList: List<Pair<String, String>> = emptyList(),
    val accountList: List<String> = emptyList(),
    val showEditTemaDialog: Boolean = false,
    val editNome: String = "",
    val editEmojis: String = ""
)

class AdminViewModel : ViewModel() {

    private val service = FirebaseService()
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()

    init {
        carregarTemas()
        carregarContas()
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
        val lista = splitEmojis(emojis)
        viewModelScope.launch {
            try {
                service.adicionarTema(nome, lista)
                carregarTemas()
            } finally {
                onDismissAddDialog()
            }
        }
    }

    fun onStartEditTheme(nome: String, emojisPreview: String) {
        _uiState.update { it.copy(showEditTemaDialog = true, editNome = nome, editEmojis = emojisPreview) }
    }

    fun onEditTheme(nome: String, emojis: String) {
        val lista = splitEmojis(emojis)
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

    fun onDismissEditDialog() {
        _uiState.update { it.copy(showEditTemaDialog = false, editNome = "", editEmojis = "") }
    }

    private fun carregarContas() {
        viewModelScope.launch {
            try {
                val contas = service.listarContas()
                _uiState.update { it.copy(accountList = contas) }
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

    fun onDeleteAccount(email: String) {
        viewModelScope.launch {
            try {
                service.deletarConta(email)
                carregarContas()
            } catch (_: Exception) { }
        }
    }

    private fun splitEmojis(input: String): List<String> {
        val cleaned = input.replace(Regex("[,\\s]+"), "")
        val bi = BreakIterator.getCharacterInstance()
        bi.setText(cleaned)
        val out = mutableListOf<String>()
        var start = bi.first()
        var end = bi.next()
        while (end != BreakIterator.DONE) {
            val token = cleaned.substring(start, end)
            if (token.isNotBlank()) out.add(token)
            start = end
            end = bi.next()
        }
        return out
    }
}
