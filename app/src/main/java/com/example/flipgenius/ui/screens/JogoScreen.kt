package com.example.flipgenius.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.flipgenius.ui.components.CartaComponent
import com.example.flipgenius.ui.viewmodels.JogoViewModel

@Composable
fun JogoScreen(
    viewModel: JogoViewModel,
    navController: NavHostController,
    usuarioId: Long? = null,
    temaId: String? = null
) {
    val cartas by viewModel.cartas.collectAsState()
    val pontuacao by viewModel.pontuacao.collectAsState()
    val tentativas by viewModel.tentativas.collectAsState()
    val jogoFinalizado by viewModel.jogoFinalizado.collectAsState()
    val tema by viewModel.tema.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Navegar para ResultadoScreen quando jogo finalizar
    LaunchedEffect(jogoFinalizado) {
        if (jogoFinalizado) {
            // Aguardar um pouco antes de navegar
            kotlinx.coroutines.delay(1500)
            // Navegar para resultado com os parâmetros corretos
            // ResultadoScreen espera: modoJogo, resultado, tema
            navController.navigate("resultado?tentativas=$tentativas&tema=${tema?.nome ?: "Desconhecido"}") {
                // Limpar o back stack para evitar voltar ao jogo
                popUpTo("jogo") { inclusive = false }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título e Informações
        Text(
            text = tema?.nome ?: "Jogo da Memória",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Pontuação: $pontuacao / 6",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Tentativas: $tentativas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator()
        }
        
        // Mensagem de vitória
        if (jogoFinalizado) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Parabéns! Você completou o jogo!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Navegando para resultados...",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        // Grid de cartas 3x4
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(cartas) { carta ->
                CartaComponent(
                    carta = carta,
                    onClick = {
                        viewModel.virarCarta(carta.id)
                    }
                )
            }
        }
        
        // Botão para reiniciar
        Button(
            onClick = { viewModel.iniciarJogo() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !jogoFinalizado
        ) {
            Text("Novo Jogo")
        }
    }
}

