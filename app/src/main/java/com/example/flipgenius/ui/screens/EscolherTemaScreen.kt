package com.example.flipgenius.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecione seu tema", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.padding(8.dp))
            temas.forEach { tema ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            viewModel.onAtualizarTema(tema)
                            navController.popBackStack()
                        }
                ) {
                    Text(
                        text = if (uiState.temaPreferido == tema) "✓ $tema" else tema,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}