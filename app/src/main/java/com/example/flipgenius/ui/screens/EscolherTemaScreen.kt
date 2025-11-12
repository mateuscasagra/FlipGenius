package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.flipgenius.ui.viewmodels.ConfigViewModel

/**
 * Tela simples para seleção de tema preferido.
 */
@Composable
fun EscolherTemaScreen(
    navController: NavHostController,
    viewModel: ConfigViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val temas = listOf("padrao", "animais", "frutas", "esportes")

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFF6200EE)
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Selecione seu tema",
                    style = MaterialTheme.typography.headlineSmall,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.padding(8.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.padding(8.dp))

                temas.forEach { tema ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                viewModel.onAtualizarTema(tema)
                                navController.popBackStack()
                            },
                        colors = CardDefaults.cardColors(containerColor = cardColor.copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = if (uiState.temaPreferido == tema) "✓ $tema" else tema,
                            modifier = Modifier.padding(16.dp),
                            color = if (uiState.temaPreferido == tema) primaryColor else textColor
                        )
                    }
                }
            }
        }
    }
}