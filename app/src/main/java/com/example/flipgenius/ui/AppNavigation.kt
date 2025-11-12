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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
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
import com.example.flipgenius.ui.screens.RankingScreen
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.ViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var currentUserName by remember { mutableStateOf<String?>(null) }
    val repo = remember { ConfigRepository.create() }
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    

    Scaffold(
        bottomBar = {
            if (currentRoute in setOf("home", "temas", "perfil")) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "timeAttackGame", modifier = Modifier.padding(innerPadding)) {

            composable("login") {
                LoginScreen(
                    onUserLoginClick = { user, pass ->
                        scope.launch {
                            val ok = repo.validarLogin(user, pass)
                            if (ok) {
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
                        scope.launch {
                            repo.criarOuObter(user, pass)
                            currentUserName = user
                            navController.navigate("home")
                        }
                    },
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }

            composable("dashboard") { DashboardAdminScreen() }

            composable("home") {
                val vm: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory())
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        vm.carregarPerfilPorNome(it)
                        vm.observarPerfil(it)
                    }
                }
                HomeScreen(navController = navController, viewModel = vm)
            }

            composable("temas") {
                val vm: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory())
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        vm.carregarPerfilPorNome(it)
                        vm.observarPerfil(it)
                    }
                }
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

            composable("ranking") { RankingScreen(navController = navController) }

            composable("resultado") { /* ResultadoScreen existente */ com.example.flipgenius.ui.screens.ResultadoScreen(navController = navController) }

            composable("timeAttackGame") { com.example.flipgenius.ui.screens.TimeAttackGameScreen(navController = navController) }

            composable("timeAttackRanking") { com.example.flipgenius.ui.screens.TimeAttackRankingScreen(navController = navController) }
        }
    }

}