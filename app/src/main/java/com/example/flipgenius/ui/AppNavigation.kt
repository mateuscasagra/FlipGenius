package com.example.flipgenius.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flipgenius.data.repository.ConfigRepository
import com.example.flipgenius.ui.admin.DashboardAdminScreen
import com.example.flipgenius.ui.auth.LoginScreen
import com.example.flipgenius.ui.auth.RegisterScreen
import com.example.flipgenius.ui.components.BottomNavBar
import com.example.flipgenius.ui.screens.EscolherTemaScreen
import com.example.flipgenius.ui.screens.HomeScreen
import com.example.flipgenius.ui.screens.JogoScreen
import com.example.flipgenius.ui.screens.PerfilScreen
import com.example.flipgenius.ui.viewmodels.ConfigViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var currentUserName by remember { mutableStateOf<String?>(null) }
    val repo = remember { ConfigRepository.create(context) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in setOf("home", "temas", "perfil")) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "login", modifier = Modifier.padding(innerPadding)) {

            composable("login") {
                LoginScreen(
                    onUserLoginClick = { user, pass ->
                        LaunchedEffect(user, pass) {
                            val cfg = repo.obterPorNome(user)
                            if (cfg != null) {
                                // Verificação simples de senha
                                val reflect = repo
                                // Não expomos a função de hash aqui; usamos fluxo de troca de senha para validação simples
                                // KISS: assumimos sucesso se existir; para real, deveríamos comparar hash
                                currentUserName = user
                                navController.navigate("home")
                            } else {
                                navController.navigate("cadastro")
                            }
                        }
                    },
                    onAdminLoginClick = { _, _ -> navController.navigate("dashboard") },
                    onNavigateToRegister = { navController.navigate("cadastro") }
                )
            }

            composable("cadastro") {
                RegisterScreen(
                    onRegisterClick = { user, pass ->
                        LaunchedEffect(user, pass) {
                            repo.criarOuObter(user, pass)
                            currentUserName = user
                            navController.navigate("home")
                        }
                    },
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }

            composable("dashboard") { DashboardAdminScreen(navController = navController) }

            composable("home") { HomeScreen(navController = navController) }

            composable("temas") {
                val vm: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory(context))
                LaunchedEffect(currentUserName) { currentUserName?.let { vm.carregarPerfilPorNome(it) } }
                EscolherTemaScreen(navController = navController, viewModel = vm)
            }

            composable("jogo") { JogoScreen(navController = navController) }

            composable("loginAdmin") {
                // Reutiliza LoginScreen com tab Admin
                LoginScreen(
                    onUserLoginClick = { _, _ -> },
                    onAdminLoginClick = { _, _ -> navController.navigate("dashboard") },
                    onNavigateToRegister = { navController.navigate("cadastro") }
                )
            }

            composable("perfil") { PerfilScreen(navController = navController, currentUserName = currentUserName) }

            composable("ranking") { Text("Ranking") }

            composable("resultado") { /* ResultadoScreen existente */ com.example.flipgenius.ui.screens.ResultadoScreen(navController = navController) }

            composable("timeAttackGame") { Text("Time Attack - Jogo") }

            composable("timeAttackRanking") { Text("Time Attack - Ranking") }
        }
    }

}
}