package it.qbr.testapisudoku.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.blue_button
import it.qbr.testapisudoku.ui.theme.blue_primary

@Composable
fun HomeScreen(onStartGame: () -> Unit, onStorico: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sudoku",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "The ultimate game"
                , fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Image(
                painter = painterResource(id = it.qbr.testapisudoku.R.drawable.sudokuimage),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(240.dp)
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = onStartGame,
                Modifier.padding(8.dp).size(150.dp, 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue_primary),
                shape = RoundedCornerShape(50.dp),
            ) {
                Text(text = "Gioca", fontSize = 22.sp)
            }

            Button(
                onClick = onStorico,
                Modifier.padding(8.dp).size(150.dp, 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue_primary),
                shape = RoundedCornerShape(50.dp),
            ) {
                Spacer(Modifier.width(8.dp))
                Text("Storico", fontSize = 22.sp)
            }



            Text(
                text = "by QBR",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

