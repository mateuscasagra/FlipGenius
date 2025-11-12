package com.example.flipgenius.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.data.local.entities.PartidaTimeAttack
import com.example.flipgenius.data.repository.TimeAttackRepository
import com.example.flipgenius.data.repository.TemaRepository
import com.example.flipgenius.model.CartaJogo
import kotlinx.coroutines.Dispatchers
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
import kotlin.concurrent.timer

class TimeAttackViewModel(
    private val timeAttackRepository: TimeAttackRepository,
    private val temaRepository: TemaRepository
    ) : ViewModel() {

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


     fun iniciarJogo(playerName: String, themeName: String) {
        viewModelScope.launch {

            nomeJogadorAtual = playerName
            nomeTemaAtual = themeName
            _tempoRestante.value = 60
            timerJob?.cancel()
            iniciarTimer()
            _pontuacaoFinal.value = null

            val emojisDoTema = temaRepository.getEmojis(nomeTema)


            val cartasPares = emojisDoTema.flatMap { emoji ->
                listOf(
                    CartaJogo(id = 0, conteudo = emoji),
                    CartaJogo(id = 0, conteudo = emoji)
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
            }
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
                    if (primeiraCarta.conteudo == segundaCarta.conteudo) {

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
                            salvarPartida()
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
                }
                .onCompletion {
                    if(!_jogoFinalizado.value){
                        _jogoFinalizado.value = true
                    }
            }
            .launchIn(viewModelScope)
    }


    private fun calcularPontuacao(): Int {
        return 1000 + (_tempoRestante.value * 10)
    }

    private fun salvarPartida() {
        viewModelScope.launch(Dispatchers.IO){
            val pontuacaoFinal = calcularPontuacao()
            _pontuacaoFinal.value = pontuacaoFinal

            val partida = PartidaTimeAttack(
                nomeJogador = nomeJogadorAtual,
                pontuacao = pontuacaoFinal,
                temaNome = nomeTemaAtual,
                dataPartida = System.currentTimeMillis()
            )

            timeAttackRepository.insertPartida(partida)

        }
    }
}
