package it.qbr.testapisudoku.ui

import android.content.SyncStats
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.blue_button
import it.qbr.testapisudoku.ui.theme.blue_primary

@Composable
fun HomeScreen(onStartGame: () -> Unit, onStorico: () -> Unit, onStats: () -> Unit ) {
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
                painter = painterResource(id = R.drawable.sudokuimage),
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

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = onStats,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(38.dp)
                    .border(
                        width = 2.dp,
                        color = blue_primary,
                        shape = RoundedCornerShape(50.dp)
                    ),
            ) {
                Text("Statistiche", fontSize = 15.sp, color = blue_primary)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onStorico,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .height(38.dp)
                    .border(
                        width = 2.dp,
                        color = blue_primary,
                        shape = RoundedCornerShape(50.dp)
                    ),
            ) {
                Text("Storico", fontSize = 15.sp, color = blue_primary)
            }

            Text(
                text = "by QBR",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )


        }
    }
}


