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
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onRegisterClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Criar Nova Conta",
                style = MaterialTheme.typography.headlineMedium,
                color = Purple40)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.registerUser,
                onValueChange = { viewModel.onRegisterUserChange(it) },
                label = { Text("Nome de usuário") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.registerPassword,
                onValueChange = { viewModel.onRegisterPasswordChange(it) },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.registerConfirmPassword,
                onValueChange = {viewModel.onRegisterConfirmPasswordChange(it) },
                label = { Text("Confirmar Senha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onRegisterClick(uiState.registerUser, uiState.registerPassword)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple40,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Conta")
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("Já tem conta? Faça Login")
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    FlipGeniusTheme (
        darkTheme = true,
        dynamicColor = false
    ){
        RegisterScreen(
            viewModel = AuthViewModel(),
            onRegisterClick = { _,_ -> },
            onNavigateToLogin = {}
        )
    }
}