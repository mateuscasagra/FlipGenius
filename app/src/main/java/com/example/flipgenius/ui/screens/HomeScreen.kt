package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.ViewModelFactory

/**
 * Tela inicial com boas-vindas e navegação básica.
 */
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFF6200EE)
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "FlipGenius",
                    style = MaterialTheme.typography.headlineMedium,
                    color = primaryColor
                )

                Text(
                    text = if (uiState.nomeUsuario.isNotBlank()) "Olá, ${uiState.nomeUsuario}" else "Bem-vindo ao FlipGenius",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

                Button(onClick = { navController.navigate("jogo") }) {
                    Text("Começar Jogo")
                }
                Button(onClick = { navController.navigate("temas") }) {
                    Text("Escolher Tema (${uiState.temaPreferido})")
                }
                Button(onClick = { navController.navigate("perfil") }) {
                    Text("Meu Perfil")
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}
