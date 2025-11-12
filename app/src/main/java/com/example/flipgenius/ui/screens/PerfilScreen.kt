package com.example.flipgenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.flipgenius.ui.ViewModelFactory
import com.example.flipgenius.ui.viewmodels.ConfigViewModel

/**
 * Tela de Perfil: exibe dados, edição de nome, troca de senha, logout e deletar conta.
 */
@Composable
fun PerfilScreen(
    navController: NavHostController,
    currentUserName: String? = null
) {
    val viewModel: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory())
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(currentUserName) {
        currentUserName?.let {
            viewModel.carregarPerfilPorNome(it)
            viewModel.observarPerfil(it)
        }
    }

    var novoNome by remember { mutableStateOf("") }
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarNovaSenha by remember { mutableStateOf("") }

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
                    text = "Meu Perfil",
                    style = MaterialTheme.typography.headlineMedium,
                    color = primaryColor
                )

                Spacer(modifier = Modifier.padding(8.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.padding(8.dp))

                // Dados do usuário
                Text("Usuário: ${uiState.nomeUsuario}", color = textColor)
                Text("Tema: ${uiState.temaPreferido}", color = textColor)
                Spacer(modifier = Modifier.padding(8.dp))

                // Edição de nome
                Text("Editar nome", style = MaterialTheme.typography.titleMedium, color = textColor)
                OutlinedTextField(
                    value = novoNome,
                    onValueChange = { novoNome = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text(uiState.nomeUsuario) }
                )
                Button(onClick = { if (novoNome.isNotBlank()) viewModel.onAtualizarNome(novoNome) }) {
                    Text("Salvar nome")
                }
                Spacer(modifier = Modifier.padding(8.dp))

                // Troca de senha
                Text("Trocar senha", style = MaterialTheme.typography.titleMedium, color = textColor)
                OutlinedTextField(
                    value = senhaAtual,
                    onValueChange = { senhaAtual = it },
                    label = { Text("Senha atual") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = novaSenha,
                    onValueChange = { novaSenha = it },
                    label = { Text("Nova senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = confirmarNovaSenha,
                    onValueChange = { confirmarNovaSenha = it },
                    label = { Text("Confirmar nova senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(onClick = {
                    if (novaSenha == confirmarNovaSenha && novaSenha.isNotBlank()) {
                        viewModel.onTrocarSenha(senhaAtual, novaSenha)
                    }
                }) { Text("Atualizar senha") }

                Spacer(modifier = Modifier.padding(8.dp))

                // Logout
                Button(onClick = { navController.navigate("login") }) { Text("Logout") }

                Spacer(modifier = Modifier.padding(8.dp))

                // Deletar conta
                TextButton(onClick = {
                    viewModel.onDeletarConta()
                    navController.navigate("login")
                }) { Text("Deletar conta") }

                Spacer(modifier = Modifier.padding(8.dp))

                // Mensagens
                uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                uiState.successMessage?.let { Text(it, color = primaryColor) }
            }
        }
    }
}