package it.qbr.testapisudoku.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun HomeScreen(onStartGame: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sudoku",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "The utimate game"
                , fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onStartGame,
                shape = MaterialTheme.shapes.small, //il bottone non deve essere rotono
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.5f)
            ) {
                Text(text = "Start Game", fontSize = 20.sp)
            }
            Text(
                text = "by QBR",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onStartGame = {})
}
