package it.qbr.testapisudoku.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.db.AppDatabase
import it.qbr.testapisudoku.db.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoricoPartiteScreen(navController: NavHostController) {
    val context = LocalContext.current
    var storico by remember { mutableStateOf<List<Game>>(emptyList()) }
    val scope = rememberCoroutineScope()
    // Stato per gli elementi espansi
    val expandedStates = remember { mutableStateMapOf<Long, Boolean>() }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            storico = AppDatabase.getDatabase(context).partitaDao().tutteLePartite()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storico Partite") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_svgrepo_com),
                            contentDescription = "Torna alla Home",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (storico.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nessuna partita giocata", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(storico) { partita ->
                    val expanded = expandedStates[partita.dataOra] == true
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clickable {
                                // Inverti lo stato di espansione
                                expandedStates[partita.dataOra] = !(expandedStates[partita.dataOra] ?: false)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (partita.vinta) Color(0xFFD0F5E8) else Color(0xFFFFE0E0)
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column {
                            Row(
                                Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (partita.vinta) R.drawable.ic_win else R.drawable.ic_lose
                                    ),
                                    contentDescription = null,
                                    tint = if (partita.vinta) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date(partita.dataOra)),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "Tempo: ${partita.tempo} sec  •  Difficoltà: ${partita.difficolta}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Errori: ${partita.errori}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (partita.vinta) "Vinta" else "Persa",
                                    color = if (partita.vinta) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Espandi quando cliccata
                            if (expanded) {
                                Divider()
                                // Mostra la tabella finale qui
                                partita.finalBard?.let { boardFinaleJson ->
                                    SudokuBoardPreview(boardFinaleJson)
                                } ?: Text(
                                    "Nessuna tabella finale salvata.",
                                    Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// Funzione che deserializza una board (stringa JSON) e la mostra in griglia semplice
@Composable
fun SudokuBoardPreview(boardJson: String) {
    val board: List<List<Int>> = remember(boardJson) {
        try {
            val type = object : TypeToken<List<List<Int>>>() {}.type
            Gson().fromJson(boardJson, type)
        } catch (e: Exception) {
            List(9) { List(9) { 0 } }
        }
    }
    Column(Modifier.padding(12.dp)) {
        Text("Tabella finale:", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        // Box per centrare la griglia
        Box(
            Modifier
                .fillMaxWidth(), // Usa tutta la larghezza disponibile
            contentAlignment = Alignment.Center // Centra il contenuto
        ) {
            // Bordo esterno spesso per tutta la tabella
            Box(
                Modifier
                    .border(2.dp, Color.Black)
                    .padding(1.dp)
            ) {
                Column {
                    board.forEachIndexed { rowIdx, row ->
                        Row {
                            row.forEachIndexed { colIdx, cell ->
                                val top = if (rowIdx % 3 == 0) 2.dp else 0.5.dp
                                val left = if (colIdx % 3 == 0) 2.dp else 0.5.dp
                                val right = if (colIdx == 8) 2.dp else 0.5.dp
                                val bottom = if (rowIdx == 8) 2.dp else 0.5.dp

                                Box(
                                    Modifier
                                        .size(28.dp)
                                        .drawBehind {
                                            // Top
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(0f, 0f),
                                                end = Offset(size.width, 0f),
                                                strokeWidth = top.toPx()
                                            )
                                            // Left
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(0f, 0f),
                                                end = Offset(0f, size.height),
                                                strokeWidth = left.toPx()
                                            )
                                            // Right
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(size.width, 0f),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = right.toPx()
                                            )
                                            // Bottom
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = bottom.toPx()
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (cell == 0) "" else cell.toString(),
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = if (cell == 0) FontWeight.Normal else FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}