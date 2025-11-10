package com.example.flipgenius.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flipgenius.ui.admin.DashboardAdminScreen
import com.example.flipgenius.ui.auth.LoginScreen
import com.example.flipgenius.ui.screens.JogoScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("cadastro") {
            LoginScreen(navController = navController)
        }
        composable("dashboard") {
            DashboardAdminScreen(navController = navController)
        }
        composable("temas") {
            EscolherTemasScreen(navController = navController)
        }
        composable("home") {
            HomeScreeen(navController = navController)
        }
        composable("jogo") {
            JogoScreen(navController = navController)
        }
        composable("loginAdmin") {
            LoginAdminScreen(navController = navController)
        }
        composable("perfil") {
            PerfilScreen(navController= navController)
        }
        composable("ranking") {
            RakingScreen(navController = navController)
        }
        composable("resultado") {
            ResultadoScreen(navController = navController)
        }
        composable("timeAttackGame") {
            TimeAttackGameScreen(navController = navController)
        }
        composable("timeAttackRanking") {
            TimeAttackRankingScreen(navController = navController)
        }

    }
}