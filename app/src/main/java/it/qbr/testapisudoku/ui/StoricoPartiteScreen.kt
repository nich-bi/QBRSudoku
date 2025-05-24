package it.qbr.testapisudoku.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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


enum class FiltroVittoria(val label: String) { TUTTE("Tutte"), VINTE("Vinte"), PERSE("Perse") }
enum class Ordinamento(val label: String) { CRONO("Data"), TEMPO("Tempo di gioco") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoricoPartiteScreen(navController: NavHostController) {
    val context = LocalContext.current
    var storico by remember { mutableStateOf<List<Game>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val expandedStates = remember { mutableStateMapOf<Long, Boolean>() }

    // Stati per i filtri
    var filtroVittoria by remember { mutableStateOf(FiltroVittoria.TUTTE) }
    var ordinamento by remember { mutableStateOf(Ordinamento.CRONO) }
    var ordineDecrescente by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            storico = AppDatabase.getDatabase(context).partitaDao().tutteLePartite()
        }
    }

    // Applica filtri e ordinamento
    val storicoFiltratoOrdinato = remember(storico, filtroVittoria, ordinamento, ordineDecrescente) {
        storico
            .filter {
                when (filtroVittoria) {
                    FiltroVittoria.TUTTE -> true
                    FiltroVittoria.VINTE -> it.vinta
                    FiltroVittoria.PERSE -> !it.vinta
                }
            }
            .let { lista ->
                when (ordinamento) {
                    Ordinamento.CRONO ->
                        if (ordineDecrescente)
                            lista.sortedByDescending { it.dataOra }
                        else
                            lista.sortedBy { it.dataOra }
                    Ordinamento.TEMPO ->
                        if (ordineDecrescente)
                            lista.sortedByDescending { it.tempo }
                        else
                            lista.sortedBy { it.tempo }
                }
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
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Sezione filtri
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F1FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Filtra:", modifier = Modifier.padding(end = 8.dp))
                        // Filtro vittoria
                        FiltroTab(
                            options = FiltroVittoria.entries,
                            selected = filtroVittoria,
                            onSelect = { filtroVittoria = it }
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ordina per:", modifier = Modifier.padding(end = 8.dp))
                        // Ordinamento
                        FiltroTab(
                            options = Ordinamento.entries,
                            selected = ordinamento,
                            onSelect = { ordinamento = it }
                        )
                        // Ordine crescente/decrescente
                        IconButton(
                            onClick = { ordineDecrescente = !ordineDecrescente }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (ordineDecrescente) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up
                                ),
                                contentDescription = "Inverti ordine",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            if (storicoFiltratoOrdinato.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nessuna partita trovata", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(storicoFiltratoOrdinato) { partita ->
                        val expanded = expandedStates[partita.dataOra] == true
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .clickable {
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
                                if (expanded) {
                                    Divider()
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
}

// Composable per tab di filtro/ordinamento
@Composable
fun <T> FiltroTab(options: List<T>, selected: T, onSelect: (T) -> Unit) where T : Enum<T> {
    val selectedTabIndex = options.indexOf(selected)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFFFFF), // opzionale: stesso colore sfondo card filtro
        shadowElevation = 2.dp
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            divider = {},
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color(0xFF1976D2) // Blu
                )
            }
        ) {
            options.forEachIndexed { i, opzione ->
                Tab(
                    selected = selected == opzione,
                    onClick = { onSelect(opzione) },
                    text = {
                        Text(
                            opzione.name.replaceFirstChar { it.uppercase() },
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = Color.Black
                        )
                    }
                )
            }
        }
    }
}


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
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
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