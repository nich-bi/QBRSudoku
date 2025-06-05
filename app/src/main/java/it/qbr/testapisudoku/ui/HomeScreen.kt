package it.qbr.testapisudoku.ui

import android.R.id.primary
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.blue_secondary
import kotlinx.coroutines.delay
import androidx.compose.runtime.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit


@Composable
fun HomeScreen(
    onStartGame: () -> Unit,
    onStorico: () -> Unit,
    onStats: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Titolo e immagine con padding dall'alto e tra loro
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 96.dp)
        ) {
            TypewriterText(
                text = "QBRSudoku",
            )
            Spacer(modifier = Modifier.height(64.dp))
            Image(
                painter = painterResource(id = R.drawable.sudokuimage),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(240.dp)
            )
        }

        // Bottoni più in alto rispetto al bordo inferiore
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp) // Spazio tra i bottoni e il bordo inferiore
        ) {
            Button(
                onClick = onStartGame,
                Modifier
                    .padding(8.dp)
                    .size(150.dp, 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue_secondary),
                shape = RoundedCornerShape(50.dp),
            ) {
                Text(text = stringResource(id = R.string.StartGame), fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onStats,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .wrapContentWidth()
                    .border(
                        width = 2.dp,
                        color = blue_secondary,
                        shape = RoundedCornerShape(50.dp)
                    ),
            ) {
                Text(text = stringResource(R.string.Stat), fontSize = 15.sp, color = blue_secondary)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onStorico,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .wrapContentWidth()
                    .border(
                        width = 2.dp,
                        color = blue_secondary,
                        shape = RoundedCornerShape(50.dp)
                    ),
            ) {
                Text(text = stringResource(R.string.History), fontSize = 15.sp, color = blue_secondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "by QBR",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}



@Composable
fun TypewriterText(
    text: String,
    specialPartLength: Int = 3, // "QBR" sono i primi 3 caratteri
    specialColor: Color = blue_secondary, // colore di "QBR"
    restColor: Color = Color.Unspecified,
    fontSize: TextUnit = 45.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    charDelayMillis: Long = 250L // più lento!
) {
    var visibleTextLength by remember { mutableStateOf(0) }

    LaunchedEffect(text) {
        visibleTextLength = 0
        while (visibleTextLength < text.length) {
            delay(charDelayMillis)
            visibleTextLength++
        }
    }

    val visibleText = text.take(visibleTextLength)
    val specialText = visibleText.take(specialPartLength)
    val restText = if (visibleTextLength > specialPartLength) visibleText.drop(specialPartLength) else ""

    Text(
        text = buildAnnotatedString {
            if (specialText.isNotEmpty()) {
                withStyle(SpanStyle(color = specialColor)) {
                    append(specialText)
                }
            }
            if (restText.isNotEmpty()) {
                withStyle(SpanStyle(color = restColor)) {
                    append(restText)
                }
            }
        },
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}

