package com.example.flipgenius.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flipgenius.ui.theme.FlipGeniusTheme

@Composable
fun LoginScreen(
    onUserLoginClick: (String, String) -> Unit = { _, _ -> }, // O que fazer ao clicar em "Entrar" (Usuário)
    onAdminLoginClick: (String, String) -> Unit = { _, _ -> }, // O que fazer ao clicar em "Entrar" (Admin)
    onNavigateToRegister: () -> Unit = {} // O que fazer ao clicar em "Cadastre-se"
) {

    // Controla qual aba está selecionada (0 = Usuário, 1 = Admin)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Usuário", "Admin")

    var emailOrUser by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        // Limpa os campos quando trocar de aba
                        emailOrUser = ""
                        password = ""
                    },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val title = if (selectedTabIndex == 0) "Login do Jogador" else "Acesso Admin"
        val emailLabel = if (selectedTabIndex == 0) "Email" else "Usuário Admin"

        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = emailOrUser,
            onValueChange = { emailOrUser = it },
            label = { Text(emailLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation() // Esconde a senha
        )
        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {
                if (selectedTabIndex == 0) {
                    onUserLoginClick(emailOrUser, password)
                } else {
                    onAdminLoginClick(emailOrUser, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        // Botão de Cadastro só aparece na aba "Usuário
        if (selectedTabIndex == 0) {
            TextButton(onClick = onNavigateToRegister) {
                Text("Não tem conta? Cadastre-se")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {

    FlipGeniusTheme {
        LoginScreen()
    }
}