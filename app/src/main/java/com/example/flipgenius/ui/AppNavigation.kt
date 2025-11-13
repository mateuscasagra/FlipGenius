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
import com.example.flipgenius.ui.screens.LoginAdminScreen
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.viewmodels.JogoViewModel
import com.example.flipgenius.ui.ViewModelFactory
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
    val session = remember { SessionManager(context) }


    Scaffold(
        bottomBar = {
            if (currentRoute in setOf("home", "temas", "perfil", "ranking")) {
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
                            val ok = repo.validarLogin(user, pass)
                            if (ok) {
                                session.salvarUsuario(0L, user, false)
                                currentUserName = user
                                navController.navigate("home")
                            }
                        }
                    },
                    onAdminLoginClick = { adminUser, adminPass ->
                        scope.launch {
                            val ok = (adminUser == "admin" && adminPass == "admin")
                            if (ok) {
                                session.salvarUsuario(0L, adminUser, true)
                                navController.navigate("dashboard")
                            }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("cadastro") }
                )
            }

            composable("cadastro") {
                CadastroScreen(
                    navController = navController,
                    onCadastroClick = { user, pass ->
                        scope.launch {
                            try {
                                val criado = repo.criarOuObter(user, pass)
                                session.salvarUsuario(0L, criado.nomeUsuario, false)
                                currentUserName = criado.nomeUsuario
                                navController.navigate("home")
                            } catch (_: Exception) {
                                // Falha de cadastro (ex.: regras do Firestore). Permanece na tela.
                            }
                        }
                    }
                )
            }

            composable("dashboard") {
                LaunchedEffect(Unit) {
                    if (!session.isAdmin()) {
                        navController.navigate("loginAdmin")
                    }
                }
                if (session.isAdmin()) {
                    DashboardAdminScreen()
                }
            }

            composable("home") {
                LaunchedEffect(Unit) {
                    if (!session.isLoggedIn()) navController.navigate("login")
                }
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
                LaunchedEffect(Unit) { if (!session.isLoggedIn()) navController.navigate("login") }
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
                LaunchedEffect(Unit) { if (!session.isLoggedIn()) navController.navigate("login") }
                val jogoVm: JogoViewModel = viewModel(
                    factory = ViewModelFactory.getJogoFactory(context, "padrao")
                )
                JogoScreen(
                    viewModel = jogoVm,
                    navController = navController
                )
            }

            composable("loginAdmin") {
                LoginAdminScreen(
                    onAdminLogin = { adminUser: String, adminPass: String ->
                        scope.launch {
                            val ok = (adminUser == "admin" && adminPass == "admin")
                            if (ok) {
                                session.salvarUsuario(0L, adminUser, true)
                                navController.navigate("dashboard")
                            }
                        }
                    },
                    onBack = { navController.navigate("login") }
                )
            }

            composable("perfil") { 
                LaunchedEffect(Unit) { if (!session.isLoggedIn()) navController.navigate("login") }
                PerfilScreen(navController = navController, currentUserName = currentUserName) 
            }

            composable("ranking") { 
                LaunchedEffect(Unit) { if (!session.isLoggedIn()) navController.navigate("login") }
                RankingScreen(navController = navController) 
            }

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

            composable("timeAttackGame") { 
                LaunchedEffect(Unit) { if (!session.isLoggedIn()) navController.navigate("login") }
                com.example.flipgenius.ui.screens.TimeAttackGameScreen(navController = navController) 
            }

            composable("timeAttackRanking") { 
                LaunchedEffect(Unit) { if (!session.isLoggedIn()) navController.navigate("login") }
                com.example.flipgenius.ui.screens.TimeAttackRankingScreen(navController = navController) 
            }
        }
    }

}
