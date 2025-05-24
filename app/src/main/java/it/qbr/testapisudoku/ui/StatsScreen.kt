package it.qbr.testapisudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.db.Game
import java.util.concurrent.TimeUnit



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavHostController,
    games: List<Game>
) {
    val bestTime = games.filter { it.vinta }.minByOrNull { it.tempo }?.tempo
    val avgTime = games.filter { it.vinta }.map { it.tempo }.average().takeIf { !it.isNaN() }
    val gamesStarted = games.size
    val gamesCompleted = games.count { it.vinta }
    val winRate = if (gamesStarted > 0) (gamesCompleted * 100 / gamesStarted) else 0

    val topGames = games
        .filter { it.vinta }
        .sortedWith(compareBy<Game> { it.tempo }.thenBy { it.errori })
        .take(5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiche") },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sezione TEMPO
            StatsSection(
                icon = R.drawable.ic_time,
                title = "Tempo"
            ) {
                StatsRow(label = "Best time", value = bestTime?.let { formatTime(it) } ?: "--")
                Divider(Modifier.padding(vertical = 4.dp))
                StatsRow(label = "Average time", value = avgTime?.let { formatTime(it.toInt()) } ?: "--")
            }

            Spacer(Modifier.height(20.dp))

            // Sezione PARTITE
            StatsSection(
                icon = R.drawable.ic_game,
                title = "Partite"
            ) {
                StatsRow(label = "Games started", value = gamesStarted.toString())
                Divider(Modifier.padding(vertical = 4.dp))
                StatsRow(label = "Games completed", value = gamesCompleted.toString())
                Divider(Modifier.padding(vertical = 4.dp))
                StatsRow(label = "Win rate", value = "$winRate%")
            }

            Spacer(Modifier.height(20.dp))

            // Sezione MIGLIORI PARTITE
            StatsSection(
                icon = R.drawable.ic_star,
                title = "Migliori partite"
            ) {
                if (topGames.isEmpty()) {
                    Text(
                        text = "No games won yet.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    topGames.forEachIndexed { idx, game ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${idx + 1}.",
                                modifier = Modifier.width(28.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Time: ${formatTime(game.tempo)}",
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Errors: ${game.errori}",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (idx < topGames.lastIndex) Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun StatsSection(
    icon: Int,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE9E7F2), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFF7C7C8A),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E1E25),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
fun StatsRow(
    label: String,
    value: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF7C7C8A),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF363648),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun formatTime(seconds: Int): String {
    val min = TimeUnit.SECONDS.toMinutes(seconds.toLong())
    val sec = seconds - min * 60
    return "%02d:%02d".format(min, sec)
}