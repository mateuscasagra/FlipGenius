package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.ViewModelFactory

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "FlipGenius",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

        Text(
            text = if (uiState.nomeUsuario.isNotBlank())
                "Olá, ${uiState.nomeUsuario}! 👋"
            else
                "Bem-vindo ao FlipGenius! 🎮",
            fontSize = 18.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        GameModeCard(
            title = "🎯 Modo Clássico",
            description = "Encontre todos os pares no menor número de tentativas",
            primaryColor = primaryColor,
            cardColor = cardColor,
            onClick = { navController.navigate("jogo") }
        )

        GameModeCard(
            title = "⚡ Time Attack",
            description = "60 segundos para encontrar o máximo de pares possível",
            primaryColor = primaryColor,
            cardColor = cardColor,
            onClick = { navController.navigate("timeAttackGame") }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("temas") },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Tema Selecionado",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.temaPreferido.replaceFirstChar { it.uppercase() },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Text(
                    text = "🎨",
                    fontSize = 32.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { navController.navigate("ranking") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📊 Ranking")
            }

            OutlinedButton(
                onClick = { navController.navigate("perfil") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("👤 Perfil")
            }
        }
    }
}

@Composable
private fun GameModeCard(
    title: String,
    description: String,
    primaryColor: Color,
    cardColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Jogar Agora",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}
