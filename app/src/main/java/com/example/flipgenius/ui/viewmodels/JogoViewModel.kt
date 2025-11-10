package com.example.flipgenius.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.model.CartaJogo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JogoViewModel : ViewModel() {
    
    private val _cartas = MutableStateFlow<List<CartaJogo>>(emptyList())
    val cartas: StateFlow<List<CartaJogo>> = _cartas.asStateFlow()
    
    private val _pontuacao = MutableStateFlow(0)
    val pontuacao: StateFlow<Int> = _pontuacao.asStateFlow()
    
    private val _jogoFinalizado = MutableStateFlow(false)
    val jogoFinalizado: StateFlow<Boolean> = _jogoFinalizado.asStateFlow()
    
    private var cartasViradas: List<Int> = emptyList()
    private var podeVirar = true
    
    init {
        iniciarJogo()
    }
    
    fun iniciarJogo() {
        // Grid 3x4 = 12 cartas = 6 pares
        val numeros = listOf(1, 2, 3, 4, 5, 6)
        
        // Criar pares de cartas usando flatMap para duplicar cada número
        val cartasPares = numeros.flatMap { numero ->
            listOf(
                CartaJogo(id = numero * 2 - 1, numero = numero),
                CartaJogo(id = numero * 2, numero = numero)
            )
        }
        
        // Embaralhar as cartas usando shuffled()
        val cartasEmbaralhadas = cartasPares.shuffled()
        
        // Adicionar índices únicos usando mapIndexed para garantir IDs únicos
        val cartasComIndices = cartasEmbaralhadas.mapIndexed { index, carta ->
            carta.copy(id = index)
        }
        
        _cartas.value = cartasComIndices
        _pontuacao.value = 0
        _jogoFinalizado.value = false
        cartasViradas = emptyList()
        podeVirar = true
    }
    
    fun virarCarta(cartaId: Int) {
        if (!podeVirar) return
        
        val carta = _cartas.value.find { it.id == cartaId }
        if (carta == null || carta.virada || carta.encontrada) return
        
        viewModelScope.launch {
            // Atualizar a carta para virada
            val cartasAtualizadas = _cartas.value.map { c ->
                if (c.id == cartaId) c.copy(virada = true) else c
            }
            _cartas.value = cartasAtualizadas
            
            // Adicionar à lista de cartas viradas
            cartasViradas = cartasViradas + cartaId
            
            // Se duas cartas estão viradas, verificar se são iguais
            if (cartasViradas.size == 2) {
                podeVirar = false
                delay(1000) // Aguardar 1 segundo para o jogador ver as cartas
                
                val primeiraCarta = _cartas.value.find { it.id == cartasViradas[0] }
                val segundaCarta = _cartas.value.find { it.id == cartasViradas[1] }
                
                if (primeiraCarta != null && segundaCarta != null) {
                    if (primeiraCarta.numero == segundaCarta.numero) {
                        // Cartas iguais - marcar como encontradas
                        val cartasAtualizadas = _cartas.value.map { c ->
                            if (c.id == cartasViradas[0] || c.id == cartasViradas[1]) {
                                c.copy(encontrada = true, virada = true)
                            } else {
                                c
                            }
                        }
                        _cartas.value = cartasAtualizadas
                        _pontuacao.value = _pontuacao.value + 1
                        
                        // Verificar se o jogo terminou
                        val todasEncontradas = _cartas.value.all { it.encontrada }
                        if (todasEncontradas) {
                            _jogoFinalizado.value = true
                        }
                    } else {
                        // Cartas diferentes - desvirar
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
}

