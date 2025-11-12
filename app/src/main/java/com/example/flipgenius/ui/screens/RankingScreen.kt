package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

data class ItemRanking(
    val posicao: Int,
    val nomeJogador: String,
    val resultado: String,
    val tema: String,
    val modoJogo: String
)

@Composable
fun RankingScreen(
    navController: NavHostController,
    partidas: List<ItemRanking> = emptyList()
) {
    var modoSelecionado by remember { mutableStateOf("Todos") }
    var temaSelecionado by remember { mutableStateOf("Todos") }

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFF6200EE)

    val partidasFiltradas = partidas.filter {
        (modoSelecionado == "Todos" || it.modoJogo == modoSelecionado) &&
        (temaSelecionado == "Todos" || it.tema == temaSelecionado)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "Rankings",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = modoSelecionado == "Todos",
                onClick = { modoSelecionado = "Todos" },
                label = { Text("Todos") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = modoSelecionado == "Clássico",
                onClick = { modoSelecionado = "Clássico" },
                label = { Text("Clássico") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = modoSelecionado == "Time Attack",
                onClick = { modoSelecionado = "Time Attack" },
                label = { Text("Time Attack") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor,
                    selectedLabelColor = Color.White
                )
            )
        }

        if (partidasFiltradas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma partida encontrada",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(partidasFiltradas) { index, item ->
                    RankingItem(
                        item = item,
                        primaryColor = primaryColor,
                        cardColor = cardColor
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingItem(
    item: ItemRanking,
    primaryColor: Color,
    cardColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (item.posicao <= 3) primaryColor else Color.Gray
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${item.posicao}º",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.nomeJogador,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${item.tema} • ${item.modoJogo}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Text(
                text = item.resultado,
                color = primaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun RankingScreenPreview() {
    val partidasFake = listOf(
        ItemRanking(1, "João", "12 tent.", "Animais", "Clássico"),
        ItemRanking(2, "Maria", "15 tent.", "Frutas", "Clássico"),
        ItemRanking(3, "Pedro", "18 tent.", "Animais", "Clássico"),
        ItemRanking(4, "Ana", "1500 pts", "Esportes", "Time Attack"),
        ItemRanking(5, "Carlos", "1200 pts", "Frutas", "Time Attack")
    )

    RankingScreen(
        navController = rememberNavController(),
        partidas = partidasFake
    )
}
