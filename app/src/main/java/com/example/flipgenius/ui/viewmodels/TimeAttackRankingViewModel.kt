package com.example.flipgenius.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.example.flipgenius.data.repository.TimeAttackRepository
import com.example.flipgenius.ui.screens.RankingUiState
import com.example.flipgenius.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class TimeAttackRankingViewModel(
    private val repository: TimeAttackRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _temaSelecionado = MutableStateFlow("Todos")
    val temaSelecionado: StateFlow<String> = _temaSelecionado.asStateFlow()

    private val _filtrarPorUsuario = MutableStateFlow(false)
    val filtrarPorUsuario: StateFlow<Boolean> = _filtrarPorUsuario.asStateFlow()

    val rankingUiState: StateFlow<RankingUiState> = repository.allPartidasFlow
        .combine(_temaSelecionado) { partidas, filtroTema ->
            val filtradasTema = if (filtroTema == "Todos") partidas else partidas.filter { it.temaNome == filtroTema }
            filtradasTema
        }
        .combine(_filtrarPorUsuario) { partidas, porUsuario ->
            val uid = sessionManager.getUsuarioId()
            val filtradasUsuario = if (porUsuario && uid > 0) partidas.filter { it.usuarioId == uid } else partidas
            val ordenadas = filtradasUsuario.sortedByDescending { it.pontuacao }
            RankingUiState.Success(ordenadas) as RankingUiState
        }
        .catch { emit(RankingUiState.Error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RankingUiState.Loading)

    fun selecionarTema(tema: String) {
        _temaSelecionado.value = tema
    }

    fun alternarFiltroUsuario() {
        _filtrarPorUsuario.value = !_filtrarPorUsuario.value
    }

    fun clearHistory() {
        viewModelScope.launch { repository.deleteAllPartidas() }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
<<<<<<< HEAD
                val db = AppDatabase.getInstance(context)
                val repo = TimeAttackRepository(db.timeAttackDao())
                val session = SessionManager(context.applicationContext)
=======
                val repo = TimeAttackRepository.create()
>>>>>>> 20f133e (FIREBASE)
                @Suppress("UNCHECKED_CAST")
                return TimeAttackRankingViewModel(repo, session) as T
            }
        }
    }
}
