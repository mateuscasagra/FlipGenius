package com.example.flipgenius.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.flipgenius.ui.components.CartaComponent
import com.example.flipgenius.ui.viewmodels.TimeAttackViewModel
import com.example.flipgenius.ui.viewmodels.TimeAttackUiState
import com.example.flipgenius.ui.viewmodels.Resultado

@Composable
fun TimeAttackGameScreen(
    navController: NavHostController
) {
    val viewModel: TimeAttackViewModel = viewModel(
        factory = TimeAttackViewModel.factory(androidx.compose.ui.platform.LocalContext.current)
    )

    val uiState by viewModel.uiState.collectAsState()

    var nomeJogador by remember { mutableStateOf("") }
    var temaNome by remember { mutableStateOf("Animais") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Time Attack",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        val tempoAtual = when (val s = uiState) {
            is TimeAttackUiState.Playing -> s.tempoRestante
            is TimeAttackUiState.Finished -> s.tempoRestante
            else -> 60
        }
        Text(text = "Tempo: ${tempoAtual}s", style = MaterialTheme.typography.titleMedium)

        if (uiState is TimeAttackUiState.Idle) {
            // Form de inicialização simples
            OutlinedTextField(
                value = nomeJogador,
                onValueChange = { nomeJogador = it },
                label = { Text("Nome do jogador") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = temaNome,
                onValueChange = { temaNome = it },
                label = { Text("Tema (ex.: Animais)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val nome = if (nomeJogador.isBlank()) "Jogador" else nomeJogador
                    viewModel.iniciarJogo(nome, temaNome)
                }
            ) { Text("Iniciar jogo") }
        } else if (uiState is TimeAttackUiState.Playing) {
            // Grid simples usando linhas com 3 cartas por linha
            val linhas = (uiState as TimeAttackUiState.Playing).cartas.chunked(3)
            linhas.forEach { linha ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    linha.forEach { carta ->
                        CartaComponent(
                            carta = carta,
                            onClick = { viewModel.virarCarta(carta.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - linha.size) {
                        // Preenche espaço se a última linha tiver menos de 3 cartas
                        androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f)) {}
                    }
                }
            }
        } else if (uiState is TimeAttackUiState.Finished) {
            // Resultado simples e ações
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val finished = uiState as TimeAttackUiState.Finished
                    val titulo = if (finished.resultado == Resultado.Vitoria) "Vitória!" else "Tempo esgotado!"
                    Text(text = titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Pontuação: ${finished.pontuacaoFinal} pontos")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { viewModel.iniciarJogo(nomeJogador.ifBlank { "Jogador" }, temaNome) }) {
                            Text("Jogar novamente")
                        }
                        Button(onClick = { navController.navigate("timeAttackRanking") }) {
                            Text("Ver ranking")
                        }
                    }
                }
            }
        }
    }
}