package com.example.flipgenius.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
<<<<<<< HEAD
import androidx.lifecycle.ViewModelProvider
import com.example.flipgenius.data.local.AppDatabase
import com.example.flipgenius.data.local.entities.PartidaTimeAttack
import com.example.flipgenius.data.repository.TimeAttackRepository
import com.example.flipgenius.data.repository.TemaRepository
=======
>>>>>>> 1bc6e9e (ajustado layout das telas e logica com o banco)
import com.example.flipgenius.model.CartaJogo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
<<<<<<< HEAD
 
=======
>>>>>>> 1bc6e9e (ajustado layout das telas e logica com o banco)

class TimeAttackViewModel : ViewModel() {

    private val _cartas = MutableStateFlow<List<CartaJogo>>(emptyList())
    val cartas: StateFlow<List<CartaJogo>> = _cartas.asStateFlow()

    private val _jogoFinalizado = MutableStateFlow(false)
    val jogoFinalizado: StateFlow<Boolean> = _jogoFinalizado.asStateFlow()

    private var cartasViradas: List<Int> = emptyList()
    private var podeVirar = true

    private var _tempoRestante = MutableStateFlow(60)
    val tempoRestante: StateFlow<Int> = _tempoRestante.asStateFlow()

    private var timerJob: Job? = null

    private var nomeJogadorAtual: String = ""

    private var nomeTemaAtual: String =  ""

    private val _pontuacaoFinal = MutableStateFlow<Int?>(null)
    val pontuacaoFinal: StateFlow<Int?> = _pontuacaoFinal.asStateFlow()

    private val _uiState = MutableStateFlow<TimeAttackUiState>(TimeAttackUiState.Idle)
    val uiState: StateFlow<TimeAttackUiState> = _uiState.asStateFlow()

    private fun updateUiState() {
        if (!_jogoFinalizado.value) {
            if (_cartas.value.isEmpty()) {
                _uiState.value = TimeAttackUiState.Idle
            } else {
                _uiState.value = TimeAttackUiState.Playing(
                    cartas = _cartas.value,
                    tempoRestante = _tempoRestante.value
                )
            }
        } else {
            val resultado = if (_cartas.value.all { it.encontrada }) Resultado.Vitoria else Resultado.Derrota
            val score = _pontuacaoFinal.value ?: calcularPontuacao()
            _uiState.value = TimeAttackUiState.Finished(
                cartas = _cartas.value,
                tempoRestante = _tempoRestante.value,
                pontuacaoFinal = score,
                resultado = resultado
            )
        }
    }


    fun iniciarJogo(playerName: String = "", themeName: String = "") {
        viewModelScope.launch {
            nomeJogadorAtual = playerName
            nomeTemaAtual = themeName
            _tempoRestante.value = 60
            timerJob?.cancel()
            iniciarTimer()
            _pontuacaoFinal.value = null

<<<<<<< HEAD
            val emojisDoTema = temaRepository.getEmojis(nomeTemaAtual)


            val cartasPares = emojisDoTema.flatMap { emoji ->
=======
            // Gera pares com números (1..6), dois de cada
            val numeros = listOf(1, 2, 3, 4, 5, 6)
            val cartasPares = numeros.flatMap { numero ->
>>>>>>> 1bc6e9e (ajustado layout das telas e logica com o banco)
                listOf(
                    CartaJogo(id = 0, numero = numero),
                    CartaJogo(id = 0, numero = numero)
                )
            }

            val cartasEmbaralhadas = cartasPares.shuffled()
            val cartasComIndices = cartasEmbaralhadas.mapIndexed { index, carta ->
                carta.copy(id = index)
            }
            _cartas.value = cartasComIndices
            _jogoFinalizado.value = false
            cartasViradas = emptyList()
            podeVirar = true
<<<<<<< HEAD
            updateUiState()
            }
=======
        }
>>>>>>> 1bc6e9e (ajustado layout das telas e logica com o banco)
    }

     fun virarCarta(cartaId: Int) {
        if (!podeVirar) return

        val carta = _cartas.value.find { it.id == cartaId }
        if (carta == null || carta.virada || carta.encontrada) return

        viewModelScope.launch {

            val cartasAtualizadas = _cartas.value.map { c ->
                if (c.id == cartaId) c.copy(virada = true) else c
            }
            _cartas.value = cartasAtualizadas

            cartasViradas = cartasViradas + cartaId

            if (cartasViradas.size == 2) {
                podeVirar = false
                delay(1000)

                val primeiraCarta = _cartas.value.find { it.id == cartasViradas[0] }
                val segundaCarta = _cartas.value.find { it.id == cartasViradas[1] }

                if (primeiraCarta != null && segundaCarta != null) {
                    if (primeiraCarta.numero == segundaCarta.numero) {

                        val cartasAtualizadas = _cartas.value.map { c ->
                            if (c.id == cartasViradas[0] || c.id == cartasViradas[1]) {
                                c.copy(encontrada = true, virada = true)
                            } else {
                                c
                            }
                        }
                        _cartas.value = cartasAtualizadas

                        val todasEncontradas = _cartas.value.all { it.encontrada }
                        if (todasEncontradas) {
                            _jogoFinalizado.value = true
                            timerJob?.cancel()
                            val pontuacao = calcularPontuacao()
                            _pontuacaoFinal.value = pontuacao
                            salvarPartida()
                            updateUiState()
                        }
                    } else {
                        val cartasAtualizadas = _cartas.value.map { c ->
                            if (c.id == cartasViradas[0] || c.id == cartasViradas[1]) {
                                c.copy(virada = false)
                            } else {
                                c
                            }
                        }
                        _cartas.value = cartasAtualizadas
                        updateUiState()
                    }
                }

                cartasViradas = emptyList()
                podeVirar = true
            }
        }
    }

    private fun iniciarTimer() {

        timerJob?.cancel()

            timerJob = (60 downTo 0).asFlow()
                .onEach { delay(1000L) }
                .onEach { tempo ->
                    _tempoRestante.value = tempo
                    updateUiState()
                }
                .onCompletion {
                    if(!_jogoFinalizado.value){
                        _jogoFinalizado.value = true
                        _pontuacaoFinal.value = calcularPontuacao()
                        updateUiState()
                    }
            }
            .launchIn(viewModelScope)
    }


    private fun calcularPontuacao(): Int {
        return 1000 + (_tempoRestante.value * 10)
    }

    private fun salvarPartida() {
        // No-op para compilar enquanto o repositório de TimeAttack não está disponível
        val pontuacao = calcularPontuacao()
        _pontuacaoFinal.value = pontuacao
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getInstance(context)
                val timeRepo = TimeAttackRepository(db.timeAttackDao())
                val temaRepo = TemaRepository()
                @Suppress("UNCHECKED_CAST")
                return TimeAttackViewModel(timeRepo, temaRepo) as T
            }
        }
    }
}
sealed interface TimeAttackUiState {
    object Idle : TimeAttackUiState
    data class Playing(
        val cartas: List<CartaJogo>,
        val tempoRestante: Int
    ) : TimeAttackUiState
    data class Finished(
        val cartas: List<CartaJogo>,
        val tempoRestante: Int,
        val pontuacaoFinal: Int,
        val resultado: Resultado
    ) : TimeAttackUiState
}

enum class Resultado { Vitoria, Derrota }
