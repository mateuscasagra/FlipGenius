package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.flipgenius.data.local.entities.Partida
import com.example.flipgenius.ui.viewmodels.RankingViewModel
import com.example.flipgenius.ui.viewmodels.RankingClassicUiState

@Composable
fun RankingScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: RankingViewModel = viewModel(factory = RankingViewModel.factory(context))
    val uiState by viewModel.rankingUiState.collectAsState()
    val temaSelecionado by viewModel.temaSelecionado.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFF6200EE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ranking Clássico",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        when (val state = uiState) {
            is RankingClassicUiState.Loading -> {
                Text(text = "Carregando...", color = Color.Gray)
            }
            is RankingClassicUiState.Error -> {
                Text(text = "Erro ao carregar o ranking", color = Color.Red)
            }
            is RankingClassicUiState.Success -> {
                val temas = listOf("Todos") + state.partidas.map { it.tema }.distinct()

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
                                selectedContainerColor = primaryColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Button(
                    onClick = { viewModel.clearHistory() },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) { Text(text = "Limpar histórico", color = Color.White) }

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

                if (state.partidas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text(text = "Nenhuma partida encontrada", color = Color.Gray) }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(state.partidas) { index, partida ->
                            RankingItem(partida = partida, posicao = index + 1, primaryColor = primaryColor, cardColor = cardColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingItem(
    partida: Partida,
    posicao: Int,
    primaryColor: Color,
    cardColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${posicao}º", color = primaryColor, fontWeight = FontWeight.Bold)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Usuário ${partida.usuarioId}", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(text = partida.tema, color = Color.Gray)
            }
            Text(text = "${partida.tentativas} tent.", color = primaryColor, fontWeight = FontWeight.Bold)
        }
    }
}
