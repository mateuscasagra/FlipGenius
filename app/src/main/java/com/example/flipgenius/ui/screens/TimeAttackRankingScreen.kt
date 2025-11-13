package com.example.flipgenius.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ranking Time Attack",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        when (val state = uiState) {
            is RankingUiState.Loading -> {
                Text(text = "Carregando...")
            }
            is RankingUiState.Error -> {
                Text(text = "Erro ao carregar o ranking")
            }
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
                            label = { Text(tema) }
                        )
                    }
                    FilterChip(
                        selected = filtrarPorUsuario,
                        onClick = { viewModel.alternarFiltroUsuario() },
                        label = { Text("Meu") }
                    )
                }

                Button(onClick = { viewModel.clearHistory() }) {
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
        colors = CardDefaults.cardColors(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "#${index + 1}", fontWeight = FontWeight.Bold)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = partida.nomeJogador, fontWeight = FontWeight.SemiBold)
                Text(text = "Tema: ${partida.temaNome}")
            }
            Text(text = "${partida.pontuacao} pts", fontWeight = FontWeight.Bold)
        }
    }
}