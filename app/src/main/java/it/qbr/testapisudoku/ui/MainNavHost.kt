package it.qbr.testapisudoku.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.qbr.testapisudoku.db.AppDatabase
import it.qbr.testapisudoku.db.Game
import kotlinx.coroutines.launch

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartGame = { navController.navigate("sudoku") },
                onStorico = { navController.navigate("storico") },
                onStats = { navController.navigate("stats") }
            )
        }
        composable("sudoku") {
            SudokuScreen(navController)
        }
        composable("storico") {
            StoricoPartiteScreen(navController)
        }
        composable("stats") {
            val context = LocalContext.current
            var games by remember { mutableStateOf<List<Game>>(emptyList()) }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                scope.launch {
                    games = AppDatabase.getDatabase(context).partitaDao().tutteLePartite()
                }
            }

            StatsScreen(
                navController,
                games = games
            )
        }
    }
}