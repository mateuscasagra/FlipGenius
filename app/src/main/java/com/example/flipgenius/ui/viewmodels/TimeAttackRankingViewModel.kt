package com.example.flipgenius.ui.screens.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.data.repository.TimeAttackRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.flipgenius.ui.screens.RankingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class TimeAttackViewModel(
    private val repository: TimeAttackRepository
) : ViewModel() {

    private val _temaSelecionado = MutableStateFlow<String?>(null)
    val temaSelecionado: StateFlow<String?> = _temaSelecionado

    val rankingUiState: StateFlow<RankingUiState> = repository.allPartidasFlow
        .combine(_temaSelecionado) {listaPartidas, tema ->

            val listaFiltrada = if (tema == null) {
                listaPartidas
            } else {
                listaPartidas.filter { it.temaNome == tema }
            }
            val listaOrdenada = listaFiltrada.sortedByDescending { it.pontuacao }

            RankingUiState.Success(listaOrdenada) as RankingUiState
        }
        .catch {
            emit(RankingUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RankingUiState.Loading
        )

    fun selecionarTema(tema: String?){
        _temaSelecionado.value = tema
    }

    fun clearHistory(){
        viewModelScope.launch {
            repository.deleteAllPartidas()
        }
    }
}