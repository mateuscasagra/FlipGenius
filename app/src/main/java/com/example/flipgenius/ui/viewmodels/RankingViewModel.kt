package com.example.flipgenius.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.data.local.entities.Partida
import com.example.flipgenius.data.repository.PartidaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

sealed interface RankingClassicUiState {
    object Loading : RankingClassicUiState
    data class Success(val partidas: List<Partida>) : RankingClassicUiState
    object Error : RankingClassicUiState
}

class RankingViewModel(
    private val repository: PartidaRepository
) : ViewModel() {

    private val _temaSelecionado = MutableStateFlow("Todos")
    val temaSelecionado: StateFlow<String> = _temaSelecionado.asStateFlow()

    val rankingUiState: StateFlow<RankingClassicUiState> = repository.getAllPartidas()
        .combine(_temaSelecionado) { partidas, filtroTema ->
            val filtradas = if (filtroTema == "Todos") partidas else partidas.filter { it.tema == filtroTema }
            val ordenadas = filtradas.sortedBy { it.tentativas }
            RankingClassicUiState.Success(ordenadas) as RankingClassicUiState
        }
        .catch { emit(RankingClassicUiState.Error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RankingClassicUiState.Loading)

    fun selecionarTema(tema: String) {
        _temaSelecionado.value = tema
    }

    fun clearHistory() {
        viewModelScope.launch { repository.limparHistorico() }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = PartidaRepository.create()
                @Suppress("UNCHECKED_CAST")
                return RankingViewModel(repo) as T
            }
        }
    }
}
