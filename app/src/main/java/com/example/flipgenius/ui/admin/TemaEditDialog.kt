package com.example.flipgenius.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flipgenius.ui.theme.FlipGeniusTheme
import com.example.flipgenius.ui.theme.Purple40

@Composable
fun TemaEditDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    initialNome: String = "",
    initialEmojis: String = ""
) {
    var nomeTema by remember { mutableStateOf(initialNome) }
    var emojisTema by remember { mutableStateOf(initialEmojis) }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = { Text("Editar Tema",
            color = Purple40) },

        text = {
            Column {
                OutlinedTextField(
                    value = nomeTema,
                    onValueChange = { nomeTema = it },
                    label = { Text("Nome do Tema") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = emojisTema,
                    onValueChange = { emojisTema = it },
                    label = { Text("Emojis (ex: 🐶🐱🐭)") }
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onSave(nomeTema, emojisTema)
                }
            ) {
                Text("Salvar", color = Purple40)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar", color = Purple40)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TemaEditDialogPreview() {
    FlipGeniusTheme (
        darkTheme = true,
        dynamicColor = false
    ) {
        TemaEditDialog(
            onDismiss = {},
            onSave = { nome, emojis -> }
        )
    }
}
