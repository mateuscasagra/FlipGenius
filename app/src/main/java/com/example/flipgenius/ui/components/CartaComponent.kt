package com.example.flipgenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flipgenius.model.CartaJogo

@Composable
fun CartaComponent(
    carta: CartaJogo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = if (carta.encontrada) Color.Green else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = when {
                    carta.encontrada -> Color.Green.copy(alpha = 0.3f)
                    carta.virada -> Color.White
                    else -> Color.Blue.copy(alpha = 0.7f)
                }
            )
            .clickable(enabled = !carta.encontrada && !carta.virada) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (carta.virada || carta.encontrada) {
            Text(
                text = carta.conteudo,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

