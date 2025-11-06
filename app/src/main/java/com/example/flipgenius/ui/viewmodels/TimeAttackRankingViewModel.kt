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


class TimeAttackViewModel(
    private val repository: TimeAttackRepository
) : ViewModel() {

    val rankingUiState: StateFlow<RankingUiState> = repository.allPartidasFlow
        .map { lista ->
            RankingUiState.Success(lista) as RankingUiState
        }
        .catch {
            emit(RankingUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RankingUiState.Loading
        )

    fun clearHistory(){
        viewModelScope.launch {
            repository.deleteAllPartidas()
        }
    }

}