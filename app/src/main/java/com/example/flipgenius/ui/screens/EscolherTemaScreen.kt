package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.ViewModelFactory
import com.example.flipgenius.data.remote.FirebaseService

data class TemaInfo(
    val id: String,
    val nome: String,
    val emoji: String,
    val previewEmojis: List<String>
)

/**
 * Tela simples para seleção de tema preferido.
 */
@Composable
fun EscolherTemaScreen(
    navController: NavHostController,
    viewModel: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    var temas by remember {
        mutableStateOf(
            listOf(
                TemaInfo("padrao", "Padrão", "🎮", listOf("🎮", "🎯", "🎲", "🎪"))
            )
        )
    }
    LaunchedEffect(Unit) {
        try {
            val service = FirebaseService()
            val remotos = service.listarTemas()
            if (remotos.isNotEmpty()) {
                temas = remotos.map { t ->
                    val preview = if (t.emojis.isNotEmpty()) t.emojis.take(4) else listOf("✨")
                    TemaInfo(
                        id = t.id,
                        nome = t.nome.replaceFirstChar { it.uppercase() },
                        emoji = preview.firstOrNull() ?: "🎨",
                        previewEmojis = preview
                    )
                }
            }
        } catch (_: Exception) { }
    }

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFF6200EE)
    val textColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Escolher Tema",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Selecione o tema para suas partidas",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Grid de Temas
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(temas) { tema ->
                TemaCard(
                    tema = tema,
                    isSelected = uiState.temaPreferido == tema.id,
                    primaryColor = primaryColor,
                    cardColor = cardColor,
                    textColor = textColor,
                    onClick = {
                        viewModel.onAtualizarTema(tema.id)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botão Voltar
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = primaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Voltar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TemaCard(
    tema: TemaInfo,
    isSelected: Boolean,
    primaryColor: Color,
    cardColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) primaryColor.copy(alpha = 0.2f) else cardColor
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, primaryColor)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = tema.emoji,
                fontSize = 48.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tema.nome,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) primaryColor else textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Preview dos emojis
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tema.previewEmojis.take(4).forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 12.sp
                    )
                }
            }

            if (isSelected) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Selecionado",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun EscolherTemaScreenPreview() {
    EscolherTemaScreen(navController = rememberNavController())
}
