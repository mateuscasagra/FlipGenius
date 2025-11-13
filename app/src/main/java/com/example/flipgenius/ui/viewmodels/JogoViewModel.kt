package com.example.flipgenius.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.data.local.entities.Partida
import com.example.flipgenius.data.repository.PartidaRepository
import com.example.flipgenius.model.CartaJogo
import com.example.flipgenius.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JogoViewModel(context: Context, tema: String = "padrao") : ViewModel() {
    
    private val sessionManager = SessionManager(context.applicationContext)
    private val partidaRepository = PartidaRepository.create(context)
    private val temaAtual = tema.ifBlank { "padrao" }
    
    private val _cartas = MutableStateFlow<List<CartaJogo>>(emptyList())
    val cartas: StateFlow<List<CartaJogo>> = _cartas.asStateFlow()
    
    private val _pontuacao = MutableStateFlow(0)
    val pontuacao: StateFlow<Int> = _pontuacao.asStateFlow()
    
    private val _tentativas = MutableStateFlow(0)
    val tentativas: StateFlow<Int> = _tentativas.asStateFlow()
    
    private val _jogoFinalizado = MutableStateFlow(false)
    val jogoFinalizado: StateFlow<Boolean> = _jogoFinalizado.asStateFlow()
    
    private var cartasViradas: List<Int> = emptyList()
    private var podeVirar = true
    
    init {
        iniciarJogo()
    }
    
    fun iniciarJogo() {
        val numeros = listOf(1, 2, 3, 4, 5, 6)
        
        val cartasPares = numeros.flatMap { numero ->
            listOf(
                CartaJogo(id = numero * 2 - 1, conteudo = numero.toString()),
                CartaJogo(id = numero * 2, conteudo = numero.toString())
            )
        }
        
        val cartasEmbaralhadas = cartasPares.shuffled()
        
        val cartasComIndices = cartasEmbaralhadas.mapIndexed { index, carta ->
            carta.copy(id = index)
        }
        
        _cartas.value = cartasComIndices
        _pontuacao.value = 0
        _tentativas.value = 0
        _jogoFinalizado.value = false
        cartasViradas = emptyList()
        podeVirar = true
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
                _tentativas.value = _tentativas.value + 1
                
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
                        _pontuacao.value = _pontuacao.value + 1
                        
                        val todasEncontradas = _cartas.value.all { it.encontrada }
                        if (todasEncontradas) {
                            _jogoFinalizado.value = true
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
    
    private fun salvarPartida() {
        viewModelScope.launch {
            val nomeUsuario = sessionManager.getNomeUsuario()
            if (nomeUsuario.isNotBlank()) {
                val partida = Partida(
                    usuarioId = 0L,
                    tema = temaAtual,
                    tentativas = _tentativas.value
                )
                partidaRepository.salvarPartida(partida)
            }
        }
    }
    
    fun getTema(): String = temaAtual
}
