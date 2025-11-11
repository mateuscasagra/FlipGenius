package com.example.flipgenius.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flipgenius.ui.theme.FlipGeniusTheme
import com.example.flipgenius.ui.admin.TemaEditDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    onAddThemeClick: (String, String) -> Unit = { _,_ -> },
    onEditThemeClick: (String) -> Unit = {},
    onDeleteThemeClick: (String) -> Unit = {},
    onDeleteAccountClick: (String) -> Unit = {}
) {

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Temas", "Contas")

    var showAddTemaDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painel do Admin") }
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = {
                        showAddTemaDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Tema")
                }
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            // 3. As Abas (Tabs)
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> AdminTemasTab(
                    onEdit = onEditThemeClick,
                    onDelete = onDeleteThemeClick
                )
                1 -> AdminContasTab(
                    onDelete = onDeleteAccountClick
                )
            }
        }

       if (showAddTemaDialog) {
            TemaEditDialog(
                onDismiss = {
                    showAddTemaDialog = false
                },
                onSave = { nome, emojis ->
                    onAddThemeClick(nome, emojis)
                    showAddTemaDialog = false
                }
            )
        }
    }
}

@Composable
private fun AdminTemasTab(
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val fakeThemeList = listOf(
        "Animais" to "🐶🐱🐭🐹",
        "Frutas" to "🍎🍌🍇🍓",
        "Esportes" to "⚽️🏀🏈⚾️"
    )

    LazyColumn {
        items(fakeThemeList) { (nome, emojis) ->
            TemaAdminItem(
                nome = nome,
                emojisPreview = emojis,
                onEdit = { onEdit(nome) },
                onDelete = { onDelete(nome) }
            )
        }
    }
}

@Composable
private fun AdminContasTab(
    onDelete: (String) -> Unit
) {
    val fakeAccountList = listOf("marco@email.com", "joao@email.com", "rhillary@email.com")

    LazyColumn {
        items(fakeAccountList) { email ->
            ContaAdminItem(
                email = email,
                onDelete = { onDelete(email) }
            )
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
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Tema")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Deletar Tema")
                }
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun ContaAdminItem(
    email: String,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(email) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar Conta")
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}


@Preview(showBackground = true)
@Composable
fun DashboardAdminScreenPreview() {
    FlipGeniusTheme {
        DashboardAdminScreen()
    }
}