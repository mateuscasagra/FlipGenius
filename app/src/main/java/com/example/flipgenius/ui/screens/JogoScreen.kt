package com.example.flipgenius.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    viewModel: JogoViewModel = viewModel(),
    navController: NavHostController
) {
    val cartas by viewModel.cartas.collectAsState()
    val pontuacao by viewModel.pontuacao.collectAsState()
    val jogoFinalizado by viewModel.jogoFinalizado.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título e Pontuação
        Text(
            text = "Jogo da Memória",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Pontuação: $pontuacao / 6",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Novo Jogo")
        }
    }
}

