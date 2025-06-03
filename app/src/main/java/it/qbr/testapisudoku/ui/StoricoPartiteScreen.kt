package it.qbr.testapisudoku.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.db.AppDatabase
import it.qbr.testapisudoku.db.Game
import it.qbr.testapisudoku.ui.theme.blue_primary
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
                            onSelect = { ordinamento = it },
                            showArrows = true,
                            ordineDecrescente = ordineDecrescente,
                            onInvertiOrdine = { ordineDecrescente = !ordineDecrescente }
                        )
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
                        val cardMinHeight = 68.dp
                        val cardMaxHeight = if (expanded) 365.dp else cardMinHeight
                        val animatedHeight by animateDpAsState(
                            targetValue = cardMaxHeight,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "expandableCardHeight"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .height(animatedHeight)
                                .clickable {
                                    expandedStates[partita.dataOra] = expandedStates[partita.dataOra] != true
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (partita.vinta) Color(0xFFD0F5E8) else Color(0xFFFFE0E0)
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            // Box per gestire l’overflow del contenuto in caso di compressione
                            Box(Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxSize()
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
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
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Data:", fontWeight = FontWeight.Bold)
                                            Text(
                                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(partita.dataOra)),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            val min = partita.tempo / 60
                                            val secs = partita.tempo % 60
                                            Text("Tempo:", fontWeight = FontWeight.Bold)
                                            Text(
                                                "%02d:%02d".format(min, secs),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Difficoltà:", fontWeight = FontWeight.Bold)
                                            Text(
                                                partita.difficolta,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Errori:", fontWeight = FontWeight.Bold)
                                            Text(
                                                "${partita.errori}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    // Contenuto espanso
                                    if (expanded) {
                                        HorizontalDivider(
                                            modifier = Modifier,
                                            0.5.dp,
                                            Color.Gray
                                        )
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
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun <T> FiltroTab(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    showArrows: Boolean = false,
    ordineDecrescente: Boolean = true,
    onInvertiOrdine: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) where T : Enum<T> {
    val tabHeight = 36.dp // aumenta l'altezza per avere spazio anche per la freccia
    val arrowWidth = if (showArrows) tabHeight else 0.dp // la freccia è un quadrato come i tab
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(50))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        val tabCount = options.size
        val tabWidth = (maxWidth - (8.dp * (tabCount - 1)) - arrowWidth) / tabCount
        val selectedIndex = options.indexOf(selected)
        val animatedOffset by animateDpAsState(
            targetValue = (tabWidth + 8.dp) * selectedIndex,
            label = "FiltroTab Ovale Offset"
        )

        Box {
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset, y = 0.dp)
                    .width(tabWidth)
                    .height(tabHeight)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFCCE6FF))
                    .border(
                        width = 2.dp,
                        color = blue_primary,
                        shape = RoundedCornerShape(50)
                    ),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tabHeight), // allinea tutto verticalmente
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEachIndexed { i, opzione ->
                    Box(
                        modifier = Modifier
                            .width(tabWidth)
                            .height(tabHeight)
                            .clip(RoundedCornerShape(50))
                            .clickable { onSelect(opzione) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (opzione is FiltroVittoria) opzione.label
                            else if (opzione is Ordinamento) opzione.label
                            else opzione.name.replaceFirstChar { it.uppercase() },
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = if (selected == opzione) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected == opzione) blue_primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                    if (i != options.lastIndex) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                if (showArrows && onInvertiOrdine != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onInvertiOrdine,
                        modifier = Modifier
                            .size(tabHeight)
                            .padding(end = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (ordineDecrescente) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                            contentDescription = "Inverti ordine",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
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