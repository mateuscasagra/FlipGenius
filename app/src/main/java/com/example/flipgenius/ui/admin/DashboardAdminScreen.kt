package com.example.flipgenius.ui.admin

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.flipgenius.ui.theme.FlipGeniusTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    onAddThemeClick: () -> Unit = {},
    onEditThemeClick: (String) -> Unit = {},
    onDeleteThemeClick: (String) -> Unit = {},
    navController: NavHostController
) {

    // Quando o backend estiver pronto, esta lista virá do ViewModel
    val fakeThemeList = listOf(
        "Animais" to "🐶🐱🐭🐹🐰🦊🐻🐼",
        "Frutas" to "🍎🍌🍇🍓🍉🍒🍑🍍",
        "Esportes" to "⚽️🏀🏈⚾️🎾🏐🏉🎱"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painel de Temas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddThemeClick) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tema")
            }
        }
    ) { paddingValues ->

        //  Lista de Temas
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(fakeThemeList) { (nome, emojis) ->
                TemaAdminItem(
                    nome = nome,
                    emojisPreview = emojis,
                    onEdit = { onEditThemeClick(nome) },
                    onDelete = { onDeleteThemeClick(nome) }
                )
            }
        }
    }
}

@Composable
private fun TemaAdminItem(
    nome: String,
    emojisPreview: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(nome) },
        supportingContent = { Text(emojisPreview) },
        trailingContent = {
            Row {
                // Botão de Editar
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Tema")
                }
                // Botão de Deletar
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Deletar Tema")
                }
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}


@Preview(showBackground = true)
@Composable
fun DashboardAdminScreenPreview() {
    FlipGeniusTheme {
        DashboardAdminScreen(navController = navController)
    }
}