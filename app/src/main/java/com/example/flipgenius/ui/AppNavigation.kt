package com.example.flipgenius.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.flipgenius.data.repository.ConfigRepository
import com.example.flipgenius.ui.admin.DashboardAdminScreen
import com.example.flipgenius.ui.auth.LoginScreen
import com.example.flipgenius.ui.screens.CadastroScreen
import com.example.flipgenius.ui.components.BottomNavBar
import com.example.flipgenius.ui.screens.EscolherTemaScreen
import com.example.flipgenius.ui.screens.HomeScreen
import com.example.flipgenius.ui.screens.JogoScreen
import com.example.flipgenius.ui.screens.PerfilScreen
import com.example.flipgenius.ui.screens.RankingScreen
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.viewmodels.JogoViewModel
import com.example.flipgenius.ui.ViewModelFactory
import kotlinx.coroutines.launch
import com.example.flipgenius.data.local.AppDatabase
import com.example.flipgenius.data.repository.AuthRepository
import com.example.flipgenius.ui.utils.SessionManager

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var currentUserName by remember { mutableStateOf<String?>(null) }
    val repo = remember { ConfigRepository.create() }
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val db = remember { AppDatabase.getInstance(context) }
    val authRepo = remember { AuthRepository(db.usuarioDao()) }
    val session = remember { SessionManager(context) }


    Scaffold(
        bottomBar = {
            if (currentRoute in setOf("home", "temas", "perfil")) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        val startDest = remember { if (session.isLoggedIn()) "home" else "login" }
        NavHost(navController = navController, startDestination = startDest, modifier = Modifier.padding(innerPadding)) {

            composable("login") {
                LoginScreen(
                    onUserLoginClick = { user, pass ->
                        scope.launch {
                            val result = authRepo.login(user, pass)
                            if (result.isSuccess) {
                                val u = result.getOrNull()!!
                                session.salvarUsuario(u.id, u.nome, u.isAdmin)
                                currentUserName = u.nome
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
                CadastroScreen(
                    navController = navController,
                    onCadastroClick = { user, pass ->
                        scope.launch {
                            val result = authRepo.cadastrarUsuario(user, pass)
                            if (result.isSuccess) {
                                val u = result.getOrNull()!!
                                session.salvarUsuario(u.id, u.nome, u.isAdmin)
                                currentUserName = u.nome
                                navController.navigate("home")
                            }
                        }
                    }
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

            composable("jogo") { 
                val configVm: ConfigViewModel = viewModel(factory = ViewModelFactory.getFactory())
                val configState by configVm.uiState.collectAsState()
                val tema = configState.temaPreferido.ifBlank { "padrao" }
                val jogoVm: JogoViewModel = viewModel(
                    factory = ViewModelFactory.getJogoFactory(context, tema)
                )
                JogoScreen(
                    viewModel = jogoVm,
                    navController = navController,
                    configViewModel = configVm
                )
            }

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

            composable(
                route = "resultado/{modo}/{tentativas}/{tema}",
                arguments = listOf(
                    navArgument("modo") { type = NavType.StringType },
                    navArgument("tentativas") { type = NavType.IntType },
                    navArgument("tema") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val modo = backStackEntry.arguments?.getString("modo")?.replace("_", " ") ?: "Clássico"
                val tentativas = backStackEntry.arguments?.getInt("tentativas") ?: 0
                val tema = backStackEntry.arguments?.getString("tema")?.replace("_", " ") ?: "Padrão"
                com.example.flipgenius.ui.screens.ResultadoScreen(
                    navController = navController,
                    modoJogo = modo,
                    resultado = tentativas,
                    tema = tema
                )
            }

            composable("timeAttackGame") { com.example.flipgenius.ui.screens.TimeAttackGameScreen(navController = navController) }

            composable("timeAttackRanking") { com.example.flipgenius.ui.screens.TimeAttackRankingScreen(navController = navController) }
        }
    }

}
