package com.example.flipgenius.ui.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flipgenius.ui.theme.FlipGeniusTheme
import com.example.flipgenius.ui.theme.Purple40
import com.example.flipgenius.ui.viewmodels.AdminUiState
import com.example.flipgenius.ui.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Painel do Admin", style = MaterialTheme.typography.headlineMedium,
                    color = Purple40) }
            )
        },
        floatingActionButton = {
            if (uiState.selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onShowAddDialog()
                    },
                    containerColor = Purple40,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Tema")
                }
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            val tabs = listOf("Temas", "Contas")
            TabRow(selectedTabIndex = uiState.selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTabIndex == index,
                        onClick = { viewModel.onTabChange(index) },
                        text = { Text(title) }
                    )
                }
            }

            when (uiState.selectedTabIndex) {
                0 -> AdminTemasTab(
                    temasList = uiState.temasList,
                    onEdit = { /* viewModel.onEditTheme(it) */ },
                    onDelete = { /* viewModel.onDeleteTheme(it) */ }
                )
                1 -> AdminContasTab(
                    accountList = uiState.accountList,
                    onDelete = { viewModel.onDeleteAccount(it) }
                )
            }
        }

       if (uiState.showAddTemaDialog) {
            TemaEditDialog(
                onDismiss = {
                    viewModel.onDismissAddDialog()
                },
                onSave = { nome, emojis ->
                    viewModel.onAddTheme(nome, emojis)
                }
            )
        }
    }
}

@Composable
private fun AdminTemasTab(
    temasList: List<Pair<String, String>>,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {

    LazyColumn {
        items(temasList) { (nome, emojis) ->
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
    accountList: List<String>,
    onDelete: (String) -> Unit
) {

    LazyColumn {
        items(accountList) { nome ->
            ContaAdminItem(
                nome = nome,
                onDelete = { onDelete(nome) }
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
    nome: String,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(nome) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar Conta")
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DashboardAdminScreenPreview() {
    FlipGeniusTheme (
        darkTheme = true,
        dynamicColor = false
    ){
        DashboardAdminScreen(viewModel = AdminViewModel())
    }
}