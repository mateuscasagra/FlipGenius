package com.example.flipgenius.ui.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flipgenius.ui.theme.FlipGeniusTheme
import com.example.flipgenius.ui.theme.Purple40
import com.example.flipgenius.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onUserLoginClick: (String, String) -> Unit = { _, _ -> },
    onAdminLoginClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToRegister: () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Usuário", "Admin")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TabRow(selectedTabIndex = uiState.loginSelectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.loginSelectedTabIndex == index,
                        onClick = {
                            viewModel.onLoginTabChange(index)
                        },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val title = if (uiState.loginSelectedTabIndex == 0) "Login do Jogador" else "Acesso Admin"
            val userLabel = if (uiState.loginSelectedTabIndex == 0) "Nome de Usuário" else "Usuário Admin"

            Text(title,
                style = MaterialTheme.typography.headlineMedium,
                color = Purple40)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.loginUser,
                onValueChange = { viewModel.onLoginUserChange(it) },
                label = { Text(userLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.loginPassword,
                onValueChange = { viewModel.onLoginPasswordChange(it) },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = {
                    if (uiState.loginSelectedTabIndex == 0) {
                        onUserLoginClick(uiState.loginUser, uiState.loginPassword)
                    } else {
                        onAdminLoginClick(uiState.loginUser, uiState.loginPassword)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple40,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            if (uiState.loginSelectedTabIndex == 0) {
                TextButton(onClick = onNavigateToRegister) {
                    Text("Não tem conta? Cadastre-se")
                }
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {

    FlipGeniusTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        LoginScreen(
            viewModel = AuthViewModel(),
            onUserLoginClick = {_,_ -> },
            onAdminLoginClick = {_,_ -> },
            onNavigateToRegister = {}
        )
    }
}