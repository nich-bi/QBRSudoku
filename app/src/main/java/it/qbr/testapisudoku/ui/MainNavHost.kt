package it.qbr.testapisudoku.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartGame = { navController.navigate("sudoku") }
            )
        }
        composable("sudoku") {
            // Qui va la tua schermata Sudoku
            SudokuScreen()
        }
    }
}