package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.flipgenius.ui.components.CartaComponent
import com.example.flipgenius.ui.viewmodels.TimeAttackViewModel
import com.example.flipgenius.ui.viewmodels.TimeAttackUiState
import com.example.flipgenius.ui.viewmodels.Resultado
import com.example.flipgenius.ui.utils.SessionManager
import com.example.flipgenius.data.remote.FirebaseService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.ViewModelFactory

@Composable
fun TimeAttackGameScreen(
    navController: NavHostController
) {
    val viewModel: TimeAttackViewModel = viewModel(
        factory = TimeAttackViewModel.factory(androidx.compose.ui.platform.LocalContext.current)
    )

    val uiState by viewModel.uiState.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFF6200EE)

    val context = androidx.compose.ui.platform.LocalContext.current
    val session = remember { SessionManager(context) }
    val defaultName = session.getNomeUsuario().ifBlank { "Jogador" }

    var temaNome by remember { mutableStateOf("padrao") }
    var temasDisponiveis by remember { mutableStateOf(listOf("padrao")) }

    val configVm: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory())
    val configUi by configVm.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        try {
            val service = FirebaseService()
            val remotos = service.listarTemas()
            if (remotos.isNotEmpty()) {
                temasDisponiveis = remotos.map { it.id }
                val preferido = configUi.temaPreferido.ifBlank { "animais" }
                temaNome = if (temasDisponiveis.contains(preferido)) preferido else temasDisponiveis.firstOrNull() ?: "padrao"
            }
        } catch (_: Exception) { }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val bgResId = remember {
            val r1 = context.resources.getIdentifier("baianomaldito", "drawable", context.packageName)
            if (r1 != 0) r1 else context.resources.getIdentifier("baiano_maldito", "drawable", context.packageName)
        }
        if (temaNome.lowercase() == "joao" && bgResId != 0) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {}
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        Text(
            text = "Time Attack",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        val tempoAtual = when (val s = uiState) {
            is TimeAttackUiState.Playing -> s.tempoRestante
            is TimeAttackUiState.Finished -> s.tempoRestante
            else -> 60
        }
        Text(text = "Tempo: ${tempoAtual}s", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

        if (uiState is TimeAttackUiState.Idle) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Jogador: $defaultName", color = Color.White, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = temaNome,
                        onValueChange = { temaNome = it },
                        label = { Text("Tema (id)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            cursorColor = primaryColor,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )
                    Text(text = "Disponíveis: ${temasDisponiveis.joinToString(", ")}", color = Color.Gray)
                }
            }
            Button(
                onClick = {
                    viewModel.iniciarJogo("", temaNome)
                }
            , colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) { Text("Iniciar jogo") }
        } else if (uiState is TimeAttackUiState.Playing) {
            // Grid simples usando linhas com 3 cartas por linha
            val linhas = (uiState as TimeAttackUiState.Playing).cartas.chunked(3)
            linhas.forEach { linha ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    linha.forEach { carta ->
                        CartaComponent(
                            carta = carta,
                            onClick = { viewModel.virarCarta(carta.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - linha.size) {
                        // Preenche espaço se a última linha tiver menos de 3 cartas
                        androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f)) {}
                    }
                }
            }
        } else if (uiState is TimeAttackUiState.Finished) {
            // Resultado simples e ações
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val finished = uiState as TimeAttackUiState.Finished
                    val titulo = if (finished.resultado == Resultado.Vitoria) "Vitória!" else "Tempo esgotado!"
                    Text(text = titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Pontuação: ${finished.pontuacaoFinal} pontos", color = Color.Gray)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { viewModel.iniciarJogo("", temaNome) }, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) {
                            Text("Jogar novamente")
                        }
                        Button(onClick = { navController.navigate("timeAttackRanking") }, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) {
                            Text("Ver ranking")
                        }
                    }
                }
            }
        }
        }
    }
}
