package com.example.flipgenius.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.data.local.entities.ConfigJogador
import com.example.flipgenius.data.repository.ConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar estado de configurações e perfil do jogador.
 */
data class ConfigUiState(
    val currentUserId: Long? = null,
    val nomeUsuario: String = "",
    val temaPreferido: String = "padrao",
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class ConfigViewModel(
    private val repository: ConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigUiState())
    val uiState: StateFlow<ConfigUiState> = _uiState.asStateFlow()

    fun carregarPerfilPorNome(nome: String) {
        viewModelScope.launch {
            val config = repository.obterPorNome(nome)
            if (config == null) {
                _uiState.update { it.copy(error = "Usuário não encontrado") }
            } else setFromConfig(config)
        }
    }

    fun carregarPerfilPorId(id: Long) {
        viewModelScope.launch {
            val config = repository.obterPorId(id)
            if (config == null) {
                _uiState.update { it.copy(error = "Usuário não encontrado") }
            } else setFromConfig(config)
        }
    }

    fun onAtualizarNome(novoNome: String) {
        val id = _uiState.value.currentUserId ?: return
        viewModelScope.launch {
            val result = repository.atualizarNome(id, novoNome.trim())
            if (result.isSuccess) {
                _uiState.update { it.copy(nomeUsuario = novoNome, successMessage = "Nome atualizado") }
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun onTrocarSenha(senhaAtual: String, novaSenha: String) {
        val id = _uiState.value.currentUserId ?: return
        viewModelScope.launch {
            val result = repository.trocarSenha(id, senhaAtual, novaSenha)
            _uiState.update { state ->
                if (result.isSuccess) state.copy(successMessage = "Senha alterada com sucesso")
                else state.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun onAtualizarTema(tema: String) {
        val id = _uiState.value.currentUserId ?: return
        viewModelScope.launch {
            val result = repository.atualizarTema(id, tema)
            _uiState.update { state ->
                if (result.isSuccess) state.copy(temaPreferido = tema, successMessage = "Tema atualizado")
                else state.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun onDeletarConta() {
        val id = _uiState.value.currentUserId ?: return
        viewModelScope.launch {
            val result = repository.deletarConta(id)
            _uiState.update { state ->
                if (result.isSuccess) ConfigUiState()
                else state.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    private fun setFromConfig(config: ConfigJogador) {
        _uiState.update {
            it.copy(
                currentUserId = config.id,
                nomeUsuario = config.nomeUsuario,
                temaPreferido = config.temaPreferido,
                createdAt = config.dataCriacao,
                updatedAt = config.dataAtualizacao,
                error = null,
                successMessage = null
            )
        }
    }
}