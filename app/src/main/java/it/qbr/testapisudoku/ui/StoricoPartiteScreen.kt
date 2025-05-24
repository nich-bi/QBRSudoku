package it.qbr.testapisudoku.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (partita.vinta) Color(0xFFD0F5E8) else Color(0xFFFFE0E0)
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
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
                    }
                }
            }
        }
    }
}