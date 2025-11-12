package com.example.flipgenius.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipgenius.data.repository.PartidaRepository
import com.example.flipgenius.data.repository.TemaRepository
import com.example.flipgenius.data.remote.Tema
import com.example.flipgenius.model.CartaJogo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JogoViewModel(
    private val temaRepository: TemaRepository,
    private val partidaRepository: PartidaRepository,
    private val usuarioId: Long?,
    private val temaId: String? = null
) : ViewModel() {
    
    private val _cartas = MutableStateFlow<List<CartaJogo>>(emptyList())
    val cartas: StateFlow<List<CartaJogo>> = _cartas.asStateFlow()
    
    private val _pontuacao = MutableStateFlow(0)
    val pontuacao: StateFlow<Int> = _pontuacao.asStateFlow()
    
    private val _tentativas = MutableStateFlow(0)
    val tentativas: StateFlow<Int> = _tentativas.asStateFlow()
    
    private val _jogoFinalizado = MutableStateFlow(false)
    val jogoFinalizado: StateFlow<Boolean> = _jogoFinalizado.asStateFlow()
    
    private val _tema = MutableStateFlow<Tema?>(null)
    val tema: StateFlow<Tema?> = _tema.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var cartasViradas: List<Int> = emptyList()
    private var podeVirar = true
    
    init {
        carregarTemaEIniciarJogo()
    }
    
    private fun carregarTemaEIniciarJogo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val temaSelecionado = if (temaId != null) {
                    temaRepository.buscarTemaPorId(temaId)
                } else {
                    // Se não tem temaId, buscar o primeiro tema disponível
                    temaRepository.buscarTodosTemas().firstOrNull()
                }
                
                _tema.value = temaSelecionado
                
                if (temaSelecionado != null) {
                    iniciarJogo(temaSelecionado)
                } else {
                    // Fallback: usar emojis fake para teste
                    val temaFake = Tema(
                        id = "fake",
                        nome = "Teste",
                        emojis = "🐶🐱🐭🐹🦊🐻"
                    )
                    _tema.value = temaFake
                    iniciarJogo(temaFake)
                }
            } catch (e: Exception) {
                // Em caso de erro, usar emojis fake
                val temaFake = Tema(
                    id = "fake",
                    nome = "Teste",
                    emojis = "🐶🐱🐭🐹🦊🐻"
                )
                _tema.value = temaFake
                iniciarJogo(temaFake)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun iniciarJogo(tema: Tema? = null) {
        val temaParaUsar = tema ?: _tema.value
        if (temaParaUsar == null) {
            carregarTemaEIniciarJogo()
            return
        }
        
        // Grid 3x4 = 12 cartas = 6 pares
        // Pegar os primeiros 6 emojis do tema
        val emojis = temaParaUsar.emojis.chunked(1).take(6)
        
        if (emojis.size < 6) {
            // Se não tem emojis suficientes, duplicar os que tem
            val emojisCompletos = mutableListOf<String>()
            repeat(6) { index ->
                emojisCompletos.add(emojis[index % emojis.size])
            }
            criarCartas(emojisCompletos)
        } else {
            criarCartas(emojis)
        }
    }
    
    private fun criarCartas(emojis: List<String>) {
        // Criar pares de cartas usando flatMap para duplicar cada emoji
        val cartasPares = emojis.mapIndexed { index, emoji ->
            listOf(
                CartaJogo(id = index * 2, emoji = emoji),
                CartaJogo(id = index * 2 + 1, emoji = emoji)
            )
        }.flatMap { it }
        
        // Embaralhar as cartas usando shuffled()
        val cartasEmbaralhadas = cartasPares.shuffled()
        
        // Adicionar índices únicos usando mapIndexed para garantir IDs únicos
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
                _tentativas.value = _tentativas.value + 1
                delay(1000) // Aguardar 1 segundo para o jogador ver as cartas
                
                val primeiraCarta = _cartas.value.find { it.id == cartasViradas[0] }
                val segundaCarta = _cartas.value.find { it.id == cartasViradas[1] }
                
                if (primeiraCarta != null && segundaCarta != null) {
                    if (primeiraCarta.emoji == segundaCarta.emoji) {
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
                            salvarPartida()
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
    
    private fun salvarPartida() {
        if (usuarioId == null) return
        
        viewModelScope.launch {
            try {
                val temaAtual = _tema.value
                val partida = com.example.flipgenius.data.local.entities.Partida(
                    usuarioId = usuarioId,
                    temaId = temaAtual?.id ?: "",
                    temaNome = temaAtual?.nome ?: "Desconhecido",
                    pontuacao = _pontuacao.value,
                    tentativas = _tentativas.value
                )
                partidaRepository.salvarPartida(partida)
            } catch (e: Exception) {
                // Erro ao salvar partida - silencioso
            }
        }
    }
}
