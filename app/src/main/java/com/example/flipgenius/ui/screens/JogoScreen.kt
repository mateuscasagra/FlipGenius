package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.flipgenius.ui.components.CartaComponent
import com.example.flipgenius.ui.viewmodels.JogoViewModel
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.ViewModelFactory

@Composable
fun JogoScreen(
    viewModel: JogoViewModel,
    navController: NavHostController,
    configViewModel: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory())
) {
    val context = LocalContext.current
    val cartas by viewModel.cartas.collectAsState()
    val pontuacao by viewModel.pontuacao.collectAsState()
    val tentativas by viewModel.tentativas.collectAsState()
    val jogoFinalizado by viewModel.jogoFinalizado.collectAsState()
    val configState by configViewModel.uiState.collectAsState()
    val temaPreferido = configState.temaPreferido
    
    // Navegar para ResultadoScreen quando jogo finalizar
    LaunchedEffect(jogoFinalizado, tentativas) {
        if (jogoFinalizado && tentativas > 0) {
            val temaNome = when (temaPreferido) {
                "padrao" -> "Padrão"
                "animais" -> "Animais"
                "frutas" -> "Frutas"
                "esportes" -> "Esportes"
                "comidas" -> "Comidas"
                else -> temaPreferido.ifBlank { "Padrão" }
            }
            // Substituir caracteres problemáticos na URL
            val modoEncoded = "Clássico"
            val tentativasEncoded = tentativas.toString()
            val temaEncoded = temaNome.replace("/", "_").replace(" ", "_")
            try {
                navController.navigate("resultado/$modoEncoded/$tentativasEncoded/$temaEncoded")
            } catch (e: Exception) {
                // Ignorar erro de navegação duplicada
            }
        }
    }

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFF6200EE)

    Box(modifier = Modifier.fillMaxSize()) {
    val bgResId = remember {
        val r1 = context.resources.getIdentifier("baianomaldito", "drawable", context.packageName)
        if (r1 != 0) r1 else context.resources.getIdentifier("baiano_maldito", "drawable", context.packageName)
    }
        if (temaPreferido.lowercase() == "joao" && bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {}
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Header
        Text(
            text = "🎯 Jogo da Memória",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Card de Pontuação
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pares Encontrados",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$pontuacao / 6",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }

                // Barra de progresso visual
                LinearProgressIndicator(
                    progress = { pontuacao / 6f },
                    modifier = Modifier
                        .width(120.dp)
                        .height(8.dp),
                    color = primaryColor,
                    trackColor = Color.Gray.copy(alpha = 0.3f),
                )
            }
        }

        // Mensagem de vitória
        if (jogoFinalizado) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = primaryColor.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 Parabéns!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Você completou o jogo!",
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Grid de cartas 3x4
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(cartas) { carta ->
                CartaComponent(
                    carta = carta,
                    onClick = {
                        viewModel.virarCarta(carta.id)
                    }
                )
            }
        }

        // Botões de ação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Voltar")
            }

            Button(
                onClick = { viewModel.iniciarJogo() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Novo Jogo", fontWeight = FontWeight.Bold)
            }
        }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun JogoScreenPreview() {
    val context = LocalContext.current
    val jogoVm = remember { 
        com.example.flipgenius.ui.viewmodels.JogoViewModel(context, "padrao") 
    }
    JogoScreen(
        viewModel = jogoVm,
        navController = rememberNavController()
    )
}
