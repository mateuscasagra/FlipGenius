package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.flipgenius.data.local.entities.PartidaTimeAttack
import com.example.flipgenius.ui.viewmodels.TimeAttackRankingViewModel

sealed interface RankingUiState {

    object Loading: RankingUiState

    data class Success(val partidas: List<PartidaTimeAttack>) : RankingUiState

    object Error: RankingUiState

}

@Composable
fun TimeAttackRankingScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: TimeAttackRankingViewModel = viewModel(
        factory = TimeAttackRankingViewModel.factory(context)
    )
    val uiState by viewModel.rankingUiState.collectAsState()
    val temaSelecionado by viewModel.temaSelecionado.collectAsState()
    val filtrarPorUsuario by viewModel.filtrarPorUsuario.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ranking Time Attack",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        when (val state = uiState) {
            is RankingUiState.Loading -> { Text(text = "Carregando...", color = Color.Gray) }
            is RankingUiState.Error -> { Text(text = "Erro ao carregar o ranking", color = Color.Red) }
            is RankingUiState.Success -> {
                val temas = listOf("Todos") + state.partidas.map { it.temaNome }.distinct()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    temas.forEach { tema ->
                        FilterChip(
                            selected = (tema == temaSelecionado),
                            onClick = { viewModel.selecionarTema(tema) },
                            label = { Text(tema) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF6200EE),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    FilterChip(
                        selected = filtrarPorUsuario,
                        onClick = { viewModel.alternarFiltroUsuario() },
                        label = { Text("Meu") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6200EE),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Button(onClick = { viewModel.clearHistory() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))) {
                    Text(text = "Limpar histórico")
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(state.partidas) { index, partida ->
                        RankingItem(index = index, partida = partida)
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingItem(index: Int, partida: PartidaTimeAttack) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "#${index + 1}", fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = partida.nomeJogador, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(text = "Tema: ${partida.temaNome}", color = Color.Gray)
            }
            Text(text = "${partida.pontuacao} pts", fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
        }
    }
}
