package com.example.flipgenius.ui.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flipgenius.ui.theme.FlipGeniusTheme
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

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFF6200EE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "FlipGenius",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )

                TabRow(
                    selectedTabIndex = uiState.loginSelectedTabIndex,
                    containerColor = cardColor,
                    contentColor = primaryColor
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.loginSelectedTabIndex == index,
                            onClick = { viewModel.onLoginTabChange(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (uiState.loginSelectedTabIndex == index)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val title = if (uiState.loginSelectedTabIndex == 0) "Login do Jogador" else "Acesso Admin"
                val userLabel = if (uiState.loginSelectedTabIndex == 0) "Nome de Usuário" else "Usuário Admin"

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                OutlinedTextField(
                    value = uiState.loginUser,
                    onValueChange = { viewModel.onLoginUserChange(it) },
                    label = { Text(userLabel) },
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

                OutlinedTextField(
                    value = uiState.loginPassword,
                    onValueChange = { viewModel.onLoginPasswordChange(it) },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor,
                        cursorColor = primaryColor,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (uiState.loginSelectedTabIndex == 0) {
                            onUserLoginClick(uiState.loginUser, uiState.loginPassword)
                        } else {
                            onAdminLoginClick(uiState.loginUser, uiState.loginPassword)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Entrar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.loginSelectedTabIndex == 0) {
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "Não tem conta? Cadastre-se",
                            color = primaryColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, backgroundColor = 0xFF121212)
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
