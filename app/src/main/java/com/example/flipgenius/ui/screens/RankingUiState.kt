package com.example.flipgenius.ui.screens

import com.example.flipgenius.data.local.entities.PartidaTimeAttack

sealed interface RankingUiState {

    object Loading: RankingUiState

    data class Success(val partidas: List<PartidaTimeAttack>) : RankingUiState

    object Error: RankingUiState

}